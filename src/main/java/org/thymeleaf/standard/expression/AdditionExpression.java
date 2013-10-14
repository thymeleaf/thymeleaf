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
import org.thymeleaf.util.EvaluationUtil;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
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
    
    
    
    
    
    static Object executeAddition(final Configuration configuration, final IProcessingContext processingContext, 
            final AdditionExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating addition expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final IStandardVariableExpressionEvaluator expressionEvaluator =
                StandardExpressions.getVariableExpressionEvaluator(configuration);

        final IStandardExpression leftExpr = expression.getLeft();
        final IStandardExpression rightExpr = expression.getRight();

        // We try use the Expression.execute methods directly if possible, because we cannot allow the results
        // to be literal-unwrapped (if literal unwrap takes place, '.' + 3 + 2 = 2.3 instead of '.32' as it should be).
        // Note this is only needed in AdditionExpression, because '+' is the only overloaded operator
        Object leftValue;
        if (leftExpr instanceof Expression) {
            // This avoids literal-unwrap
            leftValue =
                    Expression.execute(
                            configuration, processingContext, (Expression)leftExpr, expressionEvaluator, expContext);
        } else{
            leftValue = leftExpr.execute(configuration, processingContext, expContext);
        }
        Object rightValue;
        if (rightExpr instanceof Expression) {
            // This avoids literal-unwrap
            rightValue =
                    Expression.execute(
                            configuration, processingContext, (Expression)rightExpr, expressionEvaluator, expContext);
        } else{
            rightValue = rightExpr.execute(configuration, processingContext, expContext);
        }

        if (leftValue == null) {
            leftValue = "null";
        }
        if (rightValue == null) {
            rightValue = "null";
        }

        final BigDecimal leftNumberValue = EvaluationUtil.evaluateAsNumber(leftValue);
        final BigDecimal rightNumberValue = EvaluationUtil.evaluateAsNumber(rightValue);
        if (leftNumberValue != null && rightNumberValue != null) {
            // Addition will act as a mathematical 'plus'
            return leftNumberValue.add(rightNumberValue);
        }

        return new LiteralValue(LiteralValue.unwrap(leftValue).toString() + (LiteralValue.unwrap(rightValue).toString()));

    }

    
}
