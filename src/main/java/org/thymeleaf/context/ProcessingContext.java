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

import java.util.Map;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ProcessingContext extends AbstractProcessingContext {


    
    
    public ProcessingContext(final IContext context) {
        super(context);
    }

    
    
    public ProcessingContext(
            final IContext context, final Map<String,Object> localVariables) {
        super(context, localVariables);
    }

    
    
    public ProcessingContext(
            final IContext context, final Map<String,Object> localVariables, 
            final Object selectionTarget, final boolean selectionTargetSet) {
        super(context, localVariables, selectionTarget, selectionTargetSet);
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
    public ProcessingContext addLocalVariables(final Map<String,Object> newVariables) {
        if (newVariables == null || newVariables.isEmpty()) {
            return this;
        }
        final ProcessingContext newContext = 
                new ProcessingContext(getContext(), mergeNewLocalVariables(newVariables), 
                        getSelectionTarget(), hasSelectionTarget());
        return newContext;
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
    public ProcessingContext setSelectionTarget(final Object selectionTargetObject) {
        return new ProcessingContext(getContext(), getLocalVariables(), selectionTargetObject, true);
    }
    
    
    
}
