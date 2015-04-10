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
package org.thymeleaf.aurora.engine;

import java.util.Locale;

import org.thymeleaf.aurora.ITemplateEngineConfiguration;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.context.IVariablesMap;
import org.thymeleaf.aurora.model.IModelFactory;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractTemplateProcessingContext implements ITemplateProcessingContext {

    private final ITemplateEngineConfiguration configuration;
    private final String templateName;
    private final TemplateMode templateMode;
    private final IModelFactory modelFactory;
    private final boolean web;
    private final Locale locale;
    private final IVariablesMap variablesMap;


    protected AbstractTemplateProcessingContext(
            final ITemplateEngineConfiguration configuration,
            final String templateName, final TemplateMode templateMode,
            final boolean web, final Locale locale, final IVariablesMap variablesMap) {

        super();

        Validate.notNull(configuration, "Template Engine Configuration cannot be null");
        Validate.notNull(templateName, "Template Name cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(variablesMap, "Variables Map cannot be null");

        this.configuration = configuration;
        this.templateName = templateName;
        this.templateMode = templateMode;
        this.locale = locale;
        this.variablesMap = variablesMap;
        this.modelFactory =
                new StandardModelFactory(
                        this.templateMode, this.configuration.getTextRepository(),
                        this.configuration.getAttributeDefinitions(), this.configuration.getElementDefinitions());
        this.web = web;

    }


    public final ITemplateEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    public final String getTemplateName() {
        return this.templateName;
    }

    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public final IModelFactory getModelFactory() {
        return this.modelFactory;
    }

    public final Locale getLocale() {
        return this.locale;
    }

    public final IVariablesMap getVariablesMap() {
        return this.variablesMap;
    }

    public boolean isWeb() {
        return this.web;
    }

}
