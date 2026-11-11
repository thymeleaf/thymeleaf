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
package org.thymeleaf.spring5.integration.webflux;

import java.util.Collections;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.integration.CodeFormatter;
import org.thymeleaf.spring5.integration.IntegrationBean;
import org.thymeleaf.spring5.integration.IntegrationTestUtils;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;

@Configuration
@EnableWebFlux
@Import(WebFluxIntegrationController.class)
public class WebFluxPlainConfiguration implements WebFluxConfigurer {

    public static final String USE_CODE_AS_DEFAULT_MESSAGE = "test.useCodeAsDefaultMessage";
    public static final String FAIL_ON_MISSING_MESSAGE = "test.failOnMissingMessage";
    public static final String ENABLE_SPRING_EL_COMPILER = "test.enableSpringELCompiler";
    public static final String RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES = "test.renderHiddenMarkersBeforeCheckboxes";
    public static final String TEMPLATE_ENGINE_MESSAGE_SOURCE = "test.templateEngineMessageSource";
    public static final String STANDARD_MESSAGE_RESOLVER = "test.standardMessageResolver";
    public static final String VIEW_NAMES = "test.viewNames";
    public static final String EXCLUDED_VIEW_NAMES = "test.excludedViewNames";
    public static final String STATIC_VARIABLE = "test.staticVariable";
    public static final String MEDIA_TYPES = "test.mediaTypes";
    public static final String MAX_CHUNK_SIZE = "test.maxChunkSize";
    public static final String FULL_MODE_VIEW_NAMES = "test.fullModeViewNames";
    public static final String CHUNKED_MODE_VIEW_NAMES = "test.chunkedModeViewNames";
    public static final String USE_DECOUPLED_LOGIC = "test.useDecoupledLogic";


    private final Environment environment;


    public WebFluxPlainConfiguration(final Environment environment) {
        super();
        this.environment = environment;
    }


    private boolean flag(final String name) {
        return this.environment.getProperty(name, Boolean.class, Boolean.FALSE).booleanValue();
    }


    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addFormatter(new CodeFormatter());
    }


    @Override
    public void configureViewResolvers(final ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }


    @Bean
    public ResourceBundleMessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(IntegrationTestUtils.MESSAGES_BASENAME);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(flag(USE_CODE_AS_DEFAULT_MESSAGE));
        return messageSource;
    }


    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix(IntegrationTestUtils.TEMPLATE_PREFIX);
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTextTemplateModePatterns(Collections.singleton("*.txt"));
        templateResolver.setJavaScriptTemplateModePatterns(Collections.singleton("*.js"));
        templateResolver.setUseDecoupledLogic(flag(USE_DECOUPLED_LOGIC));
        return templateResolver;
    }


    @Bean
    public SpringWebFluxTemplateEngine templateEngine() {
        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setFailOnMissingMessage(flag(FAIL_ON_MISSING_MESSAGE));
        templateEngine.setEnableSpringELCompiler(flag(ENABLE_SPRING_EL_COMPILER));
        templateEngine.setRenderHiddenMarkersBeforeCheckboxes(flag(RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES));
        if (flag(TEMPLATE_ENGINE_MESSAGE_SOURCE)) {
            final StaticMessageSource templateEngineMessageSource = new StaticMessageSource();
            templateEngineMessageSource.addMessage("greeting", Locale.US, "Hi from the engine message source, {0}");
            templateEngine.setTemplateEngineMessageSource(templateEngineMessageSource);
        }
        if (flag(STANDARD_MESSAGE_RESOLVER)) {
            templateEngine.setMessageResolver(new StandardMessageResolver());
        }
        return templateEngine;
    }


    @Bean
    public ThymeleafReactiveViewResolver viewResolver() {
        final ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        final Integer maxChunkSize = this.environment.getProperty(MAX_CHUNK_SIZE, Integer.class);
        if (maxChunkSize != null) {
            viewResolver.setResponseMaxChunkSizeBytes(maxChunkSize.intValue());
        }
        final String[] mediaTypes = this.environment.getProperty(MEDIA_TYPES, String[].class);
        if (mediaTypes != null) {
            viewResolver.setSupportedMediaTypes(MediaType.parseMediaTypes(String.join(",", mediaTypes)));
        }
        viewResolver.setViewNames(this.environment.getProperty(VIEW_NAMES, String[].class));
        viewResolver.setExcludedViewNames(this.environment.getProperty(EXCLUDED_VIEW_NAMES, String[].class));
        viewResolver.setFullModeViewNames(this.environment.getProperty(FULL_MODE_VIEW_NAMES, String[].class));
        viewResolver.setChunkedModeViewNames(this.environment.getProperty(CHUNKED_MODE_VIEW_NAMES, String[].class));
        if (this.environment.containsProperty(STATIC_VARIABLE)) {
            viewResolver.addStaticVariable("staticVar", this.environment.getProperty(STATIC_VARIABLE));
        }
        return viewResolver;
    }


    @Bean
    public IntegrationBean integrationBean() {
        return new IntegrationBean();
    }


    @Bean
    public TestReactiveRequestDataValueProcessor requestDataValueProcessor() {
        return new TestReactiveRequestDataValueProcessor();
    }


    @Bean
    public TestWebFilter testWebFilter() {
        return new TestWebFilter();
    }


    @Bean
    public ExceptionMessageWebExceptionHandler exceptionMessageWebExceptionHandler() {
        return new ExceptionMessageWebExceptionHandler();
    }

}
