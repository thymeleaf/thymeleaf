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
package org.thymeleaf.templateresolver;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

public class AttributeTesterTemplateResolver extends AbstractTemplateResolver {

    private final String temp = "<div th:insert='frag'>...</div>";
    private final String frag = "<p th:text='hello'>...</p>";

    private final Map<String,Object> attributes;

    public boolean tempCalled = false;
    public boolean fragCalled = false;




    public AttributeTesterTemplateResolver(final Map<String,Object> attributes) {
        super();
        this.attributes = attributes == null? null : new HashMap<String, Object>(attributes);
    }


    @Override
    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        if (!tempCalled && template.equals("temp")) {
            if (this.attributes == null) {
                if (templateResolutionAttributes == null) {
                    this.tempCalled = true;
                    return new StringTemplateResource(temp);
                }
            } else if (this.attributes.equals(new HashMap<String, Object>(templateResolutionAttributes))) {
                this.tempCalled = true;
                return new StringTemplateResource(temp);
            }
            throw new RuntimeException("Unexpected template resolution attributes: " + templateResolutionAttributes);
        }
        if (!fragCalled && template.equals("frag")) {
            if (this.attributes == null) {
                if (templateResolutionAttributes == null) {
                    this.fragCalled = true;
                    return new StringTemplateResource(frag);
                }
            } else if (this.attributes.equals(new HashMap<String, Object>(templateResolutionAttributes))) {
                this.fragCalled = true;
                return new StringTemplateResource(frag);
            }
            throw new RuntimeException("Unexpected template resolution attributes: " + templateResolutionAttributes);
        }
        throw new RuntimeException("Unknown or repeated template: " + template);
    }

    @Override
    protected TemplateMode computeTemplateMode(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return TemplateMode.HTML;
    }

    @Override
    protected ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return AlwaysValidCacheEntryValidity.INSTANCE;
    }
}
