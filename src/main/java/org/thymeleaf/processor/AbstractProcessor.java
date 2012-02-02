/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractProcessor implements IProcessor {

    
    public AbstractProcessor() {
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
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected String getMessage(
            final Arguments arguments, final String messageKey, final Object[] messageParameters) {

        final String templateMessage =
            MessageResolutionUtils.resolveMessageForTemplate(
                    arguments, messageKey, messageParameters, false);
        
        if (templateMessage != null) {
            return templateMessage;
        }
        
        final String processorMessage =
            MessageResolutionUtils.resolveMessageForClass(
                    arguments.getConfiguration(), this.getClass(), 
                    arguments.getContext().getLocale(), messageKey, 
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
     * @param messageKey the message key
     * @param messageParameters the (optional) message parameters
     * @return the resolved message
     */
    protected String getMessageForTemplate(
            final Arguments arguments, final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                arguments, messageKey, messageParameters);
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
                arguments.getConfiguration(), this.getClass(), 
                arguments.getContext().getLocale(), messageKey, messageParameters);
    }
    
    
    
    public abstract int getPrecedence();



    /**
     * <p>
     *   Compare (and therefore order) processors according to their precedence.
     * </p>
     * <p>
     *   Be careful: This implementation of compareTo breaks
     *   <tt>(o1.compareTo(o2) == 0) == (o1.equals(o2))</tt>, as two different processors
     *   can have the same precedence.
     * </p>
     * 
     * @param o the object to compare to
     * @return the comparison result
     */
    public int compareTo(final IProcessor o) {
        if (o == null) {
            return 1;
        }
        if (!(o instanceof AbstractProcessor)) {
            // The other object does not rely on precedence, so we should delegate to
            // the other object (and its comparison policy) and invert the result.
            final int result = o.compareTo(this);
            return (-1) * result;
        }
        final int thisPrecedence = getPrecedence();
        final int otherPrecedence = ((AbstractProcessor)o).getPrecedence();
        if (thisPrecedence > otherPrecedence) {
            return 1;
        } 
        if (thisPrecedence < otherPrecedence) {
            return -1;
        }
        return 0;
    }




    public final ProcessorResult process(
            final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node) {
        
        try {
            
            return doProcess(arguments, processorMatchingContext, node);
                
        } catch (final TemplateProcessingException e) {
            if (!e.hasLineNumber()) {
                e.setLineNumber(node.getLineNumber());
            }
            throw e;
        } catch (final Exception e) {
            final TemplateProcessingException exception =
                    new TemplateProcessingException("Error during execution of processor '" + this.getClass().getName() + "'", e);
            exception.setLineNumber(node.getLineNumber());
            throw exception;
        }
        
    }
    
    
    protected abstract ProcessorResult doProcess(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node);
    
    
    
    
}
