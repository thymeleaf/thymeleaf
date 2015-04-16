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
package org.thymeleaf.aurora.standard.processor;

import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.engine.AttributeName;
import org.thymeleaf.aurora.engine.IElementTagActionHandler;
import org.thymeleaf.aurora.engine.IterationStatusVar;
import org.thymeleaf.aurora.model.IProcessableElementTag;
import org.thymeleaf.aurora.processor.element.AbstractAttributeMatchingHTMLElementProcessor;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardTextProcessor extends AbstractAttributeMatchingHTMLElementProcessor {


    public StandardTextProcessor() {
        super("text", 1300);
    }



    public void process(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final IElementTagActionHandler actionHandler) {

        // We know this will not be null, because we linked the processor to a specific attribute
        final AttributeName attributeName = getMatchingAttributeName().getMatchingAttributeName();

        final Object localIterValue = processingContext.getVariablesMap().getVariable("iter");
        if (localIterValue != null) {

            final IterationStatusVar stat = (IterationStatusVar) processingContext.getVariablesMap().getVariable("iterStat");
            actionHandler.setBody(localIterValue.toString() + " [" + stat.getCount() + (stat.getSize() != null? (" of " + stat.getSize()) : "") + "]", false);

        } else {

            if (processingContext.getVariablesMap().hasSelectionTarget()) {

                final Object selectionTarget = processingContext.getVariablesMap().getSelectionTarget();

                actionHandler.setBody(selectionTarget.toString(), false);

            } else {

                final Object localVarValue = processingContext.getVariablesMap().getVariable("one");

                if (localVarValue != null) {
                    actionHandler.setBody("*Whoohooooo!*", false);
                } else {
                    actionHandler.setBody("Whoohooooo!", false);
                }

            }

        }

        tag.getAttributes().removeAttribute(attributeName);

    }


}
