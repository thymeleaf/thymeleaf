/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class ExpressionSequenceUtils {




    public static ExpressionSequence parseExpressionSequence(
            final IExpressionContext context, final String input) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");

        final String preprocessedInput =
                StandardExpressionPreprocessor.preprocess(context, input);

        final IEngineConfiguration configuration = context.getConfiguration();

        if (configuration != null) {
            final ExpressionSequence cachedExpressionSequence =
                    ExpressionCache.getExpressionSequenceFromCache(configuration, preprocessedInput);
            if (cachedExpressionSequence != null) {
                return cachedExpressionSequence;
            }
        }

        final ExpressionSequence expressionSequence =
                internalParseExpressionSequence(preprocessedInput.trim());

        if (expressionSequence == null) {
            throw new TemplateProcessingException("Could not parse as expression sequence: \"" + input + "\"");
        }

        if (configuration != null) {
            ExpressionCache.putExpressionSequenceIntoCache(configuration, preprocessedInput, expressionSequence);
        }

        return expressionSequence;

    }



    static ExpressionSequence internalParseExpressionSequence(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final ExpressionParsingState decomposition = ExpressionParsingUtil.decompose(input);

        if (decomposition == null) {
            return null;
        }

        return composeSequence(decomposition, 0);

    }
        
    


    private static ExpressionSequence composeSequence(final ExpressionParsingState state, final int nodeIndex) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            // could happen if we are traversing pointers recursively, so we will consider an expression sequence
            // with one expression only
            final List<IStandardExpression> expressions = new ArrayList<IStandardExpression>(2);
            expressions.add(state.get(nodeIndex).getExpression());
            return new ExpressionSequence(expressions);
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // First, check whether we are just dealing with a pointer input
        final int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeSequence(state, pointer);
        }

        final String[] inputParts = StringUtils.split(input, ",");

        final List<IStandardExpression> expressions = new ArrayList<IStandardExpression>(4);
        for (final String inputPart : inputParts) {
            final Expression expression = ExpressionParsingUtil.parseAndCompose(state, inputPart);
            if (expression == null) {
                return null;
            }
            expressions.add(expression);
        }

        return new ExpressionSequence(expressions);

    }




    private ExpressionSequenceUtils() {
        super();
    }
    
    
}
