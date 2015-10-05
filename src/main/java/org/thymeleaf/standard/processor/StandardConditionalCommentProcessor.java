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
package org.thymeleaf.standard.processor;

import java.io.StringWriter;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.comment.AbstractCommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.standard.util.StandardConditionalCommentUtils;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardConditionalCommentProcessor extends AbstractCommentProcessor {


    public static final int PRECEDENCE = 1000;



    public StandardConditionalCommentProcessor(final IProcessorDialect dialect) {
        super(dialect, TemplateMode.HTML, PRECEDENCE);
    }



    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext, final IComment comment,
            final ICommentStructureHandler structureHandler) {


        final StandardConditionalCommentUtils.ConditionalCommentParsingResult parsingResult =
                StandardConditionalCommentUtils.parseConditionalComment(comment);

        if (parsingResult == null) {
            // This is NOT a Conditional Comment. Just return
            return;
        }

        final String commentStr = comment.getComment();

        final StringWriter writer = new StringWriter();

        /*
         * Rebuild the conditional comment start expression
         */
        writer.write("[");
        writer.write(commentStr, parsingResult.getStartExpressionOffset(), parsingResult.getStartExpressionLen());
        writer.write("]>");

        /*
         * Next, we need to get the content of the Conditional Comment and process it as a piece of markup. In fact,
         * we must process it as a template itself (a template fragment) so that all thymeleaf attributes and
         * structures inside this content execute correctly, including references to context variables.
         */
        final TemplateManager templateManager = processingContext.getTemplateManager();
        final IEngineConfiguration configuration = processingContext.getConfiguration();

        final String parsableContent =
                configuration.getTextRepository().getText(
                        commentStr, parsingResult.getContentOffset(), parsingResult.getContentOffset() + parsingResult.getContentLen());

        templateManager.processNested(
                processingContext.getConfiguration(),
                comment.getTemplateName(), parsableContent,
                comment.getLine(), comment.getCol(),
                processingContext.getTemplateMode(),
                processingContext.getVariables(),
                writer, true);

        /*
         * Rebuild the conditional comment end expression
         */
        writer.write("<![");
        writer.write(commentStr, parsingResult.getEndExpressionOffset(), parsingResult.getEndExpressionLen());
        writer.write("]");

        /*
         * Re-set the comment content, once processed
         */
        comment.setContent(writer.toString());

    }


}

