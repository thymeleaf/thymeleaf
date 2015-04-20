/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.aurora.context.IProcessingContext;
import org.thymeleaf.util.StringUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1 (reimplemented in 3.0.0)
 *
 */
public final class NumberTokenExpression extends Token {
    
    private static final Logger logger = LoggerFactory.getLogger(NumberTokenExpression.class);

    private static final long serialVersionUID = -3729844055243242571L;


    public static final char DECIMAL_POINT = '.';
    

    
    public NumberTokenExpression(final String value) {
        super(new BigDecimal(value));
    }
    

    
    @Override
    public String getStringRepresentation() {
        return ((BigDecimal)getValue()).toPlainString();
    }


    
    static NumberTokenExpression parseNumberToken(final String input) {
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
    

    
    static Object executeNumberToken(
            @SuppressWarnings("unused") final IProcessingContext processingContext,
            final NumberTokenExpression expression,
            @SuppressWarnings("unused") final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating number token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }
    
}
