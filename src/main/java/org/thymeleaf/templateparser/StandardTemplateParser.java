/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.aurora.text.LimitedSizeCacheTextRepository;
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
 * @since 3.0.0
 * 
 */
public class StandardTemplateParser implements ITemplateParser {


    public static final StandardTemplateParser INSTANCE = new StandardTemplateParser();

    protected static final IMarkupParser PARSER;


    private static Set<String> ALL_STANDARD_ELEMENT_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(new String[]{
                    "a", "abbr", "address", "area", "article", "aside",
                    "audio", "b", "base", "bdi", "bdo", "blockquote",
                    "body", "br", "button", "canvas", "caption", "cite",
                    "code", "col", "colgroup", "command", "content", "datalist",
                    "dd", "decorator", "del", "details", "dfn", "dialog",
                    "div", "dl", "dt", "element", "em", "embed",
                    "fieldset", "figcaption", "figure", "footer", "form", "g",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "head", "header", "hgroup", "hr", "html", "i",
                    "iframe", "img", "input", "ins", "kbd", "keygen",
                    "label", "legend", "li", "link", "main", "map",
                    "mark", "menu", "menuitem", "meta", "meter", "nav",
                    "noscript", "object", "ol", "optgroup", "option", "output",
                    "p", "param", "pre", "progress", "rb", "rp",
                    "rt", "rtc", "ruby", "s", "samp", "script",
                    "section", "select", "shadow", "small", "source", "span",
                    "strong", "style", "sub", "summary", "sup", "table",
                    "tbody", "td", "template", "textarea", "tfoot", "th",
                    "thead", "time", "title", "tr", "track", "u",
                    "ul", "var", "video", "wbr"
            })));

    private static Set<String> ALL_STANDARD_ATTRIBUTE_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(new String[]{
                    "abbr","accept","accept-charset","accesskey","action","align",
                    "alt","archive","autocomplete","autofocus","autoplay","axis",
                    "border","cellpadding","cellspacing","challenge","char","charoff",
                    "charset","checked","cite","class","classid","codebase",
                    "codetype","cols","colspan","command","content","contenteditable",
                    "contextmenu","controls","coords","data","datetime","declare",
                    "default","defer","dir","disabled","draggable","dropzone",
                    "enctype","for","form","formaction","formenctype","formmethod",
                    "formnovalidate","formtarget","frame","headers","height","hidden",
                    "high","href","hreflang","http-equiv","icon","id",
                    "ismap","keytype","kind","label","lang","list",
                    "longdesc","loop","low","max","maxlength","media",
                    "method","min","multiple","muted","name","nohref",
                    "novalidate","onabort","onafterprint","onbeforeprint","onbeforeunload","onblur",
                    "oncanplay","oncanplaythrough","onchange","onclick","oncontextmenu","oncuechange",
                    "ondblclick","ondrag","ondragend","ondragenter","ondragleave","ondragover",
                    "ondragstart","ondrop","ondurationchange","onemptied","onended","onerror",
                    "onfocus","onformchange","onforminput","onhaschange","oninput","oninvalid",
                    "onkeydown","onkeypress","onkeyup","onload","onloadeddata","onloadedmetadata",
                    "onloadstart","onmessage","onmousedown","onmousemove","onmouseout","onmouseover",
                    "onmouseup","onmousewheel","onoffline","ononline","onpagehide","onpageshow",
                    "onpause","onplay","onplaying","onpopstate","onprogress","onratechange",
                    "onredo","onreset","onresize","onscroll","onseeked","onseeking",
                    "onselect","onstalled","onstorage","onsubmit","onsuspend","ontimeupdate",
                    "onundo","onunload","onvolumechange","onwaiting","open","optimum",
                    "pattern","placeholder","poster","preload","profile","radiogroup",
                    "readonly","rel","required","rev","rows","rowspan",
                    "rules","scheme","scope","selected","shape","size",
                    "span","spellcheck","src","srclang","standby","style",
                    "summary","tabindex","title","translate","type","usemap",
                    "valign","value","valuetype","width","xml:lang","xml:space",
                    "xmlns"
            })));


    static final ParseConfiguration HTML_PARSING_CONFIGURATION;
    static final ITextRepository TEXT_REPOSITORY;

    
    
    static {

        HTML_PARSING_CONFIGURATION = ParseConfiguration.htmlConfiguration();

        final List<String> unremovableTexts  = new ArrayList<String>();
        unremovableTexts.addAll(ALL_STANDARD_ELEMENT_NAMES);
        unremovableTexts.addAll(ALL_STANDARD_ATTRIBUTE_NAMES);
        unremovableTexts.add("\n");
        unremovableTexts.add("\n  ");
        unremovableTexts.add("\n    ");
        unremovableTexts.add("\n      ");
        unremovableTexts.add("\n        ");
        unremovableTexts.add("\n          ");
        unremovableTexts.add("\n            ");
        unremovableTexts.add("\n              ");
        unremovableTexts.add("\n                ");
        unremovableTexts.add("\n\t");
        unremovableTexts.add("\n\t\t");
        unremovableTexts.add("\n\t\t\t");
        unremovableTexts.add("\n\t\t\t\t");

        // Size = 10MBytes (1 char = 2 bytes)
        TEXT_REPOSITORY = new LimitedSizeCacheTextRepository(5242880, unremovableTexts.toArray(new String[unremovableTexts.size()]));

        PARSER = new MarkupParser(HTML_PARSING_CONFIGURATION);
    }
    
    
    
    private StandardTemplateParser() {
        super();
    }
    

    
    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final Reader reader) {

        try {
            
            return doParse(documentName, reader);
            
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final ParseException e) {
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

    
    
    
    private static Document doParse(final String documentName, final Reader reader)
            throws ParseException {

        final TemplateAttoHandler handler = new TemplateAttoHandler(documentName);

        PARSER.parse(reader, handler);
        
        final String docTypeClause = handler.getDocTypeClause();
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

    
    
    
    
    
    private static final class TemplateAttoHandler extends AbstractMarkupHandler {

        private static final Logger logger = LoggerFactory.getLogger(StandardTemplateParser.class);
        
        private final String documentName;

        private ParseStatus parseStatus;
        private IMarkupParser parser;
        private IMarkupHandler handlerChain;

        private final Stack<NestableNode> elementStack;
        
        /*
         * TODO Should be an AttributeHolder instead of an Element in >= 2.1
         */
        private Element currentElement = null;

        private List<Node> rootNodes = null;

        private String docTypeClause = null;
        private String docTypeRootElementName = null;
        private String docTypePublicId = null;
        private String docTypeSystemId = null;
        
        private String xmlEncoding = null;
        private String xmlVersion = null;
        private boolean xmlStandalone = false;

        /*
         * These structures allow reporting the correct (line,col) pair in DOM nodes during an embedded parsing
         * operation performed inside a prototype-only comment block ('<!--/*/  /*/-->')
         */
        private int lineOffset = 0;
        private int colOffset = 0;

        /*
         * These structures help processing (or more specifically, not-processing) parser-level comment blocks,
         * which contents (reported as Text because the parser will be disabled inside them) should be completely
         * ignored.
         */
        private static final char[] PARSER_LEVEL_COMMENT_CLOSE = "*/-->".toCharArray();
        private boolean inParserLevelCommentBlock = false;





        public TemplateAttoHandler(final String documentName) {
            
            super();

            this.documentName = documentName;
            
            this.elementStack = new Stack<NestableNode>();
            this.rootNodes = new ArrayList<Node>(6);
            
        }


        public String getDocTypeClause() {
            return this.docTypeClause;
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
         * Handler maintenance methods
         * -----------------
         */

        @Override
        public void setParseStatus(final ParseStatus status) {
            this.parseStatus = status;
            super.setParseStatus(status);
        }


        @Override
        public void setParser(final IMarkupParser parser) {
            this.parser = parser;
            super.setParser(parser);
        }

        @Override
        public void setHandlerChain(final IMarkupHandler handlerChain) {
            this.handlerChain = handlerChain;
            super.setHandlerChain(handlerChain);
        }





        /*
         * -----------------
         * Document handling
         * -----------------
         */


        @Override
        public void handleDocumentEnd(
                final long endTimeNanos, final long totalTimeNanos,
                final int line, final int col)
                throws ParseException {

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
        public void handleXmlDeclaration(
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
                throws ParseException {

            if (versionLen > 0) {
                this.xmlVersion = TEXT_REPOSITORY.getText(buffer, versionOffset, versionLen);
            }
            if (encodingLen > 0) {
                this.xmlEncoding = TEXT_REPOSITORY.getText(buffer, encodingOffset, encodingLen);
            }
            if (standaloneLen > 0) {
                this.xmlStandalone = Boolean.parseBoolean(TEXT_REPOSITORY.getText(buffer, standaloneOffset, standaloneLen));
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
                throws ParseException {

            if (elementNameLen > 0) {
                this.docTypeRootElementName = TEXT_REPOSITORY.getText(buffer, elementNameOffset, elementNameLen);
            }
            if (publicIdLen > 0) {
                this.docTypePublicId = TEXT_REPOSITORY.getText(buffer, publicIdOffset, publicIdLen);
            }
            if (systemIdLen > 0) {
                this.docTypeSystemId = TEXT_REPOSITORY.getText(buffer, systemIdOffset, systemIdLen);
            }

            this.docTypeClause = TEXT_REPOSITORY.getText(buffer, outerOffset, outerLen);

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
                throws ParseException {

            final String content = TEXT_REPOSITORY.getText(buffer, contentOffset, contentLen);
            final Node cdata = new CDATASection(content, this.documentName, Integer.valueOf(line + lineOffset), true);

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
                throws ParseException {

            if (this.inParserLevelCommentBlock) {
                // We are inside a parser-level comment block, which contents are being reported as text
                // because parsing has been disabled. Simply ignore unless the node starts with the closing sequence
                // of the parser-level comment block, in which case we just remove this sequence, put the flag
                // to false and handle the rest of the Text.

                for (int i = 0; i < PARSER_LEVEL_COMMENT_CLOSE.length; i++) {
                    if (buffer[offset + i] != PARSER_LEVEL_COMMENT_CLOSE[i]) {
                        // Ignore the Text event
                        return;
                    }
                }

                // We actually found the end of the parser-level comment block, so we should just process the rest of the Text node
                this.inParserLevelCommentBlock = false;
                if (len - PARSER_LEVEL_COMMENT_CLOSE.length > 0) {
                    handleText(
                            buffer,
                            offset + PARSER_LEVEL_COMMENT_CLOSE.length, len - PARSER_LEVEL_COMMENT_CLOSE.length,
                            line, col + PARSER_LEVEL_COMMENT_CLOSE.length);
                }
                return; // No text left to handle

            }

            final String content = TEXT_REPOSITORY.getText(buffer, offset, len);
            final Node textNode = new Text(content, this.documentName, Integer.valueOf(line + lineOffset), true);
            
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
                throws ParseException {

            if (isPrototypeOnlyCommentBlock(buffer, contentOffset, contentLen)) {
                handlePrototypeOnlyComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
                return;
            }
            // This check must always be executed AFTER checking for prototype-only comment blocks
            if (isParserLevelCommentStartBlock(buffer, contentOffset, contentLen)) {
                handleParserLevelComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
                return;
            }

            handleNormalComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

        }


        private static boolean isPrototypeOnlyCommentBlock(
                final char[] buffer, final int contentOffset, final int contentLen) {

            return (buffer[contentOffset] == '/' &&
                    buffer[contentOffset + 1] == '*' &&
                    buffer[contentOffset + 2] == '/' &&
                    buffer[contentOffset + contentLen - 3] == '/' &&
                    buffer[contentOffset + contentLen - 2] == '*' &&
                    buffer[contentOffset + contentLen - 1] == '/');

        }


        private static boolean isParserLevelCommentStartBlock(
                final char[] buffer, final int contentOffset, final int contentLen) {

            // This check must always be executed AFTER checking for prototype-only comment blocks
            // Note we only look for the starting sequence of the block, as we will disable the parser
            // until we find the closing sequence ('*/-->') [note the inner content will be reported
            // as text, and we should ignore it]
            return (buffer[contentOffset] == '/' &&
                    buffer[contentOffset + 1] == '*');

        }


        private static boolean isParserLevelCommentEndBlock(
                final char[] buffer, final int contentOffset, final int contentLen) {

            // This check must always be executed AFTER checking for prototype-only comment blocks
            // This is used in order to determine whether the same comment block starts AND ends the parser-level
            // comment, because in this case we should not involve Text handling in this operation
            return (buffer[contentOffset + contentLen - 2] == '*' &&
                    buffer[contentOffset + contentLen - 1] == '/');

        }


        private void handleNormalComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws ParseException {

            final String content = TEXT_REPOSITORY.getText(buffer, contentOffset, contentLen);

            final Comment comment = new Comment(content, this.documentName, Integer.valueOf(line + lineOffset));

            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(comment);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(comment);
            }

        }


        private void handlePrototypeOnlyComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws ParseException {

            /*
             * Arrange offsets, so that DOM element positions in the embedded parse operation
             * are reported correctly.
             *
             * Note we will never have a level > 0 embedded prototype-only comment, because the '--' sequence
             * is forbidden inside a comment (it ends it). And besides that, it makes no sense :)
             */
            this.lineOffset = line - 1; // -1 --> lines are reported starting with 1, but we need a 0-based offset
            this.colOffset = col + 2; // 2 = 3 - 1 --> because of the '/*/' sequence (-1 in order to work as offset)

            // We parse the comment content using this same handler object, but removing the "/*/.../*/"
            this.parser.parse(buffer, contentOffset + 3, contentLen - 6, this.handlerChain);

            /*
             * Return offsets to their original value.
             */
            this.lineOffset = 0;
            this.colOffset = 0;

        }


        private void handleParserLevelComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws ParseException {

            if (isParserLevelCommentEndBlock(buffer, contentOffset, contentLen)) {
                // This block both starts AND ends the parser-level comment, so ignoring it
                // should be enough, without involving any text handling events
                return;
            }

            // Comment blocks of this type provoke the disabling of the parser until we find the
            // closing sequence ('*/-->'), which might appear in a different block of code
            this.inParserLevelCommentBlock = true;

            // Disable parsing until we find the end of the parser-level comment block
            this.parseStatus.setParsingDisabled(PARSER_LEVEL_COMMENT_CLOSE);

        }




        
        /*
         * ----------------
         * Element handling
         * ----------------
         */

        
        @Override
        public void handleAttribute(
                final char[] buffer, 
                final int nameOffset, final int nameLen,
                final int nameLine, final int nameCol, 
                final int operatorOffset, final int operatorLen,
                final int operatorLine, final int operatorCol, 
                final int valueContentOffset, final int valueContentLen, 
                final int valueOuterOffset, final int valueOuterLen,
                final int valueLine, final int valueCol) 
                throws ParseException {

            final String attributeName = TEXT_REPOSITORY.getText(buffer, nameOffset, nameLen);
            final String attributeValue = TEXT_REPOSITORY.getText(buffer, valueContentOffset, valueContentLen);
            
            this.currentElement.setAttribute(
                    attributeName, false, attributeValue, true);

        }


        
        
        @Override
        public void handleStandaloneElementStart(
                final char[] buffer,
                final int offset, final int len,
                final boolean minimized,
                final int line, final int col)
                throws ParseException {

            final String elementName = TEXT_REPOSITORY.getText(buffer, offset, len);
            
            final Element element = 
                    new Element(elementName, this.documentName, Integer.valueOf(line + lineOffset), RepresentationInTemplate.STANDALONE);
            this.currentElement = element;
            
            if (this.elementStack.isEmpty()) {
                this.rootNodes.add(element);
            } else {
                final NestableNode parent = this.elementStack.peek();
                parent.addChild(element);
            }

        }




        @Override
        public void handleOpenElementStart(
                final char[] buffer,
                final int offset, final int len,
                final int line, final int col)
                throws ParseException {

            final String elementName = TEXT_REPOSITORY.getText(buffer, offset, len);
            
            final Element element = 
                    new Element(elementName, this.documentName, Integer.valueOf(line + lineOffset), RepresentationInTemplate.ONLY_OPEN);
            this.currentElement = element;
            
            this.elementStack.push(element);

        }

        


        @Override
        public void handleCloseElementStart(
                final char[] buffer,
                final int offset, final int len,
                final int line, final int col) 
                throws ParseException {

            final String closedElementName = TEXT_REPOSITORY.getText(buffer, offset, len);

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
