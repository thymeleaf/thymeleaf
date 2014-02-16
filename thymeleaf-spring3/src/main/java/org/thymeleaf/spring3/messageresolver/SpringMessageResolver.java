/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.messageresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Implementation of {@link org.thymeleaf.messageresolver.IMessageResolver} that
 *   integrates the standard Spring way of resolving messages into Thymeleaf.
 * </p>
 * <p>
 *   This resolution is done by means of using the available Spring-configured
 *   {@link MessageSource} objects.  
 * </p>
 * <p>
 *   This message resolver will consider you are using a
 *   non-reloadable ResourceBundleMessageSource in your Spring
 *   configuration and thus will be considered Cacheable
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringMessageResolver
        extends AbstractMessageResolver 
        implements MessageSourceAware {
    

    private static final Logger logger = LoggerFactory.getLogger(SpringMessageResolver.class);

    
    private MessageSource messageSource;
    

    public SpringMessageResolver() {
        super();
    }
    
    
    

    
    @Override
    protected final void initializeSpecific() {
        
        /*
         * Check the application context has been set.
         */
        if (this.messageSource == null) {
            throw new ConfigurationException(
                    "Cannot initialize " + SpringMessageResolver.class.getSimpleName() + 
                    ": MessageSource has not been set. Either define this object as " +
                    "a Spring bean (which will automatically set the MessageSource) or, " +
                    "if you instance it directly, set the MessageSource manually using its "+
                    "corresponding setter method.");
        }

    }





    /**
     * <p>
     *   Returns the message source ({@link MessageSource}) to be
     *   used for message resolution.
     * </p>
     * 
     * @return the message source
     */
    public final MessageSource getMessageSource() {
        checkInitialized();
        return this.messageSource;
    }


    /**
     * <p>
     *   Uninitialized method meant for use by subclasses.
     * </p>
     * 
     * @return the message source
     */
    protected final MessageSource unsafeGetMessageSource() {
        return this.messageSource;
    }


    /**
     * <p>
     *   Sets the message source to be used for message resolution
     * </p>
     * 
     * @param messageSource the message source
     */
    public final void setMessageSource(final MessageSource messageSource) {
        checkNotInitialized();
        this.messageSource = messageSource;
    }
    
    



    public MessageResolution resolveMessage(
            final Arguments arguments, final String key, final Object[] messageParameters) {
        
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(arguments.getContext().getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Resolving message with key \"{}\" for template \"{}\" and locale \"{}\". Messages will be retrieved from Spring's MessageSource infrastructure.", new Object[] {TemplateEngine.threadIndex(), key, arguments.getTemplateName(), arguments.getContext().getLocale()});
        }
        
        try {
            
            final String resolvedMessage =
                this.messageSource.getMessage(key, messageParameters, arguments.getContext().getLocale());
            
            return new MessageResolution(resolvedMessage);
            
        } catch (NoSuchMessageException e) {
            return null;
        }
        
    }
    
    
    


}
