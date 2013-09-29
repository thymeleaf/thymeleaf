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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.StringUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardLocalVariableDefinitionAttrProcessor 
        extends AbstractLocalVariableDefinitionAttrProcessor {





    protected AbstractStandardLocalVariableDefinitionAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardLocalVariableDefinitionAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    

    

    @Override
    protected final Map<String, Object> getNewLocalVariables(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final AssignationSequence assignations =
                expressionParser.parseAssignationSequence(
                        configuration, arguments, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        Arguments assignationExecutionArguments = arguments;

        final Map<String,Object> newLocalVariables = new HashMap<String,Object>(assignations.size() + 1, 1.0f);
        for (final Assignation assignation : assignations) {
            
            final Expression leftExpr = assignation.getLeft();
            final Object leftValue =
                    expressionExecutor.executeExpression(configuration, assignationExecutionArguments, leftExpr);

            final Expression rightExpr = assignation.getRight();
            final Object rightValue =
                    expressionExecutor.executeExpression(configuration, assignationExecutionArguments, rightExpr);

            final String newVariableName = (leftValue == null? null : leftValue.toString());
            if (StringUtils.isEmptyOrWhitespace(newVariableName)) {
                throw new TemplateProcessingException(
                        "Variable name expression evaluated as null or empty: \"" + leftExpr + "\"");
            }

            // Creating a new Arguments object allows the reuse of variables in, for example, th:with expressions.
            assignationExecutionArguments =
                    assignationExecutionArguments.addLocalVariables(Collections.singletonMap(newVariableName, rightValue));

            newLocalVariables.put(newVariableName, rightValue);
            
        }
        
        return newLocalVariables;
        
    }




    
}
