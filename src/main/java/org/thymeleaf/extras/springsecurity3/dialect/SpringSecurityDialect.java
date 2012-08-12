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
package org.thymeleaf.extras.springsecurity3.dialect;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class SpringSecurityDialect 
        extends AbstractDialect 
        implements IExpressionEnhancingDialect {

    public static final String DEFAULT_PREFIX = "security";
    
    public static final String AUTHENTICATION_EXPRESSION_OBJECT_NAME = "authentication";
    
    
    public SpringSecurityDialect() {
        super();
    }

    
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }

    
    public boolean isLenient() {
        return false;
    }



    public Map<String, Object> getAdditionalExpressionObjects(
            final IProcessingContext processingContext) {
        
        final IContext context = processingContext.getContext();
        final IWebContext webContext =
                (context instanceof IWebContext? (IWebContext)context : null);
        
        final Map<String,Object> objects = new HashMap<String, Object>();
        
        /*
         * Create the #authentication expression object
         */
        if (webContext != null) {
            final HttpServletRequest request = webContext.getHttpServletRequest();
            if (request != null) {
                objects.put(AUTHENTICATION_EXPRESSION_OBJECT_NAME, request.getUserPrincipal());
            }
        }
       
        return objects;
        
    }

    
    
}
