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
package org.thymeleaf.testing.templateengine.testable;

import org.thymeleaf.util.Validate;





public class TestParallelizer 
        extends AbstractTestable 
        implements ITestParallelizer {
    

    private final int numThreads;
    private final ITestable parallelizedElement;
    
    
    public TestParallelizer(final ITestable parallelizedElement, final int numThreads) {
        super();
        Validate.notNull(parallelizedElement, "Parallelized element cannot be null");
        Validate.isTrue(numThreads > 0, "Number of threads must be more than zero");
        this.parallelizedElement = parallelizedElement;
        this.numThreads = numThreads;
    }


    public int getNumThreads() {
        return this.numThreads;
    }

    public ITestable getParallelizedElement() {
        return this.parallelizedElement;
    }

    
}
