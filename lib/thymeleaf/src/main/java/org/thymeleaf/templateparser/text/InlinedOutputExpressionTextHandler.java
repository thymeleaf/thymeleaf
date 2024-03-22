/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.text;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.inline.IInlinePreProcessorHandler;
import org.thymeleaf.standard.inline.OutputExpressionInlinePreProcessorHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * This class converts inlined output expressions into their equivalent element events, which makes it possible
 * to cache parsed inlined expressions.
 *
 * Some examples:
 *
 *     [[${someVar}]]            ->     [# th:text="${someVar}"/]          (decomposed into the corresponding events)
 *     [(${someVar})]            ->     [# th:utext="${someVar}"/]         (decomposed into the corresponding events)
 *
 * NOTE: The inlining mechanism is a part of the Standard Dialects, so the conversion performed by this handler
 *       on inlined output expressions should only be applied if one of the Standard Dialects has been configured.
 *
 * ---------------------------------------------------------------------------------------------------------------------
 * NOTE: Any changes here should probably go too to org.thymeleaf.templateparser.markup.InlinedOutputExpressionMarkupHandler
 * ---------------------------------------------------------------------------------------------------------------------
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class InlinedOutputExpressionTextHandler extends AbstractChainedTextHandler {


    private final OutputExpressionInlinePreProcessorHandler inlineHandler;


    InlinedOutputExpressionTextHandler(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final String standardDialectPrefix, final ITextHandler handler) {

        super(handler);

        this.inlineHandler =
                new OutputExpressionInlinePreProcessorHandler(
                        configuration,
                        templateMode, standardDialectPrefix,
                        new InlineTextAdapterPreProcessorHandler(handler));

    }






    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleText(buffer, offset, len, line, col);
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }


    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }


    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.inlineHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }




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
            throws TextParseException {
        this.inlineHandler.handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }






    private static final class InlineTextAdapterPreProcessorHandler implements IInlinePreProcessorHandler {

        private ITextHandler handler;


        InlineTextAdapterPreProcessorHandler(final ITextHandler handler) {
            super();
            this.handler = handler;
        }


        public void handleText(
                final char[] buffer,
                final int offset, final int len,
                final int line, final int col) {
            try {
                this.handler.handleText(buffer, offset, len, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleStandaloneElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final boolean minimized,
                final int line, final int col) {
            try {
                this.handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleStandaloneElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final boolean minimized,
                final int line, final int col) {
            try {
                this.handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleOpenElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            try {
                this.handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleOpenElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            try {
                this.handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleAutoOpenElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-open not allowed in text mode");
        }

        public void handleAutoOpenElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-open not allowed in text mode");
        }

        public void handleCloseElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            try {
                this.handler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleCloseElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            try {
                this.handler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        public void handleAutoCloseElementStart(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-close not allowed in text mode");
        }

        public void handleAutoCloseElementEnd(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int line, final int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-close not allowed in text mode");
        }

        public void handleAttribute(
                final char[] buffer,
                final int nameOffset, final int nameLen,
                final int nameLine, final int nameCol,
                final int operatorOffset, final int operatorLen,
                final int operatorLine, final int operatorCol,
                final int valueContentOffset, final int valueContentLen,
                final int valueOuterOffset, final int valueOuterLen,
                final int valueLine, final int valueCol) {
            try {
                this.handler.handleAttribute(
                        buffer,
                        nameOffset, nameLen, nameLine, nameCol,
                        operatorOffset, operatorLen, operatorLine, operatorCol,
                        valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
            } catch (final TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }
    }

}