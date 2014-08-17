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
package org.thymeleaf.extras.springsecurity3.auth;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class Authorization {


    private final IProcessingContext processingContext;
    private final Authentication authentication;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    
    


    public Authorization(
            final IProcessingContext processingContext,
            final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
        
        super();

        this.processingContext = processingContext;
        this.authentication = authentication;
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        
    }


    public IProcessingContext getProcessingContext() {
        return this.processingContext;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }


    public HttpServletRequest getRequest() {
        return this.request;
    }


    public HttpServletResponse getResponse() {
        return this.response;
    }


    public ServletContext getServletContext() {
        return this.servletContext;
    }



    // Synonym method
    public boolean expr(final String expression) {
        return expression(expression);
    }


    public boolean expression(final String expression) {
        
        Validate.notEmpty(expression, "Access expression cannot be null");
        
        return AuthUtils.authorizeUsingAccessExpression(
                this.processingContext, expression, this.authentication, this.request, this.response, this.servletContext);
        
    }
    

    
    
    public boolean url(final String url) {
        return url("GET", url);
    }

    
    
    public boolean url(final String method, final String url) {
        
        Validate.notEmpty(url, "URL cannot be null");
        
        return AuthUtils.authorizeUsingUrlCheck(
                url, method, this.authentication, this.request, this.servletContext);
        
    }

    
}
