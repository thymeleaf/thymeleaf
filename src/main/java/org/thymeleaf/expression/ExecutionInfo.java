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

import java.util.Calendar;

import org.thymeleaf.context.ITemplateProcessingContext;


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

    private final String templateName;
    private final Calendar now;


    public ExecutionInfo(final ITemplateProcessingContext processingContext) {
        super();
        this.templateName = processingContext.getTemplateResolution().getTemplate();
        this.now = Calendar.getInstance(processingContext.getLocale());
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
        return this.templateName;
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
