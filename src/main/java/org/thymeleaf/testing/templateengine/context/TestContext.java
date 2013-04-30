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
package org.thymeleaf.testing.templateengine.context;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public final class TestContext implements ITestContext {

    private Locale locale = null;
    private Map<String,ITestContextExpression> variables = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression[]> requestParameters = new LinkedHashMap<String, ITestContextExpression[]>();
    private Map<String,ITestContextExpression> requestAttributes = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression> sessionAttributes = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression> servletContextAttributes = new LinkedHashMap<String, ITestContextExpression>();

    
    public TestContext() {
        super();
    }
   

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    

    public Map<String, ITestContextExpression> getVariables() {
        return this.variables;
    }


    public Map<String, ITestContextExpression[]> getRequestParameters() {
        return this.requestParameters;
    }


    public Map<String, ITestContextExpression> getRequestAttributes() {
        return this.requestAttributes;
    }


    public Map<String, ITestContextExpression> getSessionAttributes() {
        return this.sessionAttributes;
    }


    public Map<String, ITestContextExpression> getServletContextAttributes() {
        return this.servletContextAttributes;
    }
 
    
    
    public ITestContext aggregate(final ITestContext context) {
        
        final TestContext newContext = new TestContext();
        
        newContext.setLocale(this.locale);
        newContext.getVariables().putAll(this.variables);
        newContext.getRequestParameters().putAll(this.requestParameters);
        newContext.getRequestAttributes().putAll(this.requestAttributes);
        newContext.getSessionAttributes().putAll(this.sessionAttributes);
        newContext.getServletContextAttributes().putAll(this.servletContextAttributes);
     
        if (context != null) {
            
            final Locale contextLocale = context.getLocale();
            if (contextLocale != null) {
                newContext.setLocale(context.getLocale());
            }
            final Map<String,ITestContextExpression> contextVariables = context.getVariables();
            if (contextVariables != null) {
                newContext.getVariables().putAll(contextVariables);
            }
            final Map<String,ITestContextExpression[]> contextRequestParameters = context.getRequestParameters();
            if (contextRequestParameters != null) {
                newContext.getRequestParameters().putAll(contextRequestParameters);
            }
            final Map<String,ITestContextExpression> contextRequestAttributes = context.getRequestAttributes();
            if (contextRequestAttributes != null) {
                newContext.getRequestAttributes().putAll(contextRequestAttributes);
            }
            final Map<String,ITestContextExpression> contextSessionAttributes = context.getSessionAttributes();
            if (contextSessionAttributes != null) {
                newContext.getSessionAttributes().putAll(contextSessionAttributes);
            }
            final Map<String,ITestContextExpression> contextServletContextAttributes = context.getServletContextAttributes();
            if (contextServletContextAttributes != null) {
                newContext.getServletContextAttributes().putAll(contextServletContextAttributes);
            }
            
        }
        
        return newContext;
        
    }
    
    
}
