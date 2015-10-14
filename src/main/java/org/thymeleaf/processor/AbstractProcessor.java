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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Base class for all processors (objects implementing the {@link IProcessor} interface).
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractProcessor implements IProcessor {

    private final IProcessorDialect dialect;
    private final int precedence;
    private final TemplateMode templateMode;



    public AbstractProcessor(final IProcessorDialect dialect, final TemplateMode templateMode, final int precedence) {

        super();

        Validate.notNull(dialect, "Dialect cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");

        this.dialect = dialect;
        this.templateMode = templateMode;
        this.precedence = precedence;

    }


    public final IProcessorDialect getDialect() {
        return this.dialect;
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
     *   (see {@link #getMessageForTemplate(ITemplateContext, String, Object[])}) and,
     *   if not found, as a <i>processor message</i> (see {@link #getMessageForProcessor(ITemplateContext, String, Object[])}.
     * </p>
     * <p>
     *   This method always returns a result: if no message is found for the specified
     *   key, a default placeholder message is returned (as a String).
     * </p>
     *
     * @param context the template context
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessage(
            final ITemplateContext context,
            final String messageKey, final Object[] messageParameters) {

        final String templateMessage =
                MessageResolutionUtils.resolveMessageForTemplate(
                        context, messageKey, messageParameters, false);

        if (templateMessage != null) {
            return templateMessage;
        }

        final String processorMessage =
                MessageResolutionUtils.resolveMessageForClass(
                        context.getConfiguration(), this.getClass(),
                        context.getLocale(), messageKey,
                        messageParameters, false);

        if (processorMessage != null) {
            return processorMessage;
        }

        return MessageResolutionUtils.getAbsentMessageRepresentation(
                messageKey, context.getLocale());

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
     * @param context the template context
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessageForTemplate(
            final ITemplateContext context,
            final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                context, messageKey, messageParameters);
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
     * @param context the processing context
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected final String getMessageForProcessor(
            final ITemplateContext context,
            final String messageKey, final Object[] messageParameters) {
        Validate.notNull(context.getLocale(), "Locale in processing context cannot be null");
        return MessageResolutionUtils.resolveMessageForClass(
                context.getConfiguration(), this.getClass(),
                context.getLocale(), messageKey, messageParameters);
    }


}
