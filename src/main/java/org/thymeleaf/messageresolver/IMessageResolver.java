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
package org.thymeleaf.messageresolver;

import org.thymeleaf.Arguments;

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
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
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
     *   Resolve the message, returning a {@link MessageResolution} object.
     * </p>
     * <p>
     *   If the message cannot be resolved, this method should return null.
     * </p>
     * 
     * @param arguments the {@link Arguments} object being used for template processing
     * @param key the message key
     * @param messageParameters the (optional) message parameters
     * @return a {@link MessageResolution} object containing the resolved message.
     */
    public MessageResolution resolveMessage(
            final Arguments arguments, final String key, final Object[] messageParameters);
    
    
    /**
     * <p>
     *   Initialize the Message Resolver. Once initialized, none of its configuration
     *   parameters should be allowed to change.
     * </p>
     * <p>
     *   This method is called by TemplateEngine. <b>Do not use directly in your code</b>.
     * </p>
     */
    public void initialize();
    
}
