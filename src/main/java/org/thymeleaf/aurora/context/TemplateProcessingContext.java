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

import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class TemplateProcessingContext implements ITemplateProcessingContext {

    private final ITemplateEngineContext templateEngineContext;
    private final String templateName;
    private final TemplateMode templateMode;


    public TemplateProcessingContext(
            final ITemplateEngineContext templateEngineContext,
            final String templateName, final TemplateMode templateMode) {
        super();
        Validate.notNull(templateEngineContext, "Template Engine Context cannot be null");
        Validate.notNull(templateName, "Template Name cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        this.templateEngineContext = templateEngineContext;
        this.templateName = templateName;
        this.templateMode = templateMode;
    }


    public ITemplateEngineContext getTemplateEngineContext() {
        return this.templateEngineContext;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

}
