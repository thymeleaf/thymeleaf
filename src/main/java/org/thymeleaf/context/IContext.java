/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

/**
 * <p>
 *   Interface for all context implementations.
 * </p>
 * <p>
 *   A context object will be used for each template execution, and every context
 *   implementation must offer at least:
 * </p>
 * <ul>
 *   <li>The <b>map of variables</b>, in the form of a {@link VariablesMap} object.</li>
 *   <li>The <b>locale</b> that will be used for template execution.</li>
 *   <li>A way to (optionally) add an "execution info" object to the map of variables, containing
 *       execution info like template name, timestamp, etc.</li> 
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface IContext {
    
    /**
     * <p>
     *   Returns the {@link VariablesMap} object containing the variables that will be
     *   available for the execution of expressions inside templates.
     * </p>
     * 
     * @return the variables map.
     */
    public VariablesMap<String,Object> getVariables();

    /**
     * <p>
     *   Returns the locale that will be used for template execution. This locale will
     *   determine the language of the externalized messages resolved by the message
     *   resolvers. 
     * </p>
     * 
     * @return the locale to be used for template execution.
     */
    public Locale getLocale();

    
    /**
     * <p>
     *   Initializes the IContext object with the (optional) addition
     *   of an "execution info" variable (usually called <tt>execInfo</tt>)
     *   to the variables map.
     * </p>
     * <p>
     *   Each implementation of IContext will be responsible for the addition
     *   of a specialized object of the class it prefers (implementation of the
     *   {@link IContextExecutionInfo} interface is recommended but not required).
     * </p>
     * 
     * @param templateName the name of the template being executed
     */
    public void addContextExecutionInfo(final String templateName);
    
    
}
