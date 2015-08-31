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

    private static final int DEFAULT_INITIAL_SIZE = 50;

    private int queueSize = 0;
    private IEngineTemplateHandlerEvent[] queue; // We use the interface, but not all implementations will be allowed

    private final TemplateMode templateMode;
    private final IEngineConfiguration configuration;

    private DocumentStart documentStartBuffer = null;
    private DocumentEnd documentEndBuffer = null;

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

        if (initialSize > 0) {
            this.queue = new IEngineTemplateHandlerEvent[initialSize];
            Arrays.fill(this.queue, null);
        }

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



    void add(final IEngineTemplateHandlerEvent event, final boolean cloneAlways) {
        insert(this.queueSize, event, cloneAlways);
    }


    void insert(final int pos, final IEngineTemplateHandlerEvent event, final boolean cloneAlways) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (event == null) {
            return;
        }

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[Math.min(this.queue.length + 25, this.queue.length * 2)];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Make room for the new event
        System.arraycopy(this.queue, pos, this.queue, pos + 1, this.queueSize - pos);

        // Set the new event in its new position
        this.queue[pos] = (cloneAlways? cloneEngineEvent(event) : event);

        this.queueSize++;

    }


    void addMarkup(final IMarkup imarkup) {
        insertMarkup(this.queueSize, imarkup);
    }


    void insertMarkup(final int pos, final IMarkup imarkup) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        if (imarkup == null) {
            return;
        }

        if (!this.configuration.equals(imarkup.getConfiguration())) {
            throw new TemplateProcessingException(
                    "Cannot add markup of class " + imarkup.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Engine Configuration.");
        }

        if (this.templateMode != imarkup.getTemplateMode()) {
            throw new TemplateProcessingException(
                    "Cannot add markup of class " + imarkup.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Mode: " + imarkup.getTemplateMode() + " instead of " +
                    "the current " + this.templateMode);
        }


        final Markup markup;
        if (imarkup instanceof ParsedTemplateMarkup) {
            // This is forbidden - we cannot add something that is not a fragment, but an entire, top level template
            throw new TemplateProcessingException(
                    "Cannot add as fragment an entire, top-level template. The specified object should have been " +
                    "parsed as fragment instead of template");
        } else if (imarkup instanceof ImmutableMarkup) {
            // No need to clone - argument is an immutable piece of markup and therefore using it without cloning will
            // produce no side/undesired effects
            markup = ((ImmutableMarkup) imarkup).getInternalMarkup();
        } else if (imarkup instanceof Markup) {
            // This implementation does not directly come from the parser nor is immutable, so we must clone its events
            // to avoid interactions.
            markup = imarkup.cloneMarkup();
        } else {
            throw new TemplateProcessingException(
                    "Unrecognized implementation of the " + IMarkup.class.getName() + " interface: " + imarkup.getClass().getName());
        }

        final EngineEventQueue markupQueue = markup.getEventQueue();

        if (this.queue.length <= (this.queueSize + markupQueue.queueSize)) {
            // We need to grow the queue!
            final IEngineTemplateHandlerEvent[] newQueue = new IEngineTemplateHandlerEvent[Math.max(this.queueSize + markupQueue.queueSize, this.queue.length + 25)];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        // Make room for the new events (if necessary because pos < this.queueSize)
        System.arraycopy(this.queue, pos, this.queue, pos + markupQueue.queueSize, this.queueSize - pos);

        // Copy the new events to their new position (no cloning needed here - if needed it would have been already done)
        System.arraycopy(markupQueue.queue, 0, this.queue, pos, markupQueue.queueSize);

        this.queueSize += markupQueue.queueSize;

    }



    void remove(final int pos) {

        if (pos < 0 && pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }

        System.arraycopy(this.queue, pos + 1, this.queue, pos, this.queueSize - (pos + 1));

        this.queueSize--;

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
            } else if (event instanceof DocumentStart) {
                handler.handleDocumentStart(bufferize((DocumentStart) event));
            } else if (event instanceof DocumentEnd) {
                handler.handleDocumentEnd(bufferize((DocumentEnd) event));
            } else {
                throw new TemplateProcessingException(
                        "Cannot handle in queue event of type: " + event.getClass().getName());
            }

        }

        if (reset) {
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



    DocumentStart bufferize(final DocumentStart event) {
        if (this.documentStartBuffer == null) {
            this.documentStartBuffer = new DocumentStart();
        }
        this.documentStartBuffer.resetAsCloneOf(event);
        return this.documentStartBuffer;
    }



    DocumentEnd bufferize(final DocumentEnd event) {
        if (this.documentEndBuffer == null) {
            this.documentEndBuffer = new DocumentEnd();
        }
        this.documentEndBuffer.resetAsCloneOf(event);
        return this.documentEndBuffer;
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
            this.queue = original.queue;
            return;
        }

        if (this.queue.length < original.queueSize) {
            this.queue = new IEngineTemplateHandlerEvent[Math.max(DEFAULT_INITIAL_SIZE, original.queueSize)];
        }

        if (!cloneEvents) {
            System.arraycopy(original.queue, 0, this.queue, 0, original.queueSize);
        } else {
            for (int i = 0; i < original.queueSize; i++) {
                this.queue[i] = cloneEngineEvent(original.queue[i]);
            }
        }

        // No need to clone the buffers...

    }


    private static IEngineTemplateHandlerEvent cloneEngineEvent(final IEngineTemplateHandlerEvent event) {

        if (event instanceof Text) {
            return ((Text)event).cloneNode();
        } else if (event instanceof OpenElementTag) {
            return ((OpenElementTag)event).cloneElementTag();
        } else if (event instanceof CloseElementTag) {
            return ((CloseElementTag)event).cloneElementTag();
        } else if (event instanceof StandaloneElementTag) {
            return ((StandaloneElementTag)event).cloneElementTag();
        } else if (event instanceof AutoOpenElementTag) {
            return ((AutoOpenElementTag)event).cloneElementTag();
        } else if (event instanceof AutoCloseElementTag) {
            return ((AutoCloseElementTag)event).cloneElementTag();
        } else if (event instanceof UnmatchedCloseElementTag) {
            return ((UnmatchedCloseElementTag)event).cloneElementTag();
        } else if (event instanceof Comment) {
            return ((Comment)event).cloneNode();
        } else if (event instanceof DocType) {
            return ((DocType)event).cloneNode();
        } else if (event instanceof XMLDeclaration) {
            return ((XMLDeclaration)event).cloneNode();
        } else if (event instanceof CDATASection) {
            return ((CDATASection)event).cloneNode();
        } else if (event instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)event).cloneNode();
        } else if (event instanceof DocumentStart) {
            return ((DocumentStart)event).cloneEvent();
        } else if (event instanceof DocumentEnd) {
            return ((DocumentEnd)event).cloneEvent();
        }

        throw new TemplateProcessingException(
                "Unrecognized implementation of " + IEngineTemplateHandlerEvent.class.getName() + ": " +
                event.getClass().getName());

    }


}