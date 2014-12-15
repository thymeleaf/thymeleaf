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
package org.thymeleaf.spring4.util;

import org.thymeleaf.util.ClassLoaderUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class SpringVersionUtils {



    public static boolean isSpring30AtLeast() {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionUtils.class);
        try {
            Class.forName("org.springframework.web.bind.annotation.RequestBody", false, classLoader);
            return true;
        } catch (final Exception e) {
            return false;
        }

    }


    public static boolean isSpring31AtLeast() {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionUtils.class);
        try {
            Class.forName("org.springframework.web.servlet.support.RequestDataValueProcessor", false, classLoader);
            return true;
        } catch (final Exception e) {
            return false;
        }

    }


    public static boolean isSpring32AtLeast() {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionUtils.class);
        try {
            Class.forName("org.springframework.web.context.request.async.DeferredResult", false, classLoader);
            return true;
        } catch (final Exception e) {
            return false;
        }

    }


    public static boolean isSpring40AtLeast() {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionUtils.class);
        try {
            Class.forName("org.springframework.core.io.PathResource", false, classLoader);
            return true;
        } catch (final Exception e) {
            return false;
        }

    }


    public static boolean isSpring41AtLeast() {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionUtils.class);
        try {
            Class.forName("org.springframework.cache.annotation.CacheConfig", false, classLoader);
            return true;
        } catch (final Exception e) {
            return false;
        }

    }



    private SpringVersionUtils() {
	    super();
    }

	
}
