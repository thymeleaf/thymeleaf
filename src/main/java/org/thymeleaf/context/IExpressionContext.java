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
package org.thymeleaf.context;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.expression.IExpressionObjects;

/**
 * <p>
 *   Interface implemented by all classes containing the context required for expression processing.
 * </p>
 * <p>
 *   This interface extends {@link IContext} by adding the required information needed to execute
 *   expressions.
 * </p>
 * <p>
 *   Note that implementations of this interface do not have to be thread-safe, and in fact should not be
 *   shared by different threads or template executions. They are meant to be local to a specific template
 *   engine execution.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public interface IExpressionContext extends IContext {

    /**
     * <p>
     *   Returns the {@link IEngineConfiguration} (engine configuration) corresponding to the
     *   {@link org.thymeleaf.ITemplateEngine} instance this expression context is meant to be used with.
     * </p>
     *
     * @return the engine configuration.
     */
    public IEngineConfiguration getConfiguration();

    /**
     * <p>
     *   Returns the {@link IExpressionObjects} instance to be used for retrieving (and maybe building
     *   lazily) expression objects ({@code ${#expobj}}) to be used at Standard Thymeleaf Expressions.
     * </p>
     * @return the expression objects instance.
     */
    public IExpressionObjects getExpressionObjects();

}
