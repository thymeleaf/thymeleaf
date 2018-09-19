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
package org.thymeleaf.extras.springsecurity5.dialect.processor;

import org.springframework.security.core.Authentication;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.extras.springsecurity5.auth.AuthUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Renders the element children (*tag content*) if the authenticated user is
 * authorized to see it according to the specified <i>Spring Security
 * expression</i>.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class AuthorizeAttrProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "authorize";
    public static final String ATTR_NAME_EXPR = "authorize-expr";




    public AuthorizeAttrProcessor(final TemplateMode templateMode, final String dialectPrefix, final String attrName) {
        super(templateMode, dialectPrefix, attrName, ATTR_PRECEDENCE);
    }






    @Override
    protected boolean isVisible(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue) {

        final String attrValue = (attributeValue == null? null : attributeValue.trim());

        if (attrValue == null || attrValue.length() == 0) {
            return false;
        }

        final Authentication authentication = AuthUtils.getAuthenticationObject(context);

        if (authentication == null) {
            return false;
        }

        return AuthUtils.authorizeUsingAccessExpression(context, attrValue, authentication);

    }
    

    
}
