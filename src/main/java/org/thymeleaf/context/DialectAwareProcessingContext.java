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

import java.util.Collection;
import java.util.Map;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Basic implementation of {@link AbstractDialectAwareProcessingContext}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.12
 *
 */
public class DialectAwareProcessingContext extends AbstractDialectAwareProcessingContext {


    
    
    public DialectAwareProcessingContext(final IContext context, final Collection<? extends IDialect> dialects) {
        super(context, dialects);
    }

    
    
    public DialectAwareProcessingContext(
            final IContext context, final Map<String,Object> localVariables, 
            final Collection<? extends IDialect> dialects) {
        super(context, localVariables, dialects);
    }

    
    
    public DialectAwareProcessingContext(
            final IContext context, final Map<String,Object> localVariables, 
            final Object selectionTarget, final boolean selectionTargetSet, 
            final Collection<? extends IDialect> dialects) {
        super(context, localVariables, selectionTarget, selectionTargetSet, dialects);
    }
    
    
    
    public DialectAwareProcessingContext(final AbstractDialectAwareProcessingContext processingContext) {
        
        super((processingContext == null? null : processingContext.getContext()),
                (processingContext == null? null : processingContext.getLocalVariables()),
                (processingContext == null? null : processingContext.getSelectionTarget()),
                (processingContext != null && processingContext.hasSelectionTarget()),
                (processingContext == null? null : processingContext.getExpressionEnhancingDialects()));
        
        Validate.notNull(processingContext, "Processing context cannot be null");
        
    }
    
    
    
    public DialectAwareProcessingContext(
            final IProcessingContext processingContext, final Collection<? extends IDialect> dialects) {
        
        super((processingContext == null? null : processingContext.getContext()),
                (processingContext == null? null : processingContext.getLocalVariables()),
                (processingContext == null? null : processingContext.getSelectionTarget()),
                (processingContext != null && processingContext.hasSelectionTarget()),
                dialects);
        
        Validate.notNull(processingContext, "Processing context cannot be null");
        
    }
    
    
    
    
    
    /**
     * <p>
     *   Creates a new DialectAwareProcessingContext object by adding some new local variables 
     *   to the existing map (the rest of the attributes are copied verbatim).
     * </p>
     * 
     * @param newVariables the new variables
     * @return the new Arguments object
     */
    public DialectAwareProcessingContext addLocalVariables(final Map<String,Object> newVariables) {
        if (newVariables == null || newVariables.isEmpty()) {
            return this;
        }
        return new DialectAwareProcessingContext(getContext(), mergeNewLocalVariables(newVariables),
                        getSelectionTarget(), hasSelectionTarget(), getExpressionEnhancingDialects());
    }
    

    
    
    /**
     * <p>
     *   Creates a new Arguments object by setting a selection target into the processing  
     *   context, so that selection expressions are executed on it from then on.
     * </p>
     * 
     * @param selectionTargetObject the selection target object to be set.
     * @return the new ProcessingContext object
     */
    public DialectAwareProcessingContext setSelectionTarget(final Object selectionTargetObject) {
        return new DialectAwareProcessingContext(getContext(), getLocalVariables(), selectionTargetObject, true, getExpressionEnhancingDialects());
    }
    
    
    
}
