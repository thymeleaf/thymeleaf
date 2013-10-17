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
package org.thymeleaf.processor.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.ICommentNodeProcessorMatcher;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.15
 *
 */
public abstract class AbstractCommentNodeProcessor extends AbstractProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ICommentNodeProcessorMatcher matcher;



    protected AbstractCommentNodeProcessor(final ICommentNodeProcessorMatcher matcher) {
        super();
        this.matcher = matcher;
    }

    
    public final IProcessorMatcher<? extends Comment> getMatcher() {
        return this.matcher;
    }

    
    @Override
    protected final ProcessorResult doProcess(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node) {
        if (this.logger.isTraceEnabled()) {
            final String content = ((Comment)node).getContent();
            this.logger.trace("[THYMELEAF][{}][{}] Processing Comment node with content \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), node.getClass().getSimpleName(), content});
        }
        // Because of the type of applicability being used, this cast will not fail
        return processCommentNode(arguments, (Comment)node);
    }
    
    protected abstract ProcessorResult processCommentNode(final Arguments arguments, final Comment commentNode);
    
    
}
