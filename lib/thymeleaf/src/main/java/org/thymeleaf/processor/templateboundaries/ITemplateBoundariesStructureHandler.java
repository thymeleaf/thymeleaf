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
package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IModel;

/**
 * <p>
 *   Structure handler class meant to be used by {@link ITemplateBoundariesProcessor} implementations.
 * </p>
 * <p>
 *   Structure handlers allow processors to instruct the engine to perform a series of actions that cannot
 *   be done directly from the processors themselves, usually because these actions are applied or have effects
 *   on scopes broader than the processed events themselves.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see ITemplateBoundariesProcessor
 * @since 3.0.0
 * 
 */
public interface ITemplateBoundariesStructureHandler {

    /**
     * <p>
     *   Resets all actions specified so far for the current processor execution.
     * </p>
     */
    public void reset();

    /**
     * <p>
     *   Instructs the engine to set a new local variable.
     * </p>
     *
     * @param name the name of the variable.
     * @param value the value of the variable.
     */
    public void setLocalVariable(final String name, final Object value);

    /**
     * <p>
     *   Instructs the engine to remove a local variable.
     * </p>
     *
     * @param name the name of the variable.
     */
    public void removeLocalVariable(final String name);

    /**
     * <p>
     *   Instructs the engine to set a new selection target.
     * </p>
     * <p>
     *   The <em>selection target</em> is the object on which selection expressions {@code (*{...})} are executed.
     *   In the Standard Dialect, this selection target is usually modified by means of the {@code th:object}
     *   attribute, but custom processors can do it too. Note the selection target has the same scope as a local
     *   variable, and will therefore be accessible only inside the body of the element being processed.
     * </p>
     * <p>
     *   See {@link org.thymeleaf.context.ITemplateContext#getSelectionTarget()}
     * </p>
     *
     * @param selectionTarget the selection target to be set.
     */
    public void setSelectionTarget(final Object selectionTarget);

    /**
     * <p>
     *   Instructs the engine to set a new inliner.
     * </p>
     * <p>
     *   Inliners are used for processing all text nodes ({@link org.thymeleaf.model.IText} events) appearing
     *   in the body of the element being processed. This is the mechanism used by the th:inline attribute to
     *   enable inlining in any of the specified modes (text, javascript, etc).
     * </p>
     * <p>
     *   See {@link org.thymeleaf.context.ITemplateContext#getInliner()}
     * </p>
     *
     * @param inliner the inliner.
     */
    public void setInliner(final IInliner inliner);

    /**
     * <p>
     *   Instructs the engine to insert the specified text.
     * </p>
     * <p>
     *   If the processed event is a {@link org.thymeleaf.model.ITemplateStart} event, then the text will
     *   be inserted at the beginning of the template (just after the <em>template start</em> event). On the other
     *   hand, if the processed event is a {@link org.thymeleaf.model.ITemplateEnd} event, then the text
     *   will be inserted at the end of the template (just before the <em>template end</em> event).
     * </p>
     *
     * @param text the text to be inserted.
     * @param processable whether the inserted text should be considered <em>processable</em> or not.
     */
    public void insert(final String text, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to insert the specified model.
     * </p>
     * <p>
     *   If the processed event is a {@link org.thymeleaf.model.ITemplateStart} event, then the model will
     *   be inserted at the beginning of the template (just after the <em>template start</em> event). On the other
     *   hand, if the processed event is a {@link org.thymeleaf.model.ITemplateEnd} event, then the model
     *   will be inserted at the end of the template (just before the <em>template end</em> event).
     * </p>
     *
     * @param model the model to be inserted.
     * @param processable whether the inserted text should be considered <em>processable</em> or not.
     */
    public void insert(final IModel model, final boolean processable);

}

