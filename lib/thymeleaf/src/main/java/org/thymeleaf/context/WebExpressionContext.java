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
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebExchange;

/**
 * <p>
 *   Basic web-oriented implementation of the {@link IExpressionContext} and {@link IWebContext} interfaces.
 * </p>
 * <p>
 *   This class is not thread-safe, and should not be shared across executions of templates.
 * </p>
 * <p>
 *   Note this class was modified in a backwards-incompatible way in Thymeleaf 3.1.0.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public final class WebExpressionContext extends AbstractExpressionContext implements IWebContext {

    private final IWebExchange webExchange;


    public WebExpressionContext(final IEngineConfiguration configuration,
                                final IWebExchange webExchange) {
        super(configuration);
        Validate.notNull(webExchange, "Web exchange cannot be null in web context");
        this.webExchange = webExchange;
    }


    public WebExpressionContext(final IEngineConfiguration configuration,
                                final IWebExchange webExchange,
                                final Locale locale) {
        super(configuration, locale);
        Validate.notNull(webExchange, "Web exchange cannot be null in web context");
        this.webExchange = webExchange;
    }


    public WebExpressionContext(
            final IEngineConfiguration configuration,
            final IWebExchange webExchange,
            final Locale locale, final Map<String, Object> variables) {
        super(configuration, locale, variables);
        Validate.notNull(webExchange, "Web exchange cannot be null in web context");
        this.webExchange = webExchange;
    }


    @Override
    public IWebExchange getExchange() {
        return this.webExchange;
    }

}
