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
package org.thymeleaf.linkbuilder;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.engine.TemplateData;


public class TestEngineContext extends EngineContext {

    private final String linkPrefix;

    public TestEngineContext(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final Map<String, Object> templateResolutionAttributes, final Locale locale,
            final Map<String, Object> variables, final String linkPrefix) {
        super(configuration, templateData, templateResolutionAttributes, locale, variables);
        this.linkPrefix = linkPrefix;
    }


    public String getLinkPrefix() {
        return this.linkPrefix;
    }

}
