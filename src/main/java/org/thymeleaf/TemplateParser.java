/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.ParserInitializationException;
import org.thymeleaf.exceptions.ParsingException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class TemplateParser {

    private static final Logger logger = LoggerFactory.getLogger(TemplateParser.class);

    
    private static final String SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT = "The content of elements must consist of well-formed character data or markup.";
    private static final String SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT_EXPLANATION = 
        "The content of elements must consist of well-formed character data or markup. A usual reason for this is that one of your elements contains " +
        "unescaped special XML symbols like '<' inside its body, which is forbidden by XML rules. For example, if you have '<' inside a <script> tag, " +
        "you should surround your script body with commented CDATA markers (like '/* <![CDATA[ */' and '/* ]]> */')";
    
    /*
     * These are structures needed to postprocess XML-ized HTML, like:
     * 
     * * Comment out <![CDATA[...]]> section begin and end
     * fragments output by HtmlCleaner as of version 2.2, which must be commented out
     * in order to be able to serve results as "text/html" 
     */
    
    static final Pattern SCRIPT_CDATA_PATTERN = Pattern.compile("(\\<script(?:[^\\>]*?)\\>)\\<!\\[CDATA\\[(.*?)\\]\\]\\>(\\</script\\>)", Pattern.DOTALL);
    static final Pattern STYLE_CDATA_PATTERN = Pattern.compile("(\\<style(?:[^\\>]*?)\\>)\\<!\\[CDATA\\[(.*?)\\]\\]\\>(\\</style\\>)", Pattern.DOTALL);
    static final Pattern ENTITY_ESCAPE_SUBSTITUTE_PATTERN = Pattern.compile(String.valueOf(TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE));
    static final Pattern XHTML_XMLNS_PATTERN = Pattern.compile("\\s*xmlns\\=\"http\\://www\\.w3\\.org/1999/xhtml\"");
    
    static final String SCRIPT_COMMENTED_CDATA_REPLACEMENT = "$1\n//<![CDATA[\n$2\n//]]>\n$3";
    static final String STYLE_COMMENTED_CDATA_REPLACEMENT = "$1\n/*<![CDATA[ */\n$2\n/*]]>*/\n$3";
    static final String ENTITY_ESCAPE_SUBSTITUTE_REPLACEMENT = "&";
    

    protected static final ErrorHandler ERROR_HANDLER = new ErrorHandler();
    
    private final DocumentBuilder[] nonValidatingDocumentBuilders;
    private final DocumentBuilder[] validatingDocumentBuilders;
    private final boolean nekoInClasspath;
    private final int maxConcurrency;
    private int currentNonValidatingBuilder;
    private int currentValidatingBuilder;

    private final LegacyHTML5Processor legacyHTML5Processor;
    
    private ParsedTemplateCache parsedTemplateCache;

    
    
    
    TemplateParser(final Configuration configuration) {
        
        super();
        
        this.nekoInClasspath = computeIsNekoInClassPath();
        
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.maxConcurrency = (availableProcessors <= 2? availableProcessors : availableProcessors - 1);
        
        this.nonValidatingDocumentBuilders = new DocumentBuilder[this.maxConcurrency];
        this.validatingDocumentBuilders = new DocumentBuilder[this.maxConcurrency];
        if (this.nekoInClasspath) {
            this.legacyHTML5Processor = new LegacyHTML5Processor(this.maxConcurrency, configuration);
        } else {
            this.legacyHTML5Processor = null;
        }
        this.currentNonValidatingBuilder = 0;
        this.currentValidatingBuilder = 0;
        
        logger.info("[THYMELEAF] Initializing template parser with a pool of {} parser/s (Number of available processors: {})", 
                Integer.valueOf(this.maxConcurrency), Integer.valueOf(availableProcessors));
        
        final DocumentBuilderFactory nonValidatingDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        nonValidatingDocumentBuilderFactory.setValidating(false);
        
        final DocumentBuilderFactory validatingDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        validatingDocumentBuilderFactory.setValidating(true);
        
        for (int i = 0; i < this.maxConcurrency; i++) {
            
            try {
            
                this.nonValidatingDocumentBuilders[i] = nonValidatingDocumentBuilderFactory.newDocumentBuilder();
                this.nonValidatingDocumentBuilders[i].setEntityResolver(new EntityResolver(configuration));
                this.nonValidatingDocumentBuilders[i].setErrorHandler(ERROR_HANDLER);
                
                this.validatingDocumentBuilders[i] = validatingDocumentBuilderFactory.newDocumentBuilder();
                this.validatingDocumentBuilders[i].setEntityResolver(new EntityResolver(configuration));
                this.validatingDocumentBuilders[i].setErrorHandler(ERROR_HANDLER);
                
            } catch (final ParserConfigurationException e) {
                throw new ParserInitializationException("Error creating document builder", e);
            }
            
        }
        
        this.parsedTemplateCache = 
            new ParsedTemplateCache(configuration.getParsedTemplateCacheSize());
        
    }

    
    
    void clearParsedTemplateCache() {
        this.parsedTemplateCache.clearParsedTemplateCache();
    }

    
    void clearParsedTemplateCacheFor(final String templateName) {
        this.parsedTemplateCache.clearParsedTemplateCacheFor(templateName);
    }
    
    
    
    
    private boolean computeIsNekoInClassPath() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.cyberneko.html.parsers.DOMParser");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    
    
    
    private int validatingRoundRobin() {
        int rr;
        synchronized(this) {
            rr = this.currentValidatingBuilder;
            this.currentValidatingBuilder = (this.currentValidatingBuilder + 1) % this.maxConcurrency;
        }
        return rr;
    }
    
    
    private int nonValidatingRoundRobin() {
        int rr;
        synchronized(this) {
            rr = this.currentNonValidatingBuilder;
            this.currentNonValidatingBuilder = (this.currentNonValidatingBuilder + 1) % this.maxConcurrency;
        }
        return rr;
    }
    
    
    
    public Node parseXMLString(final Arguments arguments, final String xmlString) {
        
        final Configuration configuration = arguments.getConfiguration();
        
        final String wrappedText = 
              Standards.HTML_STANDARD_ENTITIES_DOCTYPE + "\n<html><body><div>" + xmlString + "</div></body></html>";

        final InputSource inputSource = new InputSource(new HTMLTemplateReader(new StringReader(wrappedText), 8192, true, true, false));
        
        final int roundRobin = nonValidatingRoundRobin();

        try {
            synchronized (this.nonValidatingDocumentBuilders[roundRobin]) {
                final Document document = 
                    this.nonValidatingDocumentBuilders[roundRobin].parse(inputSource);
                DOMDocumentPreprocessor.preprocess(configuration, TemplateMode.HTML5, document);
                return document.getDocumentElement().getFirstChild().getFirstChild();
            }
        } catch (final IOException e) {
            throw new TemplateInputException("Exception parsing document", e);
        } catch (final SAXParseException e) {
            if (e.getMessage() != null && e.getMessage().contains(SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT)) {
                throw new ParsingException(SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT_EXPLANATION, e);
            }
            throw new ParsingException("An exception happened during parsing", e);
        } catch (final Exception e) {
            throw new ParsingException("Exception parsing document", e);
        }
        
    }
 
    
    
    
    
    
    
    public ParsedTemplate parseDocument(final Arguments arguments) {

        final String templateName = arguments.getTemplateName();
        
        final ParsedTemplate cached = 
            this.parsedTemplateCache.getParsedTemplate(templateName);
        if (cached != null) {
            return cached.clone();
        }
        
        final Configuration configuration = arguments.getConfiguration();
        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        TemplateResolution templateResolution = null;
        InputStream templateInputStream = null;
        
        for (final ITemplateResolver templateResolver : templateResolvers) {
            
            if (templateInputStream == null) {
                
                templateResolution = templateResolver.resolveTemplate(arguments);
                
                if (templateResolution != null) {
                    
                    final String resourceName = templateResolution.getResourceName();

                    final IResourceResolver resourceResolver = templateResolution.getResourceResolver();
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, resourceResolver.getName()});
                    }
                    
                    templateInputStream = 
                        resourceResolver.getResourceAsStream(arguments, resourceName);
                    
                    if (templateInputStream == null) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("[THYMELEAF][{}] Template \"{}\" could not be resolved as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, resourceResolver.getName()});
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("[THYMELEAF][{}] Template \"{}\" was correctly resolved as resource \"{}\" in mode {} with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, templateResolution.getTemplateMode(), resourceResolver.getName()});
                        }
                    }
                    
                } else {
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), templateName});
                    }
                    
                }
                
            }
            
        }
        
        if (templateResolution == null || templateInputStream == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + arguments.getTemplateName() + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }
        
        final Document document = doParseDocument(templateResolution, templateInputStream);
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }
        
        DOMDocumentPreprocessor.preprocess(configuration, templateResolution.getTemplateMode(), document);

        final ParsedTemplate templateDocumentResolution =
            new ParsedTemplate(templateName, templateResolution, document);
        
        if (templateResolution.getValidity().isCacheable()) {
            this.parsedTemplateCache.putParsedTemplate(templateDocumentResolution);
            return templateDocumentResolution.clone();
        }
        
        return templateDocumentResolution;
        
    }


    
    
    
    
    private Document doParseDocument(
            final TemplateResolution templateResolution, final InputStream inputStream) {

        final boolean validating = 
            templateResolution.getTemplateMode().isValidating();
        
        final int roundRobin = 
            (validating? validatingRoundRobin() : nonValidatingRoundRobin());

        final DocumentBuilder[] builders =
            (validating? this.validatingDocumentBuilders : this.nonValidatingDocumentBuilders);

        
        if (templateResolution.getTemplateMode().equals(TemplateMode.LEGACYHTML5)) {
            /*
             * LEGACYHTML5 templates need special handling
             */

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Template \"{}\" is in LEGACYHTML5 mode, converting to XML-formed document", TemplateEngine.threadIndex(), templateResolution.getTemplateName());
            }
            
            final Document document = processLegacyHTML5Template(templateResolution, inputStream);
            
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Template \"{}\" converted to XML-formed document", TemplateEngine.threadIndex(), templateResolution.getTemplateName());
            }
            
            return document;
            
        }
        
        
        final String encoding = templateResolution.getCharacterEncoding();
        Reader reader = null;
        if (encoding != null && !encoding.trim().equals("")) {
            try {
                reader = new InputStreamReader(inputStream, encoding);
            } catch (final UnsupportedEncodingException e) {
                throw new TemplateInputException("Exception parsing document", e);
            }
        } else {
            reader = new InputStreamReader(inputStream);
        }

        final HTMLTemplateReader rf = new HTMLTemplateReader(reader, 8192, true, true, false);
        
        final InputSource inputSource = new InputSource(rf);

        
        try {
            synchronized (builders[roundRobin]) {
                return builders[roundRobin].parse(inputSource);
            }
        } catch (final IOException e) {
            throw new TemplateInputException("Exception parsing document", e);
        } catch (final SAXParseException e) {
            if (e.getMessage() != null && e.getMessage().contains(SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT)) {
                throw new ParsingException(SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT_EXPLANATION, e);
            }
            throw new ParsingException("An exception happened during parsing", e);
        } catch (final Exception e) {
            throw new ParsingException("Exception parsing document", e);
        } finally {
            try{
                inputStream.close();
            } catch (final Exception e) {
                // Ignored
            }
        }
        
    }
    

    
    
    
    
    
    
    
    public Document processLegacyHTML5Template(
            final TemplateResolution templateResolution, final InputStream inputStream) {
        
        if (!this.nekoInClasspath) {
            throw new ConfigurationException(
                    "Cannot perform conversion to XML from legacy HTML: The nekoHTML library " +
                    "is not in classpath. nekoHTML 1.9.15 or newer is required for processing templates in " +
                    "LEGACYHTML5 mode [http://nekohtml.sourceforge.net]. Maven spec: " +
                    "\"net.sourceforge.nekohtml::nekohtml::1.9.15\". IMPORTANT: DO NOT use versions of " +
                    "nekoHTML older than 1.9.15.");
        }
        return this.legacyHTML5Processor.processLegacyHTML5(templateResolution, inputStream);
        
    }
    
    
    
    
    
    /*
     * This class is isolated so that nekoHTML does not have to be in
     * classpath to work if no LEGACYHTML5 templates are to be processed
     */
    private static class LegacyHTML5Processor {
        
        private int maxConcurrency;
        
        private final org.apache.xerces.parsers.DOMParser[] htmlParsers;
        private int currentHtmlParser;
        
        
        LegacyHTML5Processor(final int maxConcurrency, final Configuration configuration) {
            
            super();
            
            this.maxConcurrency = maxConcurrency;
            this.htmlParsers = new org.apache.xerces.parsers.DOMParser[this.maxConcurrency];
            this.currentHtmlParser = 0;
            
            for (int i = 0; i < this.maxConcurrency; i++) {
                
                try {
                    
                    final org.cyberneko.html.HTMLConfiguration config = 
                        new org.cyberneko.html.HTMLConfiguration();
                    config.setFeature("http://xml.org/sax/features/namespaces", false);
                    config.setFeature("http://cyberneko.org/html/features/override-doctype", true);
                    config.setProperty("http://cyberneko.org/html/properties/doctype/pubid", ""); 
                    config.setProperty("http://cyberneko.org/html/properties/doctype/sysid", ""); 
                    config.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
                    
                    this.htmlParsers[i] = 
                        new org.apache.xerces.parsers.DOMParser(config);
                    this.htmlParsers[i].setErrorHandler(TemplateParser.ERROR_HANDLER);
                    this.htmlParsers[i].setEntityResolver(new EntityResolver(configuration));
                    
                } catch (final Exception e) {
                    throw new ConfigurationException(
                            "Error while creating nekoHTML-based parser for LEGACYHTML5 template modes.", e);
                }
                
            }
            
        }
        
        
        private int htmlParserRoundRobin() {
            int rr;
            synchronized(this) {
                rr = this.currentHtmlParser;
                this.currentHtmlParser = (this.currentHtmlParser + 1) % this.maxConcurrency;
            }
            return rr;
        }
        
        
        Document processLegacyHTML5(
                final TemplateResolution templateResolution, final InputStream inputStream) {

            
            final int roundRobin = htmlParserRoundRobin();
            
            try {

                final String encoding = templateResolution.getCharacterEncoding();
                
                final org.apache.xerces.parsers.DOMParser htmlParser = this.htmlParsers[roundRobin];
                synchronized (htmlParser) {

                    Reader reader = null;
                    if (encoding != null && !encoding.trim().equals("")) {
                        try {
                            reader = new InputStreamReader(inputStream, encoding);
                        } catch (final UnsupportedEncodingException e) {
                            throw new TemplateInputException("Exception parsing document", e);
                        }
                    } else {
                        reader = new InputStreamReader(inputStream);
                    }

                    final HTMLTemplateReader rf = new HTMLTemplateReader(reader, 8192, true, true, false);
                    
                    final InputSource inputSource = new InputSource(rf);

                    htmlParser.parse(inputSource);
                    
                    final Document document = htmlParser.getDocument();
                    
                    htmlParser.reset();
                    
                    return document;
                    
                }
                
            } catch (final Exception e) {
                throw new ParsingException("Exception parsing document", e);
            } finally {
                try{
                    inputStream.close();
                } catch (final Exception e) {
                    // Ignored
                }
            }
            
        }
        
        
    }
    
    
    
}
