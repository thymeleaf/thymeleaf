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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class EngineEventQueue {

    /*
     * This is an internal event queue, used in a different way than ITemplateHandlerEventQueue and its implementations
     * (note the interface is not implemented by this one)
     */

    /*
     * This queue WILL ONLY CONTAIN ENGINE-BASED IMPLEMENTATIONS of the ITemplateEvent interface
     */

    private static final int DEFAULT_INITIAL_SIZE = 50;

    private int queueSize = 0;
    private IEngineTemplateEvent[] queue; // We use the interface, but not all implementations will be allowed

    private final TemplateMode templateMode;
    private final IEngineConfiguration configuration;

    private final TemplateStart templateStartBuffer;
    private final TemplateEnd templateEndBuffer;

    private final Text textBuffer;
    private final Comment commentBuffer;
    private final CDATASection cdataSectionBuffer;
    private final DocType docTypeBuffer;
    private final ProcessingInstruction processingInstructionBuffer;
    private final XMLDeclaration xmlDeclarationBuffer;

    private final OpenElementTag openElementTagBuffer;
    private final StandaloneElementTag standaloneElementTagBuffer;
    private final CloseElementTag closeElementTagBuffer;



    EngineEventQueue(final IEngineConfiguration configuration, final TemplateMode templateMode) {
        this(configuration, templateMode, DEFAULT_INITIAL_SIZE);
    }


    EngineEventQueue(final IEngineConfiguration configuration, final TemplateMode templateMode, final int initialSize) {

        super();

        if (initialSize > 0) {
            this.queue = new IEngineTemplateEvent[initialSize];
            Arrays.fill(this.queue, null);
        }

        this.templateMode = templateMode;
        this.configuration = configuration;

        this.templateStartBuffer = new TemplateStart();
        this.templateEndBuffer = new TemplateEnd();

        this.textBuffer = new Text(this.configuration.getTextRepository());
        this.cdataSectionBuffer = new CDATASection(this.configuration.getTextRepository());
        this.commentBuffer = new Comment(this.configuration.getTextRepository());
        this.docTypeBuffer = new DocType(this.configuration.getTextRepository());
        this.processingInstructionBuffer = new ProcessingInstruction(this.configuration.getTextRepository());
        this.xmlDeclarationBuffer = new XMLDeclaration(this.configuration.getTextRepository());

        this.standaloneElementTagBuffer =
                new StandaloneElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        this.openElementTagBuffer =
                new OpenElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        this.closeElementTagBuffer =
                new CloseElementTag(this.templateMode, this.configuration.getElementDefinitions());

    }




    int size() {
        return this.queueSize;
    }


    IEngineTemplateEvent get(final int pos) {
        if (pos < 0 || pos > this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }
        return this.queue[pos];
    }


    void build(final IEngineTemplateEvent event) {

        if (event == null) {
            return;
        }

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final IEngineTemplateEvent[] newQueue = new IEngineTemplateEvent[Math.min(this.queue.length + 25, this.queue.length * 2)];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Set the new event in its new position at the end
        this.queue[this.queueSize] = event;

        this.queueSize++;

    }



    void add(final IEngineTemplateEvent event, final boolean cloneAlways) {
        insert(this.queueSize, event, cloneAlways);
    }


    void insert(final int pos, final IEngineTemplateEvent event, final boolean cloneAlways) {

        if (pos < 0 || pos > this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (event == null) {
            return;
        }

        // Check that the event that is going to be inserted is not a template start/end
        if (event instanceof ITemplateStart || event instanceof ITemplateEnd) {
            throw new TemplateProcessingException(
                    "Cannot insert event of type " + event.getClass().getName() + " manually. Template start/end " +
                    "events can only be added to models internally during template parsing.");
        }

        // Check that we are not trying to insert an event before a 'template start', or after a 'template end'
        if (this.queueSize > 0) {
            if (pos == 0 && this.queue[0] instanceof ITemplateStart) {
                throw new TemplateProcessingException("Cannot insert event of type " + event.getClass().getName() + " before a 'template start' event");
            } else if (pos == this.queueSize && this.queue[this.queueSize - 1] instanceof ITemplateEnd) {
                throw new TemplateProcessingException("Cannot insert event of type " + event.getClass().getName() + " after a 'template end' event");
            }
        }

        // Check there is room for a new event, or grow the queue if not
        if (this.queue.length == this.queueSize) {
            final IEngineTemplateEvent[] newQueue = new IEngineTemplateEvent[Math.min(this.queue.length + 25, this.queue.length * 2)];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Make room for the new event
        System.arraycopy(this.queue, pos, this.queue, pos + 1, this.queueSize - pos);

        // Set the new event in its new position
        this.queue[pos] = (cloneAlways? (IEngineTemplateEvent) event.cloneEvent() : event);

        this.queueSize++;

    }


    void addModel(final IModel imodel) {
        insertModel(this.queueSize, imodel);
    }


    void insertModel(final int pos, final IModel imodel) {

        if (pos < 0 || pos > this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (imodel == null || imodel.size() == 0) {
            return;
        }

        if (!this.configuration.equals(imodel.getConfiguration())) {
            throw new TemplateProcessingException(
                    "Cannot add model of class " + imodel.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Engine Configuration.");
        }

        if (this.templateMode != imodel.getTemplateMode()) {
            throw new TemplateProcessingException(
                    "Cannot add model of class " + imodel.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Mode: " + imodel.getTemplateMode() + " instead of " +
                    "the current " + this.templateMode);
        }


        final Model model;
        if (imodel instanceof TemplateModel) {
            // No need to clone - argument is an immutable piece of model and therefore using it without cloning will
            // produce no side/undesired effects
            model = ((TemplateModel) imodel).getInternalModel();
        } else {
            // This implementation does not directly come from the parser nor is immutable, so we must clone its events
            // to avoid interactions.
            model = new Model(imodel);
        }

        final EngineEventQueue modelQueue = model.getEventQueue();


        // Compute whether the model to be inserted is surrounded by 'template start'/'template end' events
        final boolean modelHasTemplateStart = modelQueue.queue[0] instanceof ITemplateStart;
        final boolean modelHasTemplateEnd = modelQueue.queue[modelQueue.queueSize - 1] instanceof ITemplateEnd;
        final boolean modelWrappedInBoundaries = modelHasTemplateStart && modelHasTemplateEnd;
        if (!modelWrappedInBoundaries && (modelHasTemplateStart || modelHasTemplateEnd)) {
            throw new TemplateProcessingException(
                    "Cannot insert malformed model: Model is not properly surrounded by 'template start'/'template end' events. Models " +
                    "should have either both events (as first and last) or none of them, but not just one of them.");
        }

        // Check that we are not trying to insert an event before a 'template start', or after a 'template end'
        if (this.queueSize > 0) {
            if (pos == 0 && this.queue[0] instanceof ITemplateStart) {
                throw new TemplateProcessingException("Cannot insert model before a 'template start' event");
            } else if (pos == this.queueSize && this.queue[this.queueSize - 1] instanceof ITemplateEnd) {
                throw new TemplateProcessingException("Cannot insert model after a 'template end' event");
            }
        }

        // Recompute insertion offset and inserted model size
        final int insertionOffset = (modelWrappedInBoundaries? 1 : 0);
        final int insertionSize = (modelWrappedInBoundaries? modelQueue.queueSize - 2 : modelQueue.queueSize);

        // Check there is room for a new event, or grow the queue if not
        if (this.queue.length <= (this.queueSize + insertionSize)) {
            // We need to grow the queue!
            final IEngineTemplateEvent[] newQueue = new IEngineTemplateEvent[Math.max(this.queueSize + insertionSize, this.queue.length + 25)];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Make room for the new events (if necessary because pos < this.queueSize)
        System.arraycopy(this.queue, pos, this.queue, pos + insertionSize, this.queueSize - pos);

        // Copy the new events to their new position (no cloning needed here - if needed it would have been already done)
        System.arraycopy(modelQueue.queue, insertionOffset, this.queue, pos, insertionSize);

        this.queueSize += insertionSize;

    }



    void remove(final int pos) {

        if (pos < 0 || pos > this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        System.arraycopy(this.queue, pos + 1, this.queue, pos, this.queueSize - (pos + 1));

        this.queueSize--;

    }




    void process(final ITemplateHandler handler, final boolean reset) {

        if (handler == null || this.queueSize == 0) {
            return;
        }

        IEngineTemplateEvent event;
        int n = this.queueSize;
        int i = 0;

        while (n-- != 0) {

            event = this.queue[i++];

            if (event instanceof Text) {
                this.textBuffer.resetAsCloneOf((Text) event);
                handler.handleText(this.textBuffer);
            } else if (event instanceof OpenElementTag) {
                this.openElementTagBuffer.resetAsCloneOf((OpenElementTag) event);
                handler.handleOpenElement(this.openElementTagBuffer);
            } else if (event instanceof CloseElementTag) {
                this.closeElementTagBuffer.resetAsCloneOf((CloseElementTag) event);
                handler.handleCloseElement(this.closeElementTagBuffer);
            } else if (event instanceof StandaloneElementTag) {
                this.standaloneElementTagBuffer.resetAsCloneOf((StandaloneElementTag) event);
                handler.handleStandaloneElement(this.standaloneElementTagBuffer);
            } else if (event instanceof DocType) {
                this.docTypeBuffer.resetAsCloneOf((DocType) event);
                handler.handleDocType(this.docTypeBuffer);
            } else if (event instanceof Comment) {
                this.commentBuffer.resetAsCloneOf((Comment) event);
                handler.handleComment(this.commentBuffer);
            } else if (event instanceof CDATASection) {
                this.cdataSectionBuffer.resetAsCloneOf((CDATASection) event);
                handler.handleCDATASection(this.cdataSectionBuffer);
            } else if (event instanceof XMLDeclaration) {
                this.xmlDeclarationBuffer.resetAsCloneOf((XMLDeclaration) event);
                handler.handleXMLDeclaration(this.xmlDeclarationBuffer);
            } else if (event instanceof ProcessingInstruction) {
                this.processingInstructionBuffer.resetAsCloneOf((ProcessingInstruction) event);
                handler.handleProcessingInstruction(this.processingInstructionBuffer);
            } else if (event instanceof TemplateStart) {
                this.templateStartBuffer.resetAsCloneOf((TemplateStart) event);
                handler.handleTemplateStart(this.templateStartBuffer);
            } else if (event instanceof TemplateEnd) {
                this.templateEndBuffer.resetAsCloneOf((TemplateEnd) event);
                handler.handleTemplateEnd(this.templateEndBuffer);
            } else {
                throw new TemplateProcessingException(
                        "Cannot handle in queue event of type: " + event.getClass().getName());
            }

        }

        if (reset) {
            this.queueSize = 0;
        }

    }



    void reset() {
        this.queueSize = 0;
    }



    EngineEventQueue cloneEventQueue(final boolean cloneEvents, final boolean cloneEventArray) {

        if (cloneEvents && !cloneEventArray) {
            throw new IllegalArgumentException("Cannot clone events if the event array is not cloned too");
        }

        final EngineEventQueue clone;
        if (!cloneEventArray) {
            // We will use queue size 0 so that a new event array is not created
            clone = new EngineEventQueue(this.configuration, this.templateMode, 0);
        } else {
            clone = new EngineEventQueue(this.configuration, this.templateMode, this.queueSize);
        }

        clone.resetAsCloneOf(this, cloneEvents, cloneEventArray);

        return clone;

    }


    void resetAsCloneOf(final EngineEventQueue original, final boolean cloneEvents) {
        // When only resetting, we will always clone the event array because it makes not sense to discard an already-created array
        resetAsCloneOf(original, cloneEvents, true);
    }


    private void resetAsCloneOf(final EngineEventQueue original, final boolean cloneEvents, final boolean cloneEventArray) {

        this.queueSize = original.queueSize;

        if (!cloneEventArray) {
            // There is only one scenario in which this makes sense: Model#process(ITemplateHandler)
            // In that case, the EngineEventQueue object is cloned in order to have new buffers, but the queue itself
            // does not need to.
            this.queue = original.queue;
            return;
        }

        if (this.queue.length < original.queueSize) {
            this.queue = new IEngineTemplateEvent[Math.max(DEFAULT_INITIAL_SIZE, original.queueSize)];
        }

        if (!cloneEvents) {
            System.arraycopy(original.queue, 0, this.queue, 0, original.queueSize);
        } else {
            for (int i = 0; i < original.queueSize; i++) {
                this.queue[i] = (IEngineTemplateEvent) original.queue[i].cloneEvent();
            }
        }

        // No need to clone the buffers...

    }


}