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

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
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
public class AuthorizeUrlAttrProcessor
        extends AbstractConditionalVisibilityAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize-url";
    
    
    
    
    public AuthorizeUrlAttrProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected boolean isVisible(final Arguments arguments, final Element element,
            final String attributeName) {

        String attributeValue = element.getAttributeValue(attributeName);
        
        if (attributeValue == null || StringUtils.isEmpty(attributeValue).booleanValue()) {
            return false;
        }
        attributeValue = attributeValue.trim();
        
        final int spaceIndex = attributeValue.indexOf(' ');
        final String url = 
                (spaceIndex < 0? attributeValue : attributeValue.substring(0, spaceIndex)).trim();
        final String method =
                (spaceIndex < 0? "GET" : attributeValue.substring(spaceIndex + 1)).trim();

        final IContext context = arguments.getContext();
        if (!(context instanceof IWebContext)) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                    IWebContext.class.getName() + ". Spring Security integration can only be used in " +
                    "web environements.");
        }
        final IWebContext webContext = (IWebContext) context;
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final ServletContext servletContext = webContext.getServletContext();
        
        final Authentication authentication = AuthenticationUtils.getAuthenticationObject();

        if (authentication == null) {
            return false;
        }
        
        return authorizeUsingUrlCheck(url, method, authentication, request, servletContext);
        
    }
    

    
    protected boolean authorizeUsingUrlCheck(
            final String url, final String method, final Authentication authentication, 
            final HttpServletRequest request, final ServletContext servletContext) {
        
        return getPrivilegeEvaluator(servletContext).isAllowed(
                    request.getContextPath(), url, method, authentication) ? 
                            true : false;
        
    }


    
    protected WebInvocationPrivilegeEvaluator getPrivilegeEvaluator(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebInvocationPrivilegeEvaluator> privilegeEvaluators = 
                ctx.getBeansOfType(WebInvocationPrivilegeEvaluator.class);

        if (privilegeEvaluators.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebInvocationPrivilegeEvaluator instance could be found in the application " +
                    "context. There must be at least one in order to support the use of URL access checks in '" +
                    ATTR_NAME + "' attributes.");
        }

        return (WebInvocationPrivilegeEvaluator) privilegeEvaluators.values().toArray()[0];
        
    }
    
    
    
}
