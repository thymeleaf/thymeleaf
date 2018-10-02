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
package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelVisitor;

/**
 * <p>
 *   Interface to be implemented by all <em>element model processors</em>.
 * </p>
 * <p>
 *   Processors of this kind are executed on the entire elements they match --including their bodies--,
 *   in the form of an {@link IModel} object that contains the complete sequence of events that models such
 *   element and its contents.
 * </p>
 * <p>
 *   <b>Reading and modifying the model</b>
 * </p>
 * <p>
 *   The {@link IModel} object passed as a parameter to the
 *   {@link #process(ITemplateContext, IModel, IElementModelStructureHandler)} method is mutable,
 *   so it allows any modifications to be done on it. For example, we might want to modify it so that we
 *   replace every text node from its body with a comment with the same contents:
 * </p>
 * <code>
 *   final IModelFactory modelFactory = context.getModelFactory();<br>
 *   <br>
 *   int n = model.size();<br>
 *   while (n-- != 0) {<br>
 *       final ITemplateEvent event = model.get(n);<br>
 *       if (event instanceof IText) {<br>
 *           final IComment comment =<br>
 *               modelFactory.createComment(((IText)event).getText());<br>
 *           model.insert(n, comment);<br>
 *           model.remove(n + 1);<br>
 *       }<br>
 *   }<br>
 * </code>
 * <p>
 *   Note also that the {@link IModel} interface includes an {@link IModel#accept(IModelVisitor)} method,
 *   useful for traversing an entire model looking for specific nodes or relevant data the Visitor pattern.
 * </p>
 * <p>
 *   <b>Using the {@code structureHandler}</b>
 * </p>
 * <p>
 *   Model processors are passed a structure handler object that allows them to instruct the engine to take
 *   any actions that cannot be done by directly acting on the {@link IModel} model object itself.
 * </p>
 * <p>
 *   See the documentation for {@link IElementModelStructureHandler} for more info.
 * </p>
 * <p>
 *   <b>Abstract implementations</b>
 * </p>
 * <p>
 *   Two basic abstract implementations of this interface are offered:
 * </p>
 * <ul>
 *   <li>{@link AbstractElementModelProcessor}, meant for processors that match element events by their element
 *       name (i.e. without looking at any attributes).</li>
 *   <li>{@link AbstractAttributeModelProcessor}, meant for processors that match element events by one of their
 *       attributes (and optionally also the element name).</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 * @see AbstractElementModelProcessor
 * @see AbstractAttributeModelProcessor
 * @see IElementModelStructureHandler
 * @since 3.0.0
 * 
 */
public interface IElementModelProcessor extends IElementProcessor {


    /**
     * <p>
     *   Execute the processor.
     * </p>
     * <p>
     *   The {@link IModel} object represents the section of template (a <em>fragment</em>) on which the processor
     *   is executing, and can be directly modified. Instructions to be given to the template engine such as
     *   local variable creation, inlining etc. should be done via the {@link IElementModelStructureHandler} handler.
     * </p>
     *
     * @param context the execution context.
     * @param model the model this processor is executing on.
     * @param structureHandler the handler that will centralise modifications and commands to the engine.
     */
    public void process(
            final ITemplateContext context,
            final IModel model, final IElementModelStructureHandler structureHandler);


}
