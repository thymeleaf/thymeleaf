/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class NumberLiteralExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(NumberLiteralExpression.class);

    private static final long serialVersionUID = -3729844055243242571L;


    static final char DECIMAL_POINT = '.';
    
    
    private final BigDecimal value;

    
    NumberLiteralExpression(final String value) {
        super();
        Validate.notNull(value, "Value cannot be null");
        this.value = new BigDecimal(value);
    }
    
    
    
    public BigDecimal getValue() {
        return this.value;
    }

    
    @Override
    public String getStringRepresentation() {
        return this.value.toPlainString();
    }


    
    static NumberLiteralExpression parseNumberLiteral(final String input) {
        return new NumberLiteralExpression(input);
    }
    

    
    static Object executeNumberLiteral(
            @SuppressWarnings("unused") final Arguments arguments, @SuppressWarnings("unused") final TemplateResolution templateResolution, 
            final NumberLiteralExpression expression) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating number literal: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }
    
}
