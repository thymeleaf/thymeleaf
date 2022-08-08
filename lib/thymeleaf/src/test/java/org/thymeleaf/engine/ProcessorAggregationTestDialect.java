/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.AbstractCDATASectionProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler;
import org.thymeleaf.processor.comment.AbstractCommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.processor.doctype.AbstractDocTypeProcessor;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.processinginstruction.AbstractProcessingInstructionProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.processor.xmldeclaration.AbstractXMLDeclarationProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;


public final class ProcessorAggregationTestDialect extends AbstractProcessorDialect {

    private final String htmlProcSpecification;
    private final String xmlProcSpecification;



    public static ProcessorAggregationTestDialect buildDialect(
            final String name, final String prefix, final String htmlProcSpecification, final String xmlProcSpecification) {
        return ProcessorAggregationTestDialect.build(name, prefix, htmlProcSpecification, xmlProcSpecification);
    }

    public static ProcessorAggregationTestDialect buildHTMLDialect(
            final String name, final String prefix, final String htmlProcSpecification) {
        return buildDialect(name, prefix, htmlProcSpecification, "");
    }

    public static ProcessorAggregationTestDialect buildXMLDialect(
            final String name, final String prefix, final String xmlProcSpecification) {
        return buildDialect(name, prefix, "", xmlProcSpecification);
    }



    private static ProcessorAggregationTestDialect build(final String name, final String prefix,
                                          final String htmlProcSpecification, final String xmlProcSpecification) {
        return new ProcessorAggregationTestDialect(name, prefix, htmlProcSpecification, xmlProcSpecification);
    }


    private static Set<IProcessor> buildProcessors(
            final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix, final String specification) {

        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        final StringTokenizer specTok = new StringTokenizer(specification,", ");

        while (specTok.hasMoreTokens()) {

            final String procSpec = specTok.nextToken();

            final StringTokenizer procSpecTok = new StringTokenizer(procSpec,"-");

            final String type = procSpecTok.nextToken();
            if (type.equals("CD")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new CDATASectionProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("C")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new CommentProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("DT")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new DocTypeProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("PI")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new ProcessingInstructionProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("T")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new TextProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("XD")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                processors.add(
                        new XMLDeclarationProcessorAggregationTestProcessor(
                                procSpecTok.nextToken(), templateMode, precedence));
            } else if (type.equals("E")) {
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                String elementName = procSpecTok.nextToken();
                boolean prefixElementName = true;
                if (elementName.startsWith("*")) {
                    prefixElementName = false;
                    elementName = elementName.substring(1);
                }
                String attributeName = procSpecTok.nextToken();
                boolean prefixAttributeName = true;
                if (attributeName.startsWith("*")) {
                    prefixAttributeName = false;
                    attributeName = attributeName.substring(1);
                }
                processors.add(
                        new ElementProcessorAggregationTestTagProcessor(
                                dialectPrefix,
                                (elementName.equals("null")? null : elementName), prefixElementName,
                                (attributeName.equals("null")? null : attributeName), prefixAttributeName,
                                templateMode, precedence));
            } else if (type.equals("N")) {
                procSpecTok.nextToken(); // This will just be "ELEMENT"
                final int precedence = Integer.valueOf(procSpecTok.nextToken());
                String elementName = procSpecTok.nextToken();
                boolean prefixElementName = true;
                if (elementName.startsWith("*")) {
                    prefixElementName = false;
                    elementName = elementName.substring(1);
                }
                String attributeName = procSpecTok.nextToken();
                boolean prefixAttributeName = true;
                if (attributeName.startsWith("*")) {
                    prefixAttributeName = false;
                    attributeName = attributeName.substring(1);
                }
                processors.add(
                        new ElementModelProcessorAggregationTestProcessor(
                                dialectPrefix,
                                (elementName.equals("null")? null : elementName), prefixElementName,
                                (attributeName.equals("null")? null : attributeName), prefixAttributeName,
                                templateMode, precedence));

            } else {
                throw new IllegalArgumentException("Unrecognized: " + type);
            }

        }

        return processors;

    }


    protected ProcessorAggregationTestDialect(final String name, final String prefix, final String htmlProcSpecification, final String xmlProcSpecification) {
        super(name, prefix, 100);
        this.htmlProcSpecification = htmlProcSpecification;
        this.xmlProcSpecification = xmlProcSpecification;
    }


    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.addAll(buildProcessors(this, TemplateMode.HTML, dialectPrefix, this.htmlProcSpecification));
        processors.addAll(buildProcessors(this, TemplateMode.XML, dialectPrefix, this.xmlProcSpecification));
        return processors;
    }


    public String toString(final String dialectPrefix) {
        return "[" + getName() + "," + getPrefix() + "," + getProcessors(dialectPrefix) + "]";
    }



    interface NamedTestProcessor {
        String getName();
    }


    private static class CDATASectionProcessorAggregationTestProcessor extends AbstractCDATASectionProcessor implements NamedTestProcessor {

        private final String name;

        CDATASectionProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "CD-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final ICDATASection icdataSection, final ICDATASectionStructureHandler icdataSectionStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class CommentProcessorAggregationTestProcessor extends AbstractCommentProcessor implements NamedTestProcessor {

        private final String name;

        CommentProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "C-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final IComment iComment, final ICommentStructureHandler iCommentStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class DocTypeProcessorAggregationTestProcessor extends AbstractDocTypeProcessor implements NamedTestProcessor {

        private final String name;

        DocTypeProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "DT-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final IDocType iDocType, final IDocTypeStructureHandler iDocTypeStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class ProcessingInstructionProcessorAggregationTestProcessor extends AbstractProcessingInstructionProcessor implements NamedTestProcessor {

        private final String name;

        ProcessingInstructionProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "PI-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final IProcessingInstruction iProcessingInstruction, final IProcessingInstructionStructureHandler iProcessingInstructionStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class TextProcessorAggregationTestProcessor extends AbstractTextProcessor implements NamedTestProcessor {

        private final String name;

        TextProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "T-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final IText iText, final ITextStructureHandler iTextStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class XMLDeclarationProcessorAggregationTestProcessor extends AbstractXMLDeclarationProcessor implements NamedTestProcessor {

        private final String name;

        XMLDeclarationProcessorAggregationTestProcessor(final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, precedence);
            this.name = name;
        }

        public String getName() {
            return "XD-" + getPrecedence() + "-" + this.name;
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(final ITemplateContext iTemplateProcessingContext, final IXMLDeclaration ixmlDeclaration, final IXMLDeclarationStructureHandler ixmlDeclarationStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class ElementProcessorAggregationTestTagProcessor extends AbstractElementTagProcessor implements NamedTestProcessor {

        ElementProcessorAggregationTestTagProcessor(
                final String dialectPrefix,
                final String elementName, final boolean prefixElementName,
                final String attributeName, final boolean prefixAttributeName,
                final TemplateMode templateMode, final int precedence) {
            super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        }

        public String getName() {
            return "E-" + getPrecedence() + "-" + this.getMatchingElementName() + "-" + this.getMatchingAttributeName();
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(
                final ITemplateContext iTemplateProcessingContext,
                final IProcessableElementTag iProcessableElementTag,
                final IElementTagStructureHandler iElementTagStructureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }


    private static class ElementModelProcessorAggregationTestProcessor extends AbstractElementModelProcessor implements NamedTestProcessor {

        private final String name;

        ElementModelProcessorAggregationTestProcessor(
                final String dialectPrefix,
                final String elementName, final boolean prefixElementName,
                final String attributeName, final boolean prefixAttributeName,
                final TemplateMode templateMode, final int precedence) {
            super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
            this.name = null;
        }

        ElementModelProcessorAggregationTestProcessor(
                final String dialectPrefix, final String name, final TemplateMode templateMode, final int precedence) {
            super(templateMode, dialectPrefix, null, false, null, false, precedence);
            this.name = name;
        }

        public String getName() {
                return "N-ELEMENT" + "-" + getPrecedence() + "-" + this.getMatchingElementName() + "-" + this.getMatchingAttributeName();
        }

        public String toString() {
            return getName();
        }

        @Override
        protected void doProcess(
                final ITemplateContext iTemplateProcessingContext,
                final IModel model,
                final IElementModelStructureHandler structureHandler) {
            // Nothing to be done here - nothing to process!
        }

    }



}
