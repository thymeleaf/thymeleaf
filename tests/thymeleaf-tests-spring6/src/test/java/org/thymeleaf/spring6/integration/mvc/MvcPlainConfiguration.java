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
package org.thymeleaf.spring6.integration.mvc;

import java.util.Collections;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.integration.CodeFormatter;
import org.thymeleaf.spring6.integration.IntegrationBean;
import org.thymeleaf.spring6.integration.IntegrationTestUtils;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@EnableWebMvc
@Import(MvcIntegrationController.class)
public class MvcPlainConfiguration implements WebMvcConfigurer {

    public static final String USE_CODE_AS_DEFAULT_MESSAGE = "test.useCodeAsDefaultMessage";
    public static final String FAIL_ON_MISSING_MESSAGE = "test.failOnMissingMessage";
    public static final String ENABLE_SPRING_EL_COMPILER = "test.enableSpringELCompiler";
    public static final String RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES = "test.renderHiddenMarkersBeforeCheckboxes";
    public static final String TEMPLATE_ENGINE_MESSAGE_SOURCE = "test.templateEngineMessageSource";
    public static final String STANDARD_MESSAGE_RESOLVER = "test.standardMessageResolver";
    public static final String CONTENT_TYPE = "test.contentType";
    public static final String VIEW_NAMES = "test.viewNames";
    public static final String EXCLUDED_VIEW_NAMES = "test.excludedViewNames";
    public static final String STATIC_VARIABLE = "test.staticVariable";
    public static final String ALWAYS_PROCESS_REDIRECT_AND_FORWARD = "test.alwaysProcessRedirectAndForward";
    public static final String TEMPLATE_PREFIX = "test.templatePrefix";
    public static final String USE_DECOUPLED_LOGIC = "test.useDecoupledLogic";


    private final Environment environment;


    public MvcPlainConfiguration(final Environment environment) {
        super();
        this.environment = environment;
    }


    private boolean flag(final String name, final boolean defaultValue) {
        return this.environment.getProperty(name, Boolean.class, Boolean.valueOf(defaultValue)).booleanValue();
    }


    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addFormatter(new CodeFormatter());
    }


    @Bean
    public ResourceBundleMessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(IntegrationTestUtils.MESSAGES_BASENAME);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(flag(USE_CODE_AS_DEFAULT_MESSAGE, false));
        return messageSource;
    }


    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix(this.environment.getProperty(TEMPLATE_PREFIX, IntegrationTestUtils.TEMPLATE_PREFIX));
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTextTemplateModePatterns(Collections.singleton("*.txt"));
        templateResolver.setJavaScriptTemplateModePatterns(Collections.singleton("*.js"));
        templateResolver.setUseDecoupledLogic(flag(USE_DECOUPLED_LOGIC, false));
        return templateResolver;
    }


    @Bean
    public SpringTemplateEngine templateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setFailOnMissingMessage(flag(FAIL_ON_MISSING_MESSAGE, false));
        templateEngine.setEnableSpringELCompiler(flag(ENABLE_SPRING_EL_COMPILER, false));
        templateEngine.setRenderHiddenMarkersBeforeCheckboxes(flag(RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES, false));
        if (flag(TEMPLATE_ENGINE_MESSAGE_SOURCE, false)) {
            final StaticMessageSource templateEngineMessageSource = new StaticMessageSource();
            templateEngineMessageSource.addMessage("greeting", Locale.US, "Hi from the engine message source, {0}");
            templateEngine.setTemplateEngineMessageSource(templateEngineMessageSource);
        }
        if (flag(STANDARD_MESSAGE_RESOLVER, false)) {
            templateEngine.setMessageResolver(new StandardMessageResolver());
        }
        return templateEngine;
    }


    @Bean
    public ThymeleafViewResolver viewResolver() {
        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setContentType(this.environment.getProperty(CONTENT_TYPE));
        viewResolver.setViewNames(this.environment.getProperty(VIEW_NAMES, String[].class));
        viewResolver.setExcludedViewNames(this.environment.getProperty(EXCLUDED_VIEW_NAMES, String[].class));
        if (this.environment.containsProperty(STATIC_VARIABLE)) {
            viewResolver.addStaticVariable("staticVar", this.environment.getProperty(STATIC_VARIABLE));
        }
        viewResolver.setAlwaysProcessRedirectAndForward(flag(ALWAYS_PROCESS_REDIRECT_AND_FORWARD, true));
        return viewResolver;
    }


    @Bean
    public IntegrationBean integrationBean() {
        return new IntegrationBean();
    }


    @Bean
    public TestRequestDataValueProcessor requestDataValueProcessor() {
        return new TestRequestDataValueProcessor();
    }

}
