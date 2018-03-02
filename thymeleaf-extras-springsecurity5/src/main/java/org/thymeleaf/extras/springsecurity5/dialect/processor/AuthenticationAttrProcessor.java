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
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Outputs a property of the authentication object, similar to the Spring
 * Security &lt;sec:authentication/&gt; JSP tag.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class AuthenticationAttrProcessor extends AbstractAttributeTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 1300;
    public static final String ATTR_NAME = "authentication";




    public AuthenticationAttrProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix,  null, false, ATTR_NAME, true, ATTR_PRECEDENCE, true);
    }




    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final String attrValue = (attributeValue == null? null : attributeValue.trim());

        if (attrValue == null || attrValue.length() == 0) {
            return;
        }

        final Authentication authentication = AuthUtils.getAuthenticationObject(context);
        final Object authenticationProperty = AuthUtils.getAuthenticationProperty(authentication, attrValue);

        if (authenticationProperty == null) {
            return;
        }

        structureHandler.setBody(authenticationProperty.toString(), false);

    }

    
}
