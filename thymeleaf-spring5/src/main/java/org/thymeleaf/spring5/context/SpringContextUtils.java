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
package org.thymeleaf.spring5.context;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
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
     *   Get the {@link ApplicationContext} from the Thymeleaf context.
     * </p>
     *
     * @param context the template context
     * @return the application context
     */
    public static ApplicationContext getApplicationContext(final ITemplateContext context) {
        if (context == null) {
            return null;
        }
        // The ThymeleafEvaluationContext is set into the model by ThymeleafView
        final ThymeleafEvaluationContext evaluationContext =
                (ThymeleafEvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);
        if (evaluationContext == null) {
            return null;
        }
        return evaluationContext.getApplicationContext();
    }



    /**
     * <p>
     *   Get the {@link IThymeleafRequestContext} from the Thymeleaf context.
     * </p>
     * <p>
     *   The returned object is a wrapper on the Spring request context that hides the fact of this request
     *   context corresponding to a Spring Web MVC or Spring Web Reactive application.
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
