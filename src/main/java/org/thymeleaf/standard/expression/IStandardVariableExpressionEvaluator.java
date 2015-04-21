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

import org.thymeleaf.aurora.context.IProcessingContext;

/**
 * <p>
 *   Common interface for all objects in charge of executing <i>variable expressions</i> (<tt>${...}</tt>) inside
 *   Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   The basic implementation of this interface evaluates expressions using OGNL
 *   {@link OgnlVariableExpressionEvaluator}, but a SpringEL version also exists in the Thymeleaf + Spring
 *   integration package.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9 (reimplemented in 3.0.0)
 *
 */
public interface IStandardVariableExpressionEvaluator {

    /**
     * <p>
     *   Evaluate the variable expression.
     * </p>
     *
     * @param processingContext the processing context object.
     * @param expression the expression to be evaluated (as a String).
     * @param expContext the expression execution context to be applied (preprocessing, etc.)
     * @param useSelectionAsRoot specify whether this is a <i>variable expression</i> (<tt>${...}</tt>, false) or a
     *                           <i>selection variable expression</i> (<tt>*{...}</tt>, true).
     * @return the result of evaluating the expression.
     */
    public Object evaluate(
            final IProcessingContext processingContext,
            final String expression, final StandardExpressionExecutionContext expContext, 
            final boolean useSelectionAsRoot);
    
}
