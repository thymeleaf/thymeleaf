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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.testing.templateengine.context.ITestContext;



public class SpringWebProcessingContextBuilder extends WebProcessingContextBuilder {


    
    public SpringWebProcessingContextBuilder() {
        super();
    }

    
    
    
    @Override
    protected void doAdditionalVariableProcessing(
            final ITestContext testContext, final HttpServletRequest request,
            final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        
        final WebApplicationContext applicationContext = new GenericWebApplicationContext(servletContext);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

        final RequestContext requestContext = 
                new RequestContext(request, response, servletContext, new HashMap<String,Object>());
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        
    }
    
    
    
}
