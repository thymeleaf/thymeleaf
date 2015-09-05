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
package org.thymeleaf.message.resolver;

import java.util.Properties;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.standard.util.StandardMessageResolutionUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Standard implementation of {@link IMessageResolver}.
 * </p>
 * <p>
 *   A message in template <tt>/WEB-INF/templates/home.html</tt> for locale
 *   <tt>ll_CC-vv</tt> ("ll" = language, "CC" = country, "vv" = variant) would be looked for
 *   in <tt>.properties</tt> files in the following sequence:
 * </p>
 * <ul>
 *   <li>/WEB-INF/templates/home_ll_CC-vv.properties</li>
 *   <li>/WEB-INF/templates/home_ll_CC.properties</li>
 *   <li>/WEB-INF/templates/home_ll.properties</li>
 *   <li>/WEB-INF/templates/home.properties</li>
 *   <li>(default messages, if they exist)</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class StandardMessageResolver 
        extends AbstractMessageResolver {

    
    private final Properties defaultMessages;
    

    public StandardMessageResolver() {
        super();
        this.defaultMessages = new Properties();
    }

    
    

    /**
     * <p>
     *   Returns the default messages. These messages will be used 
     *   if no other messages can be found.  
     * </p>
     * 
     * @return the default messages
     */
    public Properties getDefaultMessages() {
        final Properties properties = new Properties();
        properties.putAll(this.defaultMessages);
        return properties;
    }
    
    
    /**
     * <p>
     *   Unsafe method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the default messages
     */
    protected Properties unsafeGetDefaultMessages() {
        return this.defaultMessages;
    }


    /**
     * <p>
     *   Sets the default messages. These messages will be used 
     *   if no other messages can be found.
     * </p>
     * 
     * @param defaultMessages the new default messages
     */
    public void setDefaultMessages(final Properties defaultMessages) {
        if (defaultMessages != null) {
            this.defaultMessages.putAll(defaultMessages);
        }
    }
    

    /**
     * <p>
     *   Adds a new message to the set of default messages.
     * </p>
     * 
     * @param key the message key
     * @param value the message value (text)
     */
    public void addDefaultMessage(final String key, final String value) {
        Validate.notNull(key, "Key for default message cannot be null");
        Validate.notNull(value, "Value for default message cannot be null");
        this.defaultMessages.put(key, value);
    }

    
    /**
     * <p>
     *   Clears the set of default messages.
     * </p>
     */
    public void clearDefaultMessages() {
        this.defaultMessages.clear();
    }

    
    
    


    public MessageResolution resolveMessage(
            final ITemplateProcessingContext processingContext, final String key, final Object[] messageParameters) {
        
        // This method can be overriden

        final String message =
            StandardMessageResolutionUtils.resolveMessageForTemplate(
                    processingContext, key, messageParameters, unsafeGetDefaultMessages());
        
        if (message == null) {
            return null;
        }
        
        return new MessageResolution(message);
        
    }
    
   
    
}
