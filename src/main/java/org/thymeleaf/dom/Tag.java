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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.processor.ProcessorAndContext;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public final class Tag extends NestableNode {

    private final String name;
    private final String normalizedName;
    
    private final boolean minimizableIfWeb;
    
    private Map<String,String> attributeNames;
    private Map<String,String> attributeValues;
    private int attributesLen;

    private List<ProcessorAndContext> processors;
    
    private boolean hasXmlnsAttributes;


    

    public Tag(final String name) {
        
        super();
        
        Validate.notNull(name, "Tag name cannot be null");
        
        // Tags should not be considered "precomputed" by default
        setPrecomputed(false);
        
        this.name = name;
        this.normalizedName = Node.normalizeName(name);
        this.minimizableIfWeb = 
                Arrays.binarySearch(Standards.MINIMIZABLE_XHTML_TAGS, this.normalizedName) >= 0;
        
        setChildren(null);
        setAttributes(null);
        
        this.processors = null;
        
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
    
    
    public Set<String> getAttributeNames() {
        if (this.attributesLen > 0) {
            return this.attributeValues.keySet();
        }
        return Collections.emptySet();
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
            this.attributeNames = new LinkedHashMap<String,String>();
            this.attributeValues = new LinkedHashMap<String,String>();
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
                this.attributeNames = new LinkedHashMap<String,String>();
                this.attributeValues = new LinkedHashMap<String,String>();
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
     * -------------------
     * PRECOMPUTING
     * -------------------
     */
    
    /*
     * TODO AS IProcessor objects process Nodes, this should be made at Node and not here!!
     */
    
    @Override
    public void precomputeNode(final Configuration configuration) {

        if (!isPrecomputed()) {

            /*
             * Compute the processors that are applicable to this node
             */
            this.processors = configuration.computeProcessorsForNode(this);

            
            /*
             * Set skippability
             */
            if (this.processors == null || this.processors.size() == 0) {
                // We only set this specific node as skippable. If we executed
                // "setSkippable", the whole tree would be set as skippable, which
                // is unnecessary due to the fact that we are going to precompute
                // all of this node's children in a moment.
                setSkippableNode(true);
            } else {
                // This time we execute "setSkippable" so that all parents at all
                // levels are also set to "false"
                setSkippable(false);
            }

            
            /*
             * Set the "precomputed" flag to true 
             */
            setPrecomputed(true);

        }
        
        
        /*
         * Precompute children
         */
        if (this.childrenLen > 0) {
            for (final Node child : this.children) {
                child.precomputeNode(configuration);
            }
        }
     
        
    }

    


    
    /*
     * ---------------
     * NODE PROCESSING
     * ---------------
     */
    
    

    @Override
    protected void doProcessNode(final Arguments arguments) {
        
        Arguments executionArguments = arguments;
        
        if (this.processors != null && this.processors.size() > 0) {
            
            for (final ProcessorAndContext processor : this.processors) {

                if (!hasParent()) {
                    // If a node were removed, it would have no parent --> stop processor execution
                    break;
                }
                    
                /*
                 * TODO refreshing applicability here is not performant. A decision has to be taken between
                 * preprocessing, not doing it, and preprocessing but mark Nodes so that they are not
                 * modified anymore... maybe allow processors to mark nodes for re-processing (second, third...
                 * processing iterations).
                 */
                if (processor.matches(this)) {
                    // We need to check that processor is still applicable, because a previously excecuted
                    // processor could have changed the node's conditions.
                    final ProcessorResult attrProcessorResult = 
                            processor.getProcessor().process(executionArguments, processor.getContext(), this);
                    executionArguments = attrProcessorResult.computeNewArguments(executionArguments);
                    // If we have added local variables, we should update the node's map for them in
                    // order to keep them synchronized
                    if (attrProcessorResult.hasLocalVariables()) {
                        setNodeLocalVariables(executionArguments.getLocalVariables());
                    }
                }
                
            }
            
        }
        
        
        if (hasParent() && this.childrenLen > 0) {
            final Set<Node> alreadyComputed = new HashSet<Node>();
            while (hasParent() && computeNextChild(executionArguments, this, alreadyComputed)) { /* Nothing to be done here */ }
        }
    
        
    }
    

    
    private static boolean computeNextChild(
            final Arguments arguments, final Tag tag, final Set<Node> alreadyComputed) {
        
        // This method scans the whole array of children each time
        // it tries to execute one so that it executes all sister nodes
        // that might be created by, for example, iteration processors.
        if (tag.childrenLen > 0) {
            for (final Node child : tag.children) {
                if (!alreadyComputed.contains(child)) {
                    child.processNode(arguments);
                    alreadyComputed.add(child);
                    return true;
                }
            }
        }
        return false;
        
    }
    
    

    
    /*
     * *********************************
     * *********************************
     *        OUTPUT HANDLING
     * *********************************
     * *********************************
     */
    
    /*
     * TODO ALL LENIENCY ISSUES (tag name and attr name) SHOULD BE CHECKED HERE DURING OUTPUT
     */
    
    @Override
    public void write(final Arguments arguments, final Writer writer) throws IOException {
        writer.write('<');
        writer.write(this.name);
        if (hasAttributes()) {
            for (final String normalizedAttributeName : this.attributeNames.keySet()) {
                boolean writeAttribute = true;
                if (this.hasXmlnsAttributes) {
                    final String prefix = 
                            arguments.getConfiguration().getPrefixIfXmlnsAttribute(normalizedAttributeName);
                    if (prefix != null) {
                        writeAttribute = arguments.getConfiguration().isLenient(prefix);
                    }
                }
                if (writeAttribute) {
                    writer.write(' ');
                    writer.write(this.attributeNames.get(normalizedAttributeName));
                    writer.write('=');
                    writer.write('\"');
                    DOMUtils.writeXmlEscaped(this.attributeValues.get(normalizedAttributeName), writer, true);
                    writer.write('\"');
                }
            }
        }
        if (hasChildren()) {
            writer.write('>');
            for (final Node child : this.children) {
                child.write(arguments, writer);
            }
            writer.write('<');
            writer.write('/');
            writer.write(this.name);
            writer.write('>');
        } else {
            if (arguments.getTemplateResolution().getTemplateMode().isWeb()) {
                if (this.minimizableIfWeb) {
                    writer.write(' ');
                    writer.write('/');
                    writer.write('>');
                } else {
                    writer.write('>');
                    writer.write('<');
                    writer.write('/');
                    writer.write(this.name);
                    writer.write('>');
                }
            } else {
                writer.write('/');
                writer.write('>');
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
    protected Node doCloneNode(final NestableNode newParent, final boolean cloneProcessors) {
        
        final Tag tag = new Tag(this.name);
        
        if (this.attributesLen > 0) {
            tag.attributeNames = new LinkedHashMap<String, String>(this.attributeNames);
            tag.attributeValues = new LinkedHashMap<String, String>(this.attributeValues);
            tag.attributesLen = this.attributesLen;
        }
        tag.hasXmlnsAttributes = this.hasXmlnsAttributes;
        
        if (this.childrenLen > 0) {
            final Node[] tagChildren = new Node[this.childrenLen];
            for (int i = 0; i < this.childrenLen; i++) {
                tagChildren[i] = this.children[i].cloneNode(tag, cloneProcessors);
            }
            tag.setChildren(tagChildren);
        }
        
        if (cloneProcessors) {
            tag.processors = this.processors;
        }
        
        return tag;
        
    }



    
    
    
    public static final Tag translateDOMTag(final org.w3c.dom.Element domNode, final NestableNode parentNode) {
        
        final String tagName = domNode.getTagName();
        final Tag tag = new Tag(tagName);
        tag.parent = parentNode;
        
        final org.w3c.dom.NamedNodeMap attributes = domNode.getAttributes();
        final int attributesLen = attributes.getLength();
        for (int i = 0; i < attributesLen; i++) {
            final org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attributes.item(i);
            tag.setAttribute(attr.getName(), attr.getValue());
        }
        
        final org.w3c.dom.NodeList children = domNode.getChildNodes();
        final int childrenLen = children.getLength();
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            tag.addChild(Node.translateDOMNode(child, tag));
        }
        
        return tag;
        
    }
    
    

    
    
}
