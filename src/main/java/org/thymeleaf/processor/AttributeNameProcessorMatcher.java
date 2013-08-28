/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class AttributeNameProcessorMatcher implements IAttributeNameProcessorMatcher {
    
    
    private final String attributeName;
    private final String elementNameFilter;
    private final Map<String,String> attributeValuesByNameFilter;
    
    
    
    
    
    public AttributeNameProcessorMatcher(final String attributeName) {
        this(attributeName, null, null);
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, final String elementNameFilter) {
        this(attributeName, elementNameFilter, null);
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, 
            final String elementNameFilter, final String filterAttributeName, final String filterAttributeValue) {
        this(attributeName, elementNameFilter, Collections.singletonMap(filterAttributeName, filterAttributeValue));
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, 
            final String elementNameFilter, final Map<String,String> attributeValuesByNameFilter) {
        super();
        Validate.notEmpty(attributeName, "Attribute name cannot be null or empty");
        this.attributeName = attributeName;
        this.elementNameFilter = Element.normalizeElementName(elementNameFilter);
        if (attributeValuesByNameFilter == null || attributeValuesByNameFilter.size() == 0) {
            this.attributeValuesByNameFilter = null;
        } else {
            final Map<String, String> newAttributeValuesByNameFilter = new HashMap<String, String>(attributeValuesByNameFilter.size() + 1, 1.0f);
            newAttributeValuesByNameFilter.putAll(attributeValuesByNameFilter);
            this.attributeValuesByNameFilter = Collections.unmodifiableMap(newAttributeValuesByNameFilter);
        }
    }
    


    
    
    public String getAttributeName(final ProcessorMatchingContext context) {
        return Attribute.applyPrefixToAttributeName(this.attributeName, context.getDialectPrefix());
    }

    public String getElementNameFilter() {
        return this.elementNameFilter;
    }
    
    public Map<String,String> getAttributeValuesByNameFilter() {
        return this.attributeValuesByNameFilter;
    }

    

    public boolean matches(final Node node, final ProcessorMatchingContext context) {
        
        if (!(node instanceof NestableAttributeHolderNode)) {
            return false;
        }
        
        final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode) node;
        final String completeAttributeName = getAttributeName(context); 
        
        if (!attributeHolderNode.hasAttribute(completeAttributeName)) {
            return false;
        }
        
        if (this.elementNameFilter != null) {
            if (attributeHolderNode instanceof Element) {
                final Element element = (Element) attributeHolderNode;
                if (!element.getNormalizedName().equals(this.elementNameFilter)) {
                    return false;
                }
            } else {
                // if node is not an element (because it probably is a group of nodes, it has no
                // "element/tag name", and therefore does not match if this matcher specifies one.
                return false;
            }
        }
        
        if (this.attributeValuesByNameFilter != null) {
            
            for (final Map.Entry<String,String> filterAttributeEntry : this.attributeValuesByNameFilter.entrySet()) {
                
                final String filterAttributeName = filterAttributeEntry.getKey();
                final String filterAttributeValue = filterAttributeEntry.getValue();
                
                if (!attributeHolderNode.hasAttribute(filterAttributeName)) {
                    if (filterAttributeValue != null) {
                        return false;
                    }
                    continue;
                }
                final String elementAttributeValue = attributeHolderNode.getAttributeValue(filterAttributeName);
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


    
    public Class<? extends NestableAttributeHolderNode> appliesTo() {
        return NestableAttributeHolderNode.class;
    }
    
    
}
