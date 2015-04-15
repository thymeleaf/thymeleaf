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

    private static final int DEFAULT_INITIAL_SIZE = 10;

    int queueSize = 0;
    ITemplateHandlerEvent[] queue;



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




    public void reset() {
        Arrays.fill(this.queue, null);
        this.queueSize = 0;
    }


}