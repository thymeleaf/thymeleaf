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
package org.thymeleaf.dom;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.util.PrefixUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Element extends NestableNode {

    private static final int DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE = 4;
    
    private final String originalName;
    private final String normalizedName;
    private final String normalizedPrefix;
    private final String unprefixedNormalizedName;
    private final boolean hasPrefix;
    
    private final boolean minimizableIfWeb;
    
    private Map<String,Attribute> attributes;
    private int attributesLen;


    public Element(final String name) {
        this(name, null, null);
    }


    public Element(final String name, final String documentName) {
        this(name, documentName, null);
    }
    

    public Element(final String name, final String documentName, final Integer lineNumber) {
        
        super(documentName, lineNumber);
        
        Validate.notNull(name, "Element name cannot be null");
        
        this.originalName = name;
        this.normalizedName = Node.normalizeName(name);
        this.normalizedPrefix = PrefixUtils.getPrefix(this.normalizedName);
        this.unprefixedNormalizedName = PrefixUtils.getUnprefixed(this.normalizedName);
        this.hasPrefix = this.normalizedPrefix != null;
        
        this.minimizableIfWeb = 
                Arrays.binarySearch(Standards.MINIMIZABLE_XHTML_TAGS, this.normalizedName) >= 0;
                
        setChildren((Node[])null);
        setAttributes(null);
        
    }
    

    
    public String getOriginalName() {
        return this.originalName;
    }
    
    public String getNormalizedName() {
        return this.normalizedName;
    }
    
    public String getNormalizedPrefix() {
        return this.normalizedPrefix;
    }


    public String getUnprefixedNormalizedName() {
        return this.unprefixedNormalizedName;
    }


    public boolean getHasPrefix() {
        return this.hasPrefix;
    }

    
    
    public boolean isMinimizableIfWeb() {
        return this.minimizableIfWeb;
    }
    
    
    

    
    /*
     * ************************
     * ************************
     *        ATTRIBUTES
     * ************************
     * ************************
     */


    public boolean hasAttributes() {
        return this.attributesLen != 0;
    }
    
    
    public int numAttributes() {
        return this.attributesLen;
    }

    
    public boolean hasAttribute(final String attributeName) {
        if (this.attributesLen > 0) {
            return this.attributes.containsKey(Node.normalizeName(attributeName));
        }
        return false;
    }
    
    
    public boolean hasNormalizedAttribute(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            return this.attributes.containsKey(normalizedAttributeName);
        }
        return false;
    }

    
    public Attribute getAttributeFromNormalizedName(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            return this.attributes.get(normalizedAttributeName);
        }
        return null;
    }

    
    public Set<String> getAttributeNormalizedNames() {
        if (this.attributesLen > 0) {
            return this.attributes.keySet();
        }
        return Collections.emptySet();
    }
    
    
    public String getAttributeOriginalNameFromNormalizedName(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            final Attribute attribute = this.attributes.get(normalizedAttributeName);
            if (attribute == null) {
                return null;
            }
            return attribute.getOriginalName();
        }
        return null;
    }
    
    
    public String getAttributeValueFromNormalizedName(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            final Attribute attribute = this.attributes.get(normalizedAttributeName);
            if (attribute == null) {
                return null;
            }
            return attribute.getValue();
        }
        return null;
    }
    
    
    public String getAttributeValue(final String attributeName) {
        if (this.attributesLen > 0) {
            final Attribute attribute = this.attributes.get(Node.normalizeName(attributeName));
            if (attribute == null) {
                return null;
            }
            return attribute.getValue();
        }
        return null;
    }
    
    
    public Map<String,Attribute> getAttributeMap() {
        if (this.attributes != null) {
            return Collections.unmodifiableMap(this.attributes);
        }
        return Collections.emptyMap();
    }
    
    
    
    public void setAttribute(final String name, final String value) {
        Validate.notNull(name, "Attribute name cannot be null");
        Validate.notNull(value, "Attribute value (" + name + ") cannot be null");
        if (this.attributesLen == 0) {
            this.attributes = new LinkedHashMap<String,Attribute>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
        }
        final Attribute attribute = new Attribute(name, value);
        this.attributes.put(attribute.getNormalizedName(), attribute);
        this.attributesLen = this.attributes.size();
    }

    
    public void setAttributes(final Map<String,String> newAttributes) {
        if (newAttributes != null && newAttributes.size() > 0) {
            if (this.attributesLen == 0) {
                this.attributes = new LinkedHashMap<String,Attribute>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
            }
            for (final Map.Entry<String,String> newAttributesEntry : newAttributes.entrySet()) {
                final String newAttributeName = newAttributesEntry.getKey();
                final String newAttributeValue = newAttributesEntry.getValue();
                Validate.notNull(newAttributeName, "Attribute name cannot be null");
                Validate.notNull(newAttributeValue, "Attribute value (" + newAttributeName + ") cannot be null");
                final Attribute attribute = new Attribute(newAttributeName, newAttributeValue);
                this.attributes.put(attribute.getNormalizedName(), attribute);
            }
            this.attributesLen = this.attributes.size();
        }
    }
    
    
    public void clearAttributes() {
        this.attributes = null;
        this.attributesLen = 0;
    }
    
    
    public void removeAttribute(final String attributeName) {
        
        Validate.notNull(attributeName, "Name of attribute to be removed cannot be null");

        if (this.attributesLen > 0) {
            final String normalizedAttributeName = Node.normalizeName(attributeName);
            this.attributes.remove(normalizedAttributeName);
            this.attributesLen = this.attributes.size();
        }
        
    }


    
    
    /*
     * --------------------
     * PREPROCESSING
     * *********************************
     */
    

    @Override
    void doAdditionalPrecomputeNestableNode(final Configuration configuration) {
        // Nothing to be done here
    }

    
    

    /*
     * *********************************
     * *********************************
     *        NODE CLONING
     * *********************************
     * *********************************
     */
    
    

    public final Element cloneElementNodeWithNewName(final NestableNode newParent, final String newElementName, final boolean cloneProcessors) {
        final Element clonedElement = new Element(newElementName);
        cloneNodeInternals(clonedElement, newParent, cloneProcessors);
        return clonedElement;
    }
    
    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Element(this.originalName, getDocumentName(), getLineNumber());
    }
    

    @Override
    void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final Element element = (Element) node;
        
        if (this.attributesLen > 0) {
            element.attributes = new LinkedHashMap<String,Attribute>(this.attributes);
            element.attributesLen = this.attributesLen;
        }
        
    }



}
