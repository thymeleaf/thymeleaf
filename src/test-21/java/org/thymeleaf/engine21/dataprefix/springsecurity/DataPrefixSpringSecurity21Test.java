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
package org.thymeleaf.engine21.dataprefix.springsecurity;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.testing.templateengine.context.web.SpringSecurityWebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.tests.util.SpringSpecificVersionUtils;


public class DataPrefixSpringSecurity21Test {


    public DataPrefixSpringSecurity21Test() {
        super();
    }
    
    
    
    
    @Test
    public void testSpringSecurity() throws Exception {

        final SpringSecurityWebProcessingContextBuilder processingContextBuilder =
                new SpringSecurityWebProcessingContextBuilder();
        processingContextBuilder.setApplicationContextConfigLocation(
                "classpath:engine21/dataprefix/springsecurity/applicationContext-security.xml");
        
        final TestExecutor executor = new TestExecutor();
        executor.setProcessingContextBuilder(processingContextBuilder);
        executor.setDialects(
                Arrays.asList(new IDialect[] { SpringSpecificVersionUtils.createSpringStandardDialectInstance(), new SpringSecurityDialect()}));
        executor.execute("classpath:engine21/dataprefix/springsecurity");
        
        Assert.assertTrue(executor.isAllOK());
        
        
    }
    
    
    
}
