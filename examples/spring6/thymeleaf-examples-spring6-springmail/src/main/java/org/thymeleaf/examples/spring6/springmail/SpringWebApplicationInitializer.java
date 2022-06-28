package org.thymeleaf.examples.spring6.springmail;

import jakarta.servlet.Filter;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.thymeleaf.examples.spring6.springmail.business.SpringBusinessConfig;
import org.thymeleaf.examples.spring6.springmail.business.SpringMailConfig;
import org.thymeleaf.examples.spring6.springmail.web.SpringWebConfig;

public class SpringWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    public static final String CHARACTER_ENCODING = "UTF-8";


    public SpringWebApplicationInitializer() {
        super();
    }



    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { SpringWebConfig.class };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { SpringBusinessConfig.class, SpringMailConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        final CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(CHARACTER_ENCODING);
        encodingFilter.setForceEncoding(true);
        return new Filter[] { encodingFilter };
    }

    /*
     * This is needed because apparently CommonsMultiPartResolver was removed from Spring 6
     * https://docs.spring.io/spring-framework/docs/6.0.x/reference/html/web.html#mvc-multipart-resolver-standard
     */
    @Override
    protected void customizeRegistration(final ServletRegistration.Dynamic registration) {
        // Optionally also set maxFileSize, maxRequestSize, fileSizeThreshold
        registration.setMultipartConfig(new MultipartConfigElement("/tmp"));
        super.customizeRegistration(registration);
    }

}
