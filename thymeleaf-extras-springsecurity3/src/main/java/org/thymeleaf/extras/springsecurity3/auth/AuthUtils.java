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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.extras.springsecurity3.util.SpringSecurityWebApplicationContextUtils;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class AuthUtils {


    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
    
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
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Obtaining authentication object.",
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        if ((SecurityContextHolder.getContext() == null)) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] No security context found, no authentication object returned.",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            return null;
        }

        final Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] No authentication object found in context.",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            return null;
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Authentication object of class {} found in context for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), authentication.getClass().getName()}, authentication.getName());
        }
        
        return authentication;
        
    }

    

    public static Object getAuthenticationProperty(final Authentication authentication, final String property) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Reading property \"{}\" from authentication object.",
                    new Object[] {TemplateEngine.threadIndex(), property});
        }
        
        if (authentication == null) {
            return null;
        }
        
        try {
            
            final BeanWrapperImpl wrapper = new BeanWrapperImpl(authentication);
            final Object propertyObj = wrapper.getPropertyValue(property);
            
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Property \"{}\" obtained from authentication object " +
                		"for user \"{}\". Returned value of class {}.",
                        new Object[] {TemplateEngine.threadIndex(), property, authentication.getName(), 
                                (propertyObj == null? null : propertyObj.getClass().getName())});
            }
            
            return propertyObj;
            
        } catch (BeansException e) {
            throw new TemplateProcessingException(
                    "Error retrieving value for property \"" + property + "\" of authentication " + 
                    "object of class " + authentication.getClass().getName(), e);
        }

    }
    
    
    
    

    public static boolean authorizeUsingAccessExpression(
            final IExpressionContext context,
            final String accessExpression, final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
    
        Validate.notNull(context, "Context cannot be null");
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization using access expression \"{}\" for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
        }

        /*
         * In case this expression is specified as a standard variable expression (${...}), clean it.
         */
        final String expr =
                ((accessExpression != null && accessExpression.startsWith("${") && accessExpression.endsWith("}"))?
                        accessExpression.substring(2, accessExpression.length() - 1) :
                        accessExpression);
        
        final WebSecurityExpressionHandler handler = getExpressionHandler(servletContext);

        Expression expressionObject = null;
        try {
            expressionObject = handler.getExpressionParser().parseExpression(expr);
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happened trying to parse Spring Security access expression \"" +  
                    expr + "\"", e);
        }

        final FilterInvocation filterInvocation = new FilterInvocation(request, response, DUMMY_CHAIN);
        
        final EvaluationContext evaluationContext = handler.createEvaluationContext(authentication, filterInvocation);
        
        /*
         * Initialize the context variables map.
         * 
         * This will allow SpringSecurity expressions to include any variables from
         * the IContext just by accessing them as properties of the "#vars" utility object.
         */
        IExpressionObjects expressionObjects = context.getExpressionObjects();

        // We add Thymeleaf's wrapper on top of the SpringSecurity basic evaluation context
        // We need to do this through a version-independent wrapper because the classes we will use for the
        // EvaluationContext wrapper are in the org.thymeleaf.spring3.* or org.thymeleaf.spring4.* packages,
        // depending on the version of Spring we are using.
        final EvaluationContext wrappedEvaluationContext =
                SpringVersionSpecificUtils.wrapEvaluationContext(evaluationContext, expressionObjects);

        
        if (ExpressionUtils.evaluateAsBoolean(expressionObject, wrappedEvaluationContext)) {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Checked authorization using access expression \"{}\" for user \"{}\". Access GRANTED.",
                        new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
            }
            
            return true;
            
        }

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checked authorization using access expression \"{}\" for user \"{}\". Access DENIED.",
                    new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
        }
        
        return false;
    
    }
    
    
    
    
    private static WebSecurityExpressionHandler getExpressionHandler(final ServletContext servletContext) {

        final ApplicationContext ctx = getContext(servletContext);
        
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
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization for URL \"{}\" and method \"{}\" for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), url, method, (authentication == null? null : authentication.getName())});
        }
        
        final boolean result =
                getPrivilegeEvaluator(servletContext, request).isAllowed(
                    request.getContextPath(), url, method, authentication) ? 
                            true : false;

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checked authorization for URL \"{}\" and method \"{}\" for user \"{}\". " +
                    (result? "Access GRANTED." : "Access DENIED."),
                    new Object[] {TemplateEngine.threadIndex(), url, method, (authentication == null? null : authentication.getName())});
        }
        
        return result;
        
    }






    private static WebInvocationPrivilegeEvaluator getPrivilegeEvaluator(
            final ServletContext servletContext, final HttpServletRequest request) {

        final WebInvocationPrivilegeEvaluator privEvaluatorFromRequest =
                (WebInvocationPrivilegeEvaluator) request.getAttribute(WebAttributes.WEB_INVOCATION_PRIVILEGE_EVALUATOR_ATTRIBUTE);
        if (privEvaluatorFromRequest != null) {
            return privEvaluatorFromRequest;
        }

        final ApplicationContext ctx = getContext(servletContext);

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



    
    public static ApplicationContext getContext(final ServletContext servletContext) {
        return SpringSecurityWebApplicationContextUtils.findRequiredWebApplicationContext(servletContext);
    }
    
    
}
