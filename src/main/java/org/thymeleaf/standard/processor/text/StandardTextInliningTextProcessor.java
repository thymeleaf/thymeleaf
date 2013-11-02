/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.TextNodeProcessorMatcher;
import org.thymeleaf.processor.document.ProcessAllNodesDocumentProcessor;
import org.thymeleaf.processor.text.AbstractTextNodeProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.inliner.IStandardTextInliner;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class StandardTextInliningTextProcessor 
        extends AbstractTextNodeProcessor {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public static final int ATTR_PRECEDENCE = 100;
    public static final TextNodeProcessorMatcher MATCHER = new TextNodeProcessorMatcher();
    

    
    public StandardTextInliningTextProcessor() {
        super(new TextNodeProcessorMatcher());
    }


    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }


    

    @Override
    public ProcessorResult processTextNode(final Arguments arguments, final AbstractTextNode textNode) {
        
        if (!textNode.getProcessTextNodes()) {
            throw new TemplateProcessingException("Cannot execute text inlining processor: Text processors are not active");
        }
        
        final Object inliner = arguments.getLocalVariable(StandardDialect.INLINER_LOCAL_VARIABLE);
        if (inliner == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("[THYMELEAF][{}][{}] Will not process Text node as inline: no inliner has been set. " +
                        "Please note that setting the 'processOnlyElementNodes' flag to false at a high level in the DOM " +
                        "tree (like for example using " + ProcessAllNodesDocumentProcessor.class.getName() + ") can reduce " +
                		"processing performance in templates with a big amount of Text nodes. Consider setting this flag " +
                        "back to true at some point in your DOM tree to reduce this effect.",
                        new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName()});
            }
            return ProcessorResult.OK;
        }
        if (!(inliner instanceof IStandardTextInliner)) {
            throw new TemplateProcessingException("Cannot execute text inlining processor: Inliner set does not implement " + IStandardTextInliner.class.getName() + 
                    " (it is an object of class " + inliner.getClass().getName() + ")");
        }
        
        ((IStandardTextInliner)inliner).inline(arguments, textNode);
        return ProcessorResult.OK;
        
    }
    

    
}
