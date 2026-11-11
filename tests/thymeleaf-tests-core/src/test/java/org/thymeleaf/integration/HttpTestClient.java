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
package org.thymeleaf.integration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * Minimal HTTP client for the integration tests: no redirects, no cookie handling (cookies are sent
 * explicitly by the tests), Accept-Language always set so that results do not depend on the JVM locale.
 */
final class HttpTestClient {

    static final String DEFAULT_LANGUAGE = "en";

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();


    static final class Result {

        final int status;
        final String contentType;
        final String setCookie;
        final String body;

        Result(final HttpResponse<String> response) {
            this.status = response.statusCode();
            this.contentType = response.headers().firstValue("Content-Type").orElse(null);
            this.setCookie = response.headers().firstValue("Set-Cookie").orElse(null);
            this.body = response.body();
        }

        String sessionId() {
            if (this.setCookie == null) {
                return null;
            }
            final Matcher matcher = Pattern.compile("JSESSIONID=([^;]+)").matcher(this.setCookie);
            return matcher.find() ? matcher.group(1) : null;
        }

    }


    static Result get(final EmbeddedTomcat server, final String path, final String... headers) throws Exception {
        return send(builder(server, path, headers).GET().build());
    }


    static Result post(final EmbeddedTomcat server, final String path, final Map<String, String[]> form, final String... headers)
            throws Exception {
        final String body =
                form.entrySet().stream()
                        .flatMap(e -> java.util.Arrays.stream(e.getValue()).map(v ->
                                URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(v, StandardCharsets.UTF_8)))
                        .collect(Collectors.joining("&"));
        return send(
                builder(server, path, headers)
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build());
    }


    private static HttpRequest.Builder builder(final EmbeddedTomcat server, final String path, final String... headers) {
        final HttpRequest.Builder builder =
                HttpRequest.newBuilder(URI.create(server.getBaseUrl() + path)).timeout(Duration.ofSeconds(60));
        boolean languageSet = false;
        for (int i = 0; i < headers.length; i += 2) {
            builder.header(headers[i], headers[i + 1]);
            languageSet |= "Accept-Language".equalsIgnoreCase(headers[i]);
        }
        if (!languageSet) {
            builder.header("Accept-Language", DEFAULT_LANGUAGE);
        }
        return builder;
    }


    private static Result send(final HttpRequest request) throws Exception {
        return new Result(CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    }


    private HttpTestClient() {
        super();
    }

}
