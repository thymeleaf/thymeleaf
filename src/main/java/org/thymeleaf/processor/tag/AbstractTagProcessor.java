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
package org.thymeleaf.processor.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ITagNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.TagNameProcessorMatcher;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractTagProcessor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTagProcessor.class);

    
    private final ITagNameProcessorMatcher matcher; 
    
    
    public AbstractTagProcessor(final String tagName) {
        this(new TagNameProcessorMatcher(tagName));
    }
    
    public AbstractTagProcessor(final ITagNameProcessorMatcher matcher) {
        super();
        this.matcher = matcher;
    }

    
    public IProcessorMatcher<? extends Tag> getMatcher() {
        return this.matcher;
    }

    
    public final ProcessorResult process(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node) {
        // Because of the type of applicability being used, this cast will not fail
        if (logger.isTraceEnabled()) {
            final String tagName = ((Tag)node).getNormalizedName();
            logger.trace("[THYMELEAF][{}][{}] Processing tag \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), tagName});
        }
        return processTag(arguments, (Tag)node);
    }
    
    protected abstract ProcessorResult processTag(final Arguments arguments, final Tag tag);

}
