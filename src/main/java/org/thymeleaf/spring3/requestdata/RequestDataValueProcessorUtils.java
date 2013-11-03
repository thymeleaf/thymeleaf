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
package org.thymeleaf.spring3.requestdata;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class RequestDataValueProcessorUtils {

    private static final boolean canApply;
    private static final boolean isSpring31AtLeast;

    private static final String SPRING31_DELEGATE_CLASS =
            "org.thymeleaf.spring3.requestdata.RequestDataValueProcessor31Delegate";
    private static final IRequestDataValueProcessorDelegate spring31Delegate;


    static {

        isSpring31AtLeast = SpringVersionUtils.isSpring31AtLeast();
        canApply = isSpring31AtLeast;

        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(RequestDataValueProcessorUtils.class);

        if (isSpring31AtLeast) {
            try {
                final Class<?> implClass = Class.forName(SPRING31_DELEGATE_CLASS, true, classLoader);
                spring31Delegate = (IRequestDataValueProcessorDelegate) implClass.newInstance();
            } catch (final Exception e) {
                throw new ConfigurationException(
                        "Environment has been detected to be at least Spring 3.1, but thymeleaf could not initialize a " +
                        "delegate of class \"" + SPRING31_DELEGATE_CLASS + "\"", e);
            }
        } else {
            spring31Delegate = null;
        }

    }




    public static String processAction(
            final Configuration configuration, final IProcessingContext processingContext,
            final String action, final String httpMethod) {

        final IContext context = processingContext.getContext();
        if (!canApply || !(context instanceof IWebContext)) {
            return action;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return action;
        }

        if (isSpring31AtLeast) {
            return spring31Delegate.processAction(
                    requestContext, ((IWebContext)context).getHttpServletRequest(), action, httpMethod);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processFormFieldValue(
            final Configuration configuration, final IProcessingContext processingContext,
            final String name, final String value, final String type) {

        final IContext context = processingContext.getContext();
        if (!canApply || !(context instanceof IWebContext)) {
            return value;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return value;
        }

        if (isSpring31AtLeast) {
            return spring31Delegate.processFormFieldValue(
                    requestContext, ((IWebContext)context).getHttpServletRequest(), name, value, type);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static Map<String, String> getExtraHiddenFields(
            final Configuration configuration, final IProcessingContext processingContext) {

        final IContext context = processingContext.getContext();
        if (!canApply || !(context instanceof IWebContext)) {
            return null;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return null;
        }

        if (isSpring31AtLeast) {
            return spring31Delegate.getExtraHiddenFields(
                    requestContext, ((IWebContext)context).getHttpServletRequest());
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processUrl(
            final Configuration configuration, final IProcessingContext processingContext, final String url) {

        final IContext context = processingContext.getContext();
        if (!canApply || !(context instanceof IWebContext)) {
            return url;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return url;
        }

        if (isSpring31AtLeast) {
            return spring31Delegate.processUrl(
                    requestContext, ((IWebContext)context).getHttpServletRequest(), url);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }




    private RequestDataValueProcessorUtils() {
	    super();
    }

	
}
