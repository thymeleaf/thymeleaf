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
package org.thymeleaf.standard.inline;

import java.io.Writer;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.LazyProcessingCharSequence;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractStandardInliner implements IInliner {

    private final TemplateMode templateMode;
    private final boolean writeTextsToOutput;



    protected AbstractStandardInliner(
            final IEngineConfiguration configuration, final TemplateMode templateMode) {

        super();

        Validate.notNull(configuration, "Engine configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");

        this.templateMode = templateMode;

        /*
         * The 'writeTextsToOutput' flag will mean that the inliner can directly use the output Writer when
         * processing inlined IText's, instead of creating a separate StringWriter object (and therefore a
         * String containing the whole result of processing the inlined text). This should result in a
         * performance optimization when inlining is very used, but can only be done if the following
         * happens:
         *
         *   - There are no post-processors that might want to do things on the processed text result.
         *   - There are no other ITextProcessor instances declared other than the
         *     corresponding StandardInlinerTextProcessor.
         *
         * In that case, the inliner will return a LazyProcessingCharSequence object, which will perform the
         * direct writer output. But the conditions above are needed to ensure that the context is not going to
         * be modified from the moment this inliner executes to the moment the output is written.
         *
         * Note: we are checking for the size of textprocessors but not checking if that one (at most) processor is
         *       actually the inline processor. And that fine because, if it isn't, then nobody will be applying
         *       inlining to text nodes in the first place, and this inliner will never be executed.
         */

        final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessors = configuration.getTextProcessors(this.templateMode);
        this.writeTextsToOutput = postProcessors.isEmpty() && textProcessors.size() <= 1;

    }



    public final String getName() {
        return this.getClass().getSimpleName();
    }




    public final CharSequence inline(final ITemplateContext context, final IText text) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(text, "Text cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, text);
        }

        /*
         * Template modes match, first we check if we actually need to apply inlining at all, and if we do, we just
         * execute the inlining mechanisms.
         */
        if (!EngineEventUtils.isInlineable(text)) {
            return null;
        }

        /*
         * In this case we don't have other option than to use a string builder and build the inlined string. We
         * cannot build an IModel that replaces the original text because that would alter the structure of the
         * Text/CDATA/Comment events being processed and the other text/CDATA/Comment processors executing afterwards
         * would see several events where they should only see one (text-event fragmentation is not a problem for
         * pre-processors or post-processors, but it is for processors that cannot just "append the next event").
         *
         * And we know we have other processors because, precisely, if it weren't so we would not have reached here
         * and inlined expressions would have been transformed to th:text/th:utext at parsing time.
         *
         * (Also, in the case of CDATAs and Comments, event fragmentation is not possible because this events have
         * prefixes and suffixes which cannot be replicated inside themselves).
         */
        final int textLen = text.length();
        final StringBuilder strBuilder = new StringBuilder(textLen + (textLen / 2));

        performInlining(context, text, 0, textLen, text.getTemplateName(), text.getLine(), text.getCol(), strBuilder);

        return strBuilder.toString();

    }


    private CharSequence inlineSwitchTemplateMode(final ITemplateContext context, final IText text) {

        final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

        final TemplateModel templateModel =
                templateManager.parseString(
                        context.getTemplateData(), text.getText(),
                        text.getLine(), text.getCol(),
                        this.templateMode, true);

        if (!this.writeTextsToOutput) {
            final Writer stringWriter = new FastStringWriter(50);
            templateManager.process(templateModel, context, stringWriter);
            return stringWriter.toString();
        }

        // If we can directly write to output (and text is an IText), we will use a LazyProcessingCharSequence
        return new LazyProcessingCharSequence(context, templateModel);

    }





    public final CharSequence inline(final ITemplateContext context, final ICDATASection cdataSection) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(cdataSection, "CDATA Section cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, cdataSection);
        }

        /*
         * Template modes match, first we check if we actually need to apply inlining at all, and if we do, we just
         * execute the inlining mechanisms.
         */
        if (!EngineEventUtils.isInlineable(cdataSection)) {
            return null;
        }

        /*
         * In this case we don't have other option than to use a string builder and build the inlined string. We
         * cannot build an IModel that replaces the original text because that would alter the structure of the
         * Text/CDATA/Comment events being processed and the other text/CDATA/Comment processors executing afterwards
         * would see several events where they should only see one (text-event fragmentation is not a problem for
         * pre-processors or post-processors, but it is for processors that cannot just "append the next event").
         *
         * And we know we have other processors because, precisely, if it weren't so we would not have reached here
         * and inlined expressions would have been transformed to th:text/th:utext at parsing time.
         *
         * (Also, in the case of CDATAs and Comments, event fragmentation is not possible because this events have
         * prefixes and suffixes which cannot be replicated inside themselves).
         */
        final int cdataSectionLen = cdataSection.length();
        final StringBuilder strBuilder = new StringBuilder(cdataSectionLen + (cdataSectionLen / 2));

        performInlining(context, cdataSection, 9, cdataSectionLen - 12, cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol(), strBuilder);

        return strBuilder.toString();

    }


    private CharSequence inlineSwitchTemplateMode(final ITemplateContext context, final ICDATASection cdataSection) {

        final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

        /*
         * Notice we are ONLY processing the contents of the CDATA, because we know the target inlining
         * mode will not understand the CDATA (it will be textual) and we don't want it to mess around with
         * the CDATA's prefix and suffix.
         *
         * Note this will only be executed in markup modes (textual modes never fire "handleCDATASection" events),
         * so we are safe assuming the sizes of CDATA prefixes and suffixes in HTML/XML.
         */

        final TemplateModel templateModel =
                templateManager.parseString(
                        context.getTemplateData(), cdataSection.getContent(),
                        cdataSection.getLine(), cdataSection.getCol() + 9, // +9 because of the prefix
                        this.templateMode, true);

        final Writer stringWriter = new FastStringWriter(50);
        templateManager.process(templateModel, context, stringWriter);

        return stringWriter.toString();

    }




    public final CharSequence inline(final ITemplateContext context, final IComment comment) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(comment, "Comment cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, comment);
        }

        /*
         * Template modes match, first we check if we actually need to apply inlining at all, and if we do, we just
         * execute the inlining mechanisms.
         */
        if (!EngineEventUtils.isInlineable(comment)) {
            return null;
        }

        /*
         * In this case we don't have other option than to use a string builder and build the inlined string. We
         * cannot build an IModel that replaces the original text because that would alter the structure of the
         * Text/CDATA/Comment events being processed and the other text/CDATA/Comment processors executing afterwards
         * would see several events where they should only see one (text-event fragmentation is not a problem for
         * pre-processors or post-processors, but it is for processors that cannot just "append the next event").
         *
         * And we know we have other processors because, precisely, if it weren't so we would not have reached here
         * and inlined expressions would have been transformed to th:text/th:utext at parsing time.
         *
         * (Also, in the case of CDATAs and Comments, event fragmentation is not possible because this events have
         * prefixes and suffixes which cannot be replicated inside themselves).
         */
        final int commentLen = comment.length();
        final StringBuilder strBuilder = new StringBuilder(commentLen + (commentLen / 2));

        performInlining(context, comment, 4, commentLen - 7, comment.getTemplateName(), comment.getLine(), comment.getCol(), strBuilder);

        return strBuilder.toString();

    }


    private CharSequence inlineSwitchTemplateMode(final ITemplateContext context, final IComment comment) {

        final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

        /*
         * Notice we are ONLY processing the contents of the Comment, because we know the target inlining
         * mode will not understand the Comment (it will be textual) and we don't want it to mess around with
         * the Comment's prefix and suffix.
         *
         * Note this will only be executed in markup modes (textual modes never fire "handleComment" events),
         * so we are safe assuming the sizes of Comment prefixes and suffixes in HTML/XML.
         */

        final TemplateModel templateModel =
                templateManager.parseString(
                        context.getTemplateData(), comment.getContent(),
                        comment.getLine(), comment.getCol() + 4, // +4 because of the prefix
                        this.templateMode, true);

        final Writer stringWriter = new FastStringWriter(50);
        templateManager.process(templateModel, context, stringWriter);

        return stringWriter.toString();

    }











    private void performInlining(
            final ITemplateContext context,
            final CharSequence text,
            final int offset, final int len,
            final String templateName,
            final int line, final int col,
            final StringBuilder strBuilder) {

        final IStandardExpressionParser expressionParser =
                StandardExpressions.getExpressionParser(context.getConfiguration());

        final int[] locator = new int[] { line, col };

        int i = offset;
        int current = i;
        int maxi = offset + len;

        int expStart, expEnd;
        int currentLine = -1;
        int currentCol = -1;
        char innerClosingChar = 0x0;

        boolean inExpression = false;

        while (i < maxi) {

            currentLine = locator[0];
            currentCol = locator[1];

            if (!inExpression) {

                expStart = findNextStructureStart(text, i, maxi, locator);

                if (expStart == -1) {
                    strBuilder.append(text, current, maxi);
                    return;
                }

                inExpression = true;

                if (expStart > current) {
                    // We avoid empty-string text events
                    strBuilder.append(text, current, expStart);
                }

                innerClosingChar = ((text.charAt(expStart + 1) == '[' )? ']' : ')');
                current = expStart;
                i = current + 2;

            } else {

                // The inner closing char we will be looking for will depend on the type of expression we just found

                expEnd = findNextStructureEndAvoidQuotes(text, i, maxi, innerClosingChar, locator);

                if (expEnd < 0) {
                    strBuilder.append(text, current, maxi);
                    return;
                }

                final String expression = text.subSequence(current + 2, expEnd).toString();
                final boolean escape = innerClosingChar == ']';
                strBuilder.append(
                        processExpression(context, expressionParser, expression, escape, templateName, currentLine, currentCol + 2));

                // The ')]' or ']]' suffix will be considered as processed too
                countChar(locator, text.charAt(expEnd));
                countChar(locator, text.charAt(expEnd + 1));

                inExpression = false;

                current = expEnd + 2;
                i = current;


            }

        }

        if (inExpression) {// Just in case input ended in '[[' or '[('
            strBuilder.append(text, current, maxi);
        }

    }







    private static void countChar(final int[] locator, final char c) {
        if (c == '\n') {
            locator[0]++;
            locator[1] = 1;
            return;
        }
        locator[1]++;
    }


    private static int findNextStructureStart(
            final CharSequence text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text.charAt(i);

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '[' && n > 0) {
                c = text.charAt(i + 1);
                if (c == '[' || c == '(') { // We've probably found either a [[...]] or a [(...)] (at least its start)
                    locator[1] += (i - colIndex);
                    return i;
                }
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }




    private static int findNextStructureEndAvoidQuotes(
            final CharSequence text, final int offset, final int maxi,
            final char innerClosingChar, final int[] locator) {

        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text.charAt(i);

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '"' && !inApos) {
                inQuotes = !inQuotes;
            } else if (c == '\'' && !inQuotes) {
                inApos = !inApos;
            } else if (c == innerClosingChar && !inQuotes && !inApos && n > 0) {
                c = text.charAt(i + 1);
                if (c == ']') {
                    locator[1] += (i - colIndex);
                    return i;
                }
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }




    private String processExpression(
            final ITemplateContext context,
            final IStandardExpressionParser expressionParser,
            final String expression,
            final boolean escape,
            final String templateName,
            final int line, final int col) {

        try {

            final String unescapedExpression =
                    EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), expression);

            final Object expressionResult;
            if (unescapedExpression != null) {
                final IStandardExpression expressionObj = expressionParser.parseExpression(context, unescapedExpression);
                expressionResult = expressionObj.execute(context);
            } else {
                expressionResult = null;
            }

            if (escape) {
                return produceEscapedOutput(expressionResult);
            } else {
                return (expressionResult == null? "": expressionResult.toString());
            }

        } catch (final TemplateProcessingException e) {
            // We will add location info
            if (!e.hasTemplateName()) {
                e.setTemplateName(templateName);
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(line, col);
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of inlined expression '" + expression + "'",
                    templateName, line, col, e);
        }

    }


    protected abstract String produceEscapedOutput(final Object input);

}
