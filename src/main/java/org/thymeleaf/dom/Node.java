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
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.CacheMap;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public abstract class Node {

    protected NestableNode parent;
    private boolean skippable;
    private boolean precomputed;
    
    private Map<String,Object> nodeLocalVariables;

    private static CacheMap<String,String> NORMALIZED_NAMES = 
            new CacheMap<String, String>("Node.normalizedNames", true, 500);
    

    
    public static String normalizeName(final String name) {
        if (name == null) {
            return null;
        }
        final String normalizedName = NORMALIZED_NAMES.get(name);
        if (normalizedName != null) {
            return normalizedName;
        }
        final String newValue = name.toLowerCase();
        NORMALIZED_NAMES.put(name,  newValue);
        return newValue;
    }

    
    public static String applyDialectPrefix(final String name, final String dialectPrefix) {
        if (name == null) {
            return null;
        }
        if (dialectPrefix == null || dialectPrefix.trim().equals("")) {
            return name;
        }
        return dialectPrefix + ":" + name;
    }
    
    
    
    protected Node() {
        super();
        this.skippable = false;
        // Most types of node are not precomputable, so we set this to true as default to avoid
        // constant precomputations
        this.precomputed = true;
        this.nodeLocalVariables = null;
    }
    
    
    public final boolean hasParent() {
        return this.parent != null;
    }
    
    public final NestableNode getParent() {
        return this.parent;
    }
    
    

    public final boolean isSkippable() {
        return this.skippable;
    }
    
    public void setSkippable(final boolean skippable) {
        this.skippable = skippable;
        if (!skippable && this.parent != null) {
            // If this node is marked as non-skippable, set its parent as
            // non-skippable too.
            if (this.parent.isSkippable()) {
                this.parent.setSkippable(false);
            }
        }
    }
    
    protected void setSkippableNode(final boolean skippable) {
        this.skippable = skippable;
    }
    

    
    
    
    public final boolean isPrecomputed() {
        return this.precomputed;
    }
    
    public void setPrecomputed(final boolean precomputed) {
        this.precomputed = precomputed;
    }

    
    
    
    
    public final boolean hasNodeLocalVariables() {
        return this.nodeLocalVariables != null && this.nodeLocalVariables.size() > 0;
    }
    
    public final Map<String,Object> getNodeLocalVariables() {
        return this.nodeLocalVariables;
    }

    public final void addNodeLocalVariable(final String name, final Object value) {
        if (this.nodeLocalVariables == null) {
            this.nodeLocalVariables = new LinkedHashMap<String, Object>();
        }
        this.nodeLocalVariables.put(name,  value);
    }

    public final void addNodeLocalVariables(final Map<String,Object> variables) {
        if (variables != null) {
            for (final Map.Entry<String,Object> variablesEntry : variables.entrySet()) {
                addNodeLocalVariable(variablesEntry.getKey(), variablesEntry.getValue());
            }
        }
    }



    
    public final void precompute(final Configuration configuration) {
        Validate.notNull(configuration, "Configuration cannot be null");
        precomputeNode(configuration);
    }
    
    protected abstract void precomputeNode(final Configuration configuration);

    
    
    
    
    public final void process(final Arguments arguments) {
        Validate.notNull(arguments, "Arguments cannot be null");
        processNode(arguments);
    }

    
    protected final void processNode(final Arguments arguments) {
        
        if (!isPrecomputed()) {
            precomputeNode(arguments.getConfiguration());
        }

        if (!isSkippable()) {
            final Arguments executionArguments =
                    (this.nodeLocalVariables != null && this.nodeLocalVariables.size() > 0?
                            arguments.addLocalVariables(this.nodeLocalVariables) : arguments);
            doProcessNode(executionArguments);
        }
    
    }
    
    protected abstract void doProcessNode(final Arguments arguments);
    
    
    
    public abstract void write(final Arguments arguments, final Writer writer) throws IOException;
    

    
    public final Node cloneNode(final NestableNode newParent, final boolean cloneProcessors) {
        final Node node = doCloneNode(newParent, cloneProcessors);
        node.skippable = this.skippable && cloneProcessors;
        node.precomputed = this.precomputed && cloneProcessors;
        node.parent = newParent;
        if (this.nodeLocalVariables != null) {
            node.nodeLocalVariables = new LinkedHashMap<String, Object>(this.nodeLocalVariables);
        }
        return node;
    }

    
    protected abstract Node doCloneNode(final NestableNode newParent, final boolean cloneProcessors);
    
    
    
    
    
    public static final Node translateDOMNode(final org.w3c.dom.Node domNode, final NestableNode parentNode) {
        
        if (domNode instanceof org.w3c.dom.Element) {
            return Tag.translateDOMTag((org.w3c.dom.Element)domNode, parentNode);
        } else if (domNode instanceof org.w3c.dom.Comment) {
            return Comment.translateDOMComment((org.w3c.dom.Comment)domNode, parentNode);
        } else if (domNode instanceof org.w3c.dom.CDATASection) {
            return CDATASection.translateDOMCDATASection((org.w3c.dom.CDATASection)domNode, parentNode);
        } else if (domNode instanceof org.w3c.dom.Text) {
            return Text.translateDOMText((org.w3c.dom.Text)domNode, parentNode);
        } else {
            throw new IllegalArgumentException(
                    "Node " + domNode.getNodeName() + " of type " + domNode.getNodeType() + 
                    " and class " + domNode.getClass().getName() + " cannot be translated to " +
                    "Thymeleaf's DOM representation.");
        }
        
    }
    
    
}

