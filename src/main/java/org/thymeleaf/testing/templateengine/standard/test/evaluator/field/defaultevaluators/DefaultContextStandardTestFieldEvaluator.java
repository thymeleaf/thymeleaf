/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ognl.Ognl;
import ognl.OgnlException;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.util.OrderedProperties;


public class DefaultContextStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    private static final String VAR_NAME_PREFIX_PARAM = "param.";
    private static final String VAR_NAME_PREFIX_REQUEST = "request.";
    private static final String VAR_NAME_PREFIX_SESSION = "session.";
    private static final String VAR_NAME_PREFIX_APPLICATION = "application.";
    
    
    
    public static final DefaultContextStandardTestFieldEvaluator INSTANCE = 
            new DefaultContextStandardTestFieldEvaluator();
    
    public static final String LOCALE_PROPERTY_NAME = "locale";
    
    
    
    private DefaultContextStandardTestFieldEvaluator() {
        super(IContext.class);
    }



    @Override
    protected StandardTestEvaluatedField getValue(final String executionId, final String documentName, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(new Context());
        }

        final Properties valueAsProperties = new OrderedProperties();

        try {
            
            /*
             * This String -> byte[] conversion is needed because java.util.Properties 
             * did not allow using a java.io.Reader for loading properties until Java 6.
             */
            final byte[] valueAsBytes = fieldValue.getBytes("ISO-8859-1");
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(valueAsBytes);

            valueAsProperties.load(inputStream);
            
        } catch (final Throwable t) {
            throw new TestEngineExecutionException( 
                    "Error while reading context specification", t);
        }
        
        final Locale locale = 
                (valueAsProperties.containsKey(LOCALE_PROPERTY_NAME)? 
                        new Locale(valueAsProperties.getProperty(LOCALE_PROPERTY_NAME)) : Locale.US);
        
        
        final Map<String,Object> contextVariables = new HashMap<String, Object>();
        final Map<String,Object> expressionUtilityObjects =
                ExpressionEvaluatorObjects.getExpressionEvaluationUtilityObjectsForLocale(locale);
        if (expressionUtilityObjects != null) {
            contextVariables.putAll(expressionUtilityObjects);
        }
        
        
        final ServletContext servletContext = createServletContext();
        final HttpSession session = createHttpSession(servletContext);
        final HttpServletRequest request = createHttpServletRequest(session);
        final HttpServletResponse response = createHttpServletResponse();
        
        final WebContext ctx = new WebContext(request, response, servletContext, locale);

        final Map<String,List<Object>> requestParameters = new HashMap<String,List<Object>>();
        
        for (final Map.Entry<?,?> entry : valueAsProperties.entrySet()) {
            
            final String varName = (String)entry.getKey();
            final String varValue = (String)entry.getValue();
            
            final Object varObjectValue;
            if (varValue != null && varValue.trim().startsWith("${") && varValue.trim().endsWith("}")) {
                // value is an expression
                varObjectValue = evaluateAsOgnlExpression(varValue, contextVariables, ctx.getVariables());
            } else {
                varObjectValue = varValue;
            }
            
            if (varName.startsWith(VAR_NAME_PREFIX_PARAM)) {
                final String unprefixedName = varName.substring(VAR_NAME_PREFIX_PARAM.length());
                List<Object> currentValues = requestParameters.get(unprefixedName);
                if (currentValues == null) {
                    currentValues = new ArrayList<Object>();
                    requestParameters.put(unprefixedName, currentValues);
                }
                currentValues.add(varObjectValue);
            } else if (varName.startsWith(VAR_NAME_PREFIX_REQUEST)) {
                final String unprefixedName = varName.substring(VAR_NAME_PREFIX_REQUEST.length());
                request.setAttribute(unprefixedName, varObjectValue);
            } else if (varName.startsWith(VAR_NAME_PREFIX_SESSION)) {
                final String unprefixedName = varName.substring(VAR_NAME_PREFIX_SESSION.length());
                session.setAttribute(unprefixedName, varObjectValue);
            } else if (varName.startsWith(VAR_NAME_PREFIX_APPLICATION)) {
                final String unprefixedName = varName.substring(VAR_NAME_PREFIX_APPLICATION.length());
                servletContext.setAttribute(unprefixedName, varObjectValue);
            } else {
                ctx.setVariable(varName, varObjectValue);
            }
            
        }
        
        initializeRequestParameters(request, requestParameters);

        return StandardTestEvaluatedField.forSpecifiedValue(ctx);
        
    }
    
    
    

    
    private static final Object evaluateAsOgnlExpression(final String varValue, 
            final Map<String,Object> contextVariables, final Object evaluationRoot) {
        
        final String varExpressionStr = varValue.trim().substring(2, varValue.length() - 1);

        try {
            
            final Object varExpression = Ognl.parseExpression(varExpressionStr);
            
            return Ognl.getValue(varExpression, contextVariables, evaluationRoot);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + varExpressionStr + "\"", e);
        }
        
    }
    
   
    
    
    private static final HttpServletRequest createHttpServletRequest(final HttpSession session) {
        
        final Map<String,Object> variables = new HashMap<String, Object>();
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        
        Mockito.when(request.getContextPath()).thenReturn("thymeleaf-test");
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(Matchers.anyBoolean())).thenReturn(session);

        Mockito.when(request.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(variables));
        Mockito.when(request.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(variables));
        Mockito.doAnswer(new SetAttributeAnswer(variables)).when(request).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        return request;
        
    }
    
    
    private static final void initializeRequestParameters(final HttpServletRequest request, final Map<String,List<Object>> variables) {
        
        Mockito.when(request.getParameterNames()).thenAnswer(new GetVariableNamesAnswer(variables));
        Mockito.when(request.getParameterValues(Matchers.anyString())).thenAnswer(new GetParameterValuesAnswer(variables));
        Mockito.when(request.getParameterMap()).thenAnswer(new GetParameterMapAnswer(variables));
        Mockito.when(request.getParameter(Matchers.anyString())).thenAnswer(new GetParameterAnswer(variables));
        
    }
    

    
    private static final HttpSession createHttpSession(final ServletContext context) {
        
        final Map<String,Object> variables = new HashMap<String, Object>();
        final HttpSession session = Mockito.mock(HttpSession.class);
        
        Mockito.when(session.getServletContext()).thenReturn(context);

        Mockito.when(session.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(variables));
        Mockito.when(session.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(variables));
        Mockito.doAnswer(new SetAttributeAnswer(variables)).when(session).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        return session;
        
    }
    
    
    
    private static final HttpServletResponse createHttpServletResponse() {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        return response;
    }

    
    
    private static final ServletContext createServletContext() {
        
        final Map<String,Object> variables = new HashMap<String, Object>();
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        
        Mockito.when(servletContext.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(variables));
        Mockito.when(servletContext.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(variables));
        Mockito.doAnswer(new SetAttributeAnswer(variables)).when(servletContext).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        return servletContext;
    }
    

    
    
    
    
    
    private static class VariableEnumeration implements Enumeration<String> {

        private final Iterator<String> iterator;
        
        public VariableEnumeration(final Collection<String> values) {
            super();
            this.iterator = values.iterator();
        }
        
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        public String nextElement() {
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
            return new VariableEnumeration(this.values.keySet());
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
            this.values.put(attributeName, attributeValue);
            return null;
        }
        
    }
    
    
    
    private static class GetParameterValuesAnswer implements Answer<String[]> {

        private final Map<String,List<Object>> values;
        
        public GetParameterValuesAnswer(final Map<String,List<Object>> values) {
            super();
            this.values = values;
        }
        
        public String[] answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final List<?> parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final String[] parameterValuesArray = new String[parameterValues.size()];
            for (int i = 0; i < parameterValuesArray.length; i++) {
                final Object value = parameterValues.get(i);
                parameterValuesArray[i] = (value == null? null : value.toString());
            }
            return parameterValuesArray;
        }
        
    }
    
    
    
    private static class GetParameterAnswer implements Answer<String> {

        private final Map<String,List<Object>> values;
        
        public GetParameterAnswer(final Map<String,List<Object>> values) {
            super();
            this.values = values;
        }
        
        public String answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final List<?> parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final Object value = parameterValues.get(0);
            return (value == null? null : value.toString());
        }
        
    }
    
    
    
    private static class GetParameterMapAnswer implements Answer<Map<String,String[]>> {

        private final Map<String,List<Object>> values;
        
        public GetParameterMapAnswer(final Map<String,List<Object>> values) {
            super();
            this.values = values;
        }
        
        public Map<String,String[]> answer(final InvocationOnMock invocation) throws Throwable {
            final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
            for (final Map.Entry<String,List<Object>> valueEntry : this.values.entrySet()) {
                final String parameterName = valueEntry.getKey();
                final List<Object> parameterValues = valueEntry.getValue();
                if (parameterValues == null) {
                    parameterMap.put(parameterName, null);
                    continue;
                }
                final String[] parameterValuesArray = new String[parameterValues.size()];
                for (int i = 0; i < parameterValuesArray.length; i++) {
                    final Object value = parameterValues.get(i);
                    parameterValuesArray[i] = (value == null? null : value.toString());
                }
                parameterMap.put(parameterName, parameterValuesArray);
            }
            return parameterMap;
        }
        
    }
    
    
}
