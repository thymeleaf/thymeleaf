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

import java.util.Map;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public interface IProcessingContext {

    
    
    /**
     * <p>
     *   Returns the current context specified for template processing.
     * </p>
     * 
     * @return the current context
     */
    public IContext getContext();
    
    

    /**
     * <p>
     *   Returns the map of expression objects that should be made available to every expression
     *   evaluation operation (whenever variable evaluation is available). In OGNL and SpringEL
     *   expressions, these will be available as <tt>#object1</tt>, <tt>#object2</tt>, etc.
     * </p>
     * <p>
     *   This method <b>cannot return null</b>, and must return a modifiable Map object (which
     *   will, in fact, be modified).
     * </p>
     * 
     * @return the map of objects
     */
    public Map<String,Object> getExpressionObjects();
    
    
    
    
    /**
     * <p>
     *   Returns the current evaluation root. This is the object on which expressions
     *   (normal expressions, like those specified in the standard dialect with
     *   <tt>${...}</tt>) are executed.
     * </p>
     * 
     * @return the expression evaluation root
     */
    public Object getExpressionEvaluationRoot();


    
    /**
     * <p>
     *   Returns the current selection evaluation root. This is the object on which selection expressions
     *   (like those specified in the standard dialect with <tt>*{...}</tt>) are executed.
     * </p>
     * 
     * @return the selection evaluation root
     */
    public Object getExpressionSelectionEvaluationRoot();

    
    
    /**
     * <p>
     *   Returns whether there currently is a selection going on
     *   (e.g. <tt>th:object</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there is a selection currently established, false if not
     */
    public boolean hasSelectionTarget();
    
    
    
    /**
     * <p>
     *   Returns the <i>selection target object</i>, and raises an
     *   exception if there isn't any.
     * </p>
     * <p>
     *   Meant for internal use.
     * </p>
     * 
     * @return the selection target object
     */
    public Object getSelectionTarget();
    
    
    
    /**
     * <p>
     *   Returns whether local variables have currently been specified or not.
     *   (e.g. <tt>th:with</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there are local variables, false if not
     */
    public boolean hasLocalVariables();

    
    
    /**
     * <p>
     *   Returns whether a specific local variable is defined or not.
     * </p>
     * 
     * @return true if the variable is currently defined, false if not.
     */
    public boolean hasLocalVariable(final String variableName);

    
    
    /**
     * <p>
     *   Returns the value of a local variable.
     * </p>
     * 
     * @param variableName the name of the local variable to be returned
     * @return the value of the variable, or null if the variable does not exist (or has null value)
     * 
     */
    public Object getLocalVariable(final String variableName);

    
    
    /**
     * <p>
     *   Returns the map of local variables.
     * </p>
     * 
     * @return the local variables
     */
    public Map<String,Object> getLocalVariables();

    
}
