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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.expression.ExpressionObjectDefinition;
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
    private static final String AUTHENTICATION_EXPRESSION_OBJECT_DESCRIPTION = "The Spring Security authentication object (org.springframework.security.core.Authentication)";
    private static final boolean AUTHENTICATION_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition AUTHENTICATION_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(AUTHENTICATION_EXPRESSION_OBJECT_NAME, AUTHENTICATION_EXPRESSION_OBJECT_DESCRIPTION, AUTHENTICATION_EXPRESSION_OBJECT_CACHEABLE);

    public static final String AUTHORIZATION_EXPRESSION_OBJECT_NAME = "authorization";
    private static final String AUTHORIZATION_EXPRESSION_OBJECT_DESCRIPTION = "Utility methods for checking authorization based on expressions, URLs and Access Control Lists";
    private static final boolean AUTHORIZATION_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition AUTHORIZATION_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(AUTHORIZATION_EXPRESSION_OBJECT_NAME, AUTHORIZATION_EXPRESSION_OBJECT_DESCRIPTION, AUTHORIZATION_EXPRESSION_OBJECT_CACHEABLE);




    private static final Set<ExpressionObjectDefinition> ALL_EXPRESSION_OBJECT_DEFINITIONS_SET =
            Collections.unmodifiableSet(new LinkedHashSet<ExpressionObjectDefinition>(java.util.Arrays.asList(
                    new ExpressionObjectDefinition[]{
                            AUTHENTICATION_EXPRESSION_OBJECT_DEFINITION,
                            AUTHORIZATION_EXPRESSION_OBJECT_DEFINITION
                    }
            )));
    public static final Map<String,ExpressionObjectDefinition> ALL_EXPRESSION_OBJECT_DEFINITIONS;






    static {
        final Map<String,ExpressionObjectDefinition> allExpressionObjectDefinitions =
                new LinkedHashMap<String, ExpressionObjectDefinition>(ALL_EXPRESSION_OBJECT_DEFINITIONS_SET.size());
        for (final ExpressionObjectDefinition definition : ALL_EXPRESSION_OBJECT_DEFINITIONS_SET) {
            allExpressionObjectDefinitions.put(definition.getName(), definition);
        }
        ALL_EXPRESSION_OBJECT_DEFINITIONS = Collections.unmodifiableMap(allExpressionObjectDefinitions);
    }




    public SpringSecurityExpressionObjectFactory() {
        super();
    }




    public Map<String,ExpressionObjectDefinition> getObjectDefinitions() {
        return ALL_EXPRESSION_OBJECT_DEFINITIONS;
    }



    public Object buildObject(final IProcessingContext processingContext, final String expressionObjectName) {

        if (AUTHENTICATION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext.isWeb()) {
                return AuthUtils.getAuthenticationObject();
            }
        }

        if (AUTHORIZATION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext.isWeb()) {

                // We retrieve it like this in order to give it the opportunity to come from cache
                final Authentication authentication =
                        (Authentication) processingContext.getExpressionObjects().getObject(AUTHENTICATION_EXPRESSION_OBJECT_NAME);

                final IWebVariablesMap webVariablesMap = (IWebVariablesMap)processingContext.getVariables();
                final HttpServletRequest request = webVariablesMap.getRequest();
                final HttpServletResponse response = webVariablesMap.getResponse();
                final ServletContext servletContext = webVariablesMap.getServletContext();

                return new Authorization(processingContext, authentication, request, response, servletContext);

            }
            return null;
        }

        return null;

    }


}
