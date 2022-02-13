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
package org.thymeleaf.templateparser.markup.decoupled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   This class specifies containers for decoupled template logic, normally coming from separate template files
 *   (decoupled templates).
 * </p>
 * <p>
 *   Instances of this class are built and populated by instances of {@link DecoupledTemplateLogicBuilderMarkupHandler},
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
 * <p>
 *   Instances of this class are <strong>not thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledTemplateLogic {


    private final Map<String, List<DecoupledInjectedAttribute>> injectedAttributes =
            new HashMap<String, List<DecoupledInjectedAttribute>>(20);


    public DecoupledTemplateLogic() {
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


    public void addInjectedAttribute(final String selector, final DecoupledInjectedAttribute injectedAttribute) {

        Validate.notNull(selector, "Selector cannot be null");
        Validate.notNull(injectedAttribute, "Injected Attribute cannot be null");

        List<DecoupledInjectedAttribute> injectedAttributesForSelector = this.injectedAttributes.get(selector);
        if (injectedAttributesForSelector == null) {
            injectedAttributesForSelector = new ArrayList<DecoupledInjectedAttribute>(2);
            this.injectedAttributes.put(selector, injectedAttributesForSelector);
        }

        injectedAttributesForSelector.add(injectedAttribute);

    }



    @Override
    public String toString() {

        // We will order the keys so that we can more easily debug and test based on this toString()
        final List<String> keys = new ArrayList<String>(this.injectedAttributes.keySet());
        Collections.sort(keys);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(keys.get(i));
            strBuilder.append('=');
            strBuilder.append(this.injectedAttributes.get(keys.get(i)));
        }
        strBuilder.append('}');

        return strBuilder.toString();

    }

}
