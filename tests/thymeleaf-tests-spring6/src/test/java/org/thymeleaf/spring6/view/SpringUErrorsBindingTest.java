/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring6.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class SpringUErrorsBindingTest {

    private static final String TEMPLATE =
            "<form th:object=\"${form}\"><p th:uerrors=\"*{age}\">..</p><p th:errors=\"*{age}\">..</p></form>";


    public static class Form {
        private Integer age;
        public Integer getAge() { return this.age; }
        public void setAge(final Integer age) { this.age = age; }
    }


    private static String render(final String submittedValue, final String typeMismatchMessage) throws Exception {

        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final StringWriter output = new StringWriter();

        Mockito.when(servletContext.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());

        final StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        if (typeMismatchMessage != null) {
            applicationContext.getStaticMessageSource().addMessage("typeMismatch", Locale.US, typeMismatchMessage);
        }
        applicationContext.refresh();

        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(request.getLocale()).thenReturn(Locale.US);
        // Request attributes hold the context variables, so they need to be stored
        final Map<String,Object> attributes = new HashMap<String,Object>();
        attributes.put(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
        Mockito.when(request.getAttribute(Mockito.anyString()))
                .thenAnswer(invocation -> attributes.get(invocation.getArgument(0, String.class)));
        Mockito.doAnswer(invocation -> attributes.put(invocation.getArgument(0, String.class), invocation.getArgument(1)))
                .when(request).setAttribute(Mockito.anyString(), Mockito.any());
        Mockito.doAnswer(invocation -> attributes.remove(invocation.getArgument(0, String.class)))
                .when(request).removeAttribute(Mockito.anyString());
        Mockito.when(request.getAttributeNames())
                .thenAnswer(invocation -> Collections.enumeration(new java.util.ArrayList<String>(attributes.keySet())));
        Mockito.when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getParameterMap()).thenReturn(Collections.emptyMap());
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getContextPath()).thenReturn("");
        Mockito.when(request.getRequestURI()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(output));

        final Form form = new Form();
        final DataBinder binder = new DataBinder(form, "form");
        final Map<String,Object> values = new HashMap<String,Object>();
        values.put("age", submittedValue);
        binder.bind(new MutablePropertyValues(values));
        final BindingResult bindingResult = binder.getBindingResult();
        Assertions.assertTrue(bindingResult.hasFieldErrors("age"));

        final Map<String,Object> model = new HashMap<String,Object>();
        model.put("form", form);
        model.put(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final ThymeleafView view = new ThymeleafView();
        view.setApplicationContext(applicationContext);
        view.setTemplateEngine(templateEngine);
        view.setTemplateName(TEMPLATE);
        view.setLocale(Locale.US);
        view.render(model, request, response);

        return output.toString();

    }


    private static String paragraph(final String output, final int index) {
        int start = -1;
        for (int i = 0; i <= index; i++) {
            start = output.indexOf("<p>", start + 1);
        }
        return output.substring(start + 3, output.indexOf("</p>", start));
    }


    @Test
    public void testDefaultMessageWithSubmittedMarkup() throws Exception {
        // Spring removes whitespace from numbers before converting them, so the submitted markup has none
        final String output = render("<i>1</i><span>2</span><img><a>3</a><u>4", null);
        final String uerrors = output.substring(output.indexOf("<p>") + 3, output.lastIndexOf("</p><p>"));
        final String errors = output.substring(output.lastIndexOf("<p>") + 3, output.lastIndexOf("</p>"));
        // Default message includes the submitted value: only formatting elements remain, all of them closed
        Assertions.assertTrue(uerrors.contains("<i>1</i><span>2</span>3<u>4"), uerrors);
        Assertions.assertTrue(uerrors.endsWith("</u>"), uerrors);
        Assertions.assertFalse(uerrors.contains("<img"), uerrors);
        Assertions.assertFalse(uerrors.contains("<a"), uerrors);
        Assertions.assertFalse(errors.contains("<"), errors);
        Assertions.assertTrue(errors.contains("&lt;i&gt;1&lt;/i&gt;"), errors);
    }


    @Test
    public void testDefaultMessageWithUnbalancedMarkup() throws Exception {
        final String output = render("<b>1<script>x()</script><style>", null);
        final String uerrors = output.substring(output.indexOf("<p>") + 3, output.lastIndexOf("</p><p>"));
        Assertions.assertFalse(uerrors.contains("script"), uerrors);
        Assertions.assertFalse(uerrors.contains("x()"), uerrors);
        Assertions.assertTrue(uerrors.endsWith("</b>"), uerrors);
        Assertions.assertEquals(1, output.split("<form>", -1).length - 1);
        Assertions.assertTrue(output.endsWith("</p></form>"), output);
    }


    @Test
    public void testResolvedMessageWithFormatting() throws Exception {
        final String output = render("abc", "<b>Invalid</b> <em>number</em><br/><span class=\"hint\">digits only</span>");
        Assertions.assertEquals(
                "<form><p><b>Invalid</b> <em>number</em><br /><span class=\"hint\">digits only</span></p>"
                        + "<p>&lt;b&gt;Invalid&lt;/b&gt; &lt;em&gt;number&lt;/em&gt;&lt;br/&gt;&lt;span class=&quot;hint&quot;&gt;digits only&lt;/span&gt;</p></form>",
                output);
    }

}
