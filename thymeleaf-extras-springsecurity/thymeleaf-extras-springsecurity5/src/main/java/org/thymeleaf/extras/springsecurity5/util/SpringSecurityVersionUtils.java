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
package org.thymeleaf.extras.springsecurity5.util;

import java.lang.reflect.Method;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.thymeleaf.util.ClassLoaderUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.1
 *
 */
public final class SpringSecurityVersionUtils {


    private static final int SPRING_SECURITY_VERSION_MAJOR;
    private static final int SPRING_SECURITY_VERSION_MINOR;



    static {

        String springSecurityVersion = SpringSecurityCoreVersion.getVersion();

        // We will first compute the package name root for the spring framework in order to improve resilience
        // against dependency renaming operations.
        final String securityCorePackageName = SpringSecurityCoreVersion.class.getPackage().getName();
        final String springSecurityPackageName =
                securityCorePackageName.substring(0, securityCorePackageName.length() - 5); // - ".core"

        // There might be times when SpringVersion cannot determine the version due to CL restrictions (see doc)
        if (springSecurityVersion != null) {

            try {

                String versionRemainder = springSecurityVersion;

                int separatorIdx = versionRemainder.indexOf('.');
                SPRING_SECURITY_VERSION_MAJOR = Integer.parseInt(versionRemainder.substring(0, separatorIdx));

                int separator2Idx = versionRemainder.indexOf('.', separatorIdx + 1);
                SPRING_SECURITY_VERSION_MINOR = Integer.parseInt(versionRemainder.substring(separatorIdx + 1, separator2Idx));

            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        "Exception during initialization of Spring Security versioning utilities. Identified Spring Security " +
                        "version is '" + springSecurityVersion + "', which does not follow the {major}.{minor}.{...} scheme");
            }

        } else {

            if (testClassExistence(springSecurityPackageName + ".web.server.context.SecurityContextServerWebExchange")) {
                SPRING_SECURITY_VERSION_MAJOR = 5;
                SPRING_SECURITY_VERSION_MINOR = 0;
            } else if (testClassExistence(springSecurityPackageName + ".jackson2.SecurityJackson2Modules")) {
                SPRING_SECURITY_VERSION_MAJOR = 4;
                SPRING_SECURITY_VERSION_MINOR = 2;
            } else if (testClassExistence(springSecurityPackageName + ".core.annotation.AuthenticationPrincipal")) {
                // There are no new classes at the core of Spring Security 4.1, so we will check for a new
                // "expression()" attribute method in the AuthenticationPrincipal annotation, which was added in 4.0

                final Class<?> authenticationPrincipalClass =
                        getClass(springSecurityPackageName + ".core.annotation.AuthenticationPrincipal");
                final Method[] methods = authenticationPrincipalClass.getDeclaredMethods();
                boolean hasExpressionAttribute = false;
                for (int i = 0; i < methods.length; i++) {
                    if ("expression".equals(methods[i].getName())) {
                        hasExpressionAttribute = true;
                    }
                }

                if (hasExpressionAttribute) {
                    SPRING_SECURITY_VERSION_MAJOR = 4;
                    SPRING_SECURITY_VERSION_MINOR = 1;
                } else {
                    SPRING_SECURITY_VERSION_MAJOR = 4;
                    SPRING_SECURITY_VERSION_MINOR = 0;
                }

            } else if (testClassExistence(springSecurityPackageName + ".access.method.P")) {
                SPRING_SECURITY_VERSION_MAJOR = 3;
                SPRING_SECURITY_VERSION_MINOR = 2;
            } else if (testClassExistence(springSecurityPackageName + ".provisioning.MutableUserDetails")) {
                SPRING_SECURITY_VERSION_MAJOR = 3;
                SPRING_SECURITY_VERSION_MINOR = 1;
            } else if (testClassExistence(springSecurityPackageName + ".access.expression.method.AbstractExpressionBasedMethodConfigAttribute")) {
                SPRING_SECURITY_VERSION_MAJOR = 3;
                SPRING_SECURITY_VERSION_MINOR = 0;
            } else {
                // We will default to 2.0
                SPRING_SECURITY_VERSION_MAJOR = 2;
                SPRING_SECURITY_VERSION_MINOR = 0;
            }


        }

    }


    private static boolean testClassExistence(final String className) {
        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringSecurityVersionUtils.class);
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (final Throwable t) {
            return false;
        }
    }


    private static Class<?> getClass(final String className) {
        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringSecurityVersionUtils.class);
        try {
            return Class.forName(className, false, classLoader);
        } catch (final Throwable t) {
            return null;
        }
    }




    public static int getSpringSecurityVersionMajor() {
        return SPRING_SECURITY_VERSION_MAJOR;
    }

    public static int getSpringSecurityVersionMinor() {
        return SPRING_SECURITY_VERSION_MINOR;
    }



    public static boolean isSpringSecurity30AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR >= 3;
    }


    public static boolean isSpringSecurity31AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR > 3 || (SPRING_SECURITY_VERSION_MAJOR == 3 && SPRING_SECURITY_VERSION_MINOR >= 1);
    }


    public static boolean isSpringSecurity32AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR > 3 || (SPRING_SECURITY_VERSION_MAJOR == 3 && SPRING_SECURITY_VERSION_MINOR >= 2);
    }


    public static boolean isSpringSecurity40AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR >= 4;
    }


    public static boolean isSpringSecurity41AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR > 4 || (SPRING_SECURITY_VERSION_MAJOR == 4 && SPRING_SECURITY_VERSION_MINOR >= 1);
    }


    public static boolean isSpringSecurity42AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR > 4 || (SPRING_SECURITY_VERSION_MAJOR == 4 && SPRING_SECURITY_VERSION_MINOR >= 2);
    }


    public static boolean isSpringSecurity50AtLeast() {
        return SPRING_SECURITY_VERSION_MAJOR >= 5;
    }



    private SpringSecurityVersionUtils() {
        super();
    }


}
