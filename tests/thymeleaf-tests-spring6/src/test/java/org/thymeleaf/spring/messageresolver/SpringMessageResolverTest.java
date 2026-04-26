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
package org.thymeleaf.spring.messageresolver;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.messageresolver.SpringMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class SpringMessageResolverTest {


    /*
     * A message key that exists in the MessageSource resolves to the configured message.
     */
    @Test
    public void testExistingKeyResolvesSuccessfully() throws Exception {

        final StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("greeting", Locale.ENGLISH, "Hello World");

        final SpringTemplateEngine engine = buildEngine(messageSource);

        final Context context = new Context(Locale.ENGLISH);
        final String result = engine.process("[[#{greeting}]]", context);

        Assertions.assertEquals("Hello World", result);
    }


    /*
     * When the MessageSource is configured with useCodeAsDefaultMessage=false (the default),
     * a missing key causes NoSuchMessageException to be thrown by the MessageSource.
     * Before the fix, this was silently swallowed and the template output ??key??.
     * After the fix, a TemplateProcessingException is thrown.
     */
    @Test
    public void testMissingKeyWithUseCodeAsDefaultMessageFalseThrows() throws Exception {

        final StaticMessageSource messageSource = new StaticMessageSource();
        // useCodeAsDefaultMessage defaults to false

        final SpringTemplateEngine engine = buildEngine(messageSource);

        final Context context = new Context(Locale.ENGLISH);

        Assertions.assertThrows(TemplateProcessingException.class, () ->
                engine.process("[[#{missing.key}]]", context));
    }


    /*
     * When the MessageSource is configured with useCodeAsDefaultMessage=true,
     * a missing key causes the MessageSource to return the key itself as the message.
     * No exception should be thrown; the key code is returned as-is.
     */
    @Test
    public void testMissingKeyWithUseCodeAsDefaultMessageTrueReturnsCode() throws Exception {

        final StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);

        final SpringTemplateEngine engine = buildEngine(messageSource);

        final Context context = new Context(Locale.ENGLISH);
        final String result = engine.process("[[#{missing.key}]]", context);

        Assertions.assertEquals("missing.key", result);
    }


    private static SpringTemplateEngine buildEngine(final StaticMessageSource messageSource) {

        final StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);

        final SpringMessageResolver messageResolver = new SpringMessageResolver();
        messageResolver.setMessageSource(messageSource);

        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setMessageResolver(messageResolver);

        return engine;
    }


}
