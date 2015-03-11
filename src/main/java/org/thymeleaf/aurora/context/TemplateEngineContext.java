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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class TemplateEngineContext implements ITemplateEngineContext {

    private final Set<IDialect> dialects;

    private final ITextRepository textRepository;

    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;


    public TemplateEngineContext(final Set<IDialect> dialects, final ITextRepository textRepository) {

        super();

        Validate.notNull(dialects, "Dialect list cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");

        this.dialects = Collections.unmodifiableSet(new LinkedHashSet<IDialect>(dialects));
        this.textRepository = textRepository;

        this.elementDefinitions = new ElementDefinitions();
        this.attributeDefinitions = new AttributeDefinitions();

    }



    public Set<IDialect> getDialects() {
        return this.dialects;
    }

    public String getStandardDialectPrefix() {
        return "th";
    }

    public ElementDefinitions getElementDefinitions() {
        return this.elementDefinitions;
    }

    public AttributeDefinitions getAttributeDefinitions() {
        return this.attributeDefinitions;
    }

    public ITextRepository getTextRepository() {
        return this.textRepository;
    }

}
