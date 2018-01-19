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
package org.thymeleaf.processor.text;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Basic abstract implementation of {@link ITextProcessor}.
 * </p>
 * <p>
 *   This abstract implementation takes care of correct exception handling so that subclasses don't have to.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractTextProcessor
        extends AbstractProcessor implements ITextProcessor {



    public AbstractTextProcessor(final TemplateMode templateMode, final int precedence) {
        super(templateMode, precedence);
    }


    public final void process(
            final ITemplateContext context,
            final IText text, final ITextStructureHandler structureHandler) {

        try {

            doProcess(context, text, structureHandler);

        } catch (final TemplateProcessingException e) {
            if (text.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(text.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(text.getLine(), text.getCol());
                }
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    text.getTemplateName(), text.getLine(), text.getCol(), e);
        }

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IText text, final ITextStructureHandler structureHandler);


}
