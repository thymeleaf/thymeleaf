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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;




public class DefaultExceptionTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    public static final DefaultExceptionTestFieldEvaluator INSTANCE = new DefaultExceptionTestFieldEvaluator();
    
    
    private DefaultExceptionTestFieldEvaluator() {
        super(Class.class);
    }


    @Override
    public StandardTestEvaluatedField getValue(
            final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forNoValue();
        }

        final String resourceName = (resource != null? resource.getName() : null);
        
        Class<?> specifiedClass = null;
        try {
            specifiedClass = Class.forName(fieldValue.trim());
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    "Exception initializing field \"" + fieldName + "\" in document " +
            		"\"" + resourceName + "\"", t);
        }
        
        if (!Throwable.class.isAssignableFrom(specifiedClass)) {
            throw new TestEngineExecutionException(
                    "Exception initializing field \"" + fieldName + "\" in document " +
                    "\"" + resourceName + "\": Class \"" + specifiedClass.getClass().getName() + "\" does not " +
                    "extend " + Throwable.class.getName());
        }
        
        return StandardTestEvaluatedField.forSpecifiedValue(specifiedClass);
        
    }

    
}
