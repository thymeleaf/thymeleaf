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

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;
import org.thymeleaf.util.ClassLoaderUtils;

/**
 * <p>
 *   Class meant to perform actions that might depend on a specific version of the thymeleaf-springX integration
 *   packages.
 * </p>
 * <p>
 *   The SpringSecurity integration modules depend on specific versions of the thymeleaf-springX packages when they
 *   are meant to work in Spring WebFlux applications, and this module tries to centralize all dependencies on those
 *   modules so that expanding support for new versions is easier.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class ThymeleafSpringUtils {

    private static final boolean thymeleafspring3Present;
    private static final boolean thymeleafspring4Present;
    private static final boolean thymeleafspring5Present;



    static {

        boolean present3 = false;
        try {
            ClassLoaderUtils.isClassPresent("org.thymeleaf.spring3.dialect.SpringStandardDialect");
            present3 = true;
        } catch (final Throwable t) {
            // safely ignored
        }
        thymeleafspring3Present = present3;

        boolean present4 = false;
        try {
            ClassLoaderUtils.isClassPresent("org.thymeleaf.spring4.dialect.SpringStandardDialect");
            present4 = true;
        } catch (final Throwable t) {
            // safely ignored
        }
        thymeleafspring4Present = present4;

        boolean present5 = false;
        try {
            ClassLoaderUtils.isClassPresent("org.thymeleaf.spring5.dialect.SpringStandardDialect");
            present5 = true;
        } catch (final Throwable t) {
            // safely ignored
        }
        thymeleafspring5Present = present5;

    }




    public static boolean isWebMvcContext(final IContext context) {
        // If this is a IWebContext, no need to depend on any version-specific structures.
        return context instanceof IWebContext;
    }


    public static boolean isWebFluxContext(final IContext context) {
        if (thymeleafspring5Present) {
            return context instanceof ISpringWebFluxContext;
        }
        if (thymeleafspring3Present || thymeleafspring4Present) {
            return false;
        }
        throw new ConfigurationException(
                "This Spring Security integration module requires thymeleaf-spring5 in order to work with WebFlux");
    }




    public static Object getWebFluxExchange(final IContext context) {
        if (thymeleafspring5Present) {
            if (context instanceof ISpringWebFluxContext) {
                return ((ISpringWebFluxContext)context).getExchange();
            }
        }
        throw new ConfigurationException(
                "This Spring Security integration module requires thymeleaf-spring5 in order to work with WebFlux");
    }




    private ThymeleafSpringUtils() {
        super();
    }


}
