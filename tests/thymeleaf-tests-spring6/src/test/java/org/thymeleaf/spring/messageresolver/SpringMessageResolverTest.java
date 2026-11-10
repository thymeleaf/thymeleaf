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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.messageresolver.SpringMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class SpringMessageResolverTest {

    private static final java.util.Map<String,Object> KEY_ARRAY =
            Collections.<String,Object>singletonMap("keyArray", new String[] { "greeting", "missing.key" });


    @Test
    public void testExistingKey() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("Hello World", process(engine, "[[${#messages.msg('greeting')}]]"));
        Assertions.assertEquals("Hello World", process(engine, "[[${#messages.msgOrNull('greeting')}]]"));
        Assertions.assertEquals("<p>Hello World</p>", processHtml(engine, "<p th:text=\"#{greeting}\">...</p>"));
    }


    @Test
    public void testExistingKeyWithParameters() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        Assertions.assertEquals("Hello, John", process(engine, "[[#{welcome('John')}]]"));
        Assertions.assertEquals("Hello, John", process(engine, "[[${#messages.msg('welcome', 'John')}]]"));
        Assertions.assertEquals("Hello, John", process(engine, "[[${#messages.msgOrNull('welcome', 'John')}]]"));
        Assertions.assertEquals("Hello, John", process(engine, "[[${#messages.msgOrNullWithParams('welcome', {'John'})}]]"));
    }


    @Test
    public void testMissingKeyAbsentMessageRepresentation() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key('John')}]]"));
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[${#messages.msg('missing.key')}]]"));
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[${#messages.msgWithParams('missing.key', {'John'})}]]"));
        Assertions.assertEquals("<p>??missing.key_en??</p>", processHtml(engine, "<p th:text=\"#{missing.key}\">...</p>"));
        Assertions.assertEquals("[Hello World, ??missing.key_en??]",
                process(engine, "[[${#messages.listMsg({'greeting', 'missing.key'})}]]"));
        Assertions.assertEquals("??missing.key_en??",
                process(engine, "[[${#messages.arrayMsg(keyArray)[1]}]]", KEY_ARRAY));
    }


    @Test
    public void testMissingKeyOrNull() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key', 'John') ?: 'fallback'}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key', 'a', 'b', 'c') ?: 'fallback'}]]"));
        Assertions.assertEquals("fallback",
                process(engine, "[[${#messages.msgOrNullWithParams('missing.key', {'John'}) ?: 'fallback'}]]"));
        Assertions.assertEquals("Hello World",
                process(engine, "[[${#messages.msgOrNull('missing.key') ?: #messages.msg('greeting')}]]"));
        Assertions.assertEquals("<p>fallback</p>",
                processHtml(engine, "<p th:text=\"${#messages.msgOrNull('missing.key')} ?: 'fallback'\">...</p>"));
        Assertions.assertEquals("[Hello World, null]",
                process(engine, "[[${#messages.listMsgOrNull({'greeting', 'missing.key'})}]]"));
        Assertions.assertEquals("true",
                process(engine, "[[${#messages.arrayMsgOrNull(keyArray)[1] == null}]]", KEY_ARRAY));
        Assertions.assertEquals("true",
                process(engine, "[[${#messages.setMsgOrNull(keys).contains(null)}]]",
                        Collections.<String,Object>singletonMap("keys", new LinkedHashSet<String>(Arrays.asList("greeting", "missing.key")))));
    }


    @Test
    public void testMissingKeyWithUseCodeAsDefaultMessage() throws Exception {
        final StaticMessageSource messageSource = buildMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        final SpringTemplateEngine engine = buildEngine(messageSource);
        Assertions.assertEquals("missing.key", process(engine, "[[#{missing.key}]]"));
        Assertions.assertEquals("missing.key", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
    }


    @Test
    public void testApplicationContextWithoutMessageSourceBean() throws Exception {
        final GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.refresh();
        try {
            final SpringTemplateEngine engine = buildEngine(applicationContext);
            Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
            Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
        } finally {
            applicationContext.close();
        }
    }


    @Test
    public void testExplicitSpringMessageResolver() throws Exception {
        final SpringMessageResolver messageResolver = new SpringMessageResolver();
        messageResolver.setMessageSource(buildMessageSource());
        final SpringTemplateEngine engine = buildEngine(null);
        engine.setMessageResolver(messageResolver);
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
    }


    @Test
    public void testFollowingMessageResolvers() throws Exception {

        final SpringMessageResolver springMessageResolver = new SpringMessageResolver();
        springMessageResolver.setMessageSource(buildMessageSource());
        springMessageResolver.setOrder(Integer.valueOf(1));

        final Properties defaultMessages = new Properties();
        defaultMessages.setProperty("other.key", "Other message");
        final StandardMessageResolver standardMessageResolver = new StandardMessageResolver();
        standardMessageResolver.setDefaultMessages(defaultMessages);
        standardMessageResolver.setOrder(Integer.valueOf(2));

        final SpringTemplateEngine engine = buildEngine(null);
        engine.addMessageResolver(springMessageResolver);
        engine.addMessageResolver(standardMessageResolver);

        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("Other message", process(engine, "[[#{other.key}]]"));
        Assertions.assertEquals("Other message", process(engine, "[[${#messages.msgOrNull('other.key')}]]"));
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));

    }


    @Test
    public void testOriginBasedResolution() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        engine.addDialect(new OriginMessageDialect());
        Assertions.assertEquals("<p>Hello World</p>", processHtml(engine, "<p ori:msg=\"greeting\">...</p>"));
        Assertions.assertEquals("<p>Origin message</p>", processHtml(engine, "<p ori:msg=\"origin.key\">...</p>"));
        Assertions.assertEquals("<p>(null)</p>", processHtml(engine, "<p ori:msg=\"missing.key\">...</p>"));
        Assertions.assertEquals("<p>??missing.key_en??</p>", processHtml(engine, "<p ori:msgabsent=\"missing.key\">...</p>"));
    }




    @Test
    public void testFailOnMissingMessageDefault() throws Exception {
        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        Assertions.assertFalse(engine.isFailOnMissingMessage());
        Assertions.assertEquals("??missing.key_en??", process(engine, "[[#{missing.key}]]"));
    }


    @Test
    public void testFailOnMissingMessage() throws Exception {

        final SpringTemplateEngine engine = buildEngine(buildMessageSource());
        engine.setFailOnMissingMessage(true);

        assertMissingMessageFails(engine, "[[#{missing.key}]]");
        assertMissingMessageFails(engine, "[[#{missing.key('John')}]]");
        assertMissingMessageFails(engine, "[[${#messages.msg('missing.key')}]]");
        assertMissingMessageFails(engine, "[[${#messages.listMsg({'greeting', 'missing.key'})}]]");
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("Hello, John", process(engine, "[[#{welcome('John')}]]"));
        // The "OrNull" methods still return null
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key', 'John') ?: 'fallback'}]]"));
        Assertions.assertEquals("[Hello World, null]", process(engine, "[[${#messages.listMsgOrNull({'greeting', 'missing.key'})}]]"));
        Assertions.assertEquals("true", process(engine, "[[${#messages.arrayMsgOrNull(keyArray)[1] == null}]]", KEY_ARRAY));

    }


    @Test
    public void testFailOnMissingMessageWithoutMessageSource() throws Exception {
        // The engine creates a StandardMessageResolver when there is no MessageSource
        final SpringTemplateEngine engine = buildEngine(null);
        engine.setFailOnMissingMessage(true);
        assertMissingMessageFails(engine, "[[#{missing.key}]]");
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));
    }


    @Test
    public void testFailOnMissingMessageExplicitResolver() throws Exception {

        final SpringMessageResolver springMessageResolver = new SpringMessageResolver();
        springMessageResolver.setMessageSource(buildMessageSource());
        springMessageResolver.setFailOnMissingMessage(true);
        springMessageResolver.setOrder(Integer.valueOf(1));

        final Properties defaultMessages = new Properties();
        defaultMessages.setProperty("other.key", "Other message");
        final StandardMessageResolver standardMessageResolver = new StandardMessageResolver();
        standardMessageResolver.setDefaultMessages(defaultMessages);
        standardMessageResolver.setOrder(Integer.valueOf(2));

        final SpringTemplateEngine engine = buildEngine(null);
        engine.addMessageResolver(springMessageResolver);
        engine.addMessageResolver(standardMessageResolver);

        // All resolvers are tried before a message is considered missing
        Assertions.assertEquals("Hello World", process(engine, "[[#{greeting}]]"));
        Assertions.assertEquals("Other message", process(engine, "[[#{other.key}]]"));
        assertMissingMessageFails(engine, "[[#{missing.key}]]");
        Assertions.assertEquals("fallback", process(engine, "[[${#messages.msgOrNull('missing.key') ?: 'fallback'}]]"));

    }


    @Test
    public void testFailOnMissingMessageWithUseCodeAsDefaultMessage() throws Exception {
        final StaticMessageSource messageSource = buildMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        final SpringTemplateEngine engine = buildEngine(messageSource);
        engine.setFailOnMissingMessage(true);
        Assertions.assertEquals("missing.key", process(engine, "[[#{missing.key}]]"));
    }


    private static void assertMissingMessageFails(final SpringTemplateEngine engine, final String template) {
        final Exception e = Assertions.assertThrows(Exception.class, () -> process(engine, template), template);
        Throwable t = e;
        while (t != null && !(t instanceof TemplateProcessingException
                                && String.valueOf(t.getMessage()).startsWith("Could not resolve message with key \"missing.key\" for locale \"en\""))) {
            t = t.getCause();
        }
        Assertions.assertNotNull(t, template + " -> " + e);
    }


    private static StaticMessageSource buildMessageSource() {
        final StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("greeting", Locale.ENGLISH, "Hello World");
        messageSource.addMessage("welcome", Locale.ENGLISH, "Hello, {0}");
        return messageSource;
    }


    private static SpringTemplateEngine buildEngine(final MessageSource messageSource) {
        final StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        if (messageSource != null) {
            engine.setTemplateEngineMessageSource(messageSource);
        }
        return engine;
    }


    private static String process(final SpringTemplateEngine engine, final String template) {
        return process(engine, template, Collections.<String,Object>emptyMap());
    }


    private static String process(final SpringTemplateEngine engine, final String template, final java.util.Map<String,Object> variables) {
        return engine.process(template, new Context(Locale.ENGLISH, variables));
    }


    private static String processHtml(final SpringTemplateEngine engine, final String template) {
        return engine.process(new TemplateSpec(template, TemplateMode.HTML), new Context(Locale.ENGLISH));
    }

}
