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

import org.thymeleaf.Configuration;



/**
 * <p>
 *   A node group, potentially with attributes but no element name (no tag associated).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.8
 *
 */
public final class GroupNode extends NestableAttributeHolderNode {

    private static final long serialVersionUID = -7049056245837086213L;


    
    public GroupNode() {
        this(null, null);
    }


    public GroupNode(final String documentName) {
        this(documentName, null);
    }
    

    public GroupNode(final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
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
    public GroupNode cloneElementNodeWithNewName(final NestableNode newParent, final String newElementName, final boolean cloneProcessors) {
        final GroupNode clonedElement = new GroupNode(newElementName);
        cloneNodeInternals(clonedElement, newParent, cloneProcessors);
        return clonedElement;
    }
    
    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new GroupNode(getDocumentName(), getLineNumber());
    }
    

    @Override
    void doCloneNestableAttributeHolderNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }



}
