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
package org.thymeleaf.processor.tag;

import org.thymeleaf.Arguments;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Base convenience implementation of {@link ITagProcessor} returning empty
 *   sets for all of the interface's methods and allowing easy access to internationalization
 *   features by providing methods for obtaining both <i>template messages</i> and 
 *   <i>processor messages</i>.
 * </p>
 * <p>
 *   This class provides a more convenient extension point for tag processors,
 *   allowing the developer only to override the methods that are really needed.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractTagProcessor 
        implements ITagProcessor {

    
    public AbstractTagProcessor() {
        super();
    }
    

    

    
    /**
     * <p>
     *   Resolves a message, trying to resolve it first as a <i>template message</i>
     *   (see {@link #getMessageForTemplate(Arguments, TemplateResolution, String, Object[])}) and,
     *   if not found, as a <i>processor message</i> (see {@link #getMessageForProcessor(Arguments, String, Object[])}.
     * </p>
     * <p>
     *   This method always returns a result: if no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     * 
     * @param arguments the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param templateResolution the result of resolving the template by the Template Resolvers.
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected String getMessage(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final String messageKey, final Object[] messageParameters) {

        final String templateMessage =
            MessageResolutionUtils.resolveMessageForTemplate(
                    arguments, templateResolution, messageKey, messageParameters, false);
        
        if (templateMessage != null) {
            return templateMessage;
        }
        
        final String processorMessage =
            MessageResolutionUtils.resolveMessageForClass(
                    this.getClass(), arguments.getContext().getLocale(), messageKey, 
                    messageParameters, false);
        
        if (processorMessage != null) {
            return processorMessage;
        }
        
        return MessageResolutionUtils.getAbsentMessageRepresentation(
                messageKey, arguments.getContext().getLocale());
        
    }
    
    
    
    /**
     * <p>
     *   Resolves the specified message as a <i>template message</i>.
     * </p>
     * <p>
     *   <i>Template messages</i> are resolved by the <i>Message Resolver</i>
     *   ({@link org.thymeleaf.messageresolver.IMessageResolver}) instances
     *   configured at the Template Engine (executed in chain) in exactly the same way as,
     *   for example, a <tt>#{...}</tt> expression would when using the <i>Standard
     *   Dialect</i> or the <i>SpringStandard Dialect</i>.
     * </p>
     * <p>
     *   This method always returns a result: if no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     * 
     * @param arguments the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param templateResolution the result of resolving the template by the Template Resolvers.
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected String getMessageForTemplate(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                arguments, templateResolution, messageKey, messageParameters);
    }
    
    
    
    /**
     * <p>
     *   Resolves the specified message as a <i>processor message</i>.
     * </p>
     * <p>
     *   <i>Processor messages</i> appear on <tt>.properties</tt> files that usually
     *   live in the same package (i.e. source folder) as the processor class itself, 
     *   and have the same base name (for example, for a <tt>com.something.MyProc</tt> processor
     *   we can have <tt>com/something/MyProc_en.properties</tt>,
     *   <tt>com/something/MyProc_es.properties</tt>, <tt>com/something/MyProc.properties</tt>
     *   (for defaults), etc.). This allows the encapsulation and packing of processors
     *   along with all of its required internationalization resources.
     * </p>
     * <p>
     *   If no message is found for the specified key in a <tt>.properties</tt> file with
     *   the same base name as the tag processor, then its superclasses are
     *   also examined.
     * </p>
     * <p>
     *   This method always returns a result: if at the end no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     * 
     * 
     * @param arguments the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected String getMessageForProcessor(
            final Arguments arguments, final String messageKey, final Object[] messageParameters) {
        Validate.notNull(arguments.getContext().getLocale(), "Locale in context cannot be null");
        return MessageResolutionUtils.resolveMessageForClass(
                this.getClass(), arguments.getContext().getLocale(), messageKey, messageParameters);
    }

    
}
