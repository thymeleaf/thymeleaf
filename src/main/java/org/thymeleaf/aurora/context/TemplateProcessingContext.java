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
package org.thymeleaf.aurora.context;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public final class TemplateProcessingContext extends AbstractTemplateProcessingContext {


    public TemplateProcessingContext(
            final IEngineConfiguration configuration,
            final TemplateResolution templateResolution,
            final Locale locale, final Map<String, Object> variables) {
        super(configuration, templateResolution, locale, variables);
    }

    public TemplateProcessingContext(
            final IEngineConfiguration configuration,
            final TemplateResolution templateResolution,
            final IContext context) {
        super(configuration, templateResolution, context);
    }

}
