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
package org.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.message.absent.IAbsentMessageFormatter;
import org.thymeleaf.message.resolver.IMessageResolver;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.document.IDocumentProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class EngineConfiguration implements IEngineConfiguration {

    private final DialectSetConfiguration dialectSetConfiguration;
    private final ITextRepository textRepository;
    private final Set<ITemplateResolver> templateResolvers;
    private final Set<IMessageResolver> messageResolvers;
    private final ICacheManager cacheManager;
    private final IAbsentMessageFormatter absentMessageFormatter;


    public EngineConfiguration(
            final Set<ITemplateResolver> templateResolvers,
            final Set<IMessageResolver> messageResolvers,
            final Set<DialectConfiguration> dialectConfigurations,
            final ICacheManager cacheManager,
            final ITextRepository textRepository,
            final IAbsentMessageFormatter absentMessageFormatter) {

        super();

        Validate.notNull(templateResolvers, "Template Reolver set cannot be null");
        Validate.isTrue(templateResolvers.size() > 0, "Template Reolver set cannot be empty");
        Validate.containsNoNulls(templateResolvers, "Template Reolver set cannot contain any nulls");
        Validate.notNull(messageResolvers, "Message Resolver set cannot be null");
        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");
        // Cache Manager CAN be null
        Validate.notNull(textRepository, "Text Repository cannot be null");

        final List<ITemplateResolver> templateResolversList = new ArrayList<ITemplateResolver>(templateResolvers);
        Collections.sort(templateResolversList, TemplateResolverComparator.INSTANCE);
        this.templateResolvers = Collections.unmodifiableSet(new LinkedHashSet<ITemplateResolver>(templateResolversList));

        final List<IMessageResolver> messageResolversList = new ArrayList<IMessageResolver>(messageResolvers);
        Collections.sort(messageResolversList, MessageResolverComparator.INSTANCE);
        this.messageResolvers = Collections.unmodifiableSet(new LinkedHashSet<IMessageResolver>(messageResolversList));

        this.cacheManager = cacheManager;

        this.dialectSetConfiguration = DialectSetConfiguration.build(dialectConfigurations);
        this.textRepository = textRepository;
        this.absentMessageFormatter = absentMessageFormatter;
    }




    public Set<ITemplateResolver> getTemplateResolvers() {
        return this.templateResolvers;
    }

    public Set<IMessageResolver> getMessageResolvers() {
        return this.messageResolvers;
    }



    public ICacheManager getCacheManager() {
        return this.cacheManager;
    }




    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectSetConfiguration.getDialectConfigurations();
    }

    public Set<IDialect> getDialects() {
        return this.dialectSetConfiguration.getDialects();
    }

    public boolean isStandardDialectPresent() {
        return this.dialectSetConfiguration.isStandardDialectPresent();
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


    public Set<IDocumentProcessor> getDocumentProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getDocumentProcessors(templateMode);
    }

    public Set<ICDATASectionProcessor> getCDATASectionProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCDATASectionProcessors(templateMode);
    }

    public Set<ICommentProcessor> getCommentProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCommentProcessors(templateMode);
    }

    public Set<IDocTypeProcessor> getDocTypeProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getDocTypeProcessors(templateMode);
    }

    public Set<IElementProcessor> getElementProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getElementProcessors(templateMode);
    }

    public Set<ITextProcessor> getTextProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getTextProcessors(templateMode);
    }

    public Set<IProcessingInstructionProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getProcessingInstructionProcessors(templateMode);
    }

    public Set<IXMLDeclarationProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode) {
        return this.dialectSetConfiguration.getXMLDeclarationProcessors(templateMode);
    }

    public boolean hasPreProcessors() {
        return this.dialectSetConfiguration.hasPreProcessors();
    }

    public List<Class<? extends ITemplateHandler>> getPreProcessors() {
        return this.dialectSetConfiguration.getPreProcessors();
    }

    public boolean hasPostProcessors() {
        return this.dialectSetConfiguration.hasPostProcessors();
    }

    public List<Class<? extends ITemplateHandler>> getPostProcessors() {
        return this.dialectSetConfiguration.getPostProcessors();
    }



    public Map<String, Object> getExecutionAttributes() {
        return this.dialectSetConfiguration.getExecutionAttributes();
    }


    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.dialectSetConfiguration.getExpressionObjectFactory();
    }

    public IAbsentMessageFormatter getAbsentMessageFormatter() {
        return absentMessageFormatter;
    }


    private static final class TemplateResolverComparator implements Comparator<ITemplateResolver> {

        private static TemplateResolverComparator INSTANCE = new TemplateResolverComparator();

        TemplateResolverComparator() {
            super();
        }

        public int compare(final ITemplateResolver o1, final ITemplateResolver o2) {
            if (o1.getOrder() == null) {
                return -1;
            }
            if (o2.getOrder() == null) {
                return 1;
            }
            return o1.getOrder().compareTo(o2.getOrder());
        }

    }




    private static final class MessageResolverComparator implements Comparator<IMessageResolver> {

        private static MessageResolverComparator INSTANCE = new MessageResolverComparator();

        MessageResolverComparator() {
            super();
        }

        public int compare(final IMessageResolver o1, final IMessageResolver o2) {
            if (o1.getOrder() == null) {
                return -1;
            }
            if (o2.getOrder() == null) {
                return 1;
            }
            return o1.getOrder().compareTo(o2.getOrder());
        }

    }

}
