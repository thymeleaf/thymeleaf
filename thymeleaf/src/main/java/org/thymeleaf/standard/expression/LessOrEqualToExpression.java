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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;


/**
 * <p>
 *   Less-or-equal complex expression (Thymeleaf Standard Expressions)
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
public final class LessOrEqualToExpression extends GreaterLesserExpression {


    private static final long serialVersionUID = 7042174616566611488L;

    private static final Logger logger = LoggerFactory.getLogger(LessOrEqualToExpression.class);
    

    
    public LessOrEqualToExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(LESS_OR_EQUAL_TO_OPERATOR);
    }
    
    
    
        
    

    
    @SuppressWarnings("unchecked")
    static Object executeLessOrEqualTo(
            final IExpressionContext context,
            final LessOrEqualToExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating LESS OR EQUAL TO expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object leftValue = expression.getLeft().execute(context, expContext);
        Object rightValue = expression.getRight().execute(context, expContext);

        if (leftValue == null || rightValue == null) {
            throw new TemplateProcessingException(
                    "Cannot execute LESS OR EQUAL TO comparison: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
        }

        leftValue = LiteralValue.unwrap(leftValue);
        rightValue = LiteralValue.unwrap(rightValue);

        Boolean result = null;

        final BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue);
        final BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue);
        
        if (leftNumberValue != null && rightNumberValue != null) {
            result = Boolean.valueOf(leftNumberValue.compareTo(rightNumberValue) != 1);
        } else {
            if (leftValue != null && rightValue != null &&
                    leftValue.getClass().equals(rightValue.getClass()) && 
                    Comparable.class.isAssignableFrom(leftValue.getClass())) {
                result = Boolean.valueOf(((Comparable<Object>)leftValue).compareTo(rightValue) <= 0);
            } else {
                throw new TemplateProcessingException(
                        "Cannot execute LESS OR EQUAL TO from Expression \"" + 
                        expression.getStringRepresentation() + "\". Left is \"" + 
                        leftValue + "\", right is \"" + rightValue + "\"");
            }
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating LESS OR EQUAL TO expression: \"{}\". Left is \"{}\", right is \"{}\". Result is \"{}\"", 
                    new Object[] {TemplateEngine.threadIndex(), expression.getStringRepresentation(), leftValue, rightValue, result});
        }
        
        return result; 
        
    }

    
}
