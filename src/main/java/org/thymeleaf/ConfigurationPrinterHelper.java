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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributesDialect;
import org.thymeleaf.dialect.IExpressionObjectsDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.expression.ExpressionObjectDefinition;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.message.resolver.IMessageResolver;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.PrecedenceProcessorComparator;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementNodeProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.StringUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class ConfigurationPrinterHelper {

    public static final String CONFIGURATION_LOGGER_NAME = org.thymeleaf.TemplateEngine.class.getName() + ".CONFIG";

    private static final Logger configLogger = LoggerFactory.getLogger(CONFIGURATION_LOGGER_NAME);

    
    
    
    
    
    
    static void printConfiguration(final IEngineConfiguration configuration) {

        final ConfigLogBuilder logBuilder = new ConfigLogBuilder();

        final ICacheManager cacheManager = configuration.getCacheManager();
        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        final Set<IMessageResolver> messageResolvers = configuration.getMessageResolvers();

        logBuilder.line("Initializing Thymeleaf Template engine configuration...");
        logBuilder.line("[THYMELEAF] TEMPLATE ENGINE CONFIGURATION:");
        if (!StringUtils.isEmptyOrWhitespace(Thymeleaf.VERSION)) {
            if (!StringUtils.isEmptyOrWhitespace(Thymeleaf.BUILD_TIMESTAMP)) {
                logBuilder.line("[THYMELEAF] * Thymeleaf version: {} (built {})", Thymeleaf.VERSION, Thymeleaf.BUILD_TIMESTAMP);
            } else {
                logBuilder.line("[THYMELEAF] * Thymeleaf version: {}", Thymeleaf.VERSION);
            }
        }
        logBuilder.line("[THYMELEAF] * Cache Manager implementation: {}", (cacheManager == null? "[no caches]" : cacheManager.getClass().getName()));
        logBuilder.line("[THYMELEAF] * Template resolvers:");
        for (final ITemplateResolver templateResolver : templateResolvers) {
            if (templateResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", templateResolver.getOrder(), templateResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", templateResolver.getName());
            }
        }
        logBuilder.line("[THYMELEAF] * Message resolvers:");
        for (final IMessageResolver messageResolver : messageResolvers) {
            if (messageResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", messageResolver.getOrder(), messageResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", messageResolver.getName());
            }
        }

        final Set<DialectConfiguration> dialectConfigurations = configuration.getDialectConfigurations();

        int dialectIndex = 1;
        final Integer totalDialects = Integer.valueOf(dialectConfigurations.size());
        
        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {

            final IDialect dialect = dialectConfiguration.getDialect();

            if (totalDialects.intValue() > 1) {
                logBuilder.line("[THYMELEAF] * Dialect [{} of {}]: {} ({})", new Object[]{Integer.valueOf(dialectIndex), totalDialects, dialect.getName(), dialect.getClass().getName()});
            } else {
                logBuilder.line("[THYMELEAF] * Dialect: {} ({})", dialect.getName(), dialect.getClass().getName());
            }

            String dialectPrefix = null;
            if (dialect instanceof IProcessorDialect) {
                dialectPrefix = (dialectConfiguration.isPrefixSpecified()? dialectConfiguration.getPrefix() : ((IProcessorDialect) dialect).getPrefix());
                logBuilder.line("[THYMELEAF]     * Prefix: \"{}\"", (dialectPrefix != null ? dialectPrefix : "(none)"));
            }

            if (configLogger.isDebugEnabled()) {
                printDebugConfiguration(logBuilder, dialect, dialectPrefix);
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
    
    
    
    
    private static void printDebugConfiguration(final ConfigLogBuilder logBuilder, final IDialect idialect, final String dialectPrefix) {

        if (idialect instanceof IProcessorDialect) {

            final IProcessorDialect dialect = (IProcessorDialect)idialect;

            final Set<IProcessor> processors = dialect.getProcessors(dialectPrefix);
            printProcessorsForTemplatrMode(logBuilder, processors, TemplateMode.HTML);
            printProcessorsForTemplatrMode(logBuilder, processors, TemplateMode.XML);
            printProcessorsForTemplatrMode(logBuilder, processors, TemplateMode.TEXT);

        }

        if (idialect instanceof IPreProcessorDialect) {

            final IPreProcessorDialect dialect = (IPreProcessorDialect)idialect;

            final List<Class<? extends ITemplateHandler>> preProcessors = dialect.getPreProcessors();
            if (preProcessors != null && !preProcessors.isEmpty()) {
                logBuilder.line("[THYMELEAF]     * Pre-Processors:");
                for (final Class<? extends ITemplateHandler> preProcessor : preProcessors) {
                    logBuilder.line("[THYMELEAF]         * {}", preProcessor.getName());
                }
            }

        }

        if (idialect instanceof IPostProcessorDialect) {
            final IPostProcessorDialect dialect = (IPostProcessorDialect)idialect;

            final List<Class<? extends ITemplateHandler>> postProcessors = dialect.getPostProcessors();
            if (postProcessors != null && !postProcessors.isEmpty()) {
                logBuilder.line("[THYMELEAF]     * Post-Processors:");
                for (final Class<? extends ITemplateHandler> postProcessor : postProcessors) {
                    logBuilder.line("[THYMELEAF]         * {}", postProcessor.getName());
                }
            }

        }

        if (idialect instanceof IExpressionObjectsDialect) {

            final IExpressionObjectsDialect dialect = (IExpressionObjectsDialect)idialect;

            final IExpressionObjectFactory expressionObjectFactory = dialect.getExpressionObjectFactory();
            if (expressionObjectFactory != null) {

                final Map<String,ExpressionObjectDefinition> expressionObjectDefinitions = expressionObjectFactory.getObjectDefinitions();
                if (expressionObjectDefinitions != null && !expressionObjectDefinitions.isEmpty()) {
                    logBuilder.line("[THYMELEAF]     * Expression Objects:");
                    for (final Map.Entry<String,ExpressionObjectDefinition> expressionObjectsEntry : expressionObjectDefinitions.entrySet()) {
                        logBuilder.line("[THYMELEAF]         * \"#{}\": {}", new Object[] {expressionObjectsEntry.getKey(), expressionObjectsEntry.getValue().getDescription()});
                    }
                }

            }

        }

        if (idialect instanceof IExecutionAttributesDialect) {

            final IExecutionAttributesDialect dialect = (IExecutionAttributesDialect)idialect;

            final Map<String, Object> executionAttributes = dialect.getExecutionAttributes();
            if (executionAttributes != null && !executionAttributes.isEmpty()) {
                logBuilder.line("[THYMELEAF]     * Execution Attributes:");
                for (final Map.Entry<String,Object> executionAttributesEntry : executionAttributes.entrySet()) {
                    final String attrName = executionAttributesEntry.getKey();
                    final String attrValue =
                            (executionAttributesEntry.getValue() == null? null : executionAttributesEntry.getValue().toString());
                    logBuilder.line("[THYMELEAF]         * \"{}\": {}", new Object[] {attrName, attrValue});
                }
            }

        }



    }





    private static void printProcessorsForTemplatrMode(final ConfigLogBuilder logBuilder, final Set<IProcessor> processors, final TemplateMode templateMode) {

        if (processors == null || processors.isEmpty()) {
            return;
        }

        final List<ICDATASectionProcessor> cdataSectionProcessors = new ArrayList<ICDATASectionProcessor>();
        final List<ICommentProcessor> commentProcessors = new ArrayList<ICommentProcessor>();
        final List<IDocTypeProcessor> docTypeProcessors = new ArrayList<IDocTypeProcessor>();
        final List<IElementTagProcessor> elementTagProcessors = new ArrayList<IElementTagProcessor>();
        final List<IElementNodeProcessor> elementNodeProcessors = new ArrayList<IElementNodeProcessor>();
        final List<IProcessingInstructionProcessor> processingInstructionProcessors = new ArrayList<IProcessingInstructionProcessor>();
        final List<ITextProcessor> textProcessors = new ArrayList<ITextProcessor>();
        final List<IXMLDeclarationProcessor> xmlDeclarationProcessors = new ArrayList<IXMLDeclarationProcessor>();

        boolean processorsForTemplateModeExist = false;
        for (final IProcessor processor : processors) {

            if (!templateMode.equals(processor.getTemplateMode())) {
                continue;
            }
            processorsForTemplateModeExist = true;

            if (processor instanceof ICDATASectionProcessor) {
                cdataSectionProcessors.add((ICDATASectionProcessor) processor);
            } else if (processor instanceof ICommentProcessor) {
                commentProcessors.add((ICommentProcessor) processor);
            } else if (processor instanceof IDocTypeProcessor) {
                docTypeProcessors.add((IDocTypeProcessor) processor);
            } else if (processor instanceof IElementTagProcessor) {
                elementTagProcessors.add((IElementTagProcessor) processor);
            } else if (processor instanceof IElementNodeProcessor) {
                elementNodeProcessors.add((IElementNodeProcessor) processor);
            } else if (processor instanceof IProcessingInstructionProcessor) {
                processingInstructionProcessors.add((IProcessingInstructionProcessor) processor);
            } else if (processor instanceof ITextProcessor) {
                textProcessors.add((ITextProcessor) processor);
            } else if (processor instanceof IXMLDeclarationProcessor) {
                xmlDeclarationProcessors.add((IXMLDeclarationProcessor) processor);
            }

        }

        if (!processorsForTemplateModeExist) {
            // Nothing to show, there are no processors for this template mode
            return;
        }

        logBuilder.line("[THYMELEAF]     * Processors for Template Mode: {}", templateMode);

        Collections.sort(cdataSectionProcessors, PrecedenceProcessorComparator.INSTANCE);
        Collections.sort(commentProcessors, PrecedenceProcessorComparator.INSTANCE);
        Collections.sort(docTypeProcessors, PrecedenceProcessorComparator.INSTANCE);
        Collections.sort(elementTagProcessors, PrinterElementProcessorComparator.INSTANCE);
        Collections.sort(elementNodeProcessors, PrinterElementProcessorComparator.INSTANCE);
        Collections.sort(processingInstructionProcessors, PrecedenceProcessorComparator.INSTANCE);
        Collections.sort(textProcessors, PrecedenceProcessorComparator.INSTANCE);
        Collections.sort(xmlDeclarationProcessors, PrecedenceProcessorComparator.INSTANCE);

        if (!elementTagProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Tag Processors by [matching element and attribute name] [precedence]:");
            for (final IElementTagProcessor processor : elementTagProcessors) {
                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                final String elementName = (matchingElementName == null? "*" : matchingElementName.toString());
                final String attributeName = (matchingAttributeName == null? "*" : matchingAttributeName.toString());
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}",
                        new Object[] {elementName, attributeName, processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!elementNodeProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Node Processors by [matching element and attribute name] [precedence]:");
            for (final IElementNodeProcessor processor : elementNodeProcessors) {
                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                final String elementName = (matchingElementName == null? "*" : matchingElementName.toString());
                final String attributeName = (matchingAttributeName == null? "*" : matchingAttributeName.toString());
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}",
                        new Object[] {elementName, attributeName, processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!textProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Text Processors by [precedence]:");
            for (final ITextProcessor processor : textProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!docTypeProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * DOCTYPE Processors by [precedence]:");
            for (final IDocTypeProcessor processor : docTypeProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!cdataSectionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * CDATA Section Processors by [precedence]:");
            for (final ICDATASectionProcessor processor : cdataSectionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!commentProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Comment Processors by [precedence]:");
            for (final ICommentProcessor processor : commentProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!xmlDeclarationProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * XML Declaration Processors by [precedence]:");
            for (final IXMLDeclarationProcessor processor : xmlDeclarationProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }
        if (!processingInstructionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Processing Instruction Processors by [precedence]:");
            for (final IProcessingInstructionProcessor processor : processingInstructionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {processor.getPrecedence(), processor.getClass().getName()});
            }
        }

    }



    
    
    private static final class ConfigLogBuilder {
        
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





    private static final class PrinterElementProcessorComparator implements Comparator<IElementProcessor> {

        private static PrinterElementProcessorComparator INSTANCE = new PrinterElementProcessorComparator();

        public int compare(final IElementProcessor o1, final IElementProcessor o2) {
            // First compare by matching element name, then by matching attribute name, then precedence
            final MatchingElementName o1Element = o1.getMatchingElementName();
            final MatchingElementName o2Element = o2.getMatchingElementName();
            if (o1Element != null) {
                if (o2Element == null) {
                    return 1;
                }
                final int nameCmp = o1Element.toString().compareTo(o2Element.toString());
                if (nameCmp != 0) {
                    return nameCmp;
                }
            } else {
                if (o2Element != null) {
                    return -1;
                }
            }
            final MatchingAttributeName o1Attribute = o1.getMatchingAttributeName();
            final MatchingAttributeName o2Attribute = o2.getMatchingAttributeName();
            if (o1Attribute != null) {
                if (o2Attribute == null) {
                    return 1;
                }
                final int nameCmp = o1Attribute.toString().compareTo(o2Attribute.toString());
                if (nameCmp != 0) {
                    return nameCmp;
                }
            } else {
                if (o2Attribute != null) {
                    return -1;
                }
            }
            return PrecedenceProcessorComparator.INSTANCE.compare(o1, o2);
        }

    }



}
