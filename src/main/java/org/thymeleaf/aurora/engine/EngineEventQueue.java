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

import java.util.Arrays;

import org.thymeleaf.aurora.ITemplateEngineConfiguration;
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
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.exceptions.TemplateProcessingException;

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
     * This queue WILL ONLY CONTAIN ENGINE-BASED IMPLEMENTATIONS of the ITemplateHandlerEvent interface
     */

    private static final int DEFAULT_INITIAL_SIZE = 20;

    private int queueSize = 0;
    private IEngineTemplateHandlerEvent[] queue; // We use the interface, but not all implementations will be allowed

    private final TemplateMode templateMode;
    private final ITemplateEngineConfiguration configuration;

    private final Text textBuffer;
    private final Comment commentBuffer;
    private final CDATASection cdataSectionBuffer;
    private final DocType docTypeBuffer;
    private final ProcessingInstruction processingInstructionBuffer;
    private final XMLDeclaration xmlDeclarationBuffer;

    private final OpenElementTag openElementTagBuffer;
    private final StandaloneElementTag standaloneElementTagBuffer;
    private final CloseElementTag closeElementTagBuffer;
    private final AutoOpenElementTag autoOpenElementTagBuffer;
    private final AutoCloseElementTag autoCloseElementTagBuffer;
    private final UnmatchedCloseElementTag unmatchedCloseElementTagBuffer;



    EngineEventQueue(final TemplateMode templateMode,
                     final ITemplateEngineConfiguration configuration) {

        super();

        this.queue = new IEngineTemplateHandlerEvent[DEFAULT_INITIAL_SIZE];
        Arrays.fill(this.queue, null);

        this.templateMode = templateMode;
        this.configuration = configuration;

        final ITextRepository textRepository = this.configuration.getTextRepository();
        final ElementDefinitions elementDefinitions = this.configuration.getElementDefinitions();
        final AttributeDefinitions attributeDefinitions = this.configuration.getAttributeDefinitions();

        this.textBuffer = new Text(textRepository);
        this.commentBuffer = new Comment(textRepository);
        this.cdataSectionBuffer = new CDATASection(textRepository);
        this.docTypeBuffer = new DocType(textRepository);
        this.processingInstructionBuffer = new ProcessingInstruction(textRepository);
        this.xmlDeclarationBuffer = new XMLDeclaration(textRepository);

        this.openElementTagBuffer = new OpenElementTag(this.templateMode, elementDefinitions, attributeDefinitions);
        this.standaloneElementTagBuffer = new StandaloneElementTag(this.templateMode, elementDefinitions, attributeDefinitions);
        this.closeElementTagBuffer = new CloseElementTag(this.templateMode, elementDefinitions);
        this.autoOpenElementTagBuffer = new AutoOpenElementTag(this.templateMode, elementDefinitions, attributeDefinitions);
        this.autoCloseElementTagBuffer = new AutoCloseElementTag(this.templateMode, elementDefinitions);
        this.unmatchedCloseElementTagBuffer = new UnmatchedCloseElementTag(this.templateMode, elementDefinitions);

    }




    int size() {
        return this.queueSize;
    }



    void add(final IEngineTemplateHandlerEvent event) {
        insert(this.queueSize, event);
    }


    void insert(final int pos, final IEngineTemplateHandlerEvent event) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (event == null) {
            return;
        }

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[this.queue.length + DEFAULT_INITIAL_SIZE];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Make room for the new event
        System.arraycopy(this.queue, pos, this.queue, pos + 1, this.queueSize - pos);

        // Set the new event in its new position
        this.queue[pos] = event;

        this.queueSize++;

    }


    public void addQueue(final ITemplateHandlerEventQueue eventQueue, final boolean cloneAlways) {
        insertQueue(this.queueSize, eventQueue, cloneAlways);
    }


    public void insertQueue(final int pos, final ITemplateHandlerEventQueue eventQueue, final boolean cloneAlways) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (eventQueue == null) {
            return;
        }

        if (eventQueue instanceof TemplateHandlerEventQueue) {
            // It's a known implementation - we can take some shortcuts

            final TemplateHandlerEventQueue templateHandlerEventQueue = (TemplateHandlerEventQueue) eventQueue;

            if (this.queue.length <= (this.queueSize + templateHandlerEventQueue.queueSize)) {
                // We need to grow the queue!
                final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[this.queueSize + templateHandlerEventQueue.queueSize + DEFAULT_INITIAL_SIZE];
                Arrays.fill(newQueue, null);
                System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
                this.queue = newQueue;
            }

            // Make room for the new events (if necessary because pos < this.queueSize)
            System.arraycopy(this.queue, pos, this.queue, pos + templateHandlerEventQueue.queueSize, this.queueSize - pos);

            // Copy the new events to their new position
            int i = pos;
            int n = templateHandlerEventQueue.queueSize;
            while (n-- != 0) {
                this.queue[i] = asEngineEvent(templateHandlerEventQueue.queue[i - pos], cloneAlways);
                i++;
            }

            this.queueSize += templateHandlerEventQueue.queueSize;

            return;

        }

        // We don't know this implementation, so we will do it using the interface's methods

        final int eventQueueLen = eventQueue.size();
        for (int i = 0 ; i < eventQueueLen; i++) {
            insert(pos + i, asEngineEvent(eventQueue.get(i), cloneAlways));
        }

    }





    private IEngineTemplateHandlerEvent asEngineEvent(final ITemplateHandlerEvent event, final boolean cloneAlways) {

        if (event instanceof IText) {
            return Text.asEngineText(this.configuration, (IText)event, cloneAlways);
        }
        if (event instanceof IOpenElementTag) {
            return OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, (IOpenElementTag)event, cloneAlways);
        }
        if (event instanceof ICloseElementTag) {
            return CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, (ICloseElementTag)event, cloneAlways);
        }
        if (event instanceof IStandaloneElementTag) {
            return StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, (IStandaloneElementTag)event, cloneAlways);
        }
        if (event instanceof IAutoOpenElementTag) {
            return AutoOpenElementTag.asEngineAutoOpenElementTag(this.templateMode, this.configuration, (IAutoOpenElementTag)event, cloneAlways);
        }
        if (event instanceof IAutoCloseElementTag) {
            return AutoCloseElementTag.asEngineAutoCloseElementTag(this.templateMode, this.configuration, (IAutoCloseElementTag)event, cloneAlways);
        }
        if (event instanceof IUnmatchedCloseElementTag) {
            return UnmatchedCloseElementTag.asEngineUnmatchedCloseElementTag(this.templateMode, this.configuration, (IUnmatchedCloseElementTag)event, cloneAlways);
        }
        if (event instanceof IDocType) {
            return DocType.asEngineDocType(this.configuration, (IDocType)event, cloneAlways);
        }
        if (event instanceof IComment) {
            return Comment.asEngineComment(this.configuration, (IComment)event, cloneAlways);
        }
        if (event instanceof ICDATASection) {
            return CDATASection.asEngineCDATASection(this.configuration, (ICDATASection)event, cloneAlways);
        }
        if (event instanceof IXMLDeclaration) {
            return XMLDeclaration.asEngineXMLDeclaration(this.configuration, (IXMLDeclaration)event, cloneAlways);
        }
        if (event instanceof IProcessingInstruction) {
            return ProcessingInstruction.asEngineProcessingInstruction(this.configuration, (IProcessingInstruction)event, cloneAlways);
        }
        throw new TemplateProcessingException(
                "Cannot handle in queue event of type: " + event.getClass().getName());

    }






    void process(final ITemplateHandler handler, final boolean reset) {

        if (handler == null || this.queueSize == 0) {
            return;
        }

        IEngineTemplateHandlerEvent event;
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
            } else if (event instanceof AutoOpenElementTag) {
                this.autoOpenElementTagBuffer.resetAsCloneOf((AutoOpenElementTag) event);
                handler.handleAutoOpenElement(this.autoOpenElementTagBuffer);
            } else if (event instanceof AutoCloseElementTag) {
                this.autoCloseElementTagBuffer.resetAsCloneOf((AutoCloseElementTag) event);
                handler.handleAutoCloseElement(this.autoCloseElementTagBuffer);
            } else if (event instanceof UnmatchedCloseElementTag) {
                this.unmatchedCloseElementTagBuffer.resetAsCloneOf((UnmatchedCloseElementTag) event);
                handler.handleUnmatchedCloseElement(this.unmatchedCloseElementTagBuffer);
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
                handler.handleXmlDeclaration(this.xmlDeclarationBuffer);
            } else if (event instanceof ProcessingInstruction) {
                this.processingInstructionBuffer.resetAsCloneOf((ProcessingInstruction) event);
                handler.handleProcessingInstruction(this.processingInstructionBuffer);
            } else {
                throw new TemplateProcessingException(
                        "Cannot handle in queue event of type: " + event.getClass().getName());
            }

        }

        if (reset) {
            Arrays.fill(this.queue, null);
            this.queueSize = 0;
        }

    }




    void reset() {
        Arrays.fill(this.queue, null);
        this.queueSize = 0;
    }



    EngineEventQueue cloneEventQueue() {
        final EngineEventQueue clone = new EngineEventQueue(this.templateMode, this.configuration);
        clone.resetAsCloneOf(this);
        return clone;
    }


    void resetAsCloneOf(final EngineEventQueue original) {

        this.queueSize = original.queueSize;

        if (this.queue.length < original.queueSize) {
            this.queue = new IEngineTemplateHandlerEvent[original.queueSize];
        }

        System.arraycopy(original.queue, 0, this.queue, 0, original.queueSize);

        // No need to clone the buffers...

    }


}