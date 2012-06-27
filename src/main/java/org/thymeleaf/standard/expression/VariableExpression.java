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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class VariableExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(VariableExpression.class);
    

    private static final long serialVersionUID = -4911752782987240708L;
    

    static final char SELECTOR = '$';
    
    
    private static final Pattern VAR_PATTERN = 
        Pattern.compile("^\\s*\\$\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    
    static final Expression NULL_VALUE = VariableExpression.parseVariable("${null}");


    
    
    private final String expression;
    
    
    
    public VariableExpression(final String expression) {
        super();
        Validate.notNull(expression, "Expression cannot be null");
        this.expression = expression;
    }
    
    
    public String getExpression() {
        return this.expression;
    }
    
    
    @Override
    public String getStringRepresentation() {
        return String.valueOf(SELECTOR) + 
               String.valueOf(SimpleExpression.EXPRESSION_START_CHAR) + 
               this.expression + 
               String.valueOf(SimpleExpression.EXPRESSION_END_CHAR);
    }
    
    
    
    static VariableExpression parseVariable(final String input) {
        final Matcher matcher = VAR_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        return new VariableExpression(matcher.group(1));
    }
    

    
    
    
    
    
    static Object executeVariable(
            final Arguments arguments, final VariableExpression expression, 
            final IStandardExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating variable expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final String exp = expression.getExpression();
        if (exp == null) {
            throw new TemplateProcessingException(
                    "Variable expression is null, which is not allowed");
        }
        
        final Object evaluationRoot = arguments.getExpressionEvaluationContext().getExpressionEvaluationRoot();
        
        return expressionEvaluator.evaluate(arguments, exp, evaluationRoot);
        
    }
    
    
    
}
