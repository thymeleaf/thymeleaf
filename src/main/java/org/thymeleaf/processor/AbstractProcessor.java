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
package org.thymeleaf.processor;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.message.resolver.IMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0 (reimplemented in 3.0.0)
 *
 */
public abstract class AbstractProcessor implements IProcessor {

    private final int precedence;
    private final TemplateMode templateMode;



    public AbstractProcessor(final TemplateMode templateMode, final int precedence) {

        super();

        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateMode = templateMode;
        this.precedence = precedence;

    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public final int getPrecedence() {
        return this.precedence;
    }



    /**
     * <p>
     *   Resolves a message, trying to resolve it first as a <i>template message</i>
     *   (see {@link #getMessageForTemplate(ITemplateProcessingContext, String, Object[])}) and,
     *   if not found, as a <i>processor message</i> (see {@link #getMessageForProcessor(ITemplateProcessingContext, String, Object[])}.
     * </p>
     * <p>
     *   This method always returns a result: if no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     *
     * @param processingContext the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessage(
            final ITemplateProcessingContext processingContext, final String messageKey, final Object[] messageParameters) {

        final String templateMessage =
                MessageResolutionUtils.resolveMessageForTemplate(
                        processingContext, messageKey, messageParameters, false);

        if (templateMessage != null) {
            return templateMessage;
        }

        return MessageResolutionUtils.resolveMessageForClass(
                processingContext.getConfiguration(), this.getClass(),
                processingContext.getLocale(), messageKey,
                messageParameters, true);
    }



    /**
     * <p>
     *   Resolves the specified message as a <i>template message</i>.
     * </p>
     * <p>
     *   <i>Template messages</i> are resolved by the <i>Message Resolver</i>
     *   ({@link IMessageResolver}) instances
     *   configured at the Template Engine (executed in chain) in exactly the same way as,
     *   for example, a <tt>#{...}</tt> expression would when using the <i>Standard
     *   Dialect</i> or the <i>SpringStandard Dialect</i>.
     * </p>
     * <p>
     *   This method always returns a result: if no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     *
     * @param processingContext the processing context
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessageForTemplate(
            final ITemplateProcessingContext processingContext, final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                processingContext, messageKey, messageParameters);
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
     *   the same base name as the attribute processor, then its superclasses are
     *   also examined.
     * </p>
     * <p>
     *   This method always returns a result: if at the end no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     *
     *
     * @param processingContext the processing context
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessageForProcessor(
            final ITemplateProcessingContext processingContext, final String messageKey, final Object[] messageParameters) {
        Validate.notNull(processingContext.getLocale(), "Locale in processing context cannot be null");
        return MessageResolutionUtils.resolveMessageForClass(
                processingContext.getConfiguration(), this.getClass(),
                processingContext.getLocale(), messageKey, messageParameters);
    }


}
