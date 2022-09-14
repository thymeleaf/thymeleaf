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
package org.thymeleaf.templateengine.processors;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.util.ThrottleArgumentsProvider;
import org.thymeleaf.templateengine.processors.dialects.noop.NoOpDialect;
import org.thymeleaf.templateengine.processors.dialects.remove.RemoveDialect;
import org.thymeleaf.templateengine.processors.dialects.replacewithnonprocessable.ReplaceWithNonProcessableDialect;
import org.thymeleaf.templateengine.processors.dialects.replacewithprocessable.ReplaceWithProcessableDialect;
import org.thymeleaf.templateengine.processors.dialects.surround.SurroundDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class ProcessorsTest {




    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testReplaceWithProcessable(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new ReplaceWithProcessableDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/processors/replacewithprocessable");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testReplaceWithNonProcessable(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new ReplaceWithNonProcessableDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/processors/replacewithnonprocessable");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRemove(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new RemoveDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/processors/remove");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testNoOp(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new NoOpDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/processors/noop");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testSurround(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new SurroundDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/processors/surround");

        Assertions.assertTrue(executor.isAllOK());

    }



}
