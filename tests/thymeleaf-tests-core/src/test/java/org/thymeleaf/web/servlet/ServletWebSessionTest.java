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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thymeleaf.web.IWebSession;

public class ServletWebSessionTest {


    @Test
    public void testJakartaSessionCreatedAndInvalidatedAfterExchange() {

        final jakarta.servlet.ServletContext servletContext = Mockito.mock(jakarta.servlet.ServletContext.class);
        final jakarta.servlet.http.HttpServletRequest request = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        final jakarta.servlet.http.HttpServletResponse response = Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);
        final jakarta.servlet.http.HttpSession session = Mockito.mock(jakarta.servlet.http.HttpSession.class);
        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(session.getAttribute("user")).thenReturn("ana");

        final IWebSession webSession =
                JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response).getSession();
        Assertions.assertFalse(webSession.exists());
        Assertions.assertNull(webSession.getAttributeValue("user"));

        Mockito.when(request.getSession(false)).thenReturn(session);
        Assertions.assertTrue(webSession.exists());
        Assertions.assertEquals("ana", webSession.getAttributeValue("user"));
        Assertions.assertSame(session, ((IServletWebSession) webSession).getNativeSessionObject());

        Mockito.when(request.getSession(false)).thenReturn(null);
        Assertions.assertFalse(webSession.exists());
        Assertions.assertNull(webSession.getAttributeValue("user"));
        Assertions.assertEquals(0, webSession.getAttributeCount());

        Mockito.when(request.getSession(true)).thenReturn(session);
        webSession.setAttributeValue("other", "x");
        Mockito.verify(session).setAttribute("other", "x");

    }


    @Test
    public void testJavaxSessionCreatedAndInvalidatedAfterExchange() {

        final javax.servlet.ServletContext servletContext = Mockito.mock(javax.servlet.ServletContext.class);
        final javax.servlet.http.HttpServletRequest request = Mockito.mock(javax.servlet.http.HttpServletRequest.class);
        final javax.servlet.http.HttpServletResponse response = Mockito.mock(javax.servlet.http.HttpServletResponse.class);
        final javax.servlet.http.HttpSession session = Mockito.mock(javax.servlet.http.HttpSession.class);
        Mockito.when(request.getServletContext()).thenReturn(servletContext);
        Mockito.when(session.getAttribute("user")).thenReturn("ana");

        final IWebSession webSession =
                JavaxServletWebApplication.buildApplication(servletContext).buildExchange(request, response).getSession();
        Assertions.assertFalse(webSession.exists());
        Assertions.assertNull(webSession.getAttributeValue("user"));

        Mockito.when(request.getSession(false)).thenReturn(session);
        Assertions.assertTrue(webSession.exists());
        Assertions.assertEquals("ana", webSession.getAttributeValue("user"));
        Assertions.assertSame(session, ((IServletWebSession) webSession).getNativeSessionObject());

        Mockito.when(request.getSession(false)).thenReturn(null);
        Assertions.assertFalse(webSession.exists());
        Assertions.assertNull(webSession.getAttributeValue("user"));
        Assertions.assertEquals(0, webSession.getAttributeCount());

        Mockito.when(request.getSession(true)).thenReturn(session);
        webSession.setAttributeValue("other", "x");
        Mockito.verify(session).setAttribute("other", "x");

    }


}
