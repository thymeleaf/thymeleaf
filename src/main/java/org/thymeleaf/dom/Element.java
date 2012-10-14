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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.PrefixUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   An Element node in a Thymeleaf DOM tree. In XML-based templates, Elements
 *   normally correspond to tags.
 * </p>
 * <p>
 *   Elements are nestable nodes, and therefore have children. Besides, they
 *   have a sequence of attributes, each of them with a value. Attributes
 *   are modeled by means of the {@link Attribute} class.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Element extends NestableNode {


    /**
     * <p>
     *   Specifies whether this element was a standalone element at the original template
     *   file, or maybe an open element with a closing tag, or just an open element
     *   (non-XML-well-formed).  
     * </p>
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 2.0.14
     *
     */
    public static enum RepresentationInTemplate {STANDALONE, OPEN_AND_CLOSE_NONEMPTY, OPEN_AND_CLOSE_EMPTY, ONLY_OPEN}
    
    
    
    private static final long serialVersionUID = -8434931215899913983L;

    
    private static final int DEFAULT_ATTRIBUTES_SIZE = 4;
    
    private final String originalName;
    private final String normalizedName;
    private final String normalizedPrefix;
    private final String unprefixedNormalizedName;
    private final boolean hasPrefix;
    
    private final boolean minimizableIfWeb;
    
    private final RepresentationInTemplate representationInTemplate;

    private String[] attributeNormalizedNames = null;
    private Attribute[] attributes = null;
    private int attributesLen = 0;


    
    
    public Element(final String name) {
        this(name, null, null, (RepresentationInTemplate)null);
    }


    public Element(final String name, final String documentName) {
        this(name, documentName, null, (RepresentationInTemplate)null);
    }
    

    public Element(final String name, final String documentName, final Integer lineNumber) {
        this(name, documentName, lineNumber, (RepresentationInTemplate)null);
    }

    
    public Element(final String name, final RepresentationInTemplate representationInTemplate) {
        this(name, null, null, representationInTemplate);
    }


    public Element(final String name, final String documentName, final RepresentationInTemplate representationInTemplate) {
        this(name, documentName, null, representationInTemplate);
    }
    

    public Element(final String name, final String documentName, final Integer lineNumber, final RepresentationInTemplate representationInTemplate) {
        
        super(documentName, lineNumber);
        
        Validate.notNull(name, "Element name cannot be null");
        
        this.originalName = name;
        this.normalizedName = Node.normalizeName(name);
        this.normalizedPrefix = PrefixUtils.getPrefix(this.normalizedName);
        this.unprefixedNormalizedName = PrefixUtils.getUnprefixed(this.normalizedName);
        this.hasPrefix = this.normalizedPrefix != null;
        
        this.minimizableIfWeb = 
                Arrays.binarySearch(Standards.MINIMIZABLE_XHTML_TAGS, this.normalizedName) >= 0;

        this.representationInTemplate = representationInTemplate;
                
    }
    

    
    /**
     * <p>
     *   Returns the original name of the element. Given the fact that the engine
     *   works using the normalized versions of element names (in order to be case-insensitive),
     *   it is necessary to make a distinction between the 'original' name of an element
     *   (as it is written in the template file) and its 'normalized' name. 
     * </p>
     * <p>
     *   If the element name has a prefix, this 'original name' includes such prefix.
     * </p>
     * 
     * @return the original name of the element.
     */
    public String getOriginalName() {
        return this.originalName;
    }

    
    /**
     * <p>
     *   Returns the normalized name of the element. Element names are normalized by
     *   means of the {@link Node#normalizeName(String)} method so that the engine
     *   can work in a case-insensitive way.
     * </p>
     * <p>
     *   If the element name has a prefix, this 'normalized name' includes such prefix.
     * </p>
     * 
     * @return the normalized name of the element.
     */
    public String getNormalizedName() {
        return this.normalizedName;
    }

    
    /**
     * <p>
     *   Returns the normalized prefix of this element (part of its name), if it exists,
     *   or null if the element is unprefixed.
     * </p>
     * <p>
     *   Prefixes are normalized in the same way as element names, using the
     *   {@link Node#normalizeName(String)} method.
     * </p>
     * 
     * @return the normalized prefix.
     */
    public String getNormalizedPrefix() {
        return this.normalizedPrefix;
    }


    /**
     * <p>
     *   Returns the normalized version of the element name, without its prefix
     *   (if it has one).
     * </p>
     * 
     * @return the unprefixed normalized name.
     */
    public String getUnprefixedNormalizedName() {
        return this.unprefixedNormalizedName;
    }


    /**
     * <p>
     *   Returns whether the element name has a prefix or not.
     * </p>
     * 
     * @return true if the element name has a prefix, false if not.
     */
    public boolean hasPrefix() {
        return this.hasPrefix;
    }

    

    /**
     * <p>
     *   Returns whether this element is <i>minimizable</i> when performing
     *   XHTML or HTML5 output.
     * </p>
     * <p>
     *   <i>Minimizing</i> tags means writing them as <tt>&lt;tag/&gt;</tt> instead
     *   of <tt>&lt;tag&gt;&lt;/tag&gt;</tt> when they have no body. XML allows this
     *   for every tag, bug the the XHTML/HTML5 specifications do not allow 
     *   minimizing most of the tags (for example, <tt>&lt;div&gt;</tt>, <tt>&lt;p&gt;</tt>, 
     *   <tt>&lt;textarea&gt;</tt>, etc cannot be minimized). 
     * </p>
     * <p>
     *   This flag precomputes whether the tag can be minimized or not
     *   when written as XHTML or HTML5.
     * </p>
     * 
     * 
     * @return true if the tag is minimizable in web-output scenarios, false if not.
     */
    public boolean isMinimizableIfWeb() {
        return this.minimizableIfWeb;
    }
    
    
    
    
    /**
     * <p>
     *   Optionally specifies whether this element is written at the original document
     *   as a standalone element, an open element with a closing tag, or just an open element
     *   (non-XML-well-formed).
     * </p>
     * <p>
     *   This flag might be ignored by certain parser implementations, and therefore <b>it can 
     *   be null</b>. This can happen when parsers cannot determine the difference between a standalone
     *   or an open+closed element, or when parsers have no support for non-XML-well-formed code.
     *   This can also happen when the document format being parsed is not XML or HTML, and therefore
     *   this flag does not apply.
     * </p>
     * <p>
     *   Note that this flag only influences how the element should be written if there are no changes
     *   in the amount of children it contains. For example, an originally-standalone element to which children
     *   are added will be written as an open plus a close tags (and a body between them containing its children).
     * </p>
     * 
     * @return the original representation of the Element at the template, or null.
     * 
     * @since 2.0.14
     */
    public RepresentationInTemplate getRepresentationInTemplate() {
        return this.representationInTemplate;
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
    public boolean hasAttributes() {
        return this.attributesLen != 0;
    }
    

    /**
     * <p>
     *   Returns the number of attributes contained in this element.
     * </p>
     * 
     * @return the number of attributes.
     */
    public int numAttributes() {
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
    public boolean hasNormalizedAttribute(final String normalizedAttributeName) {
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
    public Attribute getAttributeFromNormalizedName(final String normalizedAttributeName) {
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
    public String[] unsafeGetAttributeNormalizedNames() {
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
    public Attribute[] unsafeGetAttributes() {
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
    public String getAttributeOriginalNameFromNormalizedName(final String normalizedAttributeName) {
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
    public String getAttributeValueFromNormalizedName(final String normalizedAttributeName) {
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
    
    

    /**
     * <p>
     *   Establishes a new value for an element attribute. If the attribute already
     *   exists, its value is substituted by the one specified.
     * </p>
     * 
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    public void setAttribute(final String name, final String value) {
        setAttribute(name, false, value);
    }

    
    
    /**
     * <p>
     *   Establishes a new value for an element attribute. If the attribute already
     *   exists, its value is substituted by the one specified.
     * </p>
     * 
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     * @since 2.0.14
     */
    public void setAttribute(final String name, final boolean onlyName, final String value) {
        
        Validate.notNull(name, "Attribute name cannot be null");
        
        final Attribute attribute = new Attribute(name, onlyName, value);
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
    public void setAttributes(final Map<String,String> newAttributes) {
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
    public void clearAttributes() {
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
    
    

    /**
     * <p>
     *   Clones this element node, setting it a new name in the process.
     * </p>
     * 
     * @param newParent the new parent node to be assigned to the cloned element.
     * @param newElementName the new element name
     * @param cloneProcessors whether the precomputed list of processors should be cloned too or not.
     * @return the cloned element.
     */
    public final Element cloneElementNodeWithNewName(final NestableNode newParent, final String newElementName, final boolean cloneProcessors) {
        final Element clonedElement = new Element(newElementName);
        cloneNodeInternals(clonedElement, newParent, cloneProcessors);
        return clonedElement;
    }
    
    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Element(this.originalName, getDocumentName(), getLineNumber(), this.representationInTemplate);
    }
    

    @Override
    void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final Element element = (Element) node;
        
        if (this.attributesLen > 0) {
            element.attributeNormalizedNames = ArrayUtils.copyOf(this.attributeNormalizedNames, this.attributesLen);
            element.attributes = ArrayUtils.copyOf(this.attributes, this.attributesLen);
            element.attributesLen = this.attributesLen;
        }
        
    }



}
