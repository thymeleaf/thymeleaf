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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateengine.aggregation.dialect.Dialect01;
import org.thymeleaf.templateengine.features.elementstack.ElementStackDialect;
import org.thymeleaf.templateengine.features.interaction.InteractionDialect01;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.util.ThrottledWebTestExecutorArgumentsProvider;


public class FeaturesTest {



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testText(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-text.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testLink(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-link.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testUtil(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-util.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testExpression(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-expression.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testMessages(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-messages.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testServletContext(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-servletcontext.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testSession(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-session.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testNormalization(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-normalization.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testExecInfo(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-execinfo.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testAccessRestrictions(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-accessrestrictions.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInliningStandard(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-inlining-standard.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInliningNoStandard(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new Dialect01()}));
        executor.execute("classpath:templateengine/features/features-inlining-nostandard.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInliningInteraction(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new InteractionDialect01()}));
        executor.execute("classpath:templateengine/features/features-inlining-interaction.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testLazy(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/features/features-lazy.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testElementStack(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new ElementStackDialect()}));
        executor.execute("classpath:templateengine/features/features-elementstack.thindex");

        Assertions.assertTrue(executor.isAllOK());

    }





}
