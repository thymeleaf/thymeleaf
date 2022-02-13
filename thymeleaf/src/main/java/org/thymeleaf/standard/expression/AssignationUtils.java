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
public final class AssignationUtils {




    public static AssignationSequence parseAssignationSequence(
            final IExpressionContext context,
            final String input, final boolean allowParametersWithoutValue) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");

        final String preprocessedInput =
                    StandardExpressionPreprocessor.preprocess(context, input);

        final IEngineConfiguration configuration = context.getConfiguration();

        if (configuration != null) {
            final AssignationSequence cachedAssignationSequence =
                    ExpressionCache.getAssignationSequenceFromCache(configuration, preprocessedInput);
            if (cachedAssignationSequence != null) {
                return cachedAssignationSequence;
            }
        }

        final AssignationSequence assignationSequence =
                internalParseAssignationSequence(preprocessedInput.trim(), allowParametersWithoutValue);

        if (assignationSequence == null) {
            throw new TemplateProcessingException("Could not parse as assignation sequence: \"" + input + "\"");
        }

        if (configuration != null) {
            ExpressionCache.putAssignationSequenceIntoCache(configuration, preprocessedInput, assignationSequence);
        }

        return assignationSequence;

    }


    static AssignationSequence internalParseAssignationSequence(final String input, final boolean allowParametersWithoutValue) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final ExpressionParsingState decomposition = ExpressionParsingUtil.decompose(input);

        if (decomposition == null) {
            return null;
        }

        return composeSequence(decomposition, 0, allowParametersWithoutValue);

    }






    private static AssignationSequence composeSequence(
            final ExpressionParsingState state, final int nodeIndex, final boolean allowParametersWithoutValue) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            if (!allowParametersWithoutValue) {
                return null;
            }
            // could happen if we are traversing pointers recursively, so we will consider it a sequence containing
            // only one, no-value assignation (though we will let the Assignation.compose(...) method do the job.
            final Assignation assignation = composeAssignation(state, nodeIndex, allowParametersWithoutValue);
            if (assignation == null) {
                return null;
            }
            final List<Assignation> assignations = new ArrayList<Assignation>(2);
            assignations.add(assignation);
            return new AssignationSequence(assignations);
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // First, check whether we are just dealing with a pointer input
        final int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeSequence(state, pointer, allowParametersWithoutValue);
        }

        final String[] inputParts = StringUtils.split(input, ",");

        for (final String inputPart : inputParts) {
            // We create new String parsing nodes for all of the elements
            // We add all nodes first so that we know the exact indexes in which they are
            // (composing assignations here can modify the size of the state object without we noticing)
            state.addNode(inputPart.trim());
        }

        final List<Assignation> assignations = new ArrayList<Assignation>(4);
        final int startIndex = state.size() - inputParts.length;
        final int endIndex = state.size();
        for (int i = startIndex; i < endIndex; i++) {
            final Assignation assignation =
                    composeAssignation(state, i, allowParametersWithoutValue);
            if (assignation == null) {
                return null;
            }
            assignations.add(assignation);
        }

        return new AssignationSequence(assignations);

    }




    static Assignation composeAssignation(
            final ExpressionParsingState state, final int nodeIndex, final boolean allowParametersWithoutValue) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            if (!allowParametersWithoutValue) {
                return null;
            }
            // could happen if we are traversing pointers recursively, so we will consider it a no-value assignation
            return new Assignation(state.get(nodeIndex).getExpression(),null);
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // First, check whether we are just dealing with a pointer input
        final int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeAssignation(state, pointer, allowParametersWithoutValue);
        }

        final int inputLen = input.length();
        final int operatorPos = input.indexOf('=');

        final String leftInput =
                (operatorPos == -1? input.trim() : input.substring(0,operatorPos).trim());
        final String rightInput =
                (operatorPos == -1 || operatorPos == (inputLen - 1) ? null : input.substring(operatorPos + 1).trim());

        if (StringUtils.isEmptyOrWhitespace(leftInput)) {
            return null;
        }

        final Expression leftExpr = ExpressionParsingUtil.parseAndCompose(state, leftInput);
        if (leftExpr == null) {
            return null;
        }

        final Expression rightExpr;
        if (!StringUtils.isEmptyOrWhitespace(rightInput)) {
            rightExpr = ExpressionParsingUtil.parseAndCompose(state, rightInput);
            if (rightExpr == null) {
                return null;
            }
        } else if (!allowParametersWithoutValue) {
            return null;
        } else {
            rightExpr = null;
        }

        return new Assignation(leftExpr, rightExpr);

    }




    private AssignationUtils() {
        super();
    }


}
