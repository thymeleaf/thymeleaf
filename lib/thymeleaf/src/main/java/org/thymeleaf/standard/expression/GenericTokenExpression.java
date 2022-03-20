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
 *   Generic token (Thymeleaf Standard Expressions)
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
public final class GenericTokenExpression extends Token {

    private static final Logger logger = LoggerFactory.getLogger(GenericTokenExpression.class);

    private static final long serialVersionUID = 7913229642187691263L;


    GenericTokenExpression(final String value) {
        super(value);
    }


    
    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
    
    
    public static GenericTokenExpression parseGenericTokenExpression(final String input) {
        if (input == null) {
            return null;
        }
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            if (!isTokenChar(input, i)) {
                return null;
            }
        }
        return new GenericTokenExpression(input);
    }



    static Object executeGenericTokenExpression(
            final IExpressionContext context,
            final GenericTokenExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating generic token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        return expression.getValue();

    }


}
