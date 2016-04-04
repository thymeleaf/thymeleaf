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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EventModelController.SkipBody;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler implements ITemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorTemplateHandler.class);

    // This is a set containing all the names of the elements for which, when iterated, we should preserve
    // the preceding whitespace if it exists so that resulting markup is more readable. Note they are all block
    // elements or, at least, elements for which preceding whitespace should not matter
    private static final Set<HTMLElementName> ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES =
            new HashSet<HTMLElementName>(Arrays.asList(new HTMLElementName[] {
                    ElementNames.forHTMLName("address"), ElementNames.forHTMLName("article"), ElementNames.forHTMLName("aside"),
                    ElementNames.forHTMLName("audio"), ElementNames.forHTMLName("blockquote"), ElementNames.forHTMLName("canvas"),
                    ElementNames.forHTMLName("dd"), ElementNames.forHTMLName("div"), ElementNames.forHTMLName("dl"),
                    ElementNames.forHTMLName("dt"), ElementNames.forHTMLName("fieldset"), ElementNames.forHTMLName("figcaption"),
                    ElementNames.forHTMLName("figure"), ElementNames.forHTMLName("footer"),ElementNames.forHTMLName("form"),
                    ElementNames.forHTMLName("h1"), ElementNames.forHTMLName("h2"), ElementNames.forHTMLName("h3"),
                    ElementNames.forHTMLName("h4"), ElementNames.forHTMLName("h5"), ElementNames.forHTMLName("h6"),
                    ElementNames.forHTMLName("header"), ElementNames.forHTMLName("hgroup"), ElementNames.forHTMLName("hr"),
                    ElementNames.forHTMLName("li"), ElementNames.forHTMLName("main"), ElementNames.forHTMLName("nav"),
                    ElementNames.forHTMLName("noscript"), ElementNames.forHTMLName("ol"), ElementNames.forHTMLName("option"),
                    ElementNames.forHTMLName("output"), ElementNames.forHTMLName("p"), ElementNames.forHTMLName("pre"),
                    ElementNames.forHTMLName("section"), ElementNames.forHTMLName("table"), ElementNames.forHTMLName("tbody"),
                    ElementNames.forHTMLName("td"), ElementNames.forHTMLName("tfoot"), ElementNames.forHTMLName("th"),
                    ElementNames.forHTMLName("tr"), ElementNames.forHTMLName("ul"), ElementNames.forHTMLName("video")
            }));

    static final String SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME = ISyntheticModel.class.getName();


    private static final ITemplateBoundariesProcessor[] EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS = new ITemplateBoundariesProcessor[0];
    private static final ICDATASectionProcessor[] EMPTY_CDATA_SECTION_PROCESSORS = new ICDATASectionProcessor[0];
    private static final ICommentProcessor[] EMPTY_COMMENT_PROCESSORS = new ICommentProcessor[0];
    private static final IDocTypeProcessor[] EMPTY_DOCTYPE_PROCESSORS = new IDocTypeProcessor[0];
    private static final IProcessingInstructionProcessor[] EMPTY_PROCESSING_INSTRUCTION_PROCESSORS = new IProcessingInstructionProcessor[0];
    private static final ITextProcessor[] EMPTY_TEXT_PROCESSORS = new ITextProcessor[0];
    private static final IXMLDeclarationProcessor[] EMPTY_XML_DECLARATION_PROCESSORS = new IXMLDeclarationProcessor[0];


    // Structure handlers are reusable objects that will be used by processors in order to instruct the engine to
    // do things with the processed structures themselves (things that cannot be directly done from the processors like
    // removing structures or iterating elements)
    private final ElementTagStructureHandler elementTagStructureHandler;
    private final ElementModelStructureHandler elementModelStructureHandler;
    private final TemplateBoundariesStructureHandler templateStructureHandler;
    private final CDATASectionStructureHandler cdataSectionStructureHandler;
    private final CommentStructureHandler commentStructureHandler;
    private final DocTypeStructureHandler docTypeStructureHandler;
    private final ProcessingInstructionStructureHandler processingInstructionStructureHandler;
    private final TextStructureHandler textStructureHandler;
    private final XMLDeclarationStructureHandler xmlDeclarationStructureHandler;

    private ITemplateHandler next = null;

    private IEngineConfiguration configuration = null;
    private AttributeDefinitions attributeDefinitions = null;
    private TemplateMode templateMode = null;

    private ITemplateContext context = null;
    private IEngineContext engineContext = null;


    // These arrays will be initialized with all the registered processors for the different kind of non-element
    // processors. This is done so because non-element processors will not change during the execution of the engine
    // (whereas element processors can). And they are kept in the form of an array because they will be faster to
    // iterate than asking every time the configuration object for the Set of processors and creating an iterator for it
    private ITemplateBoundariesProcessor[] templateBoundariesProcessors = null;
    private ICDATASectionProcessor[] cdataSectionProcessors = null;
    private ICommentProcessor[] commentProcessors = null;
    private IDocTypeProcessor[] docTypeProcessors = null;
    private IProcessingInstructionProcessor[] processingInstructionProcessors = null;
    private ITextProcessor[] textProcessors = null;
    private IXMLDeclarationProcessor[] xmlDeclarationProcessors = null;


    private Integer initialContextLevel = null;


    // This variable will contain the last event that has been processed, if this last event was an IText. Its aim
    // is to allow the inclusion of preceding whitespace in the iteration of block elements (such as <tr>, <li>, etc.)
    // so that resulting markup is more readable than the alternative "</tr><tr ...>"
    // Note also that, given whitespace between tags is not significative in XML, this mechanism will be applied in XML
    // template mode disregarding the name of the element.
    private IText lastTextEvent = null;

    // The eventModelController will be in charge of deciding if we have to skip the processing of an event, because it has to be
    // discarded or maybe because events are being gathered for future processing as a whole (e.g. iteration or
    // element model processors).
    private EventModelController eventModelController = null;




    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {

        super();

        this.elementTagStructureHandler = new ElementTagStructureHandler();
        this.elementModelStructureHandler = new ElementModelStructureHandler();
        this.templateStructureHandler = new TemplateBoundariesStructureHandler();
        this.cdataSectionStructureHandler = new CDATASectionStructureHandler();
        this.commentStructureHandler = new CommentStructureHandler();
        this.docTypeStructureHandler = new DocTypeStructureHandler();
        this.processingInstructionStructureHandler = new ProcessingInstructionStructureHandler();
        this.textStructureHandler = new TextStructureHandler();
        this.xmlDeclarationStructureHandler = new XMLDeclarationStructureHandler();

    }




    @Override
    public void setNext(final ITemplateHandler next) {
        this.next = next;
    }




    @Override
    public void setContext(final ITemplateContext context) {

        this.context = context;
        Validate.notNull(this.context, "Context cannot be null");
        Validate.notNull(this.context.getTemplateMode(), "Template Mode returned by context cannot be null");

        this.configuration = context.getConfiguration();
        Validate.notNull(this.configuration, "Engine Configuration returned by context cannot be null");
        Validate.notNull(this.configuration.getElementDefinitions(), "Element Definitions returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getAttributeDefinitions(), "Attribute Definitions returned by the Engine Configuration cannot be null");

        this.attributeDefinitions = this.configuration.getAttributeDefinitions();

        this.templateMode = this.context.getTemplateMode(); // Just a way to avoid doing the call each time

        if (this.context instanceof IEngineContext) {
            this.engineContext = (IEngineContext) this.context;
        } else {
            logger.warn("Unknown implementation of the " + ITemplateContext.class.getName() + " interface: " +
                        this.context.getClass().getName() + ". Local variable support will be DISABLED (this " +
                        "includes iteration, target selection and inlining). In order to enable these, context " +
                        "implementations should also implement the " + IEngineContext.class.getName() +
                        " interface.");
            this.engineContext = null;
        }

        // Instance the gatherer
        this.eventModelController = new EventModelController(this.configuration, this.templateMode, this.engineContext);

        // Obtain all processor sets and compute sizes
        final Set<ITemplateBoundariesProcessor> templateBoundariesProcessorSet = this.configuration.getTemplateBoundariesProcessors(this.templateMode);
        final Set<ICDATASectionProcessor> cdataSectionProcessorSet = this.configuration.getCDATASectionProcessors(this.templateMode);
        final Set<ICommentProcessor> commentProcessorSet = this.configuration.getCommentProcessors(this.templateMode);
        final Set<IDocTypeProcessor> docTypeProcessorSet = this.configuration.getDocTypeProcessors(this.templateMode);
        final Set<IProcessingInstructionProcessor> processingInstructionProcessorSet = this.configuration.getProcessingInstructionProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessorSet = this.configuration.getTextProcessors(this.templateMode);
        final Set<IXMLDeclarationProcessor> xmlDeclarationProcessorSet = this.configuration.getXMLDeclarationProcessors(this.templateMode);

        // Initialize arrays containing the processors for all the non-element structures (these do not change during execution)
        this.templateBoundariesProcessors =
                templateBoundariesProcessorSet.size() == 0? EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS : templateBoundariesProcessorSet.toArray(new ITemplateBoundariesProcessor[templateBoundariesProcessorSet.size()]);
        this.cdataSectionProcessors =
                cdataSectionProcessorSet.size() == 0? EMPTY_CDATA_SECTION_PROCESSORS : cdataSectionProcessorSet.toArray(new ICDATASectionProcessor[cdataSectionProcessorSet.size()]);
        this.commentProcessors =
                commentProcessorSet.size() == 0? EMPTY_COMMENT_PROCESSORS : commentProcessorSet.toArray(new ICommentProcessor[commentProcessorSet.size()]);
        this.docTypeProcessors =
                docTypeProcessorSet.size() == 0? EMPTY_DOCTYPE_PROCESSORS : docTypeProcessorSet.toArray(new IDocTypeProcessor[docTypeProcessorSet.size()]);
        this.processingInstructionProcessors =
                processingInstructionProcessorSet.size() == 0? EMPTY_PROCESSING_INSTRUCTION_PROCESSORS : processingInstructionProcessorSet.toArray(new IProcessingInstructionProcessor[processingInstructionProcessorSet.size()]);
        this.textProcessors =
                textProcessorSet.size() == 0? EMPTY_TEXT_PROCESSORS : textProcessorSet.toArray(new ITextProcessor[textProcessorSet.size()]);
        this.xmlDeclarationProcessors =
                xmlDeclarationProcessorSet.size() == 0? EMPTY_XML_DECLARATION_PROCESSORS : xmlDeclarationProcessorSet.toArray(new IXMLDeclarationProcessor[xmlDeclarationProcessorSet.size()]);

    }










    private Model initializeModel(final Model model) {
        if (model != null) {
            model.reset();
            return model;
        }
        return new Model(this.configuration, this.templateMode);
    }




    @Override
    public void handleTemplateStart(final ITemplateStart itemplateStart) {

        /*
         * Save the initial engine context level, so that after processing we can ensure it matches
         */
        if (this.engineContext != null) {
            this.initialContextLevel = Integer.valueOf(this.engineContext.level());
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.templateBoundariesProcessors.length == 0) {
            this.next.handleTemplateStart(itemplateStart);
            return;
        }


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; i < this.templateBoundariesProcessors.length; i++) {

            this.templateStructureHandler.reset();

            this.templateBoundariesProcessors[i].processTemplateStart(
                    this.context, itemplateStart, this.templateStructureHandler);

            if (this.templateStructureHandler.setLocalVariable) {
                if (this.engineContext != null) {
                    this.engineContext.setVariables(this.templateStructureHandler.addedLocalVariables);
                }
            }

            if (this.templateStructureHandler.removeLocalVariable) {
                if (this.engineContext != null) {
                    for (final String variableName : this.templateStructureHandler.removedLocalVariableNames) {
                        this.engineContext.removeVariable(variableName);
                    }
                }
            }

            if (this.templateStructureHandler.setSelectionTarget) {
                if (this.engineContext != null) {
                    this.engineContext.setSelectionTarget(this.templateStructureHandler.selectionTargetObject);
                }
            }

            if (this.templateStructureHandler.setInliner) {
                if (this.engineContext != null) {
                    this.engineContext.setInliner(this.templateStructureHandler.setInlinerValue);
                }
            }

            if (this.templateStructureHandler.insertText) {

                model = initializeModel(model);
                model.add(new Text(this.templateStructureHandler.insertTextValue));
                modelHandler = this.templateStructureHandler.insertTextProcessable? this : this.next;

            } else if (this.templateStructureHandler.insertModel) {

                model = initializeModel(model);
                model.addModel(this.templateStructureHandler.insertModelValue);
                modelHandler = this.templateStructureHandler.insertModelProcessable? this : this.next;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        this.next.handleTemplateStart(itemplateStart);


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }








    @Override
    public void handleTemplateEnd(final ITemplateEnd itemplateEnd) {

        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.templateBoundariesProcessors.length == 0) {
            this.next.handleTemplateEnd(itemplateEnd);
            return;
        }


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; i < this.templateBoundariesProcessors.length; i++) {

            this.templateStructureHandler.reset();

            this.templateBoundariesProcessors[i].processTemplateEnd(
                    this.context, itemplateEnd, this.templateStructureHandler);

            if (this.templateStructureHandler.setLocalVariable) {
                if (this.engineContext != null) {
                    this.engineContext.setVariables(this.templateStructureHandler.addedLocalVariables);
                }
            }

            if (this.templateStructureHandler.removeLocalVariable) {
                if (this.engineContext != null) {
                    for (final String variableName : this.templateStructureHandler.removedLocalVariableNames) {
                        this.engineContext.removeVariable(variableName);
                    }
                }
            }

            if (this.templateStructureHandler.setSelectionTarget) {
                if (this.engineContext != null) {
                    this.engineContext.setSelectionTarget(this.templateStructureHandler.selectionTargetObject);
                }
            }

            if (this.templateStructureHandler.setInliner) {
                if (this.engineContext != null) {
                    this.engineContext.setInliner(this.templateStructureHandler.setInlinerValue);
                }
            }

            if (this.templateStructureHandler.insertText) {

                model = initializeModel(model);
                model.add(new Text(this.templateStructureHandler.insertTextValue));
                modelHandler = this.templateStructureHandler.insertTextProcessable? this : this.next;

            } else if (this.templateStructureHandler.insertModel) {

                model = initializeModel(model);
                model.addModel(this.templateStructureHandler.insertModelValue);
                modelHandler = this.templateStructureHandler.insertModelProcessable? this : this.next;

            }

        }


        /*
         * PROCESS THE QUEUE, launching all the queued events (BEFORE DELEGATING)
         */
        if (model != null) {
            model.process(modelHandler);
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN.
         */
        this.next.handleTemplateEnd(itemplateEnd);


        /*
         * LAST ROUND OF CHECKS. If we have not returned our indexes to -1, something has gone wrong during processing
         */
        if (this.eventModelController.getModelLevel() != 0) {
            throw new TemplateProcessingException(
                    "Bad markup or template processing sequence. Model level is != 0 (" + this.eventModelController.getModelLevel() + ") " +
                    "at template end.", itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
        }
        if (this.engineContext != null) {
            if (this.engineContext.level() != this.initialContextLevel.intValue()) {
                throw new TemplateProcessingException(
                        "Bad markup or template processing sequence. Context level after processing (" + this.engineContext.level() + ") " +
                        "does not correspond to context level before processing (" + this.initialContextLevel.intValue() + ").",
                        itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
            }
        }


    }








    @Override
    public void handleText(final IText itext) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessText(itext)) {
            return;
        }


        /*
         * KEEP THE POINTER to this event, now we know it will be processed somehow
         */
        this.lastTextEvent = itext;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.textProcessors.length == 0) {
            this.next.handleText(itext);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        Text text = Text.asEngineText(itext);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.textProcessors.length; i++) {

            this.textStructureHandler.reset();

            this.textProcessors[i].process(this.context, text, this.textStructureHandler);

            if (this.textStructureHandler.setText) {

                text = new Text(this.textStructureHandler.setTextValue);

            } else if (this.textStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.textStructureHandler.replaceWithModelValue);
                modelHandler = this.textStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.textStructureHandler.removeText) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleText(text);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }








    @Override
    public void handleComment(final IComment icomment) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessComment(icomment)) {
            return;
        }


        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.commentProcessors.length == 0) {
            this.next.handleComment(icomment);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        Comment comment = Comment.asEngineComment(icomment);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.commentProcessors.length; i++) {

            this.commentStructureHandler.reset();

            this.commentProcessors[i].process(this.context, comment, this.commentStructureHandler);

            if (this.commentStructureHandler.setContent) {

                comment = new Comment(comment.prefix, this.commentStructureHandler.setContentValue, comment.suffix);

            } else if (this.commentStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.commentStructureHandler.replaceWithModelValue);
                modelHandler = this.commentStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.commentStructureHandler.removeComment) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleComment(comment);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }







    
    @Override
    public void handleCDATASection(final ICDATASection icdataSection) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessCDATASection(icdataSection)) {
            return;
        }


        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.cdataSectionProcessors.length == 0) {
            this.next.handleCDATASection(icdataSection);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        CDATASection cdataSection = CDATASection.asEngineCDATASection(icdataSection);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.cdataSectionProcessors.length; i++) {

            this.cdataSectionStructureHandler.reset();

            this.cdataSectionProcessors[i].process(this.context, cdataSection, this.cdataSectionStructureHandler);

            if (this.cdataSectionStructureHandler.setContent) {

                cdataSection = new CDATASection(cdataSection.prefix, this.cdataSectionStructureHandler.setContentValue, cdataSection.suffix);

            } else if (this.cdataSectionStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.cdataSectionStructureHandler.replaceWithModelValue);
                modelHandler = this.cdataSectionStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.cdataSectionStructureHandler.removeCDATASection) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleCDATASection(cdataSection);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }








    @Override
    public void handleStandaloneElement(final IStandaloneElementTag istandaloneElementTag) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessStandaloneElement(istandaloneElementTag)) {
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        StandaloneElementTag standaloneElementTag = StandaloneElementTag.asEngineStandaloneElementTag(istandaloneElementTag);


        /*
         * SAVE AND RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         * Note we will only be interested on it if it is whitespace, in order to add it to iteration queues, so
         * that iterated model looks better (by including the last whitespace before the iterated element)
         * Also, note we do not mind the fact that IText events are reusable buffers and might have changed, because
         * if there is a this.lastTextEvent != null, it means it was the last event and therefore cannot have been
         * reused so far
         */
        final IText lastText = this.lastTextEvent;
        this.lastTextEvent = null;


        /*
         * OBTAIN THE PENDING SYNTHETIC MODEL (if any)
         */
        final ISyntheticModel executedSyntheticModel;
        if (this.engineContext != null && this.engineContext.containsVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME)) {
            executedSyntheticModel = (ISyntheticModel) this.engineContext.getVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME);
            this.engineContext.removeVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME);
        } else {
            executedSyntheticModel = null;
        }


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (executedSyntheticModel == null && !standaloneElementTag.hasAssociatedProcessors()) {
            if (this.engineContext != null) {
                this.engineContext.increaseLevel();
            }
            this.next.handleStandaloneElement(standaloneElementTag);
            if (this.engineContext != null) {
                this.engineContext.decreaseLevel();
            }
            return;
        }


        /*
         * DECLARE THE FLAGS AND STATE VARS NEEDED FOR PROCESSOR EXECUTION
         */
        final ElementProcessorIterator processorIterator;
        Model modelBefore = null;
        Model modelAfter = null;
        boolean modelAfterProcessable = false;
        boolean discardEvent = false;
        SkipBody skipBody = SkipBody.PROCESS;
        boolean skipCloseTag = false;


        /*
         * INITIALIZE THE FLAGS IN CASE THE EXECUTION WAS SUSPENDED (while a gathering took place)
         */
        if (executedSyntheticModel == null) {
            processorIterator = new ElementProcessorIterator();
        } else {
            processorIterator = executedSyntheticModel.getSuspendedProcessorIterator();
            modelBefore = executedSyntheticModel.getSuspendedModelBefore();
            modelAfter = executedSyntheticModel.getSuspendedModelAfter();
            modelAfterProcessable = executedSyntheticModel.isSuspendedModelAfterProcessable();
            discardEvent = executedSyntheticModel.isSuspendedDiscardEvent();
            skipBody = executedSyntheticModel.getSuspendedSkipBody();
            skipCloseTag = executedSyntheticModel.isSuspendedSkipCloseTag();
        }


        /*
         * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        // TODO Isn't this wrong?
        if (this.engineContext != null) {
            this.engineContext.increaseLevel();
        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!discardEvent && (processor = processorIterator.next(standaloneElementTag)) != null) {

            this.elementTagStructureHandler.reset();
            this.elementModelStructureHandler.reset();

            if (processor instanceof IElementTagProcessor) {

                final IElementTagProcessor elementProcessor = ((IElementTagProcessor)processor);
                elementProcessor.process(this.context, standaloneElementTag, this.elementTagStructureHandler);

                if (this.engineContext != null) {
                    this.elementTagStructureHandler.applyContextModifications(this.engineContext);
                }

                standaloneElementTag =
                        this.elementTagStructureHandler.applyAttributes(this.attributeDefinitions, standaloneElementTag);

                if (this.elementTagStructureHandler.iterateElement) {

                    // If there is a preceding whitespace, add it to the iteration spec
                    Text precedingWhitespace = null;
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                                    (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(standaloneElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(lastText);
                        if (lastEngineText.isWhitespace()) {
                            precedingWhitespace = lastEngineText;
                        }
                    }

                    // Initialize a synthetic model
                    this.eventModelController.startGatheringIteratedModel(
                            standaloneElementTag,
                            processorIterator,
                            modelBefore, modelAfter, modelAfterProcessable,
                            discardEvent, skipBody, skipCloseTag,
                            this.elementTagStructureHandler.iterVariableName,
                            this.elementTagStructureHandler.iterStatusVariableName,
                            this.elementTagStructureHandler.iteratedObject,
                            precedingWhitespace);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Process the synthetic model
                    final ISyntheticModel newGatheredModel = this.eventModelController.getGatheredModel();
                    this.eventModelController.resetGathering();
                    newGatheredModel.process(this);

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(
                                    standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                                    standaloneElementTag.elementCompleteName, standaloneElementTag.attributes, standaloneElementTag.synthetic,
                                    standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);
                    final CloseElementTag closeTag =
                            new CloseElementTag(
                                    standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                                    standaloneElementTag.elementCompleteName, null, standaloneElementTag.synthetic, false,
                                    standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);

                    // Prepare the text node that will be added to the queue (which will be suspended)
                    final Text text = new Text(this.elementTagStructureHandler.setBodyTextValue);
                    modelAfter.add(text);
                    modelAfterProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // Initialize the iterated model object
                    this.eventModelController.startGatheringStandaloneEquivalentModel(
                                    openTag,
                                    processorIterator,
                                    modelBefore, modelAfter, modelAfterProcessable,
                                    discardEvent, skipBody, skipCloseTag);
                    final ISyntheticModel newGatheredModel = this.eventModelController.getGatheredModel();
                    this.eventModelController.resetGathering();
                    newGatheredModel.gatherCloseElement(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    newGatheredModel.process(this);

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(
                                    standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                                    standaloneElementTag.elementCompleteName, standaloneElementTag.attributes, standaloneElementTag.synthetic,
                                    standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);
                    final CloseElementTag closeTag =
                            new CloseElementTag(
                                    standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                                    standaloneElementTag.elementCompleteName, null, standaloneElementTag.synthetic, false,
                                    standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);

                    // Prepare the queue (that we will suspend)
                    // Model will be automatically cloned if mutable
                    modelAfter.addModel(this.elementTagStructureHandler.setBodyModelValue);
                    modelAfterProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Initialize the iterated model object
                    this.eventModelController.startGatheringStandaloneEquivalentModel(
                            openTag,
                            processorIterator,
                            modelBefore, modelAfter, modelAfterProcessable,
                            discardEvent, skipBody, skipCloseTag);
                    final ISyntheticModel newGatheredModel = this.eventModelController.getGatheredModel();
                    this.eventModelController.resetGathering();
                    newGatheredModel.gatherCloseElement(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    newGatheredModel.process(this);

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    // Initialize model
                    modelBefore = resetModel(modelBefore, true);

                    modelBefore.addModel(this.elementTagStructureHandler.insertBeforeModelValue);

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // We will only be resetting the queue if we had set it to be executed before delegating, as in that
                    // case adding our new model to the beginning of what already is in the queue would make no sense
                    // Initialize model
                    if (modelAfter == null) {
                        modelAfter = resetModel(modelAfter, true);
                    }

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    modelAfterProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    modelAfter.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    modelAfter.add(new Text(this.elementTagStructureHandler.replaceWithTextValue));

                    discardEvent = true;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    modelAfter.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    discardEvent = true;

                } else if (this.elementTagStructureHandler.removeElement) {

                    // Initialize model (if it already exists)
                    modelAfter = resetModel(modelAfter, false);

                    discardEvent = true;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    discardEvent = true;

                }
                // No way to process 'removeBody' or 'removeAllButFirstChild' on a standalone tag

            } else if (processor instanceof IElementModelProcessor) {

                /*
                 * This is an Element Model processor, which means that before executing we might need to gather
                 * all the model that is inside the element (including the element's events themselves) and then,
                 * once all model has been gathered, call the processor. Note this process is quite similar to
                 * that of iteration.
                 *
                 * In order to know whether we need to start the model gathering process, or if just finished it
                 * and we need to actually execute the processor, we will ask the elementProcessorIterator to know
                 * if this is the first or the second time we execute this processor.
                 */

                if (!processorIterator.lastWasRepeated()){

                    if ((modelBefore != null && modelBefore.size() > 0) || (modelAfter != null && modelAfter.size() > 0)) {
                        throw new TemplateProcessingException(
                                "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                "of the target element has already been modified by a previously executed processor " +
                                "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                                standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());
                    }

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    processorIterator.setLastToBeRepeated(standaloneElementTag);

                    // Initialize the synthetic model
                    this.eventModelController.startGatheringDelayedModel(
                            standaloneElementTag,
                            processorIterator,
                            modelBefore, modelAfter, modelAfterProcessable,
                            discardEvent, skipBody, skipCloseTag);
                    final ISyntheticModel newSyntheticModel = this.eventModelController.getGatheredModel();
                    this.eventModelController.resetGathering();

                    // Note we DO NOT DECREASE THE MODEL LEVEL -- that will be done when we re-execute this after gathering model
                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be done when we re-execute this after gathering model
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- that's the responsibility of the close event

                    // Process the new synthetic model (no need to wait for a "close" event, as this is a standalone)
                    newSyntheticModel.process(this);

                    // Nothing else to be done by this handler... let's just queue the rest of the events in this element
                    return;

                }

                /*
                 * This is not the first time we try to execute this processor, which means the model gathering
                 * process has already taken place.
                 */

                // We will use the model buffer in order to save in number of Model objects created. This is safe
                // because we will only be calling one of these processors at a time, and the model contents will
                // be cloned after execution in order to insert them into the queue.
                //
                // NOTE we are not cloning the events themselves here. There should be no need, as we are going to
                //      re-locate these events into a new queue, and their old position (which will be executed
                //      anyway) will be ignored.
                final Model processedModel = new Model(executedSyntheticModel.getInnerModel());

                ((IElementModelProcessor) processor).process(this.context, processedModel, this.elementModelStructureHandler);

                if (this.elementModelStructureHandler.setLocalVariable) {
                    if (this.engineContext != null) {
                        this.engineContext.setVariables(this.elementModelStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementModelStructureHandler.removeLocalVariable) {
                    if (this.engineContext != null) {
                        for (final String variableName : this.elementModelStructureHandler.removedLocalVariableNames) {
                            this.engineContext.removeVariable(variableName);
                        }
                    }
                }

                if (this.elementModelStructureHandler.setSelectionTarget) {
                    if (this.engineContext != null) {
                        this.engineContext.setSelectionTarget(this.elementModelStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementModelStructureHandler.setInliner) {
                    if (this.engineContext != null) {
                        this.engineContext.setInliner(this.elementModelStructureHandler.setInlinerValue);
                    }
                }

                if (this.elementModelStructureHandler.setTemplateData) {
                    if (this.engineContext != null) {
                        this.engineContext.setTemplateData(this.elementModelStructureHandler.setTemplateDataValue);
                    }
                }

                // Reset the skipbody flags so that this model can be executed in the same conditions as the original
                executedSyntheticModel.resetGatheredSkipFlags();

                // Initialize model
                modelAfter = resetModel(modelAfter, true);

                modelAfterProcessable = true; // We actually NEED TO process this queue

                // Set the model to be executed
                modelAfter.addModel(processedModel);

                discardEvent = true;

            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE QUEUE BEFORE DELEGATING, if specified to do so
         */
        if (modelBefore != null) {
            modelBefore.process(this.next); // This is never processable
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (modelAfter != null) {
            modelAfter.process(modelAfterProcessable ? this : this.next);
        }



        /*
         * DECREASE THE CONTEXT LEVEL once we have executed all the processors (and maybe a body if we added
         * one to the tag converting it into an open tag)
         */
        if (this.engineContext != null) {
            this.engineContext.decreaseLevel();
        }

    }








    @Override
    public void handleOpenElement(final IOpenElementTag iopenElementTag) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessOpenElement(iopenElementTag)) {
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        OpenElementTag openElementTag = OpenElementTag.asEngineOpenElementTag(iopenElementTag);


        /*
         * SAVE AND RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         * Note we will only be interested on it if it is whitespace, in order to add it to iteration queues, so
         * that iterated model looks better (by including the last whitespace before the iterated element)
         * Also, note we do not mind the fact that IText events are reusable buffers and might have changed, because
         * if there is a this.lastTextEvent != null, it means it was the last event and therefore cannot have been
         * reused so far
         */
        final IText lastText = this.lastTextEvent;
        this.lastTextEvent = null;


        /*
         * OBTAIN THE GATHERED MODEL
         */
        final ISyntheticModel executedSyntheticModel;
        if (this.engineContext != null && this.engineContext.containsVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME)) {
            executedSyntheticModel = (ISyntheticModel) this.engineContext.getVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME);
            this.engineContext.removeVariable(SYNTHETIC_MODEL_CONTEXT_VARIABLE_NAME);
        } else {
            executedSyntheticModel = null;
        }


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (executedSyntheticModel == null && !openElementTag.hasAssociatedProcessors()) {
            this.next.handleOpenElement(openElementTag);
            return;
        }


        /*
         * DECLARE THE FLAGS AND STATE VARS NEEDED FOR PROCESSOR EXECUTION
         */
        final ElementProcessorIterator processorIterator;
        Model modelBefore = null;
        Model modelAfter = null;
        boolean modelAfterProcessable = false;
        boolean discardEvent = false;
        SkipBody skipBody = SkipBody.PROCESS;
        boolean skipCloseTag = false;


        /*
         * INITIALIZE THE FLAGS IN CASE THE EXECUTION WAS SUSPENDED (while a gathering took place)
         */
        if (executedSyntheticModel == null) {
            processorIterator = new ElementProcessorIterator();
        } else {
            processorIterator = executedSyntheticModel.getSuspendedProcessorIterator();
            modelBefore = executedSyntheticModel.getSuspendedModelBefore();
            modelAfter = executedSyntheticModel.getSuspendedModelAfter();
            modelAfterProcessable = executedSyntheticModel.isSuspendedModelAfterProcessable();
            discardEvent = executedSyntheticModel.isSuspendedDiscardEvent();
            skipBody = executedSyntheticModel.getSuspendedSkipBody();
            skipCloseTag = executedSyntheticModel.isSuspendedSkipCloseTag();
        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!discardEvent && (processor = processorIterator.next(openElementTag)) != null) {

            this.elementTagStructureHandler.reset();
            this.elementModelStructureHandler.reset();

            if (processor instanceof IElementTagProcessor) {

                final IElementTagProcessor elementProcessor = ((IElementTagProcessor)processor);
                elementProcessor.process(this.context, openElementTag, this.elementTagStructureHandler);

                if (this.engineContext != null) {
                    this.elementTagStructureHandler.applyContextModifications(this.engineContext);
                }

                openElementTag =
                        this.elementTagStructureHandler.applyAttributes(this.attributeDefinitions, openElementTag);

                if (this.elementTagStructureHandler.iterateElement) {

                    // If there is a preceding whitespace, add it to the iteration spec
                    Text precedingWhitespace = null;
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                                    (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(openElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(lastText);
                        if (lastEngineText.isWhitespace()) {
                            precedingWhitespace = lastEngineText;
                        }
                    }

                    // Initialize the synthetic model
                    this.eventModelController.startGatheringIteratedModel(
                            openElementTag,
                            processorIterator,
                            modelBefore, modelAfter, modelAfterProcessable,
                            discardEvent, skipBody, skipCloseTag,
                            this.elementTagStructureHandler.iterVariableName,
                            this.elementTagStructureHandler.iterStatusVariableName,
                            this.elementTagStructureHandler.iteratedObject,
                            precedingWhitespace);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // Add the new Text to the queue
                    modelAfter.add(new Text(this.elementTagStructureHandler.setBodyTextValue));

                    skipBody = SkipBody.SKIP_ALL;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Model will be automatically cloned if mutable
                    modelAfter.addModel(this.elementTagStructureHandler.setBodyModelValue);

                    skipBody = SkipBody.SKIP_ALL;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    // Initialize model
                    modelBefore = resetModel(modelBefore, true);

                    modelBefore.addModel(this.elementTagStructureHandler.insertBeforeModelValue);

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // We will only be resetting the queue if we had set it to be executed before delegating, as in that
                    // case adding our new model to the beginning of what already is in the queue would make no sense
                    // Initialize model
                    if (modelAfter == null) {
                        modelAfter = resetModel(modelAfter, true);
                    }

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    modelAfterProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    modelAfter.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    modelAfter.add(new Text(this.elementTagStructureHandler.replaceWithTextValue));

                    discardEvent = true;
                    skipBody = SkipBody.SKIP_ALL;
                    skipCloseTag = true;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    // Initialize model
                    modelAfter = resetModel(modelAfter, true);

                    modelAfterProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    modelAfter.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    discardEvent = true;
                    skipBody = SkipBody.SKIP_ALL;
                    skipCloseTag = true;

                } else if (this.elementTagStructureHandler.removeElement) {

                    // Initialize model (if it already exists)
                    modelAfter = resetModel(modelAfter, false);

                    discardEvent = true;
                    skipBody = SkipBody.SKIP_ALL;
                    skipCloseTag = true;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    discardEvent = true;
                    skipCloseTag = true;

                } else if (this.elementTagStructureHandler.removeBody) {

                    // Initialize model (if it already exists)
                    modelAfter = resetModel(modelAfter, false);

                    skipBody = SkipBody.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeAllButFirstChild) {

                    // Initialize model (if it already exists)
                    modelAfter = resetModel(modelAfter, false);

                    skipBody = SkipBody.PROCESS_ONE_ELEMENT;

                }

            } else if (processor instanceof IElementModelProcessor) {

                /*
                 * This is an Element Model processor, which means that before executing we might need to gather
                 * all the model that is inside the element (including the element's events themselves) and then,
                 * once all model has been gathered, call the processor. Note this process is quite similar to
                 * that of iteration.
                 *
                 * In order to know whether we need to start the model gathering process, or if just finished it
                 * and we need to actually execute the processor, we will ask the elementProcessorIterator to know
                 * if this is the first or the second time we execute this processor.
                 */

                if (!processorIterator.lastWasRepeated()){

                    if ((modelBefore != null && modelBefore.size() > 0) || (modelAfter != null && modelAfter.size() > 0)) {
                        throw new TemplateProcessingException(
                                "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                "of the target element has already been modified by a previously executed processor " +
                                "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                                openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());
                    }

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    processorIterator.setLastToBeRepeated(openElementTag);

                    // Initialize the synthetic model
                    this.eventModelController.startGatheringDelayedModel(
                            openElementTag,
                            processorIterator,
                            modelBefore, modelAfter, modelAfterProcessable,
                            discardEvent, skipBody, skipCloseTag);

                    // Note we DO NOT DECREASE THE MODEL LEVEL -- that will be done when we re-execute this after gathering model
                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be done when we re-execute this after gathering model
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- that's the responsibility of the close event

                    // Nothing else to be done by this handler... let's just queue the rest of the events in this element
                    return;

                }

                /*
                 * This is not the first time we try to execute this processor, which means the model gathering
                 * process has already taken place.
                 */

                // We will use the model buffer in order to save in number of Model objects created. This is safe
                // because we will only be calling one of these processors at a time, and the model contents will
                // be cloned after execution in order to insert them into the queue.
                //
                // NOTE we are not cloning the events themselves here. There should be no need, as we are going to
                //      re-locate these events into a new queue, and their old position (which will be executed
                //      anyway) will be ignored.
                final Model processedModel = new Model(executedSyntheticModel.getInnerModel());

                ((IElementModelProcessor) processor).process(this.context, processedModel, this.elementModelStructureHandler);

                if (this.elementModelStructureHandler.setLocalVariable) {
                    if (this.engineContext != null) {
                        this.engineContext.setVariables(this.elementModelStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementModelStructureHandler.removeLocalVariable) {
                    if (this.engineContext != null) {
                        for (final String variableName : this.elementModelStructureHandler.removedLocalVariableNames) {
                            this.engineContext.removeVariable(variableName);
                        }
                    }
                }

                if (this.elementModelStructureHandler.setSelectionTarget) {
                    if (this.engineContext != null) {
                        this.engineContext.setSelectionTarget(this.elementModelStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementModelStructureHandler.setInliner) {
                    if (this.engineContext != null) {
                        this.engineContext.setInliner(this.elementModelStructureHandler.setInlinerValue);
                    }
                }

                if (this.elementModelStructureHandler.setTemplateData) {
                    if (this.engineContext != null) {
                        this.engineContext.setTemplateData(this.elementModelStructureHandler.setTemplateDataValue);
                    }
                }

                /*
                 * Now we will do the exact equivalent to what is performed for an Element Tag processor, when this
                 * returns a result of type "replaceWithModel".
                 */

                // Reset the skipbody flags so that this model can be executed in the same conditions as the original
                executedSyntheticModel.resetGatheredSkipFlags();

                // Initialize model
                modelAfter = resetModel(modelAfter, true);

                modelAfterProcessable = true; // We actually NEED TO process this queue

                modelAfter.addModel(processedModel);

                discardEvent = true;
                skipBody = SkipBody.SKIP_ALL;
                skipCloseTag = true;

            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE QUEUE BEFORE DELEGATING, if specified to do so
         */
        if (modelBefore != null) {
            modelBefore.process(this.next); // This is never processable
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MODEL LEVEL RIGHT AFTERWARDS
         */
        if (!discardEvent) {
            this.next.handleOpenElement(openElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events. Note executing the queue after increasing the model
         * level makes sense even if what the queue contains is a replacement for the complete element (including open
         * and close tags), because that way whatever comes in the queue will be encapsulated in a different model level
         * and its internal open/close tags should not affect the correct delimitation of this block.
         */
        if (modelAfter != null) {
            modelAfter.process(modelAfterProcessable ? this : this.next);
        }


        /*
         * SET BODY TO BE SKIPPED, if required. Importantly, this has to be done AFTER executing the queue
         */
        this.eventModelController.skip(skipBody, skipCloseTag);

    }








    @Override
    public void handleCloseElement(final ICloseElementTag icloseElementTag) {

        /*
         * FIRST OF ALL CHECK IF IT IS AN UNMATCHED CLOSE EVENT, and in such case process in a much simpler way
         */
        if (icloseElementTag.isUnmatched()) {
            handleUnmatchedCloseElement(icloseElementTag);
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessCloseElement(icloseElementTag)) {

            /*
             * IF WE JUST ENDED GATHERING A SYNTHETIC MODEL, PROCESS IT
             */
            if (this.eventModelController.isGatheringFinished()) {
                final ISyntheticModel syntheticModel = this.eventModelController.getGatheredModel();
                this.eventModelController.resetGathering();
                syntheticModel.process(this);
            }

            return;

        }


        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * CALL THE NEXT HANDLER in the chain
         */
        this.next.handleCloseElement(icloseElementTag);

    }








    private void handleUnmatchedCloseElement(final ICloseElementTag icloseElementTag) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessUnmatchedCloseElement(icloseElementTag)) {
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * -------------------------------------------------------------------------------------------------
         * THERE IS NOTHING ELSE THAT SHOULD BE DONE WITH AN UNMATCHED CLOSE ELEMENT. No processors apply...
         * -------------------------------------------------------------------------------------------------
         */


        /*
         * CALL THE NEXT HANDLER in the chain
         */
        this.next.handleCloseElement(icloseElementTag);

    }








    @Override
    public void handleDocType(final IDocType idocType) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessDocType(idocType)) {
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.docTypeProcessors.length == 0) {
            this.next.handleDocType(idocType);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        DocType docType = DocType.asEngineDocType(idocType);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.docTypeProcessors.length; i++) {

            this.docTypeStructureHandler.reset();

            this.docTypeProcessors[i].process(this.context, docType, this.docTypeStructureHandler);

            if (this.docTypeStructureHandler.setDocType) {

                docType =
                        new DocType(
                            this.docTypeStructureHandler.setDocTypeKeyword,
                            this.docTypeStructureHandler.setDocTypeElementName,
                            this.docTypeStructureHandler.setDocTypePublicId,
                            this.docTypeStructureHandler.setDocTypeSystemId,
                            this.docTypeStructureHandler.setDocTypeInternalSubset);

            } else if (this.docTypeStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.docTypeStructureHandler.replaceWithModelValue);
                modelHandler = this.docTypeStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.docTypeStructureHandler.removeDocType) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleDocType(docType);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }





    
    
    
    @Override
    public void handleXMLDeclaration(final IXMLDeclaration ixmlDeclaration) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessXMLDeclaration(ixmlDeclaration)) {
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.xmlDeclarationProcessors.length == 0) {
            this.next.handleXMLDeclaration(ixmlDeclaration);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        XMLDeclaration xmlDeclaration = XMLDeclaration.asEngineXMLDeclaration(ixmlDeclaration);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.xmlDeclarationProcessors.length; i++) {

            this.xmlDeclarationStructureHandler.reset();

            this.xmlDeclarationProcessors[i].process(this.context, xmlDeclaration, this.xmlDeclarationStructureHandler);

            if (this.xmlDeclarationStructureHandler.setXMLDeclaration) {

                xmlDeclaration =
                        new XMLDeclaration(
                                this.xmlDeclarationStructureHandler.setXMLDeclarationKeyword,
                                this.xmlDeclarationStructureHandler.setXMLDeclarationVersion,
                                this.xmlDeclarationStructureHandler.setXMLDeclarationEncoding,
                                this.xmlDeclarationStructureHandler.setXMLDeclarationStandalone);

            } else if (this.xmlDeclarationStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.xmlDeclarationStructureHandler.replaceWithModelValue);
                modelHandler = this.xmlDeclarationStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.xmlDeclarationStructureHandler.removeXMLDeclaration) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleXMLDeclaration(xmlDeclaration);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }








    @Override
    public void handleProcessingInstruction(final IProcessingInstruction iprocessingInstruction) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (!this.eventModelController.shouldProcessProcessingInstruction(iprocessingInstruction)) {
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (this.processingInstructionProcessors.length == 0) {
            this.next.handleProcessingInstruction(iprocessingInstruction);
            return;
        }


        /*
         * CAST EVENT TO ENGINE-SPECIFIC IMPLEMENTATION
         */
        ProcessingInstruction processingInstruction = ProcessingInstruction.asEngineProcessingInstruction(iprocessingInstruction);


        /*
         * DECLARE VARIABLES THAT MIGHT BE NEEDED FOR TAKING ACTIONS INSTRUCTED BY THE PROCESSORS
         */
        boolean discardEvent = false;
        Model model = null;
        ITemplateHandler modelHandler = this;


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !discardEvent && i < this.processingInstructionProcessors.length; i++) {

            this.processingInstructionStructureHandler.reset();

            this.processingInstructionProcessors[i].process(this.context, processingInstruction, this.processingInstructionStructureHandler);

            if (this.processingInstructionStructureHandler.setProcessingInstruction) {

                processingInstruction =
                        new ProcessingInstruction(
                                this.processingInstructionStructureHandler.setProcessingInstructionTarget,
                                this.processingInstructionStructureHandler.setProcessingInstructionContent);

            } else if (this.processingInstructionStructureHandler.replaceWithModel) {

                model = initializeModel(model);
                model.addModel(this.processingInstructionStructureHandler.replaceWithModelValue);
                modelHandler = this.processingInstructionStructureHandler.replaceWithModelProcessable? this : this.next;
                discardEvent = true;

            } else if (this.processingInstructionStructureHandler.removeProcessingInstruction) {

                model = null;
                discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!discardEvent) {
            this.next.handleProcessingInstruction(processingInstruction);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (model != null) {
            model.process(modelHandler);
        }

    }





    private Model resetModel(final Model model, final boolean createIfNull) {
        if (model == null) {
            if (createIfNull) {
                return new Model(this.configuration, this.templateMode);
            }
            return model;
        }
        model.reset();
        return model;
    }



}