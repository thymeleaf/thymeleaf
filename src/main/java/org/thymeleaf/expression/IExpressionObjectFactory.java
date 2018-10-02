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
package org.thymeleaf.expression;

import java.util.Set;

import org.thymeleaf.context.IExpressionContext;


/**
 * <p>
 *   Factory objects for creating {@link IExpressionObjects} instances. These factories are the artifacts
 *   specified by {@link org.thymeleaf.dialect.IExpressionObjectDialect} implementations, instead of specifying
 *   the expression objects themselves, so that these expression objects are only created when really needed
 *   in template expressions.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public interface IExpressionObjectFactory {

    /**
     * <p>
     *   Return the complete list of expression objects that can be created by this factory.
     * </p>
     * <p>
     *   This list will be used for determining if a factory might actually be asked to build an object, so
     *   it should contain all possible objects to be built by the factory.
     * </p>
     *
     * @return the list of objects this factory can build.
     */
    public Set<String> getAllExpressionObjectNames();

    /**
     * <p>
     *   Build the requested object.
     * </p>
     *
     * @param context the context being used for processing the template.
     * @param expressionObjectName the name of the expression object to be built.
     * @return the built object, or {@code null} if the object could not be built.
     */
    public Object buildObject(final IExpressionContext context, final String expressionObjectName);

    /**
     * <p>
     *   Returns whether a specific expression object can be cached and reused for all expressions in the
     *   same template execution or not.
     * </p>
     * <p>
     *   Note this <em>cacheable</em> flag refers only to reuse of the object in expressions in expressions
     *   executed during a single template execution.
     * </p>
     * @param expressionObjectName the name of the expression object.
     * @return {@code true} is the object is to be considered cacheable, {@code false} if not.
     */
    public boolean isCacheable(final String expressionObjectName);

}
