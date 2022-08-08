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
package org.thymeleaf.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.testing.templateengine.util.JakartaServletMockUtils;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class LazyContextVariableTest {


    private static final String TEMPLATE1 = "<[# th:if='${doit}'][[${lazz}]][/]>";
    private static final TemplateSpec TEMPLATE_SPEC1 = new TemplateSpec(TEMPLATE1, TemplateMode.TEXT);

    private static final String TEMPLATE2 = "<[# th:if='${doit}'][[${'Hey, ' + lazz}]][/]>";
    private static final TemplateSpec TEMPLATE_SPEC2 = new TemplateSpec(TEMPLATE2, TemplateMode.TEXT);

    private static final String TEMPLATE3 = "<[# th:if='${doit}'][['Hey, ' + ${lazz}]][/]>";
    private static final TemplateSpec TEMPLATE_SPEC3 = new TemplateSpec(TEMPLATE3, TemplateMode.TEXT);

    private static final String TEMPLATE4 = "<[# th:if='${doit}' th:text='${lazz}']...[/]>";
    private static final TemplateSpec TEMPLATE_SPEC4 = new TemplateSpec(TEMPLATE4, TemplateMode.TEXT);

    private static final String TEMPLATE9 = "<[# th:if='${doit}' th:text='${session.lazz}']...[/]>";
    private static final TemplateSpec TEMPLATE_SPEC9 = new TemplateSpec(TEMPLATE9, TemplateMode.TEXT);

    private static final String TEMPLATE10 = "<[# th:if='${doit}' th:text='${application.lazz}']...[/]>";
    private static final TemplateSpec TEMPLATE_SPEC10 = new TemplateSpec(TEMPLATE10, TemplateMode.TEXT);

    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine();


    @Test
    public void testLazyContextVariable01() throws Exception {

        final Context contextTrue = new Context(Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final Context contextFalse = new Context(Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC1, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC1, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable02() throws Exception {

        final Context contextTrue = new Context(Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final Context contextFalse = new Context(Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hey, Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC2, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC2, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable03() throws Exception {

        final Context contextTrue = new Context(Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final Context contextFalse = new Context(Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hey, Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC3, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC3, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable04() throws Exception {

        final Context contextTrue = new Context(Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final Context contextFalse = new Context(Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC4, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC4, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable05() throws Exception {

        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);
        
        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC1, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC1, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable06() throws Exception {

        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);

        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hey, Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC2, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC2, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable07() throws Exception {

        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);

        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hey, Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC3, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC3, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable08() throws Exception {

        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);

        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        contextTrue.setVariable("lazz", lazyTrue);


        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        contextFalse.setVariable("lazz", lazyFalse);


        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC4, contextTrue));
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC4, contextFalse));

        Assertions.assertTrue(lazyTrue.initialized);
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable09() throws Exception {

        final Map<String, Object> sessionAttrs = new HashMap<String, Object>();
        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final HttpSession session = JakartaServletMockUtils.buildSession(servletContext).attributeMap(sessionAttrs).build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").session(session).build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);

        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        sessionAttrs.put("lazz", lazyTrue);

        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC9, contextTrue));
        Assertions.assertTrue(lazyTrue.initialized);

        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        sessionAttrs.put("lazz", lazyFalse);


        Assertions.assertFalse(lazyFalse.initialized);
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC9, contextFalse));
        Assertions.assertFalse(lazyFalse.initialized);

    }


    @Test
    public void testLazyContextVariable10() throws Exception {

        final Map<String, Object> servletContextAttrs = new HashMap<String, Object>();
        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttrs).build();
        final HttpServletRequest request =
                JakartaServletMockUtils.buildRequest(servletContext, "/something").build();
        final HttpServletResponse response = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);

        final WebContext contextTrue = new WebContext(webExchange, Locale.US);
        contextTrue.setVariable("doit", "true");
        final Lazy lazyTrue = new Lazy();
        servletContextAttrs.put("lazz", lazyTrue);

        Assertions.assertFalse(lazyTrue.initialized);
        Assertions.assertEquals("<Hello there!>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC10, contextTrue));
        Assertions.assertTrue(lazyTrue.initialized);

        final WebContext contextFalse = new WebContext(webExchange, Locale.US);
        contextFalse.setVariable("doit", "false");
        final Lazy lazyFalse = new Lazy();
        servletContextAttrs.put("lazz", lazyFalse);


        Assertions.assertFalse(lazyFalse.initialized);
        Assertions.assertEquals("<>", TEMPLATE_ENGINE.process(TEMPLATE_SPEC10, contextFalse));
        Assertions.assertFalse(lazyFalse.initialized);

    }



    static final class Lazy extends LazyContextVariable<String> {

        boolean initialized = false;

        @Override
        protected String loadValue() {
            this.initialized = true;
            return "Hello there!";
        }

    }


}
