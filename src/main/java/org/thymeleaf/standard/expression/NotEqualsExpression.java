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
import org.thymeleaf.util.ObjectUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class NotEqualsExpression extends EqualsNotEqualsExpression {


    private static final long serialVersionUID = 5831688164085171802L;

    private static final Logger logger = LoggerFactory.getLogger(NotEqualsExpression.class);

    
    
    public NotEqualsExpression(final Expression left, final Expression right) {
        super(left, right);
    }

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(NOT_EQUALS_OPERATOR);
    }
    
    
    
    

    
    @SuppressWarnings({"unchecked","null"})
    static Object executeNotEquals(final Configuration configuration, final IProcessingContext processingContext, 
            final NotEqualsExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        Object leftValue = 
            Expression.execute(configuration, processingContext, expression.getLeft(), expressionEvaluator, expContext);

        Object rightValue = 
            Expression.execute(configuration, processingContext, expression.getRight(), expressionEvaluator, expContext);

        leftValue = LiteralValue.unwrap(leftValue);
        rightValue = LiteralValue.unwrap(rightValue);

        if (leftValue == null) {
            return Boolean.valueOf(rightValue != null);
        }
        
        Boolean result = null; 
        
        final BigDecimal leftNumberValue = ObjectUtils.evaluateAsNumber(leftValue); 
        final BigDecimal rightNumberValue = ObjectUtils.evaluateAsNumber(rightValue);
        
        if (leftNumberValue != null && rightNumberValue != null) {
            result = Boolean.valueOf(leftNumberValue.compareTo(rightNumberValue) != 0);
        } else {
            if (leftValue instanceof Character) {
                leftValue = leftValue.toString();
            }
            if (rightValue != null && rightValue instanceof Character) {
                rightValue = rightValue.toString();
            }
            if (rightValue != null &&
                    leftValue.getClass().equals(rightValue.getClass()) && 
                    Comparable.class.isAssignableFrom(leftValue.getClass())) {
                result = Boolean.valueOf(((Comparable<Object>)leftValue).compareTo(rightValue) != 0);
            } else {
                result = Boolean.valueOf(!leftValue.equals(rightValue));
            }
        }
            
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating NOT EQUALS expression: \"{}\". Left is \"{}\", right is \"{}\". Result is \"{}\"", 
                    new Object[] {TemplateEngine.threadIndex(), expression.getStringRepresentation(), leftValue, rightValue, result});
        }
        
        return result; 
        
    }

    
}
