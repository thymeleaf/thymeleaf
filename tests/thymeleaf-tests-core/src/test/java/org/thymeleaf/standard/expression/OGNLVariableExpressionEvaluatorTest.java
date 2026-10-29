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
package org.thymeleaf.standard.expression;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class OGNLVariableExpressionEvaluatorTest {


    public static final class Item {
        public String getName() { return "item"; }
        public Item getChild() { return null; }
    }


    private static String process(final String template) {
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        final Context context = new Context(Locale.US);
        context.setVariable("item", new Item());
        return templateEngine.process(template, context);
    }

    private static String rootMessage(final Throwable throwable) {
        Throwable t = throwable;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage();
    }



    @Test
    public void testSafeNavigation() {
        Assertions.assertEquals("<p>item</p>", process("<p th:text=\"${item?.name}\">..</p>"));
        Assertions.assertEquals("<p></p>", process("<p th:text=\"${item.child?.name}\">..</p>"));
        Assertions.assertEquals("<p></p>", process("<p th:text=\"${nonexistent?.name}\">..</p>"));
    }


    @Test
    public void testExpressionObjectsAndLocalVariables() {
        Assertions.assertEquals("<p>ABC</p>", process("<p th:text=\"${#strings.toUpperCase('abc')}\">..</p>"));
        Assertions.assertEquals("<p>6</p>", process("<p th:text=\"${#x = 5, #x + 1}\">..</p>"));
        // Expression objects cannot be replaced from expressions
        final Exception e = Assertions.assertThrows(Exception.class,
                () -> process("<p th:text=\"${#strings = 'x', #strings}\">..</p>"));
        Assertions.assertTrue(String.valueOf(rootMessage(e)).contains("key matches the name of one of the expression objects"));
    }


}
