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

package org.thymeleaf.testing.templateengine.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thymeleaf.util.Validate;
import org.unbescape.uri.UriEscape;

public class JakartaServletMockUtils {


    private JakartaServletMockUtils() {
        super();
    }


    public static HttpServletRequest createMockHttpServletRequest(
            final String testName,
            final HttpSession session,
            final Map<String, Object> attributeMap,
            final Map<String, String[]> parameterMap,
            final String method,
            final String scheme,
            final String serverName, final int port,
            final String contextPath,
            final String contentType, final String characterEncoding,
            final Locale locale) {

        final String protocol = "HTTP/1.1";
        final String servletPath = "/" + testNameToServletPath(testName);
        final String requestURI = contextPath + servletPath;
        final String requestURL = scheme + "://" + serverName + requestURI;
        final String queryString = buildQueryString(parameterMap);
        final int contentLength = -1; // -1 is HTTP standard for 'unknown'
        final Enumeration<String> headerNames = new ObjectEnumeration<String>(null);

        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getContentType()).thenReturn(contentType);
        Mockito.when(request.getCharacterEncoding()).thenReturn(characterEncoding);
        Mockito.when(request.getMethod()).thenReturn(method);
        Mockito.when(request.getProtocol()).thenReturn(protocol);
        Mockito.when(request.getScheme()).thenReturn(scheme);
        Mockito.when(request.getServerName()).thenReturn(serverName);
        Mockito.when(Integer.valueOf(request.getServerPort())).thenReturn(Integer.valueOf(port));
        Mockito.when(request.getContextPath()).thenReturn(contextPath);
        Mockito.when(request.getServletPath()).thenReturn(servletPath);
        Mockito.when(request.getRequestURI()).thenReturn(requestURI);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        Mockito.when(request.getQueryString()).thenReturn(queryString);
        Mockito.when(request.getLocale()).thenReturn(locale);
        Mockito.when(request.getLocales()).thenReturn(new ObjectEnumeration<Locale>(Arrays.asList(new Locale[]{locale})));
        Mockito.when(Integer.valueOf(request.getContentLength())).thenReturn(Integer.valueOf(contentLength));
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);

        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(ArgumentMatchers.anyBoolean())).thenReturn(session);

        Mockito.when(request.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributeMap));
        Mockito.when(request.getAttribute(ArgumentMatchers.anyString())).thenAnswer(new GetAttributeAnswer(attributeMap));
        Mockito.doAnswer(new SetAttributeAnswer(attributeMap)).when(request).setAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributeMap)).when(request).removeAttribute(ArgumentMatchers.anyString());

        Mockito.when(request.getParameterNames()).thenAnswer(new GetVariableNamesAnswer(parameterMap));
        Mockito.when(request.getParameterValues(ArgumentMatchers.anyString())).thenAnswer(new GetParameterValuesAnswer(parameterMap));
        Mockito.when(request.getParameterMap()).thenReturn(parameterMap);
        Mockito.when(request.getParameter(ArgumentMatchers.anyString())).thenAnswer(new GetParameterAnswer(parameterMap));


        return request;

    }



    public static HttpSession createMockHttpSession(final ServletContext context, final Map<String, Object> attributeMap) {

        final HttpSession session = Mockito.mock(HttpSession.class);

        Mockito.when(session.getServletContext()).thenReturn(context);

        Mockito.when(session.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributeMap));
        Mockito.when(session.getAttribute(ArgumentMatchers.anyString())).thenAnswer(new GetAttributeAnswer(attributeMap));
        Mockito.doAnswer(new SetAttributeAnswer(attributeMap)).when(session).setAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributeMap)).when(session).removeAttribute(ArgumentMatchers.anyString());

        return session;

    }



    public static HttpServletResponse createMockHttpServletResponse(
            final Function<String,String> transformUrlFunction) {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.encodeURL(ArgumentMatchers.anyString())).thenAnswer(new EncodeUrlAnswer(transformUrlFunction));
        return response;
    }



    public static ServletContext createMockServletContext(
            final Map<String, Object> attributeMap,
            final String contextPath) {

        final ServletContext servletContext = Mockito.mock(ServletContext.class);

        Mockito.when(servletContext.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributeMap));
        Mockito.when(servletContext.getAttribute(ArgumentMatchers.anyString())).thenAnswer(new GetAttributeAnswer(attributeMap));
        Mockito.doAnswer(new SetAttributeAnswer(attributeMap)).when(servletContext).setAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributeMap)).when(servletContext).removeAttribute(ArgumentMatchers.anyString());
        Mockito.when(servletContext.getContextPath()).thenReturn(contextPath);

        Mockito.when(servletContext.getInitParameterNames()).thenReturn(new ObjectEnumeration<String>(null));
        Mockito.when(servletContext.getInitParameter(ArgumentMatchers.anyString())).thenReturn(null);

        return servletContext;
    }







    private static class ObjectEnumeration<T> implements Enumeration<T> {

        private final Iterator<T> iterator;

        @SuppressWarnings("unchecked")
        public ObjectEnumeration(final Collection<T> values) {
            super();
            if (values != null) {
                this.iterator = (new ArrayList<T>(values)).iterator();
            } else {
                this.iterator = ((List<T>) Collections.emptyList()).iterator();
            }
        }

        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        public T nextElement() {
            return this.iterator.next();
        }

    }




    private static class GetVariableNamesAnswer implements Answer<Enumeration<?>> {

        private final Map<String,?> values;

        public GetVariableNamesAnswer(final Map<String,?> values) {
            super();
            this.values = values;
        }

        public Enumeration<?> answer(final InvocationOnMock invocation) throws Throwable {
            return new ObjectEnumeration<String>(this.values.keySet());
        }

    }



    private static class GetAttributeAnswer implements Answer<Object> {

        private final Map<String,Object> values;

        public GetAttributeAnswer(final Map<String,Object> values) {
            super();
            this.values = values;
        }

        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final String attributeName = (String) invocation.getArguments()[0];
            return this.values.get(attributeName);
        }

    }



    private static class SetAttributeAnswer implements Answer<Object> {

        private final Map<String,Object> values;

        public SetAttributeAnswer(final Map<String,Object> values) {
            super();
            this.values = values;
        }

        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final String attributeName = (String) invocation.getArguments()[0];
            final Object attributeValue = invocation.getArguments()[1];
            if (attributeValue == null) {
                // According to the Servlet API, setting an attribute to null is the same as removing it
                this.values.remove(attributeName);
            } else {
                this.values.put(attributeName, attributeValue);
            }
            return null;
        }

    }



    private static class RemoveAttributeAnswer implements Answer<Object> {

        private final Map<String,Object> values;

        public RemoveAttributeAnswer(final Map<String,Object> values) {
            super();
            this.values = values;
        }

        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final String attributeName = (String) invocation.getArguments()[0];
            this.values.remove(attributeName);
            return null;
        }

    }



    private static class GetParameterValuesAnswer implements Answer<String[]> {

        private final Map<String,String[]> values;

        public GetParameterValuesAnswer(final Map<String,String[]> values) {
            super();
            this.values = values;
        }

        public String[] answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            return this.values.get(parameterName);
        }

    }



    private static class GetParameterAnswer implements Answer<String> {

        private final Map<String,String[]> values;

        public GetParameterAnswer(final Map<String,String[]> values) {
            super();
            this.values = values;
        }

        public String answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final String[] parameterValues = this.values.get(parameterName);
            return (parameterValues != null)? parameterValues[0] : null;
        }

    }




    private static class EncodeUrlAnswer implements Answer<String> {

        private final Function<String, String> transformUrlFunction;

        public EncodeUrlAnswer(final Function<String, String> transformUrlFunction) {
            super();
            Validate.notNull(transformUrlFunction, "transformUrl Function cannot be null");
            this.transformUrlFunction = transformUrlFunction;
        }

        public String answer(final InvocationOnMock invocation) throws Throwable {
            return this.transformUrlFunction.apply((String) invocation.getArguments()[0]);
        }

    }



    private static String testNameToServletPath(final String testName) {

        String normalizedName = StringUtils.stripAccents(testName);
        if (normalizedName.contains("/")) {
            normalizedName = normalizedName.substring(normalizedName.lastIndexOf('/'));
        }
        if (normalizedName.contains("\\")) {
            normalizedName = normalizedName.substring(normalizedName.lastIndexOf('\\'));
        }

        if (normalizedName.endsWith(".thtest")) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 7);
        }

        final StringBuilder strBuilder = new StringBuilder();
        final int nameLen = normalizedName.length();
        for (int i = 0; i < nameLen; i++) {
            final char c = normalizedName.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                strBuilder.append(c);
            }
        }
        return UriEscape.escapeUriPath(strBuilder.toString());
    }


    private static String buildQueryString(final Map<String,String[]> parameters) {

        if (parameters == null || parameters.size() == 0) {
            return null;
        }

        final StringBuilder strBuilder = new StringBuilder();
        for (final Map.Entry<String,String[]> parameterEntry : parameters.entrySet()) {

            final String parameterName = parameterEntry.getKey();
            final String[] parameterValues = parameterEntry.getValue();

            if (parameterValues == null || parameterValues.length == 0) {
                if (strBuilder.length() > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(parameterName);
                continue;
            }

            for (final String parameterValue : parameterValues) {
                if (strBuilder.length() > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(parameterName);
                if (parameterValue != null) {
                    strBuilder.append("=");
                    try {
                        strBuilder.append(URLEncoder.encode(parameterValue, "UTF-8"));
                    } catch (final UnsupportedEncodingException e) {
                        // Should never happen, UTF-8 just exists.
                        throw new RuntimeException(e);
                    }
                }
            }

        }

        return strBuilder.toString();

    }

}
