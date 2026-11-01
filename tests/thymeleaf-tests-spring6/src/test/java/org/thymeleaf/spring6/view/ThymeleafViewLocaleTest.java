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
import java.util.Locale;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class ThymeleafViewLocaleTest {


    @Test
    public void testWebExchangeLocaleIsTheViewLocale() throws Exception {

        // The view locale (the one resolved by Spring), the request locale and the response locale all differ
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final StringWriter output = new StringWriter();

        Mockito.when(servletContext.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());

        final StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        applicationContext.refresh();

        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(request.getLocale()).thenReturn(new Locale("es", "ES"));
        Mockito.when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getParameterMap()).thenReturn(Collections.emptyMap());
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getContextPath()).thenReturn("");
        Mockito.when(request.getRequestURI()).thenReturn("/");
        Mockito.when(request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(applicationContext);
        Mockito.when(response.getLocale()).thenReturn(Locale.US);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(output));

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final ThymeleafView view = new ThymeleafView();
        view.setApplicationContext(applicationContext);
        view.setTemplateEngine(templateEngine);
        view.setTemplateName("<p th:text=\"${#ctx.getExchange().getLocale()}\">..</p>");
        view.setLocale(Locale.FRANCE);

        view.render(Collections.emptyMap(), request, response);

        Assertions.assertEquals("<p>fr_FR</p>", output.toString());

    }


}
