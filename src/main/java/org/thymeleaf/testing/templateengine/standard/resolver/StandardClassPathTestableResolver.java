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
package org.thymeleaf.testing.templateengine.standard.resolver;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;






public class StandardClassPathTestableResolver extends AbstractStandardLocalFileTestableResolver {

    
    
    public StandardClassPathTestableResolver() {
        super();
    }

    
    
    @Override
    protected File getFileFromTestableName(final String executionId, final String testableName) {
        
        Validate.notNull(testableName, "Testable name cannot be null");
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(StandardClassPathTestableResolver.class);
        final URL url = cl.getResource(testableName);
        if (url == null) {
            throw new TestEngineExecutionException(
                    "Cannot resolve testable with name: \"" + testableName + "\"");
        }
        try {
            return new File(url.toURI());
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(
                    "ClassPath folder name resulted in an unusable URL: \"" + url + "\". " +
                    "Note that this builder cannot be used for resources contained in .jars", e);
        }
        
    }

    
    
}
