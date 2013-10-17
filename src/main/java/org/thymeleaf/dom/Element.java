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
import java.util.concurrent.ConcurrentHashMap;

import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.util.StringUtils;
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


    // @since 2.1.0
    private static final ConcurrentHashMap<String,ConcurrentHashMap<String,String[]>> prefixedElementNamesByPrefix =
            new ConcurrentHashMap<String, ConcurrentHashMap<String,String[]>>(3);

    // @since 2.1.0
    private static final ConcurrentHashMap<String,String> prefixesByElementName = new ConcurrentHashMap<String,String>(100);

    
    private final String originalName;
    private final String normalizedName;

    private final boolean minimizableIfWeb;
    
    private RepresentationInTemplate representationInTemplate;


    
    
    public Element(final String name) {
        this(name, null, null, null);
    }


    public Element(final String name, final String documentName) {
        this(name, documentName, null, null);
    }
    

    public Element(final String name, final String documentName, final Integer lineNumber) {
        this(name, documentName, lineNumber, null);
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
        this.normalizedName = normalizeElementName(name);

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
     *   means of the {@link #normalizeElementName(String)} method so that the engine
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
     *   Returns whether the element matches its name with any of the specified normalized names.
     * </p>
     *
     * @param dialectPrefix the dialect prefix to be applied to the specified attribute. Can be null.
     * @param normalizedElementName the normalized name of the attribute.
     * @return the normalized name of the element.
     * @since 2.1.0
     */
    public boolean hasNormalizedName(final String dialectPrefix, final String normalizedElementName) {
        final String[] prefixedElementNames =
                Element.applyPrefixToElementName(normalizedElementName, dialectPrefix);
        for (final String prefixedElementName : prefixedElementNames) {
            if (this.normalizedName.equals(prefixedElementName)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * <p>
     *   Returns the normalized prefix of this element (part of its name), if it exists,
     *   or null if the element is unprefixed.
     * </p>
     * <p>
     *   Prefixes are normalized in the same way as element names.
     * </p>
     * 
     * @return the normalized prefix.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public String getNormalizedPrefix() {
        return getPrefixFromElementName(this.normalizedName);
    }


    /**
     * <p>
     *   Returns the normalized version of the element name, without its prefix
     *   (if it has one).
     * </p>
     * 
     * @return the unprefixed normalized name.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public String getUnprefixedNormalizedName() {
        return getUnprefixedElementName(this.normalizedName);
    }


    /**
     * <p>
     *   Returns whether the element name has a prefix or not.
     * </p>
     * 
     * @return true if the element name has a prefix, false if not.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public boolean hasPrefix() {
        final int colonPos = this.normalizedName.indexOf(':');
        return colonPos != -1;
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
    
    
    /**
     * <p>
     *   Sets a new value to the 'representationInTemplate' property.
     * </p>
     * <p>
     *   <b>This method should only be called from parsers</b>, as a result of adjusts on DOM creation
     *   during parsing. It is not intended to be called from processors, as this property
     *   should reflect the way the element was represented at the original template.
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
     * @param representationInTemplate the original representation of the Element at the template, or null.
     * 
     * @since 2.0.15
     */
    public void setRepresentationInTemplate(final RepresentationInTemplate representationInTemplate) {
        this.representationInTemplate = representationInTemplate;
    }
    

    
    
    /*
     * *********************************
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
    public Element cloneElementNodeWithNewName(final NestableNode newParent, final String newElementName, final boolean cloneProcessors) {
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







    /**
     * <p>
     *   Normalizes an element name by converting it
     *   to lower-case. Elements are processed as
     *   case-insensitive, and this method allows normalizing their
     *   names before processing.
     * </p>
     *
     * @param name the name to be normalized.
     * @return the normalized name.
     * @since 2.1.0
     */
    public static String normalizeElementName(final String name) {
        if (name == null) {
            return null;
        }
        return name.toLowerCase();
    }




    /**
     * <p>
     *   Applies a prefix (a dialect prefix) to the specified name in order to obtain a complete
     *   valid element (tag) name.
     * </p>
     * <p>
     *   The result looks like: <tt>"${prefix}:${name}"</tt>.
     * </p>
     *
     * @param name the name to be prefixed.
     * @param dialectPrefix the prefix to be applied.
     * @return the prefixed name.
     * @since 2.1.0
     */
    public static String[] applyPrefixToElementName(final String name, final String dialectPrefix) {

        if (name == null) {
            return null;
        }

        // ConcurrentHashMaps dont allow null keys, so we will use the empty string as equivalent
        final String prefix = (dialectPrefix == null? "" : dialectPrefix);

        ConcurrentHashMap<String,String[]> prefixedElementNamesForPrefix = prefixedElementNamesByPrefix.get(prefix);
        if (prefixedElementNamesForPrefix == null) {
            prefixedElementNamesForPrefix = new ConcurrentHashMap<String, String[]>(100);
            prefixedElementNamesByPrefix.put(prefix, prefixedElementNamesForPrefix);
        }

        String[] prefixedElementNames = prefixedElementNamesForPrefix.get(name);
        if (prefixedElementNames != null) {
            // cache hit!
            return prefixedElementNames;
        }

        if (StringUtils.isEmptyOrWhitespace(prefix)) {
            prefixedElementNames = new String[] { name };
        } else {
            prefixedElementNames = new String[] { prefix + ':' + name, prefix + '-' + name };
        }

        prefixedElementNamesForPrefix.put(name, prefixedElementNames);

        return prefixedElementNames;

    }



    /**
     * <p>
     *   Returns the equivalent, un-prefixed name of an element from its complete (prefixed, if applies) version.
     * </p>
     * <p>
     *   It supports both namespace prefix style (using <tt>:</tt>) and HTML5 custom element style
     *   (using '-' as a separator). Examples: table -> table, th:block -> block, th-block -> block.
     * </p>
     *
     * @param elementName the complete (prefixed, if applies) version of an element name.
     * @return the unprefixed version of the specified element name.
     * @since 2.1.0
     */
    public static String getUnprefixedElementName(final String elementName) {
        if (elementName == null) {
            return null;
        }
        final int colonPos = elementName.indexOf(':');
        if (colonPos != -1) {
            return elementName.substring(colonPos + 1);
        }
        final int dashPos = elementName.indexOf('-');
        if (dashPos != -1) {
            return elementName.substring(dashPos + 1);
        }
        return elementName;
    }




    /**
     * <p>
     *   Returns the prefix being applied to an element.
     * </p>
     *
     * @param elementName the complete (prefixed, if applies) version of an element name.
     * @return the prefix being applied to the name, or null if the element has no prefix.
     * @since 2.1.0
     */
    public static String getPrefixFromElementName(final String elementName) {

        if (elementName == null) {
            return null;
        }

        String prefix = prefixesByElementName.get(elementName);
        if (prefix != null) {
            // cache hit!
            if (prefix.length() == 0) {
                // ConcurrentHashMap objects do not allow null values. So we use "" as a substitute.
                return null;
            }
            return prefix;
        }

        final int colonPos = elementName.indexOf(':');
        if (colonPos != -1) {
            prefix = elementName.substring(0, colonPos);
        } else {
            final int dashPos = elementName.indexOf('-');
            if (dashPos != -1) {
                prefix = elementName.substring(0, dashPos);
            }
        }

        if (prefix == null) {
            // ConcurrentHashMap objects do not allow null values. So we use "" as a substitute.
            prefix = "";
        }

        prefixesByElementName.put(elementName, prefix);

        if (prefix.length() == 0) {
            return null;
        }
        return prefix;

    }


}
