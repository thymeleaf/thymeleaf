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

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.engine.AttributeName;
import org.thymeleaf.aurora.engine.IElementStructureHandler;
import org.thymeleaf.aurora.engine.IterationStatusVar;
import org.thymeleaf.aurora.model.IProcessableElementTag;
import org.thymeleaf.aurora.processor.element.AbstractAttributeMatchingHTMLElementTagProcessor;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardTextTagProcessor extends AbstractAttributeMatchingHTMLElementTagProcessor {


    public StandardTextTagProcessor() {
        super("text", 1300);
    }



    public void process(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final IElementStructureHandler structureHandler) {


        final AttributeName attributeName = getMatchingAttributeName().getMatchingAttributeName();
        final String attributeValue = tag.getAttributes().getValue(attributeName);

        final IEngineConfiguration configuration = processingContext.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
//
//        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
//
//        final Object result = expression.execute(configuration, arguments);
//
//        return (result == null? "" : result.toString());




        final Object localIterValue = processingContext.getVariablesMap().getVariable("iter");
        if (localIterValue != null) {

            final IterationStatusVar stat = (IterationStatusVar) processingContext.getVariablesMap().getVariable("iterStat");
            structureHandler.setBody(localIterValue.toString() + " [" + stat.getCount() + (stat.getSize() != null? (" of " + stat.getSize()) : "") + "]", false);

        } else {

            final boolean inlining = processingContext.getVariablesMap().isTextInliningActive();

            if (processingContext.getVariablesMap().hasSelectionTarget()) {

                final Object selectionTarget = processingContext.getVariablesMap().getSelectionTarget();

                structureHandler.setBody((inlining? "" : "$") + selectionTarget.toString(), false);

            } else {


                final Object localVarValue = processingContext.getVariablesMap().getVariable("one");

                if (localVarValue != null) {
                    structureHandler.setBody((inlining? "" : "$") + "*Whoohooooo!*", false);
                } else {
                    structureHandler.setBody((inlining? "" : "$") + "Whoohooooo!", false);
                }

            }

        }

        tag.getAttributes().removeAttribute(attributeName);

    }


}
