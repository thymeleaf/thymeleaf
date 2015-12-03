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
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.inline.IInliner;
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
    private final boolean writeToOutput;



    protected AbstractStandardInliner(final IEngineConfiguration configuration, final TemplateMode templateMode) {

        super();

        Validate.notNull(configuration, "Engine configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");

        this.templateMode = templateMode;

        /*
         * The 'writeToOutput' flag will mean that the inliner can directly use the output Writer when processing
         * inlined text, instead of creating a separate StringWriter object (and therefore a String containing the
         * whole result of processing the inlined text). This should result in a performance optimization when
         * inlining is very used, but can only be done if the following happens:
         *
         *   - There are no post-processors that might want to do things on the processed text result.
         *   - There are no other ITextProcessor instances declared other than the InlinerTextProcessor.
         *
         * In that case, the inliner will return a LazyProcessingCharSequence object, which will perform the
         * direct writer output. But the conditions above are needed to ensure that the context is not going to
         * be modified from the moment this inliner executes to the moment the output is written.
         *
         * Note: we are checking for the size of textprocessors but not checking if that one (at most) processor is
         *       actually the InlineTextProcessor. And that fine because, if it isn't, then nobody will be applying
         *       inlining to text nodes in the first place, and this inliner will never be executed.
         */

        final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessors = configuration.getTextProcessors(this.templateMode);
        this.writeToOutput = postProcessors.isEmpty() && textProcessors.size() <= 1;

    }



    public final String getName() {
        return this.getClass().getSimpleName();
    }


    public final CharSequence inline(final ITemplateContext context, final CharSequence text) {

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
                            context.getTemplateData(), text.toString(),
                            computeLine(text), computeCol(text),
                            this.templateMode, true);

            if (!this.writeToOutput || !(text instanceof IText)) {

                final Writer stringWriter = new FastStringWriter(50);
                templateManager.process(templateModel, context, stringWriter);
                return stringWriter.toString();

            }

            // If we can directly write to output (and text is an IText), we will use a LazyProcessingCharSequence
            return new LazyProcessingCharSequence(context, templateModel);

        }


        /*
         * Template modes match, so there is nothing we need to do (all output expressions will have been replaced
         * by th:block's with th:text/th:utext at parsing time!)
         */

        return text;

    }




    static int computeLine(final CharSequence text) {
        if (text instanceof ITemplateEvent) {
            return ((ITemplateEvent)text).getLine();
        }
        return Integer.MIN_VALUE; // Negative (line,col) will mean 'no locator'
    }


    static int computeCol(final CharSequence text) {
        if (text instanceof ITemplateEvent) {
            return ((ITemplateEvent)text).getCol();
        }
        return Integer.MIN_VALUE; // Negative (line,col) will mean 'no locator'
    }


}
