/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.spring5.context.web;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;
import org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring6.web.webflux.ISpringWebFluxWebExchange;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.IServletWebExchange;


final class Spring5VersionSpecificContextInitializer implements ISpringVersionSpecificContextInitializer {


    public void versionSpecificAdditionalVariableProcessing(
            final ApplicationContext applicationContext, final ConversionService conversionService,
            final IWebExchange exchange, final Map<String, Object> variables) {

        /*
         * EVALUATION CONTEXT
         */
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, conversionService);

        variables.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        /*
         * INITIALIZE OBJECTS WHICH IMPLEMENTATION DEPENDS ON WHETHER THIS IS A Spring MVC OR Spring WebFlux APPLICATION
         */
        if (exchange instanceof IServletWebExchange) {
            doVersionSpecificAdditionalVariableProcessingForSpringMVC((IServletWebExchange) exchange, variables);
        } else if (exchange instanceof ISpringWebFluxWebExchange) {
            doVersionSpecificAdditionalVariableProcessingForSpringWebFlux((ISpringWebFluxWebExchange) exchange, variables);
        } else {
            throw new IllegalStateException("Spring 5 is being used, but web exchange object is neither a Spring MVC nor a Spring WebFlux one");
        }

    }

    private void doVersionSpecificAdditionalVariableProcessingForSpringMVC(
            final IServletWebExchange exchange, final Map<String, Object> variables) {

        /*
         * REQUEST CONTEXT
         */
        final org.springframework.web.servlet.support.RequestContext requestContext =
                (org.springframework.web.servlet.support.RequestContext) variables.get(
                        AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE);
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);

        /*
         * THYMELEAF REQUEST CONTEXT
         */
        final SpringWebMvcThymeleafRequestContext thymeleafRequestContext =
                new SpringWebMvcThymeleafRequestContext(
                        requestContext, (HttpServletRequest) exchange.getRequest().getNativeRequestObject());
        variables.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);

    }

    private void doVersionSpecificAdditionalVariableProcessingForSpringWebFlux(
            final ISpringWebFluxWebExchange exchange, final Map<String, Object> variables) {

        /*
         * REQUEST CONTEXT
         */
        final RequestContext requestContext =
                (RequestContext) variables.get(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE);
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);

        /*
         * THYMELEAF REQUEST CONTEXT
         */
        final SpringWebMvcThymeleafRequestContext thymeleafRequestContext =
                new SpringWebFluxThymeleafRequestContext(
                        requestContext, (ServerWebExchange) exchange.getNativeExchangeObject());
        variables.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);

    }


    public IWebContext versionSpecificCreateContextInstance(
            final ApplicationContext applicationContext, final IWebExchange exchange,
            final Locale locale, final Map<String, Object> variables) {

        return new WebContext(exchange, locale, variables);

    }

    
}
