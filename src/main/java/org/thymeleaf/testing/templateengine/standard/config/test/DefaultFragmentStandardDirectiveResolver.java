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

import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;


public class DefaultFragmentStandardDirectiveResolver extends AbstractStandardDirectiveResolver<IFragmentSpec> {

    
    public static final DefaultFragmentStandardDirectiveResolver INSTANCE = new DefaultFragmentStandardDirectiveResolver();
    public static final IFragmentSpec DEFAULT_VALUE = WholeFragmentSpec.INSTANCE; 

    
    private DefaultFragmentStandardDirectiveResolver() {
        super(IFragmentSpec.class);
    }


    @Override
    protected IFragmentSpec getValue(final String executionId, final String documentName, 
            final String directiveName, final String directiveValue, 
            final String directiveQualifier) {

        if (directiveValue == null || directiveValue.trim().equals("")) {
            return DEFAULT_VALUE;
        }
        
        return new DOMSelectorFragmentSpec(directiveValue.trim());
        
    }
    
    
}
