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

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
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
import org.thymeleaf.util.DOMUtils;
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
    
    
    private final ResourcePool<SAXParser> pool;
    
    
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


    
    private static final Document parseTemplateUsingPool(final Configuration configuration, final String documentName, 
            final Reader reader, final ResourcePool<SAXParser> pool) {

        final SAXParser saxParser = pool.allocate();

        final TemplatePreprocessingReader templateReader = 
                (reader instanceof TemplatePreprocessingReader? 
                        (TemplatePreprocessingReader) reader : new TemplatePreprocessingReader(reader, 8192));
        
        try {
            
            final Document document = 
                    doParse(configuration, documentName, templateReader, saxParser);
            saxParser.reset();
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
            
            pool.release(saxParser);
            
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
        
        final DocType docType = 
                new DocType(docTypeRootElementName, docTypePublicId, docTypeSystemId, docTypeClause);
        
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

    
    
    
    private static final class XmlSAXHandler extends DefaultHandler2 {

        private final String documentName;
        private final Stack<Element> elementStack;
        
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
        
        
        public XmlSAXHandler(final String documentName,
                final org.xml.sax.EntityResolver entityResolver, 
                final org.xml.sax.ErrorHandler errorHandler) {
            
            super();

            this.documentName = documentName;
            
            this.elementStack = new Stack<Element>();
            this.rootNodes = new ArrayList<Node>();
            
            this.textBuffer = new char[512];
            this.cdataBuffer = new char[512];
            
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
                Node cdata = 
                        new CDATASection(ArrayUtils.copyOf(this.cdataBuffer, this.cdataBufferLen), false);
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(cdata);
                } else {
                    final Element parent = this.elementStack.peek();
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
        public void characters(final char ch[], final int start, final int length) {
            
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
                
                final Comment comment = 
                        new Comment(ArrayUtils.copyOfRange(ch, start, start + length));
                
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(comment);
                } else {
                    final Element parent = this.elementStack.peek();
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
                        DOMUtils.unescapeXml(
                                TemplatePreprocessingReader.removeEntitySubstitutions(attributes.getValue(i)),
                                true));
            }
            
            this.elementStack.push(element);
            
        }
        

        
        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            
            flushBuffer();
            
            final Element element = this.elementStack.pop();
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(element);
            } else {
                final Element parent = this.elementStack.peek();
                parent.addChild(element);
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
                        new Text(ArrayUtils.copyOf(this.textBuffer, this.textBufferLen), false);
                
                if (this.elementStack.isEmpty()) {
                    this.rootNodes.add(textNode);
                } else {
                    final Element parent = this.elementStack.peek();
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
        
        public SAXTemplateParserFactory(final SAXParserFactory saxParserFactory) {
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
