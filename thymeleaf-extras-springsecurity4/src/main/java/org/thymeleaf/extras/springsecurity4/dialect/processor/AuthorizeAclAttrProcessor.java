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
package org.thymeleaf.extras.springsecurity4.dialect.processor;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.springsecurity4.auth.AclAuthUtils;
import org.thymeleaf.extras.springsecurity4.auth.AuthUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.TextLiteralExpression;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;

/**
 * Takes the form sec:authorize-acl="object :: permissions", renders the element
 * children (*tag content*) if the authenticated user has the specified
 * permissions on the specified domain object, according to Spring Source's
 * Access Control List system.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class AuthorizeAclAttrProcessor extends AbstractStandardConditionalVisibilityTagProcessor {


    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize-acl";

    
    private static final String VALUE_SEPARATOR = "::";



    public AuthorizeAclAttrProcessor(final String dialectPrefix) {
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

        if (!processingContext.isWeb()) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                            IWebContext.class.getName() + "). Spring Security integration can only be used in " +
                            "web environments.");
        }

        final IWebContext webContext = (IWebContext) processingContext.getVariables();
        final ServletContext servletContext = webContext.getServletContext();

        final Authentication authentication = AuthUtils.getAuthenticationObject();
        if (authentication == null) {
            return false;
        }

        final ApplicationContext applicationContext = AuthUtils.getContext(servletContext);

        final IEngineConfiguration configuration = processingContext.getConfiguration();

        final int separatorPos = attrValue.lastIndexOf(VALUE_SEPARATOR);
        if (separatorPos == -1) {
            throw new TemplateProcessingException(
                    "Could not parse \"" + attributeValue + "\" as an access control list " +
                            "expression. Syntax should be \"[domain object expression] :: [permissions]\"");
        }

        final String domainObjectExpression = attrValue.substring(0,separatorPos).trim();
        final String permissionsExpression = attrValue.substring(separatorPos + 2).trim();

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression domainObjectExpr =
                getExpressionDefaultToLiteral(expressionParser, processingContext, domainObjectExpression);
        final IStandardExpression permissionsExpr =
                getExpressionDefaultToLiteral(expressionParser, processingContext, permissionsExpression);

        final Object domainObject = domainObjectExpr.execute(processingContext);

        final Object permissionsObject = permissionsExpr.execute(processingContext);
        final String permissionsStr =
                (permissionsObject == null? null : permissionsObject.toString());

        return AclAuthUtils.authorizeUsingAccessControlList(
                domainObject, applicationContext, permissionsStr, authentication, servletContext);

    }





    protected static IStandardExpression getExpressionDefaultToLiteral(
            final IStandardExpressionParser expressionParser, final IProcessingContext processingContext, final String input) {

        final IStandardExpression expression = expressionParser.parseExpression(processingContext, input);
        if (expression == null) {
            return new TextLiteralExpression(input);
        }
        return expression;

    }
    
    
}
