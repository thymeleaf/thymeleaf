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
package org.thymeleaf.templateengine.elementprocessors;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateengine.elementprocessors.dialect.MarkupDialect;
import org.thymeleaf.templateengine.elementprocessors.dialect.PrecedenceDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.util.ThrottledWebTestExecutorArgumentsProvider;


public class ElementProcessorsTest {





    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testBlock(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/elementprocessors/block");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testElementMarkupProcessors(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new MarkupDialect()}));
        executor.execute("classpath:templateengine/elementprocessors/markup");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceModelBefore(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE - 1)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencemodelbefore");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceModelSame(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencemodelsame");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceModelAfter(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE + 1)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencemodelafter");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceTagBefore(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE - 1)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencetagbefore");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceTagSame(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencetagsame");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDialectPrecedenceTagAfter(final TestExecutor executor) throws Exception {

        executor.setDialects(Arrays.asList(new IDialect[]{new StandardDialect(), new PrecedenceDialect(StandardDialect.PROCESSOR_PRECEDENCE + 1)}));
        executor.execute("classpath:templateengine/elementprocessors/precedencetagafter");

        Assertions.assertTrue(executor.isAllOK());

    }

}
