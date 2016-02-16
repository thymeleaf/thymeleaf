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
package org.thymeleaf.templateparser.markup.decoupled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.model.IElementAttributes;

/**
 * <p>
 *   This class specifies containers for template logic metadata, normally coming from separate template files
 *   (decoupled templates).
 * </p>
 * <p>
 *   Instances of this class are built and populated by instances of {@link DecoupledTemplateMetadataBuilderMarkupHandler},
 *   acting as handlers on the {@link org.attoparser.IMarkupParser} being used for parsing the normal template
 *   resources.
 * </p>
 * <p>
 *   Once built and populated, instances of this class are handled over to
 *   {@link org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler} instances which are one of the steps in
 *   the template parsing chain (converting parser events into {@link org.thymeleaf.engine.ITemplateHandler} events).
 *   Attributes specified here to be injected into the template are injected at real-time during the parsing operation
 *   itself, so that overhead is minimal (and zero once the template is cached).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledTemplateMetadata {


    private final Map<String, List<DecoupledInjectedAttribute>> injectedAttributes =
            new HashMap<String, List<DecoupledInjectedAttribute>>(20);


    public DecoupledTemplateMetadata() {
        super();
    }



    public boolean hasInjectedAttributes() {
        return this.injectedAttributes.size() > 0;
    }


    public Set<String> getAllInjectedAttributeSelectors() {
        return this.injectedAttributes.keySet();
    }

    public List<DecoupledInjectedAttribute> getInjectedAttributesForSelector(final String selector) {
        return this.injectedAttributes.get(selector);
    }


    public void addInjectedAttribute(final String selector,
            final String attributeName, final IElementAttributes.ValueQuotes valueQuotes, final String attributeValue) {

        List<DecoupledInjectedAttribute> injectedAttributesForSelector = this.injectedAttributes.get(selector);
        if (injectedAttributesForSelector == null) {
            injectedAttributesForSelector = new ArrayList<DecoupledInjectedAttribute>(2);
            this.injectedAttributes.put(selector, injectedAttributesForSelector);
        }

        injectedAttributesForSelector.add(new DecoupledInjectedAttribute(attributeName, valueQuotes, attributeValue));

    }
    
}
