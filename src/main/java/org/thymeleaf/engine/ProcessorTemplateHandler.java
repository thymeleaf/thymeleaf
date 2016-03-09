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

    private IEngineConfiguration configuration;
    private TemplateMode templateMode;

    private ITemplateContext context;
    private IEngineContext engineContext;

    private boolean hasTemplateBoundariesProcessors = false;
    private boolean hasCDATASectionProcessors = false;
    private boolean hasCommentProcessors = false;
    private boolean hasDocTypeProcessors = false;
    private boolean hasProcessingInstructionProcessors = false;
    private boolean hasTextProcessors = false;
    private boolean hasXMLDeclarationProcessors = false;


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

    // Used for gathering and keeping account of model events when
    private boolean gatheringIteration = false;
    private IterationSpec iterationSpec = null;
    private boolean gatheringElementModel = false;
    private ElementModelSpec elementModelSpec = null;

    // Used during iteration, in order to not create too many queue and processor objects (which in turn might
    // create too many event buffer objects)
    private IterationArtifacts[] iterationArtifacts = null;
    private int iterationArtifactsIndex = 0;

    // Used during element model processing, in order to not create too many queue and processor objects (which in
    // turn might create too many event buffer objects)
    private ElementModelArtifacts[] elementModelArtifacts = null;
    private int elementModelArtifactsIndex = 0;

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

        this.execLevelData = new ExecLevelData[3];
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

        // Specs containing all the info required for suspending the execution of a processor in order to e.g. change
        // handling method (standalone -> open) or start caching an iteration
        this.iterationSpec = new IterationSpec(this.templateMode, this.configuration);
        this.elementModelSpec = new ElementModelSpec(this.templateMode, this.configuration);

        // Obtain all processor sets and compute sizes
        final Set<ITemplateBoundariesProcessor> templateBoundariesProcessorSet = this.configuration.getTemplateBoundariesProcessors(this.templateMode);
        final Set<ICDATASectionProcessor> cdataSectionProcessorSet = this.configuration.getCDATASectionProcessors(this.templateMode);
        final Set<ICommentProcessor> commentProcessorSet = this.configuration.getCommentProcessors(this.templateMode);
        final Set<IDocTypeProcessor> docTypeProcessorSet = this.configuration.getDocTypeProcessors(this.templateMode);
        final Set<IProcessingInstructionProcessor> processingInstructionProcessorSet = this.configuration.getProcessingInstructionProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessorSet = this.configuration.getTextProcessors(this.templateMode);
        final Set<IXMLDeclarationProcessor> xmlDeclarationProcessorSet = this.configuration.getXMLDeclarationProcessors(this.templateMode);
        final int templateBoundariesProcessorSetSize = templateBoundariesProcessorSet.size();
        final int cdataSectionProcessorSetSize = cdataSectionProcessorSet.size();
        final int commentProcessorSetSize = commentProcessorSet.size();
        final int docTypeProcessorSetSize = docTypeProcessorSet.size();
        final int processingInstructionProcessorSetSize = processingInstructionProcessorSet.size();
        final int textProcessorSetSize = textProcessorSet.size();
        final int xmlDeclarationProcessorSetSize = xmlDeclarationProcessorSet.size();

        // Flags used for quickly determining if a non-element structure might have to be processed or not
        this.hasTemplateBoundariesProcessors = templateBoundariesProcessorSetSize > 0;
        this.hasCDATASectionProcessors = cdataSectionProcessorSetSize > 0;
        this.hasCommentProcessors = commentProcessorSetSize > 0;
        this.hasDocTypeProcessors = docTypeProcessorSetSize > 0;
        this.hasProcessingInstructionProcessors = processingInstructionProcessorSetSize > 0;
        this.hasTextProcessors = textProcessorSetSize > 0;
        this.hasXMLDeclarationProcessors = xmlDeclarationProcessorSetSize > 0;

        // Initialize arrays containing the processors for all the non-element structures (these do not change during execution)
        this.templateBoundariesProcessors =
                templateBoundariesProcessorSetSize == 0? EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS : templateBoundariesProcessorSet.toArray(new ITemplateBoundariesProcessor[templateBoundariesProcessorSetSize]);
        this.cdataSectionProcessors =
                cdataSectionProcessorSetSize == 0? EMPTY_CDATA_SECTION_PROCESSORS : cdataSectionProcessorSet.toArray(new ICDATASectionProcessor[cdataSectionProcessorSetSize]);
        this.commentProcessors =
                commentProcessorSetSize == 0? EMPTY_COMMENT_PROCESSORS : commentProcessorSet.toArray(new ICommentProcessor[commentProcessorSetSize]);
        this.docTypeProcessors =
                docTypeProcessorSetSize == 0? EMPTY_DOCTYPE_PROCESSORS : docTypeProcessorSet.toArray(new IDocTypeProcessor[docTypeProcessorSetSize]);
        this.processingInstructionProcessors =
                processingInstructionProcessorSetSize == 0? EMPTY_PROCESSING_INSTRUCTION_PROCESSORS : processingInstructionProcessorSet.toArray(new IProcessingInstructionProcessor[processingInstructionProcessorSetSize]);
        this.textProcessors =
                textProcessorSetSize == 0? EMPTY_TEXT_PROCESSORS : textProcessorSet.toArray(new ITextProcessor[textProcessorSetSize]);
        this.xmlDeclarationProcessors =
                xmlDeclarationProcessorSetSize == 0? EMPTY_XML_DECLARATION_PROCESSORS : xmlDeclarationProcessorSet.toArray(new IXMLDeclarationProcessor[xmlDeclarationProcessorSetSize]);

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
        this.modelLevelData[this.modelLevel].reset();
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
        this.execLevelData[this.execLevel].reset();
        this.execLevel--;
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
        if (!this.hasTemplateBoundariesProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * HANDLER EXEC LEVEL WILL NOT BE DECREASED until the "templateEnd" event
         */
        decreaseExecLevel();

    }




    @Override
    public void handleTemplateEnd(final ITemplateEnd itemplateEnd) {

        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasTemplateBoundariesProcessors) {
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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(Text.asEngineText(this.configuration, itext, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(Text.asEngineText(this.configuration, itext, true));
            return;
        }


        /*
         * KEEP THE POINTER to this event, now we know it will be processed somehow
         */
        this.lastTextEvent = itext;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasTextProcessors) {
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

        PENDING_LOAD_QUEUE_AND_LEVEL.execute(this);
//
//        /*
//         * PROCESS THE QUEUE, launching all the queued events
//         */
//        queue.process(this.eventQueuesProcessable[this.handlerExecLevel] ? this : getNext());
//        queue.reset();
//
//
//        /*
//         * DECREASE THE EXEC LEVEL, so that the structures can be reused
//         */
//        decreaseExecLevel();

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(Comment.asEngineComment(this.configuration, icomment, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(Comment.asEngineComment(this.configuration, icomment, true));
            return;
        }


        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasCommentProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(CDATASection.asEngineCDATASection(this.configuration, icdataSection, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(CDATASection.asEngineCDATASection(this.configuration, icdataSection, true));
            return;
        }


        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasCDATASectionProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    StandaloneElementTag.asEngineStandaloneElementTag(
                            this.templateMode, this.configuration, istandaloneElementTag, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
                    StandaloneElementTag.asEngineStandaloneElementTag(
                            this.templateMode, this.configuration, istandaloneElementTag, true));
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
            if (this.engineContext != null) {
                this.engineContext.decreaseLevel();
            }
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

                    // Set the iteration info in order to start gathering all iterated events
                    this.gatheringIteration = true;
                    this.iterationSpec.fromModelLevel = this.modelLevel + 1;
                    this.iterationSpec.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    this.iterationSpec.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    this.iterationSpec.iteratedObject = this.elementTagStructureHandler.iteratedObject;
                    this.iterationSpec.iterationQueue.reset();

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                             (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(standaloneElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(this.configuration, lastText, true);
                        if (lastEngineText.isWhitespace()) {
                            this.iterationSpec.precedingWhitespace = lastEngineText;
                        }
                    }

                    // Suspend execution - execution will be restarted by the handleOpenElement event at the
                    // processIteration() call performed after gathering all the iterated markup
                    execLevelData.suspended = true;

                    // Add this standalone tag to the iteration queue
                    this.iterationSpec.iterationQueue.build(standaloneElementTag.cloneEvent());

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

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

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

                    // Note we DO NOT DECREASE THE EXEC LEVEL -- that will be the responsibility of handleOpenElement
                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

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

                    // Set the element model info in order to start gathering all the element model's events
                    this.gatheringElementModel = true;
                    this.elementModelSpec.fromModelLevel = this.modelLevel + 1;
                    this.elementModelSpec.modelQueue.reset();

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    execLevelData.processorIterator.setLastToBeRepeated(standaloneElementTag);

                    // Suspend the queue - execution will be restarted by the execution of this event again once model is gathered
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    execLevelData.suspended = true;

                    // Add this standalone tag to the element model queue
                    this.elementModelSpec.modelQueue.build(standaloneElementTag.cloneEvent());

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

                final EngineEventQueue gatheredQueue = this.elementModelArtifacts[this.elementModelArtifactsIndex - 1].modelQueue;

                // We will use the model buffer in order to save in number of Model objects created. This is safe
                // because we will only be calling one of these processors at a time, and the model contents will
                // be cloned after execution in order to insert them into the queue.
                //
                // NOTE we are not cloning the events themselves here. There should be no need, as we are going to
                //      re-locate these events into a new queue, and their old position (which will be executed
                //      anyway) will be ignored.
                this.modelBuffer.getEventQueue().resetAsCloneOf(gatheredQueue, false);

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, iopenElementTag, true));
            increaseModelLevel();
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
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

                    // Set the iteration info in order to start gathering all iterated events
                    this.gatheringIteration = true;
                    this.iterationSpec.fromModelLevel = this.modelLevel + 1;
                    this.iterationSpec.iterVariableName = this.elementTagStructureHandler.iterVariableName;
                    this.iterationSpec.iterStatusVariableName = this.elementTagStructureHandler.iterStatusVariableName;
                    this.iterationSpec.iteratedObject = this.elementTagStructureHandler.iteratedObject;
                    this.iterationSpec.iterationQueue.reset();

                    // If there is a preceding whitespace, add it to the iteration spec
                    if (lastText != null &&
                            ((this.templateMode == TemplateMode.XML) ||
                                    (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(openElementTag.elementDefinition.elementName)))) {
                        final Text lastEngineText = Text.asEngineText(this.configuration, lastText, true);
                        if (lastEngineText.isWhitespace()) {
                            this.iterationSpec.precedingWhitespace = lastEngineText;
                        }
                    }

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

                    // Add the tag itself to the iteration queue
                    this.iterationSpec.iterationQueue.build(openElementTag.cloneEvent());

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

                    // Set the element model info in order to start gathering all the element model's events
                    this.gatheringElementModel = true;
                    this.elementModelSpec.fromModelLevel = this.modelLevel + 1;
                    this.elementModelSpec.modelQueue.reset();

                    // Set the processor to be executed again, because this time we will just set the "model gathering" mechanism
                    execLevelData.processorIterator.setLastToBeRepeated(openElementTag);

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    execLevelData.suspended = true;

                    // Add the tag itself to the element model queue
                    this.elementModelSpec.modelQueue.build(openElementTag.cloneEvent());

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

                final EngineEventQueue gatheredQueue = this.elementModelArtifacts[this.elementModelArtifactsIndex - 1].modelQueue;

                // We will use the model buffer in order to save in number of Model objects created. This is safe
                // because we will only be calling one of these processors at a time, and the model contents will
                // be cloned after execution in order to insert them into the queue.
                //
                // NOTE we are not cloning the events themselves here. There should be no need, as we are going to
                //      re-locate these events into a new queue, and their old position (which will be executed
                //      anyway) will be ignored.
                this.modelBuffer.getEventQueue().resetAsCloneOf(gatheredQueue, false);

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
            return;
        }

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
            return;
        }

        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ITERATION, and in such case, process it
         */
        if (this.gatheringIteration && this.modelLevel + 1 == this.iterationSpec.fromModelLevel) {

            // Add the last tag: the closing one
            this.iterationSpec.iterationQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));

            // Process the queue by iterating it
            processIteration();

            // Decrease the context level
            if (this.engineContext != null) {
                this.engineContext.decreaseLevel();
            }

            return;

        }

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ELEMENT's MODEL GATHERING, and in such case, process it
         */
        if (this.gatheringElementModel && this.modelLevel + 1 == this.elementModelSpec.fromModelLevel) {

            // Add the last tag: the closing one
            this.elementModelSpec.modelQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));

            // Process the queue
            processElementModel();

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
            return;
        }

        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(DocType.asEngineDocType(this.configuration, idocType, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(DocType.asEngineDocType(this.configuration, idocType, true));
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasDocTypeProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

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
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    XMLDeclaration.asEngineXMLDeclaration(this.configuration, ixmlDeclaration, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
                    XMLDeclaration.asEngineXMLDeclaration(this.configuration, ixmlDeclaration, true));
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasXMLDeclarationProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction iprocessingInstruction) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ALL) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.gatheringIteration && this.modelLevel >= this.iterationSpec.fromModelLevel) {
            this.iterationSpec.iterationQueue.build(
                    ProcessingInstruction.asEngineProcessingInstruction(this.configuration, iprocessingInstruction, true));
            return;
        }


        /*
         * CHECK WHETHER WE ARE GATHERING AN ELEMENT's MODEL and we just need to cache this to the queue (for now)
         */
        if (this.gatheringElementModel && this.modelLevel >= this.elementModelSpec.fromModelLevel) {
            this.elementModelSpec.modelQueue.build(
                    ProcessingInstruction.asEngineProcessingInstruction(this.configuration, iprocessingInstruction, true));
            return;
        }

        
        /*
         * RESET THE LAST-TEXT POINTER, now we know this event will be processed somehow
         */
        this.lastTextEvent = null;


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasProcessingInstructionProcessors) {
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
         * PROCESS THE QUEUE, launching all the queued events
         */
        execLevelData.queue.process(execLevelData.queueProcessable ? this : getNext());
        execLevelData.queue.reset();


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseExecLevel();

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
         * PREPARE THE ITERATION ARTIFACTS (used in order to being able to reuse the spec in nested execs)
         */
        if (this.iterationArtifacts == null) {
            this.iterationArtifacts = new IterationArtifacts[2];
            Arrays.fill(this.iterationArtifacts, null);
        }
        if (this.iterationArtifactsIndex == this.iterationArtifacts.length) {
            final IterationArtifacts[] newIterationArtifacts = new IterationArtifacts[this.iterationArtifacts.length + 2];
            Arrays.fill(newIterationArtifacts, null);
            System.arraycopy(this.iterationArtifacts, 0, newIterationArtifacts, 0, this.iterationArtifacts.length);
            this.iterationArtifacts = newIterationArtifacts;
        }
        if (this.iterationArtifacts[this.iterationArtifactsIndex] == null) {
            this.iterationArtifacts[this.iterationArtifactsIndex] = new IterationArtifacts(this.templateMode, this.configuration);
        }
        final IterationArtifacts iterArtifacts = this.iterationArtifacts[this.iterationArtifactsIndex];
        this.iterationArtifactsIndex++;

        /*
         * FIX THE ITERATION-RELATED VARIABLES
         */

        final Text precedingWhitespace = this.iterationSpec.precedingWhitespace;
        final String iterVariableName = this.iterationSpec.iterVariableName;
        String iterStatusVariableName = this.iterationSpec.iterStatusVariableName;
        if (StringUtils.isEmptyOrWhitespace(iterStatusVariableName)) {
            // If no name has been specified for the status variable, we will use the same as the iter var + "Stat"
            iterStatusVariableName = iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        }
        final Object iteratedObject = this.iterationSpec.iteratedObject;

        /*
         * Copy the gathered iterated queue into the real queue that will be executed (at iterArtifacts)
         */
        iterArtifacts.iterationQueue.resetAsCloneOf(iterationSpec.iterationQueue, false);

        /*
         * This will compute whether transformations on the first/last body events need to be performed
         * (e.g. if we need to remove some whitespace when iterating in TEXT mode)
         */
        prepareIterationEvents(this.configuration, this.templateMode, this.iterationSpec, iterArtifacts);

        /*
         * Depending on the class of the iterated object, we will iterate it in one way or another. And also we
         * might have a "size" value for the stat variable or not.
         */
        final Iterator<?> iterator = computeIteratedObjectIterator(iteratedObject);

        final IterationStatusVar status = new IterationStatusVar();
        status.index = 0;
        status.size = computeIteratedObjectSize(iteratedObject);

        // We need to reset it or we won't be able to reuse it in nested iterations
        this.iterationSpec.reset();
        this.gatheringIteration = false;


        /*
         * FIX THE SUSPENSION-RELATED VARIABLES
         */

        final ExecLevelData execLevelData = this.execLevelData[this.execLevel];

        final BodyBehaviour suspendedBodyBehaviour = execLevelData.bodyBehaviour;
        final boolean suspendedQueueProcessable = execLevelData.queueProcessable;
        iterArtifacts.suspendedQueue.resetAsCloneOf(execLevelData.queue, false);
        iterArtifacts.suspendedElementProcessorIterator.resetAsCloneOf(execLevelData.processorIterator);

        // We already saved the required info, so we will decrease exec level, and then increase it for each iteration
        decreaseExecLevel();


        /*
         * PERFORM THE ITERATION
         */

        boolean iterHasNext = iterator.hasNext();
        while (iterHasNext) {

            status.current = iterator.next();

            // we precompute this in order to know when we are at the last element
            iterHasNext = iterator.hasNext();

            this.engineContext.increaseLevel();

            this.engineContext.setVariable(iterVariableName, status.current);
            this.engineContext.setVariable(iterStatusVariableName, status);

            // We will increase the exec level because that was the state in which it was when suspended
            // Note there is no need to decrease it because that will be done by the handleOpenElement or handleStandaloneElement
            // that originally suspended execution in order to gather iterated markup
            increaseExecLevel();

            // We will initialize the execution level artifacts just as if we had just suspended them
            execLevelData.suspended = true;
            execLevelData.bodyBehaviour = suspendedBodyBehaviour;
            execLevelData.queueProcessable = suspendedQueueProcessable;
            execLevelData.queue.resetAsCloneOf(iterArtifacts.suspendedQueue, false);
            execLevelData.processorIterator.resetAsCloneOf(iterArtifacts.suspendedElementProcessorIterator);

            // If this iterator (e.g. th:each) lived in a tag with a "remove all but first" instruction,
            // we need to reinitialize SKIP_ELEMENTS -> SKIP_ELEMENTS_BUT_FIRST
            // The reason this is done on modelLevel and not on execLevel is because execLevel is for things
            // that have been changed by the iterated element itself before processing the iteration, and in this case
            // we are talking about an element-skipping operation performed by a higher-level tag that was
            // executed previously.
            if (this.modelLevelData[this.modelLevel].bodyBehaviour == BodyBehaviour.SKIP_ELEMENTS) {
                this.modelLevelData[this.modelLevel].bodyBehaviour = BodyBehaviour.SKIP_ELEMENTS_BUT_FIRST;
            }

            // We might need to perform some modifications to the iteration queue for this iteration
            // For example, in text modes we might modify the first whitespaces in the body of the iterated element
            prepareIterationQueueForIteration(iterArtifacts, status.index, !iterHasNext);

            // Execute the queue itself
            iterArtifacts.iterationQueue.process(this);

            this.engineContext.decreaseLevel();

            // We will use the index in order to determine the moment we need to insert the preceding whitespace into
            // the iteration queue. This is because the preceding text event will have already been issued by the moment
            // we start iterating, and we want to avoid a double whitespace before the first iteration
            if (status.index == 0 && precedingWhitespace != null) {
                // We are calling 'insert' instead of 'build' because we need to add it at the beginning and we are
                // perfectly sure that an iteration queue will never have template start/end events, and therefore
                // no undesirable interactions with the Text precedingWhitespace event
                iterArtifacts.iterationQueue.insert(0, precedingWhitespace, false);
            }

            status.index++;

        }

        // Allow the reuse of the iteration artifacts
        this.iterationArtifactsIndex--;

    }






    private void processElementModel() {

        /*
         * PREPARE THE ELEMENT MODEL ARTIFACTS (used in order to being able to reuse the spec in nested execs)
         */
        if (this.elementModelArtifacts == null) {
            this.elementModelArtifacts = new ElementModelArtifacts[2];
            Arrays.fill(this.elementModelArtifacts, null);
        }
        if (this.elementModelArtifactsIndex == this.elementModelArtifacts.length) {
            final ElementModelArtifacts[] newElementModelArtifacts = new ElementModelArtifacts[this.elementModelArtifacts.length + 2];
            Arrays.fill(newElementModelArtifacts, null);
            System.arraycopy(this.elementModelArtifacts, 0, newElementModelArtifacts, 0, this.elementModelArtifacts.length);
            this.elementModelArtifacts = newElementModelArtifacts;
        }
        if (this.elementModelArtifacts[this.elementModelArtifactsIndex] == null) {
            this.elementModelArtifacts[this.elementModelArtifactsIndex] = new ElementModelArtifacts(this.templateMode, this.configuration);
        }
        final ElementModelArtifacts elemArtifacts = this.elementModelArtifacts[this.elementModelArtifactsIndex];
        this.elementModelArtifactsIndex++;

        /*
         * CLONE THE MODEL EVENTS INTO THE BUFFERIZED ELEMENT MODEL STRUCTURE FOR PROCESSING
         */

        elemArtifacts.modelQueue.resetAsCloneOf(this.elementModelSpec.modelQueue, false);

        // We need to reset it or we won't be able to reuse it in nested executions
        this.elementModelSpec.reset();
        this.gatheringElementModel = false;


        /*
         * PERFORM THE EXECUTION
         */

        elemArtifacts.modelQueue.process(this);

        // Allow the reuse of the artifacts
        this.elementModelArtifactsIndex--;

    }




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




    private static void prepareIterationEvents(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final IterationSpec iterationSpec, final IterationArtifacts iterArtifacts) {

        if (!templateMode.isText() || iterationSpec.iterationQueue.size() <= 2) {
            // This is not a text-mode template, so we will just ignore those first/last events
            // (or either it is a text-mode template, but it only contains the open + close element events)
            iterArtifacts.performBodyFirstLastSwitch = false;
            return;
        }

        /*
         * We are in a textual template mode, and it might be possible to fiddle a bit with whitespaces at the beginning
         * and end of the body, so that iterations look better.
         *
         * The goal is that this:
         *
         * ---------------------
         * List:
         * [# th:each="i : ${items}"]
         *   - [[${i}]]
         * [/]
         * ---------------------
         *
         * ...doesn't look like:
         *
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         *
         *   - [[${i}]]
         * ---------------------
         *
         * ...but instead like:
         *
         * ---------------------
         * List:
         *
         *   - [[${i}]]
         *   - [[${i}]]
         *   - [[${i}]]
         * ---------------------
         *
         * And in order to do this, the steps to be taken will be:
         *
         *   - Check that the iterated block starts with an 'open element' and ends with a 'close element'. If not,
         *     don't apply any of this.
         *   - Except for the first iteration, remove all whitespace after the 'open element', until the
         *     first '\n' (and remove that too).
         *   - Except for the last iteration, remove all whitespace after the last '\n' (not including it) and before
         *     the 'close element'.
         */

        int firstBodyEventCutPoint = -1;
        int lastBodyEventCutPoint = -1;

        final ITemplateEvent firstBodyEvent = iterArtifacts.iterationQueue.get(1); // we know there is at least one body event
        Text firstTextBodyEvent = null;
        if (iterArtifacts.iterationQueue.get(0) instanceof OpenElementTag && firstBodyEvent instanceof IText) {

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

        final ITemplateEvent lastBodyEvent = iterArtifacts.iterationQueue.get(iterArtifacts.iterationQueue.size() - 2);
        Text lastTextBodyEvent = null;
        if (firstBodyEventCutPoint >= 0 &&
                iterArtifacts.iterationQueue.get(iterArtifacts.iterationQueue.size() - 1) instanceof CloseElementTag && lastBodyEvent instanceof IText) {

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

            iterArtifacts.iterationFirstBodyEventIter0.setText(textFor0);
            iterArtifacts.iterationFirstBodyEventIterN.setText(textForN);
            iterArtifacts.iterationLastBodyEventIterMax.setText(textForMax);
            iterArtifacts.iterationLastBodyEventIterN.setText(textForN);
            return;
        }

        // At this point, we know the first and last body events are different objects

        iterArtifacts.iterationFirstBodyEventIter0.resetAsCloneOf(firstTextBodyEvent);
        iterArtifacts.iterationLastBodyEventIterMax.resetAsCloneOf(lastTextBodyEvent);

        if (firstBodyEventCutPoint == 0) {
            iterArtifacts.iterationFirstBodyEventIterN.resetAsCloneOf(firstTextBodyEvent);
        } else {
            iterArtifacts.iterationFirstBodyEventIterN.setText(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
        }

        if (lastBodyEventCutPoint == lastTextBodyEvent.length()) {
            iterArtifacts.iterationLastBodyEventIterN.resetAsCloneOf(lastTextBodyEvent);
        } else {
            iterArtifacts.iterationLastBodyEventIterN.setText(lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
        }


    }




    private static void prepareIterationQueueForIteration(
            final IterationArtifacts iterArtifacts, final int iterationIndex, final boolean last) {

        if (!iterArtifacts.performBodyFirstLastSwitch || (iterationIndex > 1 && !last)) {
            // No modifications to be done to the iteration queue here
            return;
        }

        final EngineEventQueue queue = iterArtifacts.iterationQueue;

        if (iterationIndex == 0) {
            ((Text)queue.get(1)).resetAsCloneOf(iterArtifacts.iterationFirstBodyEventIter0);
            ((Text)queue.get(queue.size() - 2)).resetAsCloneOf(iterArtifacts.iterationLastBodyEventIterN);
        }

        if (iterationIndex == 1) {
            ((Text)queue.get(1)).resetAsCloneOf(iterArtifacts.iterationFirstBodyEventIterN);
        }

        if (last) {
            ((Text)queue.get(queue.size() - 2)).resetAsCloneOf(iterArtifacts.iterationLastBodyEventIterMax);
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

        boolean suspended;
        EngineEventQueue queue;
        ElementProcessorIterator processorIterator;
        boolean queueProcessable;
        boolean discardEvent;
        BodyBehaviour bodyBehaviour;


        ExecLevelData(final IEngineConfiguration configuration, final TemplateMode templateMode) {
            super();
            this.queue = new EngineEventQueue(configuration, templateMode);
            this.processorIterator = new ElementProcessorIterator();
            reset();
        }

        void reset() {
            this.suspended = false;
            this.queue.reset();
            this.processorIterator.reset();
            this.queueProcessable = false;
            this.discardEvent = false;
            this.bodyBehaviour = BodyBehaviour.PROCESS;
        }

    }





    private static final class IterationSpec {

        private int fromModelLevel;
        private Text precedingWhitespace;
        private String iterVariableName;
        private String iterStatusVariableName;
        private Object iteratedObject;
        final EngineEventQueue iterationQueue;

        IterationSpec(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.iterationQueue = new EngineEventQueue(configuration, templateMode, 50);
            reset();
        }

        void reset() {
            this.fromModelLevel = Integer.MAX_VALUE;
            this.precedingWhitespace = null;
            this.iterVariableName = null;
            this.iterStatusVariableName = null;
            this.iteratedObject = null;
            this.iterationQueue.reset();
        }

    }


    private static final class IterationArtifacts {

        boolean performBodyFirstLastSwitch = false;
        final Text iterationFirstBodyEventIter0;
        final Text iterationFirstBodyEventIterN;
        final EngineEventQueue iterationQueue;
        final Text iterationLastBodyEventIterN;
        final Text iterationLastBodyEventIterMax;
        final EngineEventQueue suspendedQueue;
        final ElementProcessorIterator suspendedElementProcessorIterator;

        IterationArtifacts(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.iterationFirstBodyEventIter0 = new Text(configuration.getTextRepository());
            this.iterationFirstBodyEventIterN = new Text(configuration.getTextRepository());
            this.iterationQueue = new EngineEventQueue(configuration, templateMode, 50);
            this.iterationLastBodyEventIterN = new Text(configuration.getTextRepository());
            this.iterationLastBodyEventIterMax = new Text(configuration.getTextRepository());
            this.suspendedQueue = new EngineEventQueue(configuration, templateMode, 5);
            this.suspendedElementProcessorIterator = new ElementProcessorIterator();
        }

    }



    private static final class ElementModelSpec {

        private int fromModelLevel;
        final EngineEventQueue modelQueue;

        ElementModelSpec(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.modelQueue = new EngineEventQueue(configuration, templateMode, 50);
            reset();
        }

        void reset() {
            this.fromModelLevel = Integer.MAX_VALUE;
            this.modelQueue.reset();
        }

    }


    private static final class ElementModelArtifacts {

        final EngineEventQueue modelQueue;

        ElementModelArtifacts(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.modelQueue = new EngineEventQueue(configuration, templateMode, 50);
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