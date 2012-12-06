/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.xmldom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ParserInitializationException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.ErrorHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;
import org.thymeleaf.util.ResourcePool;
import org.thymeleaf.util.StandardDOMTranslator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Parses XML documents, using a standard non-validating DOM parser.
 * </p>
 * 
 * <p>
 *   This implementation first builds a DOM tree using the
 *   standard DOM API, and then translates this tree into a
 *   Thymeleaf-specific one. It also populates tree nodes with 
 *   basic location information (document name only).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public abstract class AbstractNonValidatingDOMTemplateParser implements ITemplateParser {
    
    
    private static final String SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT =
        "The content of elements must consist of well-formed character data or markup.";
    
    private static final String SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT_EXPLANATION = 
        "The content of elements must consist of well-formed character data or " +
        "markup. A usual reason for this is that one of your elements contains " +
        "unescaped special XML symbols like '<' inside its body, which is " +
        "forbidden by XML rules. For example, if you have '<' inside a <script> tag, " +
        "you should surround your script body with commented CDATA markers (like " +
        "'/* <![CDATA[ */' and '/* ]]> */')";


    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ResourcePool<DocumentBuilder> pool;
    private boolean canResetParsers = true;

    
    
    protected AbstractNonValidatingDOMTemplateParser(final int poolSize) {
        super();
        this.pool = createDocumentBuilders(poolSize, false);
    }
    
    
    protected ResourcePool<DocumentBuilder> getPool() {
        return this.pool;
    }

    
    protected final ResourcePool<DocumentBuilder> getNonValidatingPool() {
        return this.pool;
    }
    
    
    protected final ResourcePool<DocumentBuilder> createDocumentBuilders(final int poolSize, final boolean validating) {
        
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setValidating(validating);

        return new ResourcePool<DocumentBuilder>(
                new DOMTemplateParserFactory(docBuilderFactory), poolSize);
        
    }


    
    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
        return parseTemplateUsingPool(configuration, documentName, reader, getPool());
    }


    
    protected final Document parseTemplateUsingPool(final Configuration configuration, final String documentName, 
            final Reader reader, final ResourcePool<DocumentBuilder> poolToBeUsed) {
        
        final DocumentBuilder docBuilder = poolToBeUsed.allocate();

        final TemplatePreprocessingReader templateReader = getTemplatePreprocessingReader(reader);
        
        try {
            
            docBuilder.setEntityResolver(new EntityResolver(configuration));
            docBuilder.setErrorHandler(ErrorHandler.INSTANCE);

            /*
             * Really parse the document
             */
            final org.w3c.dom.Document domDocument = docBuilder.parse(new InputSource(templateReader));
            
            if (this.canResetParsers) {
                try {
                    /*
                     * Reset the parser so that it can be used again.
                     */
                    docBuilder.reset();
                } catch (final UnsupportedOperationException e) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn(
                                "[THYMELEAF] The DOM Parser implementation being used (\"{}\") does not implement " +
                                    "the \"reset\" operation. This will force Thymeleaf to re-create parser instances " +
                                    "each time they are needed for parsing templates, which is more costly. Enabling template " +
                                    "cache is recommended, and also using a parser library which implements \"reset\" such as " +
                                    "xerces version 2.9.1 or newer.",
                                docBuilder.getClass().getName());
                    }                    
                    this.canResetParsers = false;
                }
            }
            
            return StandardDOMTranslator.translateDocument(domDocument, documentName, templateReader.getDocTypeClause());
            
        } catch (final SAXException e) {
            
            if(e.getMessage() != null &&
                e.getMessage().contains(SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT)) {
                throw new TemplateInputException(
                    SAXPARSEEXCEPTION_BAD_ELEMENT_CONTENT_EXPLANATION, e);
            }
            
            throw new TemplateInputException("An exception happened during parsing", e);
            
        } catch (final IOException e) {
            
            throw new TemplateInputException("Exception parsing document", e);
            
        } catch (final TemplateProcessingException e) {
            
            throw e;
            
        } catch (final Exception e) {
            
            throw new TemplateInputException("Exception parsing document", e);
            
        } finally {

            if (templateReader != null) {
                try {
                    templateReader.close();
                } catch (final Exception ignored) {
                    // ignored
                }
            }
            

            if (this.canResetParsers) {
                poolToBeUsed.release(docBuilder);
            } else {
                poolToBeUsed.discardAndReplace(docBuilder);
            }
            
        }
        
    }

    

    public final List<Node> parseFragment(final Configuration configuration, final String fragment) {
        final String wrappedFragment = wrapFragment(fragment);
        final Document document = 
                parseTemplateUsingPool(
                        configuration, 
                        null, // documentName 
                        new StringReader(wrappedFragment), 
                        getNonValidatingPool());
        return unwrapFragment(document);
    }
    
    
    protected abstract String wrapFragment(final String fragment);
    protected abstract List<Node> unwrapFragment(final Document document);
    
    
    
    

    /**
     * @since 2.0.11
     */
    protected boolean shouldAddThymeleafRootToParser() {
        return true;
    }

    
    
    /**
     * @since 2.0.11
     */
    protected TemplatePreprocessingReader getTemplatePreprocessingReader(final Reader reader) {
        if (reader instanceof TemplatePreprocessingReader) {
            final TemplatePreprocessingReader templatePreprocessingReader = (TemplatePreprocessingReader) reader;
            return new TemplatePreprocessingReader(
                    templatePreprocessingReader.getInnerReader(), 8192, shouldAddThymeleafRootToParser());
        }
        return new TemplatePreprocessingReader(reader, 8192, shouldAddThymeleafRootToParser());
    }
    
    
    
    
    
    static class DOMTemplateParserFactory implements ResourcePool.IResourceFactory<DocumentBuilder> {

        private final DocumentBuilderFactory docBuilderFactory;
        
        public DOMTemplateParserFactory(final DocumentBuilderFactory docBuilderFactory) {
            super();
            this.docBuilderFactory = docBuilderFactory;
        }

        
        public DocumentBuilder createResource() {
            
            try {
                return this.docBuilderFactory.newDocumentBuilder();
            } catch(final ParserConfigurationException e) {
                throw new ParserInitializationException("Error creating document builder", e);
            }
            
        }
        
        
    }
    
}
