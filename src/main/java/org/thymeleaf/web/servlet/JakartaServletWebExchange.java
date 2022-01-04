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

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;

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
final class JakartaServletWebExchange implements IServletWebExchange {

    private final JakartaServletWebRequest request;
    private final JakartaServletWebSession session; // can be null
    private final JakartaServletWebApplication application;

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;


    JakartaServletWebExchange(final JakartaServletWebRequest request,
                              final JakartaServletWebSession session,
                              final JakartaServletWebApplication application,
                              final HttpServletResponse httpServletResponse) {
        super();
        Validate.notNull(request, "Request cannot be null");
        // session CAN be null
        Validate.notNull(application, "Application cannot be null");
        Validate.notNull(httpServletResponse, "Response cannot be null");
        this.request = request;
        this.session = session;
        this.application = application;
        this.httpServletRequest = (HttpServletRequest) this.request.getNativeObject();
        this.httpServletResponse = httpServletResponse;
    }


    @Override
    public IServletWebRequest getRequest() {
        return this.request;
    }

    @Override
    public IServletWebSession getSession() {
        return this.session;
    }

    @Override
    public IServletWebApplication getApplication() {
        return this.application;
    }


    @Override
    public Principal getPrincipal() {
        return this.httpServletRequest.getUserPrincipal();
    }

    @Override
    public Locale getLocale() {
        return this.httpServletRequest.getLocale();
    }


    @Override
    public Enumeration<String> getAttributeNames() {
        return this.httpServletRequest.getAttributeNames();
    }

    @Override
    public Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.httpServletRequest.getAttribute(name);
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        this.httpServletRequest.setAttribute(name, value);
    }


    @Override
    public String transformURL(final String url) {
        return this.httpServletResponse.encodeURL(url);
    }

}
