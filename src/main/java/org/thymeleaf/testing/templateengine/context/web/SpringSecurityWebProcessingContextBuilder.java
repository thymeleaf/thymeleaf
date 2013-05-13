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
    public static final String DEFAULT_USERNAME_VARIABLE_NAME = "j_username";
    public static final String DEFAULT_PASSWORD_VARIABLE_NAME = "j_password";
    
    
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
        
        
        SecurityContextHolder.clearContext();
        
        final AuthenticationManager authenticationManager = 
                (AuthenticationManager) applicationContext.getBean(this.authenticationManagerBeanName);
        
        final Authentication authentication = 
                getAuthentication(applicationContext, testContext, testMessages, 
                        request, response, servletContext, locale, variables);

        
        if (authentication != null) {
            
            final Authentication fullAuthentication = 
                    authenticationManager.authenticate(authentication);
            
            final SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(fullAuthentication);
            
            final HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            
        }
        
    }
    
    
    
    /**
     * <p>
     *   Computes and returns a Spring Security {@link Authentication} object to be used 
     *   for authenticating the desired user for testing purposes.
     * </p>
     * <p>
     *   By default, an {@link UsernamePasswordAuthenticationToken} object is returned,
     *   built with the values of two context variables:
     * </p>
     * <ul>
     *   <li><tt>j_username</tt> for the user name.</li>
     *   <li><tt>j_password</tt> for the user password.</li>
     * </ul>
     * <p>
     *   If one or both of these variables are not present, null is returned and therefore
     *   no user will be considered to be authenticated.
     * </p>
     * 
     * @param applicationContext the application context, already initialized
     * @param testContext the {@link ITestContext} object
     * @param testMessages the {@link ITestMessages} object
     * @param request the HTTP request object
     * @param response the HTTP response object
     * @param servletContext the ServletContext object
     * @param locale the locale being used for this test
     * @param variables the context variables
     * @return the Authentication object, null if no user is to be considered authenticated.
     */
    @SuppressWarnings("unused")
    protected Authentication getAuthentication(final ApplicationContext applicationContext,
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Locale locale,
            final Map<String, Object> variables) {
        
        final Object usernameObj = variables.get(DEFAULT_USERNAME_VARIABLE_NAME);
        final Object passwordObj = variables.get(DEFAULT_PASSWORD_VARIABLE_NAME);
        
        if (usernameObj == null || passwordObj == null) {
            return null;
        }
        
        return new UsernamePasswordAuthenticationToken(usernameObj.toString(), passwordObj.toString());
        
    }
    
    
    
}
