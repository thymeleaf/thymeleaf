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

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.extras.springsecurity3.auth.AuthUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;

/**
 * Renders the element children (*tag content*) if the authenticated user is
 * authorized to see the specified URL.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class AuthorizeUrlAttrProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize-url";
    
    
    
    
    public AuthorizeUrlAttrProcessor(final String dialectPrefix) {
        super(dialectPrefix, ATTR_NAME, ATTR_PRECEDENCE);
    }



    @Override
    protected boolean isVisible(
            final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue) {

        final String attrValue = (attributeValue == null? null : attributeValue.trim());

        if (attrValue == null || attrValue.length() == 0) {
            return false;
        }
        
        final int spaceIndex = attrValue.indexOf(' ');
        final String url = 
                (spaceIndex < 0? attrValue : attrValue.substring(spaceIndex + 1)).trim();
        final String method =
                (spaceIndex < 0? "GET" : attrValue.substring(0, spaceIndex)).trim();

        if (!processingContext.isWeb()) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                    IWebContext.class.getName() + "). Spring Security integration can only be used in " +
                    "web environments.");
        }
        final IWebContext webContext = (IWebContext) processingContext.getVariables();
        
        final HttpServletRequest request = webContext.getRequest();
        final ServletContext servletContext = webContext.getServletContext();
        
        final Authentication authentication = AuthUtils.getAuthenticationObject();

        if (authentication == null) {
            return false;
        }
        
        return AuthUtils.authorizeUsingUrlCheck(
                url, method, authentication, request, servletContext);
        
    }

    
    
}
