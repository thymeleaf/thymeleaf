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
package org.thymeleaf.extras.springsecurity3.auth;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.exceptions.TemplateProcessingException;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class AuthUtils {


    
    private static final FilterChain DUMMY_CHAIN = new FilterChain() {
        public void doFilter(ServletRequest request, ServletResponse response) 
                throws IOException, ServletException {
           throw new UnsupportedOperationException();
        }
    };
    
    
    
    private AuthUtils() {
        super();
    }

    
    

    public static Authentication getAuthenticationObject() {
        
        if ((SecurityContextHolder.getContext() == null)) {
            return null;
        }

        final Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            // There is an Authentication object, but 
            return null;
        }
        
        return authentication;
        
    }

    

    public static Object getAuthenticationProperty(final Authentication authentication, final String property) {
        
        if (authentication == null) {
            return null;
        }
        
        try {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(authentication);
            return wrapper.getPropertyValue(property);
        } catch (BeansException e) {
            throw new TemplateProcessingException(
                    "Error retrieving value for property \"" + property + "\" of authentication " + 
                    "object of class " + authentication.getClass().getName(), e);
        }

    }
    
    
    
    

    public static boolean authorizeUsingAccessExpression(
            final String accessExpression, final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
    
        final WebSecurityExpressionHandler handler = getExpressionHandler(servletContext);

        Expression expressionObject = null;
        try {
            expressionObject = handler.getExpressionParser().parseExpression(accessExpression);
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happened trying to parse Spring Security access expression \"" +  
                    accessExpression + "\"", e);
        }

        final FilterInvocation filterInvocation = new FilterInvocation(request, response, DUMMY_CHAIN);

        if (ExpressionUtils.evaluateAsBoolean(
                expressionObject, 
                handler.createEvaluationContext(authentication, filterInvocation))) {
            return true;
        }

        return false;
    
    }
    
    
    
    
    private static WebSecurityExpressionHandler getExpressionHandler(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebSecurityExpressionHandler> expressionHandlers = 
                ctx.getBeansOfType(WebSecurityExpressionHandler.class);

        if (expressionHandlers.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebSecurityExpressionHandler instance could be found in the application " +
                    "context. There must be at least one in order to support expressions in Spring Security " +
                    "authorization queries.");
        }

        return (WebSecurityExpressionHandler) expressionHandlers.values().toArray()[0];
        
    }
    
    
    
    
    
    
    public static boolean authorizeUsingUrlCheck(
            final String url, final String method, final Authentication authentication, 
            final HttpServletRequest request, final ServletContext servletContext) {
        
        return getPrivilegeEvaluator(servletContext).isAllowed(
                    request.getContextPath(), url, method, authentication) ? 
                            true : false;
        
    }


    


    
    private static WebInvocationPrivilegeEvaluator getPrivilegeEvaluator(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebInvocationPrivilegeEvaluator> privilegeEvaluators = 
                ctx.getBeansOfType(WebInvocationPrivilegeEvaluator.class);

        if (privilegeEvaluators.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebInvocationPrivilegeEvaluator instance could be found in the application " +
                    "context. There must be at least one in order to support URL access checks in " +
                    "Spring Security authorization queries.");
        }

        return (WebInvocationPrivilegeEvaluator) privilegeEvaluators.values().toArray()[0];
        
    }
    
    
}
