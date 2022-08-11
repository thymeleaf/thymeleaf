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
package org.thymeleaf.templateengine.gtvg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.templateengine.ThrottleArgumentsProvider;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class GTVGTest {




    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testGTVGHTML5(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/gtvg_html5");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testGTVGXHTML(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/gtvg_xhtml");

        Assertions.assertTrue(executor.isAllOK());

    }


}
