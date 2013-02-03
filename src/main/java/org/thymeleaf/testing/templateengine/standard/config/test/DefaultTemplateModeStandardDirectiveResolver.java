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
package org.thymeleaf.testing.templateengine.standard.config.test;

import java.util.List;

import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.ITestable;





public class DefaultTemplateModeStandardDirectiveResolver extends AbstractStandardDirectiveResolver<String> {

    
    public static final DefaultTemplateModeStandardDirectiveResolver INSTANCE = new DefaultTemplateModeStandardDirectiveResolver();
    public static final String DEFAULT_VALUE = "HTML5"; 

    
    private DefaultTemplateModeStandardDirectiveResolver() {
        super(String.class);
    }
    

    public String getValue(final ITestSuite suite, final List<ITestable> path, final String fileName, 
            final String directiveName, final String directiveValue) {
        
        if (directiveValue == null) {
            return DEFAULT_VALUE;
        }
        
        return directiveValue.trim();
        
    }
    
}
