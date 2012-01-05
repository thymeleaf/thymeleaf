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


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class ProcessorResult {

    private static final Map<String,Object> EMPTY_VARIABLES = Collections.unmodifiableMap(new HashMap<String, Object>());

    
    public static final ProcessorResult OK = new ProcessorResult(null, null, false, false, false);
    
    private final Map<String,Object> localVariables;
    private final Object selectionTarget;
    private final boolean selectionTargetSet;
    private final boolean processOnlyTags;
    private final boolean processOnlyTagsSet;

    
    
    public static ProcessorResult ok() {
        return OK;
    }
    
    public static ProcessorResult setLocalVariables(final Map<String,Object> localVariables) {
        return new ProcessorResult(localVariables, null, false, false, false);
    }
    
    public static ProcessorResult setSelectionTarget(final Object target) {
        return new ProcessorResult(null, target, true, false, false);
    }
    
    public static ProcessorResult setProcessOnlyTags(final boolean processOnlyTags) {
        return new ProcessorResult(null, null, false, processOnlyTags, true);
    }
    
    public static ProcessorResult setLocalVariablesAndSelectionTarget(final Map<String,Object> localVariables, final Object target) {
        return new ProcessorResult(localVariables, target, true, false, false);
    }
    
    public static ProcessorResult setLocalVariablesAndProcessOnlyTags(final Map<String,Object> localVariables, final boolean processOnlyTags) {
        return new ProcessorResult(localVariables, null, false, processOnlyTags, true);
    }
    
    public static ProcessorResult setSelectionTargetAndProcessOnlyTags(final Object target, final boolean processOnlyTags) {
        return new ProcessorResult(null, target, true, processOnlyTags, true);
    }
    
    public static ProcessorResult setLocalVariablesAndSelectionTargetAndProcessOnlyTags(final Map<String,Object> localVariables, final Object target, final boolean processOnlyTags) {
        return new ProcessorResult(localVariables, target, true, processOnlyTags, true);
    }


    
    
    private ProcessorResult(
            final Map<String,Object> localVariables,
            final Object selectionTarget,
            final boolean selectionTargetSet,
            final boolean processOnlyTags,
            final boolean processOnlyTagsSet) {
        super();
        this.localVariables =
            (localVariables == null?
                    EMPTY_VARIABLES :
                    Collections.unmodifiableMap(new HashMap<String, Object>(localVariables)));
        this.selectionTarget = selectionTarget;
        this.selectionTargetSet = selectionTargetSet;
        this.processOnlyTags = processOnlyTags;
        this.processOnlyTagsSet = processOnlyTagsSet;
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
    
    public boolean getProcessOnlyTags() {
        return this.processOnlyTags;
    }
    
    public boolean isProcessOnlyTagsSet() {
        return this.processOnlyTagsSet;
    }
    
    public boolean isOK() {
        return (this.localVariables == null || this.localVariables.size() == 0) &&
               !this.selectionTargetSet && !this.processOnlyTagsSet; 
    }
    

    
    
    
    public Arguments computeNewArguments(final Arguments arguments) {
        
        if (isOK()) {
            return arguments;
        }
        
        if (this.localVariables != null && this.localVariables.size() > 0) {
            if (this.selectionTargetSet) {
                // A selection target has been set
                if (this.processOnlyTagsSet) {
                    // A new value has been set for processOnlyTags
                    return arguments.addLocalVariablesAndProcessOnlyTagsAndSetSelectionTarget(this.localVariables, this.processOnlyTags, this.selectionTarget);
                }
                // A text inliner has not been set
                return arguments.addLocalVariablesAndSetSelectionTarget(this.localVariables, this.selectionTarget);
            }
            // A selection target has not been set
            if (this.processOnlyTagsSet) {
                // A text inliner has been set
                return arguments.addLocalVariablesAndProcessOnlyTags(this.localVariables, this.processOnlyTags);
            }
            // A text inliner has not been set
            return arguments.addLocalVariables(this.localVariables);
        }
        // There are no local variables
        if (this.selectionTargetSet) {
            // A selection target has been set
            if (this.processOnlyTagsSet) {
                // A text inliner has been set
                return arguments.setProcessOnlyTagsAndSetSelectionTarget(this.processOnlyTags, this.selectionTarget);
            }
            // A text inliner has not been set
            return arguments.setSelectionTarget(this.selectionTarget);
        }
        // A selection target has not been set
        if (this.processOnlyTagsSet) {
            // A text inliner has been set
            return arguments.setProcessOnlyTags(this.processOnlyTags);
        }
        // A text inliner has not been set
        return arguments;
        
    }
    
}
