/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafsandbox.springreactive.application;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebReactiveConfiguration;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import thymeleafsandbox.springreactive.thymeleaf.context.SpringReactiveEngineContextFactory;
import thymeleafsandbox.springreactive.thymeleaf.linkbuilder.SpringReactiveLinkBuilder;
import thymeleafsandbox.springreactive.thymeleaf.view.ThymeleafViewResolver;

@Configuration
@ComponentScan("thymeleafsandbox.springreactive.web")
public class  SpringReactiveWebConfig extends WebReactiveConfiguration {

    // TODO * Extending WebReactiveConfiguration should be completely
    // TODO   unnecessary once https://github.com/bclozel/spring-boot-web-reactive/issues/31
    // TODO   is implemented (auto-configuration of View Resolvers). Once that is implemented,
    // TODO   this class should not extend anything and just let the auto-configurer do its job,
    // TODO   including the handling of static resources in Spring-Boot style.


    private ApplicationContext applicationContext;
    private ITemplateResolver thymeleafTemplateResolver; // auto-configured by Spring Boot



    public SpringReactiveWebConfig(
            final ApplicationContext applicationContext,
            final ITemplateResolver thymeleafTemplateResolver) {
        super();
        this.applicationContext = applicationContext;
        this.thymeleafTemplateResolver = thymeleafTemplateResolver;
    }



    /*
     * --------------------------------------
     * STATIC RESOURCE CONFIGURATION
     * --------------------------------------
     */

    // TODO * This should be unnecessary once https://github.com/bclozel/spring-boot-web-reactive/issues/31
    // TODO   is implemented (auto-configuration of View Resolvers). See comment above on this class' parent.

    @Override
    protected void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }




    /*
     * --------------------------------------
     * VIEW RESOLVER CONFIGURATION
     * --------------------------------------
     */

    // TODO * This should be unnecessary once https://github.com/bclozel/spring-boot-web-reactive/issues/31
    // TODO   is implemented (auto-configuration of View Resolvers). See comment above on this class' parent.

    @Override
    protected void configureViewResolvers(final ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafDataDrivenViewResolver());
        registry.viewResolver(thymeleafBufferedViewResolver());
        registry.viewResolver(thymeleafNormalViewResolver());
        registry.viewResolver(freeMarkerViewResolver());
    }




    /*
     * --------------------------------------
     * FREEMARKER CONFIGURATION
     * --------------------------------------
     */


    @Bean
    public FreeMarkerConfigurer freeMarkerConfig() {
        // Note this is the reactive version of FreeMarker's configuration, so there is no auto-configuration yet.
        final FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setPreTemplateLoaders(new SpringTemplateLoader(this.applicationContext, "/templates/"));
        return freeMarkerConfigurer;
    }

    /*
     * ViewResolver for FreeMarker templates executing in NORMAL mode (only mode available for FreeMarker)
     * No limit to output buffer size, all data fully resolved in context.
     */
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        final FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver("", ".ftl");
        freeMarkerViewResolver.setOrder(4);
        // TODO * Apparently no way to specify which views can be handled by this ViewResolver (viewNames property)
        return freeMarkerViewResolver;
    }





    /*
     * --------------------------------------
     * THYMELEAF CONFIGURATION
     * --------------------------------------
     */


    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(){
        // We override here the SpringTemplateEngine instance that would otherwise be instantiated by
        // Spring Boot because we want to apply the SpringReactive-specific context factory, link builder...
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(this.thymeleafTemplateResolver);
        templateEngine.setEngineContextFactory(new SpringReactiveEngineContextFactory());
        templateEngine.setLinkBuilder(new SpringReactiveLinkBuilder());
        return templateEngine;
    }

    /*
     * ViewResolver for Thymeleaf templates executing in NORMAL mode.
     * No limit to output buffer size, non-data-driven (all data fully resolved in context).
     */
    @Bean
    public ThymeleafViewResolver thymeleafNormalViewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
        viewResolver.setOrder(3);
        viewResolver.setViewNames(new String[] {"thymeleaf/*"});
        return viewResolver;
    }

    /*
     * ViewResolver for Thymeleaf templates executing in BUFFERED mode.
     * Non-data-driven (all data fully resolved in context), but with an established limit to output buffers size.
     */
    @Bean
    public ThymeleafViewResolver thymeleafBufferedViewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
        viewResolver.setOrder(2);
        viewResolver.setViewNames(new String[] {"thymeleaf/*buffered*"});
        viewResolver.setResponseMaxBufferSizeBytes(16384); // OUTPUT BUFFER size limit
        return viewResolver;
    }

    /*
     * ViewResolver for Thymeleaf templates executing in NORMAL mode
     * Data-driven: the "dataSource" variable can be a Publisher<X>, in which case it will drive the execution of
     * the engine and Thymeleaf will be executed as a part of the data flow.
     */
    @Bean
    public ThymeleafViewResolver thymeleafDataDrivenViewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
        viewResolver.setOrder(1);
        viewResolver.setViewNames(new String[] {"thymeleaf/*datadriven*"});
        viewResolver.setResponseMaxBufferSizeBytes(16384); // OUTPUT BUFFER size limit
        viewResolver.setDataDrivenVariableName("dataSource"); // Name of the Publisher<X> that will DRIVE execution
        viewResolver.setDataDrivenChunkSizeElements(1000); // Size (in elements) of the chunks of published data to be processed
        return viewResolver;
    }

}
