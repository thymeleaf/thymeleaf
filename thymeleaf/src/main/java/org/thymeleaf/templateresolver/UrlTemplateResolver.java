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

import java.net.MalformedURLException;
import java.util.Map;
import java.util.regex.Pattern;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.UrlTemplateResource;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractConfigurableTemplateResolver}
 *   and creates {@link UrlTemplateResource} instances for template resources.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely rewritten in Thymeleaf 3.0.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class UrlTemplateResolver extends AbstractConfigurableTemplateResolver {

    private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(.*?);jsessionid(.*?)");
    
    
    public UrlTemplateResolver() {
        super();
    }


    @Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {
        try {
            return new UrlTemplateResource(resourceName, characterEncoding);
        } catch (final MalformedURLException ignored) {
            return null;
        }
    }






    @Override
    protected ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        /*
         * This check is made so that we don't fill the cache with entries for the same
         * template with different jsessionid values.
         */
        if (JSESSIONID_PATTERN.matcher(template.toLowerCase()).matches()) {
            return NonCacheableCacheEntryValidity.INSTANCE;
        }
        return super.computeValidity(configuration, ownerTemplate, template, templateResolutionAttributes);
    }
    
    
}
