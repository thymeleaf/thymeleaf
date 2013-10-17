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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.IdentityCounter;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Base abstract class for all nodes in a Thymeleaf DOM tree which have
 *   children.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
@SuppressWarnings("ObjectEquality")
public abstract class NestableNode extends Node {
    
    private static final long serialVersionUID = -5601217853971985055L;

    private static final int DEFAULT_CHILDREN_SIZE = 3;
    
    private Node[] children = null;
    private int childrenLen = 0;
    



    NestableNode(final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
    }
    
    

    /*
     * ************************
     * ************************
     *        CHILDREN
     * ************************
     * ************************
     */
    
    
    /**
     * <p>
     *   Returns whether this node has any children.
     * </p>
     * 
     * @return true if the node as any children, false if not.
     */
    public final boolean hasChildren() {
        return this.childrenLen != 0;
    }
    

    /**
     * <p>
     *   Returns the number of children in this node.
     * </p>
     * 
     * @return the number of children.
     */
    public final int numChildren() {
        return this.childrenLen;
    }
    

    /**
     * <p>
     *   Returns the children of this node.
     *   The returned list is immutable.
     * </p>
     * 
     * @return the list of children.
     */
    public final List<Node> getChildren() {
        
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(ArrayUtils.copyOf(this.children, this.childrenLen));
    }


    /**
     * <p>
     *   Returns the only the {@link Element} children
     *   of this node, discarding children of any other types.
     *   The returned list is immutable.
     * </p>
     * 
     * @return the list of Element children.
     */
    public final List<Element> getElementChildren() {
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        final List<Element> elementChildren = new ArrayList<Element>(this.childrenLen + 2);
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] instanceof Element) {
                elementChildren.add((Element)this.children[i]);
            }
        }
        return Collections.unmodifiableList(elementChildren);
    }
    
    
    /**
     * <p>
     *   Returns the real, unsafe, inner array of node children. <b>DO NOT</b>
     *   use this method directly. Modifying this array could result in
     *   severe DOM corruption.
     * </p>
     * 
     * @return the array of node children.
     */
    public final Node[] unsafeGetChildrenNodeArray() {
        return this.children;
    }
    

    /**
     * <p>
     *   Returns the first child of this node.
     * </p>
     * 
     * @return the first child.
     */
    public final Node getFirstChild() {
        if (this.childrenLen == 0) {
            return null;
        }
        return this.children[0];
    }


    /**
     * <p>
     *   Returns the first child of type {@link Element}.
     * </p>
     * 
     * @return the first Element child.
     */
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
    
    
    /**
     * <p>
     *   Adds a new child to the node.
     * </p>
     * 
     * @param newChild the new child to be added.
     */
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
                    this.children = ArrayUtils.copyOf(this.children, this.children.length * 2);
                }
                this.children[this.childrenLen++] = newChild;
                
            }
            
            newChild.parent = this;
        
        }
        
    }
    

    
    /**
     * <p>
     *   Adds a new child to the node, at a specific position.
     * </p>
     * <p>
     *   All children nodes from that position are moved one position
     *   forward in order to make room for the new child.
     * </p>
     * 
     * @param index the position to insert the new child into.
     * @param newChild the new child.
     */
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
                    this.children = ArrayUtils.copyOf(this.children, this.children.length * 2);
                }

                System.arraycopy(this.children, index, this.children, index + 1, (this.childrenLen - index));
                this.children[index] = newChild;
                this.childrenLen++;
                
            }
            
            newChild.parent = this;
        
        }
        
    }



    /**
     * <p>
     *   Adds a new children to the node, positioned just before
     *   another child node that is also specified.
     * </p>
     * <p>
     *   This method is effectively equivalent to first searching
     *   the existing child and then executing {@link #insertChild(int, Node)}
     *   specifying its position.
     * </p>
     * 
     * @param existingChild the child we want to insert the new child just before.
     * @param newChild the new child.
     */
    public final void insertBefore(final Node existingChild, final Node newChild) {
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] == existingChild) {
                insertChild(i, newChild);
                return;
            }
        }
        throw new IllegalArgumentException("Child does not exist: cannot execute 'insertBefore' operation");
        
    }



    /**
     * <p>
     *   Adds a new children to the node, positioned just after
     *   another child node that is also specified.
     * </p>
     * <p>
     *   This method is effectively equivalent to first searching
     *   the existing child and then executing {@link #insertChild(int, Node)}
     *   specifying its position + 1.
     * </p>
     * 
     * @param existingChild the child we want to insert the new child just after.
     * @param newChild the new child.
     */
    public final void insertAfter(final Node existingChild, final Node newChild) {
        for (int i = 0; i < this.childrenLen; i++) {
            if (this.children[i] == existingChild) {
                insertChild(i + 1, newChild);
                return;
            }
        }
        throw new IllegalArgumentException("Child does not exist: cannot execute 'insertAfter' operation");
    }
    


    /**
     * <p>
     *   Sets the new children of the node to the specified list.
     * </p>
     * 
     * @param newChildren the new chidren.
     */
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
    

    /**
     * <p>
     *   Removes all the children nodes.
     * </p>
     */
    public final void clearChildren() {
        if (this.children != null) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].parent = null;
            }
            this.children = null;
            this.childrenLen = 0;
        }
    }
    
    
    /**
     * <p>
     *  Removes a node child at a specific position.
     * </p>
     * 
     * @param index the position to be removed.
     */
    public final void removeChild(final int index) {
        Validate.isTrue(index >= 0, "Index of child to remove must be >= 0");
        Validate.isTrue(index < this.childrenLen, "Index of child to be removed must be less than size (" + this.childrenLen + ")");
        unsafeRemoveChild(index);
    }
    
    
    final void unsafeRemoveChild(final int index) {
        this.children[index].parent = null;
        System.arraycopy(this.children, index + 1, this.children, index, (this.childrenLen - (index + 1)));
        this.childrenLen--;
    }
    

    
    /**
     * <p>
     *   Removes a specific child node from this node.
     * </p>
     * 
     * @param child the child to be removed.
     */
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

    
    /**
     * <p>
     *   Refactors a DOM tree by moving all the children of this node
     *   to another (which will be their new parent node).
     * </p>
     * 
     * @param newParent the new parent.
     */
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
    

    /**
     * <p>
     *   Extracts a child by removing it from the DOM tree and lifting all
     *   of its children one level, so that they become children nodes of
     *   this node.
     * </p>
     * <p>
     *   Node local variables, because of their hierarchical nature, are
     *   handled accordingly.
     * </p>
     * 
     * @param child the child to be extracted.
     */
    public final void extractChild(final Node child) {
        
        if (child != null) {
            
            if (child instanceof NestableNode) {

                final NestableNode nestableChild = (NestableNode) child;
                final NodeLocalVariablesMap nestableChildNodeLocalVariables = 
                        nestableChild.unsafeGetNodeLocalVariables();
               
                for (int i = 0; i < this.childrenLen; i++) {
                    
                    if (this.children[i] == nestableChild) {
                        unsafeRemoveChild(i);
                        for (int j = 0; j < nestableChild.childrenLen; j++) {
                            insertChild(i + j, nestableChild.children[j]);
                            nestableChild.children[j].setAllNodeLocalVariables(nestableChildNodeLocalVariables);
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
     * -------------------------------
     * SKIPPABILITY AND PROCESSABILITY
     * -------------------------------
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
    
    
    
    @Override
    final void doAdditionalProcessableComputing(final boolean processable) {
        if (!processable && this.childrenLen > 0) {
            // If this node is marked as non-processable, all of its
            // children should be marked non-processable too.
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].setProcessable(false);
            }
        }
    }



    @Override
    public void setProcessTextNodes(final boolean processTextNodes) {
        super.setProcessTextNodes(processTextNodes);
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].setProcessTextNodes(processTextNodes);
            }
        }
    }


    @Override
    public void setProcessCommentNodes(final boolean processCommentNodes) {
        super.setProcessCommentNodes(processCommentNodes);
        if (this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                this.children[i].setProcessCommentNodes(processCommentNodes);
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
    final void doAdditionalProcess(final Arguments arguments) {
        if (!isDetached() && this.childrenLen > 0) {
            final IdentityCounter<Node> alreadyProcessed = new IdentityCounter<Node>(this.childrenLen);
            while (!isDetached() && computeNextChild(arguments, this, alreadyProcessed)) { /* Nothing to be done here */ }
        }
    }
    

    
    
    private static boolean computeNextChild(
            final Arguments arguments, final NestableNode node, final IdentityCounter<Node> alreadyProcessed) {
        
        // This method scans the whole array of children each time
        // it tries to execute one so that it executes all sister nodes
        // that might be created by, for example, iteration processors.
        if (node.childrenLen > 0) {
            for (int i = 0; i < node.childrenLen; i++) {
                final Node child = node.children[i];
                if (!alreadyProcessed.isAlreadyCounted(child)) {
                    child.processNode(arguments);
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
