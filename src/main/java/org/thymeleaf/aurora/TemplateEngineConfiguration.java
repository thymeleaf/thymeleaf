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
package org.thymeleaf.aurora;

import java.util.List;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.engine.ITemplateHandler;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class TemplateEngineConfiguration implements ITemplateEngineConfiguration {


    private final DialectSetConfiguration dialectSetConfiguration;
    private final ITextRepository textRepository;


    public TemplateEngineConfiguration(final Set<DialectConfiguration> dialectConfigurations, final ITextRepository textRepository) {

        super();

        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");

        this.dialectSetConfiguration = DialectSetConfiguration.build(dialectConfigurations);
        this.textRepository = textRepository;

    }




    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectSetConfiguration.getDialectConfigurations();
    }

    public Set<IDialect> getDialects() {
        return this.dialectSetConfiguration.getDialects();
    }

    public String getStandardDialectPrefix() {
        return this.dialectSetConfiguration.getStandardDialectPrefix();
    }

    public ITextRepository getTextRepository() {
        return this.textRepository;
    }


    public ElementDefinitions getElementDefinitions() {
        return this.dialectSetConfiguration.getElementDefinitions();
    }


    public AttributeDefinitions getAttributeDefinitions() {
        return this.dialectSetConfiguration.getAttributeDefinitions();
    }


    public Set<IProcessor> getCDATASectionProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCDATASectionProcessors(templateMode);
    }

    public Set<IProcessor> getCommentProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCommentProcessors(templateMode);
    }

    public Set<IProcessor> getDocTypeProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getDocTypeProcessors(templateMode);
    }

    public Set<IProcessor> getElementProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getElementProcessors(templateMode);
    }

    public Set<IProcessor> getTextProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getTextProcessors(templateMode);
    }

    public Set<IProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getProcessingInstructionProcessors(templateMode);
    }

    public Set<IProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getXMLDeclarationProcessors(templateMode);
    }

    public List<Class<? extends ITemplateHandler>> getPreProcessors() {
        return this.dialectSetConfiguration.getPreProcessors();
    }

    public List<Class<? extends ITemplateHandler>> getPostProcessors() {
        return this.dialectSetConfiguration.getPostProcessors();
    }

}
