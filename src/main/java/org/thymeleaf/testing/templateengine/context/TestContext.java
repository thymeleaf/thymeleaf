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
package org.thymeleaf.testing.templateengine.context;

import java.util.LinkedHashMap;
import java.util.Map;


public final class TestContext implements ITestContext {

    private ITestContextExpression locale = null;
    private Map<String,ITestContextExpression> variables = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression[]> requestParameters = new LinkedHashMap<String, ITestContextExpression[]>();
    private Map<String,ITestContextExpression> requestAttributes = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression> sessionAttributes = new LinkedHashMap<String, ITestContextExpression>();
    private Map<String,ITestContextExpression> servletContextAttributes = new LinkedHashMap<String, ITestContextExpression>();

    
    public TestContext() {
        super();
    }
   

    public ITestContextExpression getLocale() {
        return this.locale;
    }

    public void setLocale(final ITestContextExpression locale) {
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
            
            final ITestContextExpression contextLocale = context.getLocale();
            if (contextLocale != null) {
                newContext.setLocale(context.getLocale());
            }
            final Map<String,ITestContextExpression> contextVariables = context.getVariables();
            if (contextVariables != null) {
                mergeMaps(newContext.getVariables(),contextVariables);
            }
            final Map<String,ITestContextExpression[]> contextRequestParameters = context.getRequestParameters();
            if (contextRequestParameters != null) {
                mergeMaps(newContext.getRequestParameters(),contextRequestParameters);
            }
            final Map<String,ITestContextExpression> contextRequestAttributes = context.getRequestAttributes();
            if (contextRequestAttributes != null) {
                mergeMaps(newContext.getRequestAttributes(),contextRequestAttributes);
            }
            final Map<String,ITestContextExpression> contextSessionAttributes = context.getSessionAttributes();
            if (contextSessionAttributes != null) {
                mergeMaps(newContext.getSessionAttributes(),contextSessionAttributes);
            }
            final Map<String,ITestContextExpression> contextServletContextAttributes = context.getServletContextAttributes();
            if (contextServletContextAttributes != null) {
                mergeMaps(newContext.getServletContextAttributes(),contextServletContextAttributes);
            }
            
        }
        
        return newContext;
        
    }
    
    
    
    private static <T> void mergeMaps(final Map<String,T> target, final Map<String,T> newEntries) {
        // This merging method is needed in order to make sure that an entry (a key) with several
        // appearances appears in its last position, an not in a previous one in which not all its required
        // data might be available.
        for (final Map.Entry<String,T> newEntry : newEntries.entrySet()) {
            final String newEntryKey = newEntry.getKey();
            final T newEntryValue = newEntry.getValue();
            target.remove(newEntryKey);
            target.put(newEntryKey, newEntryValue);
        }
    }
    
    
}
