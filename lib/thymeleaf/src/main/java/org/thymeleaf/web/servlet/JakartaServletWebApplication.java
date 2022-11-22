/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public class JakartaServletWebApplication implements IServletWebApplication {
// This class is made NOT final so that it can be proxied by Dependency Injection frameworks

    private final ServletContext servletContext;


    JakartaServletWebApplication(final ServletContext servletContext) {
        super();
        Validate.notNull(servletContext, "Servlet context cannot be null");
        this.servletContext = servletContext;
    }


    public static JakartaServletWebApplication buildApplication(final ServletContext servletContext) {
        return new JakartaServletWebApplication(servletContext);
    }

    public IServletWebExchange buildExchange(final HttpServletRequest httpServletRequest,
                                             final HttpServletResponse httpServletResponse) {

        Validate.notNull(httpServletRequest, "Request cannot be null");
        Validate.notNull(httpServletResponse, "Response cannot be null");
        Validate.isTrue(servletContextMatches(httpServletRequest),
                "Cannot build an application for a request which servlet context does not match with " +
                "the application that it is being built for.");

        final JakartaServletWebRequest request = new JakartaServletWebRequest(httpServletRequest);
        final JakartaServletWebSession session = new JakartaServletWebSession(httpServletRequest);

        return new JakartaServletWebExchange(request, session, this, httpServletResponse);

    }


    @Override
    public Enumeration<String> getAttributeNames() {
        return this.servletContext.getAttributeNames();
    }

    @Override
    public Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.servletContext.getAttribute(name);
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        this.servletContext.setAttribute(name, value);
    }


    @Override
    public InputStream getResourceAsStream(final String path) {
        Validate.notNull(path, "Path cannot be null");
        return this.servletContext.getResourceAsStream(path);
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        Validate.notNull(path, "Path cannot be null");
        return this.servletContext.getResource(path);
    }


    @Override
    public Object getNativeServletContextObject() {
        return this.servletContext;
    }


    private boolean servletContextMatches(final HttpServletRequest httpServletRequest) {
        // We should not be directly matching servletContext objects because a wrapper might have been applied
        final String servletContextPath = this.servletContext.getContextPath();
        final String requestServletContextPath = httpServletRequest.getServletContext().getContextPath();
        return Objects.equals(servletContextPath, requestServletContextPath);
    }

}
