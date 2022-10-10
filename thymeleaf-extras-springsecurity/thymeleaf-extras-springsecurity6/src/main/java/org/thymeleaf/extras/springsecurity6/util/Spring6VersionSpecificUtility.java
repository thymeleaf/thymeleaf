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
package org.thymeleaf.extras.springsecurity6.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.expression.EvaluationContext;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring6.expression.IThymeleafEvaluationContext;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContextWrapper;
import org.thymeleaf.spring6.web.webflux.ISpringWebFluxWebExchange;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.IServletWebExchange;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.2
 *
 */
final class Spring6VersionSpecificUtility implements ISpringVersionSpecificUtility {




    Spring6VersionSpecificUtility() {
        super();
    }



    public EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final IExpressionObjects expresionObjects) {
        final IThymeleafEvaluationContext thymeleafEvaluationContext = new ThymeleafEvaluationContextWrapper(evaluationContext);
        thymeleafEvaluationContext.setExpressionObjects(expresionObjects);
        return thymeleafEvaluationContext;
    }




    @Override
    public boolean isWebContext(final IContext context) {
        if (context instanceof IWebContext) {
            return true;
        }
        return false;
    }



    @Override
    public boolean isWebMvcContext(final IContext context) {
        if (context instanceof IWebContext) {
            IWebContext webContext = (IWebContext) context;
            IWebExchange webExchange = webContext.getExchange();
            if (webExchange instanceof IServletWebExchange) {
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean isWebFluxContext(final IContext context) {
        if (!isWebContext(context)) {
            return false;
        }
        return getWebExchange(context) instanceof ISpringWebFluxWebExchange;
    }

    @Override
    public HttpServletRequest getHttpServletRequest(final IContext context) {
        return (HttpServletRequest) getServletWebExchange(context).getNativeRequestObject();
    }

    @Override
    public HttpServletResponse getHttpServletResponse(final IContext context) {
        return (HttpServletResponse) getServletWebExchange(context).getNativeResponseObject();
    }



    @Override
    public ServerWebExchange getServerWebExchange(final IContext context) {
        IWebExchange webExchange = getWebExchange(context);
        if (webExchange instanceof ISpringWebFluxWebExchange) {
            ISpringWebFluxWebExchange webFluxWebExchange = (ISpringWebFluxWebExchange) webExchange;
            return (ServerWebExchange) webFluxWebExchange.getNativeExchangeObject();
        }
        throw new TemplateProcessingException(
                "Cannot obtain ServerWebExchange from a non-WebFlux context implementation (\"" +
                context.getClass().getName() + "\")");
    }



    private static IServletWebExchange getServletWebExchange(final IContext context) {
        IWebExchange webExchange = getWebExchange(context);
        if (webExchange instanceof IServletWebExchange) {
            return (IServletWebExchange) webExchange;
        }
        throw new TemplateProcessingException(
                "Cannot obtain IServletWebExchange from a non-Servlet context implementation (\"" +
                        context.getClass().getName() + "\")");
    }



    private static IWebExchange getWebExchange(final IContext context) {
        if (context instanceof IWebContext) {
            IWebContext webContext = (IWebContext) context;
            return webContext.getExchange();
        }
        throw new TemplateProcessingException(
                "Cannot obtain IWebExchange from a non-Servlet context implementation (\"" +
                        context.getClass().getName() + "\")");
    }



}
