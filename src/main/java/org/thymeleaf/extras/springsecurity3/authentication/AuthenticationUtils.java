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
package org.thymeleaf.extras.springsecurity3.authentication;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.exceptions.TemplateProcessingException;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class AuthenticationUtils {


    
    private AuthenticationUtils() {
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
                    "object of class " + authentication.getClass().getName());
        }

    }
    
}
