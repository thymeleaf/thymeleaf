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
package org.thymeleaf.engine.markup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.attoparser.AttoHandleResult;
import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandleResult;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.MarkupParsingConfiguration;
import org.attoparser.markup.html.AbstractDetailedNonValidatingHtmlAttoHandler;
import org.attoparser.markup.html.HtmlParsing;
import org.attoparser.markup.html.elements.IHtmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.engine.markup.text.IMarkupTextRepository;
import org.thymeleaf.engine.markup.MarkupEngineConfiguration;
import org.thymeleaf.engine.markup.handler.IMarkupHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardHtmlParser implements IMarkupParser {

    protected final MarkupAttoParser attoParser;
    
    static final MarkupParsingConfiguration MARKUP_PARSING_CONFIGURATION;

    
    
    static {

        /*
         * We don't need the underlying AttoHandler to perform all these validations, and this will
         * remove the necessity to use a stack at the Markup Detailed AttoHandler level (not at the HTML
         * level, which is needed to determine HTML-specific aspects like which elements should be reported
         * as standalone (void) even if they have no closing solidus character (img, br, etc.) and the fact
         * that script and style have CDATA bodies (instead of PCDATA).
         */
        MARKUP_PARSING_CONFIGURATION = HtmlParsing.baseHtmlMarkupParsingConfiguration();
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(MarkupParsingConfiguration.ElementBalancing.AUTO_CLOSE);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(false);
        MARKUP_PARSING_CONFIGURATION.setRequireUniqueAttributesInElement(false);
        MARKUP_PARSING_CONFIGURATION.setRequireXmlWellFormedAttributeValues(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(MarkupParsingConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParsingConfiguration().setDoctypePresence(MarkupParsingConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParsingConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParsingConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParsingConfiguration().setXmlDeclarationPresence(MarkupParsingConfiguration.PrologPresence.ALLOWED);

    }
    
    
    
    public StandardHtmlParser(final int bufferSize, final int bufferPoolSize) {
        super();
        this.attoParser = new MarkupAttoParser(false, bufferPoolSize, bufferSize);
    }
    

    
    
    public final void parseTemplate(final MarkupEngineConfiguration configuration,
                                    final IMarkupHandler handler,
                                    final String documentName,
                                    final Reader reader) {
        parseTemplate(configuration, handler, documentName, reader, 0, 0);
    }



    public final void parseTemplate(final MarkupEngineConfiguration configuration,
                                    final IMarkupHandler handler,
                                    final String documentName,
                                    final Reader reader,
                                    final int lineOffset, final int colOffset) {

        try {


            final StandardHtmlParserAttoHandler attoHandler =
                    new StandardHtmlParserAttoHandler(configuration, handler, this.attoParser, documentName, lineOffset, colOffset);
            attoParser.parse(reader, attoHandler);

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



    public final void parseFragment(final MarkupEngineConfiguration configuration,
                                    final IMarkupHandler handler,
                                    final String fragment) {
        parseFragment(configuration, handler, fragment, 0, 0);
    }



    public final void parseFragment(final MarkupEngineConfiguration configuration,
                                    final IMarkupHandler handler,
                                    final String fragment,
                                    final int lineOffset, final int colOffset) {
        parseTemplate(configuration, handler, null, new StringReader(fragment), lineOffset, colOffset);
    }

    
    
    
    
    private static final class StandardHtmlParserAttoHandler extends AbstractDetailedNonValidatingHtmlAttoHandler {

        private static final Logger logger = LoggerFactory.getLogger(StandardHtmlParserAttoHandler.class);
        
        private final IMarkupHandler markupHandler;
        private final IMarkupTextRepository textRepository;
        private final MarkupAttoParser attoParser;
        private final String documentName;

        /*
         * These structures allow reporting the correct (line,col) pair in DOM nodes during an embedded parsing
         * operation performed inside a prototype-only comment block ('<!--/*/  /*/-->')
         */
        private int lineOffset;
        private int colOffset;

        /*
         * These structures help processing (or more specifically, not-processing) parser-level comment blocks,
         * which contents (reported as Text because the parser will be disabled inside them) should be completely
         * ignored.
         */
        private static final char[] PARSER_LEVEL_COMMENT_CLOSE = "*/-->".toCharArray();
        private static final IAttoHandleResult PARSER_LEVEL_COMMENT_OPEN_RESULT = new AttoHandleResult(PARSER_LEVEL_COMMENT_CLOSE);
        private boolean inParserLevelCommentBlock = false;





        public StandardHtmlParserAttoHandler(
                final MarkupEngineConfiguration configuration, final IMarkupHandler markupHandler,
                final MarkupAttoParser attoParser, final String documentName,
                final int lineOffset, final int colOffset) {
            
            super(StandardHtmlParser.MARKUP_PARSING_CONFIGURATION);

            this.markupHandler = markupHandler;
            this.textRepository = configuration.getTextRepository();
            this.attoParser = attoParser;
            this.documentName = documentName;

            this.lineOffset = lineOffset;
            this.colOffset = colOffset;

        }

        

        /*
         * -----------------
         * Document handling
         * -----------------
         */

        @Override
        public IAttoHandleResult handleDocumentStart(
                final long startTimeNanos,
                final int line, final int col,
                final MarkupParsingConfiguration parsingConfiguration)
               throws AttoParseException {

            this.markupHandler.onDocumentStart(startTimeNanos, this.documentName);
            return null;

        }


        @Override
        public IAttoHandleResult handleDocumentEnd(
                final long endTimeNanos, final long totalTimeNanos,
                final int line, final int col, 
                final MarkupParsingConfiguration configuration)
                throws AttoParseException {

            this.markupHandler.onDocumentEnd(endTimeNanos, totalTimeNanos, this.documentName);

            if (logger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(totalTimeNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP);
                if (this.documentName == null) {
                    logger.trace("[THYMELEAF][{}][{}][{}] Processed unnamed template or fragment in {} nanoseconds (approx. {}ms)",
                            new Object[] {TemplateEngine.threadIndex(), 
                                            elapsed, elapsedMs,
                                            elapsed, elapsedMs});
                } else {
                    logger.trace("[THYMELEAF][{}][{}][{}][{}] Processed template \"{}\" in {} nanoseconds (approx. {}ms)",
                            new Object[] {TemplateEngine.threadIndex(), 
                                            this.documentName, elapsed, elapsedMs,
                                            this.documentName, elapsed, elapsedMs});
                }
            }

            return null;

        }

        

        
        /*
         * ------------------------
         * XML Declaration handling
         * ------------------------
         */
        
        
        @Override
        public IAttoHandleResult handleXmlDeclarationDetail(
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

            final String xmlDeclaration = this.textRepository.getText(buffer, outerOffset, outerLen);

            final String version =
                    (versionLen > 0)? this.textRepository.getText(buffer, versionOffset, versionLen) : null;
            final String encoding =
                    (encodingLen > 0)? this.textRepository.getText(buffer, encodingOffset, encodingLen) : null;
            final boolean standalone =
                    (standaloneLen > 0)? Boolean.parseBoolean(this.textRepository.getText(buffer, standaloneOffset, standaloneLen)): false;

            this.markupHandler.onXmlDeclaration(xmlDeclaration, version, encoding, standalone, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;
            
        }



        
        /*
         * ----------------
         * DOCTYPE handling
         * ----------------
         */
        

        @Override
        public IAttoHandleResult handleDocType(
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

            final String docTypeClause = this.textRepository.getText(buffer, outerOffset, outerLen);

            final String rootElementName =
                    (elementNameLen > 0)? this.textRepository.getText(buffer, elementNameOffset, elementNameLen) : null;
            final String publicId =
                    (publicIdLen > 0)? this.textRepository.getText(buffer, publicIdOffset, publicIdLen) : null;
            final String systemId =
                    (systemIdLen > 0)? this.textRepository.getText(buffer, systemIdOffset, systemIdLen) : null;

            this.markupHandler.onDocTypeClause(
                    docTypeClause, rootElementName, publicId, systemId, this.documentName,
                    outerLine + this.lineOffset, outerCol + this.colOffset);

            return null;

        }

        

        
        /*
         * ----------------------
         * CDATA Section handling
         * ----------------------
         */


        @Override
        public IAttoHandleResult handleCDATASection(
                final char[] buffer, 
                final int contentOffset, final int contentLen, 
                final int outerOffset, final int outerLen, 
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onCDATASection(buffer, contentOffset, contentLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }
        
        

        
        /*
         * -------------
         * Text handling
         * -------------
         */

        
        @Override
        public IAttoHandleResult handleText(
                final char[] buffer, 
                final int offset, final int len, 
                final int line, final int col) 
                throws AttoParseException {

            if (this.inParserLevelCommentBlock) {
                // We are inside a parser-level comment block, which contents are being reported as text
                // because parsing has been disabled. Simply ignore unless the node starts with the closing sequence
                // of the parser-level comment block, in which case we just remove this sequence, put the flag
                // to false and handle the rest of the Text.

                for (int i = 0; i < PARSER_LEVEL_COMMENT_CLOSE.length; i++) {
                    if (buffer[offset + i] != PARSER_LEVEL_COMMENT_CLOSE[i]) {
                        // Ignore the Text event
                        return null;
                    }
                }

                // We actually found the end of the parser-level comment block, so we should just process the rest of the Text node
                this.inParserLevelCommentBlock = false;
                if (len - PARSER_LEVEL_COMMENT_CLOSE.length > 0) {
                    return handleText(
                            buffer,
                            offset + PARSER_LEVEL_COMMENT_CLOSE.length, len - PARSER_LEVEL_COMMENT_CLOSE.length,
                            line, col + PARSER_LEVEL_COMMENT_CLOSE.length);
                }
                return null; // No text left to handle

            }

            this.markupHandler.onText(buffer, offset, len, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }
        


        
        /*
         * ----------------
         * Comment handling
         * ----------------
         */
        

        @Override
        public IAttoHandleResult handleComment(
                final char[] buffer,
                final int contentOffset, final int contentLen, 
                final int outerOffset, final int outerLen, 
                final int line, final int col)
                throws AttoParseException {

            if (isPrototypeOnlyCommentBlock(buffer, contentOffset, contentLen)) {
                return handlePrototypeOnlyComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
            }
            // This check must always be executed AFTER checking for prototype-only comment blocks
            if (isParserLevelCommentStartBlock(buffer, contentOffset, contentLen)) {
                return handleParserLevelComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
            }

            return handleNormalComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

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


        private IAttoHandleResult handleNormalComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onComment(buffer, contentOffset, contentLen, this.documentName, line + this.lineOffset, col + this.lineOffset);

            return null;

        }


        private IAttoHandleResult handlePrototypeOnlyComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws AttoParseException {

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
            this.attoParser.parse(buffer, contentOffset + 3, contentLen - 6, this);

            /*
             * Return offsets to their original value.
             */
            this.lineOffset = 0;
            this.colOffset = 0;

            return null;

        }


        private IAttoHandleResult handleParserLevelComment(
                final char[] buffer,
                final int contentOffset, final int contentLen,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws AttoParseException {

            if (isParserLevelCommentEndBlock(buffer, contentOffset, contentLen)) {
                // This block both starts AND ends the parser-level comment, so ignoring it
                // should be enough, without involving any text handling events
                return null;
            }

            // Comment blocks of this type provoke the disabling of the parser until we find the
            // closing sequence ('*/-->'), which might appear in a different block of code
            this.inParserLevelCommentBlock = true;
            return PARSER_LEVEL_COMMENT_OPEN_RESULT;

        }




        
        /*
         * ----------------
         * Element handling
         * ----------------
         */

        
        @Override
        public IAttoHandleResult handleHtmlAttribute(
                final char[] buffer, 
                final int nameOffset, final int nameLen,
                final int nameLine, final int nameCol, 
                final int operatorOffset, final int operatorLen,
                final int operatorLine, final int operatorCol, 
                final int valueContentOffset, final int valueContentLen, 
                final int valueOuterOffset, final int valueOuterLen,
                final int valueLine, final int valueCol) 
                throws AttoParseException {

            this.markupHandler.onAttribute(
                    buffer,
                    nameOffset, nameLen,
                    nameLine + this.lineOffset, nameCol + this.colOffset,
                    operatorOffset, operatorLen,
                    operatorLine + this.lineOffset, operatorCol + this.colOffset,
                    valueContentOffset, valueContentLen,
                    valueOuterOffset, valueOuterLen,
                    valueLine + this.lineOffset, valueCol + this.colOffset, this.documentName);

            return null;

        }


        
        
        @Override
        public IAttoHandleResult handleHtmlStandaloneElementStart(
                final IHtmlElement element,
                final boolean minimized,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) 
                throws AttoParseException {

            this.markupHandler.onStandaloneElementStart(
                    element.getName(), buffer, nameOffset, nameLen, minimized, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlOpenElementStart(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onOpenElementStart(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }

        


        @Override
        public IAttoHandleResult handleHtmlCloseElementStart(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) 
                throws AttoParseException {

            this.markupHandler.onCloseElementStart(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlAutoCloseElementStart(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onAutoCloseElementStart(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlUnmatchedCloseElementStart(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onUnmatchedCloseElementStart(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlStandaloneElementEnd(
                final IHtmlElement element,
                final boolean minimized,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onStandaloneElementEnd(
                    element.getName(), buffer, nameOffset, nameLen, minimized, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlOpenElementEnd(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onOpenElementEnd(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }


        @Override
        public IAttoHandleResult handleHtmlCloseElementEnd(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onCloseElementEnd(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlAutoCloseElementEnd(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onAutoCloseElementEnd(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlUnmatchedCloseElementEnd(
                final IHtmlElement element,
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onUnmatchedCloseElementEnd(
                    element.getName(), buffer, nameOffset, nameLen, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }



        @Override
        public IAttoHandleResult handleHtmlInnerWhiteSpace(
                final char[] buffer,
                final int offset, final int len,
                final int line, final int col)
                throws AttoParseException {

            this.markupHandler.onElementInnerWhiteSpace(
                    buffer, offset, len, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }





        /*
         * -------------------------------
         * Processing Instruction handling
         * -------------------------------
         */


        @Override
        public IAttoHandleResult handleProcessingInstruction(
                final char[] buffer,
                final int targetOffset, final int targetLen,
                final int targetLine, final int targetCol,
                final int contentOffset, final int contentLen,
                final int contentLine, final int contentCol,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws AttoParseException {

            final String processingInstruction = this.textRepository.getText(buffer, outerOffset, outerLen);

            final String target = this.textRepository.getText(buffer, targetOffset, targetLen);
            final String content = this.textRepository.getText(buffer, contentOffset, contentLen);

            this.markupHandler.onProcessingInstruction(
                    processingInstruction, target, content, this.documentName, line + this.lineOffset, col + this.colOffset);

            return null;

        }


    }

    
    
    
}
