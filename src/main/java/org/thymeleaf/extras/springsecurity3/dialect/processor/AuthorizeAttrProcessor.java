/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.extras.springsecurity3.auth.AuthUtils;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;

/**
 * Renders the element children (*tag content*) if the authenticated user is
 * authorized to see it according to the specified <i>Spring Security
 * expression</i>.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class AuthorizeAttrProcessor
        extends AbstractConditionalVisibilityAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize";
    public static final String ATTR_NAME_EXPR = "authorize-expr";
    
    
    
    
    public AuthorizeAttrProcessor() {
        super(ATTR_NAME);
    }

    
    public AuthorizeAttrProcessor(final String attrName) {
        super(attrName);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected boolean isVisible(final Arguments arguments, final Element element,
            final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        if (attributeValue == null || attributeValue.trim().equals("")) {
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
        
        final Authentication authentication = AuthUtils.getAuthenticationObject();

        if (authentication == null) {
            return false;
        }
        
        return AuthUtils.authorizeUsingAccessExpression(
                arguments, attributeValue, authentication, request, response, servletContext);
        
    }
    

    
}
