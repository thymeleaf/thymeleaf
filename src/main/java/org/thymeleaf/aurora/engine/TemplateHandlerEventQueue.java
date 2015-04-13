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
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class TemplateHandlerEventQueue implements ITemplateHandlerEventQueue {

    private static final int DEFAULT_INITIAL_SIZE = 20;

    private int queueSize = 0;
    private ITemplateHandlerEvent[] queue;

    private final ITextRepository textRepository;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final TemplateMode templateMode;

    private final boolean buffersEnabled;

    private final Text text;
    private final Comment comment;
    private final CDATASection cdataSection;
    private final DocType docType;
    private final ProcessingInstruction processingInstruction;
    private final XMLDeclaration xmlDeclaration;

    private final OpenElementTag openElementTag;
    private final StandaloneElementTag standaloneElementTag;
    private final CloseElementTag closeElementTag;
    private final AutoOpenElementTag autoOpenElementTag;
    private final AutoCloseElementTag autoCloseElementTag;
    private final UnmatchedCloseElementTag unmatchedCloseElementTag;



    public TemplateHandlerEventQueue() {
        this(DEFAULT_INITIAL_SIZE);
    }


    public TemplateHandlerEventQueue(final int initialSize) {

        super();

        Validate.isTrue(initialSize > 0, "Queue initial size must be greater than zero");

        this.queue = new ITemplateHandlerEvent[initialSize];
        Arrays.fill(this.queue, null);

        this.textRepository = null;
        this.elementDefinitions = null;
        this.attributeDefinitions = null;
        this.templateMode = null;

        this.buffersEnabled = false;

        // This constructor does not provide the required data to build the buffers, so we will have none
        this.text = null;
        this.comment = null;
        this.cdataSection = null;
        this.docType = null;
        this.processingInstruction = null;
        this.xmlDeclaration = null;

        this.openElementTag = null;
        this.standaloneElementTag = null;
        this.closeElementTag = null;
        this.autoOpenElementTag = null;
        this.autoCloseElementTag = null;
        this.unmatchedCloseElementTag = null;

    }


    TemplateHandlerEventQueue(final int initialSize,
                              final ITextRepository textRepository,
                              final ElementDefinitions elementDefinitions,
                              final AttributeDefinitions attributeDefinitions,
                              final TemplateMode templateMode) {

        super();

        Validate.isTrue(initialSize > 0, "Queue initial size must be greater than zero");

        this.queue = new ITemplateHandlerEvent[initialSize];
        Arrays.fill(this.queue, null);

        this.textRepository = textRepository;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateMode = templateMode;

        if (textRepository != null && elementDefinitions != null && attributeDefinitions != null) {
            // We will be using these as objectual buffers in order to avoid creating too many objects

            this.buffersEnabled = true;

            this.text = new Text(this.textRepository);
            this.comment = new Comment(this.textRepository);
            this.cdataSection = new CDATASection(this.textRepository);
            this.docType = new DocType(this.textRepository);
            this.processingInstruction = new ProcessingInstruction(this.textRepository);
            this.xmlDeclaration = new XMLDeclaration(this.textRepository);

            this.openElementTag = new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
            this.standaloneElementTag = new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
            this.closeElementTag = new CloseElementTag(this.templateMode, this.elementDefinitions);
            this.autoOpenElementTag = new AutoOpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
            this.autoCloseElementTag = new AutoCloseElementTag(this.templateMode, this.elementDefinitions);
            this.unmatchedCloseElementTag = new UnmatchedCloseElementTag(this.templateMode, this.elementDefinitions);

        } else {

            this.buffersEnabled = false;

            this.text = null;
            this.comment = null;
            this.cdataSection = null;
            this.docType = null;
            this.processingInstruction = null;
            this.xmlDeclaration = null;

            this.openElementTag = null;
            this.standaloneElementTag = null;
            this.closeElementTag = null;
            this.autoOpenElementTag = null;
            this.autoCloseElementTag = null;
            this.unmatchedCloseElementTag = null;

        }

    }




    public int size() {
        return this.queueSize;
    }


    public ITemplateHandlerEvent get(final int pos) {
        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }
        return this.queue[pos];
    }


    public void add(final ITemplateHandlerEvent event) {
        insert(this.queueSize, event);
    }


    public void insert(final int pos, final ITemplateHandlerEvent event) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (event == null) {
            return;
        }

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final ITemplateHandlerEvent[] newQueue = new ITemplateHandlerEvent[this.queue.length + DEFAULT_INITIAL_SIZE];
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


    public void addAll(final ITemplateHandlerEventQueue eventQueue) {
        insertAll(this.queueSize, eventQueue);
    }


    public void insertAll(final int pos, final ITemplateHandlerEventQueue eventQueue) {

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
                final ITemplateHandlerEvent[] newQueue = new ITemplateHandlerEvent[this.queueSize + templateHandlerEventQueue.queueSize + DEFAULT_INITIAL_SIZE];
                Arrays.fill(newQueue, null);
                System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
                this.queue = newQueue;
            }

            // Make room for the new events (if necessary because pos < this.queueSize)
            System.arraycopy(this.queue, pos, this.queue, pos + templateHandlerEventQueue.queueSize, this.queueSize - pos);

            // Copy the new events to their new position
            System.arraycopy(templateHandlerEventQueue.queue, 0, this.queue, pos, templateHandlerEventQueue.queueSize);

            this.queueSize += templateHandlerEventQueue.queueSize;

            return;

        }

        // We don't know this implementation, so we will do it using the interface's methods

        final int eventQueueLen = eventQueue.size();
        for (int i = 0 ; i < eventQueueLen; i++) {
            eventQueue.insert(pos + i, eventQueue.get(i));
        }

    }




    public void process(final ITemplateHandler handler, final boolean reset) {
        if (this.buffersEnabled) {
            processWithBuffers(handler, reset);
        } else {
            processWithoutBuffers(handler, reset);
        }
    }


    private void processWithBuffers(final ITemplateHandler handler, final boolean reset) {

        if (handler == null || this.queueSize == 0) {
            return;
        }

        ITemplateHandlerEvent event;
        int n = this.queueSize;
        int i = 0;

        while (n-- != 0) {

            event = this.queue[i++];

            if (event instanceof IText) {
                this.text.setFromText((IText) event);
                handler.handleText(this.text);
            } else if (event instanceof IOpenElementTag) {
                this.openElementTag.setFromOpenElementTag((IOpenElementTag) event);
                handler.handleOpenElement(this.openElementTag);
            } else if (event instanceof ICloseElementTag) {
                this.closeElementTag.setFromCloseElementTag((ICloseElementTag) event);
                handler.handleCloseElement(this.closeElementTag);
            } else if (event instanceof IStandaloneElementTag) {
                this.standaloneElementTag.setFromStandaloneElementTag((IStandaloneElementTag) event);
                handler.handleStandaloneElement(this.standaloneElementTag);
            } else if (event instanceof IAutoOpenElementTag) {
                this.autoOpenElementTag.setFromAutoOpenElementTag((IAutoOpenElementTag) event);
                handler.handleAutoOpenElement(this.autoOpenElementTag);
            } else if (event instanceof IAutoCloseElementTag) {
                this.autoCloseElementTag.setFromAutoCloseElementTag((IAutoCloseElementTag) event);
                handler.handleAutoCloseElement(this.autoCloseElementTag);
            } else if (event instanceof IUnmatchedCloseElementTag) {
                this.unmatchedCloseElementTag.setFromUnmatchedCloseElementTag((IUnmatchedCloseElementTag) event);
                handler.handleUnmatchedCloseElement(this.unmatchedCloseElementTag);
            } else if (event instanceof IDocType) {
                this.docType.setFromDocType((IDocType) event);
                handler.handleDocType(this.docType);
            } else if (event instanceof IComment) {
                this.comment.setFromComment((IComment) event);
                handler.handleComment(this.comment);
            } else if (event instanceof ICDATASection) {
                this.cdataSection.setFromCDATASection((ICDATASection) event);
                handler.handleCDATASection(this.cdataSection);
            } else if (event instanceof IXMLDeclaration) {
                this.xmlDeclaration.setFromXMLDeclaration((IXMLDeclaration) event);
                handler.handleXmlDeclaration(this.xmlDeclaration);
            } else if (event instanceof IProcessingInstruction) {
                this.processingInstruction.setFromProcessingInstruction((IProcessingInstruction) event);
                handler.handleProcessingInstruction(this.processingInstruction);
            } else {
                throw new UnsupportedOperationException(
                        "Still not implemented! cannot handle in queue event of type: " + event.getClass().getName());
            }


        }

        if (reset) {
            Arrays.fill(this.queue, null);
            this.queueSize = 0;
        }

    }


    private void processWithoutBuffers(final ITemplateHandler handler, final boolean reset) {

        if (handler == null || this.queueSize == 0) {
            return;
        }

        ITemplateHandlerEvent event;
        int n = this.queueSize;
        int i = 0;

        while (n-- != 0) {

            event = this.queue[i++];

            if (event instanceof IText) {
                handler.handleText(((IText) event).cloneNode());
            } else if (event instanceof IOpenElementTag) {
                handler.handleOpenElement(((IOpenElementTag) event).cloneElementTag());
            } else if (event instanceof ICloseElementTag) {
                handler.handleCloseElement(((ICloseElementTag) event).cloneElementTag());
            } else if (event instanceof IStandaloneElementTag) {
                handler.handleStandaloneElement(((IStandaloneElementTag) event).cloneElementTag());
            } else if (event instanceof IAutoOpenElementTag) {
                handler.handleAutoOpenElement(((IAutoOpenElementTag) event).cloneElementTag());
            } else if (event instanceof IAutoCloseElementTag) {
                handler.handleAutoCloseElement(((IAutoCloseElementTag) event).cloneElementTag());
            } else if (event instanceof IUnmatchedCloseElementTag) {
                handler.handleUnmatchedCloseElement(((IUnmatchedCloseElementTag) event).cloneElementTag());
            } else if (event instanceof IDocType) {
                handler.handleDocType(((IDocType) event).cloneNode());
            } else if (event instanceof IComment) {
                handler.handleComment(((IComment) event).cloneNode());
            } else if (event instanceof ICDATASection) {
                handler.handleCDATASection(((ICDATASection) event).cloneNode());
            } else if (event instanceof IXMLDeclaration) {
                handler.handleXmlDeclaration(((IXMLDeclaration) event).cloneNode());
            } else if (event instanceof IProcessingInstruction) {
                handler.handleProcessingInstruction(((IProcessingInstruction) event).cloneNode());
            } else {
                throw new UnsupportedOperationException(
                        "Still not implemented! cannot handle in queue event of type: " + event.getClass().getName());
            }


        }

        if (reset) {
            Arrays.fill(this.queue, null);
            this.queueSize = 0;
        }

    }



    public void reset() {
        Arrays.fill(this.queue, null);
        this.queueSize = 0;
    }



    public ITemplateHandlerEventQueue cloneQueue(final boolean deep) {

        final TemplateHandlerEventQueue clone =
                new TemplateHandlerEventQueue(this.queueSize, this.textRepository, this.elementDefinitions, this.attributeDefinitions, this.templateMode);

        if (!deep) {

            System.arraycopy(this.queue, 0, clone.queue, 0, this.queueSize);

        } else {

            ITemplateHandlerEvent event;
            int n = this.queueSize;
            while (n-- != 0) {

                event = this.queue[n];

                if (event instanceof IText) {
                    clone.queue[n] = ((IText) event).cloneNode();
                } else if (event instanceof IOpenElementTag) {
                    clone.queue[n] = ((IOpenElementTag) event).cloneElementTag();
                } else if (event instanceof ICloseElementTag) {
                    clone.queue[n] = ((ICloseElementTag) event).cloneElementTag();
                } else if (event instanceof IStandaloneElementTag) {
                    clone.queue[n] = ((IStandaloneElementTag) event).cloneElementTag();
                } else if (event instanceof IAutoOpenElementTag) {
                    clone.queue[n] = ((IAutoOpenElementTag) event).cloneElementTag();
                } else if (event instanceof IAutoCloseElementTag) {
                    clone.queue[n] = ((IAutoCloseElementTag) event).cloneElementTag();
                } else if (event instanceof IUnmatchedCloseElementTag) {
                    clone.queue[n] = ((IUnmatchedCloseElementTag) event).cloneElementTag();
                } else if (event instanceof IDocType) {
                    clone.queue[n] = ((IDocType) event).cloneNode();
                } else if (event instanceof IComment) {
                    clone.queue[n] = ((IComment) event).cloneNode();
                } else if (event instanceof ICDATASection) {
                    clone.queue[n] = ((ICDATASection) event).cloneNode();
                } else if (event instanceof IXMLDeclaration) {
                    clone.queue[n] = ((IXMLDeclaration) event).cloneNode();
                } else if (event instanceof IProcessingInstruction) {
                    clone.queue[n] = ((IProcessingInstruction) event).cloneNode();
                } else {
                    throw new UnsupportedOperationException(
                            "Still not implemented! cannot handle in queue event of type: " + event.getClass().getName());
                }

            }

        }

        clone.queueSize = this.queueSize;

        return clone;

    }

}