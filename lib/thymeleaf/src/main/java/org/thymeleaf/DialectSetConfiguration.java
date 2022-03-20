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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.engine.IElementDefinitionsAware;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ProcessorComparators;
import org.thymeleaf.util.ProcessorConfigurationUtils;
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
    private final EnumMap<TemplateMode,Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ICommentProcessor>> commentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IElementProcessor>> elementProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<ITextProcessor>> textProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode;

    private final EnumMap<TemplateMode,Set<IPreProcessor>> preProcessors;
    private final EnumMap<TemplateMode,Set<IPostProcessor>> postProcessors;




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

        // EnumMaps for each type of processor (depending on the structures that they can be applied to)
        final EnumMap<TemplateMode, List<ITemplateBoundariesProcessor>> templateBoundariesProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ITemplateBoundariesProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ICDATASectionProcessor>> cdataSectionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ICDATASectionProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ICommentProcessor>> commentProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ICommentProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IDocTypeProcessor>> docTypeProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IDocTypeProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IElementProcessor>> elementProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IElementProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessingInstructionProcessor>> processingInstructionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessingInstructionProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<ITextProcessor>> textProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<ITextProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IXMLDeclarationProcessor>> xmlDeclarationProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IXMLDeclarationProcessor>>(TemplateMode.class);

        // Lists for merging all pre and postprocessors from all dialects
        final EnumMap<TemplateMode, List<IPreProcessor>> preProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IPreProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IPostProcessor>> postProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IPostProcessor>>(TemplateMode.class);

        /*
         * ITERATE ALL DIALECTS, processing each one according to its features
         */
        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {

            final IDialect dialect = dialectConfiguration.getDialect(); // cannot be null -- ConfigurationDialect checks this


            /*
             * STEP ONE for each dialect: process, initialize and merge processors
             */
            if (dialect instanceof IProcessorDialect) {

                final IProcessorDialect processorDialect = (IProcessorDialect)dialect;

                // Might be null if the dialect has been specified to use no prefix (or that is the default of such dialect)
                final String dialectPrefix =
                        (dialectConfiguration.isPrefixSpecified()? dialectConfiguration.getPrefix() : processorDialect.getPrefix());

                if (dialect instanceof StandardDialect) {
                    standardDialectPresent = true;
                    standardDialectPrefix = dialectPrefix;
                }

                final Set<IProcessor> dialectProcessors = processorDialect.getProcessors(dialectPrefix);
                if (dialectProcessors == null) {
                    throw new ConfigurationException("Dialect should not return null processor set: " + dialect.getClass().getName());
                }

                for (final IProcessor dialectProcessor : dialectProcessors) {

                    if (dialectProcessor == null) {
                        throw new ConfigurationException("Dialect should not return null processor in processor set: " + dialect.getClass().getName());
                    }

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
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((IElementProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);


                    } else if (dialectProcessor instanceof ITemplateBoundariesProcessor) {

                        List<ITemplateBoundariesProcessor> processorsForTemplateMode = templateBoundariesProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ITemplateBoundariesProcessor>(5);
                            templateBoundariesProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((ITemplateBoundariesProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof ICDATASectionProcessor) {

                        List<ICDATASectionProcessor> processorsForTemplateMode = cdataSectionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ICDATASectionProcessor>(5);
                            cdataSectionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((ICDATASectionProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof ICommentProcessor) {

                        List<ICommentProcessor> processorsForTemplateMode = commentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ICommentProcessor>(5);
                            commentProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((ICommentProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof IDocTypeProcessor) {

                        List<IDocTypeProcessor> processorsForTemplateMode = docTypeProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IDocTypeProcessor>(5);
                            docTypeProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((IDocTypeProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof IProcessingInstructionProcessor) {

                        List<IProcessingInstructionProcessor> processorsForTemplateMode = processingInstructionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessingInstructionProcessor>(5);
                            processingInstructionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((IProcessingInstructionProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof ITextProcessor) {

                        List<ITextProcessor> processorsForTemplateMode = textProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<ITextProcessor>(5);
                            textProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((ITextProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    } else if (dialectProcessor instanceof IXMLDeclarationProcessor) {

                        List<IXMLDeclarationProcessor> processorsForTemplateMode = xmlDeclarationProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IXMLDeclarationProcessor>(5);
                            xmlDeclarationProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((IXMLDeclarationProcessor)dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);

                    }


                }

            }


            /*
             * STEP TWO for each dialect: merge execution attributes
             */
            if (dialect instanceof IExecutionAttributeDialect) {

                final Map<String, Object> dialectExecutionAttributes = ((IExecutionAttributeDialect) dialect).getExecutionAttributes();
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
            if (dialect instanceof IExpressionObjectDialect) {

                final IExpressionObjectFactory factory = ((IExpressionObjectDialect) dialect).getExpressionObjectFactory();
                if (factory != null) {
                    aggregateExpressionObjectFactory.add(factory);
                }

            }


            /*
             * STEP FOUR for each dialect: aggregate pre-processors (and check the correctness of the list)
             */
            if (dialect instanceof IPreProcessorDialect) {

                final Set<IPreProcessor> dialectPreProcessors = ((IPreProcessorDialect)dialect).getPreProcessors();
                if (dialectPreProcessors != null) {

                    for (final IPreProcessor preProcessor : dialectPreProcessors) {

                        if (preProcessor == null) {
                            throw new ConfigurationException(
                                    "Pre-Processor list for dialect " + dialect.getClass().getName() + " includes a " +
                                    "null entry, which is forbidden.");
                        }

                        // Obtain and check template mode
                        final TemplateMode templateMode = preProcessor.getTemplateMode();
                        if (templateMode == null) {
                            throw new ConfigurationException(
                                    "Template mode cannot be null (pre-processor: " + preProcessor.getClass().getName() +
                                    ", dialect" + dialect.getClass().getName() + ")");
                        }

                        // Check the handler class: should extend ITemplateHandler and have an empty constructor
                        final Class<?> handlerClass = preProcessor.getHandlerClass();
                        if (handlerClass == null) {
                            throw new ConfigurationException(
                                    "Pre-Processor " + preProcessor.getClass().getName() + " for dialect " +
                                    preProcessor.getClass().getName() + " returns a null handler class, which is forbidden.");
                        }
                        if (!ITemplateHandler.class.isAssignableFrom(handlerClass)) {
                            throw new ConfigurationException(
                                    "Handler class " + handlerClass.getName() + " specified for " +
                                    "pre-processor " + preProcessor.getClass().getName() + " in dialect " +
                                    dialect.getClass().getName() + " does not implement required " +
                                    "interface " + ITemplateHandler.class.getName());
                        }
                        try {
                            // Check the empty constructor is present -- we will need to use it for creating new instances
                            handlerClass.getConstructor(new Class[0]);
                        } catch (final NoSuchMethodException e) {
                            throw new ConfigurationException(
                                    "Pre-Processor class " + handlerClass.getName() + " specified for " +
                                    "pre-processor " + preProcessor.getClass().getName() + " in dialect " +
                                    dialect.getClass().getName() + " does not implement required " +
                                    "zero-argument constructor.", e);
                        }

                        // Add the pre-processor to its corresponding map and sort
                        List<IPreProcessor> preProcessorsForTemplateMode = preProcessorListsByTemplateMode.get(templateMode);
                        if (preProcessorsForTemplateMode == null) {
                            preProcessorsForTemplateMode = new ArrayList<IPreProcessor>(5);
                            preProcessorListsByTemplateMode.put(templateMode, preProcessorsForTemplateMode);
                        }
                        preProcessorsForTemplateMode.add(preProcessor);
                        Collections.sort(preProcessorsForTemplateMode, ProcessorComparators.PRE_PROCESSOR_COMPARATOR);

                    }

                }

            }


            /*
             * STEP FIVE for each dialect: aggregate post-processors (and check the correctness of the list)
             */
            if (dialect instanceof IPostProcessorDialect) {

                final Set<IPostProcessor> dialectPostProcessors = ((IPostProcessorDialect)dialect).getPostProcessors();
                if (dialectPostProcessors != null) {

                    for (final IPostProcessor postProcessor : dialectPostProcessors) {

                        if (postProcessor == null) {
                            throw new ConfigurationException(
                                    "Post-Processor list for dialect " + dialect.getClass().getName() + " includes a " +
                                    "null entry, which is forbidden.");
                        }

                        // Obtain and check template mode
                        final TemplateMode templateMode = postProcessor.getTemplateMode();
                        if (templateMode == null) {
                            throw new ConfigurationException(
                                    "Template mode cannot be null (post-processor: " + postProcessor.getClass().getName() +
                                    ", dialect" + dialect.getClass().getName() + ")");
                        }

                        // Check the handler class: should extend ITemplateHandler and have an empty constructor
                        final Class<?> handlerClass = postProcessor.getHandlerClass();
                        if (handlerClass == null) {
                            throw new ConfigurationException(
                                    "Post-Processor " + postProcessor.getClass().getName() + " for dialect " +
                                    postProcessor.getClass().getName() + " returns a null handler class, which is forbidden.");
                        }
                        if (!ITemplateHandler.class.isAssignableFrom(handlerClass)) {
                            throw new ConfigurationException(
                                    "Handler class " + handlerClass.getName() + " specified for " +
                                    "post-processor " + postProcessor.getClass().getName() + " in dialect " +
                                    dialect.getClass().getName() + " does not implement required " +
                                    "interface " + ITemplateHandler.class.getName());
                        }
                        try {
                            // Check the empty constructor is present -- we will need to use it for creating new instances
                            handlerClass.getConstructor(new Class[0]);
                        } catch (final NoSuchMethodException e) {
                            throw new ConfigurationException(
                                    "Post-Processor class " + handlerClass.getName() + " specified for " +
                                    "post-processor " + postProcessor.getClass().getName() + " in dialect " +
                                    dialect.getClass().getName() + " does not implement required " +
                                    "zero-argument constructor.", e);
                        }

                        // Add the pre-processor to its corresponding map and sort
                        List<IPostProcessor> postProcessorsForTemplateMode = postProcessorListsByTemplateMode.get(templateMode);
                        if (postProcessorsForTemplateMode == null) {
                            postProcessorsForTemplateMode = new ArrayList<IPostProcessor>(5);
                            postProcessorListsByTemplateMode.put(templateMode, postProcessorsForTemplateMode);
                        }
                        postProcessorsForTemplateMode.add(postProcessor);
                        Collections.sort(postProcessorsForTemplateMode, ProcessorComparators.POST_PROCESSOR_COMPARATOR);

                    }

                }

            }


            /*
             * LAST STEP for each dialect: add it to the dialects set
             */
            dialects.add(dialect);


        }


        // Time to turn the list-based structures into sets -- we needed the lists because we needed a way to order them using Collections.sort()
        final EnumMap<TemplateMode, Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode = listMapToSetMap(templateBoundariesProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode = listMapToSetMap(cdataSectionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode = listMapToSetMap(commentProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode = listMapToSetMap(docTypeProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode = listMapToSetMap(elementProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode = listMapToSetMap(processingInstructionProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode = listMapToSetMap(textProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode = listMapToSetMap(xmlDeclarationProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IPreProcessor>> preProcessorsByTemplateMode = listMapToSetMap(preProcessorListsByTemplateMode);
        final EnumMap<TemplateMode, Set<IPostProcessor>> postProcessorsByTemplateMode = listMapToSetMap(postProcessorListsByTemplateMode);


        // Initialize the ElementDefinitions and AttributeDefinitions structures -- they need the element processors so that these
        // are directly applied to the element/attribute definitions and therefore per element/attribute matching is not required
        // during template processing.
        final ElementDefinitions elementDefinitions = new ElementDefinitions(elementProcessorsByTemplateMode);
        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(elementProcessorsByTemplateMode);


        // Traverse the sets of processors in order to set the AttributeDefinitions and/or ElementDefinitions objects
        // to those that need them in order to initialize and cache attribute/element definition-related structures
        initializeDefinitionsForProcessors(templateBoundariesProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(cdataSectionProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(commentProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(docTypeProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(elementProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(processingInstructionProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(textProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(xmlDeclarationProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForPreProcessors(preProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForPostProcessors(postProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);


        return new DialectSetConfiguration(
                new LinkedHashSet<DialectConfiguration>(dialectConfigurations), dialects,
                standardDialectPresent, standardDialectPrefix,
                executionAttributes, aggregateExpressionObjectFactory,
                elementDefinitions, attributeDefinitions,
                templateBoundariesProcessorsByTemplateMode,
                cdataSectionProcessorsByTemplateMode, commentProcessorsByTemplateMode, docTypeProcessorsByTemplateMode,
                elementProcessorsByTemplateMode, processingInstructionProcessorsByTemplateMode,
                textProcessorsByTemplateMode, xmlDeclarationProcessorsByTemplateMode,
                preProcessorsByTemplateMode, postProcessorsByTemplateMode);

    }



    private static <T> EnumMap<TemplateMode, Set<T>> listMapToSetMap(final EnumMap<TemplateMode, List<T>> map) {
        final EnumMap<TemplateMode, Set<T>> newMap = new EnumMap<TemplateMode, Set<T>>(TemplateMode.class);
        for (final Map.Entry<TemplateMode, List<T>> entry : map.entrySet()) {
            newMap.put(entry.getKey(), new LinkedHashSet<T>(entry.getValue()));
        }
        return newMap;
    }




    private static void initializeDefinitionsForProcessors(
            final EnumMap<TemplateMode, ? extends Set<? extends IProcessor>> processorsByTemplateMode,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions) {

        for (final Map.Entry<TemplateMode, ? extends Set<? extends IProcessor>> entry : processorsByTemplateMode.entrySet()) {
            final Set<? extends IProcessor> processors = entry.getValue();
            for (final IProcessor processor : processors) {
                if (processor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) processor).setElementDefinitions(elementDefinitions);
                }
                if (processor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) processor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }

    }


    private static void initializeDefinitionsForPreProcessors(
            final EnumMap<TemplateMode, ? extends Set<IPreProcessor>> preProcessorsByTemplateMode,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions) {

        for (final Map.Entry<TemplateMode, ? extends Set<IPreProcessor>> entry : preProcessorsByTemplateMode.entrySet()) {
            final Set<IPreProcessor> preProcessors = entry.getValue();
            for (final IPreProcessor preProcessor : preProcessors) {
                if (preProcessor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) preProcessor).setElementDefinitions(elementDefinitions);
                }
                if (preProcessor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) preProcessor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }

    }


    private static void initializeDefinitionsForPostProcessors(
            final EnumMap<TemplateMode, ? extends Set<IPostProcessor>> postProcessorsByTemplateMode,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions) {

        for (final Map.Entry<TemplateMode, ? extends Set<IPostProcessor>> entry : postProcessorsByTemplateMode.entrySet()) {
            final Set<IPostProcessor> postProcessors = entry.getValue();
            for (final IPostProcessor postProcessor : postProcessors) {
                if (postProcessor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) postProcessor).setElementDefinitions(elementDefinitions);
                }
                if (postProcessor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) postProcessor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }

    }






    private DialectSetConfiguration(
            final Set<DialectConfiguration> dialectConfigurations, final Set<IDialect> dialects,
            final boolean standardDialectPresent, final String standardDialectPrefix,
            final Map<String, Object> executionAttributes,
            final AggregateExpressionObjectFactory expressionObjectFactory,
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions,
            final EnumMap<TemplateMode, Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IPreProcessor>> preProcessors,
            final EnumMap<TemplateMode, Set<IPostProcessor>> postProcessors) {

        super();

        this.dialectConfigurations = Collections.unmodifiableSet(dialectConfigurations);
        this.dialects = Collections.unmodifiableSet(dialects);
        this.standardDialectPresent = standardDialectPresent;
        this.standardDialectPrefix = standardDialectPrefix;
        this.executionAttributes = Collections.unmodifiableMap(executionAttributes);
        this.expressionObjectFactory = expressionObjectFactory;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateBoundariesProcessorsByTemplateMode = templateBoundariesProcessorsByTemplateMode;
        this.cdataSectionProcessorsByTemplateMode = cdataSectionProcessorsByTemplateMode;
        this.commentProcessorsByTemplateMode = commentProcessorsByTemplateMode;
        this.docTypeProcessorsByTemplateMode = docTypeProcessorsByTemplateMode;
        this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
        this.processingInstructionProcessorsByTemplateMode = processingInstructionProcessorsByTemplateMode;
        this.textProcessorsByTemplateMode = textProcessorsByTemplateMode;
        this.xmlDeclarationProcessorsByTemplateMode = xmlDeclarationProcessorsByTemplateMode;
        this.preProcessors = preProcessors;
        this.postProcessors = postProcessors;

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

    public Set<ITemplateBoundariesProcessor> getTemplateBoundariesProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<ITemplateBoundariesProcessor> processors = this.templateBoundariesProcessorsByTemplateMode.get(templateMode);
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


    public Set<IPreProcessor> getPreProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IPreProcessor> preProcessors = this.preProcessors.get(templateMode);
        if (preProcessors == null) {
            return Collections.EMPTY_SET;
        }
        return preProcessors;
    }


    public Set<IPostProcessor> getPostProcessors(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        final Set<IPostProcessor> postProcessors = this.postProcessors.get(templateMode);
        if (postProcessors == null) {
            return Collections.EMPTY_SET;
        }
        return postProcessors;
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

        public Set<String> getAllExpressionObjectNames() {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.getAllExpressionObjectNames();
            }
            if (this.expressionObjectFactoryList == null) {
                return null;
            }
            final Set<String> expressionObjectNames = new LinkedHashSet<String>(30);
            int n = this.expressionObjectFactoryList.size();
            while (n-- != 0) {
                expressionObjectNames.addAll(this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames());
            }
            return expressionObjectNames;
        }

        public Object buildObject(final IExpressionContext context, final String expressionObjectName) {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.buildObject(context, expressionObjectName);
            }
            if (this.expressionObjectFactoryList == null) {
                return null;
            }
            int n = this.expressionObjectFactoryList.size();
            while (n-- != 0) {
                if (this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames().contains(expressionObjectName)) {
                    return this.expressionObjectFactoryList.get(n).buildObject(context, expressionObjectName);
                }
            }
            return null;
        }

        public boolean isCacheable(final String expressionObjectName) {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.isCacheable(expressionObjectName);
            }
            if (this.expressionObjectFactoryList == null) {
                return false;
            }
            int n = this.expressionObjectFactoryList.size();
            while (n-- != 0) {
                if (this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames().contains(expressionObjectName)) {
                    return this.expressionObjectFactoryList.get(n).isCacheable(expressionObjectName);
                }
            }
            return false;
        }

    }


}

