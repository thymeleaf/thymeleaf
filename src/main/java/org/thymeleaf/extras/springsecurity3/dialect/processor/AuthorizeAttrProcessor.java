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
package org.thymeleaf.extras.springsecurity3.dialect.processor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.springsecurity3.authentication.AuthenticationUtils;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;
import org.thymeleaf.util.StringUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class AuthorizeAttrProcessor
        extends AbstractConditionalVisibilityAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize";
    
    
    
    private static final FilterChain DUMMY_CHAIN = new FilterChain() {
        public void doFilter(ServletRequest request, ServletResponse response) 
                throws IOException, ServletException {
           throw new UnsupportedOperationException();
        }
    };
    
    
    
    
    public AuthorizeAttrProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected boolean isVisible(final Arguments arguments, final Element element,
            final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        if (attributeValue == null || StringUtils.isEmpty(attributeValue).booleanValue()) {
            return false;
        }
        
        final IContext context = arguments.getContext();
        if (!(context instanceof IWebContext)) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                    IWebContext.class.getName() + ". Spring Security integration can only be used in " +
                    "web environements.");
        }
        final IWebContext webContext = (IWebContext) context;
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final HttpServletResponse response = webContext.getHttpServletResponse();
        final ServletContext servletContext = webContext.getServletContext();
        
        final Authentication authentication = AuthenticationUtils.getAuthenticationObject();

        if (authentication == null) {
            return false;
        }
        
        return authorizeUsingAccessExpression(attributeValue, authentication, request, response, servletContext);
        
    }
    


    protected boolean authorizeUsingAccessExpression(
            final String attributeValue, final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
    
        final WebSecurityExpressionHandler handler = getExpressionHandler(servletContext);

        Expression accessExpression = null;
        try {
            accessExpression = handler.getExpressionParser().parseExpression(attributeValue);
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happened trying to parse expression \"" +  
                    attributeValue + "\" for attribute '" + ATTR_NAME + "'", e);
        }

        final FilterInvocation filterInvocation = new FilterInvocation(request, response, DUMMY_CHAIN);

        if (ExpressionUtils.evaluateAsBoolean(
                accessExpression, 
                handler.createEvaluationContext(authentication, filterInvocation))) {
            return true;
        }

        return false;
    
    }
    
    
    
    
    protected WebSecurityExpressionHandler getExpressionHandler(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebSecurityExpressionHandler> expressionHandlers = 
                ctx.getBeansOfType(WebSecurityExpressionHandler.class);

        if (expressionHandlers.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebSecurityExpressionHandler instance could be found in the application " +
                    "context. There must be at least one in order to support expressions in '" +
                    ATTR_NAME + "' attributes.");
        }

        return (WebSecurityExpressionHandler) expressionHandlers.values().toArray()[0];
        
    }
    

    
}
