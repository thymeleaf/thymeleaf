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
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class AttributeNameProcessorMatcher implements IAttributeNameProcessorMatcher {
    
    @SuppressWarnings("unchecked")
    private static final Class<? extends Node>[] APPLIES_TO =
            (Class<? extends Node>[]) new Class<?>[] { Tag.class };
    
    private final String attributeName;
    private final String tagNameFilter;
    private final Map<String,String> attributeValuesByNameFilter;
    
    
    
    
    
    public AttributeNameProcessorMatcher(final String attributeName) {
        this(attributeName, null, null);
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, final String tagNameFilter) {
        this(attributeName, tagNameFilter, null);
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, 
            final String tagNameFilter, final String filterAttributeName, final String filterAttributeValue) {
        this(attributeName, tagNameFilter, Collections.singletonMap(filterAttributeName, filterAttributeValue));
    }

    
    public AttributeNameProcessorMatcher(final String attributeName, 
            final String tagNameFilter, final Map<String,String> attributeValuesByNameFilter) {
        super();
        Validate.notEmpty(attributeName, "Attribute name cannot be null or empty");
        this.attributeName = attributeName;
        this.tagNameFilter = Node.normalizeName(tagNameFilter);
        if (attributeValuesByNameFilter == null || attributeValuesByNameFilter.size() == 0) {
            this.attributeValuesByNameFilter = null;
        } else {
            final Map<String, String> newAttributeValuesByNameFilter = new LinkedHashMap<String, String>();
            newAttributeValuesByNameFilter.putAll(attributeValuesByNameFilter);
            this.attributeValuesByNameFilter = Collections.unmodifiableMap(newAttributeValuesByNameFilter);
        }
    }
    


    
    
    public String getAttributeName(final ProcessorMatchingContext context) {
        return Node.applyDialectPrefix(this.attributeName, context.getDialectPrefix());
    }

    public String getTagNameFilter() {
        return this.tagNameFilter;
    }
    
    public Map<String,String> getAttributeValuesByNameFilter() {
        return this.attributeValuesByNameFilter;
    }

    

    public boolean matches(final Node node, final ProcessorMatchingContext context) {
        
        if (!(node instanceof Tag)) {
            return false;
        }
        
        final Tag tag = (Tag) node;
        final String completeAttributeName = getAttributeName(context); 
        
        if (!tag.hasAttribute(completeAttributeName)) {
            return false;
        }
        
        if (this.tagNameFilter != null) {
            if (!tag.getNormalizedName().equals(this.tagNameFilter)) {
                return false;
            }
        }
        
        if (this.attributeValuesByNameFilter != null) {
            
            for (final Map.Entry<String,String> filterAttributeEntry : this.attributeValuesByNameFilter.entrySet()) {
                
                final String filterAttributeName = filterAttributeEntry.getKey();
                final String filterAttributeValue = filterAttributeEntry.getValue();
                
                if (!tag.hasAttribute(filterAttributeName)) {
                    if (filterAttributeValue != null) {
                        return false;
                    }
                    continue;
                }
                final String tagAttributeValue = tag.getAttributeValue(filterAttributeName);
                if (tagAttributeValue == null) {
                    if (filterAttributeValue != null) {
                        return false;
                    }
                } else {
                    if (!tagAttributeValue.equals(filterAttributeValue)) {
                        return false;
                    }
                }
                
            }
            
        }
        
        return true;
        
    }


    
    public final Class<? extends Node>[] appliesTo() {
        return APPLIES_TO;
    }
    
    
}
