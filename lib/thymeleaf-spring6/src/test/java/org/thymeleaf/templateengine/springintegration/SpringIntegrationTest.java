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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateengine.springintegration.context.ErrorsSpringIntegrationWebProcessingContextBuilder;
import org.thymeleaf.templateengine.springintegration.context.SpringIntegrationWebProcessingContextBuilder;
import org.thymeleaf.templateengine.springintegration.dialect.binding.BindingDialect;
import org.thymeleaf.testing.templateengine.context.IProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.spring6.context.web.SpringMVCWebProcessingContextBuilder;
import org.thymeleaf.tests.util.TestExecutorFactory;
import org.thymeleaf.util.SpringStandardDialectUtils;


@RunWith(Parameterized.class)
public class SpringIntegrationTest {



    private final int throttleStep;


    public SpringIntegrationTest(final Integer throttleStep) {
        super();
        this.throttleStep = throttleStep.intValue();
    }


    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {

        final int[] throttleSteps = new int[] { Integer.MAX_VALUE, 1000, 100, 11, 9, 5, 1};

        final List<Object[]> params = new ArrayList<Object[]>();
        for (int i = 0; i < throttleSteps.length; i++) {
            params.add(new Object[] { Integer.valueOf(i) });
        }
        return params;

    }




    @Test
    public void testForm() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/form");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testFormCompiledSpEL() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/form");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testHiddenMarkers() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(false, true)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/hiddenmarkers");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testErrors() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/errors");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testBindingDialect() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(
                Arrays.asList(new IDialect[] {
                        SpringStandardDialectUtils.createSpringStandardDialectInstance(),
                        new BindingDialect() }));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/bindingdialect");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testErrorsCompiledSpEL() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/errors");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testBindingDialectCompiledSpEL() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new ErrorsSpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(
                Arrays.asList(new IDialect[] {
                        SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false),
                        new BindingDialect() }));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/bindingdialect");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testBeans() throws Exception {

        final IProcessingContextBuilder processingContextBuilder =
                new SpringIntegrationWebProcessingContextBuilder("classpath:templateengine/springintegration/applicationContext-beans.xml");
        final TestExecutor executor = TestExecutorFactory.createTestExecutor(processingContextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/beans");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testBeansCompiledSpEL() throws Exception {

        final IProcessingContextBuilder processingContextBuilder =
                new SpringIntegrationWebProcessingContextBuilder("classpath:templateengine/springintegration/applicationContext-beans.xml");
        final TestExecutor executor = TestExecutorFactory.createTestExecutor(processingContextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/beans");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testExpression() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/expression");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testExpressionCompiledSpEL() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(new SpringIntegrationWebProcessingContextBuilder());
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/expression");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testMvc() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/mvc/applicationContext.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/mvc");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testMvcCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/mvc/applicationContext.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/mvc");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testXmlNs() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation(null);

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/xmlns");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testXmlNsCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation(null);

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/xmlns");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataFormWith() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataFormWithCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataFormWithout() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwithout");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataFormWithoutCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/formwithout");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testRequestDataUrlsWith() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswith");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testRequestDataUrlsWithCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswith");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataUrlsWithout() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswithout");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testRequestDataUrlsWithoutCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-without.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlswithout");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testRequestUrlsExpOobject() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlsexpobject");

        Assertions.assertTrue(executor.isAllOK());

    }



    @Test
    public void testRequestUrlsExpOobjectCompiledSpEL() throws Exception {

        final SpringMVCWebProcessingContextBuilder contextBuilder = new SpringMVCWebProcessingContextBuilder();
        contextBuilder.setApplicationContextConfigLocation("classpath:templateengine/springintegration/requestdata/applicationContext-with.xml");

        final TestExecutor executor = TestExecutorFactory.createTestExecutor(contextBuilder);
        executor.setDialects(Arrays.asList(new IDialect[] { SpringStandardDialectUtils.createSpringStandardDialectInstance(true, false)}));
        executor.setThrottleStep(this.throttleStep);
        executor.execute("classpath:templateengine/springintegration/requestdata/urlsexpobject");

        Assertions.assertTrue(executor.isAllOK());

    }



}
