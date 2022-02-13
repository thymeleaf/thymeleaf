/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.ProcessorComparators;
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
        final Set<ILinkBuilder> linkBuilders = configuration.getLinkBuilders();

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
        logBuilder.line("[THYMELEAF] * Link builders:");
        for (final ILinkBuilder linkBuilder : linkBuilders) {
            if (linkBuilder.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", linkBuilder.getOrder(), linkBuilder.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", linkBuilder.getName());
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
        if (configLogger.isTraceEnabled()) {
            configLogger.trace(logBuilder.toString());
        } else if (configLogger.isDebugEnabled()) {
            configLogger.debug(logBuilder.toString());
        }
        
    }
    
    
    
    
    private static void printDebugConfiguration(final ConfigLogBuilder logBuilder, final IDialect idialect, final String dialectPrefix) {

        if (idialect instanceof IProcessorDialect) {

            final IProcessorDialect dialect = (IProcessorDialect)idialect;

            final Set<IProcessor> processors = dialect.getProcessors(dialectPrefix);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.HTML);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.XML);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.TEXT);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.JAVASCRIPT);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.CSS);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.RAW);

        }

        if (idialect instanceof IPreProcessorDialect) {

            final IPreProcessorDialect dialect = (IPreProcessorDialect)idialect;

            final Set<IPreProcessor> preProcessors = dialect.getPreProcessors();
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.HTML);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.XML);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.TEXT);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.JAVASCRIPT);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.CSS);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.RAW);

        }

        if (idialect instanceof IPostProcessorDialect) {

            final IPostProcessorDialect dialect = (IPostProcessorDialect)idialect;

            final Set<IPostProcessor> postProcessors = dialect.getPostProcessors();
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.HTML);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.XML);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.TEXT);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.JAVASCRIPT);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.CSS);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.RAW);

        }

        if (idialect instanceof IExpressionObjectDialect) {

            final IExpressionObjectDialect dialect = (IExpressionObjectDialect)idialect;

            final IExpressionObjectFactory expressionObjectFactory = dialect.getExpressionObjectFactory();
            if (expressionObjectFactory != null) {

                final Set<String> expressionObjectNames = expressionObjectFactory.getAllExpressionObjectNames();
                if (expressionObjectNames != null && !expressionObjectNames.isEmpty()) {
                    logBuilder.line("[THYMELEAF]     * Expression Objects:");
                    for (final String expressionObjectName : expressionObjectNames) {
                        logBuilder.line("[THYMELEAF]         * #{}", new Object[] {expressionObjectName});
                    }
                }

            }

        }

        if (idialect instanceof IExecutionAttributeDialect) {

            final IExecutionAttributeDialect dialect = (IExecutionAttributeDialect)idialect;

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





    private static void printProcessorsForTemplateMode(final ConfigLogBuilder logBuilder, final Set<IProcessor> processors, final TemplateMode templateMode) {

        if (processors == null || processors.isEmpty()) {
            return;
        }

        final List<ICDATASectionProcessor> cdataSectionProcessors = new ArrayList<ICDATASectionProcessor>();
        final List<ICommentProcessor> commentProcessors = new ArrayList<ICommentProcessor>();
        final List<IDocTypeProcessor> docTypeProcessors = new ArrayList<IDocTypeProcessor>();
        final List<IElementTagProcessor> elementTagProcessors = new ArrayList<IElementTagProcessor>();
        final List<IElementModelProcessor> elementModelProcessors = new ArrayList<IElementModelProcessor>();
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
            } else if (processor instanceof IElementModelProcessor) {
                elementModelProcessors.add((IElementModelProcessor) processor);
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

        Collections.sort(cdataSectionProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(commentProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(docTypeProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(elementTagProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(elementModelProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(processingInstructionProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(textProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(xmlDeclarationProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);

        if (!elementTagProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Tag Processors by [matching element and attribute name] [precedence]:");
            for (final IElementTagProcessor processor : elementTagProcessors) {
                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                final String elementName = (matchingElementName == null? "*" : matchingElementName.toString());
                final String attributeName = (matchingAttributeName == null? "*" : matchingAttributeName.toString());
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}",
                        new Object[] {elementName, attributeName, Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!elementModelProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Model Processors by [matching element and attribute name] [precedence]:");
            for (final IElementModelProcessor processor : elementModelProcessors) {
                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                final String elementName = (matchingElementName == null? "*" : matchingElementName.toString());
                final String attributeName = (matchingAttributeName == null? "*" : matchingAttributeName.toString());
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}",
                        new Object[] {elementName, attributeName, Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!textProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Text Processors by [precedence]:");
            for (final ITextProcessor processor : textProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!docTypeProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * DOCTYPE Processors by [precedence]:");
            for (final IDocTypeProcessor processor : docTypeProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!cdataSectionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * CDATA Section Processors by [precedence]:");
            for (final ICDATASectionProcessor processor : cdataSectionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!commentProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Comment Processors by [precedence]:");
            for (final ICommentProcessor processor : commentProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!xmlDeclarationProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * XML Declaration Processors by [precedence]:");
            for (final IXMLDeclarationProcessor processor : xmlDeclarationProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }
        if (!processingInstructionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Processing Instruction Processors by [precedence]:");
            for (final IProcessingInstructionProcessor processor : processingInstructionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}",
                        new Object[] {Integer.valueOf(processor.getPrecedence()), processor.getClass().getName()});
            }
        }

    }




    private static void printPreProcessorsForTemplateMode(final ConfigLogBuilder logBuilder, final Set<IPreProcessor> preProcessors, final TemplateMode templateMode) {

        if (preProcessors == null || preProcessors.isEmpty()) {
            return;
        }

        final List<IPreProcessor> preProcessorsForTemplateMode = new ArrayList<IPreProcessor>();

        for (final IPreProcessor preProcessor : preProcessors) {
            if (!templateMode.equals(preProcessor.getTemplateMode())) {
                continue;
            }
            preProcessorsForTemplateMode.add(preProcessor);
        }

        if (preProcessorsForTemplateMode.isEmpty()) {
            // Nothing to show, there are no artifacts for this template mode
            return;
        }

        Collections.sort(preProcessorsForTemplateMode, ProcessorComparators.PRE_PROCESSOR_COMPARATOR);

        logBuilder.line("[THYMELEAF]     * Pre-Processors for Template Mode: {} by [precedence]", templateMode);
        for (final IPreProcessor preProcessor : preProcessorsForTemplateMode) {
            logBuilder.line("[THYMELEAF]             * [{}]: {}",
                    new Object[] {Integer.valueOf(preProcessor.getPrecedence()), preProcessor.getClass().getName()});
        }

    }




    private static void printPostProcessorsForTemplateMode(final ConfigLogBuilder logBuilder, final Set<IPostProcessor> postProcessors, final TemplateMode templateMode) {

        if (postProcessors == null || postProcessors.isEmpty()) {
            return;
        }

        final List<IPostProcessor> postProcessorsForTemplateMode = new ArrayList<IPostProcessor>();

        for (final IPostProcessor postProcessor : postProcessors) {
            if (!templateMode.equals(postProcessor.getTemplateMode())) {
                continue;
            }
            postProcessorsForTemplateMode.add(postProcessor);
        }

        if (postProcessorsForTemplateMode.isEmpty()) {
            // Nothing to show, there are no artifacts for this template mode
            return;
        }

        Collections.sort(postProcessorsForTemplateMode, ProcessorComparators.POST_PROCESSOR_COMPARATOR);

        logBuilder.line("[THYMELEAF]     * Post-Processors for Template Mode: {} by [precedence]", templateMode);
        for (final IPostProcessor postProcessor : postProcessorsForTemplateMode) {
            logBuilder.line("[THYMELEAF]             * [{}]: {}",
                    new Object[]{Integer.valueOf(postProcessor.getPrecedence()), postProcessor.getClass().getName()});
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
            return p.toString().replaceAll("\\$", "\\.");
        }
        
    }
    
    
    
    
    private ConfigurationPrinterHelper() {
        super();
    }



}
