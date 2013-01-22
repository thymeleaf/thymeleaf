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
package org.thymeleaf.testing.templateengine.engine;

import org.junit.Test;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestResource;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.StringTestResource;
import org.thymeleaf.testing.templateengine.test.SuccessExpectedTest;
import org.thymeleaf.testing.templateengine.test.TestIterator;
import org.thymeleaf.testing.templateengine.test.TestSuite;





public class TestExecutorTest {
    
    
    
    
    public TestExecutorTest() {
        super();
    }
    
    
    
    
    
    
    
    @Test
    public void testExecutor() throws Exception {
        
        try {
            
            final ITestResource res0 = new StringTestResource("hello!");
            final ITestResource res1 = new StringTestResource("goodbye!");
            final ITestResource res2 = new StringTestResource("<span th:text=\"${'hey!'}\">cucu</span>");
            final ITestResource res3 = new StringTestResource("<span>hey!</span>");
            
            final ITest test0 = new SuccessExpectedTest(res1, true, res0);
            final ITest test1 = new SuccessExpectedTest(res2, false, res0);
            final ITest test2 = new SuccessExpectedTest(res2, false, res3);
            
            final ITestIterator iter2 = new TestIterator(test2, 2);
            
//            final ITestSuite testSuite = new TestSuite("testing01", test0, test2, test1, test2, iter2);
            final ITestSuite testSuite = new TestSuite("testing01", iter2);

            
            TestExecutor.execute(testSuite);
            
            
            
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        
        
    }
    
    
    
    
    
}
