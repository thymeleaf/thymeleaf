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
package org.thymeleaf.standard.expression;

import org.thymeleaf.Arguments;
import org.thymeleaf.util.Validate;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class StandardExpressionExecutor {


    
    private final IStandardExpressionEvaluator expressionEvaluator;
    
    
    StandardExpressionExecutor(final IStandardExpressionEvaluator expressionEvaluator) {
        super();
        this.expressionEvaluator = expressionEvaluator;
    }
    
    

    
    
    public Object executeExpression(final Arguments arguments, final Expression expression) {

        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(expression, "Expression cannot be null");
        
        final Object result = 
            Expression.execute(arguments, expression, this.expressionEvaluator);
        return LiteralValue.unwrap(result); 
        
    }





    @Override
    public String toString() {
        return "Standard Expression Executor with expression evaluator: " + this.expressionEvaluator.toString();
    }
    
    
}
