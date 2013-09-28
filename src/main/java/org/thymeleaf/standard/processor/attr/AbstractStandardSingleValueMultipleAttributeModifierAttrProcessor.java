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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {





    protected AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    




    @Override
    protected final Map<String, String> getModifiedAttributeValues(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final Expression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
        
        final Set<String> newAttributeNames = 
                getModifiedAttributeNames(arguments, element, attributeName, attributeValue, expression);

        final Object valueForAttributes = expressionExecutor.executeExpression(configuration, arguments, expression);
        
        final Map<String,String> result = new HashMap<String,String>(newAttributeNames.size() + 1, 1.0f);
        for (final String newAttributeName : newAttributeNames) {
            result.put(newAttributeName, (valueForAttributes == null? "" : valueForAttributes.toString()));
        }
        
        return result;
        
    }


    protected abstract Set<String> getModifiedAttributeNames(final Arguments arguments,
            final Element element, final String attributeName, final String attributeValue, final Expression expression);





    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        return false;
    }


    
}
