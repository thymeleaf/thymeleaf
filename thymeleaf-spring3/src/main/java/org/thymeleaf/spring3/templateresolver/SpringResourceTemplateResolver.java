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
package org.thymeleaf.spring3.templateresolver;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.spring3.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link org.thymeleaf.templateresolver.ITemplateResolver} that extends {@link TemplateResolver}
 *   and uses a {@link org.thymeleaf.spring3.resourceresolver.SpringResourceResourceResolver} for resource resolution.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public class SpringResourceTemplateResolver
        extends TemplateResolver
        implements ApplicationContextAware, InitializingBean {


    private final SpringResourceResourceResolver resourceResolver;
    private ApplicationContext applicationContext = null;



    public SpringResourceTemplateResolver() {
        super();
        this.resourceResolver = new SpringResourceResourceResolver();
        super.setResourceResolver(this.resourceResolver);
    }



    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void afterPropertiesSet() throws Exception {

        Validate.notNull(this.applicationContext,
                "ApplicationContext has not been initialized in resource resolver. TemplateResolver or " +
                        "ResourceResolver might not have been correctly configured by the Spring Application Context.");

        final AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        beanFactory.initializeBean(this.resourceResolver, "springResourceResolver");

    }



    /**
     * <p>
     *   This method <b>should not be called</b>, because the resource resolver is
     *   fixed to be {@link org.thymeleaf.spring3.resourceresolver.SpringResourceResourceResolver}. Every execution of this method
     *   will result in an exception.
     * </p>
     * <p>
     *   If you need to select a different resource resolver, use the {@link org.thymeleaf.templateresolver.TemplateResolver}
     *   class instead.
     * </p>
     * 
     * @param resourceResolver the new resource resolver
     */
    @Override
    public void setResourceResolver(final IResourceResolver resourceResolver) {
        throw new ConfigurationException(
                "Cannot set a resource resolver on " + this.getClass().getName() + ". If " +
                "you want to set your own resource resolver, use " + TemplateResolver.class.getName() + 
                "instead");
    }

}
