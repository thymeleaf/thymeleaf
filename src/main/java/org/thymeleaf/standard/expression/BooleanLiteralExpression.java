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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class BooleanLiteralExpression extends SimpleExpression {

    private static final Logger logger = LoggerFactory.getLogger(BooleanLiteralExpression.class);

    private static final long serialVersionUID = 7003426193298054476L;


    private final Boolean value;


    public BooleanLiteralExpression(final String value) {
        super();
        Validate.notNull(value, "Value cannot be null");
        this.value = Boolean.valueOf(value);
    }
    
    
    
    public Boolean getValue() {
        return this.value;
    }

    
    @Override
    public String getStringRepresentation() {
        return this.value.toString();
    }


    
    static BooleanLiteralExpression parseBooleanLiteral(final String input) {
        if ("true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input)) {
            return new BooleanLiteralExpression(input);
        }
        return null;
    }
    

    
    static Object executeBooleanLiteral(
            @SuppressWarnings("unused") final IProcessingContext processingContext, 
            final BooleanLiteralExpression expression,
            @SuppressWarnings("unused") final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating boolean literal: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }
    
}
