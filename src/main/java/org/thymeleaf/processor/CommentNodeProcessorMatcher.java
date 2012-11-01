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
package org.thymeleaf.processor;

import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.Node;





/**
 * <p>
 *   Implementation of {@link ICommentNodeProcessorMatcher} that matches every 
 *   {@link org.thymeleaf.dom.Comment} node.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.15
 *
 */
public final class CommentNodeProcessorMatcher implements ICommentNodeProcessorMatcher {
    
    
    
    public CommentNodeProcessorMatcher() {
        super();
    }
    

    public boolean matches(final Node node, final ProcessorMatchingContext context) {
        return (node instanceof Comment);
    }


    
    public final Class<? extends Comment> appliesTo() {
        return Comment.class;
    }
    
    
}
