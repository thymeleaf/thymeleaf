/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.expression;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class ExpressionEvaluationContext {

    
    public static final String SELECTION_TARGET_LOCAL_VARIABLE_NAME = "%%{SELECTION_TARGET}%%";
    

    private IContext context;
    private Object evaluationRoot;
    private Object selectionEvaluationRoot;
    private final HashMap<String,Object> localVariables;
    
    
    
    public ExpressionEvaluationContext(final IContext context) {
        
        super();
        
        Validate.notNull(context, "Context cannot be null");
        
        this.context = context;
        this.localVariables = null;
        this.evaluationRoot = createEvaluationRoot();
        this.selectionEvaluationRoot = createSelectedEvaluationRoot();
        
    }

    
    public ExpressionEvaluationContext(
            final IContext context, final Map<String,Object> localVariables) {
        
        super();
        
        Validate.notNull(context, "Context cannot be null");
        
        this.context = context;
        this.localVariables =
                (localVariables != null?
                        new HashMap<String,Object>(localVariables) : null);
        this.evaluationRoot = createEvaluationRoot();
        this.selectionEvaluationRoot = createSelectedEvaluationRoot();
        
    }
    
    
    
    
    private Object createEvaluationRoot() {
        
        final Map<String,Object> newEvaluationRoot = new VariablesMap<String,Object>();
        newEvaluationRoot.putAll(this.context.getVariables());
        if (hasLocalVariables()) {
            newEvaluationRoot.putAll(this.localVariables);
        }
        
        return newEvaluationRoot;
    }
    
    
    private Object createSelectedEvaluationRoot() {
        
        if (hasSelectionTarget()) {
            return getSelectionTarget();
        }
        return this.evaluationRoot;

    }
    
    

    

    /**
     * <p>
     *   Returns the current context specified for template processing.
     * </p>
     * 
     * @return the current context
     */
    public IContext getContext() {
        return this.context;
    }
    
    
    
    
    /**
     * <p>
     *   Returns the current evaluation root. This is the object on which expressions
     *   (normal expressions, like those specified in the standard dialect with
     *   <tt>${...}</tt>) are executed.
     * </p>
     * 
     * @return the expression evaluation root
     */
    public Object getExpressionEvaluationRoot() {
        return this.evaluationRoot;
    }

    
    /**
     * <p>
     *   Returns the current selection evaluation root. This is the object on which selection expressions
     *   (like those specified in the standard dialect with <tt>*{...}</tt>) are executed.
     * </p>
     * 
     * @return the selection evaluation root
     */
    public Object getExpressionSelectionEvaluationRoot() {
        return this.selectionEvaluationRoot;
    }
    
    
    /**
     * <p>
     *   Returns whether there currently is a selection going on
     *   (e.g. <tt>th:object</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there is a selection currently established, false if not
     */
    public boolean hasSelectionTarget() {
        return hasLocalVariable(ExpressionEvaluationContext.SELECTION_TARGET_LOCAL_VARIABLE_NAME);
    }
    
    
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
    public Object getSelectionTarget() {
        if (hasSelectionTarget()) {
            return getLocalVariable(ExpressionEvaluationContext.SELECTION_TARGET_LOCAL_VARIABLE_NAME);
        }
        throw new IllegalStateException(
                "Cannot return selection target object, a selection target has not been set.");    }
    
    
    /**
     * <p>
     *   Returns whether local variables have currently been specified or not.
     *   (e.g. <tt>th:with</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there are local variables, false if not
     */
    public boolean hasLocalVariables() {
        return this.localVariables != null && this.localVariables.size() > 0;
    }
    
    
    /**
     * <p>
     *   Returns whether a specific local variable is defined or not.
     * </p>
     * 
     * @return true if the variable is currently defined, false if not.
     */
    public boolean hasLocalVariable(final String variableName) {
        if (this.localVariables == null) {
            return false;
        }
        return this.localVariables.containsKey(variableName);
    }
    
    
    /**
     * <p>
     *   Returns the value of a local variable.
     * </p>
     * 
     * @param variableName the name of the local variable to be returned
     * @return the value of the variable, or null if the variable does not exist (or has null value)
     * 
     */
    public Object getLocalVariable(final String variableName) {
        if (this.localVariables == null) {
            return null;
        }
        return this.localVariables.get(variableName);
    }
    
    
    /**
     * <p>
     *   Returns the real inner map of local variables. This
     *   method should not be called directly.
     * </p>
     * 
     * @return the local variables map, which could be null if no variables are defined
     */
    public HashMap<String,Object> unsafeGetLocalVariables() {
        return this.localVariables;
    }

    
    /**
     * <p>
     *   Returns a safe copy of the map of local variables.
     * </p>
     * 
     * @return the local variables
     */
    public Map<String,Object> getLocalVariables() {
        final HashMap<String,Object> vars = new HashMap<String, Object>();
        if (this.localVariables != null) {
            vars.putAll(this.localVariables);
        }
        return vars;
    }
    
    
    
    
    
    
    
    
    /**
     * <p>
     *   Creates a new Arguments object by adding some new local variables 
     *   to the existing map (the rest of the attributes are copied verbatim).
     * </p>
     * 
     * @param newVariables the new variables
     * @return the new Arguments object
     */
    public ExpressionEvaluationContext addLocalVariables(final Map<String,Object> newVariables) {
        if (newVariables == null || newVariables.isEmpty()) {
            return this;
        }
        final int localVariablesSize = (this.localVariables != null? this.localVariables.size() : 0);
        final HashMap<String,Object> cloneLocalVariables = 
                new HashMap<String, Object>(localVariablesSize + newVariables.size() + 1, 1.0f);
        if (this.localVariables != null) {
            cloneLocalVariables.putAll(this.localVariables);
        }
        cloneLocalVariables.putAll(newVariables);
        final ExpressionEvaluationContext newContext = 
                new ExpressionEvaluationContext(this.context, cloneLocalVariables);
        return newContext;
    }

    
    
    
}
