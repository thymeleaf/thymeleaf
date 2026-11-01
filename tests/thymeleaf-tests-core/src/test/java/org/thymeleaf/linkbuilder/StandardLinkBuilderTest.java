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
package org.thymeleaf.linkbuilder;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;


public class StandardLinkBuilderTest {


    private static final IExpressionContext CONTEXT = new ExpressionContext(new TemplateEngine().getConfiguration());



    @ParameterizedTest
    @ValueSource(strings = {
            "javascript:void(0)",
            "JavaScript:void(0)",
            "\u0001javascript:void(0)",
            "\u0000 \u001Fjavascript:void(0)",
            "java\tscript:void(0)",
            "java\nscript:void(0)",
            "javascript\r:void(0)",
            "\tj\na\rv\ta\ns\rc\tr\ni\rp\tt\n:void(0)"
    })
    public void testJavaScriptSchemeRefused(final String base) {
        final TemplateProcessingException e =
                Assertions.assertThrows(
                        TemplateProcessingException.class,
                        () -> new StandardLinkBuilder().buildLink(CONTEXT, base, null));
        Assertions.assertTrue(e.getMessage().contains("'javascript:' is forbidden in this context"));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "javascript",
            "javascript.html",
            "javascripts/app.js",
            "java\tscripts/app.js",
            "java script:void(0)",
            "\u000Cjava\u000Cscript:void(0)",
            "page?x=javascript:void(0)",
            "http://example.com/javascript:void(0)"
    })
    public void testOtherLinksAllowed(final String base) {
        Assertions.assertEquals(base, new StandardLinkBuilder().buildLink(CONTEXT, base, null));
    }


    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "{p}             | javascript:void(0)",
            "{/p}            | javascript:void(0)",
            "{p}:void(0)     | javascript",
            "java{p}:void(0) | script",
            "{p}#x           | javascript:void(0)"
    })
    public void testJavaScriptSchemeRefusedWithVariableTemplates(final String base, final String value) {
        final TemplateProcessingException e =
                Assertions.assertThrows(
                        TemplateProcessingException.class,
                        () -> new StandardLinkBuilder().buildLink(CONTEXT, base, Collections.singletonMap("p", value)));
        Assertions.assertTrue(e.getMessage().contains("'javascript:' is forbidden in this context"));
    }


    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "~/{p}       | javascript:void(0)  | /javascript:void(0)",
            "x/{p}       | javascript:void(0)  | x/javascript:void(0)",
            "{p}/app.js  | javascripts         | javascripts/app.js",
            "{p}         | java script:void(0) | java%20script:void(0)",
            "{p}         | java\tscript:void(0) | java%09script:void(0)",
            "page?x={p}  | javascript:void(0)  | page?x=javascript:void(0)"
    })
    public void testOtherLinksWithVariableTemplatesAllowed(final String base, final String value, final String expected) {
        Assertions.assertEquals(
                expected, new StandardLinkBuilder().buildLink(CONTEXT, base, Collections.singletonMap("p", value)));
    }



}
