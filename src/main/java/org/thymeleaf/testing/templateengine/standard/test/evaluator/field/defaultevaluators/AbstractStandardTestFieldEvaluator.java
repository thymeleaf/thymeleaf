/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestRawData;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.IStandardTestFieldEvaluator;
import org.thymeleaf.util.Validate;




public abstract class AbstractStandardTestFieldEvaluator implements IStandardTestFieldEvaluator {

    private final Class<?> expectedClass;
    
    protected AbstractStandardTestFieldEvaluator(final Class<?> expectedClass) {
        super();
        Validate.notNull(expectedClass, "Expected class cannot be null");
        this.expectedClass = expectedClass;
    }

    
    public final Class<?> getValueClass() {
        return this.expectedClass;
    }

    public final StandardTestEvaluatedField getValue(final String executionId, final StandardTestRawData data, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier) {

        final StandardTestEvaluatedField evaluation =
                getValue(executionId, data.getTestResource(), testResourceResolver, fieldName, fieldQualifier, data.getValueForFieldAndQualifier(fieldName, fieldQualifier));
        
        if (evaluation != null && evaluation.hasNotNullValue()) {
            
            final Object value = evaluation.getValue();
                
            final Class<?> valueClass = getValueClass();
            if (!valueClass.isAssignableFrom(value.getClass())) {
                // Value returned is not of the correct class
                throw new TestEngineExecutionException(
                        "A value of class \"" + value.getClass().getName() + "\" resulted from evaluation " +
                        "of field \"" + fieldName + "\" in " +
                        "\"" + data.getTestResource().getName() + "\", but value was expected to be of class " +
                        "\"" + valueClass.getName() + "\"");
            }
            
        }
        
        return evaluation;
        
    }

    
    protected abstract StandardTestEvaluatedField getValue(
            final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue);

    
}
