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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Abstract base class for most {@link IContext} implementations.
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
public abstract class AbstractContext implements IContext {


    private final Map<String,Object> variables;
    private Locale locale;


    protected AbstractContext() {
        this(null, null);
    }


    protected AbstractContext(final Locale locale) {
        this(locale, null);
    }


    protected AbstractContext(final Locale locale, final Map<String, Object> variables) {
        super();
        this.locale =
                (locale == null? Locale.getDefault() : locale);
        this.variables =
                (variables == null?
                        new LinkedHashMap<String, Object>(10) :
                        new LinkedHashMap<String, Object>(variables));
    }


    public final Locale getLocale() {
        return this.locale;
    }

    public final boolean containsVariable(final String name) {
        return this.variables.containsKey(name);
    }

    public final Set<String> getVariableNames() {
        return this.variables.keySet();
    }


    public final Object getVariable(final String name) {
        return this.variables.get(name);
    }


    /**
     * <p>
     *   Sets the locale to be used.
     * </p>
     *
     * @param locale the locale.
     */
    public void setLocale(final Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }


    /**
     * <p>
     *   Sets a new variable into the context.
     * </p>
     *
     * @param name the name of the variable.
     * @param value the value of the variable.
     */
    public void setVariable(final String name, final Object value) {
        this.variables.put(name, value);
    }


    /**
     * <p>
     *   Sets several variables at a time into the context.
     * </p>
     *
     * @param variables the variables to be set.
     */
    public void setVariables(final Map<String,Object> variables) {
        if (variables == null) {
            return;
        }
        this.variables.putAll(variables);
    }


    /**
     * <p>
     *   Removes a variable from the context.
     * </p>
     *
     * @param name the name of the variable to be removed.
     */
    public void removeVariable(final String name) {
        this.variables.remove(name);
    }


    /**
     * <p>
     *   Removes all the variables from the context.
     * </p>
     */
    public void clearVariables() {
        this.variables.clear();
    }


}
