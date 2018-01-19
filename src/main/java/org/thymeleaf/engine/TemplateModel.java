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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class TemplateModel implements IModel {

    final IEngineConfiguration configuration;
    final TemplateData templateData;
    final IEngineTemplateEvent[] queue; // This is final because this IModel is IMMUTABLE


    // Package-protected constructor, because we don't want anyone creating these objects from outside the engine.
    // If a processor (be it standard or custom-made) wants to create a piece of model, that should be a Model
    // object, not this.
    TemplateModel(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final IEngineTemplateEvent[] queue) {
        
        super();
        
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateData, "Template Resolution cannot be null");
        Validate.notNull(queue, "Event queue cannot be null");
        Validate.isTrue(queue.length >= 2, "At least TemplateStart/TemplateEnd events must be added to a TemplateModel");
        Validate.isTrue(queue[0] == TemplateStart.TEMPLATE_START_INSTANCE, "First event in queue is not TemplateStart");
        Validate.isTrue(queue[queue.length - 1] == TemplateEnd.TEMPLATE_END_INSTANCE, "Last event in queue is not TemplateEnd");

        this.configuration = configuration;
        this.templateData = templateData;
        this.queue = queue;

    }


    public final TemplateData getTemplateData() {
        return this.templateData;
    }


    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    public final TemplateMode getTemplateMode() {
        return this.templateData.getTemplateMode();
    }



    public final int size() {
        return this.queue.length;
    }


    public final ITemplateEvent get(final int pos) {
        return this.queue[pos];
    }


    public final void add(final ITemplateEvent event) {
        immutableModelException();
    }


    public final void insert(final int pos, final ITemplateEvent event) {
        immutableModelException();
    }


    public final void replace(final int pos, final ITemplateEvent event) {
        immutableModelException();
    }


    public final void addModel(final IModel model) {
        immutableModelException();
    }


    public final void insertModel(final int pos, final IModel model) {
        immutableModelException();
    }


    public final void remove(final int pos) {
        immutableModelException();
    }


    public final void reset() {
        immutableModelException();
    }




    void process(final ITemplateHandler handler) {
        for (int i = 0; i < this.queue.length; i++) {
            this.queue[i].beHandled(handler);
        }
    }


    int process(final ITemplateHandler handler, final int offset, final TemplateFlowController controller) {

        if (controller == null) {
            process(handler);
            return this.queue.length;
        }

        if (this.queue.length == 0 || offset >= this.queue.length) {
            return 0;
        }

        int processed = 0;

        for (int i = offset; i < this.queue.length && !controller.stopProcessing; i++) {
            this.queue[i].beHandled(handler);
            processed++;
        }

        return processed;

    }




    public final IModel cloneModel() {
        return new Model(this);
    }




    public final void write(final Writer writer) throws IOException {
        for (int i = 0; i < this.queue.length; i++) {
            this.queue[i].write(writer);
        }
    }




    public void accept(final IModelVisitor visitor) {
        for (int i = 0; i < this.queue.length; i++) {
            // We will execute the visitor on the Immutable events, that we need to create during the visit
            this.queue[i].accept(visitor);
        }
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




    private static void immutableModelException() {
        throw new UnsupportedOperationException(
                "Modifications are not allowed on immutable model objects. This model object is an immutable " +
                "implementation of the " + IModel.class.getName() + " interface, and no modifications are allowed in " +
                "order to keep cache consistency and improve performance. To modify model events, convert first your " +
                "immutable model object to a mutable one by means of the " + IModel.class.getName() + "#cloneModel() method");
    }


}