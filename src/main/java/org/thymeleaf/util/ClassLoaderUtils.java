/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;


/**
 * <p>
 *   Utility class for obtaining a correct classloader on which to operate from a
 *   specific class.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.6
 *
 */
public final class ClassLoaderUtils {
    
    
    
    /**
     * <p>
     *   Try to obtain a classloader, following these priorities:
     * </p>
     * <ol>
     *   <li>If there is a <i>thread context class loader</i>, return it.</li>
     *   <li>Else if there is a class loader related to the class passed as argument, return it.</li>
     *   <li>Else return the <i>system class loader</i>.</li>
     * </ol>
     * 
     * @param clazz the class which loader will be obtained in the second step. Can be null (that will
     *              skip that second step).
     * @return a non-null, safe classloader to use.
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        // Context class loader can be null
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        if (clazz != null) {
            // The class loader for a specific class can also be null
            final ClassLoader clazzClassLoader = clazz.getClassLoader();
            if (clazzClassLoader != null) {
                return clazzClassLoader;
            }
        }
        // The only class loader we can rely on for not being null is the system one
        return ClassLoader.getSystemClassLoader();
    }
    
    
    
    private ClassLoaderUtils() {
        super();
    }
    
    
    
}
