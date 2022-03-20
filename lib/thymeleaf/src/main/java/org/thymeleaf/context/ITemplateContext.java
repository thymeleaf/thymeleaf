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
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Interface implemented by all classes containing the context required for template processing.
 * </p>
 * <p>
 *   This interface extends {@link IExpressionContext} and {@link IContext} by adding the required
 *   information needed to process templates.
 * </p>
 * <p>
 *   Note that, in order for the template engine to use a custom-made implementation of this interface
 *   for template processing instead of cloning its data, such implementation should also implement
 *   the {@link IEngineContext} interface.
 * </p>
 * <p>
 *   Also note these implementations do not have to be thread-safe, and in fact should not be shared by different
 *   threads or template executions. They are meant to be local to a specific template engine execution.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public interface ITemplateContext extends IExpressionContext {

    /**
     * <p>
     *   Returns the <em>template data</em> object containing metadata about the template currently
     *   being processed.
     * </p>
     * <p>
     *   Note that the {@link TemplateData} returned here corresponds with the origin of the elements or
     *   nodes being currently processed. This is, if a processor is being executed for an element inserted
     *   from an external template (via a {@code th:insert}, for example), then this method will return
     *   the template data for the template in which the inserted fragment lives, not the one it was inserted
     *   into.
     * </p>
     *
     * @return the template data corresponding to the elements or nodes being currently processed.
     */
    public TemplateData getTemplateData();

    /**
     * <p>
     *   Returns the <em>template mode</em> of the template currently being processed.
     * </p>
     * <p>
     *   Note that the {@link TemplateMode} returned here corresponds with origin of the elements or nodes being
     *   currently processed. This is, if a processor is being executed for an element inserted from an external
     *   template (via a {@code th:insert}, for example), then this method will return the template mode
     *   for the template in which the inserted fragment lives, not the one it was inserted into.
     * </p>
     *
     * @return the template mode of the elements or nodes being currently processed.
     */
    public TemplateMode getTemplateMode();

    /**
     * <p>
     *   Returns the list of all the {@link TemplateData} objects corresponding to all the templates that have
     *   been nested in order to reach the current execution point.
     * </p>
     * <p>
     *   This is a way in which processors can know the complete execution route that lead to the execution
     *   of a specific event (e.g. Template A inserted fragment B, which inserted fragment C).
     * </p>
     * <p>
     *   The first element in this list will always be the top-level template (the one called at the
     *   {@link org.thymeleaf.ITemplateEngine} {@code process(...)} methods).
     * </p>
     *
     * @return the stack of templates (list of {@link TemplateData}).
     */
    public List<TemplateData> getTemplateStack();

    /**
     * <p>
     *   Returns the list of all the {@link IProcessableElementTag} objects corresponding to the hierarchy
     *   of elements (open or standalone elements) that had to be processed in order to reach the current
     *   point in execution.
     * </p>
     * <p>
     *   If the element being processed is a tag (open/standalone), it will appear at the end of the list.
     * </p>
     * <p>
     *   Note this hierarchy does not correspond with the tag hierarchy at the original template, but with the
     *   hierarchy of processing (many tags could appear during processing itself and not be present at the
     *   original template).
     * </p>
     * <p>
     *   Also note that, because of this being the <em>processing-time</em> hierarchy, this information is set
     *   at the <em>processor</em> level, so <strong>it should not be considered to be <em>available</em>
     *   and/or <em>valid</em> at the pre-processor layer</strong>.
     * </p>
     *
     * @return the stack of elements (list of {@link IProcessableElementTag}).
     */
    public List<IProcessableElementTag> getElementStack();

    /**
     * <p>
     *   Returns the map of configuration items that have been specified at the {@code process(...)} methods of
     *   {@link org.thymeleaf.ITemplateEngine}, aimed at further configuring the template being used and its
     *   resolution by means of the {@link org.thymeleaf.templateresolver.ITemplateResolver}s.
     * </p>
     *
     * @return the template resolution attributes map
     */
    public Map<String, Object> getTemplateResolutionAttributes();

    /**
     * <p>
     *   Returns the <em>model factory</em> that can be used for creating or modifying events.
     * </p>
     * <p>
     *   This is actually a convenience method completely equivalent to calling
     *   {@link #getConfiguration()} and then {@link org.thymeleaf.IEngineConfiguration#getModelFactory(TemplateMode)}
     *   using as template mode the result of {@link #getTemplateMode()}.
     * </p>
     *
     * @return the model factory
     */
    public IModelFactory getModelFactory();

    /**
     * <p>
     *   Returns whether the current template has set a <em>selection target</em> for the current point of
     *   execution or not.
     * </p>
     * <p>
     *   Selection targets are objects on which all {@code *{...}} expression will be executed (instead of on the
     *   root context). They are normally set by means of {@code th:objects}.
     * </p>
     *
     * @return {@code true} if there is a selection target, {@code false} if not.
     */
    public boolean hasSelectionTarget();

    /**
     * <p>
     *   Returns the <em>selection target</em> set for the current point of execution (or {@code null} if there
     *   isn't any).
     * </p>
     * <p>
     *   Selection targets are objects on which all {@code *{...}} expression will be executed (instead of on the
     *   root context). They are normally set by means of {@code th:objects}.
     * </p>
     *
     * @return the selection target, or null if there isn't any.
     */
    public Object getSelectionTarget();

    /**
     * <p>
     *   Returns the <em>inliner</em> (implementation of {@link IInliner}) set to be used at the current point
     *   of execution.
     * </p>
     *
     * @return the inliner to be used.
     */
    public IInliner getInliner();

    /**
     * <p>
     *   Computes an externalized (internationalized, i18n) message to be used on a template.
     * </p>
     * <p>
     *   This method is meant to be called mainly by processors that need to output externalized messages.
     * </p>
     *
     * @param origin the <em>origin</em> class to be used for message resolution. When calling from a processor, this
     *               is normally the processor class itself. See {@link org.thymeleaf.messageresolver.IMessageResolver}.
     * @param key the key of the message to be retrieved.
     * @param messageParameters the parameters to be applied to the requested message.
     * @param useAbsentMessageRepresentation whether an <em>absent message representation</em> should be returned in
     *                                       the case that the message does not exist
     *                                       (see {@link org.thymeleaf.messageresolver.IMessageResolver}).
     * @return the requested message, correctly formatted. Or an <em>absent message representation</em>, or
     *         {@code null} if no absent message representations are allowed.
     */
    public String getMessage(
            final Class<?> origin, final String key, final Object[] messageParameters, final boolean useAbsentMessageRepresentation);

    /**
     * <p>
     *   Computes link to be used on a template.
     * </p>
     * <p>
     *   This method is meant to be called mainly by processors or expressions that need to output links.
     * </p>
     *
     * @param base the base of the link URL to be built, i.e. its path. Can be null.
     * @param parameters the (optional) URL parameters.
     * @return the built URL, or an exception if no link builders are able to build this link.
     */
    public String buildLink(final String base, final Map<String, Object> parameters);

    /**
     * <p>
     *   Returns the {@link IdentifierSequences} object set to be used at the current point of execution.
     * </p>
     *
     * @return the identifier sequences object.
     */
    public IdentifierSequences getIdentifierSequences();

}
