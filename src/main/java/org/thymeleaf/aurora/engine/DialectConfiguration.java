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
package org.thymeleaf.aurora.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.dialect.IProcessorDialect;
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
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class DialectConfiguration {

    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final EnumMap<TemplateMode,Set<IProcessor>> cdataSectionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> commentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> docTypeProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> elementProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> processingInstructionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> textProcessorsByTemplateMode;
    private final EnumMap<TemplateMode,Set<IProcessor>> xmlDeclarationProcessorsByTemplateMode;




    public static DialectConfiguration build(final Map<String,IDialect> dialects) {

        Validate.notNull(dialects, "Dialect map cannot be null");

        final Set<IProcessor> allProcessors = new LinkedHashSet<IProcessor>(80); // Just used for checking processors are not repeated

        final EnumMap<TemplateMode, List<IProcessor>> cdataSectionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> commentProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> docTypeProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> elementProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> processingInstructionProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> textProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);
        final EnumMap<TemplateMode, List<IProcessor>> xmlDeclarationProcessorListsByTemplateMode = new EnumMap<TemplateMode, List<IProcessor>>(TemplateMode.class);

        for (final Map.Entry<String,IDialect> dialectEntry : dialects.entrySet()) {

            final String dialectPrefix = dialectEntry.getKey();
            final IDialect dialect = dialectEntry.getValue();

            if (dialect == null) {
                throw new IllegalArgumentException("Null dialect has been specified");
            }

            if (dialect instanceof IProcessorDialect) {

                final Set<IProcessor> dialectProcessors = ((IProcessorDialect)dialect).getProcessors();

                if (dialectProcessors == null) {
                    throw new IllegalArgumentException("Dialect should not return null processor set: " + dialect.getClass().getName());
                }

                for (final IProcessor dialectProcessor : dialectProcessors) {

                    if (dialectProcessor == null) {
                        throw new IllegalArgumentException("Dialect should not return null processor in processor set: " + dialect.getClass().getName());
                    }


                    // Check that the processor instance is unique among all the dialect instances - this is a requirement
                    // due to the fact that processors have to be initialized by setting them a prefix and a dialect
                    if (allProcessors.contains(dialectProcessor)) {
                        throw new IllegalArgumentException(
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
                        throw new IllegalArgumentException("Template mode cannot be null (processor: " + dialectProcessor.getClass().getName() + ")");
                    }

                    final INodeProcessor.MatchingNodeType matchingNodeType =
                            (dialectProcessor instanceof INodeProcessor? ((INodeProcessor)dialectProcessor).getMatchingNodeType() : null);

                    if (dialectProcessor instanceof IElementProcessor || INodeProcessor.MatchingNodeType.ELEMENT.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = elementProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            elementProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);


                    } else if (dialectProcessor instanceof ICDATASectionProcessor || INodeProcessor.MatchingNodeType.CDATA_SECTION.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = cdataSectionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            cdataSectionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ICommentProcessor || INodeProcessor.MatchingNodeType.COMMENT.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = commentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            commentProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IDocTypeProcessor || INodeProcessor.MatchingNodeType.DOC_TYPE.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = docTypeProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            docTypeProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IProcessingInstructionProcessor || INodeProcessor.MatchingNodeType.PROCESSING_INSTRUCTION.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = processingInstructionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            processingInstructionProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof ITextProcessor || INodeProcessor.MatchingNodeType.TEXT.equals(matchingNodeType)) {

                        List<IProcessor> processorsForTemplateMode = textProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<IProcessor>(5);
                            textProcessorListsByTemplateMode.put(templateMode, processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(dialectProcessor);
                        Collections.sort(processorsForTemplateMode, PrecedenceProcessorComparator.INSTANCE);

                    } else if (dialectProcessor instanceof IXMLDeclarationProcessor || INodeProcessor.MatchingNodeType.XML_DECLARATION.equals(matchingNodeType)) {

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



        return new DialectConfiguration(
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








    private DialectConfiguration(
            final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions,
            final EnumMap<TemplateMode, Set<IProcessor>> cdataSectionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> commentProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> docTypeProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> elementProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> processingInstructionProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> textProcessorsByTemplateMode,
            final EnumMap<TemplateMode, Set<IProcessor>> xmlDeclarationProcessorsByTemplateMode) {

        super();

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

}
