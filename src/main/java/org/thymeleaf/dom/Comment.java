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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Comment extends Node {
    
    
    private static final long serialVersionUID = 1750478035496424682L;

    private final char[] content;


    
    public Comment(final String content) {
        this(content, null, null);
    }
    
    
    public Comment(final char[] content) {
        this(content, null, null);
    }
    
    
    public Comment(final String content, final String documentName) {
        this(content, documentName, null);
    }
    
    
    public Comment(final char[] content, final String documentName) {
        this(content, documentName, null);
    }
    
    
    public Comment(final String content, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Comment content cannot be null");
        this.content = content.toCharArray();
    }
    
    
    public Comment(final char[] content, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Comment content cannot be null");
        this.content = content;
    }
    
    
    
    
    public String getContent() {
        return new String(this.content);
    }
    
    public char[] unsafeGetContentCharArray() {
        return this.content;
    }

    


    
    @Override
    final void doAdditionalSkippableComputing(final boolean skippable) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalPrecomputeNode(final Configuration configuration) {
        // Nothing to be done here!
    }


    @Override
    final void doAdditionalProcess(final Arguments arguments, final boolean processOnlyElementNodes) {
        // Nothing to be done here
    }




    @Override
    public final void visit(final DOMVisitor visitor) {
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
