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
 *   Remainder (division remainder) complex expression (Thymeleaf Standard Expressions)
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
public final class RemainderExpression extends MultiplicationDivisionRemainderExpression {


    private static final long serialVersionUID = -8830009392616779821L;



    private static final Logger logger = LoggerFactory.getLogger(RemainderExpression.class);




    
    public RemainderExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }
    

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(REMAINDER_OPERATOR);
    }
    
    
    
    
    static Object executeRemainder(
            final IExpressionContext context,
            final RemainderExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating remainder expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object leftValue = expression.getLeft().execute(context, expContext);
        Object rightValue = expression.getRight().execute(context, expContext);

        if (leftValue == null) {
            leftValue = "null";
        }
        if (rightValue == null) {
            rightValue = "null";
        }

        final BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue);
        final BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue);
        if (leftNumberValue != null && rightNumberValue != null) {
            // Addition will act as a mathematical 'plus'
            return leftNumberValue.remainder(rightNumberValue);
        }
        
        throw new TemplateProcessingException(
            "Cannot execute division: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
        
    }
    
}
