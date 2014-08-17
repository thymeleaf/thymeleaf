/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   A Comment node in a Thymeleaf DOM tree.
 * </p>
 * 
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Comment extends Node {
    
    
    private static final long serialVersionUID = 1750478035496424682L;

    private String content;


    
    public Comment(final String content) {
        this(content, null, null);
    }

    
    public Comment(final String content, final String documentName) {
        this(content, documentName, null);
    }

    
    public Comment(final String content, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Comment content cannot be null");
        this.content = content;
    }

    
    
    
    /**
     * <p>
     *   Returns the textual content of this node, as a String.
     * </p>
     * 
     * @return the textual content of this node.
     */
    public String getContent() {
        return this.content;
    }


    /**
     * <p>
     *   Modify the textual content of this node.
     * </p>
     * 
     * @param content the new content
     * @since 2.0.15
     */
    public void setContent(final String content) {
        this.content = content;
    }

    


    
    @Override
    void doAdditionalSkippableComputing(final boolean skippable) {
        // Nothing to be done here!
    }


    @Override
    void doAdditionalProcessableComputing(final boolean processable) {
        // Nothing to be done here!
    }

    
    
    @Override
    void doAdditionalPrecomputeNode(final Configuration configuration) {
        // Nothing to be done here!
    }


    @Override
    void doAdditionalProcess(final Arguments arguments) {
        // Nothing to be done here
    }




    @Override
    public void visit(final DOMVisitor visitor) {
        visitor.visit(this);
    }
    
    
    
    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Comment(this.content);
    }
    

    
    
    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }

    
}
