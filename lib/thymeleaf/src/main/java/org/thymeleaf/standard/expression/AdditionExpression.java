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
import org.thymeleaf.util.EvaluationUtils;


/**
 * <p>
 *   Addition complex expression (Thymeleaf Standard Expressions)
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
public final class AdditionExpression extends AdditionSubtractionExpression {
    
    private static final long serialVersionUID = -971366486450425605L;


    private static final Logger logger = LoggerFactory.getLogger(AdditionExpression.class);


    
    public AdditionExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }
    

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(ADDITION_OPERATOR);
    }
    
    
    
    
    
    static Object executeAddition(
            final IExpressionContext context,
            final AdditionExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating addition expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final IStandardVariableExpressionEvaluator expressionEvaluator =
                StandardExpressions.getVariableExpressionEvaluator(context.getConfiguration());

        final IStandardExpression leftExpr = expression.getLeft();
        final IStandardExpression rightExpr = expression.getRight();

        // We try use the Expression.execute methods directly if possible, because we cannot allow the results
        // to be literal-unwrapped (if literal unwrap takes place, '.' + 3 + 2 = 2.3 instead of '.32' as it should be).
        // Note this is only needed in AdditionExpression, because '+' is the only overloaded operator
        Object leftValue;
        if (leftExpr instanceof Expression) {
            // This avoids literal-unwrap
            leftValue = Expression.execute(context, (Expression)leftExpr, expressionEvaluator, expContext);
        } else{
            leftValue = leftExpr.execute(context, expContext);
        }
        Object rightValue;
        if (rightExpr instanceof Expression) {
            // This avoids literal-unwrap
            rightValue = Expression.execute(context, (Expression)rightExpr, expressionEvaluator, expContext);
        } else{
            rightValue = rightExpr.execute(context, expContext);
        }

        if (leftValue == null) {
            leftValue = "null";
        }
        if (rightValue == null) {
            rightValue = "null";
        }

        final BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue);
        if (leftNumberValue != null) {
            final BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue);
            if (rightNumberValue != null) {
                // Addition will act as a mathematical 'plus'
                return leftNumberValue.add(rightNumberValue);
            }
        }

        return new LiteralValue(LiteralValue.unwrap(leftValue).toString() + (LiteralValue.unwrap(rightValue).toString()));

    }

    
}
