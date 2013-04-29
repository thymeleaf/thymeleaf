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
package org.thymeleaf.testing.templateengine.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.util.Validate;


public final class OgnlTestContextExpression implements ITestContextExpression {
    
    private final String expression;
    
    public OgnlTestContextExpression(final String expression) {
        super();
        Validate.notNull(expression, "Expression cannot be null or empty");
        this.expression = (expression.trim().equals("")? "''" : expression);
    }
    
    
    public Object evaluate(final Map<String,Object> context, final Locale locale) {

        final Map<String,Object> contextVariables = new HashMap<String, Object>();
        final Map<String,Object> expressionUtilityObjects =
                ExpressionEvaluatorObjects.getExpressionEvaluationUtilityObjectsForLocale(locale);
        if (expressionUtilityObjects != null) {
            contextVariables.putAll(expressionUtilityObjects);
        }
        
        try {
            
            final Object varExpression = Ognl.parseExpression(this.expression);
            return Ognl.getValue(varExpression, contextVariables, context);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + this.expression + "\"", e);
        }
        
    }
    
}
