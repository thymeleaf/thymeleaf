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
package org.thymeleaf.extras.springsecurity5.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.expression.EvaluationContext;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;
import org.thymeleaf.spring5.expression.IThymeleafEvaluationContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContextWrapper;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.2
 *
 */
final class Spring5VersionSpecificUtility implements ISpringVersionSpecificUtility {




    Spring5VersionSpecificUtility() {
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
        if (context instanceof IWebContext || context instanceof ISpringWebFluxContext) {
            return true;
        }
        return false;
    }



    @Override
    public boolean isWebMvcContext(final IContext context) {
        return context instanceof IWebContext;
    }



    @Override
    public boolean isWebFluxContext(final IContext context) {
        return context instanceof ISpringWebFluxContext;
    }




    @Override
    public HttpServletRequest getHttpServletRequest(final IContext context) {
        if (context instanceof IWebContext) {
            return ((IWebContext)context).getRequest();
        }
        throw new TemplateProcessingException(
                "Cannot obtain HttpServletRequest from a non-Servlet context implementation (\"" +
                context.getClass().getName() + "\")");
    }


    @Override
    public HttpServletResponse getHttpServletResponse(final IContext context) {
        if (context instanceof IWebContext) {
            return ((IWebContext)context).getResponse();
        }
        throw new TemplateProcessingException(
                "Cannot obtain HttpServletResponse from a non-WebFlux context implementation (\"" +
                context.getClass().getName() + "\")");
    }


    @Override
    public ServerWebExchange getServerWebExchange(final IContext context) {
        if (context instanceof ISpringWebFluxContext) {
            return ((ISpringWebFluxContext)context).getExchange();
        }
        throw new TemplateProcessingException(
                "Cannot obtain ServerWebExchange from a non-WebFlux context implementation (\"" +
                context.getClass().getName() + "\")");
    }


}
