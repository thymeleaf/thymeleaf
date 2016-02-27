/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.context.web;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring3.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;


final class Spring3VersionSpecificContextInitializer implements ISpringVersionSpecificContextInitializer {


    public void versionSpecificAdditionalVariableProcessing(
            final ApplicationContext applicationContext, final ConversionService conversionService,
            final Map<String, Object> variables) {

        /*
         * REQUEST CONTEXT
         */
        final RequestContext requestContext =
                (RequestContext) variables.get(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE);
        variables.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);

        /*
         * EVALUATION CONTEXT
         */
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, conversionService);

        variables.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

    }


    public IWebContext versionSpecificCreateContextInstance(
            final ApplicationContext applicationContext, final HttpServletRequest request,
            final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String, Object> variables) {

        return new WebContext(request, response, servletContext, locale, variables);

    }

    
}
