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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.dom.Node;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.ProcessorAndContext;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class ConfigurationPrinterHelper {

    public static final String CONFIGURATION_LOGGER_NAME = TemplateEngine.class.getName() + ".CONFIG";

    private static final Logger configLogger = LoggerFactory.getLogger(CONFIGURATION_LOGGER_NAME);

    
    
    
    
    
    
    static void printConfiguration(
            final Set<DialectConfiguration> dialectConfigurations,
            final Set<ITemplateResolver> templateResolvers, final Set<IMessageResolver> messageResolvers,
            final ICacheManager cacheFactory, final Set<ITemplateModeHandler> templateModeHandlers) {

        final ConfigLogBuilder logBuilder = new ConfigLogBuilder();
        
        logBuilder.line("[THYMELEAF] TEMPLATE ENGINE CONFIGURATION:");
        logBuilder.line("[THYMELEAF] * Cache Factory implementation: {}", (cacheFactory == null? "[no caches]" : cacheFactory.getClass().getName()));
        logBuilder.line("[THYMELEAF] * Template modes:");
        for (final ITemplateModeHandler templateModeHandler : templateModeHandlers) {
            logBuilder.line("[THYMELEAF]     * {}", templateModeHandler.getTemplateModeName());
        }
        logBuilder.line("[THYMELEAF] * Template resolvers (in order):");
        for (final ITemplateResolver templateResolver : templateResolvers) {
            if (templateResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", templateResolver.getOrder(), templateResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", templateResolver.getName());
            }
        }
        logBuilder.line("[THYMELEAF] * Message resolvers (in order):");
        for (final IMessageResolver messageResolver : messageResolvers) {
            if (messageResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", messageResolver.getOrder(), messageResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", messageResolver.getName());
            }
        }
        
        int dialectIndex = 1;
        final Integer totalDialects = Integer.valueOf(dialectConfigurations.size());
        
        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {
        
            final IDialect dialect = dialectConfiguration.getDialect();
            
            if (totalDialects.intValue() > 1) {
                logBuilder.line("[THYMELEAF] * Dialect [{} of {}]: {}", new Object[] {Integer.valueOf(dialectIndex), totalDialects, dialect.getClass().getName()});
            } else {
                logBuilder.line("[THYMELEAF] * Dialect: {}", dialect.getClass().getName());
            }
            
            logBuilder.line("[THYMELEAF]     * Prefix: \"{}\"", (dialectConfiguration.getPrefix() != null? dialectConfiguration.getPrefix() : "(none)"));

            if (configLogger.isDebugEnabled()) {
                printDebugConfiguration(logBuilder, dialectConfiguration);
            }
            
            dialectIndex++;
            
        }

        logBuilder.end("[THYMELEAF] TEMPLATE ENGINE CONFIGURED OK");

        /*
         * The following condition makes sense because contents in each case will differ a lot.
         */
        if (configLogger.isDebugEnabled()) {
            configLogger.debug(logBuilder.toString());
        } else if (configLogger.isInfoEnabled()) {
            configLogger.info(logBuilder.toString());
        }
        
    }
    
    
    
    
    private static void printDebugConfiguration(
            final ConfigLogBuilder logBuilder, final DialectConfiguration dialectConfiguration) {

        
        final Map<String,Set<ProcessorAndContext>> specificProcessorsByElementName = dialectConfiguration.unsafeGetSpecificProcessorsByElementName();
        final Map<String,Set<ProcessorAndContext>> specificProcessorsByAttributeName = dialectConfiguration.unsafeGetSpecificProcessorsByAttributeName();
        final Map<Class<? extends Node>, Set<ProcessorAndContext>> nonSpecificProcessorsByNodeClass = dialectConfiguration.unsafeGetNonSpecificProcessorsByNodeClass();
        
        final Map<String,Object> executionAttributes = dialectConfiguration.getExecutionAttributes();
        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = dialectConfiguration.getDialect().getDocTypeResolutionEntries();
        final Set<IDocTypeTranslation> docTypeTranslations = dialectConfiguration.getDialect().getDocTypeTranslations();
        
        
        final Map<String,Set<ProcessorAndContext>> orderedSpecificProcessorsByElementName =
                new LinkedHashMap<String,Set<ProcessorAndContext>>(20);
        final List<String> elementNames = new ArrayList<String>(specificProcessorsByElementName.keySet());
        Collections.sort(elementNames);
        for (final String elementName : elementNames) {
            orderedSpecificProcessorsByElementName.put(elementName, specificProcessorsByElementName.get(elementName));
        }
        
        final Map<String,Set<ProcessorAndContext>> orderedSpecificProcessorsByAttributeName = new LinkedHashMap<String,Set<ProcessorAndContext>>(20);
        final List<String> attributeNames = new ArrayList<String>(specificProcessorsByAttributeName.keySet());
        Collections.sort(attributeNames);
        for (final String attrName : attributeNames) {
            orderedSpecificProcessorsByAttributeName.put(attrName, specificProcessorsByAttributeName.get(attrName));
        }
        
        if (!orderedSpecificProcessorsByElementName.isEmpty()) {
            logBuilder.line("[THYMELEAF]     * Processors matching nodes by element name [precedence]:");
            for (final Map.Entry<String,Set<ProcessorAndContext>> elementApplicabilityEntry : orderedSpecificProcessorsByElementName.entrySet()) {
                final String elementName = elementApplicabilityEntry.getKey();
                for (final ProcessorAndContext elementProcessorEntry : elementApplicabilityEntry.getValue()) {
                    final IProcessor elementProcessor = elementProcessorEntry.getProcessor();
                    final String precedence = 
                            (elementProcessor instanceof AbstractProcessor? Integer.valueOf(((AbstractProcessor)elementProcessor).getPrecedence()).toString() : "-");
                    logBuilder.line("[THYMELEAF]         * \"{}\" [{}]: {}", new Object[] {elementName, precedence, elementProcessor.getClass().getName()});
                }
            }
        }
        if (!orderedSpecificProcessorsByAttributeName.isEmpty()) {
            logBuilder.line("[THYMELEAF]     * Processors matching nodes by element attribute [precedence]:");
            for (final Map.Entry<String,Set<ProcessorAndContext>> attrApplicabilityEntry : orderedSpecificProcessorsByAttributeName.entrySet()) {
                final String attrName = attrApplicabilityEntry.getKey();
                for (final ProcessorAndContext attrProcessorEntry : attrApplicabilityEntry.getValue()) {
                    final IProcessor attrProcessor = attrProcessorEntry.getProcessor();
                    final String precedence = 
                            (attrProcessor instanceof AbstractProcessor? Integer.valueOf(((AbstractProcessor)attrProcessor).getPrecedence()).toString() : "-");
                    logBuilder.line("[THYMELEAF]         * \"{}\" [{}]: {}", new Object[] {attrName, precedence, attrProcessor.getClass().getName()});
                }
            }
        }
        if (!nonSpecificProcessorsByNodeClass.isEmpty()) {
            logBuilder.line("[THYMELEAF]     * Processors with non-element-specific matching methods [precedence]:");
            for (final Map.Entry<Class<? extends Node>,Set<ProcessorAndContext>> nonSpecificProcessorEntry : nonSpecificProcessorsByNodeClass.entrySet()) {
                final Class<? extends Node> nodeClass = nonSpecificProcessorEntry.getKey();
                for (final ProcessorAndContext processorEntry : nonSpecificProcessorEntry.getValue()) {
                    final IProcessor processor = processorEntry.getProcessor();
                    final String precedence = 
                            (processor instanceof AbstractProcessor? Integer.valueOf(((AbstractProcessor)processor).getPrecedence()).toString() : "-");
                    logBuilder.line(
                            "[THYMELEAF]         * [{}] [{}]: {}", 
                            new Object[] {nodeClass.getSimpleName(), precedence, processor.getClass().getName()});
                }
            }
        }
        if (!executionAttributes.isEmpty()) {
            logBuilder.line("[THYMELEAF]     * Execution Attributes:");
            for (final Map.Entry<String,Object> executionAttributesEntry : executionAttributes.entrySet()) {
                final String attrName = executionAttributesEntry.getKey();
                final String attrValue = 
                    (executionAttributesEntry.getValue() == null? null : executionAttributesEntry.getValue().toString());
                logBuilder.line("[THYMELEAF]         * \"{}\": {}", new Object[] {attrName, attrValue});
            }
        }
        logBuilder.line("[THYMELEAF]     * DOCTYPE translations:");
        for (final IDocTypeTranslation translation : docTypeTranslations) {
            logBuilder.line("[THYMELEAF]         * DOCTYPE Translation:");
            if (translation.getSourcePublicID().isNone()) {
                logBuilder.line("[THYMELEAF]             * Source: SYSTEM \"{}\"", 
                        new Object[] {
                            (translation.getSourceSystemID().isAny()? "*" : translation.getSourceSystemID())});
            } else {
                logBuilder.line("[THYMELEAF]             * Source: PUBLIC \"{}\" \"{}\"", 
                        new Object[] {
                            (translation.getSourcePublicID().isAny()? "*" : translation.getSourcePublicID()), 
                            (translation.getSourceSystemID().isAny()? "*" : translation.getSourceSystemID())});
            }
            if (translation.getTargetPublicID().isNone()) {
                logBuilder.line("[THYMELEAF]             * Target: SYSTEM \"{}\"", 
                        new Object[] {
                            (translation.getTargetSystemID().isAny()? "*" : translation.getTargetSystemID())});
            } else {
                logBuilder.line("[THYMELEAF]             * Target: PUBLIC \"{}\" \"{}\"", 
                        new Object[] {
                            (translation.getTargetPublicID().isAny()? "*" : translation.getTargetPublicID()), 
                            (translation.getTargetSystemID().isAny()? "*" : translation.getTargetSystemID())});
            }
        }
        logBuilder.line("[THYMELEAF]     * DOCTYPE resolution entries:");
        for (final IDocTypeResolutionEntry entry : docTypeResolutionEntries) {
            if (entry.getPublicID().isNone()) {
                logBuilder.line("[THYMELEAF]         * SYSTEM \"{}\"", 
                        new Object[] {
                            (entry.getSystemID().isAny()? "*" : entry.getSystemID())});
            } else {
                logBuilder.line("[THYMELEAF]         * PUBLIC \"{}\" \"{}\"", 
                        new Object[] {
                            (entry.getPublicID().isAny()? "*" : entry.getPublicID()), 
                            (entry.getSystemID().isAny()? "*" : entry.getSystemID())});
            }
        }

    }
    
    
    
    
    
    private static class ConfigLogBuilder {
        
        private static final String PLACEHOLDER = "\\{\\}";
        private final StringBuilder strBuilder;
        
        protected ConfigLogBuilder() {
            super();
            this.strBuilder = new StringBuilder();
        }
        
        protected void end(final String line) {
            this.strBuilder.append(line);
        }
        
        protected void line(final String line) {
            this.strBuilder.append(line).append("\n");
        }
        
        protected void line(final String line, final Object p1) {
            this.strBuilder.append(replace(line, p1)).append("\n");
        }
        
        protected void line(final String line, final Object p1, final Object p2) {
            this.strBuilder.append(replace(replace(line, p1), p2)).append("\n");
        }
        
        protected void line(final String line, final Object[] pArr) {
            String newLine = line;
            for (final Object aPArr : pArr) {
                newLine = replace(newLine, aPArr);
            }
            this.strBuilder.append(newLine).append("\n");
        }
        
        @Override
        public String toString() {
            return this.strBuilder.toString();
        }
        
        private String replace(final String str, final Object replacement) {
            return str.replaceFirst(PLACEHOLDER, (replacement == null? "" : param(replacement)));
        }
        
        private String param(final Object p) {
            if (p == null) {
                return null;
            }
            return p.toString().replaceAll("\\$","\\.");
        }
        
    }
    
    
    
    
    private ConfigurationPrinterHelper() {
        super();
    }
    
}
