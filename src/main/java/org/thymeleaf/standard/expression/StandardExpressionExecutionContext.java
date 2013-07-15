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
package org.thymeleaf.standard.expression;







/**
 * <p>
 *   Context class that contains several conditions that might be of interest to the
 *   expression executor (like for instance, whether the expression comes from 
 *   preprocessing or not)
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.16
 *
 */
public final class StandardExpressionExecutionContext {

    public static final StandardExpressionExecutionContext NORMAL = new StandardExpressionExecutionContext(false);
    public static final StandardExpressionExecutionContext PREPROCESSING = new StandardExpressionExecutionContext(true);
    public static final StandardExpressionExecutionContext UNESCAPED_EXPRESSION = new StandardExpressionExecutionContext(true);

    private final boolean forbidRequestParameters;
    
    
    
    public StandardExpressionExecutionContext(final boolean forbidRequestParameters) {
        super();
        this.forbidRequestParameters = forbidRequestParameters;
    }
    
    
    public boolean getForbidRequestParameters() {
        return this.forbidRequestParameters;
    }
    
}
