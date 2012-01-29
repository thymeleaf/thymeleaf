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

    private static final long serialVersionUID = -8434931215899913983L;

    
    private static final int DEFAULT_ATTRIBUTES_SIZE = 4;
    
    private final String originalName;
    private final String normalizedName;
    private final String normalizedPrefix;
    private final String unprefixedNormalizedName;
    private final boolean hasPrefix;
    
    private final boolean minimizableIfWeb;

    private String[] attributeNormalizedNames = null;
    private Attribute[] attributes = null;
    private int attributesLen = 0;


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
            final String normalizedAttributeName = Node.normalizeName(attributeName);
            for (int i = 0; i < this.attributesLen; i++) {
                if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    public boolean hasNormalizedAttribute(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return true;
            }
        }
        return false;
    }

    
    public Attribute getAttributeFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i];
            }
        }
        return null;
    }

    
    public String[] unsafeGetAttributeNormalizedNames() {
        return this.attributeNormalizedNames;
    }
    
    public Attribute[] unsafeGetAttributes() {
        return this.attributes;
    }
    
    
    public String getAttributeOriginalNameFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i].getOriginalName();
            }
        }
        return null;
    }
    
    
    public String getAttributeValueFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i].getValue();
            }
        }
        return null;
    }
    
    
    public String getAttributeValue(final String attributeName) {
        if (this.attributesLen > 0) {
            final String normalizedAttributeName = Node.normalizeName(attributeName);
            for (int i = 0; i < this.attributesLen; i++) {
                if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                    return this.attributes[i].getValue();
                }
            }
        }
        return null;
    }
    
    
    public Map<String,Attribute> getAttributeMap() {
        if (this.attributesLen > 0) {
            final Map<String,Attribute> attributeMap = new LinkedHashMap<String, Attribute>();
            for (int i = 0; i < this.attributesLen; i++) {
                attributeMap.put(this.attributeNormalizedNames[i], this.attributes[i]);
            }
            return attributeMap;
        }
        return Collections.emptyMap();
    }
    
    
    
    public void setAttribute(final String name, final String value) {
        
        Validate.notNull(name, "Attribute name cannot be null");
        Validate.notNull(value, "Attribute value (" + name + ") cannot be null");
        
        final Attribute attribute = new Attribute(name, value);
        final String attributeNormalizedName = attribute.getNormalizedName();
        
        if (this.attributesLen == 0) {
            
            this.attributeNormalizedNames = new String[DEFAULT_ATTRIBUTES_SIZE];
            this.attributes = new Attribute[DEFAULT_ATTRIBUTES_SIZE];
            
            this.attributeNormalizedNames[0] = attributeNormalizedName;
            this.attributes[0] = attribute;
            
            this.attributesLen = 1;
            
            return;
            
        }
        
        for (int i = 0; i < this.attributesLen; i++) {
            // First, we will check if attribute already exists
            if (this.attributeNormalizedNames[i].equals(attributeNormalizedName)) {
                this.attributes[i] = attribute;
                return;
            }
        }
        
        if (this.attributesLen >= this.attributes.length) {
            final int newLength = this.attributesLen * 2;
            final String[] newAttributeNormalizedNames = Arrays.copyOf(this.attributeNormalizedNames, newLength);
            final Attribute[] newAttributes = Arrays.copyOf(this.attributes, newLength);
            this.attributeNormalizedNames = newAttributeNormalizedNames;
            this.attributes = newAttributes;
        }
        
        this.attributeNormalizedNames[this.attributesLen] = attributeNormalizedName;
        this.attributes[this.attributesLen] = attribute;
        this.attributesLen++;
        
    }


    
    public void setAttributes(final Map<String,String> newAttributes) {
        clearAttributes();
        if (newAttributes != null && newAttributes.size() > 0) {
            for (final Map.Entry<String,String> newAttributesEntry : newAttributes.entrySet()) {
                setAttribute(newAttributesEntry.getKey(), newAttributesEntry.getValue());
            }
        }
    }
    

    
    public void clearAttributes() {
        this.attributeNormalizedNames = null;
        this.attributes = null;
        this.attributesLen = 0;
    }
    

    
    public void removeAttribute(final String attributeName) {
        
        Validate.notNull(attributeName, "Name of attribute to be removed cannot be null");

        if (this.attributesLen > 0) {
            
            final String normalizedAttributeName = Node.normalizeName(attributeName);
            
            for (int i = 0; i < this.attributesLen; i++) {
                
                if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                    for (int j = i + 1; j < this.attributesLen; j++) {
                        this.attributeNormalizedNames[j - 1] = this.attributeNormalizedNames[j];
                        this.attributes[j - 1] = this.attributes[j];
                    }
                    this.attributesLen--;
                    return;
                }
                
            }

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
            element.attributeNormalizedNames = Arrays.copyOf(this.attributeNormalizedNames, this.attributesLen);
            element.attributes = Arrays.copyOf(this.attributes, this.attributesLen);
            element.attributesLen = this.attributesLen;
        }
        
    }



}
