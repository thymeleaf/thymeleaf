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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public class Comment extends Node {

    private static final char[] COMMENT_PREFIX = "<!--".toCharArray();
    private static final char[] COMMENT_SUFFIX = "-->".toCharArray();
    
    
    private final char[] content;

    
    public Comment(final String content) {
        super();
        Validate.notNull(content, "Comment content cannot be null");
        this.content = content.toCharArray();
    }

    public Comment(final char[] content) {
        super();
        Validate.notNull(content, "Comment content cannot be null");
        this.content = content;
    }
    
    
    public String getContent() {
        return new String(this.content);
    }
    
    char[] unsafeGetContentCharArray() {
        return this.content;
    }

    


    
    @Override
    protected void precomputeNode(final Configuration configuration) {
        // Nothing to be done
    }

    
    


    @Override
    protected void doProcessNode(final Arguments arguments) {
        // Nothing to be done here
    }

    
    
    @Override
    public void write(final Arguments arguments, final Writer writer) throws IOException {
        writer.write(COMMENT_PREFIX);
        writer.write(this.content);
        writer.write(COMMENT_SUFFIX);
    }

    
    
    @Override
    protected Node doCloneNode(final NestableNode newParent, final boolean cloneProcessors) {
        return new Comment(this.content);
    }



    
    
    public static final Comment translateDOMComment(final org.w3c.dom.Comment domNode, final NestableNode parentNode) {
        final Comment comment = new Comment(domNode.getData());
        comment.parent = parentNode;
        return comment;
    }
    

    
}
