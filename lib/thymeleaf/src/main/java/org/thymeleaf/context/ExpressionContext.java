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

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;

/**
 * <p>
 *   Basic implementation of the {@link IExpressionContext} interface.
 * </p>
 * <p>
 *   This class is not thread-safe, and should not be shared across executions of templates.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public final class ExpressionContext extends AbstractExpressionContext {


    public ExpressionContext(final IEngineConfiguration configuration) {
        super(configuration);
    }


    public ExpressionContext(final IEngineConfiguration configuration, final Locale locale) {
        super(configuration, locale);
    }


    public ExpressionContext(
            final IEngineConfiguration configuration, final Locale locale, final Map<String, Object> variables) {
        super(configuration, locale, variables);
    }

}
