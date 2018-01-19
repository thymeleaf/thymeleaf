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
package org.thymeleaf.spring5.context.webflux;

import java.util.Locale;
import java.util.Map;

import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;

/**
 * <p>
 *   Basic Spring WebFlux-oriented implementation of the {@link IExpressionContext} and
 *   {@link ISpringWebFluxContext} interfaces.
 * </p>
 * <p>
 *   This class is not thread-safe, and should not be shared across executions of templates.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringWebFluxExpressionContext extends SpringWebFluxContext implements IExpressionContext {

    private final IEngineConfiguration configuration;
    private IExpressionObjects expressionObjects = null;


    public SpringWebFluxExpressionContext(
            final IEngineConfiguration configuration, final ServerWebExchange exchange) {
        this(configuration, exchange, null, null, null);
    }

    public SpringWebFluxExpressionContext(
            final IEngineConfiguration configuration, final ServerWebExchange exchange, final Locale locale) {
        this(configuration, exchange, null, locale, null);
    }

    public SpringWebFluxExpressionContext(
            final IEngineConfiguration configuration,
            final ServerWebExchange exchange,
            final Locale locale, final Map<String, Object> variables) {
        this(configuration, exchange, null, locale, variables);
    }

    public SpringWebFluxExpressionContext(
            final IEngineConfiguration configuration,
            final ServerWebExchange exchange,
            final ReactiveAdapterRegistry reactiveAdapterRegistry,
            final Locale locale, final Map<String, Object> variables) {
        super(exchange, reactiveAdapterRegistry, locale, variables);
        this.configuration = configuration;
    }



    @Override
    public IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    @Override
    public IExpressionObjects getExpressionObjects() {
        // We delay creation of expression objects in case they are not needed at all
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }

}
