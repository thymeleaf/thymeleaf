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
package org.thymeleaf.templateengine.springintegration;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateengine.springintegration.context.ErrorsSpringIntegrationWebProcessingContextBuilder;
import org.thymeleaf.templateengine.springintegration.context.SpringIntegrationWebProcessingContextBuilder;
import org.thymeleaf.templateengine.springintegration.dialect.binding.BindingDialect;
import org.thymeleaf.testing.templateengine.context.IProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.testing.templateengine.spring5.context.web.SpringMVCWebProcessingContextBuilder;
import org.thymeleaf.util.SpringStandardDialectUtils;
import org.thymeleaf.util.ThrottleArgumentsProvider;


public class SpringIntegrationTest {



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testForm(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/form");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testFormCompiledSpEL(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/form");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testHiddenMarkers(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(false, true)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/hiddenmarkers");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testErrors(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/errors");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testBindingDialect(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(
                Arrays.asList(new IDialect[] {
                        SpringStandardDialectUtils.createSpringStandardDialectInstance(),
                        new BindingDialect() }));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/bindingdialect");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testErrorsCompiledSpEL(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/errors");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testBindingDialectCompiledSpEL(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(
                Arrays.asList(new IDialect[] {
                        SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false),
                        new BindingDialect() }));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/bindingdialect");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testBeans(final int throttleStep) throws Exception {

        final IProcessingContextBuilder processingContextBuilder =
                new SpringIntegrationWebProcessingContextBuilder("classpath:templateengine/springintegration/applicationContext-beans.xml");
        final TestExecutor executor = TestExecutorFactory.createTestExecutor(processingContextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/beans");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testBeansCompiledSpEL(final int throttleStep) throws Exception {

        final IProcessingContextBuilder processingContextBuilder =
                new SpringIntegrationWebProcessingContextBuilder("classpath:templateengine/springintegration/applicationContext-beans.xml");
        final TestExecutor executor = TestExecutorFactory.createTestExecutor(processingContextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/beans");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testExpression(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/expression");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testExpressionCompiledSpEL(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/expression");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testMvc(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/mvc/applicationContext.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/mvc");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testMvcCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/mvc/applicationContext.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/mvc");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testXmlNs(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation(null);

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/xmlns");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testXmlNsCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation(null);

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/xmlns");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataFormWith(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataFormWithCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataFormWithout(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwithout");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataFormWithoutCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwithout");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataUrlsWith(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswith");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataUrlsWithCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataUrlsWithout(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswithout");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestDataUrlsWithoutCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswithout");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestUrlsExpOobject(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlsexpobject");

        Assertions.assertTrue(executor.isAllOK());

    }



    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRequestUrlsExpOobjectCompiledSpEL(final int throttleStep) throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlsexpobject");

        Assertions.assertTrue(executor.isAllOK());

    }



}
