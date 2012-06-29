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
import org.thymeleaf.Configuration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluationContext;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
@SuppressWarnings("deprecation")
class DeprecatedBridgeStandardExpressionEvaluator implements IStandardVariableExpressionEvaluator {
    /*
     * WILL BE REMOVED WHEN IStandardExpressionEvaluator IS REMOVED 
     */

    private final IStandardExpressionEvaluator evaluator;
    
    
    public DeprecatedBridgeStandardExpressionEvaluator(final IStandardExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }


    public Object evaluate(final Configuration configuration, final ExpressionEvaluationContext evalContext,
            final String expression, final boolean useSelectionAsRoot) {
        
        if (!(evalContext instanceof Arguments)) {
            throw new TemplateProcessingException(
                    "Cannot evaluate expression \"" + expression + "\". A non-standard expression evaluator " +
                    "is being used of class \"" + this.evaluator.getClass().getName() + "\", which implements the old deprecated " +
            		"\"" + IStandardExpressionEvaluator.class.getName() +"\" interface instead of the new " +
    				"\"" + IStandardVariableExpressionEvaluator.class.getName() +"\" one, which forbids the evaluation of expressions " +
            		"outside templates");
        }
        
        final Object evaluationRoot = 
                (useSelectionAsRoot?
                        evalContext.getExpressionSelectionEvaluationRoot() :
                        evalContext.getExpressionEvaluationRoot());
        
        return this.evaluator.evaluate((Arguments)evalContext, expression, evaluationRoot);
        
    }
    
    
    
}
