/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor.attr;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateAssertionException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractAssertionAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.ExpressionSequence;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public abstract class AbstractStandardAssertionAttrProcessor
        extends AbstractAssertionAttrProcessor {




    protected AbstractStandardAssertionAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardAssertionAttrProcessor(final String attributeName) {
        super(attributeName);
    }



    @Override
    protected final void checkAssertions(final Arguments arguments,
            final Element element, final String attributeName, final String attributeValue) {

        if (StringUtils.isEmptyOrWhitespace(attributeValue)) {
            return;
        }

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final ExpressionSequence expressionSequence =
                expressionParser.parseExpressionSequence(arguments.getConfiguration(), arguments, attributeValue);

        final List<Expression> expressions = expressionSequence.getExpressions();

        for (final Expression expression : expressions) {
            final Object expressionResult =
                    expressionExecutor.executeExpression(arguments.getConfiguration(), arguments, expression);
            final boolean expressionBooleanResult = ObjectUtils.evaluateAsBoolean(expressionResult);
            if (!expressionBooleanResult) {
                throw new TemplateAssertionException(expression.getStringRepresentation(),
                        arguments.getTemplateName(), element.getLineNumber());
            }
        }

    }

}
