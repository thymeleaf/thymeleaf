package org.thymeleaf.templateparser.html;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.EntitySubstitutionTemplateReader;
import org.thymeleaf.templateparser.ErrorHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.ResourcePool;
import org.thymeleaf.util.StandardDOMTranslator;
import org.xml.sax.InputSource;

/**
 * <p>
 *   Document parser implementation for non-XML HTML documents.
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 */
public abstract class AbstractHtmlTemplateParser implements ITemplateParser {

    private final String templateModeName;
    private final boolean nekoInClasspath;
    private final NekoBasedHtmlParser parser;
    
    
    public AbstractHtmlTemplateParser(final String templateModeName, int poolSize) {
        
        super();

        boolean nekoFound = true;
        try {
            ClassLoaderUtils.getClassLoader(AbstractHtmlTemplateParser.class).
                    loadClass("org.cyberneko.html.parsers.DOMParser");
        } catch (final ClassNotFoundException e) {
            nekoFound = false;
        }
        this.nekoInClasspath = nekoFound;
        this.templateModeName = templateModeName;
        if (this.nekoInClasspath) {
            this.parser = new NekoBasedHtmlParser(poolSize);
        } else {
            this.parser = null;
        }
        
    }



    
    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
        if (!this.nekoInClasspath) {
            throw new ConfigurationException(
                    "Cannot perform conversion to XML from legacy HTML: The nekoHTML library " +
                    "is not in classpath. nekoHTML 1.9.15 or newer is required for processing templates in " +
                    "\"" + this.templateModeName + "\" mode [http://nekohtml.sourceforge.net]. Maven spec: " +
                    "\"net.sourceforge.nekohtml::nekohtml::1.9.15\". IMPORTANT: DO NOT use versions of " +
                    "nekoHTML older than 1.9.15.");
        }
        return this.parser.parseTemplate(configuration, documentName, reader);
    }




    public final List<Node> parseFragment(final Configuration configuration, final String fragment) {
        final String wrappedFragment = wrapFragment(fragment);
        final Document document = 
                parseTemplate(
                        configuration, 
                        null, // documentName 
                        new StringReader(wrappedFragment));
        return unwrapFragment(document);
    }
    
    
    protected abstract String wrapFragment(final String fragment);
    protected abstract List<Node> unwrapFragment(final Document document);


    
    

    /*
     * This is defined in a class apart so that the classloader does not always try to load
     * neko and xerces classes that might not be in the classpath.
     */
    private static class NekoBasedHtmlParser {
        
        
        // The org.apache.xerces.parsers.DOMParser is not used here as a type
        // parameter to avoid the class loader to try to load this xerces class
        // (and fail) before we control the error at the constructor.
        private ResourcePool<Object> pool;

        
        public NekoBasedHtmlParser(int poolSize) {
            super();
            this.pool = createDomParsers(poolSize);
        }
        
        
        
        private final ResourcePool<Object> createDomParsers(final int poolSize) {
            
            final List<Object> domParsers = new ArrayList<Object>();
            
            for(int i = 0; i < poolSize; i++) {
                
                try {
                    
                    final org.cyberneko.html.HTMLConfiguration config = 
                        new org.cyberneko.html.HTMLConfiguration();
                    
                    config.setFeature("http://xml.org/sax/features/namespaces", false);
                    config.setFeature("http://cyberneko.org/html/features/override-doctype", true);
                    config.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
                    config.setProperty("http://cyberneko.org/html/properties/doctype/pubid", ""); 
                    config.setProperty("http://cyberneko.org/html/properties/doctype/sysid", ""); 
                    config.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
                    
                    domParsers.add(new org.apache.xerces.parsers.DOMParser(config));
                    
                } catch(final Exception e) {
                    throw new ConfigurationException(
                        "Error while creating nekoHTML-based parser for " +
                        "LEGACYHTML5 template modes.", e);
                }
                
            }
            
            return new ResourcePool<Object>(domParsers);
            
        }
        
        
        
        public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
            
            final org.apache.xerces.parsers.DOMParser domParser = (org.apache.xerces.parsers.DOMParser) this.pool.allocate();

            final Reader templateReader = 
                    (reader instanceof EntitySubstitutionTemplateReader? 
                            reader : new EntitySubstitutionTemplateReader(reader, 8192));
            
            try {
                
                domParser.setErrorHandler(ErrorHandler.INSTANCE);
                domParser.setEntityResolver(new EntityResolver(configuration));
                
                domParser.parse(new InputSource(templateReader));
                final org.w3c.dom.Document domDocument = domParser.getDocument();
                domParser.reset();
                
                return StandardDOMTranslator.translateDocument(domDocument, documentName);
                
            } catch (final TemplateProcessingException e) {
                throw e;
            } catch (final Exception e) {
                throw new TemplateInputException("Exception parsing document", e);
            } finally {
                this.pool.release(domParser);
            }
        }

        
    }
    
}
