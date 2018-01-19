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
 *   Basic abstract implementation for the {@link ILazyContextVariable} interface.
 * </p>
 * <p>
 *   By extending this class instead of directly implementing the {@link ILazyContextVariable} interface,
 *   users can make sure that their variables will be initialized only once (per template execution). Once its
 *   inner abstract {@link #loadValue()} method is called --which implementation has to be provided by the user--,
 *   objects of this class will cache the results of such load and return these results every time the
 *   variable value is accessed.
 * </p>
 * <p>
 *   An example:
 * </p>
 * <pre><code>
 * context.setVariable(
 *     "users",
 *     new LazyContextVariable&lt;List&lt;User&gt;&gt;() {
 *         &#64;Override
 *         protected List&lt;User&gt; loadValue() {
 *             return databaseRepository.findAllUsers();
 *         }
 *     });
 * </code></pre>
 *
 * @param <T> the type of the value being returned by this variable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class LazyContextVariable<T> implements ILazyContextVariable<T> {


    private volatile boolean initialized = false;
    private T value;

    protected LazyContextVariable() {
        super();
    }


    /**
     * <p>
     *   Lazily resolve the value.
     * </p>
     * <p>
     *   This will be transparently called by the Thymeleaf engine at template rendering time when an object
     *   of this class is resolved in a Thymeleaf expression.
     * </p>
     * <p>
     *   Note lazy variables will be resolved just once, and their resolved values will be reused as many times
     *   as they appear in the template.
     * </p>
     *
     * @return the resolved value.
     */
    public final T getValue() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    this.value = loadValue();
                    this.initialized = true;
                }
            }
        }
        return this.value;
    }


    /**
     * <p>
     *   Perform the actual resolution of the variable's value.
     * </p>
     * <p>
     *   This method will be called only once, the first time this variable is resolved.
     * </p>
     *
     * @return the resolved value.
     */
    protected abstract T loadValue();

}
