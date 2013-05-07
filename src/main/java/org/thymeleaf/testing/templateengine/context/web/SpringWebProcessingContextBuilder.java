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
package org.thymeleaf.testing.templateengine.context.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;



public class SpringWebProcessingContextBuilder extends WebProcessingContextBuilder {
    

    
    public SpringWebProcessingContextBuilder() {
        super();
    }

    
    
    
    @Override
    protected final void doAdditionalVariableProcessing(
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        
        final List<String> bindingVariableNames = 
                getBindingVariableNames(testContext, testMessages, request, response, servletContext, locale, variables);
        for (final String bindingVariableName : bindingVariableNames) {
                
            final Object bindingObject = variables.get(bindingVariableName);
            final WebDataBinder dataBinder = new WebDataBinder(bindingObject, bindingVariableName);
            
            initBinders(bindingVariableName, bindingObject, testContext, testMessages, dataBinder, locale);
            
            final String bindingResultName = BindingResult.MODEL_KEY_PREFIX + bindingVariableName;
            variables.put(bindingResultName, dataBinder.getBindingResult());
            
        }
        
        final WebApplicationContext appCtx = 
                createApplicationContext(
                        testContext, testMessages, request, response, servletContext, locale, variables);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);

        final RequestContext requestContext = 
                new RequestContext(request, response, servletContext, variables);
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        
    }
    
    
    /**
     * <p>
     *   Returns the name of the variables that must be considered "binding models", usually
     *   those that serve as form-backing beans.
     * </p>
     * <p>
     *   Default behaviour is:
     * </p>
     * <ul>
     *   <li>Look for a context variable called <tt>binding</tt>. If this variable exists,
     *       it will be considered to contain the name (single-valued) or names (list) of
     *       the binding variables (as literal/s).</li>
     *   <li>If <tt>binding</tt> does not exist, look for a context variable called <tt>model</tt>.
     *       The object contained in that variable will be considered to be the binding model itself.</li>
     * </ul>
     * 
     * @return the binding variable names
     */
    @SuppressWarnings("unused")
    protected List<String> getBindingVariableNames(
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        
        final Object bindingObj = variables.get("binding");
        
        if (bindingObj == null) {
            return Collections.singletonList("model");
        }
        
        if (bindingObj instanceof List) {
            final List<String> variableNames = new ArrayList<String>();
            for (final Object bindingObjValue : ((List<?>)bindingObj)) {
                variableNames.add(bindingObjValue != null? bindingObjValue.toString() : null);
            }
            return variableNames;
        }
        
        return Collections.singletonList(bindingObj.toString());
        
    }
    
    
    
    @SuppressWarnings("unused")
    protected void initBinders(
            final String bindingVariableName, final Object bindingObject,
            final ITestContext testContext, final ITestMessages testMessages,
            final DataBinder dataBinder, final Locale locale) {
        // Nothing to be done. Meant to be overridden.
    }
    
    
    @SuppressWarnings("unused")
    protected WebApplicationContext createApplicationContext(
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        final StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        return applicationContext;

    }
    
    
}
