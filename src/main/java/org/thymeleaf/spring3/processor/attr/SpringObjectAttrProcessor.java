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
package org.thymeleaf.spring3.processor.attr;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.attr.AbstractStandardSelectionAttrProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringObjectAttrProcessor 
        extends AbstractStandardSelectionAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 500;
    public static final String ATTR_NAME = "object";
    
    
    
    public SpringObjectAttrProcessor() {
        super(ATTR_NAME);
    }


    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    
    

    @Override
    protected void validateSelectionValue(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final IStandardExpression expression) {

        if (expression == null || !(expression instanceof VariableExpression)) {

            throw new TemplateProcessingException(
                    "The expression used for object selection is " + expression + ", which is not valid: " +
                    "only variable expressions (${...}) are allowed in '" +  attributeName + "' attributes in " +
                    "Spring-enabled environments.");

        }


    }

    
    
    

    @Override
    protected Map<String, Object> getAdditionalLocalVariables(
            final Arguments arguments, final Element element, final String attributeName) {

        final Map<String, Object> previousAdditionalLocalVariables =
                super.getAdditionalLocalVariables(arguments, element, attributeName);

        final Map<String,Object> additionalLocalVariables =
                new HashMap<String, Object>(previousAdditionalLocalVariables.size() + 3);
        additionalLocalVariables.putAll(previousAdditionalLocalVariables);

        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final VariableExpression varExpression =
            (VariableExpression) expressionParser.parseExpression(arguments.getConfiguration(), arguments, attributeValue);

        additionalLocalVariables.put(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION, varExpression);
        // Added also with the deprecated name, for backwards compatibility
        additionalLocalVariables.put(SpringContextVariableNames.SPRING_FORM_COMMAND_VALUE, varExpression);

        return additionalLocalVariables;
        
    }


    

}
