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
package org.thymeleaf.spring.reactive.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.result.view.View;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring.reactive.exchange.TestingServerWebExchange;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import reactor.core.publisher.Mono;

public class ThymeleafReactiveViewTest {




    @Test
    public void testSeveralConversionServices() throws Exception {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/view/applicationContextConversionService.xml");

        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("spring/view/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        final ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
        resolver.setApplicationContext(context);
        resolver.setTemplateEngine(templateEngine);
        resolver.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));

        // testview2 does not exist as a declared bean at the application context
        final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices", Locale.US);

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");
        final TestingServerWebExchange exchange = new TestingServerWebExchange("/testing");

        view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block();

    }




    @Test
    public void testViewNameInRequestForbidden() throws Exception {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/view/applicationContextConversionService.xml");

        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("spring/view/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        final ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
        resolver.setApplicationContext(context);
        resolver.setTemplateEngine(templateEngine);
        resolver.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");

        // The view name is a fragment expression containing a Standard Expression, and a request parameter
        // echoes that same expression. As in the servlet ThymeleafView, this must be rejected so that no code
        // coming from direct user input is executed as a part of view-name resolution.
        final Map<String,List<String>> queryParams = new HashMap<>();
        queryParams.put("p", Collections.singletonList("${one}"));
        final TestingServerWebExchange exchange =
                new TestingServerWebExchange("/testing", queryParams, new HashMap<>(), new HashMap<>());

        final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);

        final TemplateProcessingException e = Assertions.assertThrows(
                TemplateProcessingException.class,
                () -> view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block());
        // Make sure it is the request-inspection guard that rejected the view name, and not an incidental error
        Assertions.assertTrue(e.getMessage() != null && e.getMessage().contains("View name contains an expression"));

    }




    @Test
    public void testFragmentViewNameAllowed() throws Exception {

        final ThymeleafReactiveViewResolver resolver = fragmentTestViewResolver();

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");

        // Fragment view name containing an expression, but no request input containing it: rendering is allowed,
        // also when the form data cannot be read (e.g. the request body was consumed in some other way)
        for (final boolean formDataReadable : new boolean[] { true, false }) {
            final TestingServerWebExchange exchange = new TestingServerWebExchange("/testing");
            if (!formDataReadable) {
                exchange.setFormDataException(new IllegalStateException("Request body already consumed"));
            }
            final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);
            Assertions.assertDoesNotThrow(() -> view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block());
        }

    }




    @Test
    public void testViewNameInRequestForbiddenIfFormDataNotReadable() throws Exception {

        final ThymeleafReactiveViewResolver resolver = fragmentTestViewResolver();

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");

        // Form data that cannot be read must not prevent the rest of the request from being inspected
        final Map<String,List<String>> queryParams = new HashMap<>();
        queryParams.put("p", Collections.singletonList("${one}"));
        final TestingServerWebExchange exchange =
                new TestingServerWebExchange("/testing", queryParams, new HashMap<>(), new HashMap<>());
        exchange.setFormDataException(new IllegalStateException("Request body already consumed"));

        final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);

        final TemplateProcessingException e = Assertions.assertThrows(
                TemplateProcessingException.class,
                () -> view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block());
        Assertions.assertTrue(e.getMessage() != null && e.getMessage().contains("View name contains an expression"));

    }


    private static ThymeleafReactiveViewResolver fragmentTestViewResolver() {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/view/applicationContextConversionService.xml");

        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("spring/view/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        final ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
        resolver.setApplicationContext(context);
        resolver.setTemplateEngine(templateEngine);
        resolver.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        return resolver;

    }




    @Test
    public void testViewNameInFormDataForbidden() throws Exception {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/view/applicationContextConversionService.xml");

        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("spring/view/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        final ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
        resolver.setApplicationContext(context);
        resolver.setTemplateEngine(templateEngine);
        resolver.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");

        // Same as above, but the colliding expression arrives in the request *form data* (body). The servlet
        // parameter map would include it, but the WebFlux query-parameter map does not, so the reactive check
        // must inspect the form data too. Query params are left empty so that only the form data can trigger it.
        final TestingServerWebExchange exchange = new TestingServerWebExchange("/testing");
        final MultiValueMap<String,String> formData = new LinkedMultiValueMap<String,String>();
        formData.add("p", "${one}");
        exchange.setFormData(formData);

        final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);

        final TemplateProcessingException e = Assertions.assertThrows(
                TemplateProcessingException.class,
                () -> view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block());
        Assertions.assertTrue(e.getMessage() != null && e.getMessage().contains("View name contains an expression"));

    }


    @Test
    public void testViewNameInRequestHeaderForbidden() throws Exception {

        final ThymeleafReactiveViewResolver resolver = fragmentTestViewResolver();

        final Map<String,Object> model = new ModelMap();
        model.put("one", "one");

        final MockServerWebExchange exchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/testing").header("X-Test", "${one}").build());
        final Mono<View> view = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);
        final TemplateProcessingException e = Assertions.assertThrows(
                TemplateProcessingException.class,
                () -> view.flatMap(v -> v.render(model, MediaType.TEXT_HTML, exchange)).block());
        Assertions.assertTrue(e.getMessage() != null && e.getMessage().contains("View name contains an expression"));

        final MockServerWebExchange otherExchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/testing").header("X-Test", "${two}").build());
        final Mono<View> otherView = resolver.resolveViewName("testSeveralConversionServices :: ${one}", Locale.US);
        Assertions.assertDoesNotThrow(() -> otherView.flatMap(v -> v.render(model, MediaType.TEXT_HTML, otherExchange)).block());

    }


}
