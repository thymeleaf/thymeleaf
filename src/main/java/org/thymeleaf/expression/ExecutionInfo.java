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
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;


/**
 * <p>
 *   Expression Object providing useful information about the template being processed inside Thymeleaf Standard
 *   Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   <tt>#execInfo</tt>.
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
     *   Returns the template name.
     * </p>
     * <p>
     *   Note that the same template can be resolved with different names due to
     *   aliases, links, etc. This template name refers to the one used to call
     *   the TemplateEngine itself.
     * </p>
     *
     * @return the template name
     */
    public String getTemplateName() {
        return this.context.getTemplateResolution().getTemplate();
    }


    /**
     * <p>
     *   Returns the template mode ({@link TemplateMode}).
     * </p>
     *
     * @return the template mode
     */
    public TemplateMode getTemplateMode() {
        return this.context.getTemplateResolution().getTemplateMode();
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
    public List<String> getTemplateNameStack() {
        final List<TemplateResolution> templateResolutionStack = this.context.getTemplateResolutionStack();
        final List<String> templateNameStack = new ArrayList<String>(templateResolutionStack.size());
        for (final TemplateResolution templateResolution : templateResolutionStack) {
            templateNameStack.add(templateResolution.getTemplate());
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
    public List<TemplateMode> getTemplateModeStack() {
        final List<TemplateResolution> templateResolutionStack = this.context.getTemplateResolutionStack();
        final List<TemplateMode> templateModeStack = new ArrayList<TemplateMode>(templateResolutionStack.size());
        for (final TemplateResolution templateResolution : templateResolutionStack) {
            templateModeStack.add(templateResolution.getTemplateMode());
        }
        return templateModeStack;
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
