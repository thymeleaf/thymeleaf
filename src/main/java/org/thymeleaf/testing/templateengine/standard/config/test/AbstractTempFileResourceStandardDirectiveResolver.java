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

import org.thymeleaf.testing.templateengine.standard.util.StandardTestIOUtils;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;


public abstract class AbstractTempFileResourceStandardDirectiveResolver extends AbstractStandardDirectiveResolver<ITestResource> {

    
    public static final ITestResource DEFAULT_VALUE = null; 

    
    protected AbstractTempFileResourceStandardDirectiveResolver() {
        super(ITestResource.class);
    }


    @Override
    protected final ITestResource getValue(final String executionId, final String documentName, 
            final String directiveName, final String directiveValue, final String directiveQualifier) {

        if (directiveValue == null || directiveValue.trim().equals("")) {
            return DEFAULT_VALUE;
        }

        return StandardTestIOUtils.createResource(executionId, getFileSuffix(), directiveValue);      
        
    }

    
    protected abstract String getFileSuffix();
    
}
