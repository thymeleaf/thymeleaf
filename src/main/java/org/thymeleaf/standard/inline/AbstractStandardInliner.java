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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.ITemplateEvent;
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


    public final CharSequence inline(final ITemplateContext context, final CharSequence text) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(text, "Text cannot be null");


        /*
         * First, check whether the current template is being processed using the template mode we are applying
         * inlining for. If not, we must just process the entire text as a template in the desired template mode.
         */

        if (context.getTemplateMode() != this.templateMode) {

            final StringWriter stringWriter = new StringWriter();

            final TemplateManager templateManager = context.getConfiguration().getTemplateManager();
            templateManager.parseAndProcessString(
                    computeTemplateName(text), text.toString(),
                    computeLine(text), computeCol(text),
                    this.templateMode, context, stringWriter, true);

            return stringWriter.toString();

        }


        /*
         * Template modes match, so there is nothing we need to do (all output expressions will have been replaced
         * by th:block's with th:text/th:utext at parsing time!)
         */

        return text;

    }




    static String computeTemplateName(final CharSequence text) {
        if (text instanceof ITemplateEvent) {
            return ((ITemplateEvent)text).getTemplateName();
        }
        return text.toString();
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
