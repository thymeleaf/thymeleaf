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

import org.springframework.security.core.Authentication;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.extras.springsecurity3.auth.AuthUtils;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;

/**
 * Outputs a property of the authentication object, similar to the Spring
 * Security &lt;sec:authentication/&gt; JSP tag.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class AuthenticationAttrProcessor
        extends AbstractTextChildModifierAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 1300;
    public static final String ATTR_NAME = "authentication";
    
    
    
    
    public AuthenticationAttrProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    

    
    
    @Override
    protected String getText(final Arguments arguments, final Element element,
            final String attributeName) {


        final String attributeValue = element.getAttributeValue(attributeName);

        if (attributeValue == null || attributeValue.trim().equals("")) {
            return null;
        }
        
        final Authentication authentication = AuthUtils.getAuthenticationObject();
        final Object authenticationProperty = 
                AuthUtils.getAuthenticationProperty(authentication, attributeValue);
        
        if (authenticationProperty == null) {
            return null;
        }
        
        return authenticationProperty.toString();
        
    }


    
}
