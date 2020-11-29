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
package org.thymeleaf.spring5.util;

import org.springframework.core.SpringVersion;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.VersionUtils;

/**
 * <p>
 *   Utility class useful for determining the version of Spring that is on the classpath.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringVersionUtils {

    private static final VersionUtils.VersionSpec SPRING_VERSION_SPEC;

    private static final boolean SPRING_WEB_MVC_PRESENT;
    private static final boolean SPRING_WEB_REACTIVE_PRESENT;



    static {

        String springVersion = SpringVersion.getVersion();

        // We will first compute the package name root for the spring framework in order to improve resilience
        // against dependency renaming operations.
        final String corePackageName = SpringVersion.class.getPackage().getName();
        final String springPackageName = corePackageName.substring(0, corePackageName.length() - 5); // - ".core"

        // There might be times when SpringVersion cannot determine the version due to CL restrictions (see doc)
        if (springVersion != null) {

            SPRING_VERSION_SPEC = VersionUtils.parseVersion(springVersion);

            if (SPRING_VERSION_SPEC.isUnknown()) {
                throw new ExceptionInInitializerError(
                        "Exception during initialization of Spring versioning utilities. Identified Spring " +
                        "version is '" + springVersion + "', which does not follow the " +
                        "{major}.{minor}.{patch}[.{...}] scheme");
            }

        } else {

            if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.io.buffer.DataBuffer")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("5.0.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".context.annotation.ComponentScans")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("4.3.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.annotation.AliasFor")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("4.2.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".cache.annotation.CacheConfig")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("4.1.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.io.PathResource")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("4.0.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.context.request.async.DeferredResult")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("3.2.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.servlet.support.RequestDataValueProcessor")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("3.1.0.RELEASE");
            } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.bind.annotation.RequestBody")) {
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("3.0.0.RELEASE");
            } else {
                // We will default to 2.5
                SPRING_VERSION_SPEC = VersionUtils.parseVersion("2.5.0.RELEASE");
            }


        }

        SPRING_WEB_MVC_PRESENT = ClassLoaderUtils.isClassPresent(springPackageName + ".web.servlet.View");
        SPRING_WEB_REACTIVE_PRESENT =
                SPRING_VERSION_SPEC.isAtLeast(5) && ClassLoaderUtils.isClassPresent(springPackageName + ".web.reactive.result.view.View");

    }



    public static String getSpringVersion() {
        return SPRING_VERSION_SPEC.getVersion();
    }

    public static int getSpringVersionMajor() {
        return SPRING_VERSION_SPEC.getMajor();
    }

    public static int getSpringVersionMinor() {
        return SPRING_VERSION_SPEC.getMinor();
    }

    public static int getSpringVersionPatch() {
        return SPRING_VERSION_SPEC.getPatch();
    }



    public static boolean isSpring30AtLeast() {
        return isSpringAtLeast(3,0);
    }


    public static boolean isSpring31AtLeast() {
        return isSpringAtLeast(3,1);
    }


    public static boolean isSpring32AtLeast() {
        return isSpringAtLeast(3,2);
    }


    public static boolean isSpring40AtLeast() {
        return isSpringAtLeast(4,0);
    }


    public static boolean isSpring41AtLeast() {
        return isSpringAtLeast(4,1);
    }


    public static boolean isSpring42AtLeast() {
        return isSpringAtLeast(4,2);
    }


    public static boolean isSpring43AtLeast() {
        return isSpringAtLeast(4,3);
    }


    public static boolean isSpring50AtLeast() {
        return isSpringAtLeast(5, 0);
    }


    public static boolean isSpring51AtLeast() {
        return isSpringAtLeast(5,1);
    }


    public static boolean isSpring52AtLeast() {
        return isSpringAtLeast(5,2);
    }


    public static boolean isSpring53AtLeast() {
        return isSpringAtLeast(5,3);
    }


    public static boolean isSpringAtLeast(final int major, final int minor) {
        return isSpringAtLeast(major, minor, 0);
    }


    public static boolean isSpringAtLeast(final int major, final int minor, final int patch) {
        return SPRING_VERSION_SPEC.isAtLeast(major, minor, patch);
    }


    public static boolean isSpringWebMvcPresent() {
        return SPRING_WEB_MVC_PRESENT;
    }


    public static boolean isSpringWebFluxPresent() {
        return SPRING_WEB_REACTIVE_PRESENT;
    }



    private SpringVersionUtils() {
        super();
    }


}
