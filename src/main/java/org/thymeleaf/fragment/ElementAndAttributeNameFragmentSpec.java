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
package org.thymeleaf.fragment;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;




/**
 * <p>
 *   Fragment specification that extracts a specific element from a Node tree
 *   by its name, or by the value of one of its attributes.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class ElementAndAttributeNameFragmentSpec implements IFragmentSpec {
    
    private final String elementName;
    private final String attributeName;
    private final String attributeValue;
    private final boolean returnOnlyChildren;
    
    
    

    /**
     * <p>
     *   Create a fragment spec specifying element name and/or attribute name+value.
     * </p>
     * 
     * @param elementName the element name to look for, optional.
     * @param attributeName the attribute name to look for, optional.
     * @param attributeValue the value of the attribute (if attribute name has been specified).
     */
    public ElementAndAttributeNameFragmentSpec(
            final String elementName, 
            final String attributeName, 
            final String attributeValue) {
        this(elementName, attributeName, attributeValue, false);
    }

    
    
    /**
     * <p>
     *   Create a fragment spec specifying element name and/or attribute name+value, and
     *   specifying whether the selected element itself (or selected elements if more than
     *   one) must be returned or only its/their children.
     * </p>
     * <p>
     *   If <tt>returnOnlyChildren</tt> is true, the element with the specified name 
     *   and/or containing the specified attribute will be discarded, and only its/their
     *   children will be returned.
     * </p>
     * 
     * @param elementName the element name to look for, optional.
     * @param attributeName the attribute name to look for, optional.
     * @param attributeValue the value of the attribute (if attribute name has been specified).
     * @param returnOnlyChildren whether the selected elements should be returned (false),
     *        or only their children (true).
     * @since 2.0.12
     */
    public ElementAndAttributeNameFragmentSpec(
            final String elementName, 
            final String attributeName, 
            final String attributeValue,
            final boolean returnOnlyChildren) {
        
        super();
        
        // Either fragment or element name CAN be null (but not both). If element name is
        // null, no check will be done on the containing element.
        Validate.isTrue(!(StringUtils.isEmpty(elementName).booleanValue() && 
                          StringUtils.isEmpty(attributeName).booleanValue()), 
                "Either element name of attribute name must not be null or empty");
        
        // If attribute name has been specified, a value must have been specified too.
        if (!StringUtils.isEmpty(attributeName).booleanValue()) {
            Validate.notEmpty(attributeValue, "Fragment attribute value cannot be null or empty");
        }
        
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.returnOnlyChildren = returnOnlyChildren;
        
    }


    /**
     * <p>
     *   Returns the (optional) element name.
     * </p>
     * <p>
     *   If this property is not null, this fragment specification will look for element(s)
     *   with the name specified here (note the <i>element name</i> is the <i>tag name</i>
     *   when processing markup).
     * </p>
     * <p>
     *   Both <tt>elementName</tt> and <tt>attributeName</tt> can be set (non-null), and
     *   at least one of them must be set. 
     * </p>
     * 
     * @return the element name, or null if it has not been set.
     */
    public String getElementName() {
        return this.elementName;
    }

    
    /**
     * <p>
     *   Returns the (optional) attribute name.
     * </p>
     * <p>
     *   If this property is not null, this fragment specification will look for element(s)
     *   containing an attribute with the name specified here.
     * </p>
     * <p>
     *   If this property is specified (not null), <tt>attributeValue</tt> must be specified
     *   too.
     * </p>
     * <p>
     *   Both <tt>elementName</tt> and <tt>attributeName</tt> can be set (non-null), and
     *   at least one of them must be set. 
     * </p>
     * 
     * @return the attribute name, of null if it has not been set.
     */
    public String getAttributeName() {
        return this.attributeName;
    }


    /**
     * <p>
     *   Returns the attribute value, if <tt>attributeName</tt> has been set. If 
     *   <tt>attributeName</tt> is not null, this property should be set a value
     *   too.
     * </p>
     * <p>
     *   This is the value that the attribute specified in <tt>attributeName</tt>
     *   should have in order for an element to be considered <i>extractable</i>
     *   by this fragment specification.
     * </p>
     * <p>
     *   Both <tt>elementName</tt> and <tt>attributeName</tt> can be set (non-null), and
     *   at least one of them must be set. 
     * </p>
     * 
     * @return the attribute value, or null if it has not been set.
     */
    public String getAttributeValue() {
        return this.attributeValue;
    }
    
    
    /**
     * <p>
     *   Returns whether this spec should only return the children of the selected nodes
     *   (<tt>true</tt>) or the selected nodes themselves (<tt>false</tt>, default).
     * </p>
     * 
     * @return whether this spec should only return the children of the selected nodes
     *         or not (default: false).
     * @since 2.0.12
     */
    public boolean isReturnOnlyChildren() {
        return this.returnOnlyChildren;
    }


    
    
    
    public final List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        
        final List<Node> extraction = 
                DOMUtils.extractFragmentByElementAndAttributeValue(
                        nodes, this.elementName, this.attributeName, this.attributeValue);
        
        if (!this.returnOnlyChildren) {
            return extraction;
        }
        
        final List<Node> extractionChildren = new ArrayList<Node>(); 
        for (final Node extractionNode : extraction) {
            
            if (extractionNode == null) {
                continue;
            }
            
            if (!(extractionNode instanceof NestableNode)) {
                throw new TemplateProcessingException(
                        "Cannot correctly retrieve children of node selected by fragment spec " +
                        "with element name \"" + this.elementName + "\", attribute name \"" +
                        this.attributeName + "\" and attribute value \"" + this.attributeValue +
                        "\". Node is not a nestable node (" + extractionNode.getClass().getSimpleName() + ").");
            }
            
            extractionChildren.addAll(((NestableNode)extractionNode).getChildren());
            
        }
        
        return extractionChildren;
        
    }


    
    
    @Override
    public String toString() {
        return "(ELEMENT: " + this.elementName + " | ATTRIBUTE: " + this.attributeName +"=\"" + this.attributeValue + "\")";
    }

    
    
}

