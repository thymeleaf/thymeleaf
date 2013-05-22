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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import java.io.File;

import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.resource.LocalFileTestResource;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;


public abstract class AbstractTestResourceStandardTestFieldEvaluator 
        extends AbstractStandardTestFieldEvaluator {

    
    protected AbstractTestResourceStandardTestFieldEvaluator() {
        super(ITestResource.class);
    }


    @Override
    protected final StandardTestEvaluatedField getValue(final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {

        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forNoValue();
        }

        String value = fieldValue.trim();
        
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1);
            final ITestResource newResource = 
                    testResourceResolver.resolve(value, resource);
            return StandardTestEvaluatedField.forSpecifiedValue(newResource);      
        }
        
        final File tempFile =
                ResourceUtils.createTempFile(executionId, getFileSuffix(), value, "UTF-8");
        final ITestResource tempResource = new LocalFileTestResource(tempFile, "UTF-8");
        
        return StandardTestEvaluatedField.forSpecifiedValue(tempResource);      
        
    }
    
    
    
    protected abstract String getFileSuffix();
    
}
