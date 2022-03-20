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

import java.util.Locale;
import java.util.Set;

/**
 * <p>
 *   Interface implemented by objects containing the context variables needed by the template engine in
 *   order to process templates, besides other info like locale or (in web environments) Servlet-API artifacts.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IContext {

    /*
     * NOTE it is not a great idea to make IContext extend java.util.Map or its implementations extend from
     * HashMap. Such thing would give us the advantage to directly feed OGNL or SpringEL IContext instances
     * as expression roots, but at the expense of moving to these implementations of IContext the diverse checks
     * and internal operations needed to control aspects such as context security (e.g. no access to request
     * parameters from unescaped or pre-processing expressions).
     *
     * The problem of moving such controls to these context implementations is that these classes might
     * be user defined, and therefore such security controls bypassed by a careless implementation. Also, the
     * way these restrictions and controls has to be implemented is definitely expression-language-dependant,
     * so we are better off by using PropertyAccessors in OGNL and a Map wrapper in SpringEL.
     */

    /**
     * <p>
     *   Returns the locale that should be used for processing the template.
     * </p>
     *
     * @return the locale to be used.
     */
    public Locale getLocale();

    /**
     * <p>
     *   Checks whether a specific variable is already contained in this context or not.
     * </p>
     *
     * @param name the name of the variable to be checked.
     * @return {@code true} if the variable is already contained, {@code false} if not.
     */
    public boolean containsVariable(final String name);

    /**
     * <p>
     *   Get a list with all the names of variables contained at this context.
     * </p>
     *
     * @return the variable names.
     */
    public Set<String> getVariableNames();

    /**
     * <p>
     *   Retrieve a specific variable, by name.
     * </p>
     *
     * @param name the name of the variable to be retrieved.
     * @return the variable's value.
     */
    public Object getVariable(final String name);

}
