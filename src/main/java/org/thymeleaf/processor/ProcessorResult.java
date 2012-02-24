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

    
    public static final ProcessorResult OK = new ProcessorResult(null, false, false);
    
    private final Map<String,Object> localVariables;
    private final boolean processOnlyElementNodes;
    private final boolean processOnlyElementNodesSet;

    
    
    public static ProcessorResult ok() {
        return OK;
    }
    
    public static ProcessorResult setLocalVariables(final Map<String,Object> localVariables) {
        return new ProcessorResult(localVariables, false, false);
    }
    
    public static ProcessorResult setProcessOnlyElementNodes(final boolean processOnlyElementNodes) {
        return new ProcessorResult(null, processOnlyElementNodes, true);
    }
    
    public static ProcessorResult setLocalVariablesAndProcessOnlyElementNodes(final Map<String,Object> localVariables, final boolean processOnlyElementNodes) {
        return new ProcessorResult(localVariables, processOnlyElementNodes, true);
    }


    
    
    private ProcessorResult(
            final Map<String,Object> localVariables,
            final boolean processOnlyElementNodes,
            final boolean processOnlyElementNodesSet) {
        super();
        this.localVariables =
            (localVariables == null?
                    EMPTY_VARIABLES :
                    Collections.unmodifiableMap(new HashMap<String, Object>(localVariables)));
        this.processOnlyElementNodes = processOnlyElementNodes;
        this.processOnlyElementNodesSet = processOnlyElementNodesSet;
    }

    
    
    public boolean hasLocalVariables() {
        return (this.localVariables != null && this.localVariables.size() > 0);
    }

    public Map<String, Object> getLocalVariables() {
        return this.localVariables;
    }
    
    public boolean getProcessOnlyElementNodes() {
        return this.processOnlyElementNodes;
    }
    
    public boolean isProcessOnlyElementNodesSet() {
        return this.processOnlyElementNodesSet;
    }
    
    public boolean isOK() {
        return (this.localVariables == null || this.localVariables.size() == 0) &&
               !this.processOnlyElementNodesSet; 
    }
    

    
    
    
    public Arguments computeNewArguments(final Arguments arguments) {
        
        if (isOK()) {
            return arguments;
        }
        
        if (this.localVariables != null && this.localVariables.size() > 0) {
            // There are local variables
            if (this.processOnlyElementNodesSet) {
                // A text inliner has been set
                return arguments.addLocalVariablesAndProcessOnlyElementNodes(this.localVariables, this.processOnlyElementNodes);
            }
            // A text inliner has not been set
            return arguments.addLocalVariables(this.localVariables);
        }
        // There are no local variables
        if (this.processOnlyElementNodesSet) {
            // A text inliner has been set
            return arguments.setProcessOnlyElementNodes(this.processOnlyElementNodes);
        }
        // A text inliner has not been set
        return arguments;
        
    }
    
}
