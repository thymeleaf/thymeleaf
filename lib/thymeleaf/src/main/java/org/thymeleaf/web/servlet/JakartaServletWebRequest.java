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

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
final class JakartaServletWebRequest implements IServletWebRequest {

    private final HttpServletRequest request;


    JakartaServletWebRequest(final HttpServletRequest request) {
        super();
        Validate.notNull(request, "Request cannot be null");
        this.request = request;
    }


    @Override
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getScheme() {
        return this.request.getScheme();
    }

    @Override
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override
    public Integer getServerPort() {
        return Integer.valueOf(this.request.getServerPort());
    }

    @Override
    public String getContextPath() {
        final String contextPath = this.request.getContextPath();
        // This protects against a redirection behaviour in Jetty
        return (contextPath != null && contextPath.length() == 1 && contextPath.charAt(0) == '/')? "" : contextPath;
    }

    @Override
    public String getRequestURI() {
        return this.request.getRequestURI();
    }

    @Override
    public String getQueryString() {
        return this.request.getQueryString();
    }


    @Override
    public Enumeration<String> getHeaderNames() {
        return this.request.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        return this.request.getHeaders(name);
    }

    @Override
    public String getHeaderValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.request.getHeader(name);
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }

    @Override
    public String getParameterValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.request.getParameter(name);
    }

    @Override
    public String[] getParameterValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.request.getParameterValues(name);
    }


    @Override
    public boolean containsCookie(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final Cookie[] cookies = this.request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (name.equals(cookies[i].getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCookieCount() {
        final Cookie[] cookies = this.request.getCookies();
        return (cookies == null? 0 : cookies.length);
    }

    @Override
    public Set<String> getAllCookieNames() {
        final Cookie[] cookies = this.request.getCookies();
        if (cookies == null) {
            return Collections.emptySet();
        }
        final Set<String> cookieNames = new LinkedHashSet<String>(3);
        for (int i = 0; i < cookies.length; i++) {
            cookieNames.add(cookies[i].getName());
        }
        return Collections.unmodifiableSet(cookieNames);
    }

    @Override
    public Map<String, String[]> getCookieMap() {
        final Cookie[] cookies = this.request.getCookies();
        if (cookies == null) {
            return Collections.emptyMap();
        }
        final Map<String,String[]> cookieMap = new LinkedHashMap<String,String[]>(3);
        for (int i = 0; i < cookies.length; i++) {
            final String cookieName = cookies[i].getName();
            final String cookieValue = cookies[i].getValue();
            if (cookieMap.containsKey(cookieName)) {
                final String[] currentCookieValues = cookieMap.get(cookieName);
                final String[] newCookieValues = Arrays.copyOf(currentCookieValues, currentCookieValues.length + 1);
                newCookieValues[currentCookieValues.length] = cookieValue;
                cookieMap.put(cookieName, newCookieValues);
            } else {
                cookieMap.put(cookieName, new String[]{cookieValue});
            }
        }
        return Collections.unmodifiableMap(cookieMap);
    }

    @Override
    public String[] getCookieValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final Cookie[] cookies = this.request.getCookies();
        if (cookies == null) {
            return null;
        }
        String[] cookieValues = null;
        for (int i = 0; i < cookies.length; i++) {
            final String cookieName = cookies[i].getName();
            if (name.equals(cookieName)) {
                final String cookieValue = cookies[i].getValue();
                if (cookieValues != null) {
                    final String[] newCookieValues = Arrays.copyOf(cookieValues, cookieValues.length + 1);
                    newCookieValues[cookieValues.length] = cookieValue;
                    cookieValues = newCookieValues;
                } else {
                    cookieValues = new String[]{cookieValue};
                }
            }
        }
        return cookieValues;
    }


    @Override
    public Object getNativeRequestObject() {
        return this.request;
    }

}
