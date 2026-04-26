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
package org.thymeleaf.parsing;

import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


public class ParsingExceptionLineColTest {


    public ParsingExceptionLineColTest() {
        super();
    }


    /*
     * Tests that when a TemplateProcessingException is thrown during template processing
     * with caching DISABLED (AttoParser directly drives parse+process), the resulting
     * TemplateInputException still carries the correct line and column information.
     *
     * Without the fix in AbstractMarkupTemplateParser, AttoParser's ParseException(Throwable)
     * constructor would lose the line/col from the cause, and the TemplateInputException
     * would be created without location info.
     */
    @Test
    public void testProcessingExceptionLineColWithoutCaching() throws Exception {

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        final TemplateInputException exception =
                Assertions.assertThrows(TemplateInputException.class, () ->
                        templateEngine.process("parsing/parsingexception01.html", new Context(), new StringWriter()));

        Assertions.assertTrue(exception.hasLineAndCol(),
                "TemplateInputException should carry line and col when caching is disabled");
        Assertions.assertEquals(Integer.valueOf(7), exception.getLine(),
                "Exception line should point to the th:text attribute");
        Assertions.assertEquals(Integer.valueOf(8), exception.getCol(),
                "Exception col should point to the th:text attribute");
    }


    /*
     * Tests that line and column information is also preserved when caching is enabled.
     * With caching, AttoParser only drives the parse phase (building the model); processing
     * runs against the cached model independently, so no ParseException wrapping occurs
     * and line/col was never lost in this path.
     *
     * Note: in this path the exception is a TemplateProcessingException (not TemplateInputException),
     * because the processing exception propagates directly without going through AttoParser.
     */
    @Test
    public void testProcessingExceptionLineColWithCaching() throws Exception {

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(true);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        final TemplateProcessingException exception =
                Assertions.assertThrows(TemplateProcessingException.class, () ->
                        templateEngine.process("parsing/parsingexception01.html", new Context(), new StringWriter()));

        Assertions.assertTrue(exception.hasLineAndCol(),
                "TemplateProcessingException should carry line and col when caching is enabled");
        Assertions.assertEquals(Integer.valueOf(7), exception.getLine(),
                "Exception line should point to the th:text attribute");
        Assertions.assertEquals(Integer.valueOf(8), exception.getCol(),
                "Exception col should point to the th:text attribute");
    }


}
