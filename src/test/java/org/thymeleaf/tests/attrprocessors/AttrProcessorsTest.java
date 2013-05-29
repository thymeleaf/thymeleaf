/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.tests.attrprocessors;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;





public class AttrProcessorsTest {
    
    
    public AttrProcessorsTest() {
        super();
    }
    
    
    
    
    @Test
    public void testRemove() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:attrprocessors/remove");
        
        Assert.assertTrue(executor.getReporter().isAllOK());
        
    }
    
    @Test
    public void testIf() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:attrprocessors/if");
        
        Assert.assertTrue(executor.getReporter().isAllOK());
        
    }
    
    @Test
    public void testUnless() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:attrprocessors/unless");
        
        Assert.assertTrue(executor.getReporter().isAllOK());
        
    }
    
    @Test
    public void testInline() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:attrprocessors/inline");
        
        Assert.assertTrue(executor.getReporter().isAllOK());
        
    }
    
    
}
