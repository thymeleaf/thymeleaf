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

import org.thymeleaf.testing.templateengine.resource.FileTestResource;
import org.thymeleaf.testing.templateengine.resource.FileTestResourceResolver;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.util.TempFileUtils;


public abstract class AbstractTempFileResourceStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    protected AbstractTempFileResourceStandardTestFieldEvaluator() {
        super(ITestResource.class);
    }


    @Override
    protected final StandardTestEvaluatedField getValue(final String executionId, final String documentName, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {

        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forNoValue();
        }

        final FileTestResourceResolver resolver = FileTestResourceResolver.UTF8_RESOLVER;

        final File tempFile =
                TempFileUtils.createTempFile(executionId, getFileSuffix(), fieldValue, resolver.getCharacterEncoding());
        final ITestResource resource = new FileTestResource(tempFile, resolver);
        
        return StandardTestEvaluatedField.forSpecifiedValue(resource);      
        
    }
    
    
    
    protected abstract String getFileSuffix();
    
}
