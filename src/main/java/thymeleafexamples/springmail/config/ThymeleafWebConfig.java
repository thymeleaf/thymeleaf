package thymeleafexamples.springmail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

/**
 * Spring MVC and Thymeleaf configuration.
 */
@Configuration
@ComponentScan("thymeleafexamples.springmail")
@EnableWebMvc
public class ThymeleafWebConfig extends WebMvcConfigurerAdapter {

    /**
     * THYMELEAF: implementation of Spring's ViewResolver interface for the HTML pages of this web application.
     * (we would not need this if our app was not web)
     */
	@Bean
    public ViewResolver webViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(webTemplateEngine());
        resolver.setCharacterEncoding(ENCODING);
        return resolver;
    }
    
    /**
     * THYMELEAF: Template Engine (Spring4-specific version) for HTML pages.
     */
    private TemplateEngine webTemplateEngine() {
        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(webTemplateResolver());
        return templateEngine;
    }
    
    /**
     * THYMELEAF: Template Resolver for HTML pages.
     */
    private TemplateResolver webTemplateResolver() {
        TemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(ENCODING);
        // Template cache is true by default. Set to false if you want 
        // templates to be automatically updated when modified (for development purposes).
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use Spring servlet to serve static resources
	    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
	    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	    registry.addResourceHandler("/swf/**").addResourceLocations("/swf/");
    }

    @Bean
    public IMessageResolver messageResolver() {
        return new StandardMessageResolver();
    }
}
