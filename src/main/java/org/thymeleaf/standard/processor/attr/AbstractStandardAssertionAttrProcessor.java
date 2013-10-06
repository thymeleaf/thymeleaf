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
import org.thymeleaf.standard.expression.ExpressionSequence;
import org.thymeleaf.standard.expression.ExpressionSequenceUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardConversionUtil;
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

        final ExpressionSequence expressionSequence =
                ExpressionSequenceUtils.parseExpressionSequence(configuration, arguments, attributeValue);

        final List<IStandardExpression> expressions = expressionSequence.getExpressions();

        for (final IStandardExpression expression : expressions) {
            final Object expressionResult = expression.execute(arguments.getConfiguration(), arguments);
            final boolean expressionBooleanResult = StandardConversionUtil.convertIfNeeded(configuration, arguments, expressionResult, boolean.class);
            if (!expressionBooleanResult) {
                throw new TemplateAssertionException(expression.getStringRepresentation(),
                        arguments.getTemplateName(), element.getLineNumber());
            }
        }

    }

}
