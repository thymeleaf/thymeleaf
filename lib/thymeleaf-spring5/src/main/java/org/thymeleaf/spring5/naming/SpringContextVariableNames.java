/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.naming;


/**
 * <p>
 *   Static class holding the names of context variables meant to have a special
 *   use inside the framework.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public final class SpringContextVariableNames {
    
    
    public static final String SPRING_REQUEST_CONTEXT = "springRequestContext";
    public static final String THYMELEAF_REQUEST_CONTEXT = "thymeleafRequestContext";

    public static final String THYMELEAF_FIELD_BIND_STATUS = "thymeleafFieldBindStatus";

    public static final String SPRING_BOUND_OBJECT_EXPRESSION = "springBoundObjectExpression";


    
    private SpringContextVariableNames() {
        super();
    }
    
}
