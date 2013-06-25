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
package org.thymeleaf.engine.stsm;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.stsm.context.STSMWebProcessingContextBuilder;
import org.thymeleaf.spring3.dialect.SpringStandardDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;





public class STSMTest {
    
    
    public STSMTest() {
        super();
    }
    
    
    
    
    @Test
    public void testSTSM() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.setProcessingContextBuilder(new STSMWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { new SpringStandardDialect()}));
        executor.execute("classpath:engine/stsm");
        
        Assert.assertTrue(executor.isAllOK());
        
        
    }
    
    
    
}
