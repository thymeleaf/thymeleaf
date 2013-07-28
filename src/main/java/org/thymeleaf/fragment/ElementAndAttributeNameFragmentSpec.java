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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.FragmentSelection;
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
 *
 * @since 2.0.9
 *
 */
public final class ElementAndAttributeNameFragmentSpec implements IFragmentSpec {
    
    private final String elementName;
    private final String attributeName;
    private final String attributeValue;
    private final boolean returnOnlyChildren;

    private final Map<String,Object> parameterValues;

    

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
        this(elementName, attributeName, attributeValue, null, false);
    }


    /**
     * <p>
     *   Create a fragment spec specifying element name and/or attribute name+value.
     * </p>
     * <p>
     *   This constructor allows the specification of a series of fragment parameters, which will be applied
     *   as local variables to the extracted nodes.
     * </p>
     *
     * @param elementName the element name to look for, optional.
     * @param attributeName the attribute name to look for, optional.
     * @param attributeValue the value of the attribute (if attribute name has been specified).
     * @param parameterValues the fragment parameters, which will be applied as local variables to the nodes
     *                        returned as extraction result. Might be null if no parameters are applied.
     *
     * @since 2.1.0
     */
    public ElementAndAttributeNameFragmentSpec(
            final String elementName,
            final String attributeName,
            final String attributeValue,
            final Map<String,Object> parameterValues) {
        this(elementName, attributeName, attributeValue, parameterValues, false);
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
        this(elementName, attributeName, attributeValue, null, returnOnlyChildren);
    }
    
    
    /**
     * <p>
     *   Create a fragment spec specifying element name and/or attribute name+value, and
     *   specifying whether the selected element itself (or selected elements if more than
     *   one) must be returned or only its/their children.
     * </p>
     * <p>
     *   This constructor allows the specification of a series of fragment parameters, which will be applied
     *   as local variables to the extracted nodes.
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
     * @param parameterValues the fragment parameters, which will be applied as local variables to the nodes
     *                        returned as extraction result. Might be null if no parameters are applied.
     * @param returnOnlyChildren whether the selected elements should be returned (false),
     *        or only their children (true).
     *
     * @since 2.1.0
     */
    public ElementAndAttributeNameFragmentSpec(
            final String elementName, 
            final String attributeName, 
            final String attributeValue,
            final Map<String,Object> parameterValues,
            final boolean returnOnlyChildren) {
        
        super();
        
        // Either fragment or element name CAN be null (but not both). If element name is
        // null, no check will be done on the containing element.
        Validate.isTrue(!(StringUtils.isEmptyOrWhitespace(elementName) &&
                          StringUtils.isEmptyOrWhitespace(attributeName)),
                "Either element name of attribute name must not be null or empty");
        
        // If attribute name has been specified, a value must have been specified too.
        if (!StringUtils.isEmptyOrWhitespace(attributeName)) {
            Validate.notEmpty(attributeValue, "Fragment attribute value cannot be null or empty");
        }
        
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.parameterValues = parameterValues;
        this.returnOnlyChildren = returnOnlyChildren;

        if (this.parameterValues != null && this.parameterValues.size() > 0) {
            if (FragmentSelection.parameterNamesAreSynthetic(this.parameterValues.keySet())) {
                throw new TemplateProcessingException(
                        "Cannot process fragment selection parameters " + this.parameterValues.toString() + ", " +
                        "as they are specified for an element name/attribute name+value -based fragment selector " +
                        "(<"+ this.elementName + " " + this.attributeName + "=\"" + this.attributeValue + "\">), " +
                        "but using synthetic (non-named) parameter is only allowed for fragment-signature-based " +
                        "(e.g. 'th:fragment') selection");
            }
        }

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


    /**
     * <p>
     *   Returns the map of parameter values that will be applied as local variables to the extracted nodes.
     * </p>
     *
     * @return the map of parameters.
     * @since 2.1.0
     */
    public Map<String,Object> getParameterValues() {
        return Collections.unmodifiableMap(this.parameterValues);
    }


    /**
     * <p>
     *   Returns whether this fragment specifies parameter values, to be set as local variables into the extracted
     *   nodes.
     * </p>
     *
     * @return true if the fragment spec specifies parameters, false if not
     * @since 2.1.0
     */
    public boolean hasParameterValues() {
        return this.parameterValues != null && this.parameterValues.size() > 0;
    }


    
    
    
    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        
        final List<Node> extraction = 
                DOMUtils.extractFragmentByElementAndAttributeValue(
                        nodes, this.elementName, this.attributeName, this.attributeValue);
        
        if (!this.returnOnlyChildren) {
            applyParameters(extraction, this.parameterValues);
            return extraction;
        }
        
        final List<Node> extractionChildren = new ArrayList<Node>(5);
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

        applyParameters(extractionChildren, this.parameterValues);
        return extractionChildren;
        
    }


    private static void applyParameters(final List<Node> nodes, final Map<String,Object> parameterValues) {
        for (final Node node : nodes) {
            node.setAllNodeLocalVariables(parameterValues);
        }
    }


    
    
    @Override
    public String toString() {
        return "(ELEMENT: " + this.elementName + " | ATTRIBUTE: " + this.attributeName +"=\"" + this.attributeValue + "\")";
    }

    
    
}

