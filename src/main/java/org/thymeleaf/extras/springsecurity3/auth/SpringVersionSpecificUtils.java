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
package org.thymeleaf.extras.springsecurity3.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.extras.springsecurity3.util.SpringVersionUtils;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.1
 *
 */
final class SpringVersionSpecificUtils {


    private static final Logger LOG = LoggerFactory.getLogger(SpringVersionSpecificUtils.class);

    private static final String PACKAGE_NAME = SpringVersionSpecificUtils.class.getPackage().getName();
    private static final String SPRING3_DELEGATE_CLASS = PACKAGE_NAME + ".Spring3VersionSpecificUtility";
    private static final String SPRING4_DELEGATE_CLASS = PACKAGE_NAME + ".Spring4VersionSpecificUtility";


    private static final ISpringVersionSpecificUtility spring3Delegate;
    private static final ISpringVersionSpecificUtility spring4Delegate;



    static {

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(SpringVersionSpecificUtils.class);

        if (SpringVersionUtils.isSpring40AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 4.0+ found on classpath. Initializing auth utility for Spring 4");

            try {
                final Class<?> implClass = Class.forName(SPRING4_DELEGATE_CLASS, true, classLoader);
                spring4Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
                spring3Delegate = null;
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be at least Spring 4, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING4_DELEGATE_CLASS + "\"", e);
            }

        } else if (SpringVersionUtils.isSpring30AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 3.x found on classpath. Initializing auth utility for Spring 3");

            try {
                final Class<?> implClass = Class.forName(SPRING3_DELEGATE_CLASS, true, classLoader);
                spring3Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
                spring4Delegate = null;
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be Spring 3.x, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING3_DELEGATE_CLASS + "\"", e);
            }

        } else {

            throw new ConfigurationException(
                    "The auth infrastructure could not create utility for the specific version of Spring being" +
                    "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

        }

    }




    static EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final Map<String,Object> contextVariables) {

        if (spring4Delegate != null) {
            return spring4Delegate.wrapEvaluationContext(evaluationContext, contextVariables);
        }
        if (spring3Delegate != null) {
            return spring3Delegate.wrapEvaluationContext(evaluationContext, contextVariables);
        }

        throw new ConfigurationException(
                "The authorization infrastructure could not create initializer for the specific version of Spring being" +
                "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

    }




    static Map<String,Object> computeExpressionObjectsFromExpressionEvaluator(
            final Arguments arguments, final IStandardVariableExpressionEvaluator expressionEvaluator) {

        if (spring4Delegate != null) {
            return spring4Delegate.computeExpressionObjectsFromExpressionEvaluator(arguments, expressionEvaluator);
        }
        if (spring3Delegate != null) {
            return spring3Delegate.computeExpressionObjectsFromExpressionEvaluator(arguments, expressionEvaluator);
        }

        throw new ConfigurationException(
                "The authorization infrastructure could not create initializer for the specific version of Spring being" +
                        "used. Currently Spring 3.0, 3.1, 3.2 and 4.x are supported.");

    }




    private SpringVersionSpecificUtils() {
        super();
    }


    
}
