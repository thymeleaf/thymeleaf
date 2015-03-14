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

import java.util.Set;

import org.thymeleaf.aurora.DialectConfiguration;
import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface ITemplateEngineContext {

    // Cannot be null
    public Set<DialectConfiguration> getDialectConfigurations();

    // Cannot be null
    public Set<IDialect> getDialects();

    // Might be null if no standard dialect has been registered
    public String getStandardDialectPrefix();

    // Cannot be null
    public ITextRepository getTextRepository();

    // Cannot be null
    public ElementDefinitions getElementDefinitions();

    // Cannot be null
    public AttributeDefinitions getAttributeDefinitions();

    // Cannot be null
    public Set<IProcessor> getCDATASectionProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getCommentProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getDocTypeProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getElementProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getTextProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode);

    // Cannot be null
    public Set<IProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode);

}
