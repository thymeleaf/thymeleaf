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
package org.thymeleaf.aurora.context;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0 (replaces interface with the same name existing since 1.0)
 *
 */
public abstract class AbstractContext implements IContext {

    private final Map<String,Object> variables;
    private Locale locale;


    protected AbstractContext() {
        this(Locale.getDefault());
    }


    protected AbstractContext(final Locale locale) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
        this.variables = new LinkedHashMap<String, Object>(10);
    }


    protected AbstractContext(final Locale locale, final Map<String, Object> variables) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
        this.variables = new LinkedHashMap<String, Object>(variables);
    }


    public final Locale getLocale() {
        return this.locale;
    }


    public final Set<String> getVariableNames() {
        return this.variables.keySet();
    }


    public final Object getVariable(final String name) {
        return this.variables.get(name);
    }


    public final void setLocale(final Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }


    public final void setVariable(final String name, final Object value) {
        this.variables.put(name, value);
    }


    public final void setVariables(final Map<String,Object> variables) {
        if (variables == null) {
            return;
        }
        this.variables.putAll(variables);
    }


    public final void removeVariable(final String name) {
        this.variables.remove(name);
    }


    public final void clearVariables() {
        this.variables.clear();
    }


}
