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
package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;



/**
 * <p>
 *   Base abstract class for simple expressions (Thymeleaf Standard Expressions)
 * </p>
 * <p>
 *   Note a class with this name existed since 1.1, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public abstract class SimpleExpression extends Expression {
    
    private static final long serialVersionUID = 9145380484247069725L;
    
    
    static final char EXPRESSION_START_CHAR = '{';
    static final char EXPRESSION_END_CHAR = '}';
    
    
    
    protected SimpleExpression() {
        super();
    }
    
    


    
    
    
    static Object executeSimple(
            final IExpressionContext context, final SimpleExpression expression,
            final IStandardVariableExpressionEvaluator expressionEvaluator, final StandardExpressionExecutionContext expContext) {
        
        if (expression instanceof VariableExpression) {
            return VariableExpression.executeVariableExpression(context, (VariableExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof MessageExpression) {
            return MessageExpression.executeMessageExpression(context, (MessageExpression)expression, expContext);
        }
        if (expression instanceof TextLiteralExpression) {
            return TextLiteralExpression.executeTextLiteralExpression(context, (TextLiteralExpression)expression, expContext);
        }
        if (expression instanceof NumberTokenExpression) {
            return NumberTokenExpression.executeNumberTokenExpression(context, (NumberTokenExpression) expression, expContext);
        }
        if (expression instanceof BooleanTokenExpression) {
            return BooleanTokenExpression.executeBooleanTokenExpression(context, (BooleanTokenExpression) expression, expContext);
        }
        if (expression instanceof NullTokenExpression) {
            return NullTokenExpression.executeNullTokenExpression(context, (NullTokenExpression) expression, expContext);
        }
        if (expression instanceof LinkExpression) {
            // No expContext to be specified: link expressions always execute in RESTRICTED mode for the URL base and NORMAL for URL parameters
            return LinkExpression.executeLinkExpression(context, (LinkExpression)expression);
        }
        if (expression instanceof FragmentExpression) {
            // No expContext to be specified: fragment expressions always execute in RESTRICTED mode
            return FragmentExpression.executeFragmentExpression(context, (FragmentExpression)expression);
        }
        if (expression instanceof SelectionVariableExpression) {
            return SelectionVariableExpression.executeSelectionVariableExpression(context, (SelectionVariableExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof NoOpTokenExpression) {
            return NoOpTokenExpression.executeNoOpTokenExpression(context, (NoOpTokenExpression) expression, expContext);
        }
        if (expression instanceof GenericTokenExpression) {
            return GenericTokenExpression.executeGenericTokenExpression(context, (GenericTokenExpression) expression, expContext);
        }

        throw new TemplateProcessingException("Unrecognized simple expression: " + expression.getClass().getName());
        
    }
    
}
