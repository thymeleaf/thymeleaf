/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring3.dialect.SpringStandardDialect;
import org.thymeleaf.spring3.messageresolver.SpringMessageResolver;




/**
 * <p>
 *   Subclass of {@link TemplateEngine} meant for Spring MVC applications,
 *   that establishes by default an instance of {@link SpringStandardDialect} 
 *   as a dialect (instead of an instance of {@link org.thymeleaf.standard.StandardDialect}, 
 *   which is the default in {@link TemplateEngine}.
 * </p>
 * <p>
 *   It also configures a {@link SpringMessageResolver} as message resolver, and
 *   provides a convenience method for setting the {@link MessageSource} that
 *   this resolver needs for message resolution. 
 * </p>
 * <p>
 *   Note that this class will validate during initialization that at least one of the
 *   configured dialects is {@link SpringStandardDialect} or a subclass of it.  
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class SpringTemplateEngine 
        extends TemplateEngine 
        implements MessageSourceAware, InitializingBean {

    
    private static final SpringStandardDialect SPRINGSTANDARD_DIALECT = new SpringStandardDialect();
    
    private MessageSource messageSource;
    
    
    public SpringTemplateEngine() {
        super();
        super.clearDialects();
        super.addDialect(SPRINGSTANDARD_DIALECT);
    }



    /**
     * <p>
     *   Convenience method for setting the message source that will
     *   be used by the {@link SpringMessageResolver} configured by default.
     * </p>
     * 
     * @param messageSource the message source to be used by the message resolver
     */
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    

    public void afterPropertiesSet() throws Exception {
        final SpringMessageResolver springMessageResolver = new SpringMessageResolver();
        springMessageResolver.setMessageSource(this.messageSource);
        super.setDefaultMessageResolvers(Collections.singleton(springMessageResolver));
    }
    




    
    @Override
    protected final void initializeSpecific() {
        
        final Configuration configuration = getConfiguration();
        final Map<String,IDialect> dialects = configuration.getDialects();
        for (final IDialect dialect : dialects.values()) {
            if (dialect instanceof SpringStandardDialect) {
                initializeSpringSpecific();
                return;
            }
        }
        throw new ConfigurationException(
                "When using " + SpringTemplateEngine.class.getSimpleName() + 
                ", at least one of the configured dialects must be or extend " + 
                SpringStandardDialect.class.getName() + ".");
        
    }

    
    
    /**
     * <p>
     *   Called during initialization of this Template Engine. Meant to be
     *   overridden by subclasses.
     * </p>
     */
    protected void initializeSpringSpecific() {
        // Nothing to be executed here. Meant for extension
    }
    
    
}
