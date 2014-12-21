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
package org.thymeleaf.standard.expression;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 * @deprecated The StandardExpressionExecutor class has been deprecated in 2.1.0. Instead the "execute()" methods
 *             in Expression objects returned by parsers should be used directly.
 *
 */
@Deprecated
public final class StandardExpressionExecutor {


    
    private final IStandardVariableExpressionEvaluator expressionEvaluator;


    @Deprecated
    public StandardExpressionExecutor(final IStandardVariableExpressionEvaluator expressionEvaluator) {
        super();
        this.expressionEvaluator = expressionEvaluator;
    }





    @Deprecated
    public Object executeExpression(final Arguments arguments, final Expression expression) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(expression, "Expression cannot be null");
        return expression.execute(arguments.getConfiguration(), arguments, StandardExpressionExecutionContext.NORMAL);
    }

    
    @Deprecated
    public Object executeExpression(final Arguments arguments, final Expression expression, final StandardExpressionExecutionContext expContext) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(expression, "Expression cannot be null");
        return expression.execute(arguments.getConfiguration(), arguments, expContext);
    }

    
    
    @Deprecated
    public Object executeExpression(final Configuration configuration, final IProcessingContext processingContext,
            final Expression expression) {
        Validate.notNull(expression, "Expression cannot be null");
        return expression.execute(configuration, processingContext, StandardExpressionExecutionContext.NORMAL);
    }

    
    @Deprecated
    public Object executeExpression(final Configuration configuration, final IProcessingContext processingContext,
            final Expression expression, final StandardExpressionExecutionContext expContext) {
        Validate.notNull(expression, "Expression cannot be null");
        return expression.execute(configuration, processingContext, expContext);
    }




    @Override
    public String toString() {
        return "Standard Expression Executor with expression evaluator: " + this.expressionEvaluator.toString();
    }
    
    
}
