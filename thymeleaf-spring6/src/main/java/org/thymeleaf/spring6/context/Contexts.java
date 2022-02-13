/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.spring6.context;

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring6.web.webflux.ISpringWebFluxWebExchange;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.IServletWebExchange;

public final class Contexts {

    private Contexts() {
        super();
    }


    public static boolean isEngineContext(final IContext context) {
        return org.thymeleaf.context.Contexts.isEngineContext(context);
    }

    public static IEngineContext asEngineContext(final IContext context) {
        return org.thymeleaf.context.Contexts.asEngineContext(context);
    }

    public static boolean isWebContext(final IContext context) {
        return org.thymeleaf.context.Contexts.isWebContext(context);
    }

    public static IWebContext asWebContext(final IContext context) {
        return org.thymeleaf.context.Contexts.asWebContext(context);
    }

    public static IWebExchange getWebExchange(final IContext context) {
        return org.thymeleaf.context.Contexts.getWebExchange(context);
    }

    public static boolean isServletWebContext(final IContext context) {
        return org.thymeleaf.context.Contexts.isServletWebContext(context);
    }

    public static IServletWebExchange getServletWebExchange(final IContext context) {
        return org.thymeleaf.context.Contexts.getServletWebExchange(context);
    }

    public static boolean isSpringWebFluxWebContext(final IContext context) {
        return isWebContext(context) && (asWebContext(context).getExchange() instanceof ISpringWebFluxWebExchange);
    }

    public static ISpringWebFluxWebExchange getSpringWebFluxWebExchange(final IContext context) {
        return (ISpringWebFluxWebExchange) asWebContext(context).getExchange();
    }

}
