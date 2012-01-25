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
package org.thymeleaf.processor.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ITextNodeProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractTextNodeProcessor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTextNodeProcessor.class);
    
    private final ITextNodeProcessorMatcher matcher; 
    
    

    public AbstractTextNodeProcessor(final ITextNodeProcessorMatcher matcher) {
        super();
        this.matcher = matcher;
    }

    
    public final IProcessorMatcher<? extends AbstractTextNode> getMatcher() {
        return this.matcher;
    }

    
    @Override
    protected final ProcessorResult doProcess(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node) {
        if (logger.isTraceEnabled()) {
            final String content = ((AbstractTextNode)node).getContent();
            logger.trace("[THYMELEAF][{}][{}] Processing text node of type \"{}\" with content \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), node.getClass().getSimpleName(), content});
        }
        // Because of the type of applicability being used, this cast will not fail
        return processTextNode(arguments, (AbstractTextNode)node);
    }
    
    protected abstract ProcessorResult processTextNode(final Arguments arguments, final AbstractTextNode textNode);
    
    
}
