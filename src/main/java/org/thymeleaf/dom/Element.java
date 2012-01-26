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
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Element extends NestableNode {

    private static final int DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE = 3;
    
    private final String name;
    private final String normalizedName;
    
    private final boolean minimizableIfWeb;
    
    private Map<String,String> attributeNames;
    private Map<String,String> attributeValues;
    private int attributesLen;
    
    private boolean hasXmlnsAttributes;


    public Element(final String name) {
        this(name, null, null);
    }


    public Element(final String name, final String documentName) {
        this(name, documentName, null);
    }
    

    public Element(final String name, final String documentName, final Integer lineNumber) {
        
        super(documentName, lineNumber);
        
        Validate.notNull(name, "Element name cannot be null");
        
        this.name = name;
        this.normalizedName = Node.normalizeName(name);
        this.minimizableIfWeb = 
                Arrays.binarySearch(Standards.MINIMIZABLE_XHTML_TAGS, this.normalizedName) >= 0;
                
        setChildren((Node[])null);
        setAttributes(null);
        
        this.hasXmlnsAttributes = false;
        
    }
    

    
    public String getName() {
        return this.name;
    }
    
    public String getNormalizedName() {
        return this.normalizedName;
    }
    
    
    public boolean isName(final String comparedName) {
        if (comparedName == null) {
            return false;
        }
        return Node.normalizeName(comparedName).equals(this.normalizedName);
    }

    public boolean isMinimizableIfWeb() {
        return this.minimizableIfWeb;
    }
    
    public boolean getHasXmlnsAttributes() {
        return this.hasXmlnsAttributes;
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
            return this.attributeNames.containsKey(Node.normalizeName(attributeName));
        }
        return false;
    }
    
    
    public Set<String> getAttributeNormalizedNames() {
        if (this.attributesLen > 0) {
            return this.attributeValues.keySet();
        }
        return Collections.emptySet();
    }
    
    
    public String getAttributeOriginalNameFromNormalizedName(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            return this.attributeNames.get(normalizedAttributeName);
        }
        return null;
    }
    
    
    public String getAttributeValueFromNormalizedName(final String normalizedAttributeName) {
        if (this.attributesLen > 0) {
            return this.attributeValues.get(normalizedAttributeName);
        }
        return null;
    }
    
    
    public String getAttributeValue(final String attributeName) {
        if (this.attributesLen > 0) {
            return this.attributeValues.get(Node.normalizeName(attributeName));
        }
        return null;
    }
    
    
    public Map<String,String> getAttributeMap() {
        if (this.attributeValues != null) {
            return Collections.unmodifiableMap(this.attributeValues);
        }
        return Collections.emptyMap();
    }
    
    
    
    public void setAttribute(final String name, final String value) {
        Validate.notNull(name, "Attribute name cannot be null");
        Validate.notNull(value, "Attribute value (" + name + ") cannot be null");
        if (this.attributesLen == 0) {
            this.attributeNames = new LinkedHashMap<String,String>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
            this.attributeValues = new LinkedHashMap<String,String>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
        }
        final String normalizedAttributeName = Node.normalizeName(name);
        this.attributeNames.put(normalizedAttributeName, name);
        this.attributeValues.put(normalizedAttributeName, value);
        this.attributesLen = this.attributeValues.size();
        if (normalizedAttributeName.startsWith(Standards.XMLNS_PREFIX)) {
            this.hasXmlnsAttributes = true;
        }
    }

    
    public void setAttributes(final Map<String,String> newAttributes) {
        if (newAttributes != null && newAttributes.size() > 0) {
            if (this.attributesLen == 0) {
                this.attributeNames = new LinkedHashMap<String,String>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
                this.attributeValues = new LinkedHashMap<String,String>(DEFAULT_INITIAL_ATTRIBUTE_MAP_SIZE);
            }
            for (final Map.Entry<String,String> newAttributesEntry : newAttributes.entrySet()) {
                final String newAttributeName = newAttributesEntry.getKey();
                final String newAttributeValue = newAttributesEntry.getValue();
                Validate.notNull(newAttributeName, "Attribute name cannot be null");
                Validate.notNull(newAttributeValue, "Attribute value (" + newAttributeName + ") cannot be null");
                final String normalizedAttributeName = Node.normalizeName(newAttributeName);
                this.attributeNames.put(normalizedAttributeName, newAttributeName);
                this.attributeValues.put(normalizedAttributeName, newAttributeValue);
                if (normalizedAttributeName.startsWith(Standards.XMLNS_PREFIX)) {
                    this.hasXmlnsAttributes = true;
                }
            }
            this.attributesLen = this.attributeValues.size();
        }
    }
    
    
    public void clearAttributes() {
        this.attributeValues = null;
        this.attributeNames = null;
        this.attributesLen = 0;
    }
    
    
    public void removeAttribute(final String attributeName) {
        
        Validate.notNull(attributeName, "Name of attribute to be removed cannot be null");

        if (this.attributesLen > 0) {
            final String normalizedAttributeName = Node.normalizeName(attributeName);
            this.attributeNames.remove(normalizedAttributeName);
            this.attributeValues.remove(normalizedAttributeName);
            this.attributesLen = this.attributeValues.size();
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
        return new Element(this.name, getDocumentName(), getLineNumber());
    }
    

    @Override
    void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final Element element = (Element) node;
        
        if (this.attributesLen > 0) {
            element.attributeNames = new LinkedHashMap<String, String>(this.attributeNames);
            element.attributeValues = new LinkedHashMap<String, String>(this.attributeValues);
            element.attributesLen = this.attributesLen;
        }
        element.hasXmlnsAttributes = this.hasXmlnsAttributes;
        
    }



}
