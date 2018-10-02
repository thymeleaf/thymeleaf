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
package org.thymeleaf.templateresource;

import java.io.IOException;
import java.io.Reader;

/**
 * <p>
 *   Interface implemented by all <em>Template Resource</em> instances.
 * </p>
 * <p>
 *   Template resources are created and returned by Template Resolvers
 *   ({@link org.thymeleaf.templateresolver.ITemplateResolver}) and represent the real resource
 *   that contains the template contents, but do not necessarily contain these contents themselves.
 * </p>
 * <p>
 *   Most usually, template resources are used to obtain a {@link Reader} on the template contents,
 *   abstracting the real location of those contents.
 * </p>
 * <p>
 *   Note that the existence of a template resource object does not imply the existence of the resource
 *   it represents. In order to check whether a resource really exists or not, the {@link #exists()} method
 *   can be called. Some implementations of {@link org.thymeleaf.templateresolver.ITemplateResolver} might
 *   in fact do so, but note that this can result in a loss of performance in some scenarios due to a double
 *   access to a resource that might be remote (an HTTP URL, for example): one access for checking existence,
 *   and another access for creating the {@link Reader} to be returned by {@link #reader()}.
 * </p>
 * <p>
 *   Note that implementations of these interface might not be thread-safe.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see org.thymeleaf.templateresolver.ITemplateResolver
 * @see org.thymeleaf.templateresolver.TemplateResolution
 * @see ClassLoaderTemplateResource
 * @see FileTemplateResource
 * @see ServletContextTemplateResource
 * @see StringTemplateResource
 * @see UrlTemplateResource
 *
 * @since 3.0.0
 * 
 */
public interface ITemplateResource {


    /**
     * <p>
     *   Returns a {@code String} describing the resource.
     * </p>
     * <p>
     *   Note this should not be taken for a valid <em>resource name</em>, as depending on the implementation
     *   it could be too verbose/descriptive or not unique enough to be used for identification purposes.
     * </p>
     *
     * @return the resource description. Should never return {@code null}.
     */
    public String getDescription();


    /**
     * <p>
     *   Returns the <em>base name</em> of a resource.
     * </p>
     * <p>
     *   The base name is aimed at creating <em>derivative names</em> from the name of the resource, usually from
     *   the deepest level of the resource path.
     * </p>
     * <p>
     *   For example, a file resource located at {@code /home/user/template/main.html} should return
     *   {@code main} as its <em>base name</em>, so that names like {@code main.properties},
     *   {@code main.th.xml} or similar can be derived, and afterwards resolved using {@link #relative(String)}.
     * </p>
     *
     * @return the base name, or {@code null} if it cannot be computed for the specific type of resource.
     */
    public String getBaseName();


    /**
     * <p>
     *   Determines whether the resource represented by this object really exists or not.
     * </p>
     * <p>
     *   Note that, depending on the implementation, this might mean actually access the resource, and such
     *   operation could have a cost in performance in some scenarios (e.g. a resource representing a
     *   remote URL).
     * </p>
     * <p>
     *   This mechanism will be used by Template Resolvers extending from
     *   {@link org.thymeleaf.templateresolver.AbstractTemplateResolver} for checking real resource existence
     *   if the {@link org.thymeleaf.templateresolver.AbstractTemplateResolver#setCheckExistence(boolean)} flag
     *   is set to {@code true}.
     * </p>
     *
     * @return {@code true} if the resource exists, {@code false} if not.
     */
    public boolean exists();


    /**
     * <p>
     *   Returns a {@link Reader} that can be used for consuming the template contents.
     * </p>
     * <p>
     *   Most implementations of this interface will require specifying a <em>character encoding</em>
     *   during construction, so that this readers are correctly able to decode their underlying input streams.
     * </p>
     * <p>
     *   Note this readers should be closed after being fully consumed, just like any other resources.
     * </p>
     *
     * @return a {@link Reader} on the template contents. Should never return {@code null}.
     * @throws IOException if an input/output exception happens or if the resource does not exist (e.g.
     *                     {@link java.io.FileNotFoundException}).
     */
    public Reader reader() throws IOException;


    /**
     * <p>
     *   Creates another {@link ITemplateResource}, usually of the same implementation class, for a resource
     *   living in a location relative to the current object's.
     * </p>
     * <p>
     *   Note some {@link ITemplateResource} implementations might not support this feature.
     * </p>
     *
     * @param relativeLocation the location of the resource we want to obtain, relative to the current one. Required.
     * @return the relative resource. Should never return {@code null}.
     */
    public ITemplateResource relative(final String relativeLocation);

}
