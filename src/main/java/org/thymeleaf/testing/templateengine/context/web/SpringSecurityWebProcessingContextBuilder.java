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
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.util.Validate;



public class SpringSecurityWebProcessingContextBuilder extends SpringWebProcessingContextBuilder {

    
    public static final String DEFAULT_AUTHENTICATION_MANAGER_BEAN_NAME = "authenticationManager";

    
    private String authenticationManagerBeanName = DEFAULT_AUTHENTICATION_MANAGER_BEAN_NAME;
    
    
    
    public SpringSecurityWebProcessingContextBuilder() {
        super();
    }

    

    
    
    public String getAuthenticationManagerBeanName() {
        return this.authenticationManagerBeanName;
    }

    public void setAuthenticationManagerBeanName(final String authenticationManagerBeanName) {
        Validate.notNull(authenticationManagerBeanName, "Name of AuthenticationManager bean cannot be set to null");
        this.authenticationManagerBeanName = authenticationManagerBeanName;
    }

    




    @Override
    protected final void initSpring(final ApplicationContext applicationContext,
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Locale locale,
            final Map<String, Object> variables) {
        
        super.initSpring(applicationContext, testContext, testMessages, request,
                response, servletContext, locale, variables);
        
        
        final AuthenticationManager authenticationManager = 
                (AuthenticationManager) applicationContext.getBean(this.authenticationManagerBeanName);
        
        final Authentication authentication = 
                getAuthentication(applicationContext, testContext, testMessages, 
                        request, response, servletContext, locale, variables);
        
        final Authentication fullAuthentication = 
                authenticationManager.authenticate(authentication);
        
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(fullAuthentication);
        
        final HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        
    }
    
    
    
    @SuppressWarnings("unused")
    protected Authentication getAuthentication(final ApplicationContext applicationContext,
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Locale locale,
            final Map<String, Object> variables) {
        
        return new UsernamePasswordAuthenticationToken("jim", "demo");
        
    }
    
    
    
}
