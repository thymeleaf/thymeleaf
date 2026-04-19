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
package org.thymeleaf.expression;

import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class ExpressionAllowedClassesTest {

    @Test
    public void testForbiddenClass() {

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.refresh();

        final Context context = new Context(Locale.US);
        // We simulate what ThymeleafView does by putting the ThymeleafEvaluationContext in the model
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, null);
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        // java.lang.System is forbidden by default
        final String template = "<p th:text=\"${T(java.lang.System).currentTimeMillis()}\"></p>";

        Assertions.assertThrows(Exception.class, () -> {
            templateEngine.process(template, context);
        });

    }

    @Test
    public void testAllowedClassOverride() {

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.refresh();

        final Context context = new Context(Locale.US);
        // We simulate what ThymeleafView does by putting the ThymeleafEvaluationContext in the model
        // BUT this time we provide the allowed class override
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, null, Collections.singleton(System.class));
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        final String template = "<p th:text=\"${T(java.lang.System).currentTimeMillis() != null}\"></p>";

        final String result = templateEngine.process(template, context);
        Assertions.assertTrue(result.contains("true"));

    }

    @Test
    public void testForbiddenClassMember() {

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.refresh();

        final Context context = new Context(Locale.US);
        // java.lang.Runtime is forbidden by default
        context.setVariable("runtime", Runtime.getRuntime());
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, null);
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        final String template = "<p th:text=\"${runtime.freeMemory() > 0}\"></p>";

        Assertions.assertThrows(Exception.class, () -> {
            templateEngine.process(template, context);
        });

    }

    @Test
    public void testAllowedClassMemberOverride() {

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.refresh();

        final Context context = new Context(Locale.US);
        // java.lang.Runtime is forbidden by default
        context.setVariable("runtime", Runtime.getRuntime());
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, null, Collections.singleton(Runtime.class));
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        final String template = "<p th:text=\"${runtime.freeMemory() > 0}\"></p>";

        final String result = templateEngine.process(template, context);
        Assertions.assertTrue(result.contains("true"));

    }

}
