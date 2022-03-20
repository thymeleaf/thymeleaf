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
package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Basic abstract implementation of {@link ITemplateBoundariesProcessor}.
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
public abstract class AbstractTemplateBoundariesProcessor
        extends AbstractProcessor implements ITemplateBoundariesProcessor {



    public AbstractTemplateBoundariesProcessor(final TemplateMode templateMode, final int precedence) {
        super(templateMode, precedence);
    }




    public final void processTemplateStart(
            final ITemplateContext context,
            final ITemplateStart templateStart,
            final ITemplateBoundariesStructureHandler structureHandler) {

        try {

            doProcessTemplateStart(context, templateStart, structureHandler);

        } catch (final TemplateProcessingException e) {
            if (templateStart.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(templateStart.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(templateStart.getLine(), templateStart.getCol());
                }
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    templateStart.getTemplateName(), templateStart.getLine(), templateStart.getCol(), e);
        }

    }


    public final void processTemplateEnd(
            final ITemplateContext context,
            final ITemplateEnd templateEnd,
            final ITemplateBoundariesStructureHandler structureHandler) {

        try {

            doProcessTemplateEnd(context, templateEnd, structureHandler);

        } catch (final TemplateProcessingException e) {
            if (templateEnd.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(templateEnd.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(templateEnd.getLine(), templateEnd.getCol());
                }
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    templateEnd.getTemplateName(), templateEnd.getLine(), templateEnd.getCol(), e);
        }

    }





    public abstract void doProcessTemplateStart(
            final ITemplateContext context,
            final ITemplateStart templateStart,
            final ITemplateBoundariesStructureHandler structureHandler);


    public abstract void doProcessTemplateEnd(
            final ITemplateContext context,
            final ITemplateEnd templateEnd,
            final ITemplateBoundariesStructureHandler structureHandler);




}
