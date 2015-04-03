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




    public TemplateHandlerEventQueue() {
        this(DEFAULT_INITIAL_SIZE);
    }


    public TemplateHandlerEventQueue(final int initialSize) {

        super();

        Validate.isTrue(initialSize > 0, "Queue initial size must be greater than zero");

        this.queue = new ITemplateHandlerEvent[initialSize];
        Arrays.fill(this.queue, null);

    }


    public int size() {
        return this.queueSize;
    }


    public ITemplateHandlerEvent get(final int pos) {
        if (pos >= this.queueSize) {
            throw new IndexOutOfBoundsException("Requested position " + pos + " of event queue with size " + this.queueSize);
        }
        return this.queue[pos];
    }


    public void add(final ITemplateHandlerEvent event) {

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final ITemplateHandlerEvent[] newQueue = new ITemplateHandlerEvent[this.queue.length + DEFAULT_INITIAL_SIZE];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        this.queue[this.queueSize] = event;
        this.queueSize++;

    }


    public void addAll(final ITemplateHandlerEventQueue eventQueue) {

        if (eventQueue == null) {
            return;
        }

        if (eventQueue instanceof TemplateHandlerEventQueue) {
            // We can take some shortcuts
            final TemplateHandlerEventQueue templateHandlerEventQueue = (TemplateHandlerEventQueue) eventQueue;

            if (this.queue.length <= (this.queueSize + templateHandlerEventQueue.queueSize)) {
                // We need to grow the queue!
                final ITemplateHandlerEvent[] newQueue = new ITemplateHandlerEvent[this.queueSize + templateHandlerEventQueue.queueSize + DEFAULT_INITIAL_SIZE];
                Arrays.fill(newQueue, null);
                System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
                this.queue = newQueue;
            }

            System.arraycopy(templateHandlerEventQueue.queue, 0, this.queue, this.queueSize, templateHandlerEventQueue.queueSize);
            this.queueSize += templateHandlerEventQueue.queueSize;
            return;

        }

        final int eventQueueLen = eventQueue.size();
        for (int i = 0 ; i < eventQueueLen; i++) {
            eventQueue.add(eventQueue.get(i));
        }

    }


    public void process(final ITemplateHandler handler) {

        if (handler == null) {
            return;
        }

        ITemplateHandlerEvent event;
        int n = this.queueSize;
        int i = 0;

        while (n-- != 0) {

            event = this.queue[i++];

            if (event instanceof IText) {
                handler.handleText((IText) event);
            } else if (event instanceof IOpenElementTag) {
                handler.handleOpenElement((IOpenElementTag) event);
            } else if (event instanceof ICloseElementTag) {
                handler.handleCloseElement((ICloseElementTag) event);
            } else if (event instanceof IStandaloneElementTag) {
                handler.handleStandaloneElement((IStandaloneElementTag) event);
            } else if (event instanceof IDocType) {
                handler.handleDocType((IDocType) event);
            } else if (event instanceof IComment) {
                handler.handleComment((IComment) event);
            } else if (event instanceof ICDATASection) {
                handler.handleCDATASection((ICDATASection) event);
            } else if (event instanceof IXMLDeclaration) {
                handler.handleXmlDeclaration((IXMLDeclaration) event);
            } else if (event instanceof IProcessingInstruction) {
                handler.handleProcessingInstruction((IProcessingInstruction) event);
            } else {
                throw new UnsupportedOperationException(
                        "Still not implemented! cannot handle in queue event of type: " + event.getClass().getName());
            }


        }

        Arrays.fill(this.queue, null);
        this.queueSize = 0;

    }


    public void reset() {
        Arrays.fill(this.queue, null);
        this.queueSize = 0;
    }

}