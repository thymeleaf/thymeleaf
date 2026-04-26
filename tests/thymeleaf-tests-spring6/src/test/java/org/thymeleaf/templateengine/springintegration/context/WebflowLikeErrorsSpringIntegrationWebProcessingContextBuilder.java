/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.springintegration.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.thymeleaf.testing.templateengine.testable.ITest;



/*
 * A context builder that wraps BindingResult instances with StrictFieldErrorsBindingResultWrapper
 * to simulate the behaviour of Spring Webflow's BindingModel, which does not accept wildcard
 * field expressions in getFieldErrors(String).
 */
public class WebflowLikeErrorsSpringIntegrationWebProcessingContextBuilder
        extends ErrorsSpringIntegrationWebProcessingContextBuilder {

    public WebflowLikeErrorsSpringIntegrationWebProcessingContextBuilder() {
        super();
    }


    @Override
    protected void initSpring(
            final ApplicationContext applicationContext,
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        super.initSpring(applicationContext, test, request, response, servletContext, locale, variables);

        final List<String> keys = new ArrayList<String>(variables.keySet());
        for (final String key : keys) {
            if (key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                final Object value = variables.get(key);
                if (value instanceof BindingResult) {
                    variables.put(key, new StrictFieldErrorsBindingResultWrapper((BindingResult) value));
                }
            }
        }

    }

}
