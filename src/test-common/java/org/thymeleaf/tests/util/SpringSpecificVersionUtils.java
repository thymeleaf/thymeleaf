/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.tests.util;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.testing.templateengine.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;

public final class SpringSpecificVersionUtils {

    private static final Class<? extends IDialect> dialectClass;

    private static final String SPRING3_STANDARD_DIALECT_CLASS = "org.thymeleaf.spring3.dialect.SpringStandardDialect";
    private static final String SPRING4_STANDARD_DIALECT_CLASS = "org.thymeleaf.spring4.dialect.SpringStandardDialect";


    static {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringSpecificVersionUtils.class);

        if (SpringVersionUtils.isSpring40AtLeast()) {
            try {
                dialectClass =
                        (Class<? extends IDialect>) Class.forName(SPRING4_STANDARD_DIALECT_CLASS, true, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be at least Spring 4, but thymeleaf could not initialize a " +
                        "SpringStandardDialect class \"" + SPRING4_STANDARD_DIALECT_CLASS + "\"", e);
            }
        } else if (SpringVersionUtils.isSpring30AtLeast()) {
            try {
                dialectClass =
                        (Class<? extends IDialect>) Class.forName(SPRING3_STANDARD_DIALECT_CLASS, true, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be Spring 3.x, but thymeleaf could not initialize a " +
                        "SpringStandardDialect class \"" + SPRING3_STANDARD_DIALECT_CLASS + "\"", e);
            }
        } else {
            dialectClass = null;
            throw new ConfigurationException(
                    "The testing infrastructure could not find a valid SpringStandardDialect class for the specific " +
                    "version of Spring being used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");
        }

    }



    public static IDialect createSpringStandardDialectInstance() {
        if (dialectClass == null) {
            throw new ConfigurationException(
                    "Cannot create instance of SpringStandardDialect. The testing system was not able to determine " +
                    "that the Spring version being used is a supported one.");
        }
        try {
            return dialectClass.newInstance();
        } catch (final Exception e) {
            throw new ConfigurationException("Cannot create instance of SpringStandardDialect", e);
        }
    }



    private SpringSpecificVersionUtils() {
        super();
    }

    
}
