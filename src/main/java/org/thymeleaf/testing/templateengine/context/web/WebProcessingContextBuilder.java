/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.context.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ognl.Ognl;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.testing.templateengine.context.IProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.ITestContextExpression;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.testable.ITest;


public class WebProcessingContextBuilder implements IProcessingContextBuilder {


    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final String REQUEST_PARAMS_PREFIX = "param";
    private static final String REQUEST_ATTRS_PREFIX = "request";
    private static final String SESSION_ATTRS_PREFIX = "session";
    private static final String SERVLETCONTEXT_ATTRS_PREFIX = "application";

    

    
    public WebProcessingContextBuilder() {
        super();
    }
    
    
    
    @SuppressWarnings("unchecked")
    public final IContext build(final ITest test) {
        
        if (test == null) {
            return null;
        }
        
        final ITestContext testContext = test.getContext();
        
        Locale locale = DEFAULT_LOCALE;
        final ITestContextExpression localeExpression = testContext.getLocale();
        if (localeExpression != null) {
            final Object exprResult = 
                    localeExpression.evaluate(Collections.EMPTY_MAP, DEFAULT_LOCALE);
            if (exprResult != null) {
                locale = LocaleUtils.toLocale(exprResult.toString());
            }
        }
        
        
        final Map<String,Object> variables = new HashMap<String, Object>();
        
        final Map<String,Object[]> requestParameters = new LinkedHashMap<String, Object[]>();
        variables.put(REQUEST_PARAMS_PREFIX, requestParameters);
        
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        variables.put(REQUEST_ATTRS_PREFIX, requestAttributes);
        
        final Map<String,Object> sessionAttributes = new LinkedHashMap<String, Object>();
        variables.put(SESSION_ATTRS_PREFIX, sessionAttributes);
        
        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        variables.put(SERVLETCONTEXT_ATTRS_PREFIX, servletContextAttributes);

        
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getVariables().entrySet()) {
            resolve(entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression[]> entry : testContext.getRequestParameters().entrySet()) {
            
            final int firstPoint = entry.getKey().indexOf('.');
            final String paramName =
                    (firstPoint == -1? entry.getKey() : entry.getKey().substring(0, firstPoint));
            final String remainder = 
                    (firstPoint == -1? "" : entry.getKey().substring(firstPoint));
            final Object[] paramValues = new Object[entry.getValue().length];
            
            requestParameters.put(paramName, paramValues); // We initialize an array long enough to hold all the values.

            final int expressionsLen = entry.getValue().length;
            for (int i = 0; i < expressionsLen; i++) {
                resolve((REQUEST_PARAMS_PREFIX + "." + paramName + "[" + i + "]" + remainder), entry.getValue()[i], variables, locale);
            }
            
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getRequestAttributes().entrySet()) {
            resolve(REQUEST_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getSessionAttributes().entrySet()) {
            resolve(SESSION_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getServletContextAttributes().entrySet()) {
            resolve(SERVLETCONTEXT_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        
        
        final ServletContext servletContext = createMockServletContext(servletContextAttributes);
        final HttpSession session = createMockHttpSession(servletContext, sessionAttributes);
        final HttpServletRequest request = createMockHttpServletRequest(test, session, requestAttributes, requestParameters, locale);
        final HttpServletResponse response = createMockHttpServletResponse();
        
        variables.remove(REQUEST_PARAMS_PREFIX);
        variables.remove(REQUEST_ATTRS_PREFIX);
        variables.remove(SESSION_ATTRS_PREFIX);
        variables.remove(SERVLETCONTEXT_ATTRS_PREFIX);

        doAdditionalVariableProcessing(test, request, response, servletContext, locale, variables);
        
        final IWebContext context =
                doCreateWebContextInstance(test, request, response, servletContext, locale, variables);
        
        return context;
        
    }


    @SuppressWarnings("unused")
    protected void doAdditionalVariableProcessing(
            final ITest test, 
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        // Nothing to be done here, meant to be overriden
    }



    protected IWebContext doCreateWebContextInstance(
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        return new WebContext(request, response, servletContext, locale, variables);
    }

    
    
    
    static final HttpServletRequest createMockHttpServletRequest(
            final ITest test,
            final HttpSession session, final Map<String, Object> attributes,
            final Map<String, Object[]> parameters, final Locale locale) {

        final String mimeType = "text/html";
        final String characterEncoding = "UTF-8";
        final String method = "GET";
        final String contextName = "/testing";
        final String protocol = "HTTP/1.1";
        final String scheme = "http";
        final int port = 80;
        final String serverName = "testing-server";
        final String servletPath = "/" + testNameToServletPath(test.getName());
        final String requestURI = contextName + servletPath;
        final String requestURL = scheme + "://" + serverName + requestURI;
        final String queryString = buildQueryString(parameters);
        final int contentLength = -1; // -1 is HTTP standard for 'unknown'
        final Enumeration<String> headerNames = new ObjectEnumeration<String>(null);
        
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getContentType()).thenReturn(mimeType);
        Mockito.when(request.getCharacterEncoding()).thenReturn(characterEncoding);
        Mockito.when(request.getMethod()).thenReturn(method);
        Mockito.when(request.getProtocol()).thenReturn(protocol);
        Mockito.when(request.getScheme()).thenReturn(scheme);
        Mockito.when(request.getServerName()).thenReturn(serverName);
        Mockito.when(Integer.valueOf(request.getServerPort())).thenReturn(Integer.valueOf(port));
        Mockito.when(request.getContextPath()).thenReturn(contextName);
        Mockito.when(request.getServletPath()).thenReturn(servletPath);
        Mockito.when(request.getRequestURI()).thenReturn(requestURI);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        Mockito.when(request.getQueryString()).thenReturn(queryString);
        Mockito.when(request.getLocale()).thenReturn(locale);
        Mockito.when(request.getLocales()).thenReturn(new ObjectEnumeration<Locale>(Arrays.asList(new Locale[]{locale})));
        Mockito.when(Integer.valueOf(request.getContentLength())).thenReturn(Integer.valueOf(contentLength));
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);

        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(Matchers.anyBoolean())).thenReturn(session);

        Mockito.when(request.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(request.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(request).setAttribute(Matchers.anyString(), Matchers.anyObject());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributes)).when(request).removeAttribute(Matchers.anyString());

        Mockito.when(request.getParameterNames()).thenAnswer(new GetVariableNamesAnswer(parameters));
        Mockito.when(request.getParameterValues(Matchers.anyString())).thenAnswer(new GetParameterValuesAnswer(parameters));
        Mockito.when(request.getParameterMap()).thenAnswer(new GetParameterMapAnswer(parameters));
        Mockito.when(request.getParameter(Matchers.anyString())).thenAnswer(new GetParameterAnswer(parameters));

        
        return request;
        
    }



    static final HttpSession createMockHttpSession(final ServletContext context, final Map<String, Object> attributes) {
        
        final HttpSession session = Mockito.mock(HttpSession.class);
        
        Mockito.when(session.getServletContext()).thenReturn(context);

        Mockito.when(session.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(session.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(session).setAttribute(Matchers.anyString(), Matchers.anyObject());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributes)).when(session).removeAttribute(Matchers.anyString());

        return session;
        
    }



    static final HttpServletResponse createMockHttpServletResponse() {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.encodeURL(Matchers.anyString())).thenAnswer(new EncodeUrlAnswer());
        return response;
    }



    static final ServletContext createMockServletContext(final Map<String, Object> attributes) {

        final String contextName = "/testing";

        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        
        Mockito.when(servletContext.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(servletContext.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(servletContext).setAttribute(Matchers.anyString(), Matchers.anyObject());
        Mockito.doAnswer(new RemoveAttributeAnswer(attributes)).when(servletContext).removeAttribute(Matchers.anyString());
        Mockito.when(servletContext.getContextPath()).thenReturn(contextName);

        Mockito.when(servletContext.getInitParameterNames()).thenReturn(new ObjectEnumeration<String>(null));
        Mockito.when(servletContext.getInitParameter(Matchers.anyString())).thenReturn(null);
        
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
                this.iterator = ((List<T>)Collections.emptyList()).iterator();
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

        private final Map<String,Object[]> values;
        
        public GetParameterValuesAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public String[] answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final Object[] parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final String[] parameterValuesArray = new String[parameterValues.length];
            for (int i = 0; i < parameterValuesArray.length; i++) {
                final Object value = parameterValues[i];
                parameterValuesArray[i] = (value == null? null : value.toString());
            }
            return parameterValuesArray;
        }
        
    }
    
    
    
    private static class GetParameterAnswer implements Answer<String> {

        private final Map<String,Object[]> values;
        
        public GetParameterAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public String answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final Object[] parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final Object value = parameterValues[0];
            return (value == null? null : value.toString());
        }
        
    }
    
    
    
    private static class GetParameterMapAnswer implements Answer<Map<String,String[]>> {

        private final Map<String,Object[]> values;
        
        public GetParameterMapAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public Map<String,String[]> answer(final InvocationOnMock invocation) throws Throwable {
            final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
            for (final Map.Entry<String,Object[]> valueEntry : this.values.entrySet()) {
                final String parameterName = valueEntry.getKey();
                final Object[] parameterValues = valueEntry.getValue();
                if (parameterValues == null) {
                    parameterMap.put(parameterName, null);
                    continue;
                }
                final String[] parameterValuesArray = new String[parameterValues.length];
                for (int i = 0; i < parameterValuesArray.length; i++) {
                    final Object value = parameterValues[i];
                    parameterValuesArray[i] = (value == null? null : value.toString());
                }
                parameterMap.put(parameterName, parameterValuesArray);
            }
            return parameterMap;
        }
        
    }
    

    
    private static class EncodeUrlAnswer implements Answer<String> {

        public EncodeUrlAnswer() {
            super();
        }
        
        public String answer(final InvocationOnMock invocation) throws Throwable {
            return (String) invocation.getArguments()[0];
        }
        
    }
    
    

    private static void resolve(final String expression, final ITestContextExpression contextExpression, final Map<String,Object> variables, final Locale locale) {
        
        try {
            
            final Object result = contextExpression.evaluate(variables, locale);
            
            final Object parsedExpression = Ognl.parseExpression(expression);
            Ognl.setValue(parsedExpression, variables, result);

        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    "Exception while trying to evaluate expression \"" +  expression + "\" on context for test \"" + TestExecutor.getThreadTestName() + "\"", t);
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
        return strBuilder.toString();
    }
    
    
    private static String buildQueryString(final Map<String,Object[]> parameters) {
        
        if (parameters == null || parameters.size() == 0) {
            return null;
        }
        
        final StringBuilder strBuilder = new StringBuilder();
        for (final Map.Entry<String,Object[]> parameterEntry : parameters.entrySet()) {
            
            final String parameterName = parameterEntry.getKey();
            final Object[] parameterValues = parameterEntry.getValue();

            if (parameterValues == null || parameterValues.length == 0) {
                if (strBuilder.length() > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(parameterName);
                continue;
            }
            
            for (final Object parameterValue : parameterValues) {
                if (strBuilder.length() > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(parameterName);
                if (parameterValue != null) {
                    strBuilder.append("=");
                    try {
                        strBuilder.append(URLEncoder.encode(parameterValue.toString(), "UTF-8"));
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
