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
 * NO-OP (No Operation) token (Thymeleaf Standard Expressions)
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class NoOpTokenExpression extends Token {

    private static final Logger logger = LoggerFactory.getLogger(NoOpTokenExpression.class);

    private static final long serialVersionUID = -5180150929940011L;

    private static final NoOpTokenExpression SINGLETON = new NoOpTokenExpression();


    public NoOpTokenExpression() {
        super(null);
    }


    @Override
    public String getStringRepresentation() {
        return "_";
    }


    static NoOpTokenExpression parseNoOpTokenExpression(final String input) {
        if (input.length() == 1 && input.charAt(0) == '_') {
            return SINGLETON;
        }
        return null;
    }


    static Object executeNoOpTokenExpression(
            final IExpressionContext context,
            final NoOpTokenExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating no-op token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        return NoOpToken.VALUE;

    }

}
