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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   DOM Selectors model selections of subtrees from Thymeleaf DOM trees.
 * </p>
 * <p>
 *   A common use of these selectors is for including fragments of other templates
 *   without the need of these other templates having any Thymeleaf code.
 * </p>
 * <p>
 *   DOM Selector syntax is a subset of XPath, including:
 * </p>
 * <ul>
 *   <li><tt>/x</tt> means <i>direct children of the current node with name <tt>x</tt></i>.</li>
 *   <li><tt>//x</tt> means <i>children of the current node with name <tt>x</tt>, at any depth</i>.</li>
 *   <li><tt>x[@z="v"]</tt> means <i>elements with name <tt>x</tt> and an attribute called z with
 *       value "v"</i>.</li>
 *   <li><tt>x[@z1="v1" and @z2="v2"]</tt> means <i>elements with name <tt>x</tt> and attributes
 *       <tt>z1</tt> and <tt>z2</tt> with values "v1" and "v2", respectively</i>.</li>
 *   <li><tt>x[i]</tt> means <i>element with name <tt>x</tt> positioned in number <tt>i</tt> among
 *       its siblings</i>.</li>
 *   <li><tt>x[@z="v"][i]</tt> means <i>elements with name <tt>x</tt>, attribute <tt>z</tt> with
 *       value "v" and positioned in number <tt>i</tt> among its siblings that also match this
 *       condition</i>.</li>
 * </ul>
 * <p>
 *   For example: <tt>//div[@id="menu"]</tt>.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class DOMSelector implements Serializable {

    private static final long serialVersionUID = -1680336779267140369L;

    
    private static final String selectorPatternStr =
            "(/{1,2})([^/\\s]*?)(?:\\[(.*?)\\](?:\\[(.*?)\\])?)?";
    private static final Pattern selectorPattern =
            Pattern.compile(selectorPatternStr);
    
    private final String selectorExpression;
    private final boolean descendMoreThanOneLevel;
    private final String selectorName;
    private final boolean text;
    private HashMap<String,String> attributes = null;
    private Integer index = null; // will be -1 if last()
    
    private final DOMSelector next;
    

    /**
     * <p>
     *   Creates a new DOM selector specified by the argument selector
     *   expression.
     * </p>
     * 
     * @param selectorExpression the expression specifying the selector to be used.
     */
    public DOMSelector(final String selectorExpression) {
        
        super();

        this.selectorExpression = selectorExpression;
        
        String selectorSpecStr =
            (selectorExpression.trim().startsWith("/")? selectorExpression.trim() : "/" + selectorExpression.trim());
        
        final int selectorSpecStrLen = selectorSpecStr.length();
        int firstNonSlash = 0;
        while (firstNonSlash < selectorSpecStrLen && selectorSpecStr.charAt(firstNonSlash) == '/') {
            firstNonSlash++;
        }
        
        if (firstNonSlash >= selectorSpecStrLen) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": '/' should be followed by a selector name");
        }
        
        final int selEnd = selectorSpecStr.substring(firstNonSlash).indexOf('/');
        if (selEnd != -1) {
            final String tail = selectorSpecStr.substring(firstNonSlash).substring(selEnd);
            selectorSpecStr = selectorSpecStr.substring(0, firstNonSlash + selEnd);
            this.next = new DOMSelector(tail);
        } else {
            this.next = null;
        }

        final Matcher matcher = selectorPattern.matcher(selectorSpecStr);
        if (!matcher.matches()) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
            		"(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        final String rootGroup = matcher.group(1);
        final String selectorNameGroup = matcher.group(2);
        final String index1Group = matcher.group(3);
        final String index2Group = matcher.group(4);
        
        if (rootGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        if ("//".equals(rootGroup)) {
            this.descendMoreThanOneLevel = true;
        } else if ("/".equals(rootGroup)) {
            this.descendMoreThanOneLevel = false;
        } else {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        if (selectorNameGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        this.selectorName = Node.normalizeName(selectorNameGroup);
        this.text = this.selectorName.equals("text()");
        
        if (index1Group != null) {
            
            Integer ind = parseIndex(index1Group);
            if (ind == null) {
                HashMap<String,String> attribs = parseAttributes(selectorExpression, index1Group);
                if (attribs == null) {
                    throw new TemplateProcessingException(
                            "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                            "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                }
                this.attributes = attribs;
            } else {
                this.index = ind;
            }

            if (index2Group != null) {

                if (this.index != null) {
                    throw new TemplateProcessingException(
                            "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                            "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                }
                
                ind = parseIndex(index1Group);
                if (ind == null) {
                    throw new TemplateProcessingException(
                            "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                            "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                }
                this.index = ind;
                
            }
            
            if (this.descendMoreThanOneLevel && this.index != null) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector \"" + selectorExpression + "\": index cannot be specified on a \"descend any levels\" selector (//).");
            }
            
        }
        
    }
    
    
    
    /**
     * <p>
     *   Returns the expression that specifies this DOM selector.
     * </p>
     * 
     * @return the selector expression.
     * @since 2.0.12
     */
    public String getSelectorExpression() {
        return this.selectorExpression;
    }
    
    
    
    private static Integer parseIndex(final String indexGroup) {
        if ("last()".equals(indexGroup.toLowerCase())) {
            return Integer.valueOf(-1);
        }
        try {
            return Integer.valueOf(indexGroup);
        } catch (final Exception e) {
            return null;
        }
    }
    

    
    private static HashMap<String,String> parseAttributes(final String selectorSpec, final String indexGroup) {
        final HashMap<String,String> attributes = new HashMap<String, String>();
        parseAttributes(selectorSpec, attributes, indexGroup);
        return attributes;
    }

    
    private static void parseAttributes(final String selectorSpec, final HashMap<String,String> attributes, final String indexGroup) {
        
        String att = null;
        final int andPos = indexGroup.indexOf(" and "); 
        if (andPos != -1) {
            att = indexGroup.substring(0,andPos);
            final String tail = indexGroup.substring(andPos + 5);
            parseAttributes(selectorSpec, attributes, tail);
        } else {
            att = indexGroup;
        }
            
        parseAttribute(selectorSpec, attributes, att);
        
    }

    
    
    private static void parseAttribute(final String selectorSpec, final HashMap<String,String> attributes, final String attributeSpec) {
        
        final int eqPos = attributeSpec.indexOf("="); 
        if (eqPos != -1) {
            final String attName = attributeSpec.substring(0, eqPos).trim();
            final String attValue = attributeSpec.substring(eqPos + 1).trim();
            if (!attName.startsWith("@")) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            if (!(attValue.startsWith("\"") && attValue.endsWith("\"")) && !(attValue.startsWith("'") && attValue.endsWith("'"))) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            attributes.put(Node.normalizeName(attName.substring(1)), attValue.substring(1, attValue.length() - 1));
        } else {
            final String attName = attributeSpec.trim();
            if (!attName.startsWith("@")) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            attributes.put(Node.normalizeName(attName.substring(1)), null);
        }
    }

    
    
    /**
     * <p>
     *   Executes the DOM selector against the specified node, returning
     *   the result of applying the selector expression.
     * </p>
     * 
     * @param node the node on which the selector will be executed.
     * @return the result of executing the selector.
     */
    public List<Node> select(final Node node) {
        Validate.notNull(node, "Node to be searched cannot be null");
        return select(Collections.singletonList(node));
    }


    /**
     * <p>
     *   Executes the DOM selector against the specified list of nodes,
     *   returning the result of applying the selector expression.
     * </p>
     * 
     * @param nodes the nodes on which the selector will be executed.
     * @return the result of executing the selector.
     */
    public List<Node> select(final List<Node> nodes) {
        Validate.notEmpty(nodes, "Nodes to be searched cannot be null or empty");
        final List<Node> selected = new ArrayList<Node>();
        for (final Node node : nodes) {
            doCheckNodeSelection(selected, node);
        }
        return selected;
    }
    
    
    
    private final boolean checkChildrenSelection(final List<Node> selectedNodes, final Node node) {
        // will return true if any nodes are added to selectedNodes

            if (node instanceof NestableNode) {
                
                final List<List<Node>> selectedNodesForChildren = new ArrayList<List<Node>>();
                
                final NestableNode nestableNode = (NestableNode) node;
                if (nestableNode.hasChildren()) {
                    for (final Node child : nestableNode.getChildren()) {
                        final List<Node> childSelectedNodes = new ArrayList<Node>();
                        if (doCheckNodeSelection(childSelectedNodes, child)) {
                            selectedNodesForChildren.add(childSelectedNodes);
                        }
                    }
                }
                
                if (selectedNodesForChildren.size() == 0) {
                    return false;
                }
                
                if (this.index == null) {
                    for (final List<Node> selectedNodesForChild : selectedNodesForChildren) {
                        selectedNodes.addAll(selectedNodesForChild);
                    }
                    return true;
                }
                    
                // There is an index
                
                if (this.index.intValue() == -1) {
                    selectedNodes.addAll(selectedNodesForChildren.get(selectedNodesForChildren.size() - 1));
                    return true;
                }
                if (this.index.intValue() >= selectedNodesForChildren.size()) {
                    return false;
                }
                selectedNodes.addAll(selectedNodesForChildren.get(this.index.intValue()));
                return true;
                
            }

            return false;
            
    }
    
    
    
    
    private final boolean doCheckNodeSelection(final List<Node> selectedNodes, final Node node) {
        
        if (!doCheckSpecificNodeSelection(node)) {
            
            if (this.descendMoreThanOneLevel) {
                // This level doesn't match, but maybe next levels do...
                
                if (node instanceof NestableNode) {
                    
                    final NestableNode nestableNode = (NestableNode) node;
                    if (nestableNode.hasChildren()) {
                        return checkChildrenSelection(selectedNodes, node);
                    }
                    
                }
                
            }
            
            return false;
            
        }
        
        if (this.next == null) {
            selectedNodes.add(node);
            return true;
        }
        
        if (node instanceof NestableNode) {
            
            final NestableNode nestableNode = (NestableNode) node;
            if (nestableNode.hasChildren()) {
                return this.next.checkChildrenSelection(selectedNodes, node);
            }
            
        }
        
        return false;
        
    }
    
    private final boolean doCheckSpecificNodeSelection(final Node node) {
        
        // This method checks all aspects except index (index can only
        // be applied from the superior level)
        
        if (this.text) {
            return node instanceof AbstractTextNode;
        }
            
        if (node instanceof Element) {
            final Element element = (Element)node;
            final String normalizedName = element.getNormalizedName();
            if (!normalizedName.equals(this.selectorName)) {
                return false;
            }
            if (this.attributes == null || this.attributes.size() == 0) {
                return true;
            }
            for (final Map.Entry<String,String> attributeEntry : this.attributes.entrySet()) {
                final String selectedAttributeName = attributeEntry.getKey();
                final String selectedAttributeValue = attributeEntry.getValue();
                if (selectedAttributeValue == null) {
                    if (!element.hasNormalizedAttribute(selectedAttributeName)) {
                        return false;
                    }
                } else {
                    final String attributeValue = element.getAttributeValueFromNormalizedName(selectedAttributeName);
                    if (attributeValue == null || !attributeValue.equals(selectedAttributeValue)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
        
    }
    
    
    
    
    @Override
    public final String toString() {
        return this.selectorExpression;
    }

   
    
}