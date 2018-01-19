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
package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Base abstract class implementing {@link IExpressionContext}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractExpressionContext extends AbstractContext implements IExpressionContext {

    private final IEngineConfiguration configuration;
    private IExpressionObjects expressionObjects = null;



    protected AbstractExpressionContext(final IEngineConfiguration configuration) {
        super();
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }


    protected AbstractExpressionContext(final IEngineConfiguration configuration, final Locale locale) {
        super(locale);
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }


    protected AbstractExpressionContext(
            final IEngineConfiguration configuration, final Locale locale, final Map<String, Object> variables) {
        super(locale, variables);
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }



    @Override
    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    @Override
    public IExpressionObjects getExpressionObjects() {
        // We delay creation of expression objects in case they are not needed at all
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }


}
