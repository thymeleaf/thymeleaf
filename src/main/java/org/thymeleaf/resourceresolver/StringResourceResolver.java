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
package org.thymeleaf.resourceresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.StringResource;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as the resource <tt>String</tt> itself.
 * </p>
 * <p>
 *   This allows the specification of templates/resources in the form of mere
 *   <tt>String</tt>s from the {@link org.thymeleaf.ITemplateEngine} public API,
 *   instead of needing the resolution of the template from files, classpaths
 *   or so.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StringResourceResolver
        implements IResourceResolver {

    private static final Logger logger = LoggerFactory.getLogger(StringResourceResolver.class);

    public static final String NAME = "STRING";



    public StringResourceResolver() {
        super();
    }
    
    
    public String getName() {
        return NAME; 
    }


    public IResource getResource(
            final IEngineConfiguration configuration, final IContext context,
            final String resource, final String characterEncoding) {

        Validate.notNull(resource, "Resource cannot be null");

        return new StringResource(resource, resource);

    }

}
