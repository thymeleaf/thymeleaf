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

import org.thymeleaf.testing.templateengine.builder.ITestParallelizerBuilder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITestParallelizer;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.testing.templateengine.test.TestParallelizer;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestParallelizerBuilder implements ITestParallelizerBuilder {
    
    private final String parallelizerName;
    private final int numThreads;
    
    
    protected AbstractStandardTestParallelizerBuilder(final String parallelizerName, final int numThreads) {
        super();
        this.parallelizerName = parallelizerName;
        this.numThreads = numThreads;
    }
    
    
    public String getParallelizerName() {
        return this.parallelizerName;
    }
    
    public int getNumThreads() {
        return this.numThreads;
    }
    

    
    protected abstract ITestable getParallelizedElement(final String executionId);
    
    
    public final ITestParallelizer build(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");

        final ITestable parallelizedElement = getParallelizedElement(executionId);
        if (parallelizedElement == null) {
            throw new TestEngineExecutionException(
                    executionId, "Computation of parallelized element returned null");
        }
        
        final TestParallelizer testParallelizer = new TestParallelizer(parallelizedElement, this.numThreads);
        if (this.parallelizerName != null) {
            testParallelizer.setName(this.parallelizerName);
        }
        
        return testParallelizer;
        
    }
    
}
