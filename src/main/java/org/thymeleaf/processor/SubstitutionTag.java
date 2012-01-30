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

import org.thymeleaf.util.Validate;
import org.w3c.dom.Node;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class SubstitutionTag {

    private static final Map<String,Object> EMPTY_VARIABLES = Collections.unmodifiableMap(new HashMap<String, Object>());
    
    
    private final Node node;
    private final Map<String,Object> localVariables;
    

    
    public static SubstitutionTag forNode(final Node node) {
        return new SubstitutionTag(node, null);
    }
    
    public static SubstitutionTag forNodeAndLocalVariables(final Node node, final Map<String,Object> localVariables) {
        return new SubstitutionTag(node, localVariables);
    }

    
    private SubstitutionTag(final Node node, final Map<String,Object> localVariables) {
        super();
        Validate.notNull(node, "Node cannot be null");
        this.node = node;
        this.localVariables =
            (localVariables == null?
                    EMPTY_VARIABLES :
                    Collections.unmodifiableMap(new HashMap<String, Object>(localVariables)));
            
    }


    public Node getNode() {
        return this.node;
    }
    
    
    public Map<String, Object> getLocalVariables() {
        return this.localVariables;
    }
    
    
}
