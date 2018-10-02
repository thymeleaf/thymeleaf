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
package org.thymeleaf.spring5.linkbuilder.webflux;

import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;


/**
 * <p>
 *   Spring WebFlux-based implementation of {@link ILinkBuilder}.
 * </p>
 * <p>
 *   This class will build link URLs using the Spring WebFlux API and adapting to the needs of this
 *   type of application.
 * </p>
 * <p>
 *   This implementation will only return {@code null} at {@link #buildLink(IExpressionContext, String, Map)}
 *   if the specified {@code base} argument is {@code null}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 */
public class SpringWebFluxLinkBuilder extends StandardLinkBuilder {



    public SpringWebFluxLinkBuilder() {
        super();
    }



    /**
     * <p>
     *   Compute the context path to be applied to URLs that have been determined to be context-relative (and therefore
     *   might need a context path to be inserted at their beginning).
     * </p>
     * <p>
     *   This method will obtain the context path from {@code ServerHttpRequest.getContextPath()},
     *   throwing an exception if {@code context} is not an instance of {@code ISpringWebFluxContext} given
     *   context-relative URLs are (by default) only allowed in Spring WebFlux contexts.
     * </p>
     * <p>
     *   This method can be overridden by any subclasses that want to change this behaviour.
     * </p>
     *
     * @param context the execution context.
     * @param base the URL base specified.
     * @param parameters the URL parameters specified.
     * @return the context path.
     */
    @Override
    protected String computeContextPath(
            final IExpressionContext context, final String base, final Map<String, Object> parameters) {

        if (!(context instanceof ISpringWebFluxContext)) {
            throw new TemplateProcessingException(
                    "Link base \"" + base + "\" cannot be context relative (/...) unless the context " +
                    "used for executing the engine implements the " + ISpringWebFluxContext.class.getName() + " interface");
        }

        // If it is context-relative, it has to be a Spring WebFlux-based context
        final ServerHttpRequest request = ((ISpringWebFluxContext)context).getRequest();
        return request.getPath().contextPath().value();

    }


    /**
     * <p>
     *   Process an already-built URL just before returning it.
     * </p>
     * <p>
     *   This method can be overridden by any subclasses that want to change this behaviour.
     * </p>
     *
     * @param context the execution context.
     * @param link the already-built URL.
     * @return the processed URL, ready to be used.
     */
    @Override
    protected String processLink(final IExpressionContext context, final String link) {

        if (!(context instanceof ISpringWebFluxContext)) {
            return link;
        }

        final ServerWebExchange exchange = ((ISpringWebFluxContext)context).getExchange();
        return exchange.transformUrl(link);

    }


}
