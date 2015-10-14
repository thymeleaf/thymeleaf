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
package org.thymeleaf.context;

import java.util.Locale;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Utility abstract class partially implementing {@link ITemplateContext}.
 * </p>
 * <p>
 *   This class only takes care of the methods in {@link ITemplateContext} and not those in {@link IEngineContext},
 *   so that it allows engine context implementations to not have to duplicate a lot of code simpler than their
 *   own for management of inliners, local variables, etc.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractEngineContext implements ITemplateContext {

    // NOTE we are not extending AbstractContext or AbstractExpressionContext on purpose, as the variable-oriented
    // methods are going to be handled by the subclasses, not any superclasses.

    private final IEngineConfiguration configuration;
    private final Locale locale;

    private IExpressionObjects expressionObjects = null;
    private IdentifierSequences identifierSequences = null;



    protected AbstractEngineContext(
            final IEngineConfiguration configuration,
            final Locale locale) {

        super();

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(locale, "Locale cannot be null");

        this.configuration = configuration;
        this.locale = locale;
        // Most templates will not need this, so we will initialize it lazily
        this.identifierSequences = null;

    }

    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    public final Locale getLocale() {
        return this.locale;
    }

    public final IExpressionObjects getExpressionObjects() {
        // We delay creation of expression objects in case they are not needed at all
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }

    public final TemplateMode getTemplateMode() {
        return getTemplateResolution().getTemplateMode();
    }

    public final IdentifierSequences getIdentifierSequences() {
        // No problem in lazily initializing this here, as context objects should not be used by
        // multiple threads.
        if (this.identifierSequences == null) {
            this.identifierSequences = new IdentifierSequences();
        }
        return this.identifierSequences;
    }



}
