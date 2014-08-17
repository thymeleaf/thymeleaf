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
package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Abstract class for {@link IContext} implementations, providing some of the
 *   features required to implement this interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractContext implements IContext {
    
    /**
     * <p>
     *   Name of the variable containing the "execution info" object.
     * </p>
     */
    public static final String EXEC_INFO_VARIABLE_NAME = "execInfo";

    
    
    private Locale locale;
    private final VariablesMap<String,Object> variables;


    
    /**
     * <p>
     *   Create an instance without specifying a locale. Using this constructor,
     *   the default locale (<tt>Locale.getDefault()</tt>) will be used.
     * </p>
     */
    protected AbstractContext() {
        this(Locale.getDefault());
    }
    
    /**
     * <p>
     *   Create an instance specifying a locale.
     * </p>
     * 
     * @param locale the locale to be used.
     */
    AbstractContext(final Locale locale) {
        this(locale, (Map<String,?>)null);
    }
    
    /**
     * <p>
     *   Create an instance specifying a locale and an initial set of context
     *   variables.
     * </p>
     * 
     * @param locale the locale to be used.
     * @param variables the initial set of context variables.
     */
    AbstractContext(final Locale locale, final Map<String, ?> variables) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
        final VariablesMap<String,Object> newVariablesMap;
        if (variables != null) {
            newVariablesMap = new VariablesMap<String,Object>((variables.size()*3)/2, 1.0f);
            newVariablesMap.putAll(variables);
        } else {
            newVariablesMap = new VariablesMap<String,Object>(5);
        }
        this.variables = newVariablesMap;
    }
    
    /**
     * <p>
     *   Create an instance specifying a locale and the specific VariablesMap instance to be used
     *   for containing variables.
     * </p>
     * 
     * @param locale the locale to be used.
     * @param variablesMap the variables map.
     * 
     * @since 2.0.9
     */
    AbstractContext(final Locale locale, final VariablesMap<String, Object> variablesMap) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(variablesMap, "Variables map cannot be null");
        this.locale = locale;
        this.variables = variablesMap;
    }
    

    public Locale getLocale() {
        return this.locale;
    }
    
    /**
     * <p>
     *   Set the locale to be used for template execution.
     * </p>
     * <p>
     *   The locale specified using this method overrides the one that might
     *   have been set using a constructor.
     * </p>
     * 
     * @param locale the locale to be set.
     */
    public void setLocale(final Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }

    
    public final VariablesMap<String, Object> getVariables() {
        return this.variables;
    }
    
    
    /**
     * <p>
     *   Adds a variable to the current set of context variables.
     * </p>
     * 
     * @param name the name of the variable.
     * @param value the value of the variable.
     */
    public final void setVariable(final String name, final Object value) {
        Validate.notNull(name, "Variable name cannot be null");
        this.variables.put(name, value);
    }
    
    
    /**
     * <p>
     *   Adds a set of variables to the current set of context variables.
     * </p>
     * 
     * @param additionalVariables the new variables to be added.
     */
    public final void setVariables(final Map<String,?> additionalVariables) {
        Validate.notNull(additionalVariables, "Variables map cannot be null");
        this.variables.putAll(additionalVariables);
    }
    

    /**
     * <p>
     *   Removes from the map all the context variables currently set.
     * </p>
     */
    public final void clearVariables() {
        this.variables.clear();
    }

    

    /**
     * <p>
     *   Adds the context execution info to the variables map.
     * </p>
     * <p>
     *   This {@link IContext} implementation adds an object of the
     *   {@link ContextExecutionInfo} class with variable name <tt>execInfo</tt>. 
     * </p>
     * 
     * @param templateName the name of the template being executed
     */
    public final void addContextExecutionInfo(final String templateName) {
        Validate.notEmpty(templateName, "Template name cannot be null or empty");
        setVariable(EXEC_INFO_VARIABLE_NAME, buildContextExecutionInfo(templateName));
    }
    
    
    /**
     * <p>
     *   Creates the specific instance of {@link IContextExecutionInfo} to be added
     *   to the context as the <tt>execInfo</tt> variable.
     * </p>
     * 
     * @param templateName the name of the template being executed
     * @return the execution info object
     */
    protected abstract IContextExecutionInfo buildContextExecutionInfo(final String templateName);
    
    
}
