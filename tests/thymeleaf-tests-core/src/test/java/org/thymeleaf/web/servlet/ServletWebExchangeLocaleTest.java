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
package org.thymeleaf.web.servlet;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServletWebExchangeLocaleTest {


    @Test
    public void testJakartaServletExchangeLocale() {

        final jakarta.servlet.ServletContext servletContext = Mockito.mock(jakarta.servlet.ServletContext.class);
        final jakarta.servlet.http.HttpServletRequest request = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        final jakarta.servlet.http.HttpServletResponse response = Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);
        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(request.getLocale()).thenReturn(new Locale("es", "ES"));
        Mockito.when(response.getLocale()).thenReturn(Locale.US);

        final JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);

        Assertions.assertEquals(new Locale("es", "ES"), application.buildExchange(request, response).getLocale());
        Assertions.assertEquals(new Locale("es", "ES"), application.buildExchange(request, response, null).getLocale());
        Assertions.assertEquals(Locale.FRANCE, application.buildExchange(request, response, Locale.FRANCE).getLocale());

    }


    @Test
    public void testJavaxServletExchangeLocale() {

        final javax.servlet.ServletContext servletContext = Mockito.mock(javax.servlet.ServletContext.class);
        final javax.servlet.http.HttpServletRequest request = Mockito.mock(javax.servlet.http.HttpServletRequest.class);
        final javax.servlet.http.HttpServletResponse response = Mockito.mock(javax.servlet.http.HttpServletResponse.class);
        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(request.getLocale()).thenReturn(new Locale("es", "ES"));
        Mockito.when(response.getLocale()).thenReturn(Locale.US);

        final JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);

        Assertions.assertEquals(new Locale("es", "ES"), application.buildExchange(request, response).getLocale());
        Assertions.assertEquals(new Locale("es", "ES"), application.buildExchange(request, response, null).getLocale());
        Assertions.assertEquals(Locale.FRANCE, application.buildExchange(request, response, Locale.FRANCE).getLocale());

    }


}
