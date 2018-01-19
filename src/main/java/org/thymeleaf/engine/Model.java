/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class Model implements IModel {

    private static final int INITIAL_EVENT_QUEUE_SIZE = 50; // 50 events by default, will auto-grow

    private IEngineConfiguration configuration;
    private TemplateMode templateMode;

    IEngineTemplateEvent[] queue;
    int queueSize;




    Model(final IEngineConfiguration configuration, final TemplateMode templateMode) {

        super();

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");

        this.templateMode = templateMode;
        this.configuration = configuration;

        this.queue = new IEngineTemplateEvent[INITIAL_EVENT_QUEUE_SIZE];
        Arrays.fill(this.queue, null);

        this.queueSize = 0;

    }


    Model(final IModel model) {

        super();

        Validate.notNull(model, "Model cannot be null");

        this.configuration = model.getConfiguration();
        this.templateMode = model.getTemplateMode();

        if (model instanceof Model) {

            final Model mmodel = (Model) model;
            this.queue = mmodel.queue.clone();
            this.queueSize = mmodel.queueSize;

        } else if (model instanceof TemplateModel) {

            final TemplateModel templateModel = (TemplateModel) model;
            this.queue = new IEngineTemplateEvent[templateModel.queue.length + INITIAL_EVENT_QUEUE_SIZE/2];
            System.arraycopy(templateModel.queue, 1, this.queue, 0, templateModel.queue.length - 2);
            this.queueSize = templateModel.queue.length - 2;

        } else {

            this.queue = new IEngineTemplateEvent[INITIAL_EVENT_QUEUE_SIZE];
            Arrays.fill(this.queue, null);
            this.queueSize = 0;
            insertModel(0, model);

        }

    }




    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public int size() {
        return this.queueSize;
    }


    public ITemplateEvent get(final int pos) {
        return this.queue[pos];
    }


    public void add(final ITemplateEvent event) {
        insert(this.queueSize, event);
    }


    public void insert(final int pos, final ITemplateEvent event) {

        if (event == null) {
            return;
        }

        final IEngineTemplateEvent engineEvent = asEngineEvent(event);

        // Check that the event that is going to be inserted is not a template start/end
        if (engineEvent == TemplateStart.TEMPLATE_START_INSTANCE || engineEvent == TemplateEnd.TEMPLATE_END_INSTANCE) {
            throw new TemplateProcessingException(
                    "Cannot insert event of type TemplateStart/TemplateEnd. These " +
                    "events can only be added to models internally during template parsing.");
        }

        // Check there is room for a new event, or grow the queue if not
        if (this.queue.length == this.queueSize) {
            this.queue = Arrays.copyOf(this.queue, this.queue.length + INITIAL_EVENT_QUEUE_SIZE/2);
        }

        // Make room for the new event
        if (pos != this.queueSize) {
            System.arraycopy(this.queue, pos, this.queue, pos + 1, this.queueSize - pos);
        }

        // Set the new event in its new position
        this.queue[pos] = engineEvent;

        this.queueSize++;

    }


    public void replace(final int pos, final ITemplateEvent event) {

        if (event == null) {
            return;
        }

        final IEngineTemplateEvent engineEvent = asEngineEvent(event);

        // Check that the event that is going to be inserted is not a template start/end
        if (engineEvent == TemplateStart.TEMPLATE_START_INSTANCE || engineEvent == TemplateEnd.TEMPLATE_END_INSTANCE) {
            throw new TemplateProcessingException(
                    "Cannot insert event of type TemplateStart/TemplateEnd. These " +
                    "events can only be added to models internally during template parsing.");
        }

        // Set the new event in its new position
        this.queue[pos] = engineEvent;

    }


    public void addModel(final IModel model) {
        insertModel(this.queueSize, model);
    }


    public void insertModel(final int pos, final IModel model) {

        if (model == null || model.size() == 0) {
            return;
        }

        if (this.configuration != model.getConfiguration()) {
            throw new TemplateProcessingException(
                    "Cannot add model of class " + model.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Engine Configuration.");
        }

        if (this.templateMode != model.getTemplateMode()) {
            throw new TemplateProcessingException(
                    "Cannot add model of class " + model.getClass().getName() + " to the current template, as " +
                    "it was created using a different Template Mode: " + model.getTemplateMode() + " instead of " +
                    "the current " + this.templateMode);
        }

        if (this.queue.length <= (this.queueSize + model.size())) {
            // We need to grow the queue!
            this.queue = Arrays.copyOf(this.queue, Math.max(this.queueSize + model.size(), this.queue.length + INITIAL_EVENT_QUEUE_SIZE/2));
        }

        if (model instanceof TemplateModel) {
            doInsertTemplateModel(pos, (TemplateModel)model);
        } else if (model instanceof Model) {
            doInsertModel(pos, (Model)model);
        } else {
            doInsertOtherModel(pos, model);
        }

    }


    private void doInsertModel(final int pos, final Model model) {
        // Make room for the new events (if necessary because pos < this.queueSize)
        System.arraycopy(this.queue, pos, this.queue, pos + model.queueSize, this.queueSize - pos);
        // Copy the new events to their new position
        System.arraycopy(model.queue, 0, this.queue, pos, model.queueSize);
        this.queueSize += model.queueSize;
    }


    private void doInsertTemplateModel(final int pos, final TemplateModel model) {
        // We compute the insertion size by subtracting the TemplateStart/TemplateEnd events
        final int insertionSize = model.queue.length - 2;
        // Make room for the new events (if necessary because pos < this.queueSize)
        System.arraycopy(this.queue, pos, this.queue, pos + insertionSize, this.queueSize - pos);
        // Copy the new events to their new position
        System.arraycopy(model.queue, 1, this.queue, pos, insertionSize);
        this.queueSize += insertionSize;
    }


    private void doInsertOtherModel(final int pos, final IModel model) {
        // We know nothing about this model implementation, so we will use the public interface methods
        final int modelSize = model.size();
        for (int i = 0; i < modelSize; i++) {
            insert(pos + i, model.get(i));
        }
    }


    public void remove(final int pos) {
        System.arraycopy(this.queue, pos + 1, this.queue, pos, this.queueSize - (pos + 1));
        this.queueSize--;
    }


    public void reset() {
        this.queueSize = 0;
    }



    void process(final ITemplateHandler handler) {
        for (int i = 0; i < this.queueSize; i++) {
            this.queue[i].beHandled(handler);
        }
    }


    int process(final ITemplateHandler handler, final int offset, final TemplateFlowController controller) {

        if (controller == null) {
            process(handler);
            return this.queueSize;
        }

        if (this.queueSize == 0 || offset >= this.queueSize) {
            return 0;
        }

        int i = offset;
        while (i < this.queueSize && !controller.stopProcessing) {
            this.queue[i++].beHandled(handler);
        }

        return (i - offset);

    }





    public IModel cloneModel() {
        return new Model(this);
    }



    void resetAsCloneOf(final Model model) {
        this.configuration = model.configuration;
        this.templateMode = model.templateMode;
        if (this.queue.length < model.queueSize) {
            this.queue = new IEngineTemplateEvent[model.queueSize];
        }
        System.arraycopy(model.queue, 0, this.queue, 0, model.queueSize);
        this.queueSize = model.queueSize;
    }




    public final void write(final Writer writer) throws IOException {
        for (int i = 0; i < this.queueSize; i++) {
            this.queue[i].write(writer);
        }
    }




    public void accept(final IModelVisitor visitor) {
        for (int i = 0; i < this.queueSize; i++) {
            // We will execute the visitor on the Immutable events, that we need to create during the visit
            this.queue[i].accept(visitor);
        }
    }




    // Note we will use object equality for comparing events here - the idea is to check whether
    // a model has been changed at all, and replacing an event with an equivalent one would be
    // considered "a change" anyway.
    boolean sameAs(final Model model) {
        if (model == null || model.queueSize != this.queueSize) {
            return false;
        }
        for (int i = 0; i < this.queueSize; i++) {
            if (this.queue[i] != model.queue[i]) {
                return false;
            }
        }
        return true;
    }




    @Override
    public final String toString() {
        try {
            final Writer writer = new FastStringWriter();
            write(writer);
            return writer.toString();
        } catch (final IOException e) {
            throw new TemplateProcessingException(
                    "Error while creating String representation of model");
        }
    }




    static IEngineTemplateEvent asEngineEvent(final ITemplateEvent event) {

        if (event instanceof IEngineTemplateEvent) {
            return (IEngineTemplateEvent)event;
        }

        if (event instanceof IText) {
            return Text.asEngineText((IText) event);
        }
        if (event instanceof IOpenElementTag) {
            return OpenElementTag.asEngineOpenElementTag((IOpenElementTag) event);
        }
        if (event instanceof ICloseElementTag) {
            return CloseElementTag.asEngineCloseElementTag((ICloseElementTag) event);
        }
        if (event instanceof IStandaloneElementTag) {
            return StandaloneElementTag.asEngineStandaloneElementTag((IStandaloneElementTag) event);
        }
        if (event instanceof IDocType) {
            return DocType.asEngineDocType((IDocType) event);
        }
        if (event instanceof IComment) {
            return Comment.asEngineComment((IComment) event);
        }
        if (event instanceof ICDATASection) {
            return CDATASection.asEngineCDATASection((ICDATASection) event);
        }
        if (event instanceof IXMLDeclaration) {
            return XMLDeclaration.asEngineXMLDeclaration((IXMLDeclaration) event);
        }
        if (event instanceof IProcessingInstruction) {
            return ProcessingInstruction.asEngineProcessingInstruction((IProcessingInstruction) event);
        }
        if (event instanceof ITemplateStart) {
            return TemplateStart.asEngineTemplateStart((ITemplateStart) event);
        }
        if (event instanceof ITemplateEnd) {
            return TemplateEnd.asEngineTemplateEnd((ITemplateEnd) event);
        }
        throw new TemplateProcessingException(
                "Cannot handle in event of type: " + event.getClass().getName());

    }


}