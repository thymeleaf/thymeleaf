/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.xmlsax;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.exceptions.ParserInitializationException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.ErrorHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.ResourcePool;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2;

/**
 * <p>
 *   Parses XML documents, using a standard SAX parser.
 * </p>
 * 
 * <p>
 *   This implementation populates tree nodes with detailed location 
 *   information (document name and line number).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Guven Demir
 * @author Daniel Fern&aacute;ndez
 * @author Tsuyoshi Yoshizawa
 * 
 */
public abstract class AbstractNonValidatingSAXTemplateParser implements ITemplateParser {

    private static final int BUFFER_SIZE = 8192;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ResourcePool<SAXParser> pool;
    private boolean canResetParsers = true;
    
    
    protected AbstractNonValidatingSAXTemplateParser(final int poolSize) {
        super();
        this.pool = createSaxParsers(poolSize, false);
    }
    
    
    protected ResourcePool<SAXParser> getPool() {
        return this.pool;
    }

    
    protected final ResourcePool<SAXParser> getNonValidatingPool() {
        return this.pool;
    }

    
    
    protected final ResourcePool<SAXParser> createSaxParsers(final int poolSize, final boolean validating) {
        
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);

        return new ResourcePool<SAXParser>(new SAXTemplateParserFactory(factory), poolSize);
        
    }


    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
        return parseTemplateUsingPool(configuration, documentName, reader, getPool());
    }


    
    private Document parseTemplateUsingPool(final Configuration configuration, final String documentName,
            final Reader reader, final ResourcePool<SAXParser> poolToBeUsed) {

        final SAXParser saxParser = poolToBeUsed.allocate();

        final TemplatePreprocessingReader templateReader = getTemplatePreprocessingReader(reader);
        
        try {
            
            /*
             * Parse the document
             */
            final Document document = 
                    doParse(configuration, documentName, templateReader, saxParser);
            
            if (this.canResetParsers) {
                try {
                    /*
                     * Reset the parser so that it can be used again.
                     */
                    saxParser.reset();
                } catch (final UnsupportedOperationException ignored) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn(
                                "[THYMELEAF] The SAX Parser implementation being used (\"{}\") does not implement " +
                                    "the \"reset\" operation. This will force Thymeleaf to re-create parser instances " +
                                    "each time they are needed for parsing templates, which is more costly. Enabling template " +
                                    "cache is recommended, and also using a parser library which implements \"reset\" such as " +
                                    "xerces version 2.9.1 or newer.",
                                saxParser.getClass().getName());
                    }                    
                    this.canResetParsers = false;
                }
            }
            
            return document;
            
        } catch (final IOException e) {
            throw new TemplateInputException("Exception parsing document", e);
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final SAXParseException e) {
            final String message = 
                    String.format("Exception parsing document: template=\"%s\", line %d - column %d",
                            documentName, Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()));
            throw new TemplateInputException(message, e);
        } catch (final SAXException e) {
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
                poolToBeUsed.release(saxParser);
            } else {
                poolToBeUsed.discardAndReplace(saxParser);
            }
            
        }
        
    }

    
    
    
    private static Document doParse(
            final Configuration configuration, final String documentName,
            final TemplatePreprocessingReader reader, final SAXParser saxParser)
            throws IOException, SAXException {


        final InputSource inputSource = new InputSource(reader);
        
        final XmlSAXHandler handler = 
                new XmlSAXHandler(documentName, new EntityResolver(configuration), ErrorHandler.INSTANCE);
        
        saxParser.setProperty(
            "http://xml.org/sax/properties/lexical-handler", handler);
        saxParser.setProperty(
                "http://xml.org/sax/properties/declaration-handler", handler);
        
        
        saxParser.parse(inputSource, handler);
        
        final String docTypeClause = reader.getDocTypeClause();
        final String docTypeRootElementName = handler.getDocTypeRootElementName();
        final String docTypePublicId = handler.getDocTypePublicId();
        final String docTypeSystemId = handler.getDocTypeSystemId();
        
        // The DOCTYPE root element name could be null if we are parsing
        // a non-complete document, a fragment, without a DOCTYPE declaration.
        final DocType docType = 
                (docTypeRootElementName != null?
                        new DocType(docTypeRootElementName, docTypePublicId, docTypeSystemId, docTypeClause) :
                        null);
        
        final List<Node> rootNodes = handler.getRootNodes();
        
        final String xmlVersion = handler.getXmlVersion();
        final String xmlEncoding = handler.getXmlEncoding();
        final boolean xmlStandalone = handler.isXmlStandalone();
        
        final Document document = new Document(documentName, docType);

        if (xmlVersion != null) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_VERSION, xmlVersion);
        }
        
        if (xmlEncoding != null) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_ENCODING, xmlEncoding);
        }
        
        if (xmlStandalone) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_STANDALONE, Boolean.TRUE);
        }
        
        document.setChildren(rootNodes);
        
        return document;
        
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
                    templatePreprocessingReader.getInnerReader(), BUFFER_SIZE, shouldAddThymeleafRootToParser());
        }
        return new TemplatePreprocessingReader(reader, BUFFER_SIZE, shouldAddThymeleafRootToParser());
    }
    
    
    
    
    
    
    private static final class XmlSAXHandler extends DefaultHandler2 {

        private static final int HANDLER_BUFFER_SIZE = 512;

        private final String documentName;
        private final Stack<NestableNode> elementStack;
        
        private char[] textBuffer;
        private int textBufferLen;
        
        private char[] cdataBuffer;
        private int cdataBufferLen; 
        
        private final org.xml.sax.EntityResolver entityResolver;
        private final org.xml.sax.ErrorHandler errorHandler;
        
        private Locator locator = null;
        private String docTypeRootElementName = null;
        private String docTypePublicId = null;
        private String docTypeSystemId = null;
        private List<Node> rootNodes = null;
        
        private boolean cdataMode = false;
        private boolean dtdMode = false;
        
        private String xmlEncoding = null;
        private String xmlVersion = null;
        private boolean xmlStandalone = false;

        private boolean xmlDeclarationComputed = false;

        
        
        XmlSAXHandler(final String documentName,
                              final org.xml.sax.EntityResolver entityResolver,
                              final org.xml.sax.ErrorHandler errorHandler) {
            
            super();

            this.documentName = documentName;
            
            this.elementStack = new Stack<NestableNode>();
            this.rootNodes = new ArrayList<Node>(4);
            
            this.textBuffer = new char[HANDLER_BUFFER_SIZE];
            this.cdataBuffer = new char[HANDLER_BUFFER_SIZE];
            
            this.entityResolver = entityResolver;
            this.errorHandler = errorHandler;
            
        }
        

        
        public String getDocTypeRootElementName() {
            return this.docTypeRootElementName;
        }
        
        public String getDocTypePublicId() {
            return this.docTypePublicId;
        }
        
        public String getDocTypeSystemId() {
            return this.docTypeSystemId;
        }
        
        public List<Node> getRootNodes() {
            return this.rootNodes;
        }

        public String getXmlEncoding() {
            return this.xmlEncoding;
        }

        public String getXmlVersion() {
            return this.xmlVersion;
        }

        public boolean isXmlStandalone() {
            return this.xmlStandalone;
        }



        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            flushBuffer();
        }
        

        /*
         * ----------------------
         * CDATA section handling
         * ----------------------
         */



        @Override
        public void startCDATA() throws SAXException {
            super.startCDATA();
            flushBuffer();
            this.cdataMode = true;
        }

        
        @Override
        public void endCDATA() throws SAXException {

            super.endCDATA();

            this.cdataMode = false;
            if(this.cdataBufferLen > 0) {
                final Node cdata =
                        new CDATASection(new String(this.cdataBuffer, 0, this.cdataBufferLen), null, null, true);
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(cdata);
                } else {
                    final NestableNode parent = this.elementStack.peek();
                    parent.addChild(cdata);
                }
                this.cdataBufferLen = 0;
            }
            
        }
        

        

        /*
         * -------------
         * Text handling
         * -------------
         */

        
        @Override
        public void characters(final char[] ch, final int start, final int length) {

            TemplatePreprocessingReader.removeEntitySubstitutions(ch, start, length);
            if (this.cdataMode) {
                
                while (this.cdataBufferLen + length > this.cdataBuffer.length) {
                    this.cdataBuffer = ArrayUtils.copyOf(this.cdataBuffer, this.cdataBuffer.length * 2);
                }
                System.arraycopy(ch, start, this.cdataBuffer, this.cdataBufferLen, length);
                this.cdataBufferLen += length;
                
            } else {
                
                while (this.textBufferLen + length > this.textBuffer.length) {
                    this.textBuffer = ArrayUtils.copyOf(this.textBuffer, this.textBuffer.length * 2);
                }
                System.arraycopy(ch, start, this.textBuffer, this.textBufferLen, length);
                this.textBufferLen += length;
                
            }
            
        }

        
        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length)
                throws SAXException {
            characters(ch, start, length);
        }
        
        

        
        /*
         * ----------------
         * Comment handling
         * ----------------
         */

        
        @Override
        public void comment(final char[] ch, final int start, final int length) 
                throws SAXException {

            if (!this.dtdMode) {

                flushBuffer();

                final Comment comment = 
                        new Comment(new String(ch, start, start + length));
                
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(comment);
                } else {
                    final NestableNode parent = this.elementStack.peek();
                    parent.addChild(comment);
                }
                
            }
            
        }

        

       
        /*
         * ----------------
         * Element handling
         * ----------------
         */

        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) 
                throws SAXException {

            if (!this.xmlDeclarationComputed) {
                
                // SAX specification says the "getEncoding()" method in Locator2 can only
                // be called AFTER startDocument has returned and BEFORE endDocument is called.
                
                if (this.locator != null && this.locator instanceof Locator2) {
                    
                    final Locator2 loc = (Locator2) this.locator;
                    
                    this.xmlVersion = loc.getXMLVersion();
                    this.xmlEncoding = loc.getEncoding();

                    // There seems to be no way of obtaining the "standalone" property
                    // from the XML declaration.
                    
                }
                
                this.xmlDeclarationComputed = true;

            }
            
            
            flushBuffer();

            Integer lineNumber = null;
            if (this.locator != null) {
                lineNumber = Integer.valueOf(this.locator.getLineNumber());
            }
            
            final Element element = new Element(qName, this.documentName, lineNumber);
            
            for (int i = 0; i < attributes.getLength(); i++) {
                element.setAttribute(
                        attributes.getQName(i),
                        false,
                        TemplatePreprocessingReader.removeEntitySubstitutions(attributes.getValue(i)),
                        true);
            }
            
            this.elementStack.push(element);
            
        }
        

        
        @Override
        public void endElement(final String uri, final String localName, final String qName) {

            flushBuffer();

            final NestableNode node = this.elementStack.pop();
            
            if (node instanceof Element) {
                final Element element = (Element) node;
                if (TemplatePreprocessingReader.SYNTHETIC_ROOT_ELEMENT_NAME.equals(element.getOriginalName())) {
                    // If it is the synthetic root element, then we skip the element itself and just add
                    // its children to the results.
                    final List<Node> syntheticRootChildren = element.getChildren();
                    if (this.elementStack.isEmpty()) {
                        this.rootNodes.addAll(syntheticRootChildren);
                    } else {
                        final NestableNode parent = this.elementStack.peek();
                        for (final Node syntheticRootChild : syntheticRootChildren) {
                            parent.addChild(syntheticRootChild);
                        }
                    }
                    return;
                }
            }
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(node);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(node);
            }
            
        }


        
        /*
         * ------------
         * DTD handling
         * ------------
         */

        
        @Override
        public void startDTD(final String name, final String publicId, final String systemId)
                throws SAXException {
            super.startDTD(name, publicId, systemId);
            this.docTypeRootElementName = name;
            this.docTypePublicId = publicId;
            this.docTypeSystemId = systemId;
            this.dtdMode = true;
        }
        
        
        
        @Override
        public void endDTD() throws SAXException {
            super.endDTD();
            this.dtdMode = false;
        }

        
        
        /*
         * Buffer is used for accumulating text that is read in between elements,
         * and should be flushed before creating any non-text element.
         * 
         * Note there also is a 'CDATA' buffer, similar in use to this, containing
         * all the contents read for a CDATA section.
         */
        private void flushBuffer() {
            
            if (this.textBufferLen > 0) {

                final Node textNode = 
                        new Text(new String(this.textBuffer, 0, this.textBufferLen), null, null, true);
                
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(textNode);
                } else {
                    final NestableNode parent = this.elementStack.peek();
                    parent.addChild(textNode);
                }
            
                this.textBufferLen = 0;
                
            }
            
        }


        
        /*
         * Document locator will help us determine the line number for each node
         * so that we can add debug information to our DOM tree
         */
        @Override
        public void setDocumentLocator(final Locator locator) {
            this.locator = locator; 
        }



        
        
        
        /*
         * ------------
         * ErrorHandler
         * ------------
         */

        
        @Override
        public void error(final SAXParseException exception) throws SAXException {
            this.errorHandler.error(exception);
        }


        @Override
        public void fatalError(final SAXParseException exception) throws SAXException {
            this.errorHandler.fatalError(exception);
        }


        @Override
        public void warning(final SAXParseException exception) throws SAXException {
            this.errorHandler.warning(exception);
        }

        
        
        
        /*
         * ---------------
         * EntityResolver2
         * ---------------
         */

        
        @Override
        public InputSource getExternalSubset(final String name, final String baseURI)
                throws SAXException, IOException {
            return null;
        }


        @Override
        public InputSource resolveEntity(final String publicId, final String systemId)
                throws IOException, SAXException {
            return this.entityResolver.resolveEntity(publicId, systemId);
        }


        @Override
        public InputSource resolveEntity(final String name, final String publicId,
                final String baseURI, final String systemId) 
                throws SAXException, IOException {
            return resolveEntity(publicId, systemId);
        }
        
        
    }

    
    

    
    static class SAXTemplateParserFactory implements ResourcePool.IResourceFactory<SAXParser> {

        private final SAXParserFactory saxParserFactory;
        
        SAXTemplateParserFactory(final SAXParserFactory saxParserFactory) {
            super();
            this.saxParserFactory = saxParserFactory;
        }

        
        public SAXParser createResource() {
            
            try {
                return this.saxParserFactory.newSAXParser();
            } catch(final ParserConfigurationException e) {
                throw new ParserInitializationException("Error creating SAX parser", e);
            } catch(final SAXException e) {
                throw new ParserInitializationException("Error creating SAX parser", e);
            }
            
        }
        
        
    }
    
    
}
