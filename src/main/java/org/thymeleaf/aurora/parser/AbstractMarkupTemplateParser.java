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
package org.thymeleaf.aurora.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.BlockSelectorMarkupHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.engine.ITemplateHandler;
import org.thymeleaf.aurora.engine.TemplateHandlerAdapterMarkupHandler;
import org.thymeleaf.aurora.resource.CharArrayResource;
import org.thymeleaf.aurora.resource.IResource;
import org.thymeleaf.aurora.resource.ReaderResource;
import org.thymeleaf.aurora.resource.StringResource;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractMarkupTemplateParser implements ITemplateParser {


    private final IMarkupParser parser;
    private final boolean html;



    protected AbstractMarkupTemplateParser(final ParseConfiguration parseConfiguration, final int bufferPoolSize, final int bufferSize) {
        super();
        Validate.notNull(parseConfiguration, "Parse configuration cannot be null");
        this.parser = new MarkupParser(parseConfiguration, bufferPoolSize, bufferSize);
        this.html = parseConfiguration.getMode().equals(ParseConfiguration.ParsingMode.HTML);
    }




    /*
     * -------------------
     * PARSE METHODS
     * -------------------
     */



    public final void parse(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final ITemplateHandler templateHandler) {
        parse(configuration, templateMode, templateResource, null, templateHandler);
    }



    public final void parse(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final String[] selectors,
            final ITemplateHandler templateHandler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(templateResource, "Template Resource cannot be null");
        Validate.notNull(templateHandler, "Template Handler cannot be null");

        if (templateMode.isHTML()) {
            Validate.isTrue(this.html, "Parser is configured as XML, but HTML-mode template parsing is being requested");
        } else if (templateMode.isXML()) {
            Validate.isTrue(!this.html, "Parser is configured as HTML, but XML-mode template parsing is being requested");
        } else {
            throw new IllegalArgumentException(
                    "Parser is configured as " + (this.html? "HTML" : "XML") + " but an unsupported template mode " +
                    "has been specified: " + templateMode);
        }

        final String documentName = templateResource.getName();

        try {

            // The final step of the handler chain will be the adapter that will convert attoparser's handler chain to thymeleaf's.
            IMarkupHandler handler =
                        new TemplateHandlerAdapterMarkupHandler(
                                documentName,
                                templateHandler,
                                configuration.getTextRepository(),
                                configuration.getElementDefinitions(),
                                configuration.getAttributeDefinitions(),
                                templateMode);

            // If we need to select blocks, we will need a block selector here. Note this will get executed in the
            // handler chain AFTER thymeleaf's own TemplateHandlerAdapterMarkupHandler, so that we will be able to
            // include in selectors code inside prototype-only comments.
            if (selectors != null) {

                final String standardDialectPrefix = configuration.getStandardDialectPrefix();

                final TemplateFragmentMarkupReferenceResolver referenceResolver =
                        (standardDialectPrefix != null ?
                            TemplateFragmentMarkupReferenceResolver.forPrefix(this.html, standardDialectPrefix) : null);
                handler = new BlockSelectorMarkupHandler(handler, selectors, referenceResolver);
            }

            // This is the point at which we insert thymeleaf's own handler, which will take care of parser-level
            // and prototype-only comments.
            handler = new ThymeleafMarkupHandler(handler, documentName);

            // Each type of resource will require a different parser method to be called.
            if (templateResource instanceof ReaderResource) {

                this.parser.parse(((ReaderResource)templateResource).getContent(), handler);

            } else if (templateResource instanceof StringResource) {

                this.parser.parse(((StringResource)templateResource).getContent(), handler);

            } else if (templateResource instanceof CharArrayResource) {

                final CharArrayResource charArrayResource = (CharArrayResource) templateResource;
                this.parser.parse(charArrayResource.getContent(), charArrayResource.getOffset(), charArrayResource.getLen(), handler);

            } else {

                throw new IllegalArgumentException(
                        "Cannot parse: unrecognized " + IResource.class.getSimpleName() + " implementation: " + templateResource.getClass().getName());

            }

        } catch (final ParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, templateResource.getName(), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, templateResource.getName(), e);
        }

    }






    /*
     * ---------------------------
     * HANDLER IMPLEMENTATION
     * ---------------------------
     */



    protected static final class ThymeleafMarkupHandler extends AbstractChainedMarkupHandler {

        private static final Logger logger = LoggerFactory.getLogger(AbstractMarkupTemplateParser.class);

        private final String documentName;

        private ParseStatus parseStatus;
        private IMarkupParser parser;
        private IMarkupHandler handlerChain;

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
        private boolean inParserLevelCommentBlock = false;





        protected ThymeleafMarkupHandler(
                final IMarkupHandler next, final String documentName) {

            // We need to adapt the AttoParser adapter to Thymeleaf's own, in a way that causes the less
            // disturbance to the parser, so we just chain a specific-purpose adapter handler.
            super(next);

            this.documentName = documentName;
            this.lineOffset = 0;
            this.colOffset = 0;

        }




        /*
         * -----------------
         * Handler maintenance methods
         * -----------------
         */

        @Override
        public void setParseStatus(final ParseStatus status) {
            this.parseStatus = status;
        }


        @Override
        public void setParser(final IMarkupParser parser) {
            this.parser = parser;
        }

        @Override
        public void setHandlerChain(final IMarkupHandler handlerChain) {
            this.handlerChain = handlerChain;
        }




        /*
         * -----------------
         * Document handling
         * -----------------
         */

        @Override
        public void handleDocumentStart(
                final long startTimeNanos, final int line, final int col) throws ParseException {
            
            super.handleDocumentStart(startTimeNanos, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleDocumentEnd(
                final long endTimeNanos, final long totalTimeNanos,
                final int line, final int col)
                throws ParseException {

            super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line + this.lineOffset, col + this.colOffset);

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

            super.handleXmlDeclaration(
                    buffer,
                    keywordOffset, keywordLen, keywordLine + this.lineOffset, keywordCol + this.colOffset,
                    versionOffset, versionLen, versionLine + this.lineOffset, versionCol + this.colOffset,
                    encodingOffset, encodingLen, encodingLine + this.lineOffset, encodingCol + this.colOffset,
                    standaloneOffset, standaloneLen, standaloneLine + this.lineOffset, standaloneCol + this.colOffset,
                    outerOffset, outerLen, line + this.lineOffset, col + this.colOffset);

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

            super.handleDocType(
                    buffer,
                    keywordOffset, keywordLen, keywordLine + this.lineOffset, keywordCol + this.colOffset,
                    elementNameOffset, elementNameLen, elementNameLine + this.lineOffset, elementNameCol + this.colOffset,
                    typeOffset, typeLen, typeLine + this.lineOffset, typeCol + this.colOffset,
                    publicIdOffset, publicIdLen, publicIdLine + this.lineOffset, publicIdCol + this.colOffset,
                    systemIdOffset, systemIdLen, systemIdLine + this.lineOffset, systemIdCol + this.colOffset,
                    internalSubsetOffset, internalSubsetLen, internalSubsetLine + this.lineOffset, internalSubsetCol + this.colOffset,
                    outerOffset, outerLen, outerLine + this.lineOffset, outerCol + this.colOffset);

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

            super.handleCDATASection(
                    buffer, contentOffset, contentLen, outerOffset, outerLen,
                    line + this.lineOffset, col + this.colOffset);

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

                    super.handleText(
                            buffer,
                            offset + PARSER_LEVEL_COMMENT_CLOSE.length, len - PARSER_LEVEL_COMMENT_CLOSE.length,
                            line + this.lineOffset, col + PARSER_LEVEL_COMMENT_CLOSE.length + this.colOffset);

                }

                return; // No text left to handle

            }

            super.handleText(
                    buffer, offset, len, line + this.lineOffset, col + this.colOffset);

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

            super.handleComment(
                    buffer, contentOffset, contentLen, outerOffset, outerLen, line + this.lineOffset, col + this.lineOffset);

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

            super.handleAttribute(
                    buffer,
                    nameOffset, nameLen, nameLine + this.lineOffset, nameCol + this.colOffset,
                    operatorOffset, operatorLen, operatorLine + this.lineOffset, operatorCol + this.colOffset,
                    valueContentOffset, valueContentLen,
                    valueOuterOffset, valueOuterLen,
                    valueLine + this.lineOffset, valueCol + this.colOffset);

        }




        @Override
        public void handleStandaloneElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final boolean minimized,
                final int line, final int col)
                throws ParseException {

            super.handleStandaloneElementStart(
                    buffer, nameOffset, nameLen, minimized, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleOpenElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleOpenElementStart(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }


        @Override
        public void handleAutoOpenElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleAutoOpenElementStart(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }


        @Override
        public void handleAutoOpenElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleAutoOpenElementEnd(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }


        @Override
        public void handleCloseElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleCloseElementStart(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleAutoCloseElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleAutoCloseElementStart(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleUnmatchedCloseElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleUnmatchedCloseElementStart(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleStandaloneElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final boolean minimized,
                final int line, final int col)
                throws ParseException {

            super.handleStandaloneElementEnd(
                    buffer, nameOffset, nameLen, minimized, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleOpenElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleOpenElementEnd(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }


        @Override
        public void handleCloseElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleCloseElementEnd(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleAutoCloseElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleAutoCloseElementEnd(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleUnmatchedCloseElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col)
                throws ParseException {

            super.handleUnmatchedCloseElementEnd(
                    buffer, nameOffset, nameLen, line + this.lineOffset, col + this.colOffset);

        }



        @Override
        public void handleInnerWhiteSpace(
                final char[] buffer,
                final int offset, final int len,
                final int line, final int col)
                throws ParseException {

            super.handleInnerWhiteSpace(
                    buffer, offset, len, line + this.lineOffset, col + this.colOffset);

        }





        /*
         * -------------------------------
         * Processing Instruction handling
         * -------------------------------
         */


        @Override
        public void handleProcessingInstruction(
                final char[] buffer,
                final int targetOffset, final int targetLen,
                final int targetLine, final int targetCol,
                final int contentOffset, final int contentLen,
                final int contentLine, final int contentCol,
                final int outerOffset, final int outerLen,
                final int line, final int col)
                throws ParseException {

            super.handleProcessingInstruction(
                    buffer,
                    targetOffset, targetLen, targetLine + this.lineOffset, targetCol + this.colOffset,
                    contentOffset, contentLen, contentLine + this.lineOffset, contentCol + this.colOffset,
                    outerOffset, outerLen, line + this.lineOffset, col + this.colOffset);

        }


    }

    
    
    
}
