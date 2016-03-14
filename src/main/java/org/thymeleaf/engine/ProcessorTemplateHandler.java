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
import org.thymeleaf.model.IModel;
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
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {

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


    private static final QueueAndLevelPendingLoad PENDING_LOAD_QUEUE_AND_LEVEL = new QueueAndLevelPendingLoad();

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

    private IEngineConfiguration configuration = null;
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


    // Declare the structure that will hold the processing data/flags needed to be indexed by the
    // model level (the hierarchy of the markup)
    private ModelLevelData[] modelLevelData;
    private int modelLevel;


    // Declare the structure that will hold the processing data/flags needed to be indexed by the
    // handler execution level (i.e. levels of nesting in handler method execution)
    private ExecLevelData[] execLevelData;
    private int execLevel;


    // Putting a text node to the queue for immediate execution is so common we will have a common buffer object for that
    private Text textBuffer = null;

    // In order to execute IElementModelProcessor processors we will use a buffer so that we don't create so many Model objects
    private Model modelBuffer = null;


    // This variable will contain the last event that has been processed, if this last event was an IText. Its aim
    // is to allow the inclusion of preceding whitespace in the iteration of block elements (such as <tr>, <li>, etc.)
    // so that resulting markup is more readable than the alternative "</tr><tr ...>"
    // Note also that, given whitespace between tags is not significative in XML, this mechanism will be applied in XML
    // template mode disregarding the name of the element.
    private IText lastTextEvent = null;





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
    public void setContext(final ITemplateContext context) {

        super.setContext(context);

        this.context = context;
        Validate.notNull(this.context, "Context cannot be null");
        Validate.notNull(this.context.getTemplateMode(), "Template Mode returned by context cannot be null");

        this.configuration = context.getConfiguration();
        Validate.notNull(this.configuration, "Engine Configuration returned by context cannot be null");
        Validate.notNull(this.configuration.getTextRepository(), "Text Repository returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getElementDefinitions(), "Element Definitions returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getAttributeDefinitions(), "Attribute Definitions returned by the Engine Configuration cannot be null");

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

        // Buffer used for text-shaped body replacement in tags (very common operation)
        this.textBuffer = new Text(this.configuration.getTextRepository());

        // Buffer used for executing IElementModelProcessor processors
        this.modelBuffer = new Model(this.configuration, this.templateMode);

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
            final ModelLevelData[] newModelLevelData = new ModelLevelData[this.modelLevelData.length + 10];
            Arrays.fill(newModelLevelData, null);
            System.arraycopy(this.modelLevelData, 0, newModelLevelData, 0, this.modelLevelData.length);
            this.modelLevelData = newModelLevelData;
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
            final ExecLevelData[] newExecLevelData = new ExecLevelData[this.execLevelData.length + 3];
            Arrays.fill(newExecLevelData, null);
            System.arraycopy(this.execLevelData, 0, newExecLevelData, 0, this.execLevelData.length);
            this.execLevelData = newExecLevelData;
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








    private void finalizeHandleEvent() {

        /*
         * Obtain the data bound to the execution level
         */
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

    }



    private void finalizeHandleDecreaseContextLevel() {

        /*
         * Decrease engine context level, once the handler was executed
         */
        if (this.engineContext != null) {
            this.engineContext.decreaseLevel();
        }

    }








    @Override
    public void handleTemplateStart(final ITemplateStart itemplateStart) {

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
            super.handleTemplateStart(itemplateStart);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


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

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.templateStructureHandler.insertTextProcessable;

                this.textBuffer.setText(this.templateStructureHandler.insertTextValue);
                execLevelData.queue.build(this.textBuffer);

            } else if (this.templateStructureHandler.insertModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.templateStructureHandler.insertModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.templateStructureHandler.insertModelValue);

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        super.handleTemplateStart(itemplateStart);


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }








    @Override
    public void handleTemplateEnd(final ITemplateEnd itemplateEnd) {

        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (templateBoundariesProcessors.length == 0) {
            decreaseModelLevel(); // Decrease the model level increased during template start (should be now: -1)
            super.handleTemplateEnd(itemplateEnd);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


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

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.templateStructureHandler.insertTextProcessable;

                this.textBuffer.setText(this.templateStructureHandler.insertTextValue);
                execLevelData.queue.build(this.textBuffer);

            } else if (this.templateStructureHandler.insertModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.templateStructureHandler.insertModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.templateStructureHandler.insertModelValue);

            }

        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleTemplateEnd(itemplateEnd);


    }



    private void finalizeHandleTemplateEnd(final ITemplateEnd itemplateEnd) {

        /*
         * Obtain the data bound to the execution level
         */
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * PROCESS THE QUEUE, launching all the queued events (BEFORE DELEGATING)
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE HANDLER EXEC LEVEL
         */
        decreaseExecLevel();


        /*
         * DECREASE THE MODEL LEVEL
         */
        decreaseModelLevel();


        /*
         * LAST CHECKS. If we have not returned our indexes to -1, something has gone wrong during processing
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


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN.
         */
        super.handleTemplateEnd(itemplateEnd);

    }








    @Override
    public void handleText(final IText itext) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(Text.asEngineText(this.configuration, itext, true));
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
            super.handleText(itext);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.textProcessors.length; i++) {

            this.textStructureHandler.reset();

            this.textProcessors[i].process(this.context, itext, this.textStructureHandler);

            if (this.textStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.textStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.textStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.textStructureHandler.removeText) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleText(itext);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }








    @Override
    public void handleComment(final IComment icomment) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(Comment.asEngineComment(this.configuration, icomment, true));
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
            super.handleComment(icomment);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.commentProcessors.length; i++) {

            this.commentStructureHandler.reset();

            this.commentProcessors[i].process(this.context, icomment, this.commentStructureHandler);

            if (this.commentStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.commentStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.commentStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.commentStructureHandler.removeComment) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleComment(icomment);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }







    
    @Override
    public void handleCDATASection(final ICDATASection icdataSection) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(CDATASection.asEngineCDATASection(this.configuration, icdataSection, true));
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
            super.handleCDATASection(icdataSection);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.cdataSectionProcessors.length; i++) {

            this.cdataSectionStructureHandler.reset();

            this.cdataSectionProcessors[i].process(this.context, icdataSection, this.cdataSectionStructureHandler);

            if (this.cdataSectionStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.cdataSectionStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.cdataSectionStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.cdataSectionStructureHandler.removeCDATASection) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleCDATASection(icdataSection);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }








    @Override
    public void handleStandaloneElement(final IStandaloneElementTag istandaloneElementTag) {

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
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(
                    StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, istandaloneElementTag, true));
            return;
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
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!wasSuspended && !istandaloneElementTag.hasAssociatedProcessors()) {
            if (this.engineContext != null) {
                this.engineContext.increaseLevel();
            }
            super.handleStandaloneElement(istandaloneElementTag);
            finalizeHandleDecreaseContextLevel();
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        final StandaloneElementTag standaloneElementTag =
                StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, istandaloneElementTag, false);


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

                if (this.elementTagStructureHandler.setLocalVariable) {
                    if (this.engineContext != null) {
                        this.engineContext.setVariables(this.elementTagStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementTagStructureHandler.removeLocalVariable) {
                    if (this.engineContext != null) {
                        for (final String variableName : this.elementTagStructureHandler.removedLocalVariableNames) {
                            this.engineContext.removeVariable(variableName);
                        }
                    }
                }

                if (this.elementTagStructureHandler.setSelectionTarget) {
                    if (this.engineContext != null) {
                        this.engineContext.setSelectionTarget(this.elementTagStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementTagStructureHandler.setInliner) {
                    if (this.engineContext != null) {
                        this.engineContext.setInliner(this.elementTagStructureHandler.setInlinerValue);
                    }
                }

                if (this.elementTagStructureHandler.setTemplateData) {
                    if (this.engineContext != null) {
                        this.engineContext.setTemplateData(this.elementTagStructureHandler.setTemplateDataValue);
                    }
                }

                if (this.elementTagStructureHandler.iterateElement) {

                    // Initialize gathering at the current execution level (if not initialized before)
                    execLevelData.enableGathering();

                    // Set the iteration info in order to start gathering all iterated events
                    execLevelData.gatheringType = ExecLevelData.GatheringType.ITERATION;
                    execLevelData.gatheringModelLevel = this.modelLevel + 1;
                    execLevelData.gatheringQueue.reset();

                    // Suspend execution - execution will be restarted by the handleOpenElement event at the
                    // processIteration() call performed after gathering all the iterated markup
                    execLevelData.suspended = true;

                    // Add this standalone tag to the iteration queue
                    execLevelData.gatheringQueue.build(standaloneElementTag.cloneEvent());

                    // Set the rest of the metadata needed for iteration
                    execLevelData.iterationArtifacts.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    execLevelData.iterationArtifacts.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    execLevelData.iterationArtifacts.iteratedObject = this.elementTagStructureHandler.iteratedObject;

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                             (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(standaloneElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(this.configuration, lastText, true);
                        if (lastEngineText.isWhitespace()) {
                            execLevelData.iterationArtifacts.precedingWhitespace = lastEngineText;
                        }
                    }

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Process the queue by iterating it
                    processIteration();

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
                    openTag.resetAsCloneOf(standaloneElementTag);
                    final CloseElementTag closeTag =
                            new CloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the text node that will be added to the queue (which will be suspended)
                    final Text text = new Text(this.configuration.getTextRepository());
                    text.setText(this.elementTagStructureHandler.setBodyTextValue);
                    execLevelData.queue.build(text);
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // Suspend execution - execution will be restarted by the handleOpenElement event
                    execLevelData.suspended = true;

                    // We will use the gathering queue as if we had just gathered open+close events corresponding
                    // to this standalone element we want to substitute
                    execLevelData.enableGathering();
                    execLevelData.gatheringType = ExecLevelData.GatheringType.MODEL;
                    execLevelData.gatheringQueue.reset();
                    execLevelData.gatheringQueue.build(openTag);
                    execLevelData.gatheringQueue.build(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    processElementModel();

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
                    openTag.resetAsCloneOf(standaloneElementTag);
                    final CloseElementTag closeTag =
                            new CloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the queue (that we will suspend)
                    // Model will be automatically cloned if mutable
                    execLevelData.queue.addModel(this.elementTagStructureHandler.setBodyModelValue);
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Suspend execution - execution will be restarted by the handleOpenElement event
                    execLevelData.suspended = true;

                    // We will use the gathering queue as if we had just gathered open+close events corresponding
                    // to this standalone element we want to substitute
                    execLevelData.enableGathering();
                    execLevelData.gatheringType = ExecLevelData.GatheringType.MODEL;
                    execLevelData.gatheringQueue.reset();
                    execLevelData.gatheringQueue.build(openTag);
                    execLevelData.gatheringQueue.build(closeTag);

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    processElementModel();

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    final IModel insertedModel = this.elementTagStructureHandler.insertBeforeModelValue;
                    if (execLevelData.queue.size() == 0) {
                        // The current queue object is empty, so we can use it to process this inserted model

                        execLevelData.queue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        execLevelData.queue.process(getNext());
                        execLevelData.queue.reset();

                    } else {
                        // The current queue object is not empty :-( so in order to process this inserted model
                        // we will need to use a new queue...

                        final EngineEventQueue newQueue = new EngineEventQueue(this.configuration, this.templateMode, 5);
                        newQueue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        newQueue.process(getNext());
                        newQueue.reset();

                    }

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    execLevelData.queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.queue.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBuffer.setText(this.elementTagStructureHandler.replaceWithTextValue);
                    execLevelData.queue.build(this.textBuffer);

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.queue.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.removeElement) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

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

                if (!execLevelData.processorIterator.lastWasRepeated()){

                    if (execLevelData.queue.size() > 0) {
                        throw new TemplateProcessingException(
                                "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                        "of the target element has already been modified by a previously executed processor " +
                                        "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                        "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                                standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());
                    }

                    // Initialize gathering at the current execution level (if not initialized before)
                    execLevelData.enableGathering();

                    // Set the element model info in order to start gathering all the element model's events
                    execLevelData.gatheringType = ExecLevelData.GatheringType.MODEL;
                    execLevelData.gatheringModelLevel = this.modelLevel + 1;
                    execLevelData.gatheringQueue.reset();

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    execLevelData.processorIterator.setLastToBeRepeated(standaloneElementTag);

                    // Suspend the queue - execution will be restarted by the execution of this event again once model is gathered
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    execLevelData.suspended = true;

                    // Add this standalone tag to the element model queue
                    execLevelData.gatheringQueue.build(standaloneElementTag.cloneEvent());

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be done when we re-execute this after gathering model
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Process the queue by iterating it
                    processElementModel();

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
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
                this.modelBuffer.getEventQueue().resetAsCloneOf(this.execLevelData[this.execLevel - 2].gatheringQueue, false);

                ((IElementModelProcessor) processor).process(this.context, this.modelBuffer, this.elementModelStructureHandler);

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

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = true; // We actually NEED TO process this queue

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.modelBuffer);

                execLevelData.discardEvent = true;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


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
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(
                    OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, iopenElementTag, true));
            increaseModelLevel();
            return;
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
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!wasSuspended && !iopenElementTag.hasAssociatedProcessors()) {
            if (this.engineContext != null) {
                this.engineContext.increaseLevel();
            }
            super.handleOpenElement(iopenElementTag);
            increaseModelLevel();
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        final OpenElementTag openElementTag =
                OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, iopenElementTag, false);


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

                if (this.elementTagStructureHandler.setLocalVariable) {
                    if (this.engineContext != null) {
                        this.engineContext.setVariables(this.elementTagStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementTagStructureHandler.removeLocalVariable) {
                    if (this.engineContext != null) {
                        for (final String variableName : this.elementTagStructureHandler.removedLocalVariableNames) {
                            this.engineContext.removeVariable(variableName);
                        }
                    }
                }

                if (this.elementTagStructureHandler.setSelectionTarget) {
                    if (this.engineContext != null) {
                        this.engineContext.setSelectionTarget(this.elementTagStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementTagStructureHandler.setInliner) {
                    if (this.engineContext != null) {
                        this.engineContext.setInliner(this.elementTagStructureHandler.setInlinerValue);
                    }
                }

                if (this.elementTagStructureHandler.setTemplateData) {
                    if (this.engineContext != null) {
                        this.engineContext.setTemplateData(this.elementTagStructureHandler.setTemplateDataValue);
                    }
                }

                if (this.elementTagStructureHandler.iterateElement) {

                    // Initialize gathering at the current execution level (if not initialized before)
                    execLevelData.enableGathering();

                    // Set the iteration info in order to start gathering all iterated events
                    execLevelData.gatheringType = ExecLevelData.GatheringType.ITERATION;
                    execLevelData.gatheringModelLevel = this.modelLevel + 1;
                    execLevelData.gatheringQueue.reset();

                    // Before suspending the queue, we have to check if it is the result of a "setBodyText", in
                    // which case it will contain only one non-cloned node: the text buffer. And we will need
                    // to clone that buffer before suspending the queue to avoid nasty interactions during iteration
                    if (execLevelData.queue.size() == 1 && execLevelData.queue.get(0) == this.textBuffer) {
                        // Replace the text buffer with a clone
                        execLevelData.queue.reset();
                        execLevelData.queue.build(this.textBuffer.cloneEvent());
                    }

                    // Suspend execution - execution will be restarted by the handleOpenElement event at the
                    // processIteration() call performed after gathering all the iterated markup
                    execLevelData.suspended = true;

                    // Add this standalone tag to the iteration queue
                    execLevelData.gatheringQueue.build(openElementTag.cloneEvent());

                    // Set the rest of the metadata needed for iteration
                    execLevelData.iterationArtifacts.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    execLevelData.iterationArtifacts.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    execLevelData.iterationArtifacts.iteratedObject = this.elementTagStructureHandler.iteratedObject;

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                                    (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(openElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(this.configuration, lastText, true);
                        if (lastEngineText.isWhitespace()) {
                            execLevelData.iterationArtifacts.precedingWhitespace = lastEngineText;
                        }
                    }

                    // Increase model level, as normal with open tags (we still need to traverse and gather
                    // all events until the close one before processing iteration itself)
                    increaseModelLevel();

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- we need processIteration() to read our data
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // For now we will not be cloning the buffer and just hoping it will be executed as is. This is
                    // the most common case (th:text) and this will save us a good number of Text nodes. But note that
                    // if this element is iterated AFTER we set this, we will need to clone this node before suspending
                    // the queue, or we might have nasty interactions with each of the subsequent iterations
                    this.textBuffer.setText(this.elementTagStructureHandler.setBodyTextValue);
                    execLevelData.queue.build(this.textBuffer);

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.queue.addModel(this.elementTagStructureHandler.setBodyModelValue);

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    final IModel insertedModel = this.elementTagStructureHandler.insertBeforeModelValue;
                    if (execLevelData.queue.size() == 0) {
                        // The current queue object is empty, so we can use it to process this inserted model

                        execLevelData.queue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        execLevelData.queue.process(getNext());
                        execLevelData.queue.reset();

                    } else {
                        // The current queue object is not empty :-( so in order to process this inserted model
                        // we will need to use a new queue...

                        final EngineEventQueue newQueue = new EngineEventQueue(this.configuration, this.templateMode, 5);
                        newQueue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        newQueue.process(getNext());
                        newQueue.reset();

                    }

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    execLevelData.queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.queue.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBuffer.setText(this.elementTagStructureHandler.replaceWithTextValue);
                    execLevelData.queue.build(this.textBuffer);

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue
                    execLevelData.queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    execLevelData.queue.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeElement) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

                    execLevelData.discardEvent = true;
                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    execLevelData.discardEvent = true;

                } else if (this.elementTagStructureHandler.removeBody) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

                    execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;

                } else if (this.elementTagStructureHandler.removeAllButFirstChild) {

                    execLevelData.queue.reset(); // Remove any previous results on the queue

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

                    if (execLevelData.queue.size() > 0) {
                        throw new TemplateProcessingException(
                                "Cannot execute model processor " + processor.getClass().getName() + " as the body " +
                                "of the target element has already been modified by a previously executed processor " +
                                "on the same tag. Model processors cannot execute on already-modified bodies as these " +
                                "might contain unprocessable events (e.g. as a result of a 'th:text' or similar)",
                                openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());
                    }

                    // Initialize gathering at the current execution level (if not initialized before)
                    execLevelData.enableGathering();

                    // Set the element model info in order to start gathering all the element model's events
                    execLevelData.gatheringType = ExecLevelData.GatheringType.MODEL;
                    execLevelData.gatheringModelLevel = this.modelLevel + 1;
                    execLevelData.gatheringQueue.reset();

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    execLevelData.processorIterator.setLastToBeRepeated(openElementTag);

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    execLevelData.suspended = true;

                    // Add the tag itself to the element model queue
                    execLevelData.gatheringQueue.build(openElementTag.cloneEvent());

                    // Increase model level, as normal with open tags
                    increaseModelLevel();

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
                this.modelBuffer.getEventQueue().resetAsCloneOf(this.execLevelData[this.execLevel - 2].gatheringQueue, false);

                ((IElementModelProcessor) processor).process(this.context, this.modelBuffer, this.elementModelStructureHandler);

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

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = true; // We actually NEED TO process this queue

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.modelBuffer);

                execLevelData.discardEvent = true;
                execLevelData.bodyBehaviour = BodyBehaviour.SKIP_ALL;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MODEL LEVEL RIGHT AFTERWARDS
         */
        if (!execLevelData.discardEvent) {
            super.handleOpenElement(openElementTag);
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
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


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
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
            return;
        }

        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ELEMENT's MODEL GATHERING, and in such case, process it
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel + 1 == this.execLevelData[this.execLevel].gatheringModelLevel) {

            // Add the last tag: the closing one
            this.execLevelData[this.execLevel].gatheringQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));

            // Process the queue
            switch (this.execLevelData[this.execLevel].gatheringType) {
                case ITERATION: processIteration(); break;
                case MODEL: processElementModel(); break;
                default: throw new TemplateProcessingException("Unknown gathering type: " + this.execLevelData[this.execLevel].gatheringType);
            }

            // Decrease the context level
            if (this.engineContext != null) {
                this.engineContext.decreaseLevel();
            }

            return;

        }

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
        super.handleCloseElement(icloseElementTag);

    }








    private void handleUnmatchedCloseElement(final ICloseElementTag icloseElementTag) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) { // an unmatched is not really an element
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
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
        super.handleCloseElement(icloseElementTag);

    }








    @Override
    public void handleDocType(final IDocType idocType) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(DocType.asEngineDocType(this.configuration, idocType, true));
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
            super.handleDocType(idocType);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.docTypeProcessors.length; i++) {

            this.docTypeStructureHandler.reset();

            this.docTypeProcessors[i].process(this.context, idocType, this.docTypeStructureHandler);

            if (this.docTypeStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.docTypeStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.docTypeStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.docTypeStructureHandler.removeDocType) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleDocType(idocType);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }





    
    
    
    @Override
    public void handleXMLDeclaration(final IXMLDeclaration ixmlDeclaration) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(XMLDeclaration.asEngineXMLDeclaration(this.configuration, ixmlDeclaration, true));
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
            super.handleXMLDeclaration(ixmlDeclaration);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.xmlDeclarationProcessors.length; i++) {

            this.xmlDeclarationStructureHandler.reset();

            this.xmlDeclarationProcessors[i].process(this.context, ixmlDeclaration, this.xmlDeclarationStructureHandler);

            if (this.xmlDeclarationStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.xmlDeclarationStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.xmlDeclarationStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.xmlDeclarationStructureHandler.removeXMLDeclaration) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleXMLDeclaration(ixmlDeclaration);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }








    @Override
    public void handleProcessingInstruction(final IProcessingInstruction iprocessingInstruction) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.execLevel >= 0 &&
                this.execLevelData[this.execLevel].gatheringType != ExecLevelData.GatheringType.NONE &&
                this.modelLevel >= this.execLevelData[this.execLevel].gatheringModelLevel) {
            this.execLevelData[this.execLevel].gatheringQueue.build(ProcessingInstruction.asEngineProcessingInstruction(this.configuration, iprocessingInstruction, true));
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
            super.handleProcessingInstruction(iprocessingInstruction);
            return;
        }


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseExecLevel();
        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];


        /*
         * EXECUTE PROCESSORS
         */
        for (int i = 0; !execLevelData.discardEvent && i < this.processingInstructionProcessors.length; i++) {

            this.processingInstructionStructureHandler.reset();

            this.processingInstructionProcessors[i].process(this.context, iprocessingInstruction, this.processingInstructionStructureHandler);

            if (this.processingInstructionStructureHandler.replaceWithModel) {

                execLevelData.queue.reset(); // Remove any previous results on the queue
                execLevelData.queueProcessable = this.processingInstructionStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                execLevelData.queue.addModel(this.processingInstructionStructureHandler.replaceWithModelValue);

                execLevelData.discardEvent = true;

            } else if (this.processingInstructionStructureHandler.removeProcessingInstruction) {

                execLevelData.queue.reset(); // Remove any previous results on the queue

                execLevelData.discardEvent = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!execLevelData.discardEvent) {
            super.handleProcessingInstruction(iprocessingInstruction);
        }


        /*
         * FINALIZE HANDLER EXECUTION
         */
        finalizeHandleEvent();

    }






    private void processIteration() {

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
         * STOP GATHERING. This is important because we would start gathering again when we re-execute these same
         * events from the gatheredQueue (instead of from parsing events).
         * Also, note we will not reset the gathering queue here because we actually need to process it.
         */
        outerExecLevelData.gatheringType = ExecLevelData.GatheringType.NONE;
        outerExecLevelData.gatheringModelLevel = Integer.MAX_VALUE;


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
            preparePrettificationOfTextIteration(
                    this.configuration, outerExecLevelData.gatheringQueue, outerExecLevelData.iterationArtifacts);
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
            prepareIterationQueueForIteration(
                    outerExecLevelData.gatheringQueue, outerExecLevelData.iterationArtifacts, status.index, !iterHasNext);


            /*
             * PERFORM THE EXECUTION on the gathered queue, which now does not live at the current exec level, but
             * at the previous one (we protected it by increasing execution level before)
             */
            outerExecLevelData.gatheringQueue.process(this);


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






    private void processElementModel() {

        /*
         * We will use to execution levels here: an "outer" one containing the original artifacts (esp. the
         * originally gathered queue) and an "inner" one which will be the one on which the real gathered
         * events will execute when their execution is resumed.
         */
        final ExecLevelData outerExecLevelData = this.execLevelData[this.execLevel];

        /*
         * STOP GATHERING. This is important because we would start gathering again when we re-execute these same
         * events from the gatheredQueue (instead of from parsing events).
         * Also, note we will not reset the gathering queue here because we actually need to process it.
         */
        outerExecLevelData.gatheringType = ExecLevelData.GatheringType.NONE;
        outerExecLevelData.gatheringModelLevel = Integer.MAX_VALUE;

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
         * PERFORM THE EXECUTION on the gathered queue, which now does not live at the current exec level, but
         * at the previous one (we protected it by increasing execution level before).
         */
        outerExecLevelData.gatheringQueue.process(this);

        /*
         * Decrease the execution level, twice. Note these decreases do not exactly correspond to the two increases
         * above. Instead, the first one cleans the "clean base of execution" and the second decrease will clean the
         * execution level increased by the first even that originally suspended its execution and started the model
         * gathering.
         */
        decreaseExecLevel();
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
            final IEngineConfiguration configuration, final EngineEventQueue gatheredQueue, final IterationArtifacts iterArtifacts) {

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

        if (gatheredQueue.size() <= 2) {
            // This does only contain the template start + end events -- nothing to be done
            iterArtifacts.performBodyFirstLastSwitch = false;
            return;
        }

        int firstBodyEventCutPoint = -1;
        int lastBodyEventCutPoint = -1;

        final ITemplateEvent firstBodyEvent = gatheredQueue.get(1); // we know there is at least one body event
        Text firstTextBodyEvent = null;
        if (gatheredQueue.get(0) instanceof OpenElementTag && firstBodyEvent instanceof IText) {

            firstTextBodyEvent = Text.asEngineText(configuration, (Text)firstBodyEvent, false);

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

        final ITemplateEvent lastBodyEvent = gatheredQueue.get(gatheredQueue.size() - 2);
        Text lastTextBodyEvent = null;
        if (firstBodyEventCutPoint >= 0 &&
                gatheredQueue.get(gatheredQueue.size() - 1) instanceof CloseElementTag && lastBodyEvent instanceof IText) {

            lastTextBodyEvent = Text.asEngineText(configuration, (IText)lastBodyEvent, false);

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

            iterArtifacts.iterationFirstBodyEventIter0 = new Text(configuration.getTextRepository(), textFor0);
            iterArtifacts.iterationFirstBodyEventIterN = new Text(configuration.getTextRepository(), textForN);
            iterArtifacts.iterationLastBodyEventIterMax = new Text(configuration.getTextRepository(), textForMax);
            iterArtifacts.iterationLastBodyEventIterN = new Text(configuration.getTextRepository(), textForN);
            return;
        }

        // At this point, we know the first and last body events are different objects

        iterArtifacts.iterationFirstBodyEventIter0 = firstTextBodyEvent.cloneEvent();
        iterArtifacts.iterationLastBodyEventIterMax = lastTextBodyEvent.cloneEvent();

        if (firstBodyEventCutPoint == 0) {
            iterArtifacts.iterationFirstBodyEventIterN = firstTextBodyEvent.cloneEvent();
        } else {
            iterArtifacts.iterationFirstBodyEventIterN =
                    new Text(configuration.getTextRepository(), firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
        }

        if (lastBodyEventCutPoint == lastTextBodyEvent.length()) {
            iterArtifacts.iterationLastBodyEventIterN = lastTextBodyEvent.cloneEvent();
        } else {
            iterArtifacts.iterationLastBodyEventIterN =
                    new Text(configuration.getTextRepository(), lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
        }


    }




    /*
     * This will prepare the iteration queue, before executing it for each iteration. Basically, this will manage
     * the whitespaces that need to be applied before or after the queue in order to make the results of iteration
     * more pretty.
     */
    private static void prepareIterationQueueForIteration(
            final EngineEventQueue iterQueue, final IterationArtifacts iterArtifacts, final int iterationIndex, final boolean last) {

        /*
         * FOR MARKUP TEMPLATE MODES: check if we have to add the initial whitespace (to iter > 0)
         */
        if (iterArtifacts.precedingWhitespace != null && iterationIndex == 1) {
            iterQueue.insert(0, iterArtifacts.precedingWhitespace, false);
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
            ((Text)iterQueue.get(1)).resetAsCloneOf(iterArtifacts.iterationFirstBodyEventIter0);
            ((Text)iterQueue.get(iterQueue.size() - 2)).resetAsCloneOf(iterArtifacts.iterationLastBodyEventIterN);
        }

        if (iterationIndex == 1) {
            ((Text)iterQueue.get(1)).resetAsCloneOf(iterArtifacts.iterationFirstBodyEventIterN);
        }

        if (last) {
            ((Text)iterQueue.get(iterQueue.size() - 2)).resetAsCloneOf(iterArtifacts.iterationLastBodyEventIterMax);
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

        enum GatheringType { ITERATION, MODEL, NONE }

        final IEngineConfiguration configuration;
        final TemplateMode templateMode;

        boolean suspended;
        final EngineEventQueue queue;
        final ElementProcessorIterator processorIterator;
        boolean queueProcessable;
        boolean discardEvent;
        BodyBehaviour bodyBehaviour;

        boolean gatheringEnabled;
        GatheringType gatheringType;
        EngineEventQueue gatheringQueue;
        int gatheringModelLevel;
        IterationArtifacts iterationArtifacts;


        ExecLevelData(final IEngineConfiguration configuration, final TemplateMode templateMode) {
            super();
            this.configuration = configuration;
            this.templateMode = templateMode;
            this.queue = new EngineEventQueue(configuration, templateMode);
            this.processorIterator = new ElementProcessorIterator();
            this.gatheringEnabled = false;
            reset();
        }


        void enableGathering() {
            if (!this.gatheringEnabled) {
                this.gatheringQueue = new EngineEventQueue(this.configuration, this.templateMode);
                this.iterationArtifacts = new IterationArtifacts();
                this.gatheringEnabled = true;
            }
        }

        void reset() {
            this.suspended = false;
            this.queue.reset();
            this.processorIterator.reset();
            this.queueProcessable = false;
            this.discardEvent = false;
            this.bodyBehaviour = BodyBehaviour.PROCESS;
            if (this.gatheringEnabled) { // Note this flag is itself not reset. It's just a way to perform lazy init
                this.gatheringType = GatheringType.NONE;
                this.gatheringQueue.reset();
                this.gatheringModelLevel = Integer.MAX_VALUE;
                this.iterationArtifacts.reset();
            } else {
                this.gatheringType = GatheringType.NONE; // null would be a bad idea here, as this is checked in handlers
                this.gatheringQueue = null;
                this.gatheringModelLevel = Integer.MAX_VALUE;
                this.iterationArtifacts = null;
            }
        }

        void resetAsCloneOf(final ExecLevelData execLevelData, final boolean cloneGathering) {
            reset();
            this.suspended = execLevelData.suspended;
            this.queue.resetAsCloneOf(execLevelData.queue, false);
            this.processorIterator.resetAsCloneOf(execLevelData.processorIterator);
            this.queueProcessable = execLevelData.queueProcessable;
            this.discardEvent = execLevelData.discardEvent;
            this.bodyBehaviour = execLevelData.bodyBehaviour;
            if (cloneGathering && execLevelData.gatheringEnabled) {
                if (!this.gatheringEnabled) {
                    enableGathering();
                }
                this.gatheringType = execLevelData.gatheringType;
                this.gatheringQueue.resetAsCloneOf(execLevelData.gatheringQueue, false);
                this.gatheringModelLevel = execLevelData.gatheringModelLevel;
                this.iterationArtifacts.resetAsCloneOf(execLevelData.iterationArtifacts);
            }
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
            this.iterationFirstBodyEventIter0 =
                    (iterationArtifacts.iterationFirstBodyEventIter0 == null? null : iterationArtifacts.iterationFirstBodyEventIter0.cloneEvent());
            this.iterationFirstBodyEventIterN =
                    (iterationArtifacts.iterationFirstBodyEventIterN == null? null : iterationArtifacts.iterationFirstBodyEventIterN.cloneEvent());
            this.iterationLastBodyEventIterN =
                    (iterationArtifacts.iterationLastBodyEventIterN == null? null : iterationArtifacts.iterationLastBodyEventIterN.cloneEvent());
            this.iterationLastBodyEventIterMax =
                    (iterationArtifacts.iterationLastBodyEventIterMax == null? null : iterationArtifacts.iterationLastBodyEventIterMax.cloneEvent());
        }

    }






    private static interface IPendingLoad {

        void execute(final ProcessorTemplateHandler handler);

    }


    private static final class QueueAndLevelPendingLoad implements IPendingLoad {

        @Override
        public void execute(final ProcessorTemplateHandler handler) {

            final ExecLevelData execLevelData = handler.execLevelData[handler.execLevel];

            execLevelData.queue.process(execLevelData.queueProcessable ? handler : handler.getNext());
            execLevelData.queue.reset();

            handler.decreaseExecLevel();

        }

    }


}