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
package org.thymeleaf.messageresolver;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class StandardMessageResolverFailOnMissingMessageTest {


    private static TemplateEngine buildEngine(final boolean failOnMissingMessage) {
        final Properties defaultMessages = new Properties();
        defaultMessages.setProperty("greeting", "Hello World");
        final StandardMessageResolver messageResolver = new StandardMessageResolver();
        messageResolver.setDefaultMessages(defaultMessages);
        messageResolver.setFailOnMissingMessage(failOnMissingMessage);
        final TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(new StringTemplateResolver());
        engine.setMessageResolver(messageResolver);
        return engine;
    }

    private static String process(final TemplateEngine engine, final String template) {
        return engine.process(
                new TemplateSpec(template, TemplateMode.TEXT),
                new Context(Locale.ENGLISH, Collections.<String,Object>singletonMap("keyArray", new String[] { "greeting", "missing.key" })));
    }

    private static void assertMissingMessageFails(final TemplateEngine engine, final String template) {
        final Exception e = Assertions.assertThrows(Exception.class, () -> process(engine, template), template);
        Throwable t = e;
        while (t != null && !(t instanceof TemplateProcessingException
                                && String.valueOf(t.getMessage()).startsWith("Could not resolve message with key \"missing.key\" for locale \"en\""))) {
            t = t.getCause();
        }
        Assertions.assertNotNull(t, template + " -> " + e);
    }



    @Test
    public void testDefault() {
        Assertions.assertFalse(new StandardMessageResolver().isFailOnMissingMessage());
        final TemplateEngine engine = buildEngine(false);
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key')} ?: 'fallback']]"));
    }


    @Test
    public void testFailOnMissingMessage() {
        final TemplateEngine engine = buildEngine(true);
        assertMissingMessageFails(engine, "[[#{missing.key}]]");
        assertMissingMessageFails(engine, "[[${#messages.msg('missing.key')}]]");
        assertMissingMessageFails(engine, "[[${#messages.arrayMsg(keyArray)}]]");
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key')} ?: 'fallback']]"));
        Assertions.assertEquals("true", process(engine, "[[${#messages.arrayMsgOrNull(keyArray)[1] == null}]]"));
    }

}
