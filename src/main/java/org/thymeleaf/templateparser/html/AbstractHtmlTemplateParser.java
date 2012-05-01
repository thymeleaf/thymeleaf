package org.thymeleaf.templateparser.html;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.ErrorHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;
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
        
        
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        // The org.apache.xerces.parsers.DOMParser is not used here as a type
        // parameter to avoid the class loader to try to load this xerces class
        // (and fail) before we control the error at the constructor.
        private ResourcePool<Object> pool;
        private boolean canResetParsers = true;

        
        public NekoBasedHtmlParser(int poolSize) {
            super();
            this.pool = new ResourcePool<Object>(new HtmlTemplateParserFactory(), poolSize);
        }
        
        
        
        
        public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
            
            final org.apache.xerces.parsers.DOMParser domParser = (org.apache.xerces.parsers.DOMParser) this.pool.allocate();

            final TemplatePreprocessingReader templateReader = 
                    (reader instanceof TemplatePreprocessingReader? 
                            (TemplatePreprocessingReader) reader : new TemplatePreprocessingReader(reader, 8192));
            
            try {
                
                domParser.setErrorHandler(ErrorHandler.INSTANCE);
                domParser.setEntityResolver(new EntityResolver(configuration));
                
                domParser.parse(new InputSource(templateReader));
                final org.w3c.dom.Document domDocument = domParser.getDocument();
                
                if (this.canResetParsers) {
                    try {
                        /*
                         * Reset the parser so that it can be used again.
                         */
                        domParser.reset();
                    } catch (final UnsupportedOperationException e) {
                        if (this.logger.isWarnEnabled()) {
                            this.logger.warn(
                                    "[THYMELEAF] The HTML Parser implementation being used (\"{}\") does not implement " +
                                        "the \"reset\" operation. This will force Thymeleaf to re-create parser instances " +
                                        "each time they are needed for parsing templates, which is more costly. Enabling template " +
                                        "cache is recommended, and also using a parser library which implements \"reset\" such as " +
                                        "nekoHTML version 1.9.15 or newer.",
                                    domParser.getClass().getName());
                        }                    
                        this.canResetParsers = false;
                    }
                }
                
                return StandardDOMTranslator.translateDocument(domDocument, documentName, templateReader.getDocTypeClause());
                
            } catch (final TemplateProcessingException e) {
                throw e;
            } catch (final Exception e) {
                throw new TemplateInputException("Exception parsing document", e);
            } finally {

                if (this.canResetParsers) {
                    this.pool.release(domParser);
                } else {
                    this.pool.discardAndReplace(domParser);
                }

            }
        }

        
    }
    
    
    
    static class HtmlTemplateParserFactory implements ResourcePool.IResourceFactory<Object> {
        
        public HtmlTemplateParserFactory() {
            super();
        }

        
        public Object createResource() {
            
            try {
                
                final org.cyberneko.html.HTMLConfiguration config = 
                    new org.cyberneko.html.HTMLConfiguration();
                
                config.setFeature("http://xml.org/sax/features/namespaces", false);
                config.setFeature("http://cyberneko.org/html/features/override-doctype", true);
                config.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
                config.setProperty("http://cyberneko.org/html/properties/doctype/pubid", ""); 
                config.setProperty("http://cyberneko.org/html/properties/doctype/sysid", ""); 
                config.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
                
                return new org.apache.xerces.parsers.DOMParser(config);
                
            } catch(final Exception e) {
                throw new ConfigurationException(
                    "Error while creating nekoHTML-based parser for " +
                    "LEGACYHTML5 template modes.", e);
            }
            
        }
        
        
    }
    
    
}
