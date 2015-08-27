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
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.IMarkupFactory;
import org.thymeleaf.engine.StandardMarkupFactory;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;

/**
 *
 * <p>
 *   Note these implementations do not have to be thread-safe, and in fact should not be shared by different threads
 *   or template executions. They are meant to be local to a specific template engine execution.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractTemplateProcessingContext
            extends AbstractProcessingContext
            implements ITemplateProcessingContext {

    private final ITemplateProcessingContext parent;
    private final TemplateManager templateManager;
    private final TemplateResolution templateResolution;
    private final TemplateMode templateMode;
    private final IMarkupFactory markupFactory;
    private IdentifierSequences identifierSequences;



    protected AbstractTemplateProcessingContext(
            final ITemplateProcessingContext parent,
            final IEngineConfiguration configuration,
            final TemplateManager templateManager,
            final TemplateResolution templateResolution,
            final IContext context) {

        super(configuration, context);

        // parent CAN be null, if this is a first-level template
        Validate.notNull(templateManager, "Template Processor cannot be null");
        Validate.notNull(templateResolution, "Template Resolution cannot be null");

        this.parent = parent;
        this.templateManager = templateManager;
        this.templateResolution = templateResolution;
        this.templateMode = this.templateResolution.getTemplateMode();
        this.markupFactory =
                new StandardMarkupFactory(
                        getConfiguration(), this.templateMode, this.templateResolution.getTemplateName(), this.templateManager);
        // Most templates will not need this, so we will initialize it lazily
        this.identifierSequences = null;

    }

    protected AbstractTemplateProcessingContext(
            final ITemplateProcessingContext parent,
            final IEngineConfiguration configuration,
            final TemplateManager templateManager,
            final TemplateResolution templateResolution,
            final Locale locale, final Map<String, Object> variables) {

        super(configuration, locale, variables);

        // parent CAN be null, if this is a first-level template
        Validate.notNull(templateManager, "Template Processor cannot be null");
        Validate.notNull(templateResolution, "Template Resolution cannot be null");

        this.parent = parent;
        this.templateManager = templateManager;
        this.templateResolution = templateResolution;
        this.templateMode = this.templateResolution.getTemplateMode();
        this.markupFactory =
                new StandardMarkupFactory(
                        getConfiguration(), this.templateMode, this.templateResolution.getTemplateName(), this.templateManager);
        this.identifierSequences = new IdentifierSequences();

    }


    public final ITemplateProcessingContext getParent() {
        return this.parent;
    }

    public final TemplateManager getTemplateManager() {
        return this.templateManager;
    }

    public final TemplateResolution getTemplateResolution() {
        return this.templateResolution;
    }

    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public final IMarkupFactory getMarkupFactory() {
        return this.markupFactory;
    }

    public IdentifierSequences getIdentifierSequences() {
        // No problem in lazily initializing this here, as IProcessingContext objects should not be used by
        // multiple threads.
        if (this.identifierSequences == null) {
            this.identifierSequences = new IdentifierSequences();
        }
        return this.identifierSequences;
    }

}
