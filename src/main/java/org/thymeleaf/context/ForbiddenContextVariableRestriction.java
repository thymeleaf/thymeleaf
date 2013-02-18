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

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Forbids access to a specific variable in the map.
 * </p>
 * 
 * @since 2.0.16
 *
 */
public class ForbiddenContextVariableRestriction implements IContextVariableRestriction {

    
    private final String targetVariableName;
    private final String message;
    
    
    public ForbiddenContextVariableRestriction(final String targetVariableName) {
        this(targetVariableName, null);
    }
    
    public ForbiddenContextVariableRestriction(final String targetVariableName, final String message) {
        super();
        Validate.notNull(targetVariableName, "Variable name cannot be null");
        this.targetVariableName = targetVariableName;
        this.message = message;
    }

    
    public void checkAccess(final VariablesMap<?, ?> variablesMap, final String variableName) {

        if (!this.targetVariableName.equals(variableName)) {
            return;
        }
        
        final String exceptionMessage = 
                (this.message != null ? 
                        this.message : 
                        "Access to variable \"" + variableName + "\" is forbidden in this context");
        throw new TemplateProcessingException(exceptionMessage);
        
    }

    

    
}
