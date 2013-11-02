/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Allows access to a specific variable in the map, but outputs
 *   a warning through the log.
 * </p>
 * 
 * @since 2.0.16
 *
 */
public class WarningContextVariableRestriction implements IContextVariableRestriction {

    private static final Logger log = LoggerFactory.getLogger(WarningContextVariableRestriction.class);
    
    
    private final String targetVariableName;
    private final String message;
    
    
    public WarningContextVariableRestriction(final String targetVariableName) {
        this(targetVariableName, null);
    }
    
    public WarningContextVariableRestriction(final String targetVariableName, final String message) {
        super();
        Validate.notNull(targetVariableName, "Variable name cannot be null");
        this.targetVariableName = targetVariableName;
        this.message = message;
    }

    
    public void checkAccess(final VariablesMap<?, ?> variablesMap, final String variableName) {
        
        if (!this.targetVariableName.equals(variableName)) {
            return;
        }
        
        final String warningMessage = 
                (this.message != null ? 
                    this.message : 
                    "Variable \"" + variableName + "\" has been accessed, which raises a warning in this context");
        log.warn(warningMessage);

    }

    

    
}
