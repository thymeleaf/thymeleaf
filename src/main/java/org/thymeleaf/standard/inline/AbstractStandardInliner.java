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
package org.thymeleaf.standard.inline;

import java.io.Writer;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.templatemode.TemplateMode;
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



    protected AbstractStandardInliner(final IEngineConfiguration configuration, final TemplateMode templateMode) {

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




    public final void inline(final ITemplateContext context, final IText text) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(text, "Text cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */

        if (context.getTemplateMode() != this.templateMode) {

            final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

            final TemplateModel templateModel =
                    templateManager.parseString(
                            context.getTemplateData(), text.getText(),
                            text.getLine(), text.getCol(),
                            this.templateMode, true);

            if (!this.writeTextsToOutput) {
                final Writer stringWriter = new FastStringWriter(50);
                templateManager.process(templateModel, context, stringWriter);
                text.setText(stringWriter.toString());
                return;
            }

            // If we can directly write to output (and text is an IText), we will use a LazyProcessingCharSequence
            text.setText(new LazyProcessingCharSequence(context, templateModel));
            return;

        }

        /*
         * Template modes match, first we check if we actually need to apply inline at all, and if we do, we just
         * execute the inlining mechanisms.
         */

        if (!EngineEventUtils.isInlineable(text)) {
            return;
        }

        text.setText("{{INLINED}}");

    }




    public final void inline(final ITemplateContext context, final ICDATASection cdataSection) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(cdataSection, "CDATA Section cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */

        if (context.getTemplateMode() != this.templateMode) {

            final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

            final TemplateModel templateModel =
                    templateManager.parseString(
                            context.getTemplateData(), cdataSection.getCDATASection(),
                            cdataSection.getLine(), cdataSection.getCol(),
                            this.templateMode, true);

            final Writer stringWriter = new FastStringWriter(50);
            templateManager.process(templateModel, context, stringWriter);

            final String resultCDATASection = stringWriter.toString();
            final String resultCDATASectionContent = resultCDATASection.substring(9, resultCDATASection.length() - 3);

            cdataSection.setContent(resultCDATASectionContent);
            return;

        }

        /*
         * Template modes match, first we check if we actually need to apply inline at all, and if we do, we just
         * execute the inlining mechanisms.
         */

        if (!EngineEventUtils.isInlineable(cdataSection)) {
            return;
        }

        cdataSection.setContent("{{INLINED}}");

    }




    public final void inline(final ITemplateContext context, final IComment comment) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(comment, "Comment cannot be null");

        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */

        if (context.getTemplateMode() != this.templateMode) {

            final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

            final TemplateModel templateModel =
                    templateManager.parseString(
                            context.getTemplateData(), comment.getComment(),
                            comment.getLine(), comment.getCol(),
                            this.templateMode, true);

            final Writer stringWriter = new FastStringWriter(50);
            templateManager.process(templateModel, context, stringWriter);

            final String resultComment = stringWriter.toString();
            final String resultCommentContent = resultComment.substring(4, resultComment.length() - 3);

            comment.setContent(resultCommentContent);
            return;

        }

        /*
         * Template modes match, first we check if we actually need to apply inline at all, and if we do, we just
         * execute the inlining mechanisms.
         */

        if (!EngineEventUtils.isInlineable(comment)) {
            return;
        }

        comment.setContent("{{INLINED}}");

    }


}
