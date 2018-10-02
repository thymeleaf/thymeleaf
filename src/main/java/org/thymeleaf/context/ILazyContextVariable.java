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

/**
 * <p>
 *   Interface to be implemented by context variables that need to be loaded lazily.
 * </p>
 * <p>
 *   Variables implementing this interface have the opportunity to not be completely resolved at context build
 *   time, but instead wait until the variable is actually used in an expression executed from the template. This can
 *   importantly reduce the amount of memory required to store context variables per template in scenarios when
 *   many variables are made available to the templates in a generic manner (e.g. in frameworks that allow
 *   users to create their own templates), but templates might afterwards choose not to use some or many of these
 *   variables.
 * </p>
 * <p>
 *   Note this <em>lazy resolution</em> can only be performed when the lazy variable is added to the context as a
 *   <strong>first-level</strong> variable. i.e. {@code ${lazy}} will work, but {@code ${container.lazy}}
 *   will not.
 * </p>
 * <p>
 *   The {@link LazyContextVariable} abstract class contains a sensible implementation of this interface, best
 *   suited to be used for extension than the bare interface, in most cases.
 * </p>
 *
 * @param <T> the type of the value being returned by this variable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface ILazyContextVariable<T> {

    /**
     * <p>
     *   Returns the variable value. This method is meant to be internally called, and will normally perform
     *   some kind of initialization for the variable before returning (e.g. database calls, complex computations...)
     * </p>
     *
     * @return the variable value
     */
    public T getValue();

}
