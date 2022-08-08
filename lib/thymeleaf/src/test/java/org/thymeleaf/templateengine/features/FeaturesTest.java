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
package org.thymeleaf.templateengine.features;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.TestExecutorFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateengine.ThrottleArgumentsProvider;
import org.thymeleaf.templateengine.aggregation.dialect.Dialect01;
import org.thymeleaf.templateengine.features.elementstack.ElementStackDialect;
import org.thymeleaf.templateengine.features.interaction.InteractionDialect01;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class FeaturesTest {



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testText(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/text");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testLink(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/link");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testUtil(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/util");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testExpression(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/expression");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testMessages(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/messages");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testServletContext(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/servletcontext");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testSession(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/session");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testNormalization(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/normalization");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testExecInfo(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/execinfo");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testAccessRestrictions(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/accessrestrictions");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInstanceStaticRestrictions(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/instancestaticrestrictions");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInliningStandard(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/inlining/standard");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInliningNoStandard(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new Dialect01()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/inlining/nostandard");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInliningInteraction(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new InteractionDialect01()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/inlining/interaction");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testLazy(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/lazy");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testElementStack(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new ElementStackDialect()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/features/elementstack");

        Assertions.assertTrue(executor.isAllOK());

    }





}
