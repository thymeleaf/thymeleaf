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

import org.thymeleaf.context.IExpressionContext;

/**
 * <p>
 *   Common interface for all objects in charge of parsing Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   Default implementation (used by most parts of the Thymeleaf core): {@link StandardExpressionParser}.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
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
public interface IStandardExpressionParser {

    /**
     * <p>
     *   Parse the specified expression.
     * </p>
     *
     * @param context the context object.
     * @param input the expression to be parsed, as an input String.
     * @return the expression object resulting from parsing the expression.
     */
    public IStandardExpression parseExpression(
            final IExpressionContext context, final String input);

}
