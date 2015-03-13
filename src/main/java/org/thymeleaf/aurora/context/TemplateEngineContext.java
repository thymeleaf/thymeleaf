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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.DialectConfiguration;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.aurora.processor.comment.ICommentProcessor;
import org.thymeleaf.aurora.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.aurora.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.aurora.processor.text.ITextProcessor;
import org.thymeleaf.aurora.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class TemplateEngineContext implements ITemplateEngineContext {

    private final Map<String,IDialect> dialectsByPrefix;
    private final Set<IDialect> dialects;

    private final ITextRepository textRepository;

    private final DialectConfiguration dialectConfiguration;


    public TemplateEngineContext(final Map<String,IDialect> dialectsByPrefix, final ITextRepository textRepository) {

        super();

        Validate.notNull(dialectsByPrefix, "Dialect map cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");

        this.dialectsByPrefix = Collections.unmodifiableMap(new LinkedHashMap<String,IDialect>(dialectsByPrefix));
        this.dialects = Collections.unmodifiableSet(new LinkedHashSet<IDialect>(this.dialectsByPrefix.values()));
        this.textRepository = textRepository;

        this.dialectConfiguration = DialectConfiguration.build(this.dialectsByPrefix);

    }



    public Set<IDialect> getDialects() {
        return this.dialects;
    }

    public String getStandardDialectPrefix() {
        return "th";
    }

    public ITextRepository getTextRepository() {
        return this.textRepository;
    }


    public ElementDefinitions getElementDefinitions() {
        return this.dialectConfiguration.getElementDefinitions();
    }


    public AttributeDefinitions getAttributeDefinitions() {
        return this.dialectConfiguration.getAttributeDefinitions();
    }


    public Set<IProcessor> getCDATASectionProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getCDATASectionProcessors(templateMode);
    }

    public Set<IProcessor> getCommentProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getCommentProcessors(templateMode);
    }

    public Set<IProcessor> getDocTypeProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getDocTypeProcessors(templateMode);
    }

    public Set<IProcessor> getElementProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getElementProcessors(templateMode);
    }

    public Set<IProcessor> getTextProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getTextProcessors(templateMode);
    }

    public Set<IProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getProcessingInstructionProcessors(templateMode);
    }

    public Set<IProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode) {
        return this.dialectConfiguration.getXMLDeclarationProcessors(templateMode);
    }

}
