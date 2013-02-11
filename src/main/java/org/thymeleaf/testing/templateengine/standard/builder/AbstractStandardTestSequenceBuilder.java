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
package org.thymeleaf.testing.templateengine.standard.builder;

import java.util.List;

import org.thymeleaf.testing.templateengine.builder.ITestBuilder;
import org.thymeleaf.testing.templateengine.builder.ITestSequenceBuilder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.TestSequence;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestSequenceBuilder implements ITestSequenceBuilder {
    
    protected AbstractStandardTestSequenceBuilder() {
        super();
    }
    

    
    protected abstract List<ITestBuilder> getTestBuilders(final String executionId);
    
    
    public final ITestSequence buildTestSequence(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");

        final List<ITestBuilder> testBuilders = getTestBuilders(executionId);
        if (testBuilders == null || testBuilders.isEmpty()) {
            throw new TestEngineExecutionException(
                    executionId, "Test builder list was returned empty");
        }

        final TestSequence testSequence = new TestSequence();
        for (final ITestBuilder testBuilder : testBuilders) {
            final ITest test = testBuilder.buildTest(executionId);
            if (test == null) {
                throw new TestEngineExecutionException(
                        executionId, "Test builder returned a null test object");
            }
            testSequence.addElement(test);
        }
        
        return testSequence;
        
    }
    
}
