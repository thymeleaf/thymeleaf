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
package org.thymeleaf.testing.templateengine.context.web;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.testing.templateengine.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


final class SpringVersionSpecificContextInitialization {

    private static Logger LOG = LoggerFactory.getLogger(SpringVersionSpecificContextInitialization.class);

    private static final String PACKAGE_NAME = SpringVersionSpecificContextInitialization.class.getPackage().getName();
    private static final String SPRING3_DELEGATE_CLASS = PACKAGE_NAME + ".Spring3VersionSpecificContextInitializer";
    private static final String SPRING4_DELEGATE_CLASS = PACKAGE_NAME + ".Spring4VersionSpecificContextInitializer";

    private static final ISpringVersionSpecificContextInitializer spring3Delegate;
    private static final ISpringVersionSpecificContextInitializer spring4Delegate;




    static {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionSpecificContextInitialization.class);

        if (SpringVersionUtils.isSpring40AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 4.0+ found on classpath. Initializing testing system for using Spring 4 in tests");

            try {
                final Class<?> implClass = Class.forName(SPRING4_DELEGATE_CLASS, true, classLoader);
                spring4Delegate = (ISpringVersionSpecificContextInitializer) implClass.newInstance();
                spring3Delegate = null;
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be at least Spring 4, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING4_DELEGATE_CLASS + "\"", e);
            }

        } else if (SpringVersionUtils.isSpring30AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 3.x found on classpath. Initializing testing system for using Spring 3 in tests");

            try {
                final Class<?> implClass = Class.forName(SPRING3_DELEGATE_CLASS, true, classLoader);
                spring3Delegate = (ISpringVersionSpecificContextInitializer) implClass.newInstance();
                spring4Delegate = null;
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be Spring 3.x, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING3_DELEGATE_CLASS + "\"", e);
            }

        } else {

            throw new ConfigurationException(
                    "The testing infrastructure could not create initializer for the specific version of Spring being" +
                            "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

        }

    }


    static void versionSpecificAdditionalVariableProcessing(
            final ApplicationContext applicationContext, final ConversionService conversionService,
            final Map<String,Object> variables) {

        if (spring4Delegate != null) {
            spring4Delegate.versionSpecificAdditionalVariableProcessing(applicationContext, conversionService, variables);
            return;
        }

        if (spring3Delegate != null) {
            spring3Delegate.versionSpecificAdditionalVariableProcessing(applicationContext, conversionService, variables);
            return;
        }

        throw new ConfigurationException(
                "The testing infrastructure could not create initializer for the specific version of Spring being" +
                "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

    }



    static IWebContext versionSpecificCreateContextInstance(
            final ApplicationContext applicationContext, final HttpServletRequest request,
            final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        if (spring4Delegate != null) {
            return spring4Delegate.versionSpecificCreateContextInstance(
                    applicationContext, request, response, servletContext, locale, variables);
        }

        if (spring3Delegate != null) {
            return spring3Delegate.versionSpecificCreateContextInstance(
                    applicationContext, request, response, servletContext, locale, variables);
        }

        throw new ConfigurationException(
                "The testing infrastructure could not create initializer for the specific version of Spring being" +
                "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

    }




    private SpringVersionSpecificContextInitialization() {
        super();
    }



    
}
