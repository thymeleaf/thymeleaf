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
package org.thymeleaf.testing.templateengine.test;





public class TestIterator 
        extends AbstractTestable 
        implements ITestIterator {
    

    public static final int DEFAULT_ITERATIONS = 1;
    
    private int iterations = DEFAULT_ITERATIONS;
    private final ITestable iteratedElement;
    
    
    public TestIterator(final ITestable iteratedElement) {
        super();
        this.iteratedElement = iteratedElement;
    }


    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }

    
    
    public ITestable getIteratedElement() {
        return this.iteratedElement;
    }
    
}
