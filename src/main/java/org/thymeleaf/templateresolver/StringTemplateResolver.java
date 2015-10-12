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
package org.thymeleaf.templateresolver;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractConfigurableTemplateResolver}
 *   and creates {@link StringTemplateResource} instances for template resources.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StringTemplateResolver extends AbstractConfigurableTemplateResolver {



    public StringTemplateResolver() {
        super();
    }


    @Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String template, final String resourceName, final String characterEncoding) {
        return new StringTemplateResource(template, resourceName);
    }

}
