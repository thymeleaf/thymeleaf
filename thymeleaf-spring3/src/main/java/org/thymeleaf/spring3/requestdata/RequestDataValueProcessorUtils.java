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
package org.thymeleaf.spring3.requestdata;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * <p>
 *   Utility class used for applying the <tt>org.springframework.web.servlet.support.RequestDataValueProcessor</tt>
 *   interface to URLs and forms output by Thymeleaf.
 * </p>
 * <p>
 *   Given this Spring interface only exists since Spring 3.1, application is conditional and only performed
 *   if Spring version in classpath is 3.1 or newer.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class RequestDataValueProcessorUtils {

    private static final boolean canApply;
    private static final boolean isSpring31AtLeast;
    private static final boolean isSpring40AtLeast;

    private static final String SPRING31_DELEGATE_CLASS =
            "org.thymeleaf.spring3.requestdata.RequestDataValueProcessor31Delegate";
    private static final IRequestDataValueProcessorDelegate spring31Delegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDataValueProcessorUtils.class);


    static {

        isSpring31AtLeast = SpringVersionUtils.isSpring31AtLeast();
        isSpring40AtLeast = SpringVersionUtils.isSpring40AtLeast();
        canApply = isSpring31AtLeast && !isSpring40AtLeast;

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(RequestDataValueProcessorUtils.class);

        if (isSpring31AtLeast && !isSpring40AtLeast) {
            try {
                final Class<?> implClass = Class.forName(SPRING31_DELEGATE_CLASS, true, classLoader);
                spring31Delegate = (IRequestDataValueProcessorDelegate) implClass.newInstance();
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be at least Spring 3.1, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING31_DELEGATE_CLASS + "\"", e);
            }
        } else {
            if (isSpring40AtLeast) {
                LOGGER.warn(
                        "[THYMELEAF] You seem to be using the thymeleaf-spring3 module with Spring version 4.x. " +
                        "Even though most features should work OK, support for CSRF protection on websites will be " +
                        "disabled due to incompatibilities between the different versions of the " +
                        "RequestDataValueProcessor interface in versions 3.x and 4.x.");
            }
            spring31Delegate = null;
        }

    }




    public static String processAction(
            final IProcessingContext processingContext, final String action, final String httpMethod) {

        if (!canApply || !processingContext.isWeb()) {
            return action;
        }

        final IWebVariablesMap variablesMap = (IWebVariablesMap) processingContext.getVariablesMap();

        final RequestContext requestContext =
                (RequestContext) variablesMap.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return action;
        }

        if (spring31Delegate != null) {
            return spring31Delegate.processAction(requestContext, variablesMap.getRequest(), action, httpMethod);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processFormFieldValue(
            final IProcessingContext processingContext, final String name, final String value, final String type) {

        if (!canApply || !processingContext.isWeb()) {
            return value;
        }

        final IWebVariablesMap variablesMap = (IWebVariablesMap) processingContext.getVariablesMap();

        final RequestContext requestContext =
                (RequestContext) variablesMap.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return value;
        }

        if (spring31Delegate != null) {
            return spring31Delegate.processFormFieldValue(requestContext, variablesMap.getRequest(), name, value, type);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static Map<String, String> getExtraHiddenFields(final IProcessingContext processingContext) {

        if (!canApply || !processingContext.isWeb()) {
            return null;
        }

        final IWebVariablesMap variablesMap = (IWebVariablesMap) processingContext.getVariablesMap();

        final RequestContext requestContext =
                (RequestContext) variablesMap.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return null;
        }

        if (spring31Delegate != null) {
            return spring31Delegate.getExtraHiddenFields(requestContext, variablesMap.getRequest());
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processUrl(final IProcessingContext processingContext, final String url) {

        if (!canApply || !processingContext.isWeb()) {
            return url;
        }

        final IWebVariablesMap variablesMap = (IWebVariablesMap) processingContext.getVariablesMap();

        final RequestContext requestContext =
                (RequestContext) variablesMap.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return url;
        }

        if (spring31Delegate != null) {
            return spring31Delegate.processUrl(requestContext, variablesMap.getRequest(), url);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }




    private RequestDataValueProcessorUtils() {
	    super();
    }

	
}
