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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.DialectConfiguration;
import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.dialect.IExecutionAttributesDialect;
import org.thymeleaf.aurora.dialect.IExpressionObjectsDialect;
import org.thymeleaf.aurora.dialect.IProcessorDialect;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.expression.IExpressionObjectFactory;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.aurora.processor.comment.ICommentProcessor;
import org.thymeleaf.aurora.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.aurora.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.aurora.processor.text.ITextProcessor;
import org.thymeleaf.aurora.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.aurora.standard.StandardDialect;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class DialectContext {

    private final Set<DialectConfiguration> dialectConfigurations;
    private final Set<IDialect> dialects;

    private final String standardDialectPrefix;

    private final Map<String,Object> executionAttributes;

    private final AggregateExpressionObjectFactory expressionObjectFactory;

    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final EnumMap<TemplateMode,Set<IProcessor>> cdataSectionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> commentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> docTypeProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> elementProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> processingInstructionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> textProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> xmlDeclarationProcessorsByTemplateMode;




    public static DialectContext build(final Set<DialectConfiguration> dialectConfigurations) {

        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");

        // This set will contain all the dialects - without any additional configuration information
        final Set<IDialect> dialects = new LinkedHashSet<IDialect>(dialectConfigurations.size());

        // If we find a standard dialect among the configured ones (Standard or SpringStandard), we will report its prefix
        String standardDialectPrefix = null;

        // This map will be used for merging the execution attributes of all the dialects
        final Map<String, Object> executionAttributes = new LinkedHashMap<String, Object>(10, 1.0f);

        // This will aggregate all the expression object factories provided by the different dialects
        final AggregateExpressionObjectFactory aggregateExpressionObjectFactory = new AggregateExpressionObjectFactory();

        // This allProcessors set will be used for certifying that no processor instances are repeated accross dialects
        final Set<IProcessor> allProcessors = new LinkedHashSet<IProcessor>(80);

        final EnumMap<TemplateMode, List<IProcessor>> cdataSectionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> commentProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> docTypeProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> elementProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> processingInstructionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> textProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> xmlDeclarationProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);

        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {

            final IDialect dialect = dialectConfiguration.getDialect(); // cannot be null -- ConfigurationDialect checks this

            /*
             * STEP ONE for each dialect: process, initialize and merge processors
             */
            if (dialect instanceof IProcessorDialect) {

                // Might be null if the dialect has been specified to use no prefix (or that is the default of such dialect)
                final String dialectPrefix =
                        (dialectConfiguration.isPrefixSpecified()? dialectConfiguration.getPrefix() : ((IProcessorDialect) dialect).getPrefix());

                if (dialect instanceof StandardDialect) {
                    standardDialectPrefix = dialectPrefix;
                }

                final Set<IProcessor> dialectProcessors = ((IProcessorDialect) dialect).getProcessors();
                if (dialectProcessors == null) {
                    throw new ConfigurationException("Dialect should not return null processor set: " + dialect.getClass().getName());
                }

                for (final IProcessor dialectProcessor : dialectProcessors) {

                    if (dialectProcessor == null) {
                        throw new ConfigurationException("Dialect should not return null processor in processor set: " + dialect.getClass().getName());
                    }

                    // Check that the processor instance is unique among all the dialect instances - this is a requirement
                    // due to the fact that processors have to be initialized by setting them a prefix and a dialect
                    if (allProcessors.contains(dialectProcessor)) {
                        throw new ConfigurationException(
                                "The same processor of class " + dialectProcessor.getClass().getName() + " has been " +
                                "specified more than one (probably in different dialects). Processor instances should " +
                                "be unique among all configured dialects.");
                    }

                    // Initialize the processor
                    dialectProcessor.setDialect(dialect);
                    dialectProcessor.setDialectPrefix(dialectPrefix);

                    // Add the processor to the "all processors" set
                    allProcessors.add(dialectProcessor);


                    // Obtain and check template mode
                    final TemplateMode templateMode = dialectProcessor.getTemplateMode();
                    if (templateMode == null) {
                        throw new ConfigurationException("Template mode cannot be null (processor: " + dialectProcessor.getClass().getName() + ")");
                    }

                    final INodeProcessor.MatchingNodeType matchingNodeType =
                            (dialectProcessor instanceof INodeProcessor ? ((INodeProcessor) dialectProcessor).getMatchingNodeType() : null);

                    if (dialectProcessor instanceof IElementProcessor || INodeProcessor.MatchingNodeType.ELEMENT == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = elementProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            elementProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);


                    } else if (dialectProcessor instanceof ICDATASectionProcessor || INodeProcessor.MatchingNodeType.CDATA_SECTION == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = cdataSectionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            cdataSectionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ICommentProcessor || INodeProcessor.MatchingNodeType.COMMENT == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = commentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            commentProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IDocTypeProcessor || INodeProcessor.MatchingNodeType.DOC_TYPE == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = docTypeProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            docTypeProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IProcessingInstructionProcessor || INodeProcessor.MatchingNodeType.PROCESSING_INSTRUCTION == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = processingInstructionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            processingInstructionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ITextProcessor || INodeProcessor.MatchingNodeType.TEXT == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = textProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            textProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IXMLDeclarationProcessor || INodeProcessor.MatchingNodeType.XML_DECLARATION == matchingNodeType) {

                        List<IProcessor> processorsForTemplateMode = xmlDeclarationProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            xmlDeclarationProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    }


                }

            }


            /*
             * STEP TWO for each dialect: merge execution attributes
             */
            if (dialect instanceof IExecutionAttributesDialect) {

                final Map<String, Object> dialectExecutionAttributes = ((IExecutionAttributesDialect) dialect).getExecutionAttributes();
                if (dialectExecutionAttributes != null) {
                    for (final Map.Entry<String, Object> entry : dialectExecutionAttributes.entrySet()) {
                        final String executionAttributeName = entry.getKey();
                        if (executionAttributes.containsKey(executionAttributeName)) {
                            throw new ConfigurationException(
                                    "Conflicting execution attribute. Two or more dialects specify an execution " +
                                    "attribute with the same name \"" + executionAttributeName + "\".");
                        }
                        executionAttributes.put(entry.getKey(), entry.getValue());
                    }
                }

            }


            /*
             * STEP THREE for each dialect: aggregate all the expression object factories
             */
            if (dialect instanceof IExpressionObjectsDialect) {

                final IExpressionObjectFactory factory = ((IExpressionObjectsDialect) dialect).getExpressionObjectFactory();
                if (factory != null) {
                    aggregateExpressionObjectFactory.add(factory);
                }

            }


            /*
             * LAST STEP for each dialect: add it to the dialects set
             */
            dialects.add(dialect);


        }


        // Time to turn the list-based structures into sets -- we needed the lists because we needed a way to order them using Collections.sort()
        final EnumMap<TemplateMode, Set<IProcessor>> cdataSectionProcessorsByTemplateMode = listMapToSetMap(cdataSectionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> commentProcessorsByTemplateMode = listMapToSetMap(commentProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> docTypeProcessorsByTemplateMode = listMapToSetMap(docTypeProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> elementProcessorsByTemplateMode = listMapToSetMap(elementProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> processingInstructionProcessorsByTemplateMode = listMapToSetMap(processingInstructionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> textProcessorsByTemplateMode = listMapToSetMap(textProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessor>> xmlDeclarationProcessorsByTemplateMode = listMapToSetMap(xmlDeclarationProcessorListsByTemplateMode);


        // Initialize the ElementDefinitions and AttributeDefinitions structures -- they need the element processors so that these
        // are directly applied to the element/attribute definitions and therefore per element/attribute matching is not required
        // during template processing.
        final ElementDefinitions elementDefinitions = new ElementDefinitions(elementProcessorsByTemplateMode);
        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(elementProcessorsByTemplateMode);



        return new DialectContext(
                new LinkedHashSet<DialectConfiguration>(dialectConfigurations), dialects, standardDialectPrefix,
                executionAttributes, aggregateExpressionObjectFactory,
                elementDefinitions, attributeDefinitions,
                cdataSectionProcessorsByTemplateMode, commentProcessorsByTemplateMode, docTypeProcessorsByTemplateMode,
                elementProcessorsByTemplateMode, processingInstructionProcessorsByTemplateMode, textProcessorsByTemplateMode, xmlDeclarationProcessorsByTemplateMode);

    }



    private static EnumMap<TemplateMode, Set<IProcessor>> listMapToSetMap(final EnumMap<TemplateMode, List<IProcessor>> map) {
        final EnumMap<TemplateMode, Set<IProcessor>> newMap = new EnumMap<TemplateMode, Set<IProcessor>>(TemplateMode.class);
        for (final Map.Entry<TemplateMode, List<IProcessor>> entry : map.entrySet()) {
            newMap.put(entry.getKey(), new LinkedHashSet<IProcessor>(entry.getValue()));
        }
        return newMap;
    }








    private DialectContext(
            final Set<DialectConfiguration> dialectConfigurations, final Set<IDialect> dialects,
            final String standardDialectPrefix,
            final Map<String, Object> executionAttributes,
            final AggregateExpressionObjectFactory expressionObjectFactory,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions,
            final EnumMap<TemplateMode, Set<IProcessor>> cdataSectionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> commentProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> docTypeProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> elementProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> processingInstructionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> textProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> xmlDeclarationProcessorsByTemplateMode) {

        super();

        this.dialectConfigurations = Collections.unmodifiableSet(dialectConfigurations);
        this.dialects = Collections.unmodifiableSet(dialects);
        this.standardDialectPrefix = standardDialectPrefix;
        this.executionAttributes = Collections.unmodifiableMap(executionAttributes);
        this.expressionObjectFactory = expressionObjectFactory;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.cdataSectionProcessorsByTemplateMode = cdataSectionProcessorsByTemplateMode;
        this.commentProcessorsByTemplateMode = commentProcessorsByTemplateMode;
        this.docTypeProcessorsByTemplateMode = docTypeProcessorsByTemplateMode;
        this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
        this.processingInstructionProcessorsByTemplateMode = processingInstructionProcessorsByTemplateMode;
        this.textProcessorsByTemplateMode = textProcessorsByTemplateMode;
        this.xmlDeclarationProcessorsByTemplateMode = xmlDeclarationProcessorsByTemplateMode;

    }



    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectConfigurations;
    }

    public Set<IDialect> getDialects() {
        return this.dialects;
    }

    public String getStandardDialectPrefix() {
        return this.standardDialectPrefix;
    }

    public Map<String,Object> getExecutionAttributes() {
        return this.executionAttributes;
    }

    public Object getExecutionAttribute(final String executionAttributeName) {
        return this.executionAttributes.get(executionAttributeName);
    }

    public boolean hasExecutionAttribute(final String executionAttributeName) {
        return this.executionAttributes.containsKey(executionAttributeName);
    }

    public ElementDefinitions getElementDefinitions() {
        return this.elementDefinitions;
    }

    public AttributeDefinitions getAttributeDefinitions() {
        return this.attributeDefinitions;
    }

    public Set<IProcessor> getCDATASectionProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.cdataSectionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getCommentProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.commentProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getDocTypeProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.docTypeProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getElementProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.elementProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.processingInstructionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getTextProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.textProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessor> processors = this.xmlDeclarationProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Map<String, Object> buildExpressionObjects(final ITemplateProcessingContext processingContext) {
        return this.expressionObjectFactory.buildExpressionObjects(processingContext);
    }




    /*
     * This class serves the purpose of aggregating all the registered expression object factories so that
     * obtaining all the objects from an ITemplateProcessingContext implementation is easier.
     */
    static class AggregateExpressionObjectFactory implements IExpressionObjectFactory {

        private final List<IExpressionObjectFactory> expressionObjectFactories;

        AggregateExpressionObjectFactory() {
            super();
            this.expressionObjectFactories = new ArrayList<IExpressionObjectFactory>(3);
        }

        void add(final IExpressionObjectFactory factory) {
            this.expressionObjectFactories.add(factory);
        }

        public Map<String, Object> buildExpressionObjects(final ITemplateProcessingContext processingContext) {
            if (this.expressionObjectFactories.size() == 0) {
                return Collections.EMPTY_MAP;
            }
            if (this.expressionObjectFactories.size() == 1) {
                return this.expressionObjectFactories.get(0).buildExpressionObjects(processingContext);
            }
            // HashMap, not LinkedHashMap -- order does not seem important in this, and given this building operation
            // will be done for each template being processed, we might benefit from a slight performance gain
            final Map<String,Object> expressionObjects = new HashMap<String,Object>(30);
            for (final IExpressionObjectFactory factory : this.expressionObjectFactories) {
                final Map<String,Object> expressionObjectsForFactory = factory.buildExpressionObjects(processingContext);
                if (expressionObjects.isEmpty()) {
                    expressionObjects.putAll(expressionObjectsForFactory);
                } else {
                    for (final Map.Entry<String, Object> entry : expressionObjectsForFactory.entrySet()) {
                        final String expressionObjectName = entry.getKey();
                        if (expressionObjects.containsKey(expressionObjectName)) {
                            throw new ConfigurationException(
                                    "Configured dialects tried to register duplicate expression object: \"" + expressionObjectName + "\"");
                        }
                        expressionObjects.put(expressionObjectName, entry.getValue());
                    }
                }
            }
            return expressionObjects;
        }

    }





    /*
     * Comparator used for ordering processors according to their precedence
     */
    private static final class PrecedenceProcessorComparator implements Comparator<IProcessor> {

        static final PrecedenceProcessorComparator INSTANCE = new PrecedenceProcessorComparator();


        PrecedenceProcessorComparator() {
            super();
        }

        public int compare(final IProcessor o1, final IProcessor o2) {
            return Integer.valueOf(o1.getPrecedence()).compareTo(Integer.valueOf(o2.getPrecedence()));
        }

    }


}

