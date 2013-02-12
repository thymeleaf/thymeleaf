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

import org.thymeleaf.testing.templateengine.builder.ITestIteratorBuilder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.testing.templateengine.test.TestIterator;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestIteratorBuilder implements ITestIteratorBuilder {
    
    private final String iteratorName;
    private final int iterations;
    
    
    protected AbstractStandardTestIteratorBuilder(final String iteratorName, final int iterations) {
        super();
        this.iteratorName = iteratorName;
        this.iterations = iterations;
    }
    
    
    public String getIteratorName() {
        return this.iteratorName;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    

    
    protected abstract ITestable getIteratedElement(final String executionId);
    
    
    public final ITestIterator build(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");

        final ITestable iteratedElement = getIteratedElement(executionId);
        if (iteratedElement == null) {
            throw new TestEngineExecutionException(
                    executionId, "Computation of iterated element returned null");
        }
        
        final TestIterator testIterator = new TestIterator(iteratedElement, this.iterations);
        if (this.iteratorName != null) {
            testIterator.setName(this.iteratorName);
        }
        
        return testIterator;
        
    }
    
}
