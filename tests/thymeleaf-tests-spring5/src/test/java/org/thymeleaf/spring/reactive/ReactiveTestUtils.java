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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring.reactive.exchange.TestingServerWebExchange;
import org.thymeleaf.spring.reactive.messagesource.TestingMessageSource;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.web.webflux.ISpringWebFluxWebExchange;
import org.thymeleaf.spring5.web.webflux.SpringWebFluxWebApplication;
import org.thymeleaf.util.ClassLoaderUtils;

public final class ReactiveTestUtils {


    public static final String TEMPLATE_PATH_BASE = "spring/reactive/";




    public static String bufferAsString(final DataBuffer dataBuffer, final Charset charset) {
        try {
            return IOUtils.toString(dataBuffer.asInputStream(), charset.name());
        } catch (final IOException e) {
            throw new TemplateProcessingException("Error converting databuffer to string", e);
        }
    }




    public static String readExpectedResults(final String templateName, final Charset charset) {
        final String path = TEMPLATE_PATH_BASE + templateName + "-result.html";
        try {
            final InputStream templateIS = ClassLoaderUtils.loadResourceAsStream(path);
            return IOUtils.toString(templateIS, charset.name());
        } catch (final IOException e) {
            throw new TemplateProcessingException("Could not read '" + path + "'", e);
        }
    }




    public static String readExpectedNormalizedResults(final String templateName, final Charset charset) {
        return normalizeResult(readExpectedResults(templateName, charset));
    }




    public static String normalizeResult(final String result) {
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            final char c = result.charAt(i);
            if (!Character.isWhitespace(c)) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }



    public static IWebContext buildReactiveContext(final Map<String,Object> model) {
        return buildReactiveContext(model, null);
    }

    public static IWebContext buildReactiveContext(
            final Map<String,Object> model, final RequestDataValueProcessor requestDataValueProcessor) {

        final ServerWebExchange exchange = new TestingServerWebExchange("reactive07");

        final TestingMessageSource testingMessageSource = new TestingMessageSource();

        final RequestContext requestContext = new RequestContext(exchange, model, testingMessageSource, requestDataValueProcessor);

        final SpringWebFluxThymeleafRequestContext thymeleafRequestContext =
                new SpringWebFluxThymeleafRequestContext(requestContext, exchange);

        model.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        model.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);

        final ISpringWebFluxWebExchange webExchange =
                SpringWebFluxWebApplication.buildApplication(null)
                        .buildExchange(exchange, new Locale("en", "US"), MediaType.parseMediaType("text/html"), Charset.forName("UTF-8"));

        final WebContext context = new WebContext(webExchange);
        context.setVariables(model);

        return context;

    }





    private ReactiveTestUtils() {
        super();
    }




    
}
