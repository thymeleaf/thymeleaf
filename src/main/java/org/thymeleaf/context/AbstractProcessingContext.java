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
package org.thymeleaf.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractProcessingContext implements IProcessingContext {

    
    protected static final String EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME = "%%{SELECTION_TARGET}%%";
    

    private final IContext context;
    private final Object evaluationRoot;
    private final Object selectionEvaluationRoot;
    private final HashMap<String,Object> localVariables;
    
    private boolean computedBaseContextVariables = false;
    private Map<String, Object> expressionObjects = null;
    

    
    
    protected AbstractProcessingContext(final IContext context) {
        this(context, null, null, false);
    }

    
    protected AbstractProcessingContext(
            final IContext context, final Map<String,Object> localVariables) {
        this(context, localVariables, null, false);
    }

    
    
    protected AbstractProcessingContext(
            final IContext context, final Map<String,Object> localVariables, 
            final Object selectionTarget, final boolean selectionTargetSet) {
        
        super();
        
        Validate.notNull(context, "Context cannot be null");
        
        this.context = context;
        this.localVariables =
                (localVariables != null?
                        new HashMap<String,Object>(localVariables) : 
                        (selectionTargetSet?
                                new HashMap<String, Object>(2, 1.0f) :
                                null));
        if (selectionTargetSet) {
            this.localVariables.put(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME, selectionTarget);
        }
        this.evaluationRoot = createEvaluationRoot();
        this.selectionEvaluationRoot = createSelectedEvaluationRoot();
        
    }
    
    
    
    @SuppressWarnings("unchecked")
    private Object createEvaluationRoot() {

        final VariablesMap<String, Object> contextVariables = this.context.getVariables();
        // We create a new VariablesMap instance using its constructor instead of cloning the existing one
        // because we want to avoid undesirable interactions like, for example, those that could happen
        // if we executed putAll on a WebVariablesMap object (which would add those variables to the HttpServletRequest
        // and therefore make them available to the whole page and not just the local variable scope).
        final VariablesMap<String,Object> newEvaluationRoot = new VariablesMap<String, Object>(contextVariables);
        if (this.localVariables != null) {
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
    
    
    
    protected Map<String,Object> computeExpressionObjects() {
        return ExpressionEvaluatorObjects.computeEvaluationObjects(this);
    }
    


    /**
     * @deprecated Use {@link #getExpressionObjects()} instead. Will be removed in 2.1.x 
     */
    @Deprecated
    public Map<String,Object> getBaseContextVariables() {
        return getExpressionObjects();
    }
    

    public Map<String,Object> getExpressionObjects() {
        if (!this.computedBaseContextVariables) {
            // Base context variables are computed lazily so that subclasses have
            // the chance to override the basic computing behaviour.
            // The boolean flag is modified first so that if this method is called by
            // error during the implementation of "computeExpressionObjects()" it does
            // not result in an infinite loop.
            this.computedBaseContextVariables = true;
            this.expressionObjects = computeExpressionObjects();
        }
        return this.expressionObjects;
    }

    
    public IContext getContext() {
        return this.context;
    }
    
    
    public Object getExpressionEvaluationRoot() {
        return this.evaluationRoot;
    }

    
    public Object getExpressionSelectionEvaluationRoot() {
        return this.selectionEvaluationRoot;
    }
    
    
    public boolean hasSelectionTarget() {
        return hasLocalVariable(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME) ||
               getContext().getVariables().containsKey(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME);
    }
    
    
    public Object getSelectionTarget() {
        if (hasLocalVariable(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME)) {
            return getLocalVariable(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME);
        }
        if (getContext().getVariables().containsKey(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME)) {
            return getContext().getVariables().get(EVAL_SELECTION_TARGET_LOCAL_VARIABLE_NAME);
        }
        return null;
    }
    
    
    public boolean hasLocalVariables() {
        return this.localVariables != null && this.localVariables.size() > 0;
    }
    
    
    public boolean hasLocalVariable(final String variableName) {
        if (this.localVariables == null) {
            return false;
        }
        return this.localVariables.containsKey(variableName);
    }
    
    
    public Object getLocalVariable(final String variableName) {
        if (this.localVariables == null) {
            return null;
        }
        return this.localVariables.get(variableName);
    }

    
    public Map<String,Object> getLocalVariables() {
        if (this.localVariables != null) {
            return Collections.unmodifiableMap(this.localVariables);
        }
        return null;
    }
    
    
    /**
     * <p>
     *   Returns the real inner map of local variables. This
     *   method should not be called directly.
     * </p>
     * 
     * @return the local variables map, which could be null if no variables are defined
     * @deprecated Use {@link #getLocalVariables()} instead. Will be removed in 2.1.x.
     */
    @Deprecated
    public HashMap<String,Object> unsafeGetLocalVariables() {
        return this.localVariables;
    }
    
    
    
        
    protected Map<String,Object> mergeNewLocalVariables(final Map<String,Object> newVariables) {
        if (newVariables == null || newVariables.isEmpty()) {
            return this.localVariables;
        }
        final int localVariablesSize = (this.localVariables != null? this.localVariables.size() : 0);
        final HashMap<String,Object> cloneLocalVariables = 
                new HashMap<String, Object>(localVariablesSize + newVariables.size() + 1, 1.0f);
        if (this.localVariables != null) {
            cloneLocalVariables.putAll(this.localVariables);
        }
        cloneLocalVariables.putAll(newVariables);
        return cloneLocalVariables;
    }
    
    
    
}
