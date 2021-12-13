/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.web;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 * 
 */
public interface IWebRequest {

    public String getMethod();

    public boolean isSecure();
    default String getScheme() {
        return isSecure()? "https" : "http";
    }

    public String getServerName();
    public Integer getServerPort();
    public String getApplicationPath();
    public String getPathWithinApplication();
    public String getQueryString();

    default String getRequestPath() {
        final String applicationPath = getApplicationPath();
        final String pathWithinApplication = getPathWithinApplication();
        return (applicationPath == null? "" : applicationPath) +
                (pathWithinApplication == null? "" : pathWithinApplication);
    }

    default String getRequestURL() {

        final String scheme = getScheme();
        final String serverName = getServerName();
        final Integer serverPort = getServerPort();
        final String requestPath = getRequestPath();
        final String queryString = getQueryString();

        if (scheme == null || serverName == null || serverPort == null) {
            throw new UnsupportedOperationException(
                    "Request scheme, server name or port are null in this environment. Cannot compute request URL");
        }

        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(scheme).append("://").append(serverName);
        if (!(scheme.equals("http") && serverPort.intValue() == 80) &&
                !(scheme.equals("https") && serverPort.intValue() == 443)) {
            urlBuilder.append(':').append(serverPort);
        }
        urlBuilder.append(requestPath);
        if (queryString != null) {
            urlBuilder.append('?').append(queryString);
        }
        return urlBuilder.toString();

    }

    public boolean containsHeader(final String name);
    public int getHeaderCount();
    public Set<String> getAllHeaderNames();
    public Map<String,String> getHeaderMap();
    public String getHeaderValue(final String name);

    public boolean containsParameter(final String name);
    public int getParameterCount();
    public Set<String> getAllParameterNames();
    public Map<String,String[]> getParameterMap();
    public String[] getParameterValues(final String name);

    // Only request cookies are modelled, so <Name:String,Value:String[]> is enough.
    public boolean containsCookie(final String name);
    public int getCookieCount();
    public Set<String> getAllCookieNames();
    public Map<String,String[]> getCookieMap();
    public String[] getCookieValues(final String name);

}
