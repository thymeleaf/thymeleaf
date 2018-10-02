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
package org.thymeleaf.context;

import java.util.List;
import java.util.Map;

import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;

/**
 * <p>
 *   Mostly-internal interface implemented by all classes containing the context required for
 *   template processing inside the engine itself.
 * </p>
 * <p>
 *   This interface extends {@link ITemplateContext} by adding a series of methods required internally
 *   by the template engine for processing, which should <strong>not be used from users' code</strong>.
 *   Calling these methods directly from custom processors or other extensions could have undesirable
 *   effects on template processing.
 * </p>
 * <p>
 *   Contexts used during template processing by the engine are always implementations of this interface.
 *   If the Template Engine is called with an implementation of this {@link IEngineContext} as
 *   {@code context}, the same object will be used (so that users can actually provide their own implementations).
 *   On the other side, if the {@code context} specified to the Template Engine is not an implementation of this
 *   interface, an implementation of {@link IEngineContext} will be internally created by the engine, the original
 *   context's variables and other info will be cloned, and used instead.
 * </p>
 * <p>
 *   Again note that, besides providing custom-made implementations of this interface (which is a very complex
 *   operation, not recommended in most scenarios) there should be no reason why this interface should ever be
 *   used in users' code.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IEngineContext extends ITemplateContext {


    /**
     * <p>
     *   Sets a new variable into the context.
     * </p>
     * <p>
     *   Depending on the <em>context level</em>, determined by {@link #increaseLevel()} and
     *   {@link #decreaseLevel()}, the variable being set might be considered a <em>local variable</em>
     *   and thus disappear from context once the <em>context level</em> is decreased below the
     *   level the variable was created at.
     * </p>
     *
     * @param name the name of the variable.
     * @param value the value of the variable.
     */
    public void setVariable(final String name, final Object value);

    /**
     * <p>
     *   Sets several variables at a time into the context.
     * </p>
     * <p>
     *   Depending on the <em>context level</em>, determined by {@link #increaseLevel()} and
     *   {@link #decreaseLevel()}, the variables being set might be considered a <em>local variables</em>
     *   and thus disappear from context once the <em>context level</em> is decreased below the
     *   level the variable was created at.
     * </p>
     *
     * @param variables the variables to be set.
     */
    public void setVariables(final Map<String, Object> variables);

    /**
     * <p>
     *   Removes a variable from the context.
     * </p>
     * <p>
     *   Depending on the <em>context level</em>, determined by {@link #increaseLevel()} and
     *   {@link #decreaseLevel()}, this removal might be considered <em>local variable-related</em>
     *   and thus cease to happen (i.e. the variable would be recovered) once the <em>context level</em>
     *   is decreased below the level the variable was created at.
     * </p>
     *
     * @param name the name of the variable to be removed.
     */
    public void removeVariable(final String name);

    /**
     * <p>
     *   Set a selection target. Usually the consequence of executing a {@code th:object} processor.
     * </p>
     * <p>
     *   Once set, all <em>selection expressions</em> ({@code *{...}}) will be executed on this target.
     * </p>
     * <p>
     *   This selection target will have the consideration of a <em>local variable</em> and thus depend on
     *   the <em>context level</em> (see {@link #setVariable(String, Object)}).
     * </p>
     *
     * @param selectionTarget the selection target to be set.
     */
    public void setSelectionTarget(final Object selectionTarget);

    /**
     * <p>
     *   Set an inliner. Usually the consequence of executing a {@code th:inline} processor.
     * </p>
     * <p>
     *   This inliner will have the consideration of a <em>local variable</em> and thus depend on
     *   the <em>context level</em> (see {@link #setVariable(String, Object)}).
     * </p>
     *
     * @param inliner the inliner to be set.
     */
    public void setInliner(final IInliner inliner);

    /**
     * <p>
     *   Sets a new template metadata object ({@link TemplateData}) for the current execution point, specifying
     *   that the elements and nodes that are to be processed from now on (until the <em>context level</em> is
     *   decreased below the current level) originally belonged to a different template.
     * </p>
     * <p>
     *   A call on this method is usually the consequence of {@code th:insert} or {@code th:replace}.
     * </p>
     *
     * @param template the template data.
     */
    public void setTemplateData(final TemplateData template);

    /**
     * <p>
     *   Sets a new element tag ({@link IProcessableElementTag}) into the hierarchy (stack) of element tags.
     * </p>
     * <p>
     *   This hierarchy of element tags (added this way) can be obtained with {@link #getElementStack()}.
     * </p>
     *
     * @param elementTag the element tag.
     */
    public void setElementTag(final IProcessableElementTag elementTag);

    /**
     * <p>
     *   Retrieves the element stack just like {@link #getElementStack()}, but only for those elements added
     *   to the hierarchy above a specific context level.
     * </p>
     *
     * @param contextLevel the level above which we want to obtain the element stack.
     * @return the element stack above a specified level.
     */
    public List<IProcessableElementTag> getElementStackAbove(final int contextLevel);

    /**
     * <p>
     *   Checks whether a specific variable is <em>local</em> or not.
     * </p>
     * <p>
     *   This means checking if the <em>context level</em> at which the variable was defined was 0 or not.
     * </p>
     *
     * @param name the name of the variable to be checked.
     * @return {@code true} if the variable is local (level &gt; 0), {@code false} if not (level == 0).
     */
    public boolean isVariableLocal(final String name);

    /**
     * <p>
     *   Increase the <em>context level</em>. This is usually a consequence of the
     *   {@link org.thymeleaf.engine.ProcessorTemplateHandler} detecting the start of a new element
     *   (i.e. handling an {@link org.thymeleaf.model.IOpenElementTag} event).
     * </p>
     * <p>
     *   <strong>This method should only be called internally</strong>.
     * </p>
     */
    public void increaseLevel();

    /**
     * <p>
     *   Decrease the <em>context level</em>. This is usually a consequence of the
     *   {@link org.thymeleaf.engine.ProcessorTemplateHandler} detecting the closing of an element
     *   (i.e. handling an {@link org.thymeleaf.model.ICloseElementTag} event).
     * </p>
     * <p>
     *   <strong>This method should only be called internally</strong>.
     * </p>
     */
    public void decreaseLevel();


    /**
     * <p>
     *   Return the current <em>context level</em>.
     * </p>
     * <p>
     *   <strong>This method should only be called internally</strong>.
     * </p>
     *
     * @return the current level
     */
    public int level();

}
