/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.messageresolver;

import org.thymeleaf.context.ITemplateContext;

/**
 * <p>
 *   Common interface for all objects used for the resolution of externalized
 *   (internationalized) messages.
 * </p>
 * <p>
 *   A Template Engine can be set several message resolvers, which will be asked for
 *   resolution of externalized messages in the order established by the {@link #getOrder()}
 *   method.
 * </p>
 * <p>
 *   Note that message resolution will return null if no message is found, in which case callers will have the
 *   possibility to choose between asking the resolver to create an <em>absent message representation</em> or not.
 *   This is precisely what the {@code useAbsentMessageRepresentation} flag does in
 *   {@link ITemplateContext#getMessage(Class, String, Object[], boolean)}.
 * </p>
 * <p>
 *   An absent message representation looks like {@code ??mymessage_gl_ES??} and is useful to quickly determine
 *   when a message is lacking from the application's configuration. Note {@code #{...}} message expressions will
 *   always ask for an {@code absent message representation}, whereas methods in the {@code #messages}
 *   expression object will do it depending on the specific method being called.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public interface IMessageResolver {


    /**
     * <p>
     *   Returns the name  of the message resolver.
     * </p>
     * 
     * @return the name of the message resolver
     */
    public String getName();


    /**
     * <p>
     *   Return the order in which this message resolver will be executed in the
     *   chain when several message resolvers are set for the same Template Engine.
     * </p>
     * 
     * @return the order of this resolver in the chain.
     */
    public Integer getOrder();


    /**
     * <p>
     *   Resolve the message, returning the requested message (or {@code null} if not found).
     * </p>
     * <p>
     *   Message resolvers should perform resolution of the {@code key} + {@code messageParameters} pair
     *   based on the {@code context} and {@code origin} specified. The context will provide
     *   information about the template and the (optional) {@code origin} about the point in template execution from
     *   which the message is being requested (usually an {@link org.thymeleaf.processor.IProcessor} or
     *   the {@link org.thymeleaf.standard.expression.MessageExpression} class).
     * </p>
     *
     * @param context the {@link ITemplateContext} object being used for template processing. Can be null.
     * @param origin the origin of the message request, usually a processor or expression class. Can be null.
     * @param key the message key.
     * @param messageParameters the (optional) message parameters.
     * @return the resolved message, or {@code null} if the message could not be resolved.
     */
    public String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters);


    /**
     * <p>
     *   Create a suitable representation of an absent message (a message that could not be resolved).
     * </p>
     * <p>
     *   Once the entire chain of configured {@link IMessageResolver} objects is asked for a specific message
     *   and all of them return {@code null}, the engine will call this method on the first resolver in the chain.
     *   If the first resolver returns {@code null} as a representation, the following resolver will be called, and
     *   so on until a resolver returns a non-null result. The empty String will be used if all resolvers return null.
     * </p>
     *
     * @param context the {@link ITemplateContext} object being used for template processing. Can be null.
     * @param origin the origin of the message request, usually a processor or expression class. Can be null.
     * @param key the message key.
     * @param messageParameters the (optional) message parameters.
     * @return the absent message representation, of {@code null} if the resolver cannot create such representation.
     */
    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters);

}
