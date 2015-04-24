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

import java.io.StringWriter;

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class Template {

    private static final int INITIAL_EVENT_QUEUE_SIZE = 100; // 100 events by default, will auto-grow

    private IEngineConfiguration configuration;
    private TemplateMode templateMode;
    private TemplateResolution templateResolution;
    private EngineEventQueue queue;


    // TODO Maybe this class should be used instead of the ITemplateHandlerEventQueue interface and implementations?


    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     */
    public Template(final ITemplateProcessingContext processingContext) {
        super();
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(processingContext.getConfiguration(), "Engine Configuration returned by Processing Context cannot be null");
        Validate.notNull(processingContext.getTemplateMode(), "Template Mode returned by Processing Context cannot be null");
        Validate.notNull(processingContext.getTemplateResolution(), "Template Resolution returned by Processing Context cannot be null");
        this.configuration = processingContext.getConfiguration();
        this.templateMode = processingContext.getTemplateMode();
        this.templateResolution = processingContext.getTemplateResolution();
        this.queue = new EngineEventQueue(this.configuration, this.templateMode, INITIAL_EVENT_QUEUE_SIZE);
    }


    public TemplateResolution getTemplateResolution() {
        return this.templateResolution;
    }


    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }



    void add(final IEngineTemplateHandlerEvent event) {
        this.queue.add(event);
    }



    void process(final ITemplateHandler templateHandler) {

        // Queued events themselves will not be cloned, our events will remain as the master ones, and the new EngineEventQueue will use buffers
        final EngineEventQueue eventQueue = this.queue.cloneEventQueue();

        // Process the new, cloned queue
        eventQueue.process(templateHandler, false);

    }



    public String computeMarkup() {
        final StringWriter writer = new StringWriter();
        final OutputTemplateHandler outputTemplateHandler = new OutputTemplateHandler(writer);
        process(outputTemplateHandler);
        return writer.toString();
    }



    @Override
    public String toString() {
        return computeMarkup();
    }

    
}