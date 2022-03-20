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
package org.thymeleaf.templateresolver;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.WebApplicationTemplateResource;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebApplication;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractConfigurableTemplateResolver}
 *   and creates {@link WebApplicationTemplateResource} instances for template resources.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public class WebApplicationTemplateResolver extends AbstractConfigurableTemplateResolver {


    private final IWebApplication webApplication;



    public WebApplicationTemplateResolver(final IWebApplication webApplication) {
        super();
        Validate.notNull(webApplication, "Web Application object cannot be null");
        this.webApplication = webApplication;
    }


    @Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {
        return new WebApplicationTemplateResource(this.webApplication, resourceName, characterEncoding);
    }

}
