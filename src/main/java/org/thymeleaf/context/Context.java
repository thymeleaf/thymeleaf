/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 *   Standard implementation of {@link IContext}.
 * </p>
 * <p>
 *   This {@link IContext} implementation uses a {@link ContextExecutionInfo} object as its
 *   {@link IContextExecutionInfo} implementation.
 * </p>
 * <p>
 *   If Thymeleaf is used for the creation of an HTML/XHTML interface in a
 *   web application, an implementation of the {@link IWebContext} interface should be
 *   used instead.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Context extends AbstractContext {
    
    
    
    /**
     * <p>
     *   Create an instance without specifying a locale. Using this constructor,
     *   the default locale (<tt>Locale.getDefault()</tt>) will be used.
     * </p>
     */
    public Context() {
        super();
    }

    /**
     * <p>
     *   Create an instance specifying a locale.
     * </p>
     * 
     * @param locale the locale to be used.
     */
    public Context(final Locale locale) {
        super(locale);
    }

    /**
     * <p>    
     *   Create an instance specifying a locale and an initial set of context
     *   variables.
     * </p>
     * 
     * @param locale the locale to be used.
     * @param variables the initial set of context variables.
     */
    public Context(final Locale locale, final Map<String, ?> variables) {
        super(locale, variables);
    }



    @Override
    protected IContextExecutionInfo buildContextExecutionInfo(final String templateName) {
        final Calendar now = Calendar.getInstance();
        return new ContextExecutionInfo(templateName, now);
    }

    
}
