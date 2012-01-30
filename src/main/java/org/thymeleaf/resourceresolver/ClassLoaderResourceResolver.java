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
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as classloader resources:
 * </p>
 * <p>
 *   <tt>
 *     Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)
 *   </tt>
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ClassLoaderResourceResolver 
        implements IResourceResolver {

    public static final String NAME = "CLASSLOADER";

    
    public ClassLoaderResourceResolver() {
        super();
    }
    

    public String getName() {
        return NAME; 
    }
    
    
    public InputStream getResourceAsStream(final Arguments arguments, final String resourceName) {
        Validate.notNull(resourceName, "Resource name cannot be null");
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }
    
}
