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
package org.thymeleaf.templateengine.conversion.conversion2;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.testing.templateengine.spring5.context.web.SpringMVCWebProcessingContextBuilder;
import org.thymeleaf.util.SpringStandardDialectUtils;


public class Conversion2Test {



    public Conversion2Test() {
        super();
    }






    @Test
    public void testConversion() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/conversion/conversion2/applicationContext.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[]{SpringStandardDialectUtils.createSpringStandardDialectInstance()}));

        executor.execute("classpath:templateengine/conversion/conversion2");

        Assertions.assertTrue(executor.isAllOK());

    }


}
