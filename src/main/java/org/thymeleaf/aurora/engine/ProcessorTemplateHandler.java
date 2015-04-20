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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.context.ILocalVariableAwareVariablesMap;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.context.IVariablesMap;
import org.thymeleaf.aurora.model.IAutoCloseElementTag;
import org.thymeleaf.aurora.model.IAutoOpenElementTag;
import org.thymeleaf.aurora.model.ICDATASection;
import org.thymeleaf.aurora.model.ICloseElementTag;
import org.thymeleaf.aurora.model.IComment;
import org.thymeleaf.aurora.model.IDocType;
import org.thymeleaf.aurora.model.IOpenElementTag;
import org.thymeleaf.aurora.model.IProcessingInstruction;
import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.model.IText;
import org.thymeleaf.aurora.model.IUnmatchedCloseElementTag;
import org.thymeleaf.aurora.model.IXMLDeclaration;
import org.thymeleaf.aurora.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.aurora.processor.comment.ICommentProcessor;
import org.thymeleaf.aurora.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.aurora.processor.element.IElementNodeProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.element.IElementTagProcessor;
import org.thymeleaf.aurora.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.aurora.processor.text.ITextProcessor;
import org.thymeleaf.aurora.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.TemplateProcessingException;
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

    // Structure handlers are reusable objects that will be used by processors in order to instruct the engine to
    // do things with the processed structures themselves (things that cannot be directly done from the processors like
    // removing structures or iterating elements)
    private final ElementStructureHandler elementStructureHandler;
    private final CDATASectionStructureHandler cdataSectionStructureHandler;
    private final CommentStructureHandler commentStructureHandler;
    private final DocTypeStructureHandler docTypeStructureHandler;
    private final ProcessingInstructionStructureHandler processingInstructionStructureHandler;
    private final TextStructureHandler textStructureHandler;
    private final XMLDeclarationStructureHandler xmlDeclarationStructureHandler;

    private ITemplateProcessingContext processingContext;
    private IEngineConfiguration configuration;
    private TemplateMode templateMode;

    private ILocalVariableAwareVariablesMap variablesMap;

    private boolean hasCDATASectionProcessors = false;
    private boolean hasCommentProcessors = false;
    private boolean hasDocTypeProcessors = false;
    private boolean hasProcessingInstructionProcessors = false;
    private boolean hasTextProcessors = false;
    private boolean hasXMLDeclarationProcessors = false;

    private int markupLevel = 0;

    private int skipMarkupFromLevel = Integer.MAX_VALUE;
    private LevelArray skipCloseTagLevels = new LevelArray(5);

    // We will have just one (reusable) instance of the element processor iterator, which will take into account the
    // fact that the processors applicable to an element might change during the execution of other processors, because
    // the applicability of an element processor is based on its attributes, and these might change in runtime
    private final ElementProcessorIterator elementProcessorIterator = new ElementProcessorIterator();
    // These arrays will be initialized with all the registered processors for the different kind of non-element
    // processors. This is done so because non-element processors will not change during the execution of the engine
    // (whereas element processors can). And they are kept in the form of an array because they will be faster to
    // iterate than asking everytime the configuration object for the Set of processors and creating an iterator for it
    private ICDATASectionProcessor[] cdataSectionProcessors = null;
    private ICommentProcessor[] commentProcessors = null;
    private IDocTypeProcessor[] docTypeProcessors = null;
    private IProcessingInstructionProcessor[] processingInstructionProcessors = null;
    private ITextProcessor[] textProcessors = null;
    private IXMLDeclarationProcessor[] xmlDeclarationProcessors = null;

    // This should only be modified by means of the 'increaseHandlerExecLevel' and 'decreaseHandlerExecLevel' methods
    private int handlerExecLevel = -1;

    // These structures will be indexed by the handlerExecLevel, which allows structures to be used across different levels of nesting
    private EngineEventQueue[] eventQueues = null;

    // Replacing a body with a text is so common we want to avoid creating too many objects for that
    private Text textBodyReplacementBuffer = null;

    // Used for suspending the execution of a tag and replacing it for a different event (perhaps after building a
    // queue) or iterating the suspended event and its body.
    private boolean suspended = false;
    private SuspensionSpec suspensionSpec; // Will be initialized once we have the processing context
    private boolean gatheringIteration = false;
    private IterationSpec iterationSpec = null;

    // Used during iteration, in order to not create too many queue and processor objects (which in turn might
    // create too many event buffer objects)
    private IterationArtifacts[] iterationArtifacts = null;
    private int iterationArtifactsIndex = 0;

    // Used in the cases when a standalone tag is converted into an open+close one (i.e. a body is added).
    private OpenElementTag[] standaloneOpenTagBuffers = null;
    private CloseElementTag[] standaloneCloseTagBuffers = null;
    private Text[] standaloneTextBuffers = null;
    private int standaloneTagBuffersIndex = 0;




    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {
        super();
        this.elementStructureHandler = new ElementStructureHandler();
        this.cdataSectionStructureHandler = new CDATASectionStructureHandler();
        this.commentStructureHandler = new CommentStructureHandler();
        this.docTypeStructureHandler = new DocTypeStructureHandler();
        this.processingInstructionStructureHandler = new ProcessingInstructionStructureHandler();
        this.textStructureHandler = new TextStructureHandler();
        this.xmlDeclarationStructureHandler = new XMLDeclarationStructureHandler();
    }




    @Override
    public void setProcessingContext(final ITemplateProcessingContext processingContext) {

        super.setProcessingContext(processingContext);

        this.processingContext = processingContext;
        Validate.notNull(this.processingContext, "Processing Context cannot be null");
        Validate.notNull(this.processingContext.getTemplateMode(), "Template Mode returned by Processing Context cannot be null");

        this.configuration = processingContext.getConfiguration();
        Validate.notNull(this.configuration, "Engine Configuration returned by Processing Context cannot be null");
        Validate.notNull(this.configuration.getTextRepository(), "Text Repository returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getElementDefinitions(), "Element Definitions returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getAttributeDefinitions(), "Attribute Definitions returned by the Engine Configuration cannot be null");

        this.templateMode = this.processingContext.getTemplateMode(); // Just a way to avoid doing the call each time

        final IVariablesMap variablesMap = processingContext.getVariablesMap();
        Validate.notNull(variablesMap, "Variables Map returned by Processing Context cannot be null");
        if (variablesMap instanceof ILocalVariableAwareVariablesMap) {
            this.variablesMap = (ILocalVariableAwareVariablesMap) variablesMap;
        } else {
            logger.warn("Unknown implementation of the " + IVariablesMap.class.getName() + " interface: " +
                        variablesMap.getClass().getName() + ". Local variable support will be DISABLED (this " +
                        "includes iteration, target selection and inlining)");
        }

        // Buffer used for text-shaped body replacement in tags (very common operation)
        this.textBodyReplacementBuffer = new Text(this.configuration.getTextRepository());

        // Specs containing all the info required for suspending the execution of a processor in order to e.g. change
        // handling method (standalone -> open) or start caching an iteration
        this.suspensionSpec = new SuspensionSpec(this.templateMode, this.configuration);
        this.iterationSpec = new IterationSpec(this.templateMode, this.configuration);

        // Flags used for quickly determining if a non-element structure might have to be processed or not
        this.hasCDATASectionProcessors = !this.configuration.getCDATASectionProcessors(this.templateMode).isEmpty();
        this.hasCommentProcessors = !this.configuration.getCommentProcessors(this.templateMode).isEmpty();
        this.hasDocTypeProcessors = !this.configuration.getDocTypeProcessors(this.templateMode).isEmpty();
        this.hasProcessingInstructionProcessors = !this.configuration.getProcessingInstructionProcessors(this.templateMode).isEmpty();
        this.hasTextProcessors = !this.configuration.getTextProcessors(this.templateMode).isEmpty();
        this.hasXMLDeclarationProcessors = !this.configuration.getXMLDeclarationProcessors(this.templateMode).isEmpty();

        // Initialize arrays containing the processors for all the non-element structures (do not change during execution)
        final Set<ICDATASectionProcessor> cdataSectionProcessorSet = this.configuration.getCDATASectionProcessors(this.templateMode);
        final Set<ICommentProcessor> commentProcessorSet = this.configuration.getCommentProcessors(this.templateMode);
        final Set<IDocTypeProcessor> docTypeProcessorSet = this.configuration.getDocTypeProcessors(this.templateMode);
        final Set<IProcessingInstructionProcessor> processingInstructionProcessorSet = this.configuration.getProcessingInstructionProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessorSet = this.configuration.getTextProcessors(this.templateMode);
        final Set<IXMLDeclarationProcessor> xmlDeclarationProcessorSet = this.configuration.getXMLDeclarationProcessors(this.templateMode);
        this.cdataSectionProcessors = cdataSectionProcessorSet.toArray(new ICDATASectionProcessor[cdataSectionProcessorSet.size()]);
        this.commentProcessors = commentProcessorSet.toArray(new ICommentProcessor[commentProcessorSet.size()]);
        this.docTypeProcessors = docTypeProcessorSet.toArray(new IDocTypeProcessor[docTypeProcessorSet.size()]);
        this.processingInstructionProcessors = processingInstructionProcessorSet.toArray(new IProcessingInstructionProcessor[processingInstructionProcessorSet.size()]);
        this.textProcessors = textProcessorSet.toArray(new ITextProcessor[textProcessorSet.size()]);
        this.xmlDeclarationProcessors = xmlDeclarationProcessorSet.toArray(new IXMLDeclarationProcessor[xmlDeclarationProcessorSet.size()]);

    }




    private void increaseHandlerExecLevel() {

        this.handlerExecLevel++;

        if (this.eventQueues == null) {
            // No arrays created yet - must create

            this.eventQueues = new EngineEventQueue[3];
            Arrays.fill(this.eventQueues, null);

        }

        if (this.eventQueues.length == this.handlerExecLevel) {
            // We need to grow the arrays

            final EngineEventQueue[] newEventQueues = new EngineEventQueue[this.handlerExecLevel + 3];
            Arrays.fill(newEventQueues, null);
            System.arraycopy(this.eventQueues, 0, newEventQueues, 0, this.handlerExecLevel);
            this.eventQueues = newEventQueues;

        }

        if (this.eventQueues[this.handlerExecLevel] == null) {
            this.eventQueues[this.handlerExecLevel] = new EngineEventQueue(this.templateMode, this.configuration);
        } else {
            this.eventQueues[this.handlerExecLevel].reset();
        }

    }


    private void decreaseHandlerExecLevel() {
        this.handlerExecLevel--;
    }




    private void ensureStandaloneTagBuffers() {
        if (this.standaloneOpenTagBuffers == null) {
            this.standaloneOpenTagBuffers = new OpenElementTag[2];
            this.standaloneCloseTagBuffers = new CloseElementTag[2];
            this.standaloneTextBuffers = new Text[2];
            Arrays.fill(this.standaloneOpenTagBuffers, null);
            Arrays.fill(this.standaloneCloseTagBuffers, null);
            Arrays.fill(this.standaloneTextBuffers, null);
        }
        if (this.standaloneTagBuffersIndex == this.standaloneOpenTagBuffers.length) {
            final OpenElementTag[] newStandaloneOpenTagBuffers = new OpenElementTag[this.standaloneOpenTagBuffers.length + 2];
            final CloseElementTag[] newStandaloneCloseTagBuffers = new CloseElementTag[this.standaloneCloseTagBuffers.length + 2];
            final Text[] newStandaloneTextBuffers = new Text[this.standaloneTextBuffers.length + 2];
            Arrays.fill(newStandaloneOpenTagBuffers, null);
            Arrays.fill(newStandaloneCloseTagBuffers, null);
            Arrays.fill(newStandaloneTextBuffers, null);
            System.arraycopy(this.standaloneOpenTagBuffers, 0, newStandaloneOpenTagBuffers, 0, this.standaloneOpenTagBuffers.length);
            System.arraycopy(this.standaloneCloseTagBuffers, 0, newStandaloneCloseTagBuffers, 0, this.standaloneCloseTagBuffers.length);
            System.arraycopy(this.standaloneTextBuffers, 0, newStandaloneTextBuffers, 0, this.standaloneTextBuffers.length);
            this.standaloneOpenTagBuffers = newStandaloneOpenTagBuffers;
            this.standaloneCloseTagBuffers = newStandaloneCloseTagBuffers;
            this.standaloneTextBuffers = newStandaloneTextBuffers;
        }
        if (this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex] == null) {
            this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex] =
                    new OpenElementTag(this.templateMode,
                            this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
            this.standaloneCloseTagBuffers[this.standaloneTagBuffersIndex] =
                    new CloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
            this.standaloneTextBuffers[this.standaloneTagBuffersIndex] =
                    new Text(this.configuration.getTextRepository());
        }
    }




    @Override
    public void handleDocumentStart(final long startTimeNanos, final int line, final int col) {
        super.handleDocumentStart(startTimeNanos, line, col);
        increaseHandlerExecLevel();
    }




    @Override
    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {
        decreaseHandlerExecLevel();
        super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }




    @Override
    public void handleText(final IText itext) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(Text.asEngineText(this.configuration, itext, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasTextProcessors) {
            super.handleText(itext);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.textProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.textProcessors[i].process(this.processingContext, itext, this.textStructureHandler);

            if (this.textStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.textStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.textStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.textStructureHandler.removeText) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleText(itext);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }



    @Override
    public void handleComment(final IComment icomment) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(Comment.asEngineComment(this.configuration, icomment, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasCommentProcessors) {
            super.handleComment(icomment);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.commentProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.commentProcessors[i].process(this.processingContext, icomment, this.commentStructureHandler);

            if (this.commentStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.commentStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.commentStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.commentStructureHandler.removeComment) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleComment(icomment);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }

    
    @Override
    public void handleCDATASection(final ICDATASection icdataSection) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(CDATASection.asEngineCDATASection(this.configuration, icdataSection, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasCDATASectionProcessors) {
            super.handleCDATASection(icdataSection);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.cdataSectionProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.cdataSectionProcessors[i].process(this.processingContext, icdataSection, this.cdataSectionStructureHandler);

            if (this.cdataSectionStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.cdataSectionStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.cdataSectionStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.cdataSectionStructureHandler.removeCDATASection) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleCDATASection(icdataSection);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag istandaloneElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    StandaloneElementTag.asEngineStandaloneElementTag(
                            this.templateMode, this.configuration, istandaloneElementTag, true));
            return;
        }


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!this.suspended && !istandaloneElementTag.hasAssociatedProcessors()) {
            super.handleStandaloneElement(istandaloneElementTag);
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        final StandaloneElementTag standaloneElementTag =
                StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, istandaloneElementTag, false);


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean tagRemoved = false; // If the tag is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }


        /*
         * INITIALIZE THE EXECUTION LEVEL depending on whether we have a suspended a previous execution or not
         */
        if (!this.suspended) {

            /*
             * INITIALIZE THE PROCESSOR ITERATOR that will be used for executing all the processors
             */
            this.elementProcessorIterator.reset();

        } else {
            // Execution of a tag was suspended, we need to recover the data

            /*
             * RETRIEVE THE QUEUE TO BE USED, potentially already containing some nodes. And also the flags.
             */
            queue.resetAsCloneOf(this.suspensionSpec.suspendedQueue);
            queueProcessable = this.suspensionSpec.queueProcessable;
            this.elementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);
            this.suspended = false;
            this.suspensionSpec.reset();

            // Note we will not increase the VariablesMap level here, as are keeping the level from the suspended execution

        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!tagRemoved && (processor = this.elementProcessorIterator.next(standaloneElementTag)) != null) {

            this.elementStructureHandler.reset();

            if (processor instanceof IElementTagProcessor) {

                final IElementTagProcessor elementProcessor = ((IElementTagProcessor)processor);
                elementProcessor.process(this.processingContext, standaloneElementTag, this.elementStructureHandler);

                if (this.elementStructureHandler.setLocalVariable) {
                    if (this.variablesMap != null) {
                        this.variablesMap.putAll(this.elementStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementStructureHandler.removeLocalVariable) {
                    if (this.variablesMap != null) {
                        for (final String variableName : this.elementStructureHandler.removedLocalVariableNames) {
                            this.variablesMap.remove(variableName);
                        }
                    }
                }

                if (this.elementStructureHandler.setSelectionTarget) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setSelectionTarget(this.elementStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementStructureHandler.setTextInliningActive) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setTextInliningActive(this.elementStructureHandler.setTextInliningActiveValue);
                    }
                }

                if (this.elementStructureHandler.iterateElement) {

                    // Set the iteration info in order to start gathering all iterated events
                    this.gatheringIteration = true;
                    this.iterationSpec.fromMarkupLevel = this.markupLevel + 1;
                    this.iterationSpec.iterVariableName = this.elementStructureHandler.iterVariableName;
                    this.iterationSpec.iterStatusVariableName = this.elementStructureHandler.iterStatusVariableName;
                    this.iterationSpec.iteratedObject = this.elementStructureHandler.iteratedObject;
                    this.iterationSpec.iterationQueue.reset();

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.bodyRemoved = false;
                    this.suspensionSpec.queueProcessable = queueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Add this standalone tag to the iteration queue
                    this.iterationSpec.iterationQueue.add(standaloneElementTag.cloneElementTag());

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE VARIABLES MAP LEVEL -- we need the variables stored there, if any

                    // Process the queue by iterating it
                    processIteration();

                    // Decrease the variables map level
                    if (this.variablesMap != null) {
                        this.variablesMap.decreaseLevel();
                    }

                    return;

                } else if (this.elementStructureHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    ensureStandaloneTagBuffers();
                    final OpenElementTag openTag = this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex];
                    final CloseElementTag closeTag = this.standaloneCloseTagBuffers[this.standaloneTagBuffersIndex];
                    openTag.resetAsCloneOf(standaloneElementTag);
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the text node that will be added to the queue, that we will suspend
                    // Note we are using a specific buffer for these cases, because we want to avoid cloning, and
                    // we cannot use the normal 'textBodyReplacementBuffer' because it might be needed too during
                    // the handling of the open/close events or any of its sub-events (e.g. nested queues). So the
                    // best option is take one from our own, specialized standalone-oriented buffer in order to limit
                    // the amount of objects created in these cases
                    final Text text = this.standaloneTextBuffers[this.standaloneTagBuffersIndex];
                    text.setText(this.elementStructureHandler.setBodyTextValue);
                    queue.add(text);

                    // We are done with using the standalone buffers, so increase the index
                    this.standaloneTagBuffersIndex++;

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.bodyRemoved = false;
                    this.suspensionSpec.queueProcessable = this.elementStructureHandler.setBodyTextProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE VARIABLES MAP LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    // We can free the buffers we just used
                    this.standaloneTagBuffersIndex--;

                    // Decrease the variables map level
                    if (this.variablesMap != null) {
                        this.variablesMap.decreaseLevel();
                    }

                    return;

                } else if (this.elementStructureHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    ensureStandaloneTagBuffers();
                    final OpenElementTag openTag = this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex];
                    final CloseElementTag closeTag = this.standaloneCloseTagBuffers[this.standaloneTagBuffersIndex];
                    openTag.resetAsCloneOf(standaloneElementTag);
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the queue (that we will suspend)
                    queue.addQueue(this.elementStructureHandler.setBodyQueueValue, true); // we need to clone the queue!

                    // We are done with using the standalone buffers, so increase the index
                    this.standaloneTagBuffersIndex++;

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.bodyRemoved = false;
                    this.suspensionSpec.queueProcessable = this.elementStructureHandler.setBodyQueueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE VARIABLES MAP LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    // We can free the buffers we just used
                    this.standaloneTagBuffersIndex--;

                    // Decrease the variables map level
                    if (this.variablesMap != null) {
                        this.variablesMap.decreaseLevel();
                    }

                    return;

                } else if (this.elementStructureHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBodyReplacementBuffer.setText(this.elementStructureHandler.replaceWithTextValue);
                    queue.add(this.textBodyReplacementBuffer);

                    tagRemoved = true;

                } else if (this.elementStructureHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithQueueProcessable;

                    queue.addQueue(this.elementStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                    tagRemoved = true;

                } else if (this.elementStructureHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagRemoved = true;

                } else if (this.elementStructureHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagRemoved = true;

                }

            } else if (processor instanceof IElementNodeProcessor) {
                throw new UnsupportedOperationException("Support for Node processors not implemented yet");
            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither an element nor a Node processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!tagRemoved) {
            super.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE VARIABLES MAP LEVEL once we have executed all the processors (and maybe a body if we added
         * one to the tag converting it into an open tag)
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleOpenElement(final IOpenElementTag iopenElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, iopenElementTag, true));
            this.markupLevel++;
            return;
        }


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!this.suspended && !iopenElementTag.hasAssociatedProcessors()) {
            super.handleOpenElement(iopenElementTag);
            this.markupLevel++;
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        final OpenElementTag openElementTag =
                OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, iopenElementTag, false);


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean tagRemoved = false; // If the tag is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not
        boolean bodyRemoved = false; // If the body of this tag should be removed, we must signal it accordingly at the engine


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }


        /*
         * INITIALIZE THE EXECUTION LEVEL depending on whether we have a suspended a previous execution or not
         */
        if (!this.suspended) {

            /*
             * INITIALIZE THE PROCESSOR ITERATOR that will be used for executing all the processors
             */
            this.elementProcessorIterator.reset();

        } else {
            // Execution of a tag was suspended, we need to recover the data

            /*
             * RETRIEVE THE QUEUE TO BE USED, potentially already containing some nodes. And also the flags.
             */
            queue.resetAsCloneOf(this.suspensionSpec.suspendedQueue);
            queueProcessable = this.suspensionSpec.queueProcessable;
            bodyRemoved = this.suspensionSpec.bodyRemoved;
            this.elementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);
            this.suspended = false;
            this.suspensionSpec.reset();

            // Note we will not increase the VariablesMap level here, as are keeping the level from the suspended execution

        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!tagRemoved && (processor = this.elementProcessorIterator.next(openElementTag)) != null) {

            this.elementStructureHandler.reset();

            if (processor instanceof IElementTagProcessor) {

                final IElementTagProcessor elementProcessor = ((IElementTagProcessor)processor);
                elementProcessor.process(this.processingContext, openElementTag, this.elementStructureHandler);

                if (this.elementStructureHandler.setLocalVariable) {
                    if (this.variablesMap != null) {
                        this.variablesMap.putAll(this.elementStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementStructureHandler.removeLocalVariable) {
                    if (this.variablesMap != null) {
                        for (final String variableName : this.elementStructureHandler.removedLocalVariableNames) {
                            this.variablesMap.remove(variableName);
                        }
                    }
                }

                if (this.elementStructureHandler.setSelectionTarget) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setSelectionTarget(this.elementStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementStructureHandler.setTextInliningActive) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setTextInliningActive(this.elementStructureHandler.setTextInliningActiveValue);
                    }
                }

                if (this.elementStructureHandler.iterateElement) {

                    // Set the iteration info in order to start gathering all iterated events
                    this.gatheringIteration = true;
                    this.iterationSpec.fromMarkupLevel = this.markupLevel + 1;
                    this.iterationSpec.iterVariableName = this.elementStructureHandler.iterVariableName;
                    this.iterationSpec.iterStatusVariableName = this.elementStructureHandler.iterStatusVariableName;
                    this.iterationSpec.iteratedObject = this.elementStructureHandler.iteratedObject;
                    this.iterationSpec.iterationQueue.reset();

                    // Before suspending the queue, we have to check if it is the result of a "setBodyText", in
                    // which case it will contain only one non-cloned node: the text buffer. And we will need
                    // to clone that buffer before suspending the queue to avoid nasty interactions during iteration
                    if (queue.size() == 1 && queue.get(0) == this.textBodyReplacementBuffer) {
                        // Replace the text buffer with a clone
                        queue.reset();
                        queue.add(this.textBodyReplacementBuffer.cloneNode());
                    }

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.bodyRemoved = bodyRemoved;
                    this.suspensionSpec.queueProcessable = queueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // The first event in the new iteration query
                    this.iterationSpec.iterationQueue.add(openElementTag.cloneElementTag());

                    // Increase markup level, as normal with open tags
                    this.markupLevel++;

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE VARIABLES MAP LEVEL -- that's the responsibility of the close event

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementStructureHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.setBodyTextProcessable;

                    // For now we will not be cloning the buffer and just hoping it will be executed as is. This is
                    // the most common case (th:text) and this will save us a good number of Text nodes. But note that
                    // if this element is iterated AFTER we set this, we will need to clone this node before suspending
                    // the queue, or we might have nasting interactions with each of the subsequent iterations
                    this.textBodyReplacementBuffer.setText(this.elementStructureHandler.setBodyTextValue);
                    queue.add(this.textBodyReplacementBuffer);

                    bodyRemoved = true;

                } else if (this.elementStructureHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.setBodyQueueProcessable;

                    queue.addQueue(this.elementStructureHandler.setBodyQueueValue, true); // we need to clone the queue!

                    bodyRemoved = true;

                } else if (this.elementStructureHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBodyReplacementBuffer.setText(this.elementStructureHandler.replaceWithTextValue);
                    queue.add(this.textBodyReplacementBuffer);

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithQueueProcessable;

                    queue.addQueue(this.elementStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagRemoved = true;

                }

            } else if (processor instanceof IElementNodeProcessor) {
                // TODO Implement Node processors and Node DOM structure handling, and copy those to the "autoOpen" events
                throw new UnsupportedOperationException("Support for Node processors not implemented yet");
            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither an element nor a Node processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MARKUP LEVEL RIGHT AFTERWARDS
         */
        if (!tagRemoved) {
            super.handleOpenElement(openElementTag);
        }


        /*
         * INCREASE THE MARKUP LEVEL to the value that will be applied to the tag's bodies
         */
        this.markupLevel++;


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (bodyRemoved) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel;
        }


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (tagRemoved) {
            this.skipCloseTagLevels.add(this.markupLevel - 1);
            // We cannot decrease here the variables map level because we aren't actually decreasing the markup
            // level until we find the corresponding close tag
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleAutoOpenElement(final IAutoOpenElementTag iautoOpenElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    AutoOpenElementTag.asEngineAutoOpenElementTag(this.templateMode, this.configuration, iautoOpenElementTag, true));
            this.markupLevel++;
            return;
        }


        /*
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!this.suspended && !iautoOpenElementTag.hasAssociatedProcessors()) {
            super.handleAutoOpenElement(iautoOpenElementTag);
            this.markupLevel++;
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }
            return;
        }


        /*
         * CAST (WITHOUT CLONING) TO ENGINE-SPECIFIC IMPLEMENTATION, which will ease the handling of the structure during processing
         */
        final AutoOpenElementTag autoOpenElementTag =
                AutoOpenElementTag.asEngineAutoOpenElementTag(this.templateMode, this.configuration, iautoOpenElementTag, false);


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean tagRemoved = false; // If the tag is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not
        boolean bodyRemoved = false; // If the body of this tag should be removed, we must signal it accordingly at the engine


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }


        /*
         * INITIALIZE THE EXECUTION LEVEL depending on whether we have a suspended a previous execution or not
         */
        if (!this.suspended) {

            /*
             * INITIALIZE THE PROCESSOR ITERATOR that will be used for executing all the processors
             */
            this.elementProcessorIterator.reset();

        } else {
            // Execution of a tag was suspended, we need to recover the data

            /*
             * RETRIEVE THE QUEUE TO BE USED, potentially already containing some nodes. And also the flags.
             */
            queue.resetAsCloneOf(this.suspensionSpec.suspendedQueue);
            queueProcessable = this.suspensionSpec.queueProcessable;
            bodyRemoved = this.suspensionSpec.bodyRemoved;
            this.elementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);
            this.suspended = false;
            this.suspensionSpec.reset();

            // Note we will not increase the VariablesMap level here, as are keeping the level from the suspended execution

        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!tagRemoved && (processor = this.elementProcessorIterator.next(autoOpenElementTag)) != null) {

            this.elementStructureHandler.reset();

            if (processor instanceof IElementTagProcessor) {

                final IElementTagProcessor elementProcessor = ((IElementTagProcessor)processor);
                elementProcessor.process(this.processingContext, autoOpenElementTag, this.elementStructureHandler);

                if (this.elementStructureHandler.setLocalVariable) {
                    if (this.variablesMap != null) {
                        this.variablesMap.putAll(this.elementStructureHandler.addedLocalVariables);
                    }
                }

                if (this.elementStructureHandler.removeLocalVariable) {
                    if (this.variablesMap != null) {
                        for (final String variableName : this.elementStructureHandler.removedLocalVariableNames) {
                            this.variablesMap.remove(variableName);
                        }
                    }
                }

                if (this.elementStructureHandler.setSelectionTarget) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setSelectionTarget(this.elementStructureHandler.selectionTargetObject);
                    }
                }

                if (this.elementStructureHandler.setTextInliningActive) {
                    if (this.variablesMap != null) {
                        this.variablesMap.setTextInliningActive(this.elementStructureHandler.setTextInliningActiveValue);
                    }
                }

                if (this.elementStructureHandler.iterateElement) {

                    // Set the iteration info in order to start gathering all iterated events
                    this.gatheringIteration = true;
                    this.iterationSpec.fromMarkupLevel = this.markupLevel + 1;
                    this.iterationSpec.iterVariableName = this.elementStructureHandler.iterVariableName;
                    this.iterationSpec.iterStatusVariableName = this.elementStructureHandler.iterStatusVariableName;
                    this.iterationSpec.iteratedObject = this.elementStructureHandler.iteratedObject;
                    this.iterationSpec.iterationQueue.reset();

                    // Before suspending the queue, we have to check if it is the result of a "setBodyText", in
                    // which case it will contain only one non-cloned node: the text buffer. And we will need
                    // to clone that buffer before suspending the queue to avoid nasty interactions during iteration
                    if (queue.size() == 1 && queue.get(0) == this.textBodyReplacementBuffer) {
                        // Replace the text buffer with a clone
                        queue.reset();
                        queue.add(this.textBodyReplacementBuffer.cloneNode());
                    }

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.bodyRemoved = bodyRemoved;
                    this.suspensionSpec.queueProcessable = queueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // The first event in the new iteration query
                    this.iterationSpec.iterationQueue.add(autoOpenElementTag.cloneElementTag());

                    // Increase markup level, as normal with open tags
                    this.markupLevel++;

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE VARIABLES MAP LEVEL -- that's the responsibility of the close event

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementStructureHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.setBodyTextProcessable;

                    // For now we will not be cloning the buffer and just hoping it will be executed as is. This is
                    // the most common case (th:text) and this will save us a good number of Text nodes. But note that
                    // if this element is iterated AFTER we set this, we will need to clone this node before suspending
                    // the queue, or we might have nasting interactions with each of the subsequent iterations
                    this.textBodyReplacementBuffer.setText(this.elementStructureHandler.setBodyTextValue);
                    queue.add(this.textBodyReplacementBuffer);

                    bodyRemoved = true;

                } else if (this.elementStructureHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.setBodyQueueProcessable;

                    queue.addQueue(this.elementStructureHandler.setBodyQueueValue, true); // we need to clone the queue!

                    bodyRemoved = true;

                } else if (this.elementStructureHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBodyReplacementBuffer.setText(this.elementStructureHandler.replaceWithTextValue);
                    queue.add(this.textBodyReplacementBuffer);

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementStructureHandler.replaceWithQueueProcessable;

                    queue.addQueue(this.elementStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.elementStructureHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagRemoved = true;

                }

            } else if (processor instanceof IElementNodeProcessor) {
                // TODO Implement Node processors and Node DOM structure handling, and copy those to the "autoOpen" events
                throw new UnsupportedOperationException("Support for Node processors not implemented yet");
            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                                " which is neither an element nor a Node processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MARKUP LEVEL RIGHT AFTERWARDS
         */
        if (!tagRemoved) {
            super.handleAutoOpenElement(autoOpenElementTag);
        }


        /*
         * INCREASE THE MARKUP LEVEL to the value that will be applied to the tag's bodies
         */
        this.markupLevel++;


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (bodyRemoved) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel;
        }


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (tagRemoved) {
            this.skipCloseTagLevels.add(this.markupLevel - 1);
            // We cannot decrease here the variables map level because we aren't actually decreasing the markup
            // level until we find the corresponding close tag
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleCloseElement(final ICloseElementTag icloseElementTag) {

        /*
         * DECREASE THE MARKUP LEVEL, as only the body of elements should be considered in a higher level
         */
        this.markupLevel--;

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));
            return;
        }

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ITERATION, and in such case, process it
         */
        if (this.gatheringIteration && this.markupLevel + 1 == this.iterationSpec.fromMarkupLevel) {

            // Add the last tag: the closing one
            this.iterationSpec.iterationQueue.add(
                    CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, icloseElementTag, true));

            // Process the queue by iterating it
            processIteration();

            // Decrease the variables map level
            if (this.variablesMap != null) {
                this.variablesMap.decreaseLevel();
            }

            return;

        }

        /*
         * DECREASE THE VARIABLES MAP LEVEL, once we know this tag was not part of a block of discarded markup
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }

        /*
         * CHECK WHETHER WE SHOULD KEEP SKIPPING MARKUP or we just got to the end of the discarded block
         */
        if (this.markupLevel + 1 == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        /*
         * CALL THE NEXT HANDLER in the chain
         */
        super.handleCloseElement(icloseElementTag);

    }




    @Override
    public void handleAutoCloseElement(final IAutoCloseElementTag iautoCloseElementTag) {

        /*
         * DECREASE THE MARKUP LEVEL, as only the body of elements should be considered in a higher level
         */
        this.markupLevel--;

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    AutoCloseElementTag.asEngineAutoCloseElementTag(this.templateMode, this.configuration, iautoCloseElementTag, true));
            return;
        }

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ITERATION, and in such case, process it
         */
        if (this.gatheringIteration && this.markupLevel + 1 == this.iterationSpec.fromMarkupLevel) {

            // Add the last tag: the closing one
            this.iterationSpec.iterationQueue.add(
                    AutoCloseElementTag.asEngineAutoCloseElementTag(this.templateMode, this.configuration, iautoCloseElementTag, true));

            // Process the queue by iterating it
            processIteration();

            // Decrease the variables map level
            if (this.variablesMap != null) {
                this.variablesMap.decreaseLevel();
            }

            return;

        }

        /*
         * DECREASE THE VARIABLES MAP LEVEL, once we know this tag was not part of a block of discarded markup
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }

        /*
         * CHECK WHETHER WE SHOULD KEEP SKIPPING MARKUP or we just got to the end of the discarded block
         */
        if (this.markupLevel + 1 == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        /*
         * CALL THE NEXT HANDLER in the chain
         */
        super.handleAutoCloseElement(iautoCloseElementTag);

    }




    @Override
    public void handleUnmatchedCloseElement(final IUnmatchedCloseElementTag iunmatchedCloseElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    UnmatchedCloseElementTag.asEngineUnmatchedCloseElementTag(this.templateMode, this.configuration, iunmatchedCloseElementTag, true));
            return;
        }


        /*
         * -------------------------------------------------------------------------------------------------
         * THERE IS NOTHING ELSE THAT SHOULD BE DONE WITH AN UNMATCHED CLOSE ELEMENT. No processors apply...
         * -------------------------------------------------------------------------------------------------
         */


        /*
         * CALL THE NEXT HANDLER in the chain
         */
        super.handleUnmatchedCloseElement(iunmatchedCloseElementTag);

    }




    @Override
    public void handleDocType(final IDocType idocType) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(DocType.asEngineDocType(this.configuration, idocType, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasDocTypeProcessors) {
            super.handleDocType(idocType);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.docTypeProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.docTypeProcessors[i].process(this.processingContext, idocType, this.docTypeStructureHandler);

            if (this.docTypeStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.docTypeStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.docTypeStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.docTypeStructureHandler.removeDocType) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleDocType(idocType);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }

    
    
    
    @Override
    public void handleXMLDeclaration(final IXMLDeclaration ixmlDeclaration) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    XMLDeclaration.asEngineXMLDeclaration(this.configuration, ixmlDeclaration, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasXMLDeclarationProcessors) {
            super.handleXMLDeclaration(ixmlDeclaration);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.xmlDeclarationProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.xmlDeclarationProcessors[i].process(this.processingContext, ixmlDeclaration, this.xmlDeclarationStructureHandler);

            if (this.xmlDeclarationStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.xmlDeclarationStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.xmlDeclarationStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.xmlDeclarationStructureHandler.removeXMLDeclaration) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleXMLDeclaration(ixmlDeclaration);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction iprocessingInstruction) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.gatheringIteration && this.markupLevel >= this.iterationSpec.fromMarkupLevel) {
            this.iterationSpec.iterationQueue.add(
                    ProcessingInstruction.asEngineProcessingInstruction(this.configuration, iprocessingInstruction, true));
            return;
        }


        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasProcessingInstructionProcessors) {
            super.handleProcessingInstruction(iprocessingInstruction);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean structureRemoved = false; // If the structure is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.processingInstructionProcessors.length;
        for (int i = 0; !structureRemoved && i < processorsLen; i++) {

            this.processingInstructionProcessors[i].process(this.processingContext, iprocessingInstruction, this.processingInstructionStructureHandler);

            if (this.processingInstructionStructureHandler.replaceWithQueue) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.processingInstructionStructureHandler.replaceWithQueueProcessable;

                queue.addQueue(this.processingInstructionStructureHandler.replaceWithQueueValue, true); // we need to clone the queue!

                structureRemoved = true;

            } else if (this.processingInstructionStructureHandler.removeProcessingInstruction) {

                queue.reset(); // Remove any previous results on the queue

                structureRemoved = true;

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!structureRemoved) {
            super.handleProcessingInstruction(iprocessingInstruction);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }






    private void processIteration() {

        if (this.variablesMap == null) {
            throw new TemplateProcessingException(
                    "Iteration is not supported because local variable support is DISABLED. This is due to " +
                    "the use of an implementation of the " + IVariablesMap.class.getName() + " interface that does " +
                    "not provide local-variable support. In order to have local-variable support, the variables map " +
                    "implementation should also implement the " + ILocalVariableAwareVariablesMap.class.getName() +
                    " interface");
        }


        /*
         * PREPARE THE ITERATION ARTIFACTS
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

        final String iterVariableName = this.iterationSpec.iterVariableName;
        String iterStatusVariableName = this.iterationSpec.iterStatusVariableName;
        if (StringUtils.isEmptyOrWhitespace(iterStatusVariableName)) {
            // If no name has been specified for the status variable, we will use the same as the iter var + "Stat"
            iterStatusVariableName = iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        }
        final Object iteratedObject = this.iterationSpec.iteratedObject;
        iterArtifacts.iterationQueue.resetAsCloneOf(this.iterationSpec.iterationQueue);

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

        final boolean suspendedBodyRemoved = this.suspensionSpec.bodyRemoved;
        final boolean suspendedQueueProcessable = this.suspensionSpec.queueProcessable;
        iterArtifacts.suspendedQueue.resetAsCloneOf(this.suspensionSpec.suspendedQueue);
        iterArtifacts.suspendedElementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);

        // We need to reset it or we won't be able to reuse it in nested executions
        this.suspensionSpec.reset();
        this.suspended = false;


        /*
         * PERFORM THE ITERATION
         */

        while (iterator.hasNext()) {

            status.current = iterator.next();

            this.variablesMap.increaseLevel();

            this.variablesMap.put(iterVariableName, status.current);
            this.variablesMap.put(iterStatusVariableName, status);

            // We will initialize the suspension artifacts just as if we had just suspended it
            this.suspensionSpec.bodyRemoved = suspendedBodyRemoved;
            this.suspensionSpec.queueProcessable = suspendedQueueProcessable;
            this.suspensionSpec.suspendedQueue.resetAsCloneOf(iterArtifacts.suspendedQueue);
            this.suspensionSpec.suspendedIterator.resetAsCloneOf(iterArtifacts.suspendedElementProcessorIterator);
            this.suspended = true;

            iterArtifacts.iterationQueue.process(this, false);

            this.variablesMap.decreaseLevel();

            status.index++;

        }

        // Finally, clean just in case --even if the queued events should have already cleaned this
        this.suspensionSpec.reset();
        this.suspended = false;

        // Allow the reuse of the iteration artifacts
        this.iterationArtifactsIndex--;

    }







    private static Integer computeIteratedObjectSize(final Object iteratedObject) {
        if (iteratedObject == null) {
            return 0;
        }
        if (iteratedObject instanceof Collection<?>) {
            return ((Collection<?>)iteratedObject).size();
        }
        if (iteratedObject instanceof Map<?,?>) {
            return ((Map<?,?>)iteratedObject).size();
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
        return 1; // In this case, we will iterate the object as a collection of size 1
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
        return Collections.singletonList(iteratedObject).iterator();
    }




    private static class LevelArray {

        private int[] array;
        private int size;


        LevelArray(final int initialLen) {
            super();
            this.array = new int[initialLen];
            this.size = 0;
        }


        void add(final int level) {

            if (this.array.length == this.size) {
                // We need to grow the array!
                final int[] newArray = new int[this.array.length + 5];
                System.arraycopy(this.array,0,newArray,0,this.size);
                this.array = newArray;
            }

            this.array[this.size++] = level;

        }

        boolean matchOrHigher(final int level) {
            if (this.size > 0 && this.array[this.size - 1] <= level) {
                return true;
            }
            return false;
        }

        boolean matchAndPop(final int level) {
            if (this.size > 0 && this.array[this.size - 1] == level) {
                this.size--;
                return true;
            }
            return false;
        }

    }



    private static class IterationSpec {

        private int fromMarkupLevel;
        private String iterVariableName;
        private String iterStatusVariableName;
        private Object iteratedObject;
        final EngineEventQueue iterationQueue;

        IterationSpec(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.iterationQueue = new EngineEventQueue(templateMode, configuration);
            reset();
        }

        void reset() {
            this.fromMarkupLevel = Integer.MAX_VALUE;
            this.iterVariableName = null;
            this.iterStatusVariableName = null;
            this.iteratedObject = null;
            this.iterationQueue.reset();
        }

    }


    private static class SuspensionSpec {

        boolean bodyRemoved;
        boolean queueProcessable;
        final EngineEventQueue suspendedQueue;
        final ElementProcessorIterator suspendedIterator;

        SuspensionSpec(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.suspendedQueue = new EngineEventQueue(templateMode, configuration);
            this.suspendedIterator = new ElementProcessorIterator();
        }

        void reset() {
            this.bodyRemoved = false;
            this.queueProcessable = false;
            this.suspendedQueue.reset();
            this.suspendedIterator.reset();
        }

    }


    private static class IterationArtifacts {

        final EngineEventQueue iterationQueue;
        final EngineEventQueue suspendedQueue;
        final ElementProcessorIterator suspendedElementProcessorIterator;

        IterationArtifacts(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.iterationQueue = new EngineEventQueue(templateMode, configuration);
            this.suspendedQueue = new EngineEventQueue(templateMode, configuration);
            this.suspendedElementProcessorIterator = new ElementProcessorIterator();
        }

    }

}