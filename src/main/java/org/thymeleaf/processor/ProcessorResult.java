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
package org.thymeleaf.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.inliner.ITextInliner;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class ProcessorResult {

    private static final Map<String,Object> EMPTY_VARIABLES = Collections.unmodifiableMap(new HashMap<String, Object>());

    
    public static final ProcessorResult OK = new ProcessorResult(null, null, false, null, false);
    
    private final Map<String,Object> localVariables;
    private final Object selectionTarget;
    private final boolean selectionTargetSet;
    private final ITextInliner textInliner;
    private final boolean textInlinerSet;

    
    
    public static ProcessorResult ok() {
        return OK;
    }
    
    public static ProcessorResult setLocalVariables(final Map<String,Object> localVariables) {
        return new ProcessorResult(localVariables, null, false, null, false);
    }
    
    public static ProcessorResult setSelectionTarget(final Object target) {
        return new ProcessorResult(null, target, true, null, false);
    }
    
    public static ProcessorResult setInliner(final ITextInliner textInliner) {
        return new ProcessorResult(null, null, false, textInliner, true);
    }
    
    public static ProcessorResult setLocalVariablesAndSelectionTarget(final Map<String,Object> localVariables, final Object target) {
        return new ProcessorResult(localVariables, target, true, null, false);
    }
    
    public static ProcessorResult setLocalVariablesAndTextInliner(final Map<String,Object> localVariables, final ITextInliner textInliner) {
        return new ProcessorResult(localVariables, null, false, textInliner, true);
    }
    
    public static ProcessorResult setSelectionTargetAndTextInliner(final Object target, final ITextInliner textInliner) {
        return new ProcessorResult(null, target, true, textInliner, true);
    }
    
    public static ProcessorResult setLocalVariablesAndSelectionTargetAndTextInliner(final Map<String,Object> localVariables, final Object target, final ITextInliner textInliner) {
        return new ProcessorResult(localVariables, target, true, textInliner, true);
    }


    
    
    private ProcessorResult(
            final Map<String,Object> localVariables,
            final Object selectionTarget,
            final boolean selectionTargetSet,
            final ITextInliner textInliner,
            final boolean textInlinerSet) {
        super();
        this.localVariables =
            (localVariables == null?
                    EMPTY_VARIABLES :
                    Collections.unmodifiableMap(new HashMap<String, Object>(localVariables)));
        this.selectionTarget = selectionTarget;
        this.selectionTargetSet = selectionTargetSet;
        this.textInliner = textInliner;
        this.textInlinerSet = textInlinerSet;
    }

    
    
    public boolean hasLocalVariables() {
        return (this.localVariables != null && this.localVariables.size() > 0);
    }

    public Map<String, Object> getLocalVariables() {
        return this.localVariables;
    }
    
    public Object getSelectionTarget() {
        return this.selectionTarget;
    }
    
    public boolean isSelectionTargetSet() {
        return this.selectionTargetSet;
    }
    
    public ITextInliner getTextInliner() {
        return this.textInliner;
    }
    
    public boolean isTextInlinerSet() {
        return this.textInlinerSet;
    }
    
    public boolean isOK() {
        return (this.localVariables == null || this.localVariables.size() == 0) &&
               !this.selectionTargetSet && !this.textInlinerSet; 
    }
    

    
    
    
    public Arguments computeNewArguments(final Arguments arguments) {
        
        if (isOK()) {
            return arguments;
        }
        
        if (this.localVariables != null && this.localVariables.size() > 0) {
            if (this.selectionTargetSet) {
                // A selection target has been set
                if (this.textInlinerSet) {
                    // A text inliner has been set
                    return arguments.addLocalVariablesAndTextInlinerAndSetSelectionTarget(this.localVariables, this.textInliner, this.selectionTarget);
                }
                // A text inliner has not been set
                return arguments.addLocalVariablesAndSetSelectionTarget(this.localVariables, this.selectionTarget);
            }
            // A selection target has not been set
            if (this.textInlinerSet) {
                // A text inliner has been set
                return arguments.addLocalVariablesAndTextInliner(this.localVariables, this.textInliner);
            }
            // A text inliner has not been set
            return arguments.addLocalVariables(this.localVariables);
        }
        // There are no local variables
        if (this.selectionTargetSet) {
            // A selection target has been set
            if (this.textInlinerSet) {
                // A text inliner has been set
                return arguments.setTextInlinerAndSetSelectionTarget(this.textInliner, this.selectionTarget);
            }
            // A text inliner has not been set
            return arguments.setSelectionTarget(this.selectionTarget);
        }
        // A selection target has not been set
        if (this.textInlinerSet) {
            // A text inliner has been set
            return arguments.setTextInliner(this.textInliner);
        }
        // A text inliner has not been set
        return arguments;
        
    }
    
}
