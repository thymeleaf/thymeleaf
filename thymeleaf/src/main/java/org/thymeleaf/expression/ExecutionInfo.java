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
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * <p>
 *   Expression Object providing useful information about the template being processed inside Thymeleaf Standard
 *   Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #execInfo}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class ExecutionInfo {

    private final ITemplateContext context;
    private final Calendar now;


    public ExecutionInfo(final ITemplateContext context) {
        super();
        this.context = context;
        this.now = Calendar.getInstance(context.getLocale());
    }


    /**
     * <p>
     *   Returns the template name (of the leaf template).
     * </p>
     * <p>
     *   Note that the template name returned here corresponds with origin of the elements or nodes being
     *   currently processed. This is, if a processor is being executed for an element inserted from an external
     *   template (via a {@code th:insert}, for example), then this method will return the template mode
     *   for the template in which the inserted fragment lives, not the one it was inserted into.
     * </p>
     *
     * @return the template name
     */
    public String getTemplateName() {
        return this.context.getTemplateData().getTemplate();
    }


    /**
     * <p>
     *   Returns the template mode ({@link TemplateMode}) (of the leaf template).
     * </p>
     * <p>
     *   Note that the {@link TemplateMode} returned here corresponds with origin of the elements or nodes being
     *   currently processed. This is, if a processor is being executed for an element inserted from an external
     *   template (via a {@code th:insert}, for example), then this method will return the template mode
     *   for the template in which the inserted fragment lives, not the one it was inserted into.
     * </p>
     *
     * @return the template mode
     */
    public TemplateMode getTemplateMode() {
        return this.context.getTemplateData().getTemplateMode();
    }


    /**
     * <p>
     *   Returns the template name of the first-level template.
     * </p>
     * <p>
     *   Note this template name refers to the first-level one, the one used to call the TemplateEngine itself, even
     *   if by the moment this method is called the engine is processing a fragment inserted from the first-level
     *   template (or at any other level in the hierarchy).
     * </p>
     *
     * @return the template name
     */
    public String getProcessedTemplateName() {
        return this.context.getTemplateStack().get(0).getTemplate();
    }


    /**
     * <p>
     *   Returns the template mode ({@link TemplateMode}) of the first-level template.
     * </p>
     * <p>
     *   Note this template mode refers to the first-level one, the one used to call the TemplateEngine itself, even
     *   if by the moment this method is called the engine is processing a fragment inserted from the first-level
     *   template (or at any other level in the hierarchy).
     * </p>
     *
     * @return the template mode
     */
    public TemplateMode getProcessedTemplateMode() {
        return this.context.getTemplateStack().get(0).getTemplateMode();
    }


    /**
     * <p>
     *   Returns the names of all the stack of templates appliable to the current point
     *   of execution. This will depend on which templates are inserted inside wich.
     * </p>
     * <p>
     *   The first-level template will appear first, and the most specific template will appear last.
     * </p>
     *
     * @return the stack of template names
     */
    public List<String> getTemplateNames() {
        final List<TemplateData> templateStack = this.context.getTemplateStack();
        final List<String> templateNameStack = new ArrayList<String>(templateStack.size());
        for (final TemplateData templateData : templateStack) {
            templateNameStack.add(templateData.getTemplate());
        }
        return templateNameStack;
    }


    /**
     * <p>
     *   Returns the {@link TemplateMode}s of all the stack of templates appliable to the current point
     *   of execution. This will depend on which templates are inserted inside wich.
     * </p>
     * <p>
     *   The first-level template will appear first, and the most specific template will appear last.
     * </p>
     *
     * @return the stack of template modes
     */
    public List<TemplateMode> getTemplateModes() {
        final List<TemplateData> templateStack = this.context.getTemplateStack();
        final List<TemplateMode> templateModeStack = new ArrayList<TemplateMode>(templateStack.size());
        for (final TemplateData templateData : templateStack) {
            templateModeStack.add(templateData.getTemplateMode());
        }
        return templateModeStack;
    }


    /**
     * <p>
     *   Returns the <em>template stack</em>, containing the metadata for the first-level template being
     *   processed and also any fragments that might have been nested up to the current execution point.
     * </p>
     * <p>
     *   The result of this method actually corresponds to the result of {@link ITemplateContext#getTemplateStack()}.
     * </p>
     *
     * @return the stack of {@link TemplateData} objects
     */
    public List<TemplateData> getTemplateStack() {
        return this.context.getTemplateStack();
    }


    /**
     * <p>
     *   Returns the current date and time (from the moment of template execution).
     * </p>
     *
     * @return the current date and time, as a Calendar
     */
    public Calendar getNow() {
        return this.now;
    }

    
}
