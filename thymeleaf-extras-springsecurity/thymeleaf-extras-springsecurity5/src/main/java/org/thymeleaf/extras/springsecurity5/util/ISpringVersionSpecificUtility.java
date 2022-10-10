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
import org.thymeleaf.expression.IExpressionObjects;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.1
 *
 */
interface ISpringVersionSpecificUtility {

    EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final IExpressionObjects expresionObjects);


    boolean isWebContext(final IContext context);
    boolean isWebMvcContext(final IContext context);
    boolean isWebFluxContext(final IContext context);


    HttpServletRequest getHttpServletRequest(final IContext context);
    HttpServletResponse getHttpServletResponse(final IContext context);
    ServerWebExchange getServerWebExchange(final IContext context);

}
