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

import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
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
public final class Element extends NestableAttributeHolderNode {


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

    
    private final String originalName;
    private final String normalizedName;
    private final String normalizedPrefix;
    private final String unprefixedNormalizedName;
    private final boolean hasPrefix;
    
    private final boolean minimizableIfWeb;
    
    private final RepresentationInTemplate representationInTemplate;


    
    
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
    void doCloneNestableAttributeHolderNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }



}
