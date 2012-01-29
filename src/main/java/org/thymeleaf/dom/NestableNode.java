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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.IdentityCounter;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class NestableNode extends Node {
    
    private static final long serialVersionUID = -5601217853971985055L;

    private static final int DEFAULT_CHILDREN_SIZE = 3;
    
    private Node[] children = null;
    private int childrenLen = 0;
    



    protected NestableNode(final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
    }
    
    

    /*
     * ************************
     * ************************
     *        CHILDREN
     * ************************
     * ************************
     */
    
    
    public final boolean hasChildren() {
        return this.childrenLen != 0;
    }
    
    
    public final int numChildren() {
        return this.childrenLen;
    }
    
    
    public final List<Node> getChildren() {
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(Arrays.copyOf(this.children, this.childrenLen));
    }

    
    public final List<Element> getElementChildren() {
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        final List<Element> elementChildren = new ArrayList<Element>();
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] instanceof Element) {
                elementChildren.add((Element)this.children[i]);
            }
        }
        return Collections.unmodifiableList(elementChildren);
    }
    
    
    public final Node[] unsafeGetChildrenNodeArray() {
        return this.children;
    }
    
    
    public final Node getFirstChild() {
        if (this.childrenLen == 0) {
            return null;
        }
        return this.children[0];
    }

    
    public final Element getFirstElementChild() {
        if (this.childrenLen == 0) {
            return null;
        }
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] instanceof Element) {
                return (Element)this.children[i];
            }
        }
        return null;
    }
    
    
    public void addChild(final Node newChild) {
        
        if (newChild != null) {
            
            if (this.childrenLen == 0) {
                
                this.children = new Node[DEFAULT_CHILDREN_SIZE];
                this.children[0] = newChild;
                this.childrenLen = 1;
                
            } else {
                
                for (int i = 0; i < this.childrenLen; i++) {
                    if (this.children[i] == newChild) {
                        return;
                    }
                }

                if (this.childrenLen >= this.children.length) {
                    final Node[] newChildren = Arrays.copyOf(this.children, this.children.length * 2);
                    this.children = newChildren;
                }
                this.children[this.childrenLen++] = newChild;
                
            }
            
            newChild.parent = this;
        
        }
        
    }
    

    
    
    public final void insertChild(final int index, final Node newChild) {
        
        Validate.isTrue(index >= 0, "Index for inserting child must be >= 0");
        Validate.isTrue(index <= this.childrenLen, "Index for inserting child must be less or equal than size (" + this.childrenLen + ")");
        
        if (newChild != null) {
            
            if (this.childrenLen > 0) {

                /*
                 * If new child is already there, remove it so that it can be
                 * added in its new position.
                 */
                for (int i = 0; i < this.childrenLen; i++) {
                    if (this.children[i] == newChild) {
                        if (i == index) {
                            return;
                        }
                        unsafeRemoveChild(i);
                        break;
                    }
                }

                // childrenLen could have changed, so this should be validated again
                Validate.isTrue(index <= this.childrenLen, "Index for inserting child must be less or equal than size (" + this.childrenLen + ")");
                
            }
            
            if (this.childrenLen == 0) {
                
                this.children = new Node[DEFAULT_CHILDREN_SIZE];
                this.children[0] = newChild;
                this.childrenLen = 1;
                
            } else {

                if (this.childrenLen >= this.children.length) {
                    final Node[] newChildren = Arrays.copyOf(this.children, this.children.length * 2);
                    this.children = newChildren;
                }

                for (int i = this.childrenLen; i > index; i--) {
                    this.children[i] = this.children[i - 1];
                }
                this.children[index] = newChild;
                this.childrenLen++;
                
            }
            
            newChild.parent = this;
        
        }
        
    }


    
    public final void insertBefore(final Node existingChild, final Node newChild) {
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] == existingChild) {
                insertChild(i, newChild);
                return;
            }
        }
        throw new IllegalArgumentException("Child does not exist: cannot execute 'insertBefore' operation");
        
    }


    
    public final void insertAfter(final Node existingChild, final Node newChild) {
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] == existingChild) {
                insertChild(i + 1, newChild);
                return;
            }
        }
        throw new IllegalArgumentException("Child does not exist: cannot execute 'insertAfter' operation");
    }
    

    
    public final void setChildren(final List<Node> newChildren) {
        
        if (this.children != null) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].parent = null;
            }
            this.children = null;
            this.childrenLen = 0;
        }
        
        if (newChildren == null || newChildren.size() == 0) {
            this.children = null;
            this.childrenLen = 0;
        } else {
            for (final Node newChild : newChildren) {
                addChild(newChild);
            }
        }
        
    }
    
    
    public final void clearChildren() {
        if (this.children != null) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].parent = null;
            }
            this.children = null;
            this.childrenLen = 0;
        }
    }
    
    
    
    public final void removeChild(final int index) {
        Validate.isTrue(index >= 0, "Index of child to remove must be >= 0");
        Validate.isTrue(index < this.childrenLen, "Index of child to be removed must be less than size (" + this.childrenLen + ")");
        unsafeRemoveChild(index);
    }
    
    
    final void unsafeRemoveChild(final int index) {
        this.children[index].parent = null;
        for (int i = index + 1; i < this.childrenLen; i++) {
            this.children[i - 1] = this.children[i];
        }
        this.childrenLen--;
    }
    

    
    public final void removeChild(final Node child) {
        Validate.notNull(child, "Child cannot be null");
        unsafeRemoveChild(child);
    }
    
    
    final void unsafeRemoveChild(final Node child) {
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                if (this.children[i] == child) {
                    unsafeRemoveChild(i);
                    return;
                }
            }
        }
    }

    
    public final void moveAllChildren(final NestableNode newParent) {
        
        Validate.notNull(newParent, "New parent cannot be null");
        
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                newParent.addChild(this.children[i]);
            }
            this.children = null;
            this.childrenLen = 0;
        }
        
    }
    
    
    public final void extractChild(final Node child) {
        
        if (child != null) {
            
            if (child instanceof NestableNode) {

                final NestableNode nestableChild = (NestableNode) child;
                final Map<String,Object> nestableChildNodeLocalVariables = 
                        nestableChild.getNodeLocalVariables();
               
                for (int i = 0; i < this.childrenLen; i++) {
                    
                    if (this.children[i] == nestableChild) {
                        unsafeRemoveChild(i);
                        for (int j = 0; j < nestableChild.childrenLen; j++) {
                            insertChild(i + j, nestableChild.children[j]);
                            nestableChild.children[j].addNodeLocalVariables(nestableChildNodeLocalVariables);
                        }
                        return;
                    }
                    
                }
                
            } else {
                unsafeRemoveChild(child);
            }

        }
        
    }



    
    /*
     * ------------
     * PRE-PROCESSING AND EXECUTION
     * ------------
     */
    
    @Override
    final void doAdditionalPrecomputeNode(final Configuration configuration) {
        /*
         * Precompute children
         */
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].precomputeNode(configuration);
            }
        }
        doAdditionalPrecomputeNestableNode(configuration);
    }

    
    
    abstract void doAdditionalPrecomputeNestableNode(final Configuration configuration);
    
    
    
    /*
     * ------------
     * SKIPPABILITY
     * ------------
     */
    
    
    
    @Override
    final void doAdditionalSkippableComputing(final boolean skippable) {
        if (skippable && this.childrenLen > 0) {
            // If this node is marked as skippable, all of its
            // children should be marked skippable too.
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].setSkippable(true);
            }
        }
    }

    
    

    
    /*
     * ------------
     * CLONING
     * ------------
     */
    
    
    
    @Override
    final void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final NestableNode nestableNode = (NestableNode) node;
        
        if (this.childrenLen > 0) {
            final Node[] elementChildren = new Node[this.childrenLen];
            for (int i = 0; i < this.childrenLen; i++) {
                elementChildren[i] = this.children[i].cloneNode(nestableNode, cloneProcessors);
            }
            nestableNode.children = elementChildren;
            nestableNode.childrenLen = elementChildren.length;
        }
        
        doCloneNestableNodeInternals(nestableNode, newParent, cloneProcessors);
    }
    
    
    
    abstract void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors);
    

    

    
    /*
     * ------------
     * PROCESSING
     * ------------
     */
    
    
    
    
    @Override
    final void doAdditionalProcess(final Arguments arguments, final boolean processOnlyElementNodes) {
        if (!isDetached() && this.childrenLen > 0) {
            final IdentityCounter<Node> alreadyProcessed = new IdentityCounter<Node>(this.childrenLen);
            while (!isDetached() && computeNextChild(arguments, this, alreadyProcessed, processOnlyElementNodes)) { /* Nothing to be done here */ }
        }
    }
    

    
    
    private static final boolean computeNextChild(
            final Arguments arguments, final NestableNode node, final IdentityCounter<Node> alreadyProcessed, 
            final boolean processOnlyElementNodes) {
        
        // This method scans the whole array of children each time
        // it tries to execute one so that it executes all sister nodes
        // that might be created by, for example, iteration processors.
        if (node.childrenLen > 0) {
            for (int i = 0; i < node.childrenLen; i++) {
                final Node child = node.children[i];
                if (!alreadyProcessed.isAlreadyCounted(child)) {
                    child.processNode(arguments, processOnlyElementNodes);
                    alreadyProcessed.count(child);
                    return true;
                }
            }
        }
        return false;
        
    }

    
    
    
    
    @Override
    public final void visit(final DOMVisitor visitor) {
        visitor.visit(this);
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].visit(visitor);
            }
        }
    }


    

}
