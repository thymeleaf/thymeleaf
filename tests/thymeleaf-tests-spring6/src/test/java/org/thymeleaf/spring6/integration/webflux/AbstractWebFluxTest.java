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
package org.thymeleaf.spring6.integration.webflux;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class AbstractWebFluxTest {



    protected abstract AnnotationConfigApplicationContext newContext();

    protected abstract Class<?> getConfigurationClass();

    protected abstract Map<String,Object> getBaseProperties();


    protected static Map<String,Object> properties(final String... namesAndValues) {
        final Map<String,Object> properties = new HashMap<>();
        for (int i = 0; i < namesAndValues.length; i += 2) {
            properties.put(namesAndValues[i], namesAndValues[i + 1]);
        }
        return properties;
    }


    protected AnnotationConfigApplicationContext createContext(
            final Map<String,Object> properties, final Class<?>... additionalConfigurationClasses) {
        final Map<String,Object> allProperties = new HashMap<>(getBaseProperties());
        allProperties.putAll(properties);
        final AnnotationConfigApplicationContext context = newContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", allProperties));
        context.register(getConfigurationClass());
        if (additionalConfigurationClasses.length > 0) {
            context.register(additionalConfigurationClasses);
        }
        context.refresh();
        return context;
    }


    protected WebTestClient client(final Map<String,Object> properties) {
        return WebTestClient.bindToApplicationContext(createContext(properties)).build();
    }


    protected WebTestClient client() {
        return client(Collections.emptyMap());
    }


    protected static EntityExchangeResult<String> get(final WebTestClient client, final String uri, final String acceptLanguage) {
        return client.get().uri(uri).header(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage).exchange()
                .expectBody(String.class).returnResult();
    }


    protected static String render(final WebTestClient client, final String uri) {
        final EntityExchangeResult<String> result = get(client, uri, "en-US");
        Assertions.assertEquals(200, result.getStatus().value(), result.getResponseBody());
        return result.getResponseBody();
    }


    protected static List<String> renderChunks(final WebTestClient client, final String uri) {
        final FluxExchangeResult<DataBuffer> result =
                client.get().uri(uri).header(HttpHeaders.ACCEPT_LANGUAGE, "en-US").exchange().returnResult(DataBuffer.class);
        Assertions.assertEquals(200, result.getStatus().value());
        return result.getResponseBody().map(buffer -> {
            final String chunk = buffer.toString(StandardCharsets.UTF_8);
            DataBufferUtils.release(buffer);
            return chunk;
        }).collectList().block();
    }


    protected static void assertFailure(final EntityExchangeResult<String> result, final String expectedMessage) {
        Assertions.assertEquals(500, result.getStatus().value());
        Assertions.assertTrue(result.getResponseBody() != null && result.getResponseBody().contains(expectedMessage),
                result.getResponseBody());
    }

}
