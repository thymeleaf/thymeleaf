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
import org.thymeleaf.processor.DocumentNodeProcessorMatcher;
import org.thymeleaf.processor.IDocumentNodeProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;


/**
 * <p>
 *   Subclass of {@link AbstractDocumentProcessor} used for setting to true the flag 
 *   that tells the Thymeleaf engine to process all nodes (and not only nodes of 
 *   type {@link org.thymeleaf.dom.Element}, which is the default).
 * </p>
 * <p>
 *   This is done by returning a {@link ProcessorResult} instance with its
 *   {@link ProcessorResult#setProcessOnlyElementNodes(boolean)} flag set to false.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.13
 *
 */
public class ProcessAllNodesDocumentProcessor extends AbstractDocumentProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int PRECEDENCE = 100;
    private static final DocumentNodeProcessorMatcher MATCHER = new DocumentNodeProcessorMatcher();

    
    
    public ProcessAllNodesDocumentProcessor() {
        this(MATCHER);
    }

    public ProcessAllNodesDocumentProcessor(final IDocumentNodeProcessorMatcher matcher) {
        super(matcher);
    }

    
    @Override
    public int getPrecedence() {
        return PRECEDENCE;
    }

    
    @Override
    protected ProcessorResult processDocumentNode(final Arguments arguments, final Document documentNode) {
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("[THYMELEAF][{}][{}] Setting flag 'processOnlyElementNodes' to false for " + 
                    "this document. Every node will be considered processable (including Texts, Comments, etc.)",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName()});
        }

        return ProcessorResult.setProcessOnlyElementNodes(false);
        
    }
    
    
}
