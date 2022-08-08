/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class TestTemplateResolver implements ITemplateResolver {

    private final String template;
    

    
    public TestTemplateResolver(final String template) {
        super();
        this.template = template;
    }

    public String getName() {
        return "TEST EXPRESSION TEMPLATE RESOLVER";
    }

    public Integer getOrder() {
        return Integer.valueOf(1);
    }


    public TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {

        final int placeholderPos = this.template.indexOf("{%%}");
        final String resource =
                this.template.substring(0,placeholderPos) +
                        template +
                        this.template.substring(placeholderPos + 4);

        final ITemplateResource templateResource = new StringTemplateResource(resource);

        final TemplateResolution templateResolution =
                new TemplateResolution(
                        templateResource,
                        true, // For the sake of these tests, considering resource existence verified is fine
                        TemplateMode.HTML,
                        false,
                        new NonCacheableCacheEntryValidity());

        return templateResolution;

    }



}
