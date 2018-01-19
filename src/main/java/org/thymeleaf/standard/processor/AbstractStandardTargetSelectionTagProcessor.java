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
package org.thymeleaf.standard.processor;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardTargetSelectionTagProcessor extends AbstractAttributeTagProcessor {


    protected AbstractStandardTargetSelectionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final IStandardExpression expression =
                expressionParser.parseExpression(context, attributeValue);

        validateSelectionValue(context, tag, attributeName, attributeValue, expression);

        final Object newSelectionTarget = expression.execute(context);

        final Map<String,Object> additionalLocalVariables =
                computeAdditionalLocalVariables(context, tag, attributeName, attributeValue, expression);
        if (additionalLocalVariables != null && additionalLocalVariables.size() > 0) {
            for (final Map.Entry<String,Object> variableEntry : additionalLocalVariables.entrySet()) {
                structureHandler.setLocalVariable(variableEntry.getKey(), variableEntry.getValue());
            }
        }

        structureHandler.setSelectionTarget(newSelectionTarget);

    }





    protected void validateSelectionValue(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IStandardExpression expression) {
        // Meant for being overridden. Nothing to be done in default implementation.
    }


    protected Map<String,Object> computeAdditionalLocalVariables(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IStandardExpression expression) {
        // This method is meant to be overriden. By default, no local variables
        // will be set.
        return null;
    }


}
