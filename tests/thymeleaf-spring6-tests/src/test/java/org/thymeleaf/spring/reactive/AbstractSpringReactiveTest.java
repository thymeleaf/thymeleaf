/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.reactive;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring.reactive.exchange.TestingServerHttpResponse;
import org.thymeleaf.spring.reactive.exchange.TestingServerWebExchange;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.context.Contexts;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveView;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractSpringReactiveTest {

    private static SpringWebFluxTemplateEngine templateEngine;
    private static DataBufferFactory bufferFactory;
    private static MediaType htmlMediaType;
    private static MediaType sseMediaType;
    private static Charset charset;

    // This array will contain the chunk sizes we will consider interesting for our tests
    private static int[] testResponseChunkSizes;

    private static Method thymeleafReactiveViewTemplateEngineSetter = null;
    private static Method thymeleafReactiveViewLocaleSetter = null;
    private static ApplicationContext applicationContext = null;



    static {

        testResponseChunkSizes = new int[115];
        testResponseChunkSizes[0] = Integer.MAX_VALUE; // Unlimited
        testResponseChunkSizes[1] = 65536;
        testResponseChunkSizes[2] = 32768;
        testResponseChunkSizes[3] = 16384;
        testResponseChunkSizes[4] = 8192;
        testResponseChunkSizes[5] = 4096;
        testResponseChunkSizes[6] = 3072;
        testResponseChunkSizes[7] = 2048;
        testResponseChunkSizes[8] = 1024;
        testResponseChunkSizes[9] = 513;
        testResponseChunkSizes[10] = 512;
        testResponseChunkSizes[11] = 511;
        testResponseChunkSizes[12] = 500;
        testResponseChunkSizes[13] = 256;
        testResponseChunkSizes[14] = 128;
        for (int i = 1; i <= 100; i++) {
            testResponseChunkSizes[115 - i] = i;
        }

        try {
            thymeleafReactiveViewTemplateEngineSetter =
                    ThymeleafReactiveView.class.getDeclaredMethod("setTemplateEngine", new Class<?>[] { ISpringWebFluxTemplateEngine.class });
            thymeleafReactiveViewLocaleSetter =
                    ThymeleafReactiveView.class.getDeclaredMethod("setLocale", new Class<?>[] { Locale.class });
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }

        applicationContext = new ClassPathXmlApplicationContext("classpath:spring/reactive/applicationContext.xml");

    }




    @BeforeAll
    public static void initTemplateEngine() {

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(ReactiveTestUtils.TEMPLATE_PATH_BASE);
        templateResolver.setSuffix(".html");

        templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        bufferFactory = new DefaultDataBufferFactory();

        htmlMediaType = MediaType.TEXT_HTML;
        sseMediaType = MediaType.TEXT_EVENT_STREAM;
        charset = Charset.forName("UTF-8");

    }





    protected static void testTemplate(
            final String template, final Set<String> markupSelectors, final IContext context,
            final String result) throws Exception {
        testTemplate(template, markupSelectors, context, result, false);
    }

    protected static void testTemplate(
            final String template, final Set<String> markupSelectors, final IContext context,
            final String result, final boolean sse) throws Exception {
        for (final int templateResponseChunkSize : testResponseChunkSizes) {
            testTemplate(template, markupSelectors, context, result, sse, templateResponseChunkSize);
        }
    }



    private static void testTemplate(
            final String template, final Set<String> markupSelectors, final IContext context,
            final String result, final boolean sse, final int responseMaxChunkSizeBytes) throws Exception {
        testTemplateDirectExecution(template, markupSelectors, context, result, sse, responseMaxChunkSizeBytes);
        testTemplateSpringView(template, markupSelectors, context, result, sse, responseMaxChunkSizeBytes);
    }




    private static void testTemplateDirectExecution(
            final String template, final Set<String> markupSelectors, final IContext context,
            final String result, final boolean sse, final int responseMaxChunkSizeBytes) throws Exception {

        final String dataDriverVariableName = detectDataDriver(context);
        final boolean isDataDriven = dataDriverVariableName != null;

        List<DataBuffer> resultBuffers = null;
        try {

            final Publisher<DataBuffer> resultStream =
                    templateEngine.processStream(template, markupSelectors, context, bufferFactory,
                    (sse? sseMediaType : htmlMediaType), charset, responseMaxChunkSizeBytes);

            resultBuffers = Flux.from(resultStream).collectList().block();
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error happened while executing reactive test for template " + template + " with markup " +
                    "selectors " + markupSelectors + ", context with variables " + context.getVariableNames() + " and " +
                    "response chunk size of " + responseMaxChunkSizeBytes + " bytes.", e);
        }

        if (responseMaxChunkSizeBytes != Integer.MAX_VALUE) {
            for (final DataBuffer resultBuffer : resultBuffers) {
                Assertions.assertTrue(resultBuffer.readableByteCount() <= responseMaxChunkSizeBytes, "Buffer returned by stream is of size larger than " + responseMaxChunkSizeBytes);
            }
        } else {
            if (!isDataDriven) {
                final int bufferCount = resultBuffers.size();
                Assertions.assertTrue(bufferCount == 1, "No limit set on buffer size, and non-data-driven: there should only be one result buffer instead of " + bufferCount);
            }
        }

        final String resultStr =
                resultBuffers
                        .stream()
                        .map((buffer) -> ReactiveTestUtils.bufferAsString(buffer, charset))
                        .map(ReactiveTestUtils::normalizeResult) // Note we NORMALIZE before joining it all
                        .collect(Collectors.joining());

        final String expected =
                ReactiveTestUtils.readExpectedNormalizedResults(result, charset);

        Assertions.assertEquals(expected, resultStr);

    }




    private static void testTemplateSpringView(
            final String template, final Set<String> markupSelectors, final IContext context,
            final String result, final boolean sse, final int responseMaxChunkSizeBytes) throws Exception {

        if (Contexts.isSpringWebFluxWebContext(context)) {
            // This test uses a SpringWebFluxContext and therefore is already acting at a lower level than
            // we can influence when directly instantiating the ThymeleafReactiveView. So we'll just skip
            return;
        }


        final String dataDriverVariableName = detectDataDriver(context);
        final boolean isDataDriven = dataDriverVariableName != null;


        final ThymeleafReactiveView thymeleafReactiveView =
                createThymeleafReactiveView(templateEngine, template, markupSelectors, Locale.US, responseMaxChunkSizeBytes);

        final Map<String, Object> model = new HashMap<String, Object>();
        for (final String variableName : context.getVariableNames()) {
            model.put(variableName, context.getVariable(variableName));
        }

        final ServerWebExchange serverWebExchange = new TestingServerWebExchange("testing");

        List<DataBuffer> resultBuffers = null;
        try {

            final Mono<ServerHttpResponse> responseStream =
                thymeleafReactiveView.render(model, (sse? sseMediaType : htmlMediaType), serverWebExchange)
                        .then(Mono.just(serverWebExchange.getResponse()));

            final ServerHttpResponse response = responseStream.block();

            resultBuffers = ((TestingServerHttpResponse)response).getWrittenOutput();

        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error happened while executing reactive test for template " + template + " with markup " +
                            "selectors " + markupSelectors + ", context with variables " + context.getVariableNames() + " and " +
                            "response chunk size of " + responseMaxChunkSizeBytes + " bytes.", e);
        }

        if (responseMaxChunkSizeBytes != Integer.MAX_VALUE) {
            for (final DataBuffer resultBuffer : resultBuffers) {
                Assertions.assertTrue(resultBuffer.readableByteCount() <= responseMaxChunkSizeBytes, "Buffer returned by stream is of size larger than " + responseMaxChunkSizeBytes);
            }
        } else {
            if (!isDataDriven) {
                final int bufferCount = resultBuffers.size();
                Assertions.assertTrue(bufferCount == 1, "No limit set on buffer size, and non-data-driven: there should only be one result buffer instead of " + bufferCount);
            }
        }

        final String resultStr =
                resultBuffers
                        .stream()
                        .map((buffer) -> ReactiveTestUtils.bufferAsString(buffer, charset))
                        .map(ReactiveTestUtils::normalizeResult) // Note we NORMALIZE before joining it all
                        .collect(Collectors.joining());

        final String expected =
                ReactiveTestUtils.readExpectedNormalizedResults(result, charset);

        Assertions.assertEquals(expected, resultStr);

    }



    private static String detectDataDriver(final IContext context) {

        final Set<String> contextVariableNames = context.getVariableNames();
        for (final String contextVariableName : contextVariableNames) {
            final Object contextVariableValue = context.getVariable(contextVariableName);
            if (contextVariableValue instanceof IReactiveDataDriverContextVariable) {
                return contextVariableName;
            }
        }
        return null;

    }





    private static ThymeleafReactiveView createThymeleafReactiveView(
            final ISpringWebFluxTemplateEngine templateEngine, final String viewName,
            final Set<String> markupSelectors, final Locale locale,
            final int responseMaxChunkSizeBytes) {

        if (markupSelectors != null && markupSelectors.size() > 1) {
            throw new RuntimeException("Cannot execute SpringView-based test with more than 1 markup selector");
        }

        final ThymeleafReactiveView view = new ThymeleafReactiveView();

        view.setTemplateName(viewName);
        if (markupSelectors != null) {
            view.setMarkupSelector(markupSelectors.iterator().next());
        }

        view.setResponseMaxChunkSizeBytes(responseMaxChunkSizeBytes);

        try {
            thymeleafReactiveViewTemplateEngineSetter.setAccessible(true);
            thymeleafReactiveViewTemplateEngineSetter.invoke(view, templateEngine);
            thymeleafReactiveViewTemplateEngineSetter.setAccessible(false);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        try {
            thymeleafReactiveViewLocaleSetter.setAccessible(true);
            thymeleafReactiveViewLocaleSetter.invoke(view, locale);
            thymeleafReactiveViewLocaleSetter.setAccessible(false);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        view.setApplicationContext(applicationContext);


        return view;

    }



}
