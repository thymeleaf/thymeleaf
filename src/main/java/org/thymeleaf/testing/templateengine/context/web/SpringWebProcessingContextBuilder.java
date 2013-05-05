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

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;



public class SpringWebProcessingContextBuilder extends WebProcessingContextBuilder {

    public static final String BINDING_MODEL_NAME_VARIABLE_NAME = "bindingModelName";
    

    
    public SpringWebProcessingContextBuilder() {
        super();
    }

    
    
    
    @Override
    protected void doAdditionalVariableProcessing(
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        
        final Object modelNameObj = variables.get(BINDING_MODEL_NAME_VARIABLE_NAME);
        if (modelNameObj != null) {
            
            final String modelName = modelNameObj.toString();
            final Object modelObject = variables.get(modelName);

            final WebDataBinder dataBinder = new WebDataBinder(modelObject, modelName);
            
            initBinders(modelName, modelObject, testContext, testMessages, dataBinder, locale);
            
            final String bindingResultName = BindingResult.MODEL_KEY_PREFIX + modelName;
            variables.put(bindingResultName, dataBinder.getBindingResult());
            
        }
        
        
        final WebApplicationContext applicationContext = new GenericWebApplicationContext(servletContext);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

        
        final RequestContext requestContext = 
                new RequestContext(request, response, servletContext, variables);
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        
    }
    
    
    
    @SuppressWarnings("unused")
    protected void initBinders(
            final String bindingModelName, final Object bindingModelObject,
            final ITestContext testContext, final ITestMessages testMessages,
            final DataBinder dataBinder, final Locale locale) {
        // Nothing to be done. Meant to be overriden.
    }
    
    
    
}
