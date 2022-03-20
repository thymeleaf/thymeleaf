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
package org.thymeleaf.spring5.requestdata;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;


/**
 * <p>
 *   Utility class used for applying Spring's {@code RequestDataValueProcessor}
 *   mechanism to URLs and forms rendered by Thymeleaf.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class RequestDataValueProcessorUtils {



    public static String processAction(
            final ITemplateContext context, final String action, final String httpMethod) {

        final IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return action;
        }

        return thymeleafRequestContext.getRequestDataValueProcessor().processAction(action, httpMethod);

    }



    public static String processFormFieldValue(
            final ITemplateContext context, final String name, final String value, final String type) {

        final IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return value;
        }

        return thymeleafRequestContext.getRequestDataValueProcessor().processFormFieldValue(name, value, type);

    }



    public static Map<String, String> getExtraHiddenFields(final ITemplateContext context) {

        final IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return null;
        }

        return thymeleafRequestContext.getRequestDataValueProcessor().getExtraHiddenFields();

    }



    public static String processUrl(final ITemplateContext context, final String url) {

        final IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return url;
        }

        return thymeleafRequestContext.getRequestDataValueProcessor().processUrl(url);

    }




    private RequestDataValueProcessorUtils() {
	    super();
    }

	
}
