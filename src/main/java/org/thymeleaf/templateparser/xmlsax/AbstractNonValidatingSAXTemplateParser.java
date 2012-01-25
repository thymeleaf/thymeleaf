package org.thymeleaf.templateparser.xmlsax;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.dom.Text;
import org.thymeleaf.exceptions.ParserInitializationException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateparser.AbstractTemplateParser;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.EntitySubstitutionTemplateReader;
import org.thymeleaf.templateparser.ErrorHandler;
import org.thymeleaf.util.ResourcePool;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

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
 * 
 */
public abstract class AbstractNonValidatingSAXTemplateParser extends AbstractTemplateParser {
    
    
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

        final List<SAXParser> saxParsers = new ArrayList<SAXParser>();
        
        for(int i = 0; i < poolSize; i++) {
            
            try {
                saxParsers.add(factory.newSAXParser());
            } catch(final ParserConfigurationException e) {
                throw new ParserInitializationException("Error creating SAX parser", e);
            } catch(final SAXException e) {
                throw new ParserInitializationException("Error creating SAX parser", e);
            }
            
        }
        
        return new ResourcePool<SAXParser>(saxParsers);
        
    }


    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {
        return parseTemplateUsingPool(configuration, documentName, reader, getPool());
    }


    
    private static final Document parseTemplateUsingPool(final Configuration configuration, final String documentName, 
            final Reader reader, final ResourcePool<SAXParser> pool) {

        final SAXParser saxParser = pool.allocate();

        final Reader templateReader = 
                (reader instanceof EntitySubstitutionTemplateReader? 
                        reader : new EntitySubstitutionTemplateReader(reader, 8192));
        
        try {
            
            return doParse(configuration, documentName, new InputSource(templateReader), saxParser);
            
        } catch (final IOException e) {
            throw new TemplateInputException("Exception parsing document", e);
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final SAXException e) {
            throw new TemplateInputException("Exception parsing document", e);
        } finally {
            
            pool.release(saxParser);
            
        }
        
    }

    
    
    
    private static Document doParse(
            final Configuration configuration, final String documentName,
            final InputSource inputSource, final SAXParser saxParser)
            throws IOException, SAXException {


        final XmlSAXHandler handler = 
                new XmlSAXHandler(documentName, new EntityResolver(configuration), ErrorHandler.INSTANCE);
        
        saxParser.setProperty(
            "http://xml.org/sax/properties/lexical-handler", handler);
        
        saxParser.parse(inputSource, handler);
        
        final DocType docType = handler.getDocType();
        final List<Node> rootNodes = handler.getRootNodes();
        
        final Document document = new Document(documentName, docType);
        document.setChildren(rootNodes);
        
        saxParser.reset();
        
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
        private final Stack<Tag> elementStack;
        
        private char[] textBuffer;
        private int textBufferLen;
        
        private char[] cdataBuffer;
        private int cdataBufferLen; 
        
        private final org.xml.sax.EntityResolver entityResolver;
        private final org.xml.sax.ErrorHandler errorHandler;
        
        private Locator locator = null;
        private DocType docType = null;
        private List<Node> rootNodes = null;
        
        private boolean cdataMode = false;
        private boolean dtdMode = false;

        
        
        public XmlSAXHandler(final String documentName,
                final org.xml.sax.EntityResolver entityResolver, 
                final org.xml.sax.ErrorHandler errorHandler) {
            
            super();

            this.documentName = documentName;
            
            this.elementStack = new Stack<Tag>();
            this.rootNodes = new ArrayList<Node>();
            
            this.textBuffer = new char[512];
            this.cdataBuffer = new char[512];
            
            this.entityResolver = entityResolver;
            this.errorHandler = errorHandler;
            
        }
        

        
        public DocType getDocType() {
            return this.docType;
        }
        
        public List<Node> getRootNodes() {
            return this.rootNodes;
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
                        new CDATASection(Arrays.copyOf(this.cdataBuffer, this.cdataBufferLen), false);
                this.elementStack.peek().addChild(cdata);
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
            
            EntitySubstitutionTemplateReader.removeEntitySubstitutions(ch, start, length);
            if (this.cdataMode) {
                
                while (this.cdataBufferLen + length > this.cdataBuffer.length) {
                    this.cdataBuffer = Arrays.copyOf(this.cdataBuffer, this.cdataBuffer.length * 2);
                }
                System.arraycopy(ch, start, this.cdataBuffer, this.cdataBufferLen, length);
                this.cdataBufferLen += length;
                
            } else {
                
                while (this.textBufferLen + length > this.textBuffer.length) {
                    this.textBuffer = Arrays.copyOf(this.textBuffer, this.textBuffer.length * 2);
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
                        new Comment(Arrays.copyOfRange(ch, start, start + length));
                this.elementStack.peek().addChild(comment);
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
            
            flushBuffer();

            Integer lineNumber = null;
            if (this.locator != null) {
                lineNumber = Integer.valueOf(this.locator.getLineNumber());
            }
            
            final Tag tag = new Tag(qName, this.documentName, lineNumber);
            
            for (int i = 0; i < attributes.getLength(); i++) {
                tag.setAttribute(
                        attributes.getQName(i), 
                        EntitySubstitutionTemplateReader.removeEntitySubstitutions(attributes.getValue(i)));
            }
            
            this.elementStack.push(tag);
            
        }
        

        
        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            
            flushBuffer();
            
            final Tag tag = this.elementStack.pop();
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(tag);
            } else {
                final Tag parent = this.elementStack.peek();
                parent.addChild(tag);
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
            this.docType = new DocType(name, publicId, systemId);
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

                final Tag tag = this.elementStack.peek();
                final Node textNode = 
                        new Text(Arrays.copyOf(this.textBuffer, this.textBufferLen), false);
                tag.addChild(textNode);
            
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

    
    
}
