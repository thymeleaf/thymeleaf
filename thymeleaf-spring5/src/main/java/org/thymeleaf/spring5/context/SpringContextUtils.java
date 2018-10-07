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
package org.thymeleaf.spring5.context;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.expression.IThymeleafEvaluationContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;

/**
 * <p>
 *   Utility class for easy access of information stored at the context in a Spring-enabled application
 *   (such as the Spring ApplicationContext).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class SpringContextUtils {


    /**
     * <p>
     *   This is the name of the model attribute that will hold the (asychronously resolved)
     *   {@code WebSession} object in order to be used whenever needed, avoiding the need to block
     *   for obtaining it from the {@code ServerWebExchange}.
     * </p>
     * <p>
     *   Note resolving the {@code WebSession} from the reactive {@code Mono<WebSession>} stream does
     *   mean the creation of a {@code WebSession} instance, but not the real creation of a persisted session
     *   sent to the browser.
     * </p>
     * <p>
     *     Value: {@code "thymeleafWebSession"}
     * </p>
     *
     * @see org.springframework.web.server.WebSession
     */
    public static final String WEB_SESSION_ATTRIBUTE_NAME = "thymeleafWebSession";


    /**
     * <p>
     *   Get the {@link ApplicationContext} from the Thymeleaf template context.
     * </p>
     * <p>
     *   Note that the application context might not be always accessible (and thus this method
     *   can return {@code null}). Application Context will be accessible when the template is being executed
     *   as a Spring View, or else when an object of class {@link ThymeleafEvaluationContext} has been
     *   explicitly set into the {@link ITemplateContext} {@code context} with variable name
     *   {@link ThymeleafEvaluationContext#THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME}.
     * </p>
     *
     * @param context the template context.
     * @return the application context, or {@code null} if it could not be accessed.
     */
    public static ApplicationContext getApplicationContext(final ITemplateContext context) {
        if (context == null) {
            return null;
        }
        // The ThymeleafEvaluationContext is set into the model by ThymeleafView (or wrapped by the SPEL evaluator)
        final IThymeleafEvaluationContext evaluationContext =
                (IThymeleafEvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);
        if (evaluationContext == null || !(evaluationContext instanceof ThymeleafEvaluationContext)) {
            return null;
        }
        // Only when the evaluation context is a ThymeleafEvaluationContext we can access the ApplicationContext.
        // The reason is it could also be a wrapper on another EvaluationContext implementation, created at the
        // SPELVariableExpressionEvaluator on-the-fly (where ApplicationContext is not available because there might
        // even not exist one), instead of at ThymeleafView (where we are sure we are executing a Spring View and
        // have an ApplicationContext available).
        return ((ThymeleafEvaluationContext)evaluationContext).getApplicationContext();
    }



    /**
     * <p>
     *   Get the {@link IThymeleafRequestContext} from the Thymeleaf context.
     * </p>
     * <p>
     *   The returned object is a wrapper on the Spring request context that hides the fact of this request
     *   context corresponding to a Spring WebMVC or Spring WebFlux application.
     * </p>
     * <p>
     *   This will be done by looking for a context variable called
     *   {@link SpringContextVariableNames#THYMELEAF_REQUEST_CONTEXT}.
     * </p>
     *
     * @param context the context
     * @return the thymeleaf request context
     */
    public static IThymeleafRequestContext getRequestContext(final IExpressionContext context) {
        if (context == null) {
            return null;
        }
        return (IThymeleafRequestContext) context.getVariable(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT);
    }



    private SpringContextUtils() {
        super();
    }


}
