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
package org.thymeleaf.extras.springsecurity3.dialect;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.extras.springsecurity3.auth.AuthUtils;
import org.thymeleaf.extras.springsecurity3.auth.Authorization;
import org.thymeleaf.extras.springsecurity3.dialect.processor.AuthenticationAttrProcessor;
import org.thymeleaf.extras.springsecurity3.dialect.processor.AuthorizeAclAttrProcessor;
import org.thymeleaf.extras.springsecurity3.dialect.processor.AuthorizeAttrProcessor;
import org.thymeleaf.extras.springsecurity3.dialect.processor.AuthorizeUrlAttrProcessor;
import org.thymeleaf.processor.IProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class SpringSecurityDialect 
        extends AbstractDialect 
        implements IExpressionEnhancingDialect {

    public static final String DEFAULT_PREFIX = "sec";
    
    public static final String AUTHENTICATION_EXPRESSION_OBJECT_NAME = "authentication";
    public static final String AUTHORIZATION_EXPRESSION_OBJECT_NAME = "authorization";
    
    
    public SpringSecurityDialect() {
        super();
    }

    
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }

    
    public boolean isLenient() {
        return false;
    }



    
    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new AuthenticationAttrProcessor());
        processors.add(new AuthorizeAttrProcessor());
        // synonym (sec:authorize = sec:authorize-expr) for similarity with 
        // "authorize-url" and "autorize-acl"
        processors.add(new AuthorizeAttrProcessor(AuthorizeAttrProcessor.ATTR_NAME_EXPR));
        processors.add(new AuthorizeUrlAttrProcessor());
        processors.add(new AuthorizeAclAttrProcessor());
        return processors;
    }

    

    
    
    public Map<String, Object> getAdditionalExpressionObjects(
            final IProcessingContext processingContext) {
        
        final IContext context = processingContext.getContext();
        final IWebContext webContext =
                (context instanceof IWebContext? (IWebContext)context : null);
        
        final Map<String,Object> objects = new HashMap<String, Object>(3, 1.0f);
        
        /*
         * Create the #authentication and #authorization expression objects
         */
        if (webContext != null) {
            
            final HttpServletRequest request = webContext.getHttpServletRequest();
            final HttpServletResponse response = webContext.getHttpServletResponse();
            final ServletContext servletContext = webContext.getServletContext();
            
            if (request != null && response != null && servletContext != null) {
                
                final Authentication authentication = AuthUtils.getAuthenticationObject();
                final Authorization authorization = 
                        new Authorization(processingContext, authentication, request, response, servletContext); 
                        
                objects.put(AUTHENTICATION_EXPRESSION_OBJECT_NAME, authentication);
                objects.put(AUTHORIZATION_EXPRESSION_OBJECT_NAME, authorization);
                
            }
            
        }
       
        return objects;
        
    }
    
    
}
