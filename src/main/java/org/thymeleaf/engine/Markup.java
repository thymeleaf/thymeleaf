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
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class Markup implements IMarkup {

    private static final int INITIAL_EVENT_QUEUE_SIZE = 100; // 100 events by default, will auto-grow

    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final EngineEventQueue queue;


    // TODO Complete the API of this class: insert, etc. And also create these objects from the IModelFactory
    // (now IMarkupFactory?) and also from a String representing the markup --> that should go to the factory?
    // AND ALSO create a CacheableMarkup from this!!


    public Markup(final IEngineConfiguration configuration, final TemplateMode templateMode) {
        super();
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        // Validity CAN be null
        this.configuration = configuration;
        this.templateMode = templateMode;
        this.queue = new EngineEventQueue(this.configuration, this.templateMode, INITIAL_EVENT_QUEUE_SIZE);
    }


    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }



    public void add(final ITemplateHandlerEvent event) {
        this.queue.add(asEngineEvent(this.configuration, this.templateMode, event, true));
    }



    // Note we don't want to put this visibility to public, because we want to access the internal queue only from
    // the engine...
    EngineEventQueue getEventQueue() {
        return this.queue;
    }



    void process(final ITemplateHandler templateHandler) {

        // Queued events themselves will not be cloned, our events will remain as the master ones, and the new EngineEventQueue will use buffers
        final EngineEventQueue eventQueue = this.queue.cloneEventQueue();

        // Process the new, cloned queue
        eventQueue.process(templateHandler, false);

    }



    public final String computeMarkup() {
        final StringWriter writer = new StringWriter();
        final OutputTemplateHandler outputTemplateHandler = new OutputTemplateHandler(writer);
        process(outputTemplateHandler);
        return writer.toString();
    }


    public IMarkup cloneMarkup() {
        final Markup clone = new Markup(this.configuration, this.templateMode);
        clone.queue.resetAsCloneOf(this.queue);
        return clone;
    }



    @Override
    public String toString() {
        return computeMarkup();
    }




    private static IEngineTemplateHandlerEvent asEngineEvent(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final ITemplateHandlerEvent event, final boolean cloneAlways) {

        if (event instanceof IText) {
            return Text.asEngineText(configuration, (IText)event, cloneAlways);
        }
        if (event instanceof IOpenElementTag) {
            return OpenElementTag.asEngineOpenElementTag(templateMode, configuration, (IOpenElementTag)event, cloneAlways);
        }
        if (event instanceof ICloseElementTag) {
            return CloseElementTag.asEngineCloseElementTag(templateMode, configuration, (ICloseElementTag)event, cloneAlways);
        }
        if (event instanceof IStandaloneElementTag) {
            return StandaloneElementTag.asEngineStandaloneElementTag(templateMode, configuration, (IStandaloneElementTag)event, cloneAlways);
        }
        if (event instanceof IAutoOpenElementTag) {
            return AutoOpenElementTag.asEngineAutoOpenElementTag(templateMode, configuration, (IAutoOpenElementTag)event, cloneAlways);
        }
        if (event instanceof IAutoCloseElementTag) {
            return AutoCloseElementTag.asEngineAutoCloseElementTag(templateMode, configuration, (IAutoCloseElementTag)event, cloneAlways);
        }
        if (event instanceof IUnmatchedCloseElementTag) {
            return UnmatchedCloseElementTag.asEngineUnmatchedCloseElementTag(templateMode, configuration, (IUnmatchedCloseElementTag)event, cloneAlways);
        }
        if (event instanceof IDocType) {
            return DocType.asEngineDocType(configuration, (IDocType)event, cloneAlways);
        }
        if (event instanceof IComment) {
            return Comment.asEngineComment(configuration, (IComment)event, cloneAlways);
        }
        if (event instanceof ICDATASection) {
            return CDATASection.asEngineCDATASection(configuration, (ICDATASection)event, cloneAlways);
        }
        if (event instanceof IXMLDeclaration) {
            return XMLDeclaration.asEngineXMLDeclaration(configuration, (IXMLDeclaration)event, cloneAlways);
        }
        if (event instanceof IProcessingInstruction) {
            return ProcessingInstruction.asEngineProcessingInstruction(configuration, (IProcessingInstruction)event, cloneAlways);
        }
        throw new TemplateProcessingException(
                "Cannot handle in queue event of type: " + event.getClass().getName());

    }



}