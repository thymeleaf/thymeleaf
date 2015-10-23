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

    private int modelLevel = 0;

    private boolean[] allowedNonElementStructuresByModelLevel;
    private int[] allowedElementCountByModelLevel;
    private LevelArray skipCloseTagLevels = new LevelArray(5);

    // We will have just one (reusable) instance of the element processor iterator, which will take into account the
    // fact that the processors applicable to an element might change during the execution of other processors, because
    // the applicability of an element processor is based on its attributes, and these might change in runtime
    private final ElementProcessorIterator elementProcessorIterator = new ElementProcessorIterator();
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

    // This should only be modified by means of the 'increaseHandlerExecLevel' and 'decreaseHandlerExecLevel' methods
    private int handlerExecLevel = -1;

    // These structures will be indexed by the handlerExecLevel, which allows structures to be used across different levels of nesting
    private EngineEventQueue[] eventQueues = null;

    // Putting a text node to the queue for immediate execution is so common we will have a common buffer object for that
    private Text textBuffer = null;

    // In order to execute IElementModelProcessor processors we will use a buffer so that we don't create so many Model objects
    private Model modelBuffer = null;

    // Used for suspending the execution of a tag and replacing it for a different event (perhaps after building a
    // queue) or iterating the suspended event and its body.
    private boolean suspended = false;
    private SuspensionSpec suspensionSpec; // Will be initialized once we have the processing context
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

    // Used in the cases when a standalone tag is converted into an open+close one (i.e. a body is added).
    private OpenElementTag[] standaloneOpenTagBuffers = null;
    private CloseElementTag[] standaloneCloseTagBuffers = null;
    private Text[] standaloneTextBuffers = null;
    private int standaloneTagBuffersIndex = 0;

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

        this.allowedElementCountByModelLevel = new int[10];
        Arrays.fill(this.allowedElementCountByModelLevel, Integer.MAX_VALUE);
        this.allowedNonElementStructuresByModelLevel = new boolean[10];
        Arrays.fill(this.allowedNonElementStructuresByModelLevel, true);

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
        this.suspensionSpec = new SuspensionSpec(this.templateMode, this.configuration);
        this.iterationSpec = new IterationSpec(this.templateMode, this.configuration);
        this.elementModelSpec = new ElementModelSpec(this.templateMode, this.configuration);

        // Flags used for quickly determining if a non-element structure might have to be processed or not
        this.hasTemplateBoundariesProcessors = !this.configuration.getTemplateBoundariesProcessors(this.templateMode).isEmpty();
        this.hasCDATASectionProcessors = !this.configuration.getCDATASectionProcessors(this.templateMode).isEmpty();
        this.hasCommentProcessors = !this.configuration.getCommentProcessors(this.templateMode).isEmpty();
        this.hasDocTypeProcessors = !this.configuration.getDocTypeProcessors(this.templateMode).isEmpty();
        this.hasProcessingInstructionProcessors = !this.configuration.getProcessingInstructionProcessors(this.templateMode).isEmpty();
        this.hasTextProcessors = !this.configuration.getTextProcessors(this.templateMode).isEmpty();
        this.hasXMLDeclarationProcessors = !this.configuration.getXMLDeclarationProcessors(this.templateMode).isEmpty();

        // Initialize arrays containing the processors for all the non-element structures (these do not change during execution)
        final Set<ITemplateBoundariesProcessor> templateBoundariesProcessorSet = this.configuration.getTemplateBoundariesProcessors(this.templateMode);
        final Set<ICDATASectionProcessor> cdataSectionProcessorSet = this.configuration.getCDATASectionProcessors(this.templateMode);
        final Set<ICommentProcessor> commentProcessorSet = this.configuration.getCommentProcessors(this.templateMode);
        final Set<IDocTypeProcessor> docTypeProcessorSet = this.configuration.getDocTypeProcessors(this.templateMode);
        final Set<IProcessingInstructionProcessor> processingInstructionProcessorSet = this.configuration.getProcessingInstructionProcessors(this.templateMode);
        final Set<ITextProcessor> textProcessorSet = this.configuration.getTextProcessors(this.templateMode);
        final Set<IXMLDeclarationProcessor> xmlDeclarationProcessorSet = this.configuration.getXMLDeclarationProcessors(this.templateMode);
        this.templateBoundariesProcessors = templateBoundariesProcessorSet.toArray(new ITemplateBoundariesProcessor[templateBoundariesProcessorSet.size()]);
        this.cdataSectionProcessors = cdataSectionProcessorSet.toArray(new ICDATASectionProcessor[cdataSectionProcessorSet.size()]);
        this.commentProcessors = commentProcessorSet.toArray(new ICommentProcessor[commentProcessorSet.size()]);
        this.docTypeProcessors = docTypeProcessorSet.toArray(new IDocTypeProcessor[docTypeProcessorSet.size()]);
        this.processingInstructionProcessors = processingInstructionProcessorSet.toArray(new IProcessingInstructionProcessor[processingInstructionProcessorSet.size()]);
        this.textProcessors = textProcessorSet.toArray(new ITextProcessor[textProcessorSet.size()]);
        this.xmlDeclarationProcessors = xmlDeclarationProcessorSet.toArray(new IXMLDeclarationProcessor[xmlDeclarationProcessorSet.size()]);

    }




    private void increaseModelLevel() {

        this.modelLevel++;

        if (this.modelLevel == this.allowedElementCountByModelLevel.length) {

            final int[] newAllowedElementCountByModelLevel = new int[this.allowedElementCountByModelLevel.length + 10];
            Arrays.fill(newAllowedElementCountByModelLevel, Integer.MAX_VALUE);
            System.arraycopy(this.allowedElementCountByModelLevel, 0, newAllowedElementCountByModelLevel, 0, this.allowedElementCountByModelLevel.length);
            this.allowedElementCountByModelLevel = newAllowedElementCountByModelLevel;

            final boolean[] newAllowedNonElementStructuresByModelLevel = new boolean[this.allowedNonElementStructuresByModelLevel.length + 10];
            Arrays.fill(newAllowedNonElementStructuresByModelLevel, true);
            System.arraycopy(this.allowedNonElementStructuresByModelLevel, 0, newAllowedNonElementStructuresByModelLevel, 0, this.allowedNonElementStructuresByModelLevel.length);
            this.allowedNonElementStructuresByModelLevel = newAllowedNonElementStructuresByModelLevel;

        }

    }


    private void decreaseModelLevel() {
        this.modelLevel--;
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
            this.eventQueues[this.handlerExecLevel] = new EngineEventQueue(this.configuration, this.templateMode);
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
    public void handleTemplateStart(final ITemplateStart itemplateStart) {

        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasTemplateBoundariesProcessors) {
            super.handleTemplateStart(itemplateStart);
            increaseHandlerExecLevel(); // Handling template start will always increase the handler exec level
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.templateBoundariesProcessors.length;
        for (int i = 0; i < processorsLen; i++) {

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

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.templateStructureHandler.insertTextProcessable;

                this.textBuffer.setText(this.templateStructureHandler.insertTextValue);
                queue.build(this.textBuffer);

            } else if (this.templateStructureHandler.insertModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.templateStructureHandler.insertModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.templateStructureHandler.insertModelValue);

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        super.handleTemplateStart(itemplateStart);


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * HANDLER EXEC LEVEL WILL NOT BE DECREASED until the "templateEnd" event
         */

    }




    @Override
    public void handleTemplateEnd(final ITemplateEnd itemplateEnd) {

        /*
         * FAIL FAST in case this structure has no associated processors.
         */
        if (!this.hasTemplateBoundariesProcessors) {
            decreaseHandlerExecLevel(); // Decrease the level increased during template start
            super.handleTemplateEnd(itemplateEnd);
            return;
        }


        /*
         * DECLARE THE FLAGS NEEDED DURING THE EXECUTION OF PROCESSORS
         */
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * EXECUTE PROCESSORS
         */
        final int processorsLen = this.templateBoundariesProcessors.length;
        for (int i = 0; i < processorsLen; i++) {

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

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.templateStructureHandler.insertTextProcessable;

                this.textBuffer.setText(this.templateStructureHandler.insertTextValue);
                queue.build(this.textBuffer);

            } else if (this.templateStructureHandler.insertModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.templateStructureHandler.insertModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.templateStructureHandler.insertModelValue);

            }

        }


        /*
         * PROCESS THE QUEUE, launching all the queued events (BEFORE DELEGATING)
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * DECREASE THE HANDLER EXEC LEVEL
         */
        decreaseHandlerExecLevel();


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN. This is the only case in which it really is the last operation
         */
        super.handleTemplateEnd(itemplateEnd);


    }




    @Override
    public void handleText(final IText itext) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.textStructureHandler.reset();

            this.textProcessors[i].process(this.context, itext, this.textStructureHandler);

            if (this.textStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.textStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.textStructureHandler.replaceWithModelValue);

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
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.commentStructureHandler.reset();

            this.commentProcessors[i].process(this.context, icomment, this.commentStructureHandler);

            if (this.commentStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.commentStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.commentStructureHandler.replaceWithModelValue);

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
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.cdataSectionStructureHandler.reset();

            this.cdataSectionProcessors[i].process(this.context, icdataSection, this.cdataSectionStructureHandler);

            if (this.cdataSectionStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.cdataSectionStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.cdataSectionStructureHandler.replaceWithModelValue);

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
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.allowedElementCountByModelLevel[this.modelLevel]-- <= 0) {
            return;
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
        boolean tagsRemoved = false; // If the tag is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.engineContext != null) {
            this.engineContext.increaseLevel();
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
            queue.resetAsCloneOf(this.suspensionSpec.suspendedQueue, false);
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
        while (!tagsRemoved && (processor = this.elementProcessorIterator.next(standaloneElementTag)) != null) {

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

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.allowedElementCountInBody = Integer.MAX_VALUE;
                    this.suspensionSpec.allowedNonElementStructuresInBody = true;
                    this.suspensionSpec.queueProcessable = queueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue, false);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Add this standalone tag to the iteration queue
                    this.iterationSpec.iterationQueue.build(standaloneElementTag.cloneEvent());

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

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

                    queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    ensureStandaloneTagBuffers();
                    final OpenElementTag openTag = this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex];
                    final CloseElementTag closeTag = this.standaloneCloseTagBuffers[this.standaloneTagBuffersIndex];
                    openTag.resetAsCloneOf(standaloneElementTag);
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the text node that will be added to the queue, that we will suspend
                    // Note we are using a specific buffer for these cases, because we want to avoid cloning, and
                    // we cannot use the normal 'textBuffer' because it might be needed too during
                    // the handling of the open/close events or any of its sub-events (e.g. nested queues). So the
                    // best option is take one from our own, specialized standalone-oriented buffer in order to limit
                    // the amount of objects created in these cases
                    final Text text = this.standaloneTextBuffers[this.standaloneTagBuffersIndex];
                    text.setText(this.elementTagStructureHandler.setBodyTextValue);
                    queue.build(text);

                    // We are done with using the standalone buffers, so increase the index
                    this.standaloneTagBuffersIndex++;

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.allowedElementCountInBody = Integer.MAX_VALUE;
                    this.suspensionSpec.allowedNonElementStructuresInBody = true;
                    this.suspensionSpec.queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue, false);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    // We can free the buffers we just used
                    this.standaloneTagBuffersIndex--;

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    queue.reset(); // Remove any previous results on the queue

                    // Prepare the now-equivalent open and close tags
                    ensureStandaloneTagBuffers();
                    final OpenElementTag openTag = this.standaloneOpenTagBuffers[this.standaloneTagBuffersIndex];
                    final CloseElementTag closeTag = this.standaloneCloseTagBuffers[this.standaloneTagBuffersIndex];
                    openTag.resetAsCloneOf(standaloneElementTag);
                    closeTag.resetAsCloneOf(standaloneElementTag);

                    // Prepare the queue (that we will suspend)
                    // Model will be automatically cloned if mutable
                    queue.addModel(this.elementTagStructureHandler.setBodyModelValue);

                    // We are done with using the standalone buffers, so increase the index
                    this.standaloneTagBuffersIndex++;

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.allowedElementCountInBody = Integer.MAX_VALUE;
                    this.suspensionSpec.allowedNonElementStructuresInBody = true;
                    this.suspensionSpec.queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue, false);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- we need the variables stored there, if any

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    // We can free the buffers we just used
                    this.standaloneTagBuffersIndex--;

                    // Decrease the context level
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                    }

                    // Complete exit of the handler method: no more processing to do from here
                    return;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    final IModel insertedModel = this.elementTagStructureHandler.insertBeforeModelValue;
                    if (queue.size() == 0) {
                        // The current queue object is empty, so we can use it to process this inserted model

                        queue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        queue.process(getNext(), true);

                    } else {
                        // The current queue object is not empty :-( so in order to process this inserted model
                        // we will need to use a new queue...

                        final EngineEventQueue newQueue = new EngineEventQueue(this.configuration, this.templateMode, 5);
                        newQueue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        newQueue.process(getNext(), true);

                    }

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    queue.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBuffer.setText(this.elementTagStructureHandler.replaceWithTextValue);
                    queue.build(this.textBuffer);

                    tagsRemoved = true;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    queue.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    tagsRemoved = true;

                } else if (this.elementTagStructureHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagsRemoved = true;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagsRemoved = true;

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

                if (!this.elementProcessorIterator.lastWasRepeated()){

                    if (queue.size() > 0) {
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
                    this.elementProcessorIterator.setLastToBeRepeated(standaloneElementTag);

                    // Suspend the queue - execution will be restarted by the execution of this event again once model is gathered
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    this.suspended = true;
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Add this standalone tag to the element model queue
                    this.elementModelSpec.modelQueue.build(standaloneElementTag.cloneEvent());

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

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

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = true; // We actually NEED TO process this queue

                // Model will be automatically cloned if mutable
                queue.addModel(this.modelBuffer);

                tagsRemoved = true;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!tagsRemoved) {
            super.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


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
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleOpenElement(final IOpenElementTag iopenElementTag) {

        /*
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.allowedElementCountByModelLevel[this.modelLevel] <= 0) { // Note the structure doesn't end here, so we don't decrease until the close tag
            increaseModelLevel();
            this.allowedElementCountByModelLevel[this.modelLevel] = 0; // we make sure all is skipped inside
            this.allowedNonElementStructuresByModelLevel[this.modelLevel] = false; // we make sure all is skipped inside
            return;
        }


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
         * FAIL FAST in case this tag has no associated processors and we have no reason to pay attention to it
         * anyway (because of being suspended). This avoids cast to engine-specific implementation for most cases.
         */
        if (!this.suspended && !iopenElementTag.hasAssociatedProcessors()) {
            super.handleOpenElement(iopenElementTag);
            increaseModelLevel();
            if (this.engineContext != null) {
                this.engineContext.increaseLevel();
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
        boolean tagsRemoved = false; // If the tag is removed, we have to immediately stop the execution of processors
        boolean queueProcessable = false; // When elements are added to a queue, we need to know whether it is processable or not
        boolean allowedNonElementStructuresInBody = true; // Needed to discard the body, or allow only a certain amount of children to execute
        int allowedElementCountInBody = Integer.MAX_VALUE; // Needed to discard the body, or allow only a certain amount of children to execute


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final EngineEventQueue queue = this.eventQueues[this.handlerExecLevel];


        /*
         * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.engineContext != null) {
            this.engineContext.increaseLevel();
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
            queue.resetAsCloneOf(this.suspensionSpec.suspendedQueue, false);
            queueProcessable = this.suspensionSpec.queueProcessable;
            allowedElementCountInBody = this.suspensionSpec.allowedElementCountInBody;
            allowedNonElementStructuresInBody = this.suspensionSpec.allowedNonElementStructuresInBody;
            this.elementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);
            this.suspended = false;
            this.suspensionSpec.reset();

            // Note we will not increase the VariablesMap level here, as are keeping the level from the suspended execution

        }


        /*
         * EXECUTE PROCESSORS
         */
        IElementProcessor processor;
        while (!tagsRemoved && (processor = this.elementProcessorIterator.next(openElementTag)) != null) {

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
                    if (queue.size() == 1 && queue.get(0) == this.textBuffer) {
                        // Replace the text buffer with a clone
                        queue.reset();
                        queue.build(this.textBuffer.cloneEvent());
                    }

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;
                    this.suspensionSpec.allowedElementCountInBody = allowedElementCountInBody;
                    this.suspensionSpec.allowedNonElementStructuresInBody = allowedNonElementStructuresInBody;
                    this.suspensionSpec.queueProcessable = queueProcessable;
                    this.suspensionSpec.suspendedQueue.resetAsCloneOf(queue, false);
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Add the tag itself to the iteration queue
                    this.iterationSpec.iterationQueue.build(openElementTag.cloneEvent());

                    // Increase model level, as normal with open tags
                    increaseModelLevel();

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

                    // Note we DO NOT DECREASE THE CONTEXT LEVEL -- that's the responsibility of the close event

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    return;

                } else if (this.elementTagStructureHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.setBodyTextProcessable;

                    // For now we will not be cloning the buffer and just hoping it will be executed as is. This is
                    // the most common case (th:text) and this will save us a good number of Text nodes. But note that
                    // if this element is iterated AFTER we set this, we will need to clone this node before suspending
                    // the queue, or we might have nasty interactions with each of the subsequent iterations
                    this.textBuffer.setText(this.elementTagStructureHandler.setBodyTextValue);
                    queue.build(this.textBuffer);

                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.setBodyModel) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.setBodyModelProcessable;

                    // Model will be automatically cloned if mutable
                    queue.addModel(this.elementTagStructureHandler.setBodyModelValue);

                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.insertBeforeModel) {

                    final IModel insertedModel = this.elementTagStructureHandler.insertBeforeModelValue;
                    if (queue.size() == 0) {
                        // The current queue object is empty, so we can use it to process this inserted model

                        queue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        queue.process(getNext(), true);

                    } else {
                        // The current queue object is not empty :-( so in order to process this inserted model
                        // we will need to use a new queue...

                        final EngineEventQueue newQueue = new EngineEventQueue(this.configuration, this.templateMode, 5);
                        newQueue.addModel(insertedModel);
                        // Model inserted BEFORE is never processable, so we will always use getNext() here
                        newQueue.process(getNext(), true);

                    }

                } else if (this.elementTagStructureHandler.insertImmediatelyAfterModel) {

                    // No cleaning the queue, as we are not setting the entire body, so we will respect whatever
                    // was already added to the body queue, simply adding our insertion at the beginning of it all
                    queueProcessable = this.elementTagStructureHandler.insertImmediatelyAfterModelProcessable;

                    // Model will be automatically cloned if mutable
                    queue.insertModel(0, this.elementTagStructureHandler.insertImmediatelyAfterModelValue);

                    // No intervention on the body flags - we will not be removing the body, just inserting before it

                } else if (this.elementTagStructureHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.replaceWithTextProcessable;

                    // No need to clone the text buffer because, as we are removing the tag, we will execute the queue
                    // (containing only the text node) immediately. No further processors are to be executed
                    this.textBuffer.setText(this.elementTagStructureHandler.replaceWithTextValue);
                    queue.build(this.textBuffer);

                    tagsRemoved = true;
                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.replaceWithModel) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.elementTagStructureHandler.replaceWithModelProcessable;

                    // Model will be automatically cloned if mutable
                    queue.addModel(this.elementTagStructureHandler.replaceWithModelValue);

                    tagsRemoved = true;
                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagsRemoved = true;
                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.removeTags) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagsRemoved = true;

                } else if (this.elementTagStructureHandler.removeBody) {

                    queue.reset(); // Remove any previous results on the queue

                    allowedElementCountInBody = 0;
                    allowedNonElementStructuresInBody = false;

                } else if (this.elementTagStructureHandler.removeAllButFirstChild) {

                    queue.reset(); // Remove any previous results on the queue

                    allowedElementCountInBody = 1;
                    allowedNonElementStructuresInBody = true;

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

                if (!this.elementProcessorIterator.lastWasRepeated()){

                    if (queue.size() > 0) {
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
                    this.elementProcessorIterator.setLastToBeRepeated(openElementTag);

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    // Note there is no queue to be suspended --we've made sure of that before, so we are only suspending the iterator
                    this.suspended = true;
                    this.suspensionSpec.suspendedIterator.resetAsCloneOf(this.elementProcessorIterator);

                    // Add the tag itself to the element model queue
                    this.elementModelSpec.modelQueue.build(openElementTag.cloneEvent());

                    // Increase model level, as normal with open tags
                    increaseModelLevel();

                    // Decrease the handler execution level (all important bits are already in suspensionSpec)
                    decreaseHandlerExecLevel();

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

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = true; // We actually NEED TO process this queue

                // Model will be automatically cloned if mutable
                queue.addModel(this.modelBuffer);

                tagsRemoved = true;
                allowedElementCountInBody = 0;
                allowedNonElementStructuresInBody = false;


            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither a Tag Element Processor nor a Model Element Processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MODEL LEVEL RIGHT AFTERWARDS
         */
        if (!tagsRemoved) {
            super.handleOpenElement(openElementTag);
        }


        /*
         * INCREASE THE MODEL LEVEL to the value that will be applied to the tag's bodies
         */
        increaseModelLevel();


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (!allowedNonElementStructuresInBody) {
            this.allowedNonElementStructuresByModelLevel[this.modelLevel] = false;
        }
        if (allowedElementCountInBody != Integer.MAX_VALUE) {
            // We make sure no other nested events will be processed at all
            this.allowedElementCountByModelLevel[this.modelLevel] = allowedElementCountInBody;
        }


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (tagsRemoved) {
            this.skipCloseTagLevels.add(this.modelLevel - 1);
            // We cannot decrease here the context level because we aren't actually decreasing the model
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
        if (this.allowedElementCountByModelLevel[this.modelLevel]-- <= 0) {
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
         * CHECK WHETHER WE SHOULD KEEP SKIPPING MODEL or we just got to the end of the discarded block
         */
        if (this.allowedElementCountByModelLevel[this.modelLevel + 1] <= 0) {
            // We've reached the last point where model should be discarded, so we should reset the variable
            Arrays.fill(this.allowedElementCountByModelLevel, this.modelLevel + 1, this.allowedElementCountByModelLevel.length, Integer.MAX_VALUE);
            Arrays.fill(this.allowedNonElementStructuresByModelLevel, this.modelLevel + 1, this.allowedNonElementStructuresByModelLevel.length, true);
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.skipCloseTagLevels.matchAndPop(this.modelLevel)) {
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
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) { // an unmatched is not really an element
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
            if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.docTypeStructureHandler.reset();

            this.docTypeProcessors[i].process(this.context, idocType, this.docTypeStructureHandler);

            if (this.docTypeStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.docTypeStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.docTypeStructureHandler.replaceWithModelValue);

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
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.xmlDeclarationStructureHandler.reset();

            this.xmlDeclarationProcessors[i].process(this.context, ixmlDeclaration, this.xmlDeclarationStructureHandler);

            if (this.xmlDeclarationStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.xmlDeclarationStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.xmlDeclarationStructureHandler.replaceWithModelValue);

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
         * CHECK WHETHER THIS MODEL REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (!this.allowedNonElementStructuresByModelLevel[this.modelLevel]) {
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

            this.processingInstructionStructureHandler.reset();

            this.processingInstructionProcessors[i].process(this.context, iprocessingInstruction, this.processingInstructionStructureHandler);

            if (this.processingInstructionStructureHandler.replaceWithModel) {

                queue.reset(); // Remove any previous results on the queue
                queueProcessable = this.processingInstructionStructureHandler.replaceWithModelProcessable;

                // Model will be automatically cloned if mutable
                queue.addModel(this.processingInstructionStructureHandler.replaceWithModelValue);

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

        final int suspendedAllowedElementCountInBody = this.suspensionSpec.allowedElementCountInBody;
        final boolean suspendedAllowedNonElementStructuresInBody = this.suspensionSpec.allowedNonElementStructuresInBody;
        final boolean suspendedQueueProcessable = this.suspensionSpec.queueProcessable;
        iterArtifacts.suspendedQueue.resetAsCloneOf(this.suspensionSpec.suspendedQueue, false);
        iterArtifacts.suspendedElementProcessorIterator.resetAsCloneOf(this.suspensionSpec.suspendedIterator);

        // We need to reset it or we won't be able to reuse it in nested executions
        this.suspensionSpec.reset();
        this.suspended = false;


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

            // We will initialize the suspension artifacts just as if we had just suspended it
            this.suspensionSpec.allowedElementCountInBody = suspendedAllowedElementCountInBody;
            this.suspensionSpec.allowedNonElementStructuresInBody = suspendedAllowedNonElementStructuresInBody;
            this.suspensionSpec.queueProcessable = suspendedQueueProcessable;
            this.suspensionSpec.suspendedQueue.resetAsCloneOf(iterArtifacts.suspendedQueue, false);
            this.suspensionSpec.suspendedIterator.resetAsCloneOf(iterArtifacts.suspendedElementProcessorIterator);
            this.suspended = true;

            // We increase the element counter in order to compensate for the fact that the element being iterated
            // might have been the only one allowed at this model level (which means all its iterations should
            // be allowed)
            this.allowedElementCountByModelLevel[this.modelLevel]++;

            // We might need to perform some modifications to the iteration queue for this iteration
            // For example, in text modes we might modify the first whitespaces in the body of the iterated element
            prepareIterationQueueForIteration(iterArtifacts, status.index, !iterHasNext);

            // Execute the queue itself
            iterArtifacts.iterationQueue.process(this, false);

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

        // Finally, clean just in case --even if the queued events should have already cleaned this
        this.suspensionSpec.reset();
        this.suspended = false;

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

        elemArtifacts.modelQueue.process(this, false);

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




    private static final class LevelArray {

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

        boolean matchAndPop(final int level) {
            if (this.size > 0 && this.array[this.size - 1] == level) {
                this.size--;
                return true;
            }
            return false;
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


    private static final class SuspensionSpec {

        int allowedElementCountInBody;
        boolean allowedNonElementStructuresInBody;
        boolean queueProcessable;
        final EngineEventQueue suspendedQueue;
        final ElementProcessorIterator suspendedIterator;

        SuspensionSpec(final TemplateMode templateMode, final IEngineConfiguration configuration) {
            super();
            this.suspendedQueue = new EngineEventQueue(configuration, templateMode, 5); // 5 events will probably be enough
            this.suspendedIterator = new ElementProcessorIterator();
        }

        void reset() {
            this.allowedElementCountInBody = Integer.MAX_VALUE;
            this.allowedNonElementStructuresInBody = true;
            this.queueProcessable = false;
            this.suspendedQueue.reset();
            this.suspendedIterator.reset();
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

}