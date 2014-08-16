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
package org.thymeleaf.templateparser;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.html.AbstractDetailedNonValidatingHtmlAttoHandler;
import org.attoparser.markup.html.HtmlParsing;
import org.attoparser.markup.html.HtmlParsingConfiguration;
import org.attoparser.markup.html.elements.IHtmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Element.RepresentationInTemplate;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * <p>
 *     Standard implementation of the {@link org.thymeleaf.templateparser.ITemplateParser} interface,
 *     making use internally of the <a href="http://www.attoparser.org">attoparser</a> library.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0
 * 
 */
public class StandardTemplateParser implements ITemplateParser {

    
    private static final MarkupAttoParser parser = new MarkupAttoParser();
    
    static final HtmlParsingConfiguration  HTML_PARSING_CONFIGURATION;
    
    
    
    static {
        HTML_PARSING_CONFIGURATION = HtmlParsing.htmlParsingConfiguration();
    }
    
    
    
    public StandardTemplateParser() {
        super();
    }
    

    
    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {

        final StandardTemplatePreprocessingReader templateReader = getTemplatePreprocessingReader(reader);
        
        try {
            
            return doParse(documentName, templateReader);
            
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final AttoParseException e) {
            String message = null;
            if (documentName == null) {
                message = 
                        String.format("Exception parsing unnamed document or fragment: line %d - column %d",
                                e.getLine(), e.getCol());
            } else {
                message = 
                        String.format("Exception parsing document: template=\"%s\", line %d - column %d",
                                documentName, e.getLine(), e.getCol());
            }
            throw new TemplateInputException(message, e);
        }
        
    }

    
    
    
    private static Document doParse(final String documentName, final StandardTemplatePreprocessingReader reader)
            throws AttoParseException {

        final TemplateAttoHandler handler = new TemplateAttoHandler(documentName);
        
        parser.parse(reader, handler);
        
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
        final Document document = 
                parseTemplate(
                        configuration,
                        null, // documentName 
                        new StringReader(fragment));
        return document.getChildren();
    }



    public final List<Node> parseFragment(final Configuration configuration, final String text, final int offset, final int len) {
        final Document document =
                parseTemplate(
                        configuration,
                        null, // documentName
                        new StringReader(text.substring(offset, offset + len)));
        return document.getChildren();
    }

    
    

    protected StandardTemplatePreprocessingReader getTemplatePreprocessingReader(final Reader reader) {
        if (reader instanceof StandardTemplatePreprocessingReader) {
            final StandardTemplatePreprocessingReader templatePreprocessingReader = (StandardTemplatePreprocessingReader) reader;
            return new StandardTemplatePreprocessingReader(templatePreprocessingReader.getInnerReader(), 8192);
        }
        return new StandardTemplatePreprocessingReader(reader, 8192);
    }
    
    
    
    
    
    
    
    private static final class TemplateAttoHandler extends AbstractDetailedNonValidatingHtmlAttoHandler {

        private static final Logger logger = LoggerFactory.getLogger(TemplateAttoHandler.class);
        
        private final String documentName;
        private final Stack<NestableNode> elementStack;
        
        /*
         * TODO Should be an AttributeHolder instead of an Element in >= 2.1
         */
        private Element currentElement = null;

        private List<Node> rootNodes = null;
        
        private String docTypeRootElementName = null;
        private String docTypePublicId = null;
        private String docTypeSystemId = null;
        
        private String xmlEncoding = null;
        private String xmlVersion = null;
        private boolean xmlStandalone = false;
        
        
        public TemplateAttoHandler(final String documentName) {
            
            super(StandardTemplateParser.HTML_PARSING_CONFIGURATION);

            this.documentName = documentName;
            
            this.elementStack = new Stack<NestableNode>();
            this.rootNodes = new ArrayList<Node>();
            
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
        
        

        /*
         * -----------------
         * Document handling
         * -----------------
         */


        @Override
        public void handleDocumentEnd(
                final long endTimeNanos, final long totalTimeNanos,
                final int line, final int col, 
                final HtmlParsingConfiguration configuration)
                throws AttoParseException {

            super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col, configuration);
            
            if (logger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(totalTimeNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP);
                if (this.documentName == null) {
                    logger.trace("[THYMELEAF][{}][{}][{}] Parsed unnamed template or fragment in {} nanoseconds (approx. {}ms)", 
                            new Object[] {TemplateEngine.threadIndex(), 
                                            elapsed, elapsedMs,
                                            elapsed, elapsedMs});
                } else {
                    logger.trace("[THYMELEAF][{}][{}][{}][{}] Parsed template \"{}\" in {} nanoseconds (approx. {}ms)", 
                            new Object[] {TemplateEngine.threadIndex(), 
                                            this.documentName, elapsed, elapsedMs,
                                            this.documentName, elapsed, elapsedMs});
                }
            }
            
        }

        

        
        /*
         * ------------------------
         * XML Declaration handling
         * ------------------------
         */
        
        
        @Override
        public void handleXmlDeclarationDetail(
                final char[] buffer,
                final int keywordOffset, final int keywordLen, 
                final int keywordLine, final int keywordCol, 
                final int versionOffset, final int versionLen,
                final int versionLine, final int versionCol, 
                final int encodingOffset, final int encodingLen, 
                final int encodingLine, final int encodingCol,
                final int standaloneOffset, final int standaloneLen, 
                final int standaloneLine, final int standaloneCol, 
                final int outerOffset, final int outerLen, 
                final int line, final int col) 
                throws AttoParseException {

            super.handleXmlDeclarationDetail(buffer, keywordOffset, keywordLen,
                    keywordLine, keywordCol, versionOffset, versionLen, versionLine,
                    versionCol, encodingOffset, encodingLen, encodingLine, encodingCol,
                    standaloneOffset, standaloneLen, standaloneLine, standaloneCol,
                    outerOffset, outerLen, line, col);

            if (versionLen > 0) {
                this.xmlVersion = new String(buffer, versionOffset, versionLen);
            }
            if (encodingLen > 0) {
                this.xmlEncoding = new String(buffer, encodingOffset, encodingLen);
            }
            if (standaloneLen > 0) {
                this.xmlStandalone = Boolean.parseBoolean(new String(buffer, standaloneOffset, standaloneLen));
            }
            
        }



        
        /*
         * ----------------
         * DOCTYPE handling
         * ----------------
         */
        

        @Override
        public void handleDocType(
                final char[] buffer, 
                final int keywordOffset, final int keywordLen, 
                final int keywordLine, final int keywordCol,
                final int elementNameOffset, final int elementNameLen, 
                final int elementNameLine, final int elementNameCol, 
                final int typeOffset, final int typeLen, 
                final int typeLine, final int typeCol, 
                final int publicIdOffset, final int publicIdLen,
                final int publicIdLine, final int publicIdCol, 
                final int systemIdOffset, final int systemIdLen, 
                final int systemIdLine, final int systemIdCol,
                final int internalSubsetOffset, final int internalSubsetLen,
                final int internalSubsetLine, final int internalSubsetCol, 
                final int outerOffset, final int outerLen, 
                final int outerLine, final int outerCol)
                throws AttoParseException {

            super.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol,
                    elementNameOffset, elementNameLen, elementNameLine, elementNameCol,
                    typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen,
                    publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine,
                    systemIdCol, internalSubsetOffset, internalSubsetLen,
                    internalSubsetLine, internalSubsetCol, outerOffset, outerLen,
                    outerLine, outerCol);

            if (elementNameLen > 0) {
                this.docTypeRootElementName = new String(buffer, elementNameOffset, elementNameLen);
            }
            if (publicIdLen > 0) {
                this.docTypePublicId = new String(buffer, publicIdOffset, publicIdLen);
            }
            if (systemIdLen > 0) {
                this.docTypeSystemId = new String(buffer, systemIdOffset, systemIdLen);
            }
            
        }

        

        
        /*
         * ----------------------
         * CDATA Section handling
         * ----------------------
         */


        @Override
        public void handleCDATASection(
                final char[] buffer, 
                final int contentOffset, final int contentLen, 
                final int outerOffset, final int outerLen, 
                final int line, final int col)
                throws AttoParseException {

            super.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

            final String content = new String(buffer, contentOffset, contentLen);
            final Node cdata = new CDATASection(content, null, null, true);

            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(cdata);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(cdata);
            }
            
        }
        
        

        
        /*
         * -------------
         * Text handling
         * -------------
         */

        
        @Override
        public void handleText(
                final char[] buffer, 
                final int offset, final int len, 
                final int line, final int col) 
                throws AttoParseException {

            super.handleText(buffer, offset, len, line, col);

            final String content = new String(buffer, offset, len);
            final Node textNode = new Text(content, null, null, true);
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(textNode);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(textNode);
            }
            
        }
        


        
        /*
         * ----------------
         * Comment handling
         * ----------------
         */
        

        @Override
        public void handleComment(
                final char[] buffer,
                final int contentOffset, final int contentLen, 
                final int outerOffset, final int outerLen, 
                final int line, final int col)
                throws AttoParseException {

            super.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

            final String content = new String(buffer, contentOffset, contentLen);

            final Comment comment = new Comment(content);
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(comment);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(comment);
            }
            
        }
        

        
        /*
         * ----------------
         * Element handling
         * ----------------
         */

        
        @Override
        public void handleHtmlAttribute(
                final char[] buffer, 
                final int nameOffset, final int nameLen,
                final int nameLine, final int nameCol, 
                final int operatorOffset, final int operatorLen,
                final int operatorLine, final int operatorCol, 
                final int valueContentOffset, final int valueContentLen, 
                final int valueOuterOffset, final int valueOuterLen,
                final int valueLine, final int valueCol) 
                throws AttoParseException {

            super.handleHtmlAttribute(buffer, nameOffset, nameLen, nameLine, nameCol,
                    operatorOffset, operatorLen, operatorLine, operatorCol,
                    valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen,
                    valueLine, valueCol);
            
            final String attributeName = new String(buffer, nameOffset, nameLen);
            final String attributeValue = new String(buffer, valueContentOffset, valueContentLen);
            
            this.currentElement.setAttribute(
                    attributeName, false, attributeValue, true);
            
        }


        
        
        @Override
        public void handleHtmlStandaloneElementStart(
                final IHtmlElement htmlElement,
                final boolean minimized,
                final char[] buffer, 
                final int offset, final int len, 
                final int line, final int col) 
                throws AttoParseException {
            
            super.handleHtmlStandaloneElementStart(htmlElement, minimized, buffer, offset, len, line, col);
            
            final String elementName = new String(buffer, offset, len);
            
            final Element element = 
                    new Element(elementName, this.documentName, Integer.valueOf(line), RepresentationInTemplate.STANDALONE);
            this.currentElement = element;
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(element);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(element);
            }
            
        }




        @Override
        public void handleHtmlOpenElementStart(
                final IHtmlElement htmlElement,
                final char[] buffer, 
                final int offset, final int len,
                final int line, final int col)
                throws AttoParseException {

            super.handleHtmlOpenElementStart(htmlElement, buffer, offset, len, line, col);
            
            final String elementName = new String(buffer, offset, len);
            
            final Element element = 
                    new Element(elementName, this.documentName, Integer.valueOf(line), RepresentationInTemplate.ONLY_OPEN);
            this.currentElement = element;
            
            this.elementStack.push(element);
            
        }

        


        @Override
        public void handleHtmlCloseElementStart(
                final IHtmlElement htmlElement,
                final char[] buffer, 
                final int offset, final int len,
                final int line, final int col) 
                throws AttoParseException {

            super.handleHtmlCloseElementStart(htmlElement, buffer, offset, len, line, col);
            
            final String closedElementName = new String(buffer, offset, len);

            if (this.elementStack.isEmpty()) {
                throw new TemplateInputException("Unbalanced close tag \"" + closedElementName + "\". " +
                        "Perhaps you are trying to close a non-closable standalone tag? (e.g. <link>, <meta>...)");
            }

            searchInStack(closedElementName);

            // We are sure this is the node we want to close
            final NestableNode node = this.elementStack.pop();

            if (node instanceof Element) {

                final Element element = (Element) node;

                // Adjust the representation in template. Differentiating between being
                // empty or not will allow a more correct output behaviour if children are
                // added or removed.
                if (element.hasChildren()) {
                    element.setRepresentationInTemplate(RepresentationInTemplate.OPEN_AND_CLOSE_NONEMPTY);
                } else {
                    element.setRepresentationInTemplate(RepresentationInTemplate.OPEN_AND_CLOSE_EMPTY);
                }

            }
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(node);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(node);
            }
            
            
        }
        
        
        
        
        private void searchInStack(final String soughtElementName) {

            NestableNode node = this.elementStack.peek();
            
            while (true) {
    
                if (node instanceof Element) {
                    
                    final Element element = (Element) node;
                    final String elementName = element.getOriginalName();
                    
                    if (soughtElementName.equals(elementName)) {
                        return;
                    }
                    
                }
    
                // unbalancedNode == node, but we need to pop from stack
                final NestableNode unbalancedNode = this.elementStack.pop();
                
                if (this.elementStack.isEmpty()) {
                    // Can never happen because of the parser's Document Restrictions
                    // (no unbalanced close tags are allowed)
                    throw new TemplateInputException("Unbalanced close tag \"" + soughtElementName + "\"");
                }
                
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(unbalancedNode);
                
                node = parent;
                
            }
            
        }
        
        
    }

    
    
    
}
