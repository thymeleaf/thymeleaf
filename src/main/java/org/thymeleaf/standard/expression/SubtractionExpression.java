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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.ObjectUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 *
 */
public final class SubtractionExpression extends AdditionSubtractionExpression {

    
    
    private static final long serialVersionUID = 4125686854902098944L;



    private static final Logger logger = LoggerFactory.getLogger(SubtractionExpression.class);


    
    public SubtractionExpression(final Expression left, final Expression right) {
        super(left, right);
    }
    

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(SUBTRACTION_OPERATOR);
    }
    
    
    
    
    static Object executeSubtraction(final Configuration configuration, final IProcessingContext processingContext, 
            final SubtractionExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating subtraction expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object leftValue = 
            Expression.execute(configuration, processingContext, expression.getLeft(), expressionEvaluator, expContext);

        Object rightValue = 
            Expression.execute(configuration, processingContext, expression.getRight(), expressionEvaluator, expContext);
        
        if (leftValue == null) {
            leftValue = "null";
        }
        if (rightValue == null) {
            rightValue = "null";
        }
        
        final BigDecimal leftNumberValue = ObjectUtils.evaluateAsNumber(leftValue);
        final BigDecimal rightNumberValue = ObjectUtils.evaluateAsNumber(rightValue);
        if (leftNumberValue != null && rightNumberValue != null) {
            // Addition will act as a mathematical 'plus'
            return leftNumberValue.subtract(rightNumberValue);
        }
        
        throw new TemplateProcessingException(
            "Cannot execute subtraction: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
        
    }

    
}
