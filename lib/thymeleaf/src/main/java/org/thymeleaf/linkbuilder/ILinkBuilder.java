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
package org.thymeleaf.linkbuilder;

import java.util.Map;

import org.thymeleaf.context.IExpressionContext;

/**
 * <p>
 *   Common interface for all objects used for the building of links (URLs). This extension point provides a way
 *   to integrate Thymeleaf with different web execution environments, be these based on the Servlet API or not.
 * </p>
 * <p>
 *   A Template Engine can be set several link builders, which will be asked for
 *   link building in the order established by the {@link #getOrder()} method.
 * </p>
 * <p>
 *   Note that a link builder will return null if a link cannot be built (because of it not falling under its
 *   responsibilities for whatever reason). In such case, the next builder will be asked. If the entire chain of
 *   link builders fail, an exception will be raised.
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
public interface ILinkBuilder {


    /**
     * <p>
     *   Returns the name of the link builder.
     * </p>
     * 
     * @return the name of the link builder
     */
    public String getName();


    /**
     * <p>
     *   Return the order in which this link builder will be executed in the
     *   chain when several link builders are set for the same Template Engine.
     * </p>
     * 
     * @return the order of this builder in the chain.
     */
    public Integer getOrder();


    /**
     * <p>
     *   Build a link, returning {@code null} if not possible.
     * </p>
     *
     * @param context the {@link IExpressionContext} object being used for template processing. Cannot be null.
     * @param base the base of the link URL to be built, i.e. its path. Can be null.
     * @param parameters the (optional) URL parameters.
     * @return the built URL.
     */
    public String buildLink(
            final IExpressionContext context, final String base, final Map<String, Object> parameters);

}
