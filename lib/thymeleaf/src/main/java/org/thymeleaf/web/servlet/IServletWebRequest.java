/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2021, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebRequest;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 * 
 */
public interface IServletWebRequest extends IWebRequest {

    public String getContextPath();
    public String getRequestURI();

    @Override
    default String getApplicationPath() {
        return getContextPath();
    }

    @Override
    default String getPathWithinApplication() {
        final String requestURI = getRequestURI();
        final String applicationPath = getApplicationPath();
        if (requestURI == null) {
            return null;
        }
        if (applicationPath == null || applicationPath.length() == 0) {
            return requestURI;
        }
        return (requestURI.substring(applicationPath.length()));
    }

    public Enumeration<String> getHeaderNames();
    public Enumeration<String> getHeaders(final String name);


    @Override
    default boolean containsHeader(final String name) {
        Validate.notNull(name, "Name cannot be null");
        // A header is only null when it is not present
        return getHeaderValue(name) != null;
    }

    @Override
    default int getHeaderCount() {
        int count = 0;
        final Enumeration<String> headerNamesEnum = getHeaderNames();
        while (headerNamesEnum.hasMoreElements()) {
            headerNamesEnum.nextElement();
            count++;
        }
        return count;
    }

    @Override
    default Set<String> getAllHeaderNames() {
        final Set<String> headerNames = new LinkedHashSet<String>(10);
        final Enumeration<String> headerNamesEnum = getHeaderNames();
        while (headerNamesEnum.hasMoreElements()) {
            headerNames.add(headerNamesEnum.nextElement());
        }
        return Collections.unmodifiableSet(headerNames);
    }

    @Override
    default Map<String, String[]> getHeaderMap() {
        final Map<String, String[]> headerMap = new LinkedHashMap<String, String[]>(10);
        final Enumeration<String> headerNamesEnum = getHeaderNames();
        String headerName;
        while (headerNamesEnum.hasMoreElements()) {
            headerName = headerNamesEnum.nextElement();
            headerMap.put(headerName, getHeaderValues(headerName));
        }
        return Collections.unmodifiableMap(headerMap);
    }

    @Override
    default String[] getHeaderValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final Enumeration<String> headerValues = getHeaders(name);
        if (headerValues == null || !headerValues.hasMoreElements()) {
            // request.getHeaders() returns an empty enumeration if the header does not exist, but we want null
            return null;
        }
        final List<String> headerValueList = Collections.list(headerValues);
        return headerValueList.toArray(new String[headerValueList.size()]);
    }

    @Override
    default boolean containsParameter(final String name) {
        Validate.notNull(name, "Name cannot be null");
        // A request parameter is only null when it is not present
        return getParameterValues(name) != null;
    }

    @Override
    default int getParameterCount() {
        return getParameterMap().size();
    }

    @Override
    default Set<String> getAllParameterNames() {
        return getParameterMap().keySet();
    }


    public Object getNativeRequestObject();

}
