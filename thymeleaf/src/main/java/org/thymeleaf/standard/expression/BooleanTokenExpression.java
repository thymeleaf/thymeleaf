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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;


/**
 * <p>
 *   Boolean token (Thymeleaf Standard Expressions)
 * </p>
 * <p>
 *   Note a class with this name existed since 2.1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class BooleanTokenExpression extends Token {

    private static final Logger logger = LoggerFactory.getLogger(BooleanTokenExpression.class);

    private static final long serialVersionUID = 7003426193298054476L;


    public BooleanTokenExpression(final String value) {
        super(Boolean.valueOf(value));
    }

    public BooleanTokenExpression(final Boolean value) {
        super(value);
    }


    
    static BooleanTokenExpression parseBooleanTokenExpression(final String input) {
        if ("true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input)) {
            return new BooleanTokenExpression(input);
        }
        return null;
    }
    

    
    static Object executeBooleanTokenExpression(
            final IExpressionContext context,
            final BooleanTokenExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating boolean token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }
    
}
