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


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebReactiveConfiguration;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import thymeleafsandbox.springreactive.thymeleaf.view.ThymeleafViewResolver;

@Configuration
@ComponentScan("thymeleafsandbox.springreactive.web")
public class SpringReactiveWebConfig extends WebReactiveConfiguration {




    public SpringReactiveWebConfig() {
        super();
    }



    // TODO * How could we add resource handlers for /images, /css, /js, etc.?




    /*
     * --------------------------------------
     * VIEW RESOLVER CONFIGURATION
     * --------------------------------------
     */

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
        final FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setPreTemplateLoaders(new SpringTemplateLoader(getApplicationContext(), "/webapp/templates/"));
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
    public SpringResourceTemplateResolver thymeleafTemplateResolver(){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(getApplicationContext());
        templateResolver.setPrefix("classpath:/webapp/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
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
