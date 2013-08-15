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
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
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
 *   Note this class exists since 2.0.0, but its syntax has been greatly enhanced in 2.1.0.
 * </p>
 * <h3>Selection features</h3>
 * <p>
 *   DOM Selector selection operations are based on:
 * </p>
 * <ul>
 *   <li>The <i><b>type of node</b></i>: both tags (<i>elements</i>) and text nodes can be selected.</li>
 *   <li>The <i><b>name of the element</b></i> (<i>element</i> = <i>tag</i>).</li>
 *   <li>The <i><b>path and depth</b></i> of the node in the DOM tree.</li>
 *   <li>The <i><b>attributes</b></i> of the element (if it is an element).</li>
 *   <li>The <i><b>index</b></i> of the node: its position among its siblings (<i>brother nodes of the same kind</i>).</li>
 *   <li>The <i><b>references</b></i> applied to the node. These references are resolved by means of the
 *       specification of an object implementing {@link INodeReferenceChecker} at selector execution time, and
 *       allow the selection of nodes by features other than the standard ones (name, attributes, etc.).</li>
 * </ul>
 * <h3>Syntax</h3>
 * <p>
 *   DOM Selector syntax is similar to that of XPath or jQuery selectors:
 * </p>
 * <ul>
 *   <li>Paths:
 *     <ul>
 *       <li><tt>/x</tt> means <i>direct children of the current node which either have name <tt>x</tt> or match
 *           reference <tt>x</tt></i>. For example: <tt>/html/body/ul/li</tt>.</li>
 *       <li><tt>//x</tt> means <i>children of the current node which either have name <tt>x</tt> or match
 *           reference <tt>x</tt>, at any depth</i>. For example: <tt>//li</tt>.</li></li>
 *       <li><tt>x</tt> is exactly equivalent to <tt>//x</tt>.</li>
 *       <li><tt>text()</tt> means <i>Text nodes (at the specified level)</i>. For example: <tt>//li/text()</tt>.</li>
 *     </ul>
 *   </li>
 *   <li>Attribute/index modifiers:
 *     <ul>
 *       <li><tt>x[@z='v']</tt> means <i>elements with name <tt>x</tt> and an attribute called z with
 *           value <tt>v</tt></i>.</li>
 *       <li><tt>[@z='v']</tt> means <i>elements with any name and an attribute called z with
 *           value <tt>v</tt></i>.</li>
 *       <li>Other operators are also valid, besides <tt>=</tt> (equal): <tt>!=</tt> (not equal),
 *           <tt>^=</tt> (starts with) and <tt>$=</tt> (ends with). For example:
 *           <tt>x[@class^='section']</tt> means <i>elements with name <tt>x</tt> and a value for
 *           attribute <tt>class</tt> that starts with <tt>section</tt></i>.</li>
 *       <li>Attributes can be specified both starting with <tt>@</tt> (XPath-style) and without
 *           (jQuery-style). So <tt>x[@z='v']</tt> is actually equivalent to <tt>x[z='v']</tt>.</li>
 *       <li><tt>x[@z1='v1' and @z2='v2']</tt> means <i>elements with name <tt>x</tt> and attributes
 *           <tt>z1</tt> and <tt>z2</tt> with values <tt>v1</tt> and <tt>v2</tt>, respectively</i>.</li>
 *       <li>Multiple-attribute modifiers can be joined with <tt>and</tt> (XPath-style) and also by chaining
 *           multiple modifiers (jQuery-style). So <tt>x[@z1='v1' and @z2='v2']</tt> is actually equivalent
 *           to <tt>x[@z1='v1'][@z2='v2']</tt> (and also to <tt>x[z1='v1'][z2='v2']</tt>)</li>
 *       <li><tt>x[i]</tt> means <i>element with name <tt>x</tt> positioned in number <tt>i</tt> among
 *           its siblings</i>. Note index modifiers must always come after all attribute modifiers.</li>
 *       <li><tt>x[@z='v'][i]</tt> means <i>elements with name <tt>x</tt>, attribute <tt>z</tt> with
 *           value <tt>v</tt> and positioned in number <tt>i</tt> among its siblings that also match this
 *           condition</i>.</li>
 *     </ul>
 *   </li>
 *   <li>Direct selectors:
 *     <ul>
 *       <li><tt>x.oneclass</tt> is equivalent to <tt>x[class='oneclass']</tt>.</li>
 *       <li><tt>.oneclass</tt> is equivalent to <tt>[class='oneclass']</tt>.</li>
 *       <li><tt>x#oneid</tt> is equivalent to <tt>x[id='oneid']</tt>.</li>
 *       <li><tt>#oneid</tt> is equivalent to <tt>[id='oneid']</tt>.</li>
 *       <li><tt>x%oneref</tt> means <i>nodes -not just elements- with name <tt>x</tt> that match reference <tt>oneref</tt> according to
 *           the specified {@link INodeReferenceChecker} implementation.</i>.</li>
 *       <li><tt>%oneref</tt> means <i>nodes -not just elements- with any name that match reference <tt>oneref</tt> according to
 *           the specified {@link INodeReferenceChecker} implementation.</i>. Note this is actually equivalent
 *           to simply <tt>oneref</tt> because references can be used instead of element names, as explained above.</li>
 *       <li>Direct selectors and attribute selectors can be mixed: <tt>a.external[@href^='https']</tt>.</li>
 *     </ul>
 *   </li>
 *   <li>Specific features:
 *     <ul>
 *       <li>DOM Selectors understand the <tt>class</tt> attribute to be multivalued, and therefore allow
 *           the application of modifiers on this attribute even if the element has several class values. For example,
 *           <tt>x[class='two']</tt> will match <tt>&lt;x class="one two three"/&gt;</tt>.</li>
 *     </ul>
 *   </li>
 * </ul>
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

    private static final String TEXT_SELECTOR = "text()";
    private static final String ID_MODIFIER_SEPARATOR = "#";
    private static final String CLASS_MODIFIER_SEPARATOR = ".";
    private static final String REFERENCE_MODIFIER_SEPARATOR = "%";

    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String CLASS_ATTRIBUTE_NAME = "class";

    private static final String selectorPatternStr = "^(/{1,2})([^/\\s]*?)(\\[(?:.*)\\])?$";
    private static final Pattern selectorPattern = Pattern.compile(selectorPatternStr);
    private static final String modifiersPatternStr = "^(?:\\[(.*?)\\])(\\[(?:.*)\\])?$";
    private static final Pattern modifiersPattern = Pattern.compile(modifiersPatternStr);

    private final String selectorExpression;
    private final boolean descendMoreThanOneLevel;

    private final String selectorPath;
    private final String selectorPathIdModifier;
    private final String selectorPathClassModifier;
    private final String selectorPathReferenceModifier;
    private final boolean text;
    private List<AttributeCondition> attributes = null;
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
        this(selectorExpression, true);
    }



    private DOMSelector(final String selectorExpression, final boolean atRootLevel) {

        super();

        /*
         * STRATEGY: We will divide the DOM Selector into several, one for each level, and chain them all using the
         * 'next' property. That way, a '/x//y[0]/z[@id='a']' selector will be divided into three chained selectors,
         * like: '/x' -(next)-> '//y[0]' -(next)-> '/z[@id='a']'
         */

        this.selectorExpression = selectorExpression;

        String selectorSpecStr = selectorExpression.trim();
        if (atRootLevel) {
            if (!selectorSpecStr.startsWith("/")) {
                // "x" is equivalent to "//x"
                selectorSpecStr = "//" + selectorSpecStr;
            }
        } // if we are not at root level, expression will always start with "/", and that's fine.

        final int selectorSpecStrLen = selectorSpecStr.length();
        int firstNonSlash = 0;
        while (firstNonSlash < selectorSpecStrLen && selectorSpecStr.charAt(firstNonSlash) == '/') {
            firstNonSlash++;
        }
        
        if (firstNonSlash >= selectorSpecStrLen) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": '/' should be followed by " +
                    "further selector specification");
        }
        
        final int selEnd = selectorSpecStr.substring(firstNonSlash).indexOf('/');
        if (selEnd != -1) {
            final String tail = selectorSpecStr.substring(firstNonSlash).substring(selEnd);
            selectorSpecStr = selectorSpecStr.substring(0, firstNonSlash + selEnd);
            this.next = new DOMSelector(tail, false);
        } else {
            this.next = null;
        }

        final Matcher matcher = selectorPattern.matcher(selectorSpecStr);
        if (!matcher.matches()) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
            		"((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        final String rootGroup = matcher.group(1);
        final String selectorNameGroup = matcher.group(2);
        final String modifiersGroup = matcher.group(3);

        if (rootGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        if ("//".equals(rootGroup)) {
            this.descendMoreThanOneLevel = true;
        } else if ("/".equals(rootGroup)) {
            this.descendMoreThanOneLevel = false;
        } else {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }
        
        if (selectorNameGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                    "((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
        }


        /*
         * Process path: extract id, class, reference modifiers...
         */

        String path = Node.normalizeName(selectorNameGroup);

        final int idModifierPos = path.indexOf(ID_MODIFIER_SEPARATOR);
        final int classModifierPos = path.indexOf(CLASS_MODIFIER_SEPARATOR);
        final int referenceModifierPos = path.indexOf(REFERENCE_MODIFIER_SEPARATOR);

        if (idModifierPos != -1) {
            if (classModifierPos != -1 || referenceModifierPos != -1) {
                throw new TemplateProcessingException(
                        "More than one modifier (id, class, reference) have been specified at " +
                        "DOM selector expression \"" + this.selectorExpression + "\", which is forbidden.");
            }
            this.selectorPathIdModifier = path.substring(idModifierPos + ID_MODIFIER_SEPARATOR.length());
            path = path.substring(0, idModifierPos);
            if (StringUtils.isEmptyOrWhitespace(this.selectorPathIdModifier)) {
                throw new TemplateProcessingException(
                        "Empty id modifier in DOM selector expression " +
                        "\"" + this.selectorExpression + "\", which is forbidden.");
            }
        } else {
            this.selectorPathIdModifier = null;
        }

        if (classModifierPos != -1) {
            if (idModifierPos != -1 || referenceModifierPos != -1) {
                throw new TemplateProcessingException(
                        "More than one modifier (id, class, reference) have been specified at " +
                                "DOM selector expression \"" + this.selectorExpression + "\", which is forbidden.");
            }
            this.selectorPathClassModifier = path.substring(classModifierPos + CLASS_MODIFIER_SEPARATOR.length());
            path = path.substring(0, classModifierPos);
            if (StringUtils.isEmptyOrWhitespace(this.selectorPathClassModifier)) {
                throw new TemplateProcessingException(
                        "Empty id modifier in DOM selector expression " +
                                "\"" + this.selectorExpression + "\", which is forbidden.");
            }
        } else {
            this.selectorPathClassModifier = null;
        }

        if (referenceModifierPos != -1) {
            if (idModifierPos != -1 || classModifierPos != -1) {
                throw new TemplateProcessingException(
                        "More than one modifier (id, class, reference) have been specified at " +
                                "DOM selector expression \"" + this.selectorExpression + "\", which is forbidden.");
            }
            this.selectorPathReferenceModifier = path.substring(referenceModifierPos + REFERENCE_MODIFIER_SEPARATOR.length());
            path = path.substring(0, referenceModifierPos);
            if (StringUtils.isEmptyOrWhitespace(this.selectorPathReferenceModifier)) {
                throw new TemplateProcessingException(
                        "Empty id modifier in DOM selector expression " +
                                "\"" + this.selectorExpression + "\", which is forbidden.");
            }
        } else {
            this.selectorPathReferenceModifier = null;
        }

        this.selectorPath = path;
        this.text = TEXT_SELECTOR.equals(this.selectorPath);


        /*
         * Process classifiers: attributes and index.
         */

        if (modifiersGroup != null) {

            /*
             * A selector level can include two types of filters between [...], in this order:
             *   * 1. Attribute based: [@a='X' and @b='Y'], any number of them: [@a='X'][@b='Y']...
             *   * 2. Index based: [23]
             */

            String remainingModifiers = modifiersGroup;

            while (remainingModifiers != null) {

                // This pattern is made to be recursive, acting group 2 as the recursion tail
                final Matcher modifiersMatcher = modifiersPattern.matcher(remainingModifiers);
                if (!modifiersMatcher.matches()) {
                    throw new TemplateProcessingException(
                            "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                                    "((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                }

                final String currentModifier = modifiersMatcher.group(1);
                remainingModifiers = modifiersMatcher.group(2);

                final Integer modifierAsIndex = parseIndex(currentModifier);

                if (modifierAsIndex != null) {

                    this.index = modifierAsIndex;
                    if (remainingModifiers != null) {
                        // If this is an index, it must be the last modifier!
                        throw new TemplateProcessingException(
                                "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                                        "((/|//)?selector)?([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                    }

                } else {
                    // Modifier is not an index

                    final List<AttributeCondition> attribs = parseAttributes(selectorExpression, currentModifier);
                    if (attribs == null) {
                        throw new TemplateProcessingException(
                                "Invalid syntax in DOM selector \"" + selectorExpression + "\": selector does not match selector syntax: " +
                                        "(/|//)(selector)([@attrib=\"value\" (and @attrib2=\"value\")?])?([index])?");
                    }

                    if (this.attributes == null) {
                        // This is done to save an object. The method that creates the "attribs" list is completely
                        // under our control, so there should be no problem.
                        this.attributes = attribs;
                    } else {
                        this.attributes.addAll(attribs);
                    }

                }

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
        } catch (final Exception ignored) {
            return null;
        }
    }
    

    
    private static List<AttributeCondition> parseAttributes(final String selectorSpec, final String indexGroup) {
        final List<AttributeCondition> attributes = new ArrayList<AttributeCondition>(3);
        parseAttributes(selectorSpec, attributes, indexGroup);
        return attributes;
    }

    
    private static void parseAttributes(final String selectorSpec, final List<AttributeCondition> attributes, final String indexGroup) {
        
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

    
    
    private static void parseAttribute(final String selectorSpec, final List<AttributeCondition> attributes, final String attributeSpec) {

        // 0 = attribute name, 1 = operator, 2 = value
        final String[] fragments = AttributeCondition.Operator.extractOperator(attributeSpec);

        if (fragments[1] != null) {
            // There is an operator

            String attrName = fragments[0];
            final AttributeCondition.Operator operator = AttributeCondition.Operator.parse(fragments[1]);
            final String attrValue = fragments[2];
            if (attrName.startsWith("@")) {
                attrName = attrName.substring(1);
            }
            if (!(attrValue.startsWith("\"") && attrValue.endsWith("\"")) && !(attrValue.startsWith("'") && attrValue.endsWith("'"))) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            attributes.add(new AttributeCondition(Node.normalizeName(attrName), operator, attrValue.substring(1, attrValue.length() - 1)));

        } else {
            // There is NO operator

            String attrName = fragments[0];
            if (attrName.startsWith("@")) {
                attrName = attrName.substring(1);
            }
            attributes.add(new AttributeCondition(Node.normalizeName(attrName), null, null));

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
        return select(Collections.singletonList(node), null);
    }


    /**
     * <p>
     *   Executes the DOM selector against the specified node and
     *   using the specified reference checker (if references are used in the DOM selector
     *   expression).
     * </p>
     *
     * @param node the node on which the selector will be executed.
     * @param referenceChecker the checker that will be used to compute whether a Node matches or not
     *        a specified reference. Can be null.
     * @return the result of executing the selector.
     * @since 2.1.0
     */
    public List<Node> select(final Node node, final INodeReferenceChecker referenceChecker) {
        Validate.notNull(node, "Node to be searched cannot be null");
        return select(Collections.singletonList(node), referenceChecker);
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
        return select(nodes, null);
    }


    /**
     * <p>
     *   Executes the DOM selector against the specified list of nodes and
     *   using the specified reference checker (if references are used in the DOM selector
     *   expression).
     * </p>
     *
     * @param nodes the nodes on which the selector will be executed.
     * @param referenceChecker the checker that will be used to compute whether a Node matches or not
     *        a specified reference. Can be null.
     * @return the result of executing the selector.
     * @since 2.1.0
     */
    public List<Node> select(final List<Node> nodes, final INodeReferenceChecker referenceChecker) {

        Validate.notEmpty(nodes, "Nodes to be searched cannot be null or empty");

        final List<Node> selected = new ArrayList<Node>(10);
        for (final Node node : nodes) {
            doCheckNodeSelection(selected, node, referenceChecker);
        }

        return selected;

    }




    
    private boolean checkChildrenSelection(final List<Node> selectedNodes,
            final Node node, final INodeReferenceChecker referenceChecker) {
        // will return true if any nodes are added to selectedNodes

        if (node instanceof NestableNode) {

            final List<List<Node>> selectedNodesForChildren = new ArrayList<List<Node>>(10);

            final NestableNode nestableNode = (NestableNode) node;
            if (nestableNode.hasChildren()) {
                for (final Node child : nestableNode.getChildren()) {
                    final List<Node> childSelectedNodes = new ArrayList<Node>(10);
                    if (doCheckNodeSelection(childSelectedNodes, child, referenceChecker)) {
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
    
    
    
    
    private boolean doCheckNodeSelection(final List<Node> selectedNodes,
            final Node node, final INodeReferenceChecker referenceChecker) {

        if (!doCheckSpecificNodeSelection(node, referenceChecker)) {
            
            if (this.descendMoreThanOneLevel || node instanceof Document || node instanceof GroupNode) {
                // This level doesn't match, but maybe next levels do...
                
                if (node instanceof NestableNode) {
                    
                    final NestableNode nestableNode = (NestableNode) node;
                    if (nestableNode.hasChildren()) {
                        return checkChildrenSelection(selectedNodes, node, referenceChecker);
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
                return this.next.checkChildrenSelection(selectedNodes, node, referenceChecker);
            }
            
        }
        
        return false;
        
    }
    
    private boolean doCheckSpecificNodeSelection(final Node node, final INodeReferenceChecker referenceChecker) {
        
        // This method checks all aspects except index (index can only
        // be applied from the superior level)

        if (node instanceof Element) {

            final Element element = (Element)node;

            if (this.selectorPathIdModifier != null) {
                if (!checkPathWithIdModifier(element)) {
                    return false;
                }
            } else if (this.selectorPathClassModifier != null) {
                if (!checkPathWithClassModifier(element)) {
                    return false;
                }
            } else if (this.selectorPathReferenceModifier != null) {
                if (!checkPathWithReferenceModifier(element, referenceChecker)) {
                    return false;
                }
            } else {
                if (!checkPathWithoutModifiers(element, referenceChecker)) {
                    return false;
                }
            }

            if (this.attributes == null || this.attributes.size() == 0) {
                return true;
            }

            for (final AttributeCondition attributeCondition : this.attributes) {
                final String selectedAttributeName = attributeCondition.getName();
                final boolean selectedAttributeMultipe = CLASS_ATTRIBUTE_NAME.equals(selectedAttributeName);
                if (!checkAttributeValue(element, selectedAttributeName,
                                         attributeCondition.getOperator(), attributeCondition.getValue(),
                                         selectedAttributeMultipe)) {
                    return false;
                }
            }

            return true;

        } else if (node instanceof AbstractTextNode) {
            if (referenceChecker != null) {
                return this.text && referenceChecker.checkReference(node, this.selectorPath);
            }
            return this.text;
        } else {
            if (referenceChecker != null) {
                return referenceChecker.checkReference(node, this.selectorPath);
            }
        }

        return false;
        
    }




    private static boolean checkAttributeValue(final NestableAttributeHolderNode node,
            final String attributeName, final AttributeCondition.Operator operator, final String attributeValue,
            final boolean multivalued) {

        if (!node.hasNormalizedAttribute(attributeName)) {
            if (attributeValue == null) {
                return operator == AttributeCondition.Operator.EQUALS;
            }
            return operator == AttributeCondition.Operator.NOT_EQUALS;
        }

        final String nodeAttributeValue = node.getAttributeValueFromNormalizedName(attributeName);

        if (nodeAttributeValue == null) {
            if (attributeValue == null) {
                return operator == AttributeCondition.Operator.EQUALS;
            }
            return operator == AttributeCondition.Operator.NOT_EQUALS;
        } else if (attributeValue == null) {
            return operator == AttributeCondition.Operator.NOT_EQUALS;
        }

        if (!multivalued) {

            switch (operator) {
                case EQUALS:
                    return nodeAttributeValue.equals(attributeValue);
                case NOT_EQUALS:
                    return !nodeAttributeValue.equals(attributeValue);
                case STARTS_WITH:
                    return nodeAttributeValue.startsWith(attributeValue);
                case ENDS_WITH:
                    return nodeAttributeValue.endsWith(attributeValue);
            }

        }

        // Attribute IS multivalued

        if ((operator.equals(AttributeCondition.Operator.EQUALS) || operator.equals(AttributeCondition.Operator.NOT_EQUALS))
                && !nodeAttributeValue.contains(attributeValue)) {
            // If it is equals/not equals, value must appear as a whole (not a prefix, suffix). If not, we can return.
            return operator.equals(AttributeCondition.Operator.NOT_EQUALS);
        }

        final StringTokenizer nodeAttrValueTokenizer = new StringTokenizer(nodeAttributeValue, ", ");
        while (nodeAttrValueTokenizer.hasMoreTokens()) {
            final String nodeAttrValueToken = nodeAttrValueTokenizer.nextToken();
            switch (operator) {
                case EQUALS:
                    if (nodeAttrValueToken.equals(attributeValue)) {
                        return true;
                    }
                    break;
                case NOT_EQUALS:
                    if (!nodeAttrValueToken.equals(attributeValue)) {
                        return true;
                    }
                    break;
                case STARTS_WITH:
                    if (nodeAttrValueToken.startsWith(attributeValue)) {
                        return true;
                    }
                    break;
                case ENDS_WITH:
                    if (nodeAttrValueToken.endsWith(attributeValue)) {
                        return true;
                    }
                    break;
            }
        }

        return false;

    }



    private boolean checkPathWithIdModifier(final Element element) {

        if (this.selectorPathIdModifier == null) {
            return false;
        }

        final String elementName = element.getNormalizedName();

        if (!StringUtils.isEmptyOrWhitespace(this.selectorPath)) {
            if (!this.selectorPath.equals(elementName)) {
                return false;
            }

        }
        // Checking the element name went OK, so lets check the ID
        return checkAttributeValue(element, ID_ATTRIBUTE_NAME, AttributeCondition.Operator.EQUALS, this.selectorPathIdModifier, false);

    }


    private boolean checkPathWithClassModifier(final Element element) {

        if (this.selectorPathClassModifier == null) {
            return false;
        }

        final String elementName = element.getNormalizedName();

        if (!StringUtils.isEmptyOrWhitespace(this.selectorPath)) {
            if (!this.selectorPath.equals(elementName)) {
                return false;
            }

        }
        // Checking the element name went OK, so lets check the class
        return checkAttributeValue(element, CLASS_ATTRIBUTE_NAME, AttributeCondition.Operator.EQUALS, this.selectorPathClassModifier, true);

    }


    private boolean checkPathWithReferenceModifier(final Element element, final INodeReferenceChecker referenceChecker) {

        if (this.selectorPathReferenceModifier == null || referenceChecker == null) {
            // First one being null never happen, as we should never call this method if modifier is null
            return false;
        }

        final String elementName = element.getNormalizedName();

        if (!StringUtils.isEmptyOrWhitespace(this.selectorPath)) {
            if (!this.selectorPath.equals(elementName)) {
                return false;
            }

        }
        // Checking the element name went OK, so lets check the reference
        return referenceChecker.checkReference(element, this.selectorPathReferenceModifier);

    }


    private boolean checkPathWithoutModifiers(final Element element, final INodeReferenceChecker referenceChecker) {

        final String elementName = element.getNormalizedName();
        if (!StringUtils.isEmptyOrWhitespace(this.selectorPath)) {
            if (!this.selectorPath.equals(elementName)) {
                if (referenceChecker == null) {
                    return false;
                }
                return referenceChecker.checkReference(element, this.selectorPath);
            }
        }
        // We don't have any reasons to deny it matches
        return true;

    }


    
    
    @Override
    public String toString() {
        return this.selectorExpression;
    }





    private static final class AttributeCondition {

        static enum Operator {
                EQUALS, NOT_EQUALS, STARTS_WITH, ENDS_WITH;

                static Operator parse(final String operatorStr) {
                    if (operatorStr == null) {
                        return null;
                    }
                    if (operatorStr.equals("=")) {
                        return EQUALS;
                    }
                    if (operatorStr.equals("!=")) {
                        return NOT_EQUALS;
                    }
                    if (operatorStr.equals("^=")) {
                        return STARTS_WITH;
                    }
                    if (operatorStr.equals("$=")) {
                        return ENDS_WITH;
                    }
                    return null;
                }

                static String[] extractOperator(final String specification) {
                    final int equalsPos = specification.indexOf('=');
                    if (equalsPos == -1) {
                        return new String[] {specification.trim(), null, null};
                    }
                    final char cprev = specification.charAt(equalsPos - 1);
                    switch (cprev) {
                        case '!':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "!=",
                                    specification.substring(equalsPos + 1).trim()};
                        case '^':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "^=",
                                    specification.substring(equalsPos + 1).trim()};
                        case '$':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "$=",
                                    specification.substring(equalsPos + 1).trim()};
                        default:
                            return new String[] {
                                    specification.substring(0, equalsPos).trim(), "=",
                                    specification.substring(equalsPos + 1).trim()};
                    }
                }

            }


        private final String name;
        private final Operator operator;
        private final String value;

        AttributeCondition(final String name, final Operator operator, final String value) {
            super();
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        private String getName() {
            return this.name;
        }

        private Operator getOperator() {
            return operator;
        }

        private String getValue() {
            return value;
        }

    }


   



    public static interface INodeReferenceChecker {

        public boolean checkReference(final Node node, final String referenceValue);

    }


    public static abstract class AbstractNodeReferenceChecker implements INodeReferenceChecker {

        protected AbstractNodeReferenceChecker() {
            super();
        }

    }


    public static final class AggregatingNodeReferenceChecker extends AbstractNodeReferenceChecker {

        private final INodeReferenceChecker one;
        private final INodeReferenceChecker two;

        public AggregatingNodeReferenceChecker(final INodeReferenceChecker one, final INodeReferenceChecker two) {
            super();
            Validate.notNull(one, "Reference checker one cannot be null");
            Validate.notNull(two, "Reference checker two cannot be null");
            this.one = one;
            this.two = two;
        }

        public boolean checkReference(final Node node, final String referenceValue) {
            if (this.one.checkReference(node, referenceValue)) {
                return true;
            }
            return this.two.checkReference(node, referenceValue);
        }

    }

}