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
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.standard.resolver.StandardClassPathTestableResolver;





public class TestExecutorTest {
    
    
    
    
    public TestExecutorTest() {
        super();
    }
    
    
    
    
    
    
    
    @Test
    public void testExecutor() throws Exception {
        
        try {

            final ITestableResolver resolver = new StandardClassPathTestableResolver();
            
            final TestExecutor executor = new TestExecutor();
            executor.setTestableResolver(resolver);
            executor.execute("test");
//            executor.execute("maintests");
            
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        
        
    }
    
    
    
}
