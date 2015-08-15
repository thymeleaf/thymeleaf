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

import java.io.StringWriter;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IText;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractStandardInliner implements IInliner {

    private final TemplateMode templateMode;

    protected AbstractStandardInliner(final TemplateMode templateMode) {
        super();
        this.templateMode = templateMode;
    }


    public final String getName() {
        return this.getClass().getSimpleName();
    }


    public final CharSequence inline(final ITemplateProcessingContext context, final CharSequence text) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(text, "Text cannot be null");


        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */

        if (context.getTemplateMode() != this.templateMode) {

            if (text instanceof IText) {

                final IText itext = (IText) text;

                /*
                 * If all the text to be inlined is whitespace (we know this from the moment it was parsed), then just return,
                 * because there is no way we can do anything with just whitespace.
                 */
                if (itext.isWhitespace()) {
                    return text;
                }

            }

            final StringWriter stringWriter = new StringWriter();

            final TemplateManager templateManager = context.getTemplateManager();
            templateManager.processTemplate(
                    context.getConfiguration(), text.toString(), null, true, this.templateMode,
                    context.getVariables(), stringWriter, true);

            return stringWriter.toString();

        }


        /*
         * Template modes match, so we can perform expression inlining in the text
         */

        if (text instanceof IText) {

            final IText itext = (IText) text;

            /*
             * If all the text to be inlined is whitespace (we know this from the moment it was parsed), then just return,
             * because there is no way we can do anything with just whitespace.
             */
            if (itext.isWhitespace()) {
                return text;
            }

            /*
             * We will quickly check whether inlining seems needed at all, so that we don't spend more time on this if
             * not required.
             * Note the org.thymeleaf.engine.Text implementation of IText already precomputes the answers to these
             * 'contains' questions at parsing time so that they are answered without needing to traverse the
             * entire texts.
             */
            if (!itext.contains(StandardInlineUtils.INLINE_SYNTAX_MARKER_ESCAPED) && !itext.contains(StandardInlineUtils.INLINE_SYNTAX_MARKER_UNESCAPED)) {
                return text;
            }

        } else {

            /*
             * This is not an IText, but anyway we will perform a quick test to check whether we might need inlining
             */
            if (!StandardInlineUtils.mightNeedInlining(text)) {
                return text;
            }

        }


        /*
         * Once we are quite sure that we will need to execute some inlined expressions, let's do it!
         */
        return StandardInlineUtils.performInlining(context.getConfiguration().getTextRepository(), text);

    }


}
