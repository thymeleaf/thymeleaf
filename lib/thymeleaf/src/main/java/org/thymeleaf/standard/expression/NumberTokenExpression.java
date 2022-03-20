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
import org.thymeleaf.util.StringUtils;



/**
 * <p>
 *   Number token (Thymeleaf Standard Expressions)
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
public final class NumberTokenExpression extends Token {
    
    private static final Logger logger = LoggerFactory.getLogger(NumberTokenExpression.class);

    private static final long serialVersionUID = -3729844055243242571L;


    public static final char DECIMAL_POINT = '.';
    


    static Number computeValue(final String value) {
        final BigDecimal bigDecimalValue = new BigDecimal(value);
        if (bigDecimalValue.scale() > 0) {
            return bigDecimalValue;
        }
        return bigDecimalValue.toBigInteger();
    }

    
    public NumberTokenExpression(final String value) {
        super(computeValue(value));
    }
    

    
    @Override
    public String getStringRepresentation() {
        final Object value = getValue();
        if (value instanceof BigDecimal) {
            return ((BigDecimal)getValue()).toPlainString();
        }
        return value.toString();
    }


    
    static NumberTokenExpression parseNumberTokenExpression(final String input) {
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        boolean decimalFound = false;
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            } else if (c == DECIMAL_POINT) {
                if (decimalFound) {
                    return null;
                }
                decimalFound = true;
                continue;
            } else {
                return null;
            }
        }
        try {
            return new NumberTokenExpression(input);
        } catch (final NumberFormatException e) {
            // It seems after all it wasn't valid as a number
            return null;
        }
    }
    

    
    static Object executeNumberTokenExpression(
            final IExpressionContext context,
            final NumberTokenExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating number token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }
    
}
