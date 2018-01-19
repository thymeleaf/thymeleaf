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

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateAssertionException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.ExpressionSequence;
import org.thymeleaf.standard.expression.ExpressionSequenceUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardAssertionTagProcessor extends AbstractAttributeTagProcessor {



    protected AbstractStandardAssertionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        if (StringUtils.isEmptyOrWhitespace(attributeValue)) {
            return;
        }

        final ExpressionSequence expressionSequence =
                ExpressionSequenceUtils.parseExpressionSequence(context, attributeValue);

        final List<IStandardExpression> expressions = expressionSequence.getExpressions();

        for (final IStandardExpression expression : expressions) {
            final Object expressionResult = expression.execute(context);
            final boolean expressionBooleanResult = EvaluationUtils.evaluateAsBoolean(expressionResult);
            if (!expressionBooleanResult) {
                throw new TemplateAssertionException(
                        expression.getStringRepresentation(), tag.getTemplateName(),
                        tag.getAttribute(attributeName).getLine(), tag.getAttribute(attributeName).getCol());
            }
        }

    }


}
