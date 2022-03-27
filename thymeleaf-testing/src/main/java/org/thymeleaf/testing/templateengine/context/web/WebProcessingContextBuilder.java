/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import ognl.Ognl;
import org.apache.commons.lang3.LocaleUtils;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.testing.templateengine.context.IProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.ITestContextExpression;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebExchange;


public class WebProcessingContextBuilder implements IProcessingContextBuilder {


    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final String REQUEST_PARAMS_PREFIX = "param";
    private static final String REQUEST_ATTRS_PREFIX = "request";
    private static final String SESSION_ATTRS_PREFIX = "session";
    private static final String APPLICATION_ATTRS_PREFIX = "application";


    private final ITestWebExchangeBuilder testWebExchangeBuilder;

    
    public WebProcessingContextBuilder(final ITestWebExchangeBuilder testWebExchangeBuilder) {
        super();
        Validate.notNull(testWebExchangeBuilder, "Test Web Exchange Builder cannot be null");
        this.testWebExchangeBuilder = testWebExchangeBuilder;
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
        
        final Map<String,Object[]> requestParametersObj = new LinkedHashMap<String, Object[]>();
        variables.put(REQUEST_PARAMS_PREFIX, requestParametersObj);
        
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        variables.put(REQUEST_ATTRS_PREFIX, requestAttributes);
        
        final Map<String,Object> sessionAttributes = new LinkedHashMap<String, Object>();
        variables.put(SESSION_ATTRS_PREFIX, sessionAttributes);
        
        final Map<String,Object> applicationAttributes = new LinkedHashMap<String, Object>();
        variables.put(APPLICATION_ATTRS_PREFIX, applicationAttributes);

        
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

            requestParametersObj.put(paramName, paramValues); // We initialize an array long enough to hold all the values.

            final int expressionsLen = entry.getValue().length;
            for (int i = 0; i < expressionsLen; i++) {
                resolve((REQUEST_PARAMS_PREFIX + "." + paramName + "[" + i + "]" + remainder), entry.getValue()[i], variables, locale);
            }
            
        }

        // We need to convert that Map<String,Object[]> into a Map<String,String[]>
        final Map<String,String[]> requestParameters = new LinkedHashMap<String,String[]>();
        for (final Map.Entry<String,Object[]> requestParametersObjEntry : requestParametersObj.entrySet()) {
            final Object[] value = requestParametersObjEntry.getValue();
            final String[] newValue = new String[value.length];
            for (int i = 0; i < value.length; i++) {
                newValue[i] = (value[i] == null)? null : value[i].toString();
            }
            requestParameters.put(requestParametersObjEntry.getKey(), newValue);
        }


        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getRequestAttributes().entrySet()) {
            resolve(REQUEST_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getSessionAttributes().entrySet()) {
            resolve(SESSION_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getApplicationAttributes().entrySet()) {
            resolve(APPLICATION_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        

        // We are using request attributes as "exchange attributes"
        final IWebExchange webExchange =
                this.testWebExchangeBuilder.buildExchange(
                        test, requestAttributes, requestParameters, sessionAttributes, applicationAttributes, locale);

        variables.remove(REQUEST_PARAMS_PREFIX);
        variables.remove(REQUEST_ATTRS_PREFIX);
        variables.remove(SESSION_ATTRS_PREFIX);
        variables.remove(APPLICATION_ATTRS_PREFIX);

        doAdditionalVariableProcessing(test, webExchange, locale, variables);
        
        return doCreateWebContextInstance(test, webExchange, locale, variables);
        
    }


    @SuppressWarnings("unused")
    protected void doAdditionalVariableProcessing(
            final ITest test, final IWebExchange webExchange,
            final Locale locale, final Map<String,Object> variables) {
        // Nothing to be done here, meant to be overriden
    }



    protected IWebContext doCreateWebContextInstance(
            final ITest test, final IWebExchange webExchange,
            final Locale locale, final Map<String,Object> variables) {
        return new WebContext(webExchange, locale, variables);
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


    
}
