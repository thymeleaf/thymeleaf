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
package org.thymeleaf.util;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Utility class containing methods relating to the configuration of processors (e.g. wrapping).
 * </p>
 * <p>
 *   This class is mainly for <strong>internal use</strong>.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorConfigurationUtils {


    /**
     * <p>
     *   Wraps an implementation of {@link IElementProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static IElementProcessor wrap(final IElementProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        if (processor instanceof IElementTagProcessor) {
            return new ElementTagProcessorWrapper((IElementTagProcessor) processor, dialect);
        }
        if (processor instanceof IElementModelProcessor) {
            return new ElementModelProcessorWrapper((IElementModelProcessor) processor, dialect);
        }
        throw new IllegalArgumentException("Unknown element processor interface implemented by " + processor + " of " +
                                           "class: " + processor.getClass().getName());
    }


    /**
     * <p>
     *   Wraps an implementation of {@link ICDATASectionProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static ICDATASectionProcessor wrap(final ICDATASectionProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new CDATASectionProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link ICommentProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static ICommentProcessor wrap(final ICommentProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new CommentProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link IDocTypeProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static IDocTypeProcessor wrap(final IDocTypeProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new DocTypeProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link IProcessingInstructionProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static IProcessingInstructionProcessor wrap(final IProcessingInstructionProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new ProcessingInstructionProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link ITemplateBoundariesProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static ITemplateBoundariesProcessor wrap(final ITemplateBoundariesProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new TemplateBoundariesProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link ITextProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static ITextProcessor wrap(final ITextProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new TextProcessorWrapper(processor, dialect);
    }


    /**
     * <p>
     *   Wraps an implementation of {@link IXMLDeclarationProcessor} into an object that adds some information
     *   required internally (like e.g. the dialect this processor was registered for).
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be wrapped.
     * @param dialect the dialect this processor was configured for.
     * @return the wrapped processor.
     */
    public static IXMLDeclarationProcessor wrap(final IXMLDeclarationProcessor processor, final IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new XMLDeclarationProcessorWrapper(processor, dialect);
    }




    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link IElementProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static IElementProcessor unwrap(final IElementProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IElementProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link ICDATASectionProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static ICDATASectionProcessor unwrap(final ICDATASectionProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ICDATASectionProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link ICommentProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static ICommentProcessor unwrap(final ICommentProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ICommentProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link IDocTypeProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static IDocTypeProcessor unwrap(final IDocTypeProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IDocTypeProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link IProcessingInstructionProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static IProcessingInstructionProcessor unwrap(final IProcessingInstructionProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IProcessingInstructionProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link ITemplateBoundariesProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static ITemplateBoundariesProcessor unwrap(final ITemplateBoundariesProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ITemplateBoundariesProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link ITextProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static ITextProcessor unwrap(final ITextProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ITextProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }


    /**
     * <p>
     *   Unwraps a wrapped implementation of {@link IXMLDeclarationProcessor}.
     * </p>
     * <p>
     *   This method is meant for <strong>internal</strong> use only.
     * </p>
     *
     * @param processor the processor to be unwrapped.
     * @return the unwrapped processor.
     */
    public static IXMLDeclarationProcessor unwrap(final IXMLDeclarationProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IXMLDeclarationProcessor)((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }





    private ProcessorConfigurationUtils() {
        super();
    }




    /*
     * This is the base class for a hierarchy of wrappers that will be applied to all processors configured
     * at the engine in order to add additional information to them (e.g. the dialect they are configured for).
     */
    static abstract class AbstractProcessorWrapper implements IProcessor {

        private final IProcessorDialect dialect;
        protected final IProcessor processor;

        AbstractProcessorWrapper(final IProcessor processor, final IProcessorDialect dialect) {
            super();
            this.dialect = dialect;
            this.processor = processor;
        }

        public TemplateMode getTemplateMode() {
            return this.processor.getTemplateMode();
        }

        public int getPrecedence() {
            return this.processor.getPrecedence();
        }

        public IProcessorDialect getDialect() {
            return this.dialect;
        }

        public IProcessor unwrap() {
            return this.processor;
        }

        @Override
        public String toString() {
            return this.processor.toString();
        }

    }


    static abstract class AbstractElementProcessorWrapper extends AbstractProcessorWrapper implements IElementProcessor {

        AbstractElementProcessorWrapper(final IElementProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public MatchingElementName getMatchingElementName() {
            return ((IElementProcessor)this.processor).getMatchingElementName();
        }

        public MatchingAttributeName getMatchingAttributeName() {
            return ((IElementProcessor)this.processor).getMatchingAttributeName();
        }

    }


    static final class ElementTagProcessorWrapper extends AbstractElementProcessorWrapper implements IElementTagProcessor {

        ElementTagProcessorWrapper(final IElementTagProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IProcessableElementTag tag, final IElementTagStructureHandler structureHandler) {
            ((IElementTagProcessor)this.processor).process(context, tag, structureHandler);
        }

    }


    static final class ElementModelProcessorWrapper extends AbstractElementProcessorWrapper implements IElementModelProcessor {

        ElementModelProcessorWrapper(final IElementModelProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IModel model, final IElementModelStructureHandler structureHandler) {
            ((IElementModelProcessor)this.processor).process(context, model, structureHandler);
        }

    }


    static final class CDATASectionProcessorWrapper extends AbstractProcessorWrapper implements ICDATASectionProcessor {

        CDATASectionProcessorWrapper(final ICDATASectionProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final ICDATASection cdataSection, final ICDATASectionStructureHandler structureHandler) {
            ((ICDATASectionProcessor)this.processor).process(context, cdataSection, structureHandler);
        }

    }


    static final class CommentProcessorWrapper extends AbstractProcessorWrapper implements ICommentProcessor {

        CommentProcessorWrapper(final ICommentProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IComment comment, final ICommentStructureHandler structureHandler) {
            ((ICommentProcessor)this.processor).process(context, comment, structureHandler);
        }

    }


    static final class DocTypeProcessorWrapper extends AbstractProcessorWrapper implements IDocTypeProcessor {

        DocTypeProcessorWrapper(final IDocTypeProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IDocType docType, final IDocTypeStructureHandler structureHandler) {
            ((IDocTypeProcessor)this.processor).process(context, docType, structureHandler);
        }

    }


    static final class ProcessingInstructionProcessorWrapper extends AbstractProcessorWrapper implements IProcessingInstructionProcessor {

        ProcessingInstructionProcessorWrapper(final IProcessingInstructionProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IProcessingInstruction processingInstruction, final IProcessingInstructionStructureHandler structureHandler) {
            ((IProcessingInstructionProcessor)this.processor).process(context, processingInstruction, structureHandler);
        }

    }


    static final class TemplateBoundariesProcessorWrapper extends AbstractProcessorWrapper implements ITemplateBoundariesProcessor {

        TemplateBoundariesProcessorWrapper(final ITemplateBoundariesProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void processTemplateStart(final ITemplateContext context, final ITemplateStart templateStart, final ITemplateBoundariesStructureHandler structureHandler) {
            ((ITemplateBoundariesProcessor)this.processor).processTemplateStart(context, templateStart, structureHandler);
        }

        public void processTemplateEnd(final ITemplateContext context, final ITemplateEnd templateEnd, final ITemplateBoundariesStructureHandler structureHandler) {
            ((ITemplateBoundariesProcessor)this.processor).processTemplateEnd(context, templateEnd, structureHandler);
        }

    }


    static final class TextProcessorWrapper extends AbstractProcessorWrapper implements ITextProcessor {

        TextProcessorWrapper(final ITextProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IText text, final ITextStructureHandler structureHandler) {
            ((ITextProcessor)this.processor).process(context, text, structureHandler);
        }

    }


    static final class XMLDeclarationProcessorWrapper extends AbstractProcessorWrapper implements IXMLDeclarationProcessor {

        XMLDeclarationProcessorWrapper(final IXMLDeclarationProcessor processor, final IProcessorDialect dialect) {
            super(processor, dialect);
        }

        public void process(final ITemplateContext context, final IXMLDeclaration xmlDeclaration, final IXMLDeclarationStructureHandler structureHandler) {
            ((IXMLDeclarationProcessor)this.processor).process(context, xmlDeclaration, structureHandler);
        }

    }


}
