/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.springsecurity5.auth;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class Authorization {


    private final IExpressionContext context;
    private final Authentication authentication;

    


    public Authorization(
            final IExpressionContext context,
            final Authentication authentication) {
        
        super();

        this.context = context;
        this.authentication = authentication;

    }


    public IExpressionContext getContext() {
        return this.context;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }



    // Synonym method
    public boolean expr(final String expression) {
        return expression(expression);
    }


    public boolean expression(final String expression) {
        
        Validate.notEmpty(expression, "Access expression cannot be null");
        
        return AuthUtils.authorizeUsingAccessExpression(this.context, expression, this.authentication);
        
    }
    

    
    
    public boolean url(final String url) {
        return url("GET", url);
    }

    
    
    public boolean url(final String method, final String url) {
        
        Validate.notEmpty(url, "URL cannot be null");
        
        return AuthUtils.authorizeUsingUrlCheck(this.context, url, method, this.authentication);
        
    }

    
}
