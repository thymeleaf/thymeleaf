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
package org.thymeleaf.spring.reactive.view;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.reactive.result.view.View;
import org.thymeleaf.spring.reactive.exchange.TestingServerWebExchange;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;
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


}
