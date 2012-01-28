/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class ElementNameProcessorMatcher implements IElementNameProcessorMatcher {
    
    
    private final String elementName;
    private final Map<String,String> attributeValuesByNameFilter;
    
    
    
    
    
    public ElementNameProcessorMatcher(final String elementName) {
        this(elementName, null);
    }

    
    public ElementNameProcessorMatcher(final String elementName, 
            final String filterAttributeName, final String filterAttributeValue) {
        this(elementName, Collections.singletonMap(filterAttributeName, filterAttributeValue));
    }

    
    public ElementNameProcessorMatcher(final String elementName, final Map<String,String> attributeValuesByNameFilter) {
        super();
        Validate.notEmpty(elementName, "Element name cannot be null or empty");
        this.elementName = elementName;
        if (attributeValuesByNameFilter == null || attributeValuesByNameFilter.size() == 0) {
            this.attributeValuesByNameFilter = null;
        } else {
            final Map<String, String> newAttributeValuesByNameFilter = new HashMap<String, String>(attributeValuesByNameFilter.size() + 1, 1.0f);
            newAttributeValuesByNameFilter.putAll(attributeValuesByNameFilter);
            this.attributeValuesByNameFilter = Collections.unmodifiableMap(newAttributeValuesByNameFilter);
        }
    }
    


    
    public String getElementName(final ProcessorMatchingContext context) {
        return Node.applyDialectPrefix(this.elementName, context.getDialectPrefix());
    }
    
    public Map<String,String> getAttributeValuesByNameFilter() {
        return this.attributeValuesByNameFilter;
    }

    
    public boolean hasFilter() {
        return this.attributeValuesByNameFilter != null;
    }

    

    public boolean matches(final Node node, final ProcessorMatchingContext context) {
        
        if (!(node instanceof Element)) {
            return false;
        }
        
        final Element element = (Element) node;
        final String completeElementName = getElementName(context); 
        
        if (!element.getNormalizedName().equals(completeElementName)) {
            return false;
        }
        
        if (this.attributeValuesByNameFilter != null) {
            
            for (final Map.Entry<String,String> filterAttributeEntry : this.attributeValuesByNameFilter.entrySet()) {
                
                final String filterAttributeName = filterAttributeEntry.getKey();
                final String filterAttributeValue = filterAttributeEntry.getValue();
                
                if (!element.hasAttribute(filterAttributeName)) {
                    if (filterAttributeValue != null) {
                        return false;
                    }
                    continue;
                }
                final String elementAttributeValue = element.getAttributeValue(filterAttributeName);
                if (elementAttributeValue == null) {
                    if (filterAttributeValue != null) {
                        return false;
                    }
                } else {
                    if (!elementAttributeValue.equals(filterAttributeValue)) {
                        return false;
                    }
                }
                
            }
            
        }
        
        return true;
        
    }
    

    
    public final Class<? extends Element> appliesTo() {
        return Element.class;
    }
    
    
}
