/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;


/**
 * <p>
 *   Common interface for all Thymeleaf Standard Expression objects.
 * </p>
 * <p>
 *   Objects implementing this inteface are obtained by calling the parsing methods in
 *   parser objects (implementations of {@link IStandardExpressionParser}).
 * </p>
 * <p>
 *   Default implementation (used by most of the Thymeleaf core): {@link Expression}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public interface IStandardExpression {

    /**
     * <p>
     *   Obtain a string representation of the expression.
     * </p>
     *
     * @return the String representation
     */
    public String getStringRepresentation();

    /**
     * <p>
     *   Execute the expression.
     * </p>
     *
     * @param configuration the Configuration object for the template execution environment.
     * @param processingContext the processing context object containing the variables to be applied to the expression.
     * @return the result of executing the expression.
     */
    public Object execute(
            final Configuration configuration, final IProcessingContext processingContext);

    /**
     * <p>
     *   Execute the expression.
     * </p>
     *
     * @param configuration the Configuration object for the template execution environment.
     * @param processingContext the processing context object containing the variables to be applied to the expression.
     * @param expContext the expression execution context to be applied (preprocessing, etc.)
     * @return the result of executing the expression.
     */
    public Object execute(
            final Configuration configuration, final IProcessingContext processingContext,
            final StandardExpressionExecutionContext expContext);

}
