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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class TemplateHandlerEventQueue {

    private static final int QUEUE_SIZE = 20;

    private final ITemplateHandler handler;

    private int queueSize = 0;
    private ITemplateHandlerEvent[] queue;




    TemplateHandlerEventQueue(final ITemplateHandler handler) {

        super();

        this.handler = handler;
        this.queue = new ITemplateHandlerEvent[QUEUE_SIZE];
        Arrays.fill(this.queue, null);

    }



    void add(final ITemplateHandlerEvent event) {

        if (this.queue.length == this.queueSize) {
            // We need to grow the queue!
            final ITemplateHandlerEvent[] newQueue = new ITemplateHandlerEvent[this.queue.length + QUEUE_SIZE];
            Arrays.fill(newQueue, null);
            System.arraycopy(this.queue, 0, newQueue, 0, this.queueSize);
            this.queue = newQueue;
        }

        this.queue[this.queueSize] = event;
        this.queueSize++;

    }


    void processQueue() {

        ITemplateHandlerEvent event;
        int n = this.queueSize;
        int i = 0;

        while (n-- != 0) {

            event = this.queue[i++];

            if (event instanceof IText) {
                this.handler.handleText((IText) event);
            } else {
                throw new UnsupportedOperationException("Still not implemented! Only support for queuing Texts has been implemented...");
            }


        }

        Arrays.fill(this.queue, null);
        this.queueSize = 0;

    }



}