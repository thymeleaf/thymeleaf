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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributesDialect;
import org.thymeleaf.dialect.IExpressionObjectsDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.expression.ExpressionObjectDefinition;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.PrecedenceProcessorComparator;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.document.IDocumentProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class DialectSetConfiguration {

    private final Set<DialectConfiguration> dialectConfigurations;
    private final Set<IDialect> dialects;

    private final boolean standardDialectPresent;
    private final String standardDialectPrefix;

    private final Map<String,Object> executionAttributes;

    private final AggregateExpressionObjectFactory expressionObjectFactory;

    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final EnumMap<TemplateMode,Set<IDocumentProcessor>> documentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ICommentProcessor>> commentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IElementProcessor>> elementProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ITextProcessor>> textProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode;

    private boolean hasPreProcessors;
    private final List<Class<? extends ITemplateHandler>> preProcessors;

    private boolean hasPostProcessors;
    private final List<Class<? extends ITemplateHandler>> postProcessors;




    public static DialectSetConfiguration build(final Set<DialectConfiguration> dialectConfigurations) {

        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");

        // This set will contain all the dialects - without any additional configuration information
        final Set<IDialect> dialects = new LinkedHashSet<IDialect>(dialectConfigurations.size());

        // If we find a standard dialect among the configured ones (Standard or SpringStandard), we will report its prefix
        boolean standardDialectPresent = false;
        String standardDialectPrefix = null;

        // This map will be used for merging the execution attributes of all the dialects
        final Map<String, Object> executionAttributes = new LinkedHashMap<String, Object>(10, 1.0f);

        // This will aggregate all the expression object factories provided by the different dialects
        final AggregateExpressionObjectFactory aggregateExpressionObjectFactory = new AggregateExpressionObjectFactory();

        // This allProcessors set will be used for certifying that no processor instances are repeated accross dialects
        final Set<IProcessor> allProcessors = new LinkedHashSet<IProcessor>(80);

        // EnumMaps for each type of processor (depending on the structures that they can be applied to)
        final EnumMap<TemplateMode, List<IDocumentProcessor>> documentProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IDocumentProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ICDATASectionProcessor>> cdataSectionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ICDATASectionProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ICommentProcessor>> commentProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ICommentProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IDocTypeProcessor>> docTypeProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IDocTypeProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IElementProcessor>> elementProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IElementProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessingInstructionProcessor>> processingInstructionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessingInstructionProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ITextProcessor>> textProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ITextProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IXMLDeclarationProcessor>> xmlDeclarationProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IXMLDeclarationProcessor>>(TemplateMode.class);

        // Lists for merging all pre and postprocessors from all dialects
        final List<Class<? extends ITemplateHandler>> preProcessors = new ArrayList<Class<? extends ITemplateHandler>>(5);
        final List<Class<? extends ITemplateHandler>> postProcessors = new ArrayList<Class<? extends ITemplateHandler>>(5);

        /*
         * ITERATE ALL DIALECTS, processing each one according to its features
         */
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
                    standardDialectPresent = true;
                    standardDialectPrefix = dialectPrefix;
                }

                final Set<IProcessor> dialectProcessors = ((IProcessorDialect) dialect).getProcessors(dialectPrefix);
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

                    // Add the processor to the "all processors" set
                    allProcessors.add(dialectProcessor);


                    // Obtain and check template mode
                    final TemplateMode templateMode = dialectProcessor.getTemplateMode();
                    if (templateMode == null) {
                        throw new ConfigurationException("Template mode cannot be null (processor: " + dialectProcessor.getClass().getName() + ")");
                    }

                    if (dialectProcessor instanceof IElementProcessor) { // can be either a tag processor or a node one

                        List<IElementProcessor> processorsForTemplateMode = elementProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IElementProcessor>(5);
                            elementProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((IElementProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);


                    } else if (dialectProcessor instanceof IDocumentProcessor) {

                        List<IDocumentProcessor> processorsForTemplateMode = documentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IDocumentProcessor>(5);
                            documentProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((IDocumentProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ICDATASectionProcessor) {

                        List<ICDATASectionProcessor> processorsForTemplateMode = cdataSectionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ICDATASectionProcessor>(5);
                            cdataSectionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((ICDATASectionProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ICommentProcessor) {

                        List<ICommentProcessor> processorsForTemplateMode = commentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ICommentProcessor>(5);
                            commentProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((ICommentProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IDocTypeProcessor) {

                        List<IDocTypeProcessor> processorsForTemplateMode = docTypeProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IDocTypeProcessor>(5);
                            docTypeProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((IDocTypeProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IProcessingInstructionProcessor) {

                        List<IProcessingInstructionProcessor> processorsForTemplateMode = processingInstructionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessingInstructionProcessor>(5);
                            processingInstructionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((IProcessingInstructionProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ITextProcessor) {

                        List<ITextProcessor> processorsForTemplateMode = textProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ITextProcessor>(5);
                            textProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((ITextProcessor)dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IXMLDeclarationProcessor) {

                        List<IXMLDeclarationProcessor> processorsForTemplateMode = xmlDeclarationProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IXMLDeclarationProcessor>(5);
                            xmlDeclarationProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add((IXMLDeclarationProcessor)dialectProcessor);
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
             * STEP FOUR for each dialect: aggregate pre-processors (and check the correctness of the list)
             */
            if (dialect instanceof IPreProcessorDialect) {
                final List<Class<? extends ITemplateHandler>> dialectPreProcessors = ((IPreProcessorDialect)dialect).getPreProcessors();
                if (dialectPreProcessors != null) {
                    for (final Class<? extends ITemplateHandler> dialectPreProcessorClass : dialectPreProcessors) {
                        if (dialectPreProcessorClass == null) {
                            throw new ConfigurationException(
                                    "Pre-Processor list for dialect " + dialect.getClass().getName() + " includes a null entry, which is forbidden.");
                        }
                        if (!ITemplateHandler.class.isAssignableFrom(dialectPreProcessorClass)) {
                            throw new ConfigurationException(
                                    "Pre-Processor class " + dialectPreProcessorClass.getName() + " specified for " +
                                    "dialect " + dialect.getClass().getName() + " does not implement required " +
                                    "interface " + ITemplateHandler.class.getName());
                        }
                        try {
                            // Check the empty constructor is present -- we will need to use it for creating new instances
                            dialectPreProcessorClass.getConstructor(new Class[0]);
                        } catch (final NoSuchMethodException e) {
                            throw new ConfigurationException(
                                    "Pre-Processor class " + dialectPreProcessorClass.getName() + " specified for " +
                                    "dialect " + dialect.getClass().getName() + " does not implement required " +
                                    "zero-argument constructor.", e);
                        }
                        preProcessors.add(dialectPreProcessorClass);
                    }
                }
            }


            /*
             * STEP FIVE for each dialect: aggregate post-processors (and check the correctness of the list)
             */
            if (dialect instanceof IPostProcessorDialect) {
                final List<Class<? extends ITemplateHandler>> dialectPostProcessors = ((IPostProcessorDialect)dialect).getPostProcessors();
                if (dialectPostProcessors != null) {
                    for (final Class<? extends ITemplateHandler> dialectPostProcessorClass : dialectPostProcessors) {
                        if (dialectPostProcessorClass == null) {
                            throw new ConfigurationException(
                                    "Post-Processor list for dialect " + dialect.getClass().getName() + " includes a null entry, which is forbidden.");
                        }
                        if (!ITemplateHandler.class.isAssignableFrom(dialectPostProcessorClass)) {
                            throw new ConfigurationException(
                                    "Post-Processor class " + dialectPostProcessorClass.getName() + " specified for " +
                                    "dialect " + dialect.getClass().getName() + " does not implement required " +
                                    "interface " + ITemplateHandler.class.getName());
                        }
                        try {
                            // Check the empty constructor is present -- we will need to use it for creating new instances
                            dialectPostProcessorClass.getConstructor(new Class[0]);
                        } catch (final NoSuchMethodException e) {
                            throw new ConfigurationException(
                                    "Post-Processor class " + dialectPostProcessorClass.getName() + " specified for " +
                                    "dialect " + dialect.getClass().getName() + " does not implement required " +
                                    "zero-argument constructor.", e);
                        }
                        postProcessors.add(dialectPostProcessorClass);
                    }
                }
            }


            /*
             * LAST STEP for each dialect: add it to the dialects set
             */
            dialects.add(dialect);


        }


        // Time to turn the list-based structures into sets -- we needed the lists because we needed a way to order them using Collections.sort()
        final EnumMap<TemplateMode, Set<IDocumentProcessor>> documentProcessorsByTemplateMode = listMapToSetMap(documentProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode = listMapToSetMap(cdataSectionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode = listMapToSetMap(commentProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode = listMapToSetMap(docTypeProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode = listMapToSetMap(elementProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode = listMapToSetMap(processingInstructionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode = listMapToSetMap(textProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode = listMapToSetMap(xmlDeclarationProcessorListsByTemplateMode);


        // Initialize the ElementDefinitions and AttributeDefinitions structures -- they need the element processors so that these
        // are directly applied to the element/attribute definitions and therefore per element/attribute matching is not required
        // during template processing.
        final ElementDefinitions elementDefinitions = new ElementDefinitions(elementProcessorsByTemplateMode);
        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(elementProcessorsByTemplateMode);



        return new DialectSetConfiguration(
                new LinkedHashSet<DialectConfiguration>(dialectConfigurations), dialects,
                standardDialectPresent, standardDialectPrefix,
                executionAttributes, aggregateExpressionObjectFactory,
                elementDefinitions, attributeDefinitions,
                documentProcessorsByTemplateMode,
                cdataSectionProcessorsByTemplateMode, commentProcessorsByTemplateMode, docTypeProcessorsByTemplateMode,
                elementProcessorsByTemplateMode, processingInstructionProcessorsByTemplateMode,
                textProcessorsByTemplateMode, xmlDeclarationProcessorsByTemplateMode,
                preProcessors, postProcessors);

    }



    private static <T extends IProcessor> EnumMap<TemplateMode, Set<T>> listMapToSetMap(final EnumMap<TemplateMode, List<T>> map) {
        final EnumMap<TemplateMode, Set<T>> newMap = new EnumMap<TemplateMode, Set<T>>(TemplateMode.class);
        for (final Map.Entry<TemplateMode, List<T>> entry : map.entrySet()) {
            newMap.put(entry.getKey(), new LinkedHashSet<T>(entry.getValue()));
        }
        return newMap;
    }








    private DialectSetConfiguration(
            final Set<DialectConfiguration> dialectConfigurations, final Set<IDialect> dialects,
            final boolean standardDialectPresent, final String standardDialectPrefix,
            final Map<String, Object> executionAttributes,
            final AggregateExpressionObjectFactory expressionObjectFactory,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions,
            final EnumMap<TemplateMode, Set<IDocumentProcessor>> documentProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode,
            final List<Class<? extends ITemplateHandler>> preProcessors,
            final List<Class<? extends ITemplateHandler>> postProcessors) {

        super();

        this.dialectConfigurations = Collections.unmodifiableSet(dialectConfigurations);
        this.dialects = Collections.unmodifiableSet(dialects);
        this.standardDialectPresent = standardDialectPresent;
        this.standardDialectPrefix = standardDialectPrefix;
        this.executionAttributes = Collections.unmodifiableMap(executionAttributes);
        this.expressionObjectFactory = expressionObjectFactory;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.documentProcessorsByTemplateMode = documentProcessorsByTemplateMode;
        this.cdataSectionProcessorsByTemplateMode = cdataSectionProcessorsByTemplateMode;
        this.commentProcessorsByTemplateMode = commentProcessorsByTemplateMode;
        this.docTypeProcessorsByTemplateMode = docTypeProcessorsByTemplateMode;
        this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
        this.processingInstructionProcessorsByTemplateMode = processingInstructionProcessorsByTemplateMode;
        this.textProcessorsByTemplateMode = textProcessorsByTemplateMode;
        this.xmlDeclarationProcessorsByTemplateMode = xmlDeclarationProcessorsByTemplateMode;
        this.preProcessors = preProcessors;
        this.hasPreProcessors = this.preProcessors != null && this.preProcessors.size() > 0;
        this.postProcessors = postProcessors;
        this.hasPostProcessors = this.postProcessors != null && this.postProcessors.size() > 0;

    }



    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectConfigurations;
    }

    public Set<IDialect> getDialects() {
        return this.dialects;
    }

    public boolean isStandardDialectPresent() {
        return this.standardDialectPresent;
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

    public Set<IDocumentProcessor> getDocumentProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IDocumentProcessor> processors = this.documentProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ICDATASectionProcessor> getCDATASectionProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<ICDATASectionProcessor> processors = this.cdataSectionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ICommentProcessor> getCommentProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<ICommentProcessor> processors = this.commentProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IDocTypeProcessor> getDocTypeProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IDocTypeProcessor> processors = this.docTypeProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IElementProcessor> getElementProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IElementProcessor> processors = this.elementProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessingInstructionProcessor> getProcessingInstructionProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IProcessingInstructionProcessor> processors = this.processingInstructionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ITextProcessor> getTextProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<ITextProcessor> processors = this.textProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IXMLDeclarationProcessor> getXMLDeclarationProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IXMLDeclarationProcessor> processors = this.xmlDeclarationProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }


    public boolean hasPreProcessors() {
        return this.hasPreProcessors;
    }

    public List<Class<? extends ITemplateHandler>> getPreProcessors() {
        return this.preProcessors;
    }


    public boolean hasPostProcessors() {
        return this.hasPostProcessors;
    }

    public List<Class<? extends ITemplateHandler>> getPostProcessors() {
        return this.postProcessors;
    }


    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.expressionObjectFactory;
    }




    /*
     * This class serves the purpose of aggregating all the registered expression object factories so that
     * obtaining all the objects from an IProcessingContext implementation is easier.
     */
    static class AggregateExpressionObjectFactory implements IExpressionObjectFactory {

        /*
         * We will try to optimize a bit the fact that most times only one dialect will provide an
         * IExpressionObjects implementation, so there should be no need to create a collection
         */

        private IExpressionObjectFactory firstExpressionObjectFactory = null;
        private List<IExpressionObjectFactory> expressionObjectFactoryList = null;

        AggregateExpressionObjectFactory() {
            super();
        }

        void add(final IExpressionObjectFactory expressionObjectFactory) {
            if (this.firstExpressionObjectFactory == null && this.expressionObjectFactoryList == null) {
                this.firstExpressionObjectFactory = expressionObjectFactory;
                return;
            } else if (this.expressionObjectFactoryList == null) {
                this.expressionObjectFactoryList = new ArrayList<IExpressionObjectFactory>(2);
                this.expressionObjectFactoryList.add(this.firstExpressionObjectFactory);
                this.firstExpressionObjectFactory = null;
            }
            this.expressionObjectFactoryList.add(expressionObjectFactory);
        }

        public Map<String,ExpressionObjectDefinition> getObjectDefinitions() {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.getObjectDefinitions();
            }
            if (this.expressionObjectFactoryList == null) {
                return null;
            }
            final Map<String,ExpressionObjectDefinition> objectDefinitions = new LinkedHashMap<String,ExpressionObjectDefinition>(30);
            int n = this.expressionObjectFactoryList.size();
            while (n-- != 0) {
                objectDefinitions.putAll(this.expressionObjectFactoryList.get(n).getObjectDefinitions());
            }
            return objectDefinitions;
        }

        public Object buildObject(final IProcessingContext processingContext, final String expressionObjectName) {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.buildObject(processingContext, expressionObjectName);
            }
            if (this.expressionObjectFactoryList == null) {
                return false;
            }
            int n = this.expressionObjectFactoryList.size();
            while (n-- != 0) {
                if (this.expressionObjectFactoryList.get(n).getObjectDefinitions().containsKey(expressionObjectName)) {
                    return this.expressionObjectFactoryList.get(n).buildObject(processingContext, expressionObjectName);
                }
            }
            return false;
        }

    }


}

