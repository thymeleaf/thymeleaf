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
package org.thymeleaf.processor.document;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.IDocumentStructureHandler;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IDocumentEnd;
import org.thymeleaf.model.IDocumentStart;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractDocumentProcessor
        extends AbstractProcessor implements IDocumentProcessor {



    public AbstractDocumentProcessor(final TemplateMode templateMode, final int precedence) {
        super(templateMode, precedence);
    }




    public final void processDocumentStart(
            final ITemplateProcessingContext processingContext,
            final IDocumentStart documentStart,
            final IDocumentStructureHandler structureHandler) {

        try {

            doProcessDocumentStart(processingContext, documentStart, structureHandler);

        } catch (final TemplateProcessingException e) {
            if (!e.hasTemplateName()) {
                e.setTemplateName(documentStart.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(documentStart.getLine(), documentStart.getCol());
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    documentStart.getTemplateName(), documentStart.getLine(), documentStart.getCol(), e);
        }

    }


    public final void processDocumentEnd(
            final ITemplateProcessingContext processingContext,
            final IDocumentEnd documentEnd,
            final IDocumentStructureHandler structureHandler) {

        try {

            doProcessDocumentEnd(
                    processingContext, documentEnd, structureHandler);

        } catch (final TemplateProcessingException e) {
            if (!e.hasTemplateName()) {
                e.setTemplateName(documentEnd.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(documentEnd.getLine(), documentEnd.getCol());
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    documentEnd.getTemplateName(), documentEnd.getLine(), documentEnd.getCol(), e);
        }

    }





    public abstract void doProcessDocumentStart(
            final ITemplateProcessingContext processingContext,
            final IDocumentStart documentStart,
            final IDocumentStructureHandler structureHandler);


    public abstract void doProcessDocumentEnd(
            final ITemplateProcessingContext processingContext,
            final IDocumentEnd documentEnd,
            final IDocumentStructureHandler structureHandler);




}
