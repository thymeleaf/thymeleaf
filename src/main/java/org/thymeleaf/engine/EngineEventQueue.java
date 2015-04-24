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

import java.util.Arrays;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAutoCloseElementTag;
import org.thymeleaf.model.IAutoOpenElementTag;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IUnmatchedCloseElementTag;
import org.thymeleaf.model.IXMLDeclaration;
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
     * This queue WILL ONLY CONTAIN ENGINE-BASED IMPLEMENTATIONS of the ITemplateHandlerEvent interface
     */

    private static final int DEFAULT_INITIAL_SIZE = 20;

    private int queueSize = 0;
    private IEngineTemplateHandlerEvent[] queue; // We use the interface, but not all implementations will be allowed

    private final TemplateMode templateMode;
    private final IEngineConfiguration configuration;
    private final int initialSize;

    private Text textBuffer = null;
    private Comment commentBuffer = null;
    private CDATASection cdataSectionBuffer = null;
    private DocType docTypeBuffer = null;
    private ProcessingInstruction processingInstructionBuffer = null;
    private XMLDeclaration xmlDeclarationBuffer = null;

    private OpenElementTag openElementTagBuffer = null;
    private StandaloneElementTag standaloneElementTagBuffer = null;
    private CloseElementTag closeElementTagBuffer = null;
    private AutoOpenElementTag autoOpenElementTagBuffer = null;
    private AutoCloseElementTag autoCloseElementTagBuffer = null;
    private UnmatchedCloseElementTag unmatchedCloseElementTagBuffer = null;



    EngineEventQueue(final IEngineConfiguration configuration, final TemplateMode templateMode) {
        this(configuration, templateMode, DEFAULT_INITIAL_SIZE);
    }


    EngineEventQueue(final IEngineConfiguration configuration, final TemplateMode templateMode, final int initialSize) {

        super();

        this.queue = new IEngineTemplateHandlerEvent[initialSize];
        Arrays.fill(this.queue, null);

        this.initialSize = initialSize;
        this.templateMode = templateMode;
        this.configuration = configuration;


    }




    int size() {
        return this.queueSize;
    }


    IEngineTemplateHandlerEvent get(final int pos) {
        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }
        return this.queue[pos];
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
            final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[this.queue.length + Math.max(this.initialSize / 2, DEFAULT_INITIAL_SIZE)];
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
                final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[this.queueSize + templateHandlerEventQueue.queueSize + Math.max(this.initialSize / 2, DEFAULT_INITIAL_SIZE)];
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
                handler.handleText(bufferize((Text) event));
            } else if (event instanceof OpenElementTag) {
                handler.handleOpenElement(bufferize((OpenElementTag) event));
            } else if (event instanceof CloseElementTag) {
                handler.handleCloseElement(bufferize((CloseElementTag) event));
            } else if (event instanceof StandaloneElementTag) {
                handler.handleStandaloneElement(bufferize((StandaloneElementTag) event));
            } else if (event instanceof AutoOpenElementTag) {
                handler.handleAutoOpenElement(bufferize((AutoOpenElementTag) event));
            } else if (event instanceof AutoCloseElementTag) {
                handler.handleAutoCloseElement(bufferize((AutoCloseElementTag) event));
            } else if (event instanceof UnmatchedCloseElementTag) {
                handler.handleUnmatchedCloseElement(bufferize((UnmatchedCloseElementTag) event));
            } else if (event instanceof DocType) {
                handler.handleDocType(bufferize((DocType) event));
            } else if (event instanceof Comment) {
                handler.handleComment(bufferize((Comment) event));
            } else if (event instanceof CDATASection) {
                handler.handleCDATASection(bufferize((CDATASection) event));
            } else if (event instanceof XMLDeclaration) {
                handler.handleXMLDeclaration(bufferize((XMLDeclaration) event));
            } else if (event instanceof ProcessingInstruction) {
                handler.handleProcessingInstruction(bufferize((ProcessingInstruction) event));
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



    Text bufferize(final Text event) {
        if (this.textBuffer == null) {
            this.textBuffer = new Text(this.configuration.getTextRepository());
        }
        this.textBuffer.resetAsCloneOf(event);
        return this.textBuffer;
    }



    CDATASection bufferize(final CDATASection event) {
        if (this.cdataSectionBuffer == null) {
            this.cdataSectionBuffer = new CDATASection(this.configuration.getTextRepository());
        }
        this.cdataSectionBuffer.resetAsCloneOf(event);
        return this.cdataSectionBuffer;
    }



    Comment bufferize(final Comment event) {
        if (this.commentBuffer == null) {
            this.commentBuffer = new Comment(this.configuration.getTextRepository());
        }
        this.commentBuffer.resetAsCloneOf(event);
        return this.commentBuffer;
    }



    DocType bufferize(final DocType event) {
        if (this.docTypeBuffer == null) {
            this.docTypeBuffer = new DocType(this.configuration.getTextRepository());
        }
        this.docTypeBuffer.resetAsCloneOf(event);
        return this.docTypeBuffer;
    }



    ProcessingInstruction bufferize(final ProcessingInstruction event) {
        if (this.processingInstructionBuffer == null) {
            this.processingInstructionBuffer = new ProcessingInstruction(this.configuration.getTextRepository());
        }
        this.processingInstructionBuffer.resetAsCloneOf(event);
        return this.processingInstructionBuffer;
    }



    XMLDeclaration bufferize(final XMLDeclaration event) {
        if (this.xmlDeclarationBuffer == null) {
            this.xmlDeclarationBuffer = new XMLDeclaration(this.configuration.getTextRepository());
        }
        this.xmlDeclarationBuffer.resetAsCloneOf(event);
        return this.xmlDeclarationBuffer;
    }



    StandaloneElementTag bufferize(final StandaloneElementTag event) {
        if (this.standaloneElementTagBuffer == null) {
            this.standaloneElementTagBuffer =
                    new StandaloneElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        }
        this.standaloneElementTagBuffer.resetAsCloneOf(event);
        return this.standaloneElementTagBuffer;
    }



    OpenElementTag bufferize(final OpenElementTag event) {
        if (this.openElementTagBuffer == null) {
            this.openElementTagBuffer =
                    new OpenElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        }
        this.openElementTagBuffer.resetAsCloneOf(event);
        return this.openElementTagBuffer;
    }



    CloseElementTag bufferize(final CloseElementTag event) {
        if (this.closeElementTagBuffer == null) {
            this.closeElementTagBuffer =
                    new CloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
        }
        this.closeElementTagBuffer.resetAsCloneOf(event);
        return this.closeElementTagBuffer;
    }



    AutoOpenElementTag bufferize(final AutoOpenElementTag event) {
        if (this.autoOpenElementTagBuffer == null) {
            this.autoOpenElementTagBuffer =
                    new AutoOpenElementTag(this.templateMode, this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        }
        this.autoOpenElementTagBuffer.resetAsCloneOf(event);
        return this.autoOpenElementTagBuffer;
    }



    AutoCloseElementTag bufferize(final AutoCloseElementTag event) {
        if (this.autoCloseElementTagBuffer == null) {
            this.autoCloseElementTagBuffer =
                    new AutoCloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
        }
        this.autoCloseElementTagBuffer.resetAsCloneOf(event);
        return this.autoCloseElementTagBuffer;
    }



    UnmatchedCloseElementTag bufferize(final UnmatchedCloseElementTag event) {
        if (this.unmatchedCloseElementTagBuffer == null) {
            this.unmatchedCloseElementTagBuffer =
                    new UnmatchedCloseElementTag(this.templateMode, this.configuration.getElementDefinitions());
        }
        this.unmatchedCloseElementTagBuffer.resetAsCloneOf(event);
        return this.unmatchedCloseElementTagBuffer;
    }





    void reset() {
        Arrays.fill(this.queue, null);
        this.queueSize = 0;
    }



    EngineEventQueue cloneEventQueue() {
        final EngineEventQueue clone = new EngineEventQueue(this.configuration, this.templateMode, this.queueSize);
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