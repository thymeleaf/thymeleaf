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
package org.thymeleaf.aurora.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.expression.IExpressionObjects;
import org.thymeleaf.aurora.expression.IExpressionObjectsFactory;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractProcessingContext implements IProcessingContext {

    private final IEngineConfiguration configuration;
    private final IVariablesMap variablesMap;
    private final boolean web;
    private final IExpressionObjects expressionObjects;



    protected AbstractProcessingContext(
            final IEngineConfiguration configuration,
            final Locale locale, final Map<String, Object> variables) {

        super();

        Validate.notNull(configuration, "Engine Configuration cannot be null");

        this.configuration = configuration;
        this.variablesMap = new VariablesMap(locale, variables);
        this.web = false;

        final IExpressionObjectsFactory expressionObjectFactory = getConfiguration().getExpressionObjectFactory();
        this.expressionObjects = expressionObjectFactory.buildExpressionObjects(this);

    }


    protected AbstractProcessingContext(
            final IEngineConfiguration configuration,
            final IContext context) {

        super();

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");

        this.configuration = configuration;
        this.variablesMap = buildVariablesMap(context);
        this.web = this.variablesMap instanceof IWebVariablesMap;

        final IExpressionObjectsFactory expressionObjectFactory = getConfiguration().getExpressionObjectFactory();
        this.expressionObjects = expressionObjectFactory.buildExpressionObjects(this);

    }




    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    public IExpressionObjects getExpressionObjects() {
        return this.expressionObjects;
    }

    public final Locale getLocale() {
        return this.variablesMap.getLocale();
    }

    public final boolean isWeb() {
        return this.web;
    }

    public final IVariablesMap getVariablesMap() {
        return this.variablesMap;
    }





    private static IVariablesMap buildVariablesMap(final IContext context) {

        final Set<String> variableNames = context.getVariableNames();
        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof IWebContext) {
                final IWebContext webContext = (IWebContext)context;
                return new WebVariablesMap(webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), webContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new VariablesMap(context.getLocale(), Collections.EMPTY_MAP);
        }

        final Map<String,Object> variables = new LinkedHashMap<String, Object>(variableNames.size() + 1, 1.0f);
        for (final String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext)context;
            return new WebVariablesMap(webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), webContext.getLocale(), variables);
        }

        return new VariablesMap(context.getLocale(), variables);

    }


}
