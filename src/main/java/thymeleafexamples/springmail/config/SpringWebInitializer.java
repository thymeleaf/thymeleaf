package thymeleafexamples.springmail.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 *  Spring configuration and Spring MVC bootstrapping.
 */
public class SpringWebInitializer implements WebApplicationInitializer {

    private static final String ENCODING = "UTF-8";

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(ThymeleafConfig.class);
		context.register(MailConfig.class);
		context.setServletContext(servletContext);
        configureSpringMvcFrontController(servletContext, context);
        configureEncondingFilter(servletContext);
	}
    
    private void configureSpringMvcFrontController(ServletContext servletContext, WebApplicationContext context) {
		Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
		servlet.addMapping("/");
		servlet.setLoadOnStartup(1);
    }

    private void configureEncondingFilter(ServletContext servletContext) {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(ENCODING);
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("encodingFilter", encodingFilter);
        // FIXME: set filter URL pattern 
        //      <url-pattern>/*</url-pattern>

    }
}
