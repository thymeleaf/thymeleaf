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
package org.thymeleaf.dom;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   A specialization of {@link NestableNode} that adds the ability to hold 
 *   attributes.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.8
 *
 */
public abstract class NestableAttributeHolderNode extends NestableNode {

    private static final long serialVersionUID = -292925657016881649L;

    private static final int DEFAULT_ATTRIBUTES_SIZE = 4;
    
    private String[] attributeNormalizedNames = null;
    private Attribute[] attributes = null;
    private int attributesLen = 0;



    
    protected NestableAttributeHolderNode(final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
    }
    

    
    /*
     * ************************
     * ************************
     *        ATTRIBUTES
     * ************************
     * ************************
     */


    /**
     * <p>
     *   Returns whether this element has any attributes or not.
     * </p>
     * 
     * @return true if this element has attributes, false if not.
     */
    public final boolean hasAttributes() {
        return this.attributesLen != 0;
    }
    

    /**
     * <p>
     *   Returns the number of attributes contained in this element.
     * </p>
     * 
     * @return the number of attributes.
     */
    public final int numAttributes() {
        return this.attributesLen;
    }


    /**
     * <p>
     *   Returns whether an attribute exists in the element or not. The specified
     *   name does not have to be normalized, because a normalization operation will
     *   be performed before comparing.
     * </p>
     * 
     * @param attributeName the name of the attribute to be checked.
     * @return true if the attribute exists, false if not.
     */
    public final boolean hasAttribute(final String attributeName) {
        if (this.attributesLen > 0) {
            final String normalizedAttributeName = Attribute.normalizeAttributeName(attributeName);
            for (int i = 0; i < this.attributesLen; i++) {
                if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }
    

    /**
     * <p>
     *   Returns whether an attribute exists in the element or not. The specified
     *   name has to be already normalized because no normalization operations will
     *   be performed before comparing.
     * </p>
     * 
     * @param normalizedAttributeName the name of the attribute to be checked, already normalized.
     * @return true if the attribute exists, false if not.
     */
    public final boolean hasNormalizedAttribute(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * <p>
     *   Returns an attribute, if it exists (null if not). The specified
     *   name has to be already normalized because no normalization operations will
     *   be performed before comparing.
     * </p>
     * 
     * @param normalizedAttributeName the name of the attribute to retrieve.
     * @return the attribute.
     */
    public final Attribute getAttributeFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i];
            }
        }
        return null;
    }


    /**
     * <p>
     *   Returns the inner array used for storing the normalized names of all the
     *   existing attributes. This method is unsafe and for internal use, and should
     *   not be called directly. Modifications to this array could produce severe
     *   DOM inconsistencies.
     * </p>
     * 
     * @return the unsafe array of normalized names.
     */
    public final String[] unsafeGetAttributeNormalizedNames() {
        return this.attributeNormalizedNames;
    }

    /**
     * <p>
     *   Returns the inner array used for storing the element attributes. This method is 
     *   unsafe and for internal use, and should not be called directly. 
     *   Modifications to this array could produce severe DOM inconsistencies.
     * </p>
     * 
     * @return the array of attributes.
     */
    public final Attribute[] unsafeGetAttributes() {
        return this.attributes;
    }
    

    /**
     * <p>
     *   Returns the original name of an attribute from its normalized name.
     * </p>
     * 
     * @param normalizedAttributeName the normalized name of the attribute.
     * @return the original attribute (as written in the template file).
     */
    public final String getAttributeOriginalNameFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i].getOriginalName();
            }
        }
        return null;
    }
    

    /**
     * <p>
     *   Returns the value of an attribute from its normalized name.
     * </p>
     * 
     * @param normalizedAttributeName the normalized name of the attribute.
     * @return the valur of the attribute, or null if the attribute does not exist.
     */
    public final String getAttributeValueFromNormalizedName(final String normalizedAttributeName) {
        for (int i = 0; i < this.attributesLen; i++) {
            if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                return this.attributes[i].getValue();
            }
        }
        return null;
    }
    

    /**
     * <p>
     *   Returns the value of an attribute from its attribute name, without needing this name
     *   to be normalized (because the specified name will be normalized before performing the
     *   comparison).
     * </p>
     * 
     * @param attributeName the attribute name.
     * @return the value of the attribute.
     */
    public final String getAttributeValue(final String attributeName) {
        if (this.attributesLen > 0) {
            final String normalizedAttributeName = Attribute.normalizeAttributeName(attributeName);
            for (int i = 0; i < this.attributesLen; i++) {
                if (this.attributeNormalizedNames[i].equals(normalizedAttributeName)) {
                    return this.attributes[i].getValue();
                }
            }
        }
        return null;
    }
    

    /**
     * <p>
     *   Returns a map with all the names (normalized) and values of the element attributes.
     * </p>
     * <p>
     *   The map object returned by this method is a new instance, created ad-hoc with each
     *   method call.
     * </p>
     * 
     * @return the map of all current attributes in the element.
     */
    public final Map<String,Attribute> getAttributeMap() {
        if (this.attributesLen > 0) {
            final Map<String,Attribute> attributeMap = new LinkedHashMap<String, Attribute>();
            for (int i = 0; i < this.attributesLen; i++) {
                attributeMap.put(this.attributeNormalizedNames[i], this.attributes[i]);
            }
            return attributeMap;
        }
        return Collections.emptyMap();
    }
    
   
    /**
     * <p>
     *   Establishes a new value for an element attribute. If the attribute already
     *   exists, its value is substituted by the one specified.
     * </p>
     * 
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    public final void setAttribute(final String name, final String value) {
        setAttribute(name, false, value);
    }

    
    
    /**
     * <p>
     *   Establishes a new value for an element attribute. If the attribute already
     *   exists, its value is substituted by the one specified.
     * </p>
     * 
     * @param name the name of the attribute.
     * @param onlyName whether this attribute has a value or not (is only a name). 
     * @param value the value of the attribute.
     */
    public final void setAttribute(final String name, final boolean onlyName, final String value) {
        
        Validate.notNull(name, "Attribute name cannot be null");

        final String attributeNormalizedName = Attribute.normalizeAttributeName(name);

        if (this.attributesLen > 0) {
            for (int i = 0; i < this.attributesLen; i++) {
                // First, we will check if attribute already exists
                if (this.attributeNormalizedNames[i].equals(attributeNormalizedName)) {
                    this.attributes[i] = this.attributes[i].cloneForValue(onlyName, value);
                    return;
                }
            }
        }

        // Attribute does not already exist, so we cannot take advantage from cloning it (with a different value)
        final Attribute attribute = new Attribute(name, onlyName, value);

        if (this.attributesLen == 0) {
            
            this.attributeNormalizedNames = new String[DEFAULT_ATTRIBUTES_SIZE];
            this.attributes = new Attribute[DEFAULT_ATTRIBUTES_SIZE];
            
            this.attributeNormalizedNames[0] = attributeNormalizedName;
            this.attributes[0] = attribute;
            
            this.attributesLen = 1;
            
            return;
            
        }

        if (this.attributesLen >= this.attributes.length) {
            final int newLength = this.attributesLen * 2;
            final String[] newAttributeNormalizedNames = ArrayUtils.copyOf(this.attributeNormalizedNames, newLength);
            final Attribute[] newAttributes = ArrayUtils.copyOf(this.attributes, newLength);
            this.attributeNormalizedNames = newAttributeNormalizedNames;
            this.attributes = newAttributes;
        }
        
        this.attributeNormalizedNames[this.attributesLen] = attributeNormalizedName;
        this.attributes[this.attributesLen] = attribute;
        this.attributesLen++;
        
    }



    /**
     * <p>
     *   Establishes the value for several attributes at a time. If any of the
     *   specified attributes already exists, their values will be substituted by
     *   the one specified in the parameter map.
     * </p>
     * 
     * @param newAttributes the new attributes to be established.
     */
    public final void setAttributes(final Map<String,String> newAttributes) {
        clearAttributes();
        if (newAttributes != null && newAttributes.size() > 0) {
            for (final Map.Entry<String,String> newAttributesEntry : newAttributes.entrySet()) {
                setAttribute(newAttributesEntry.getKey(), newAttributesEntry.getValue());
            }
        }
    }
    


    /**
     * <p>
     *   Clears all the attributes of the element.
     * </p> 
     */
    public final void clearAttributes() {
        this.attributeNormalizedNames = null;
        this.attributes = null;
        this.attributesLen = 0;
    }
    


    /**
     * <p>
     *   Removes a specific attribute from the element. The specified name
     *   does not have to be normalized (it will be normalized before performing
     *   the removal operation).
     * </p>
     * 
     * @param attributeName the name of tha attribute to remove (does not have to be
     *        normalized).
     */
    public final void removeAttribute(final String attributeName) {
        
        Validate.notNull(attributeName, "Name of attribute to be removed cannot be null");

        if (this.attributesLen > 0) {
            
            final String normalizedAttributeName = Attribute.normalizeAttributeName(attributeName);
            
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
     * *********************************
     * *********************************
     *        NODE CLONING
     * *********************************
     * *********************************
     */
    

    @Override
    final void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final NestableAttributeHolderNode element = (NestableAttributeHolderNode) node;
        
        if (this.attributesLen > 0) {
            element.attributeNormalizedNames = ArrayUtils.copyOf(this.attributeNormalizedNames, this.attributesLen);
            element.attributes = ArrayUtils.copyOf(this.attributes, this.attributesLen);
            element.attributesLen = this.attributesLen;
        }
        
        doCloneNestableAttributeHolderNodeInternals(node, newParent, cloneProcessors);
        
    }

    
    abstract void doCloneNestableAttributeHolderNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors);


}
