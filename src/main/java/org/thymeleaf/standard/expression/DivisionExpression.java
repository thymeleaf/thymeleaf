/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtil;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class DivisionExpression extends MultiplicationDivisionRemainderExpression {


    private static final long serialVersionUID = -6480768503994179971L;



    private static final Logger logger = LoggerFactory.getLogger(DivisionExpression.class);




    
    public DivisionExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }
    

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(DIVISION_OPERATOR);
    }
    
    
    
    
    static Object executeDivision(final Configuration configuration, final IProcessingContext processingContext, 
            final DivisionExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating division expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object leftValue = expression.getLeft().execute(configuration, processingContext, expContext);

        Object rightValue = expression.getRight().execute(configuration, processingContext, expContext);
        
        if (leftValue == null) {
            leftValue = "null";
        }
        if (rightValue == null) {
            rightValue = "null";
        }

        final BigDecimal leftNumberValue = EvaluationUtil.evaluateAsNumber(leftValue);
        final BigDecimal rightNumberValue = EvaluationUtil.evaluateAsNumber(rightValue);
        if (leftNumberValue != null && rightNumberValue != null) {
            try {
                return leftNumberValue.divide(rightNumberValue);
            } catch (final ArithmeticException ignored) {
                // Result has a non-terminating decimal expansion (like 100/3), so
                // we just use a minimum arbitrary scale (10) and HALF_UP rounding mode
                return leftNumberValue.divide(
                        rightNumberValue, 
                        Math.max(Math.max(leftNumberValue.scale(),rightNumberValue.scale()), 10), 
                        RoundingMode.HALF_UP);
            }
        }
        
        throw new TemplateProcessingException(
            "Cannot execute division: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
        
    }

    
}
