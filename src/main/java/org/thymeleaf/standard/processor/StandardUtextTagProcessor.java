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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.ParsedFragmentMarkup;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardUtextTagProcessor extends AbstractStandardAttributeTagProcessor {

    public static final int PRECEDENCE = 1400;
    public static final String ATTR_NAME = "utext";

    public StandardUtextTagProcessor() {
        super(ATTR_NAME, PRECEDENCE);
    }



    protected void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

        final IEngineConfiguration configuration = processingContext.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(processingContext, attributeValue);

        final Object result = expression.execute(processingContext, StandardExpressionExecutionContext.UNESCAPED_EXPRESSION);

        final String unescapedText = (result == null ? "" : result.toString());

        /*
         * We will check if there are configured post processors or not. The reason we do this is because output
         * inserted as a result of a th:utext attribute, even if it might be markup, will never be considered as
         * 'processable', i.e. no other processors/inliner will ever be able to act on it. The main reason for this
         * is to protect against code injection.
         *
         * So the only other agents that would be able to modify these th:utext results are POST-PROCESSORS. And
         * they will indeed need markup to have been parsed in order to separate text from structures, so that's why
         * we check if there actually are any post-processors and, if not (most common case), simply output the
         * expression result as if it were a mere (unescaped) text node.
         */
        if (!configuration.hasPostProcessors()) {
            structureHandler.setBody(unescapedText, false);
            return;
        }


        /*
         * We have post-processors, so from here one we will have to decide whether we need to parse the unescaped
         * text or not...
         */

        if (!mightContainStructures(unescapedText)) {
            // If this text contains no markup structures, there would be no need to parse it or treat it as markup!
            structureHandler.setBody(unescapedText, false);
            return;
        }

        final ParsedFragmentMarkup parsedFragment =
                processingContext.getTemplateManager().parseTextualFragment(
                        processingContext.getConfiguration(), processingContext.getTemplateMode(),
                        processingContext.getTemplateResolution().getTemplateName(), unescapedText);

        // Setting 'processable' to false avoiding text inliners processing already generated text,
        // which in turn avoids code injection.
        structureHandler.setBody(parsedFragment, false);

    }


    /*
     * This method will be used for determining if we actually need to apply a parser to the unescaped text that we
     * are going to use a a result of this th:utext execution. If there is no '>' character in it, then it is
     * nothing but a piece of text, and applying the parser would be overkill
     */
    private static boolean mightContainStructures(final String unescapedText) {
        int n = unescapedText.length();
        while (n-- != 0) {
            if (unescapedText.charAt(n) == '>') {
                // Might be the end of a structure!
                return true;
            }
        }
        return false;
    }


}
