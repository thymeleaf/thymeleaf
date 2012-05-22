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
package org.thymeleaf.resourceresolver;

import java.io.InputStream;

import org.thymeleaf.Arguments;

/**
 * <p>
 *   Base interface for all objects used for the resolution of template resources
 *   (files, URLs, etc).
 * </p>
 * <p>
 *   When a template is resolved by a Template Resolver (implementation of {@link org.thymeleaf.templateresolver.ITemplateResolver}),
 *   the resulting {@link org.thymeleaf.templateresolver.TemplateResolution} object includes
 *   both a Resource Resolver and a <i>resource name</i>. The Template Engine will then use the
 *   resource resolver to try to resolve the template resource (e.g. read a file), and if the resource 
 *   cannot be resolved then the next Template Resolver in the chain will be asked to resolve it.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface IResourceResolver {


    /**
     * <p>
     *   The name of the resource resolver.
     * </p>
     * 
     * @return the name of the resource resolver. 
     */
    public String getName();

    /**
     * <p>
     *   Resolve the resource, this is, open an input stream for it.
     * </p>
     * <p>
     *   If the resource cannot be resolved, this method should return null.
     * </p>
     * 
     * @param arguments the {@link Arguments} object being used for template processing
     * @param resourceName the resource name to be resolved/read
     * @return an InputStream on the resource
     */
    public InputStream getResourceAsStream(final Arguments arguments, final String resourceName);
    
}
