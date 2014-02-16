/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.spring4.expression;

import java.util.Locale;

import org.springframework.ui.context.Theme;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;

/**
 * A utility object, accessed in Thymeleaf templates by the <tt>#themes</tt>
 * expression, that provides the same features as the Spring
 * <tt>&lt;spring:theme</tt> JSP tag.
 * 
 * @author Emanuel Rabina
 */
public class Themes {

    private final Theme theme;
    private final Locale locale;

    /**
     * Constructor, obtains the current theme and locale from the processing
     * context for code lookups later.
     * 
     * @param processingContext the processing context being used
     */
    public Themes(final IProcessingContext processingContext) {

        super();
        final IContext context = processingContext.getContext();
        this.locale = context.getLocale();
        final RequestContext requestContext = (RequestContext) processingContext.getContext().getVariables()
        		.get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        this.theme = requestContext != null ? requestContext.getTheme() : null;
    }

    /**
     * Looks up and returns the value of the given key in the properties file of
     * the currently-selected theme.
     * 
     * @param code Key to look up in the theme properties file.
     * @return The value of the code in the current theme properties file, or an
     * 		   empty string if the code could not be resolved.
     */
    public String code(final String code) {
        if (this.theme == null) {
            throw new TemplateProcessingException("Theme cannot be resolved because RequestContext was not found. "
                + "Are you using a Context object without a RequestContext variable?");
        }
        return this.theme.getMessageSource().getMessage(code, null, "", this.locale);
    }
}
