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
package thymeleafexamples.springmail.tools;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.context.IContext;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;

/**
 * This non-standard "template resolver" always return the template contents provided in the constructor.
 */
class MemoryTemplateResolver implements ITemplateResolver {

    private static final String NAME = "MemoryTemplateResolver";
    private static final Integer ORDER = 1;
    
    private final String templateContent;
    private final TemplateMode templateMode;

    public MemoryTemplateResolver(final String templateContent, final TemplateMode templateMode) {
        Validate.notNull(templateContent, "Template content must be non-null");
        Validate.notNull(templateMode, "Template mode must be non-null");
        this.templateContent = templateContent;
        this.templateMode = templateMode;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Integer getOrder() {
        return ORDER;
    }
    
    @Override
    public TemplateResolution resolveTemplate(final IEngineConfiguration configuration,
            final IContext context, final String template) {
        String templateName = "CustomTemplate";
        String resourceName = "CustomResource";
        IResourceResolver resourceResolver = new FixedMemoryResourceResolver(templateContent);
        String characterEncoding = "utf-8";
        ICacheEntryValidity validity = new NonCacheableCacheEntryValidity();
        return new TemplateResolution(templateName, resourceName, resourceResolver, characterEncoding,
            templateMode, validity);
    }
}
