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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateEvent;
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
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler implements ITemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorTemplateHandler.class);

    private static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";

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


    private static final ITemplateBoundariesProcessor[] EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS = new ITemplateBoundariesProcessor[0];
    private static final ICDATASectionProcessor[] EMPTY_CDATA_SECTION_PROCESSORS = new ICDATASectionProcessor[0];
    private static final ICommentProcessor[] EMPTY_COMMENT_PROCESSORS = new ICommentProcessor[0];
    private static final IDocTypeProcessor[] EMPTY_DOCTYPE_PROCESSORS = new IDocTypeProcessor[0];
    private static final IProcessingInstructionProcessor[] EMPTY_PROCESSING_INSTRUCTION_PROCESSORS = new IProcessingInstructionProcessor[0];
    private static final ITextProcessor[] EMPTY_TEXT_PROCESSORS = new ITextProcessor[0];
    private static final IXMLDeclarationProcessor[] EMPTY_XML_DECLARATION_PROCESSORS = new IXMLDeclarationProcessor[0];


    private static enum BodyBehaviour { PROCESS, SKIP_ELEMENTS, SKIP_ELEMENTS_BUT_FIRST, SKIP_ALL}


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

    // Declare the structure that will hold the processing data/flags needed to be indexed by the
    // model level (the hierarchy of the markup)
    private ModelLevelData[] modelLevelData;
    private int modelLevel;


    // Declare the structure that will hold the processing data/flags needed to be indexed by the
    // handler execution level (i.e. levels of nesting in handler method execution)
    private ExecLevelData[] execLevelData;
    private int execLevel;


    // This variable will contain the last event that has been processed, if this last event was an IText. Its aim
    // is to allow the inclusion of preceding whitespace in the iteration of block elements (such as <tr>, <li>, etc.)
    // so that resulting markup is more readable than the alternative "</tr><tr ...>"
    // Note also that, given whitespace between tags is not significative in XML, this mechanism will be applied in XML
    // template mode disregarding the name of the element.
    private IText lastTextEvent = null;

    // The gatherer will be in charge of determining when an event should be gathered (into a Model) instead of processed
    private ProcessorTemplateHandlerModelGatherer gatherer = null;




    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {

        super();


        this.modelLevelData = new ModelLevelData[10];
        Arrays.fill(this.modelLevelData, null);
        this.modelLevel = -1;

        this.execLevelData = new ExecLevelData[5];
        Arrays.fill(this.execLevelData, null);
        this.execLevel = -1;

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
        this.gatherer = new ProcessorTemplateHandlerModelGatherer(this.configuration, this.templateMode);

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







    private void increaseModelLevel() {

        this.modelLevel++;

        if (this.modelLevel == this.modelLevelData.length) {
            this.modelLevelData = Arrays.copyOf(this.modelLevelData, this.modelLevelData.length + 10);
        }

        if (this.modelLevelData[this.modelLevel] == null) {
            this.modelLevelData[this.modelLevel] = new ModelLevelData();
        } else {
            this.modelLevelData[this.modelLevel].reset();
        }

    }


    private void decreaseModelLevel() {
        this.modelLevel--;
    }







    private void increaseExecLevel() {

        this.execLevel++;

        if (this.execLevel == this.execLevelData.length) {
            this.execLevelData = Arrays.copyOf(this.execLevelData, this.execLevelData.length + 3);
        }

        if (this.execLevelData[this.execLevel] == null) {
            this.execLevelData[this.execLevel] = new ExecLevelData(this.configuration, this.templateMode);
        } else {
            this.execLevelData[this.execLevel].reset();
        }

    }


    private void decreaseExecLevel() {
        this.execLevel--;
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
         *  INCREASE THE MODEL LEVEL. Markup parsing is starting, and we need to set it to 0.
         *  Doing this here instead of, for example, at the ProcessorTemplateHandler constructor is a need
         *  because the structures created here might need things like the IEngineConfiguration object, which
         *  we do not have at constructor time.
         */
        increaseModelLevel();

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
            decreaseModelLevel(); // Decrease the model level increased during template start (should be now: -1)
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
         * DECREASE THE MODEL LEVEL
         */
        decreaseModelLevel();


        /*
         * LAST ROUND OF CHECKS. If we have not returned our indexes to -1, something has gone wrong during processing
         */
        if (this.execLevel >= 0) {
            throw new TemplateProcessingException(
                    "Bad markup or template processing sequence. Execution level is >= 0 (" + this.execLevel + ") " +
                    "at template end.", itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
        }
        if (this.modelLevel >= 0) {
            throw new TemplateProcessingException(
                    "Bad markup or template processing sequence. Model level is >= 0 (" + this.modelLevel + ") " +
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
        if (this.gatherer.gatherText(itext)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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
        if (this.gatherer.gatherComment(icomment)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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
        if (this.gatherer.gatherCDATASection(icdataSection)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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
        if (this.gatherer.gatherStandaloneElement(istandaloneElementTag)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL ||
                this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS) {
            return;
        } else if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS_BUT_FIRST) {
            this.modelLevelData[this.modelLevel].bodyBehaviour = BodyBehaviour.SKIP_ELEMENTS;
        }


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
         * COMPUTE WHETHER WE SHOULD CONTINUE WHERE WE SUSPENDED THE EXECUTION OF A HANDLER (and re-init flag)
         */
        final boolean wasSuspended = this.execLevel >= 0 && this.execLevelData[this.execLevel].suspended;
        if (wasSuspended) {
            this.execLevelData[this.execLevel].suspended = false;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        StandaloneElementTag standaloneElementTag = StandaloneElementTag.asEngineStandaloneElementTag(istandaloneElementTag);


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!wasSuspended && !standaloneElementTag.hasAssociatedProcessors()) {
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
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         * Note this is not done if execution was suspended, as in that case what we want to do is actually continue
         * were we left.
         */
        if (!wasSuspended) {
            increaseExecLevel();
        }
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.engineContext != null) {
            this.engineContext.increaseLevel();
        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!execLevelData.discardEvent && (processor = execLevelData.processorIterator.next(standaloneElementTag)) != null) {

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

                    // Initialize the iterated model object
                    final Model iteratedModel = new Model(this.configuration, this.templateMode);
                    iteratedModel.add(standaloneElementTag);

                    // Suspend execution - execution will be restarted by the handleOpenElement event at the
                    // processIteration() call performed after gathering all the iterated markup
                    execLevelData.suspended = true;

                    // Set the rest of the metadata needed for iteration
                    execLevelData.iterationArtifacts.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    execLevelData.iterationArtifacts.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    execLevelData.iterationArtifacts.iteratedObject = this.elementTagStructureHandler.iteratedObject;

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                             (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(standaloneElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(lastText);
                        if (lastEngineText.isWhitespace()) {
                            execLevelData.iterationArtifacts.precedingWhitespace = lastEngineText;
                        }
                    }

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Process the queue by iterating it
                    processIteration(iteratedModel);

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

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
                    execLevelData.model.add(text);
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // Suspend execution - execution will be restarted by the handleOpenElement event
                    execLevelData.suspended = true;

                    // Initialize the iterated model object
                    final Model processedModel = new Model(this.configuration, this.templateMode);
                    processedModel.add(openTag);
                    processedModel.add(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    processedModel.process(this);

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

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
                    execLevelData.model.addModel(this.elementTagStructureHandler.setBodyModelValue);
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Suspend execution - execution will be restarted by the handleOpenElement event
                    execLevelData.suspended = true;

                    // Initialize the iterated model object
                    final Model processedModel = new Model(this.configuration, this.templateMode);
                    processedModel.add(openTag);
                    processedModel.add(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    processedModel.process(this);

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.model.addModel(this.elementTagStructureHandler.insertBeforeModelValue);
                    // Model inserted BEFORE is never processable, so we will always use this.next here
                    execLevelData.queueProcessable = false;
                    // This queue should be processed BEFORE delegating the event
                    execLevelData.queueProcessBeforeDelegate = true;

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // We will only be resetting the queue if we had set it to be executed before delegating, as in that
                    // case adding our new model to the beginning of what already is in the queue would make no sense
                    if (execLevelData.queueProcessBeforeDelegate) {
                        execLevelData.resetQueue(); // Remove any previous results on the queue
                    }

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    execLevelData.queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.model.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    execLevelData.model.add(new Text(this.elementTagStructureHandler.replaceWithTextValue));

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.model.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.removeElement) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    execLevelData.discardEvent = true;

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

                if (execLevelData.model.size() > 0) {
                    throw new TemplateProcessingException(
                            "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                    "of the target element has already been modified by a previously executed processor " +
                                    "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                    "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                            standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());
                }

                // Initialize the processed model object
                final Model processedModel = new Model(this.configuration, this.templateMode);
                processedModel.add(standaloneElementTag);

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

                execLevelData.resetQueue(); // Remove any previous results on the queue
                execLevelData.queueProcessable = true; // We actually NEED TO process this queue

                // Set the model to be executed
                execLevelData.model.addModel(processedModel);

                execLevelData.discardEvent = true;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE QUEUE BEFORE DELEGATING, if specified to do so
         */
        if (execLevelData.queueProcessBeforeDelegate) {
            execLevelData.model.process(execLevelData.queueProcessable ? this : this.next);
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            this.next.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        if (!execLevelData.queueProcessBeforeDelegate) {
            execLevelData.model.process(execLevelData.queueProcessable ? this : this.next);
        }



        /*
         * DECREASE THE CONTEXT LEVEL once we have executed all the processors (and maybe a body if we added
         * one to the tag converting it into an open tag)
         */
        if (this.engineContext != null) {
            this.engineContext.decreaseLevel();
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

    }








    @Override
    public void handleOpenElement(final IOpenElementTag iopenElementTag) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (this.gatherer.gatherOpenElement(iopenElementTag)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL ||
                this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS) {
            increaseModelLevel();
            this.modelLevelData[this.modelLevel].bodyBehaviour = BodyBehaviour.SKIP_ALL; // Skip everything inside
            return;
        }
        // Note the structure doesn't end here, so even if bodyBehaviour is set to ONLY_FIRST_ELEMENT we won't
        // set it to SKIP_ALL at this model level until the close tag.


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
         * COMPUTE WHETHER WE SHOULD CONTINUE WHERE WE SUSPENDED THE EXECUTION OF A HANDLER (and re-init flag)
         */
        final boolean wasSuspended = this.execLevel >= 0 && this.execLevelData[this.execLevel].suspended;
        if (wasSuspended) {
            this.execLevelData[this.execLevel].suspended = false;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        OpenElementTag openElementTag = OpenElementTag.asEngineOpenElementTag(iopenElementTag);


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!wasSuspended && !openElementTag.hasAssociatedProcessors()) {
            if (this.engineContext != null) {
                this.engineContext.increaseLevel();
            }
            this.next.handleOpenElement(openElementTag);
            increaseModelLevel();
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         * Note this is not done if execution was suspended, as in that case what we want to do is actually continue
         * were we left.
         */
        if (!wasSuspended) {
            increaseExecLevel();
        }
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.engineContext != null) {
            this.engineContext.increaseLevel();
        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!execLevelData.discardEvent && (processor = execLevelData.processorIterator.next(openElementTag)) != null) {

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

                    // Initialize the gatherer object
                    this.gatherer.init(ProcessorTemplateHandlerModelGatherer.GatheringType.ITERATION);
                    this.gatherer.gatherOpenElement(openElementTag);

                    // Suspend execution - execution will be restarted by the handleOpenElement event at the
                    // processIteration() call performed after gathering all the iterated markup
                    execLevelData.suspended = true;

                    // Set the rest of the metadata needed for iteration
                    execLevelData.iterationArtifacts.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    execLevelData.iterationArtifacts.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    execLevelData.iterationArtifacts.iteratedObject = this.elementTagStructureHandler.iteratedObject;

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                                    (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(openElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(lastText);
                        if (lastEngineText.isWhitespace()) {
                            execLevelData.iterationArtifacts.precedingWhitespace = lastEngineText;
                        }
                    }

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // Add the new Text to the queue
                    execLevelData.model.add(new Text(this.elementTagStructureHandler.setBodyTextValue));

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.model.addModel(this.elementTagStructureHandler.setBodyModelValue);

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.model.addModel(this.elementTagStructureHandler.insertBeforeModelValue);
                    // Model inserted BEFORE is never processable, so we will always use this.next here
                    execLevelData.queueProcessable = false;
                    // This queue should be processed BEFORE delegating the event
                    execLevelData.queueProcessBeforeDelegate = true;

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // We will only be resetting the queue if we had set it to be executed before delegating, as in that
                    // case adding our new model to the beginning of what already is in the queue would make no sense
                    if (execLevelData.queueProcessBeforeDelegate) {
                        execLevelData.resetQueue(); // Remove any previous results on the queue
                    }

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    execLevelData.queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.model.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    execLevelData.model.add(new Text(this.elementTagStructureHandler.replaceWithTextValue));

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.model.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeElement) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.removeBody) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeAllButFirstChild) {

                    execLevelData.resetQueue(); // Remove any previous results on the queue

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ELEMENTS_BUT_FIRST;

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

                if (!execLevelData.processorIterator.lastWasRepeated()){

                    if (execLevelData.model.size() > 0) {
                        throw new TemplateProcessingException(
                                "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                "of the target element has already been modified by a previously executed processor " +
                                "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                                openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());
                    }

                    // Initialize the gatherer object
                    this.gatherer.init(ProcessorTemplateHandlerModelGatherer.GatheringType.MODEL);
                    this.gatherer.gatherOpenElement(openElementTag);

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    execLevelData.processorIterator.setLastToBeRepeated(openElementTag);

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    execLevelData.suspended = true;

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
                final Model processedModel = new Model(this.gatherer.getModel());

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

                execLevelData.resetQueue(); // Remove any previous results on the queue
                execLevelData.queueProcessable = true; // We actually NEED TO process this queue

                // Model will be automatically cloned if mutable
                execLevelData.model.addModel(processedModel);

                execLevelData.discardEvent = true;
                execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE QUEUE BEFORE DELEGATING, if specified to do so
         */
        if (execLevelData.queueProcessBeforeDelegate) {
            execLevelData.model.process(execLevelData.queueProcessable ? this : this.next);
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MODEL LEVEL RIGHT AFTERWARDS
         */
        if (!execLevelData.discardEvent) {
            this.next.handleOpenElement(openElementTag);
        }


        /*
         * INCREASE THE MODEL LEVEL to the value that will be applied to the tag's bodies. Note we will do even
         * if during the execution of processors this open tag has been replaced by something else, because
         * we will still be processing the body of the open tag when it still was an open tag.
         */
        increaseModelLevel();


        /*
         * PROCESS THE QUEUE, launching all the queued events. Note executing the queue after increasing the model
         * level makes sense even if what the queue contains is a replacement for the complete element (including open
         * and close tags), because that way whatever comes in the queue will be encapsulated in a different model level
         * and its internal open/close tags should not affect the correct delimitation of this block.
         */
        if (!execLevelData.queueProcessBeforeDelegate) {
            execLevelData.model.process(execLevelData.queueProcessable ? this : this.next);
        }


        /*
         * SET BODY TO BE SKIPPED, if required. Importantly, this has to be done AFTER executing the queue
         */
        this.modelLevelData[this.modelLevel].bodyBehaviour = execLevelData.bodyBehaviour;


        /*
         * MAKE SURE WE SKIP_ALL THE CORRESPONDING CLOSE TAG, if required
         */
        if (execLevelData.discardEvent) {
            this.modelLevelData[this.modelLevel - 1].skipCloseTag = true;
            // We cannot decrease here the context level because we aren't actually decreasing the model
            // level until we find the corresponding close tag
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

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
        if (this.gatherer.gatherCloseElement(icloseElementTag)) {

            // We will only return if this close element tag didn't actually end the gathering operation
            if (!this.gatherer.isFinished()) {
                return;
            }

            // Obtain the gathered model and discard the gatherer (a nested processing might set a new one)
            final Model gatheredModel = this.gatherer.getModel();
            final ProcessorTemplateHandlerModelGatherer.GatheringType gatheringType = this.gatherer.getGatheringType();
            this.gatherer.reset();

            // Process the queue
            switch (gatheringType) {
                case ITERATION: processIteration(gatheredModel); break;
                case MODEL: gatheredModel.process(this); break;
                default: throw new TemplateProcessingException("Unknown gathering type: " + gatheringType);
            }

            // Decrease the context level
            if (this.engineContext != null) {
                this.engineContext.decreaseLevel();
            }

            return;

        }


        /*
         * DECREASE THE MODEL LEVEL, as only the body of elements should be considered in a higher level
         */
        decreaseModelLevel();

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL ||
                this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS) {
            return;
        } else if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS_BUT_FIRST) {
            this.modelLevelData[this.modelLevel].bodyBehaviour = BodyBehaviour.SKIP_ELEMENTS;
        }

        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;

        /*
         * DECREASE THE CONTEXT LEVEL, once we know this tag was not part of a block of discarded model
         */
        if (this.engineContext != null) {
            this.engineContext.decreaseLevel();
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.modelLevelData[this.modelLevel].skipCloseTag) {
            this.modelLevelData[this.modelLevel].skipCloseTag = false;
            return;
        }

        /*
         * CALL THE NEXT HANDLER in the chain
         */
        this.next.handleCloseElement(icloseElementTag);

    }








    private void handleUnmatchedCloseElement(final ICloseElementTag icloseElementTag) {

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL
         */
        if (this.gatherer.gatherUnmatchedCloseElement(icloseElementTag)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) { // an unmatched is not really an element
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
        if (this.gatherer.gatherDocType(idocType)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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
        if (this.gatherer.gatherXMLDeclaration(ixmlDeclaration)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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
        if (this.gatherer.gatherProcessingInstruction(iprocessingInstruction)) {
            return;
        }


        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
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






    private void processIteration(final Model model) {

        if (this.engineContext == null) {
            throw new TemplateProcessingException(
                    "Iteration is not supported because local variable support is DISABLED. This is due to " +
                    "the use of an implementation of the " + ITemplateContext.class.getName() + " interface that does " +
                    "not provide local-variable support. In order to have local-variable support, the context " +
                    "implementation should also implement the " + IEngineContext.class.getName() +
                    " interface");
        }


        /*
         * We will use to execution levels here: an "outer" one containing the original artifacts (esp. the
         * originally gathered queue) and an "inner" one which will be the one on which the real gathered
         * events will execute when their execution is resumed.
         */
        final ExecLevelData outerExecLevelData = this.execLevelData[this.execLevel];


        /*
         * Make sure there is a name for the ITERATION STATUS VARIABLE
         */
        if (StringUtils.isEmptyOrWhitespace(outerExecLevelData.iterationArtifacts.iterStatusVariableName)) {
            // If no name has been specified for the status variable, we will use the same as the iter var + "Stat"
            outerExecLevelData.iterationArtifacts.iterStatusVariableName =
                    outerExecLevelData.iterationArtifacts.iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        }


        /*
         * This will compute whether transformations on the first/last body events need to be performed in TEXT mode
         * (e.g. if we need to remove some whitespace for the results to look nice)
         *
         * Note the gathered queue will not be modified here.
         */
        if (this.templateMode.isText()) {
            preparePrettificationOfTextIteration(this.configuration, model, outerExecLevelData.iterationArtifacts);
        }


        /*
         * Depending on the class of the iterated object, we will iterate it in one way or another. And also we
         * might have a "size" value for the stat variable or not.
         */
        final Iterator<?> iterator = computeIteratedObjectIterator(outerExecLevelData.iterationArtifacts.iteratedObject);

        final IterationStatusVar status = new IterationStatusVar();
        status.index = 0;
        status.size = computeIteratedObjectSize(outerExecLevelData.iterationArtifacts.iteratedObject);


        /*
         * ---------------------------------
         * START OF ITERATION
         * ---------------------------------
         */
        boolean iterHasNext = iterator.hasNext();
        while (iterHasNext) {

            /*
             * Extract and precompute flags for the object being processed in this iteration
             */
            status.current = iterator.next();
            iterHasNext = iterator.hasNext(); // precomputed in order to know when we are at the last element


            /*
             * We will increase the execution level as a way of "protecting" the gathered queue we are about to
             * execute. We increase the exec level twice because we will leave the current level as a "base
             * repository" of the state of things before suspending, current + 1 will be left empty in order to be
             * used as a "clean base of execution" for all the events contained in the gathered queue, and current + 2
             * will be initialized as a clone of the current level, so that it can be read by the first event in
             * the gathered queue as the frozen state of things when execution was suspended.
             */
            increaseExecLevel();
            increaseExecLevel();
            final ExecLevelData innerExecLevelData = this.execLevelData[this.execLevel];


            /*
             * We need to clone the execution level. We protected the previous one by increasing the level, but now we
             * need to provide to the suspended event exactly the same environment (execLevelData) as when it was
             * suspended, except of course for the event-gathering structures, which we will not need at the new level
             */
            innerExecLevelData.resetAsCloneOf(outerExecLevelData, false);


            /*
             * Increase the engine context level, so that we can store the needed local variables there
             */
            this.engineContext.increaseLevel();


            /*
             * Set the iteration local variables (iteration variable and iteration status variable)
             */
            this.engineContext.setVariable(outerExecLevelData.iterationArtifacts.iterVariableName, status.current);
            this.engineContext.setVariable(outerExecLevelData.iterationArtifacts.iterStatusVariableName, status);


            /*
             * Adjust body behaviour for this model level
             *
             * If this iterator (e.g. th:each) lived in a tag with a "remove all but first" instruction,
             * we need to reinitialize SKIP_ELEMENTS -> SKIP_ELEMENTS_BUT_FIRST
             * The reason this is done on modelLevel and not on execLevel is because execLevel is for things
             * that have been changed by the iterated element itself before processing the iteration, and in this case
             * we are talking about an element-skipping operation performed by a higher-level tag that was
             * executed previously
             */
            if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS) {
                this.modelLevelData[this.modelLevel].bodyBehaviour = BodyBehaviour.SKIP_ELEMENTS_BUT_FIRST;
            }


            /*
             * We might need to perform some modifications to the iteration queue for this iteration
             * For example, in text modes we might modify the first whitespaces in the body of the iterated element
             *
             * IMPORTANT: This will actually modify the queue at outerExecLevelData, but it should be no problem
             * because the changes will be overwritten by the next iteration. And we will be discarding the queues
             * contents once the whole iteration process finishes
             */
            prepareIterationQueueForIteration(model, outerExecLevelData.iterationArtifacts, status.index, !iterHasNext);


            /*
             * PERFORM THE EXECUTION on the gathered queue, which now does not live at the current exec level, but
             * at the previous one (we protected it by increasing execution level before)
             */
            model.process(this);


            /*
             * Decrease the engine context level, now that this iteration has been executed and we can dispose of
             * the local variables
             */
            this.engineContext.decreaseLevel();


            /*
             * Decrease one of the execution levels. We had increased it twice before. The +2 level was decreased
             * by the first event in the queue (the originally suspended one) after execution and now we are decreasing
             * again level +1, so that we clean the level we used as a "clean execution base". We will create it
             * again for the next iteration so that we make sure there are no interactions between iterations.
             */
            decreaseExecLevel();


            /*
             * Increase the iteration counter
             */
            status.index++;

        }


        /*
         * Decrease the execution level. Note this decrease does not correspond to the increases we did some lines above
         * inside the iteration (those have already been decreased at this point). Instead, we are here decreasing
         * the execution level created for the event that started the model gathering the first time, the first to
         * suspend execution, which has been never decreased until now.
         */
        decreaseExecLevel();

    }




    /*
     * Whenever possible, compute the total size of the iterated object. Note sometimes we will not be able
     * to compute this size without traversing the entire collection/iterator (which we want to avoid), so
     * null will be returned.
     */
    private static Integer computeIteratedObjectSize(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Integer.valueOf(0);
        }
        if (iteratedObject instanceof Collection<?>) {
            return Integer.valueOf(((Collection<?>) iteratedObject).size());
        }
        if (iteratedObject instanceof Map<?,?>) {
            return Integer.valueOf(((Map<?, ?>) iteratedObject).size());
        }
        if (iteratedObject.getClass().isArray()) {
            return Integer.valueOf(Array.getLength(iteratedObject));
        }
        if (iteratedObject instanceof Iterable<?>) {
            return null; // Cannot determine before actually iterating
        }
        if (iteratedObject instanceof Iterator<?>) {
            return null; // Cannot determine before actually iterating
        }
        return Integer.valueOf(1); // In this case, we will iterate the object as a collection of size 1
    }


    /*
     * Creates, from the iterated object (e.g. right part of a th:each expression), the iterator that will be used.
     */
    private static Iterator<?> computeIteratedObjectIterator(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (iteratedObject instanceof Collection<?>) {
            return ((Collection<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Map<?,?>) {
            return ((Map<?,?>)iteratedObject).entrySet().iterator();
        }
        if (iteratedObject.getClass().isArray()) {
            return new Iterator<Object>() {

                protected final Object array = iteratedObject;
                protected final int length = Array.getLength(this.array);
                private int i = 0;

                public boolean hasNext() {
                    return this.i < this.length;
                }

                public Object next() {
                    return Array.get(this.array, i++);
                }

                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an array iterator");
                }

            };
        }
        if (iteratedObject instanceof Iterable<?>) {
            return ((Iterable<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Iterator<?>) {
            return (Iterator<?>)iteratedObject;
        }
        if (iteratedObject instanceof Enumeration<?>) {
            return new Iterator<Object>() {

                protected final Enumeration<?> enumeration = (Enumeration<?>)iteratedObject;


                public boolean hasNext() {
                    return this.enumeration.hasMoreElements();
                }

                public Object next() {
                    return this.enumeration.nextElement();
                }

                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an Enumeration iterator");
                }

            };
        }
        return Collections.singletonList(iteratedObject).iterator();
    }




    private static void preparePrettificationOfTextIteration(
            final IEngineConfiguration configuration, final Model gatheredModel, final IterationArtifacts iterArtifacts) {

        /*
         * We are in a textual template mode, and it might be possible to fiddle a bit with whitespaces at the beginning
         * and end of the body, so that iterations look better.
         *
         * The goal is that this:
         * ---------------------
         * List:
         * [# th:each="i : ${items}"]
         *   - [[${i}]]
         * [/]
         * ---------------------
         * ...doesn't look like:
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         * ---------------------
         * ...but instead like:
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *   - [[${i}]]
         *   - [[${i}]]
         * ---------------------
         * And in order to do this, the steps to be taken will be:
         *
         *   - Check that the iterated block starts with an 'open element' and ends with a 'close element'. If not,
         *     don't apply any of this.
         *   - Except for the first iteration, remove all whitespace after the 'open element', until the
         *     first '\n' (and remove that too).
         *   - Except for the last iteration, remove all whitespace after the last '\n' (not including it) and before
         *     the 'close element'.
         */

        if (gatheredModel.size() <= 2) {
            // This does only contain the template start + end events -- nothing to be done
            iterArtifacts.performBodyFirstLastSwitch = false;
            return;
        }

        int firstBodyEventCutPoint = -1;
        int lastBodyEventCutPoint = -1;

        final ITemplateEvent firstBodyEvent = gatheredModel.get(1); // we know there is at least one body event
        Text firstTextBodyEvent = null;
        if (gatheredModel.get(0) instanceof OpenElementTag && firstBodyEvent instanceof IText) {

            firstTextBodyEvent = Text.asEngineText((IText)firstBodyEvent);

            final int firstTextEventLen = firstTextBodyEvent.length();
            int i = 0;
            char c;
            while (i < firstTextEventLen && firstBodyEventCutPoint < 0) {
                c = firstTextBodyEvent.charAt(i);
                if (c == '\n') {
                    firstBodyEventCutPoint = i + 1;
                    break; // we've already assigned the value we were looking for
                } else if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                } else {
                    // We will not be able to perform any whitespace reduction here
                    break;
                }
            }

        }

        final ITemplateEvent lastBodyEvent = gatheredModel.get(gatheredModel.size() - 2);
        Text lastTextBodyEvent = null;
        if (firstBodyEventCutPoint >= 0 &&
                gatheredModel.get(gatheredModel.size() - 1) instanceof CloseElementTag && lastBodyEvent instanceof IText) {

            lastTextBodyEvent = Text.asEngineText((IText)lastBodyEvent);

            final int lastTextEventLen = lastTextBodyEvent.length();
            int i = lastTextEventLen - 1;
            char c;
            while (i >= 0 && lastBodyEventCutPoint < 0) {
                c = lastTextBodyEvent.charAt(i);
                if (c == '\n') {
                    lastBodyEventCutPoint = i + 1;
                    break; // we've already assigned the value we were looking for
                } else if (Character.isWhitespace(c)) {
                    i--;
                    continue;
                } else {
                    // We will not be able to perform any whitespace reduction here
                    break;
                }
            }

        }

        if (firstBodyEventCutPoint < 0 || lastBodyEventCutPoint < 0) {
            // We don't have the scenario required for performing the needed whitespace collapsing operation
            iterArtifacts.performBodyFirstLastSwitch = false;
            return;
        }

        // At this point, we are sure that we will want to perform modifications on the first/last whitespaces
        iterArtifacts.performBodyFirstLastSwitch = true;

        if (firstBodyEvent == lastBodyEvent) {
            // If the first and the last event are actually the same, we need to take better care of how we manage whitespace
            final CharSequence textFor0 = lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint);
            final CharSequence textForMax = firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length());
            final CharSequence textForN = firstTextBodyEvent.subSequence(firstBodyEventCutPoint, lastBodyEventCutPoint);

            iterArtifacts.iterationFirstBodyEventIter0 = new Text(textFor0);
            iterArtifacts.iterationFirstBodyEventIterN = new Text(textForN);
            iterArtifacts.iterationLastBodyEventIterMax = new Text(textForMax);
            iterArtifacts.iterationLastBodyEventIterN = new Text(textForN);
            return;
        }

        // At this point, we know the first and last body events are different objects

        iterArtifacts.iterationFirstBodyEventIter0 = firstTextBodyEvent;
        iterArtifacts.iterationLastBodyEventIterMax = lastTextBodyEvent;

        if (firstBodyEventCutPoint == 0) {
            iterArtifacts.iterationFirstBodyEventIterN = firstTextBodyEvent;
        } else {
            iterArtifacts.iterationFirstBodyEventIterN =
                    new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
        }

        if (lastBodyEventCutPoint == lastTextBodyEvent.length()) {
            iterArtifacts.iterationLastBodyEventIterN = lastTextBodyEvent;
        } else {
            iterArtifacts.iterationLastBodyEventIterN = new Text(lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
        }


    }




    /*
     * This will prepare the iteration queue, before executing it for each iteration. Basically, this will manage
     * the whitespaces that need to be applied before or after the queue in order to make the results of iteration
     * more pretty.
     */
    private static void prepareIterationQueueForIteration(
            final Model iterModel, final IterationArtifacts iterArtifacts, final int iterationIndex, final boolean last) {

        /*
         * FOR MARKUP TEMPLATE MODES: check if we have to add the initial whitespace (to iter > 0)
         */
        if (iterArtifacts.precedingWhitespace != null && iterationIndex == 1) {
            iterModel.insert(0, iterArtifacts.precedingWhitespace);
            return;
        }


        /*
         * FOR TEXT TEMPLATE MODES: fiddle with the initial and finidhing whitespaces
         */

        if (!iterArtifacts.performBodyFirstLastSwitch || (iterationIndex > 1 && !last)) {
            // No modifications to be done to the iteration queue here
            return;
        }

        if (iterationIndex == 0) {
            if (!last) {
                iterModel.replace(1, iterArtifacts.iterationFirstBodyEventIter0);
                if (iterModel.size() > 3) {
                    iterModel.replace(iterModel.size() - 2, iterArtifacts.iterationLastBodyEventIterN);
                }
            }
            return;
        }

        if (iterationIndex == 1) {
            iterModel.replace(1, iterArtifacts.iterationFirstBodyEventIterN);
        }

        if (last) {
            iterModel.replace(iterModel.size() - 2, iterArtifacts.iterationLastBodyEventIterMax);
        }


    }







    private static final class ModelLevelData {

        BodyBehaviour bodyBehaviour;
        boolean skipCloseTag;


        ModelLevelData() {
            super();
            reset();
        }

        void reset() {
            this.bodyBehaviour = BodyBehaviour.PROCESS;
            this.skipCloseTag = false;
        }

    }




    private static final class ExecLevelData {

        final IEngineConfiguration configuration;
        final TemplateMode templateMode;

        boolean suspended;
        final Model model;
        final ElementProcessorIterator processorIterator;
        boolean queueProcessable;
        boolean queueProcessBeforeDelegate;
        boolean discardEvent;
        BodyBehaviour bodyBehaviour;

        IterationArtifacts iterationArtifacts;


        ExecLevelData(final IEngineConfiguration configuration, final TemplateMode templateMode) {
            super();
            this.configuration = configuration;
            this.templateMode = templateMode;
            this.model = new Model(configuration, templateMode);
            this.processorIterator = new ElementProcessorIterator();
            this.iterationArtifacts = new IterationArtifacts();
            reset();
        }

        void resetQueue() {
            this.model.reset();
            this.queueProcessable = false;
            this.queueProcessBeforeDelegate = false;
        }

        void reset() {
            resetQueue();
            this.suspended = false;
            this.processorIterator.reset();
            this.discardEvent = false;
            this.bodyBehaviour = BodyBehaviour.PROCESS;
            this.iterationArtifacts.reset();
        }

        void resetAsCloneOf(final ExecLevelData execLevelData, final boolean cloneGathering) {
            reset();
            this.suspended = execLevelData.suspended;
            this.model.resetAsCloneOf(execLevelData.model);
            this.processorIterator.resetAsCloneOf(execLevelData.processorIterator);
            this.queueProcessable = execLevelData.queueProcessable;
            this.queueProcessBeforeDelegate = execLevelData.queueProcessBeforeDelegate;
            this.discardEvent = execLevelData.discardEvent;
            this.bodyBehaviour = execLevelData.bodyBehaviour;
            this.iterationArtifacts.resetAsCloneOf(execLevelData.iterationArtifacts);
        }

    }




    private static final class IterationArtifacts {

        Text precedingWhitespace;
        String iterVariableName;
        String iterStatusVariableName;
        Object iteratedObject;

        boolean performBodyFirstLastSwitch;
        Text iterationFirstBodyEventIter0;
        Text iterationFirstBodyEventIterN;
        Text iterationLastBodyEventIterN;
        Text iterationLastBodyEventIterMax;

        IterationArtifacts() {
            super();
            reset();
        }

        void reset() {
            this.precedingWhitespace = null;
            this.iterVariableName = null;
            this.iterStatusVariableName = null;
            this.iteratedObject = null;
            this.performBodyFirstLastSwitch = false;
            this.iterationFirstBodyEventIter0 = null;
            this.iterationFirstBodyEventIterN = null;
            this.iterationLastBodyEventIterN = null;
            this.iterationLastBodyEventIterMax = null;
        }

        void resetAsCloneOf(final IterationArtifacts iterationArtifacts) {
            this.precedingWhitespace = iterationArtifacts.precedingWhitespace;
            this.iterVariableName = iterationArtifacts.iterVariableName;
            this.iterStatusVariableName = iterationArtifacts.iterStatusVariableName;
            this.iteratedObject = iterationArtifacts.iteratedObject;
            this.performBodyFirstLastSwitch = iterationArtifacts.performBodyFirstLastSwitch;
            this.iterationFirstBodyEventIter0 = iterationArtifacts.iterationFirstBodyEventIter0;
            this.iterationFirstBodyEventIterN = iterationArtifacts.iterationFirstBodyEventIterN;
            this.iterationLastBodyEventIterN = iterationArtifacts.iterationLastBodyEventIterN;
            this.iterationLastBodyEventIterMax = iterationArtifacts.iterationLastBodyEventIterMax;
        }

    }


}