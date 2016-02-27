/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * <p>
 *   Interface representing all <em>model</em> objects.
 * </p>
 * <p>
 *   Models are sequences of events ({@link ITemplateEvent}) normally representing a specific element
 *   with all its body and nested elements, or even an entire document.
 * </p>
 * <p>
 *   {@link IModel} implementations are the base classes used at the template engine for representing
 *   fragments of templates.
 * </p>
 * <p>
 *   The most important implementation of this interface is {@link org.thymeleaf.engine.TemplateModel},
 *   used for representing parsed and/or cached fragments or templates.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see org.thymeleaf.engine.TemplateModel
 * @see org.thymeleaf.processor.element.IElementModelProcessor
 * @since 3.0.0
 *
 */
public interface IModel {

    /**
     * <p>
     *   Returns the engine configuration that was used for creating this model.
     * </p>
     *
     * @return the engine configuration.
     */
    public IEngineConfiguration getConfiguration();

    /**
     * <p>
     *   Returns the template mode used for creating this model.
     * </p>
     *
     * @return the template mode.
     */
    public TemplateMode getTemplateMode();

    /**
     * <p>
     *   The size of the model (number of {@link ITemplateEvent} objects contained).
     * </p>
     *
     * @return the size of the model.
     */
    public int size();

    /**
     * <p>
     *   Retrieves a specific event from the model sequence.
     * </p>
     *
     * @param pos the position (zero-based) of the event to be retrieved.
     * @return the retrieved event.
     */
    public ITemplateEvent get(final int pos);

    /**
     * <p>
     *   Adds an event at the end of the sequence.
     * </p>
     *
     * @param event the event to be added.
     */
    public void add(final ITemplateEvent event);

    /**
     * <p>
     *   Inserts an event into a specific position in the sequence.
     * </p>
     *
     * @param pos the position to insert the event (zero-based).
     * @param event the event to be inserted.
     */
    public void insert(final int pos, final ITemplateEvent event);

    /**
     * <p>
     *   Add an entire model at the end of the sequence. This effectively appends the
     *   <tt>model</tt> argument's sequence to this one.
     * </p>
     *
     * @param model the model to be appended.
     */
    public void addModel(final IModel model);

    /**
     * <p>
     *   Inserts an entire model into a specific position in this model's sequence.
     * </p>
     *
     * @param pos the position to insert the mdoel (zero-based).
     * @param model the model to be inserted.
     */
    public void insertModel(final int pos, final IModel model);

    /**
     * <p>
     *   Remove an event from the sequence.
     * </p>
     *
     * @param pos the position (zero-based) of the event to be removed.
     */
    public void remove(final int pos);

    /**
     * <p>
     *   Remove all events from the model sequence.
     * </p>
     */
    public void reset();

    /**
     * <p>
     *   Clones the model and all its events.
     * </p>
     *
     * @return the new model.
     */
    public IModel cloneModel();

    /**
     * <p>
     *   Accept a visitor implementing {@link IModelVisitor}. This visitor will be executed
     *   for each event in the sequence.
     * </p>
     *
     * @param visitor the visitor object.
     */
    public void accept(final IModelVisitor visitor);

    /**
     * <p>
     *   Write this model (its events) through the specified writer.
     * </p>
     *
     * @param writer the writer that will be used for writing the model.
     * @throws IOException should any exception happen.
     */
    public void write(final Writer writer) throws IOException;

}