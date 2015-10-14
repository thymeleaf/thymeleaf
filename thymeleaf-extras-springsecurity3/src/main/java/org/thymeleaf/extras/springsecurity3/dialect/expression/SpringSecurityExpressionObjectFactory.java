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
package org.thymeleaf.extras.springsecurity3.dialect.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity3.auth.AuthUtils;
import org.thymeleaf.extras.springsecurity3.auth.Authorization;

/**
 * <p>
 *   Builds the expression objects to be used by the Spring Security dialect.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class SpringSecurityExpressionObjectFactory implements IExpressionObjectFactory {


    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" See below.
     */

    public static final String AUTHENTICATION_EXPRESSION_OBJECT_NAME = "authentication";
    public static final String AUTHORIZATION_EXPRESSION_OBJECT_NAME = "authorization";




    protected static final Set<String> ALL_EXPRESSION_OBJECT_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(java.util.Arrays.asList(
                    new String[]{
                            AUTHENTICATION_EXPRESSION_OBJECT_NAME,
                            AUTHORIZATION_EXPRESSION_OBJECT_NAME
                    }
            )));




    public SpringSecurityExpressionObjectFactory() {
        super();
    }




    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }



    public boolean isCacheable(final String expressionObjectName) {
        // All expression objects created by this factory are cacheable (template-scope)
        return true;
    }



    public Object buildObject(final IExpressionContext context, final String expressionObjectName) {

        if (AUTHENTICATION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context.isWeb()) {
                return AuthUtils.getAuthenticationObject();
            }
        }

        if (AUTHORIZATION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context.isWeb()) {

                // We retrieve it like this in order to give it the opportunity to come from cache
                final Authentication authentication =
                        (Authentication) context.getExpressionObjects().getObject(AUTHENTICATION_EXPRESSION_OBJECT_NAME);

                final IWebVariablesMap webVariablesMap = (IWebVariablesMap)context.getVariables();
                final HttpServletRequest request = webVariablesMap.getRequest();
                final HttpServletResponse response = webVariablesMap.getResponse();
                final ServletContext servletContext = webVariablesMap.getServletContext();

                return new Authorization(context, authentication, request, response, servletContext);

            }
            return null;
        }

        return null;

    }


}
