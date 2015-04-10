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
package org.thymeleaf.aurora.engine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.ITemplateEngineConfiguration;
import org.thymeleaf.aurora.context.IContext;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.context.IWebContext;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardTemplateProcessingContextFactory {


    public static ITemplateProcessingContext build(
            final ITemplateEngineConfiguration configuration,
            final String templateName, final TemplateMode templateMode,
            final IContext context) {
        Validate.notNull(context, "Context cannot be null");
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext) context;
            return new WebTemplateProcessingContext(
                    configuration, templateName, templateMode, webContext.getLocale(), webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), buildVariablesMap(webContext));
        }
        return new TemplateProcessingContext(configuration, templateName, templateMode, context.getLocale(), buildVariablesMap(context));
    }



    private static Map<String,Object> buildVariablesMap(final IContext context) {
        final Map<String,Object> variablesMap = new LinkedHashMap<String, Object>(10);
        final Set<String> variableNames = context.getVariableNames();
        for (final String variableName : variableNames) {
            variablesMap.put(variableName, context.getVariable(variableName));
        }
        return variablesMap;
    }


    private StandardTemplateProcessingContextFactory() {
        super();
    }

}
