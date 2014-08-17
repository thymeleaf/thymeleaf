/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.Arrays;
import java.util.List;

import org.thymeleaf.context.ForbiddenContextVariableRestriction;
import org.thymeleaf.context.IContextVariableRestriction;




/**
 * <p>
 *   Class containing constants that specify different sets of
 *   variable restrictions to be applied during Standard Expression 
 *   executions.
 * </p>
 * 
 * @since 2.0.16
 *
 */
public final class StandardVariableRestrictions {

    
    public static final List<IContextVariableRestriction> REQUEST_PARAMETERS_FORBIDDEN =
            Arrays.asList(new IContextVariableRestriction[] {
                    new ForbiddenContextVariableRestriction(
                            "param", 
                            "Accessing request parameters (param.*) is forbidden in preprocessing or unescaped expressions")
            });
    
    
    private StandardVariableRestrictions() {
        super();
    }
    
}
