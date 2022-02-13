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

package org.thymeleaf.spring5.expression;

import java.util.Locale;

import org.springframework.ui.context.Theme;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;

/**
 * A utility object, accessed in Thymeleaf templates by the {@code #themes}
 * expression, that provides the same features as the Spring
 * {@code <spring:theme>} JSP tag.
 *
 * @author Emanuel Rabina
 * @author Daniel Fern&aacute;ndez
 */
public class Themes {

    private final Theme theme;
    private final Locale locale;

    /**
     * Constructor, obtains the current theme and locale from the processing
     * context for code lookups later.
     * 
     * @param context the processing context being used
     */
    public Themes(final IExpressionContext context) {

        super();
        this.locale = context.getLocale();
        final IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
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
