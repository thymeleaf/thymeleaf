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
package org.thymeleaf.processor.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IDocumentNodeProcessorMatcher;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;


/**
 * <p>
 *   Base {@link org.thymeleaf.processor.IProcessor} implementation for processors
 *   that should apply to a {@link org.thymeleaf.dom.Document} DOM node (usually
 *   for initializing DOM processing parameters or variables.
 * </p>
 * <p>
 *   One common example of these uses is represented by {@link ProcessAllNodesDocumentProcessor},
 *   which is designed for setting to true the flag that tells the Thymeleaf engine to
 *   process all nodes (and not only nodes of type {@link org.thymeleaf.dom.Element}).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.13
 *
 */
public abstract class AbstractDocumentProcessor extends AbstractProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    private final IDocumentNodeProcessorMatcher matcher; 
    
    
    public AbstractDocumentProcessor(final IDocumentNodeProcessorMatcher matcher) {
        super();
        this.matcher = matcher;
    }


    
    public IProcessorMatcher<? extends Document> getMatcher() {
        return this.matcher;
    }

    
    @Override
    protected final ProcessorResult doProcess(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Processing Document node of type \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), node.getClass().getName()});
        }
        // Because of the type of applicability being used, this cast will not fail
        return processDocumentNode(arguments, (Document)node);
    }
    
    protected abstract ProcessorResult processDocumentNode(final Arguments arguments, final Document documentNode);

}
