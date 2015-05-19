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
package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IProcessingContext;
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
public final class FragmentSelectionUtils {


    private static final String OPERATOR = "::";

    private static final String UNNAMED_PARAMETERS_PREFIX = "_arg";




    public static boolean hasSyntheticParameters(
            final FragmentSelection fragmentSelection,
            final IProcessingContext processingContext,
            final StandardExpressionExecutionContext expContext) {

        // The parameter sequence will be considered "synthetically named" if its variable names are all synthetic
        // (see the "parameterNamesAreSynthetic" method for more info).

        if (!fragmentSelection.hasParameters()) {
            return false;
        }

        final AssignationSequence fragmentSelectionParameters = fragmentSelection.getParameters();
        final Set<String> variableNames = new HashSet<String>(fragmentSelectionParameters.size() + 2);
        for (final Assignation assignation : fragmentSelectionParameters.getAssignations()) {

            final IStandardExpression variableNameExpr = assignation.getLeft();
            final Object variableNameValue = variableNameExpr.execute(processingContext, expContext);

            final String variableName = (variableNameValue == null? null : variableNameValue.toString());

            variableNames.add(variableName);

        }

        return parameterNamesAreSynthetic(variableNames);

    }





    public static FragmentSelection parseFragmentSelection(
            final IProcessingContext processingContext, final String input) {

        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");

        final IEngineConfiguration configuration = processingContext.getConfiguration();

        final String preprocessedInput =
                StandardExpressionPreprocessor.preprocess(processingContext, input);

        if (configuration != null) {
            final FragmentSelection cachedFragmentSelection =
                    ExpressionCache.getFragmentSelectionFromCache(configuration, preprocessedInput);
            if (cachedFragmentSelection != null) {
                return cachedFragmentSelection;
            }
        }

        final FragmentSelection fragmentSelection =
                FragmentSelectionUtils.internalParseFragmentSelection(preprocessedInput.trim());

        if (fragmentSelection == null) {
            return null;
        }

        if (configuration != null) {
            ExpressionCache.putFragmentSelectionIntoCache(configuration, preprocessedInput, fragmentSelection);
        }

        return fragmentSelection;

    }




    static FragmentSelection internalParseFragmentSelection(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final String trimmedInput = input.trim();

        final int lastParenthesesGroupPos = indexOfLastParenthesesGroup(trimmedInput);

        final String inputWithoutParameters;
        String parametersStr;
        if (lastParenthesesGroupPos != -1) {
            parametersStr = trimmedInput.substring(lastParenthesesGroupPos).trim();
            inputWithoutParameters = trimmedInput.substring(0, lastParenthesesGroupPos).trim();
        } else {
            parametersStr = null;
            inputWithoutParameters = trimmedInput;
        }


        String templateNameStr;
        String fragmentSpecStr;
        final int operatorPos = inputWithoutParameters.indexOf(OPERATOR);
        if (operatorPos == -1) {
            // no operator means everything is considered "before operator" (there is template name, but no
            // fragment name -- template is to be included in its entirety).

            templateNameStr = inputWithoutParameters;
            fragmentSpecStr = null;
            if (StringUtils.isEmptyOrWhitespace(templateNameStr)) {
                if (parametersStr != null) {
                    // Parameters weren't parameters, they actually were the template name!
                    templateNameStr = parametersStr;
                    parametersStr = null;
                } else {
                    // parameters are null, so template name is empty, and therefore wrong.
                    return null;
                }
            }

        } else {
            // There IS operator: we should divide between template name (which can be empty) and fragment spec.

            templateNameStr = inputWithoutParameters.substring(0, operatorPos).trim();
            fragmentSpecStr = inputWithoutParameters.substring(operatorPos + OPERATOR.length()).trim();
            if (StringUtils.isEmptyOrWhitespace(fragmentSpecStr)) {
                if (parametersStr != null) {
                    // Parameters weren't parameters, they actually were the fragment spec!
                    fragmentSpecStr = parametersStr;
                    parametersStr = null;
                } else {
                    // parameters are null, so fragment specification is empty, and therefore wrong (because we
                    // have already established that the :: operator IS present.
                    return null;
                }
            }

        }

        final Expression templateNameExpression;
        if (!StringUtils.isEmptyOrWhitespace(templateNameStr)) {
            templateNameExpression = parseDefaultAsLiteral(templateNameStr);
            if (templateNameExpression == null) {
                return null;
            }
        } else {
            templateNameExpression = null;
        }

        final Expression fragmentSpecExpression;
        if (!StringUtils.isEmptyOrWhitespace(fragmentSpecStr)) {
            fragmentSpecExpression = parseDefaultAsLiteral(fragmentSpecStr);
            if (fragmentSpecExpression == null) {
                return null;
            }
        } else {
            fragmentSpecExpression = null;
        }

        if (!StringUtils.isEmptyOrWhitespace(parametersStr)) {

            // When parsing this, we don't allow parameters without value because we would be mistakingly
            // parsing as parameter names what in fact are values for synthetically named parameters.
            final AssignationSequence parametersAsSeq =
                    AssignationUtils.internalParseAssignationSequence(parametersStr, false);

            if (parametersAsSeq != null) {
                return new FragmentSelection(templateNameExpression, fragmentSpecExpression, parametersAsSeq);
            }

            // Parameters wheren't parseable as an assignation sequence. So we should try parsing as Expression
            // sequence and create a synthetically named parameter sequence with the expressions in the sequence as
            // values.

            final ExpressionSequence parametersExpSeq =
                    ExpressionSequenceUtils.internalParseExpressionSequence(parametersStr);

            if (parametersExpSeq != null) {
                final AssignationSequence parametersAsSeqFromExp =
                        createSyntheticallyNamedParameterSequence(parametersExpSeq);
                return new FragmentSelection(templateNameExpression, fragmentSpecExpression, parametersAsSeqFromExp);
            }

            // The parameters str is not parsable neither as an assignation sequence nor as an expression sequence,
            // so we can come to the conclusion it is wrong.

            return null;

        }

        return new FragmentSelection(templateNameExpression, fragmentSpecExpression, null);

    }



    private static Expression parseDefaultAsLiteral(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final Expression expr = Expression.parse(input);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(input));
        }
        return expr;

    }





    private static int indexOfLastParenthesesGroup(final String input) {

        final int inputLen = input.length();
        final char finalC = input.charAt(inputLen - 1);
        if (finalC != ')') {
            // If there are parentheses, the last char must be an ending one.
            return -1;
        }
        int parenLevel = 1;
        for (int i = inputLen - 2; i >= 0; i--) {
            final char c = input.charAt(i);
            if (c == '(') {
                parenLevel--;
                if (parenLevel == 0) {
                    // We have closed a parenthesis at level 0, this might be what we were looking for.
                    if (i == (inputLen - 2)) {
                        // These are not real parameters, but "()", which might be a "text()" node selector.
                        return -1;
                    }
                    return i;
                }
            } else if (c == ')') {
                parenLevel++;
            }
        }
        // Cannot parse: will never be able to determine whether there are parameters or not, because they aren't
        // correctly closed. Just return -1 as if we didn't find parentheses at all.
        return -1;

    }






    public static boolean parameterNamesAreSynthetic(final Set<String> parameterNames) {

        Validate.notNull(parameterNames, "Parameter names set cannot be null");

        // Parameter names in an assignation sequence will be considered "synthetic" if all variable names
        // start by "_arg", followed by a number. This will mean they have been automatically
        // assigned when parsed because no names were assigned.

        for (final String parameterName : parameterNames) {

            if (parameterName == null) {
                return false;
            }

            if (!parameterName.startsWith(UNNAMED_PARAMETERS_PREFIX)) {
                return false;
            }
            final int parameterNameLen = parameterName.length();
            for (int i = UNNAMED_PARAMETERS_PREFIX.length(); i < parameterNameLen; i++) {
                final char c = parameterName.charAt(i);
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }

        return true;

    }



    static String getSyntheticParameterNameForIndex(final int i) {
        return UNNAMED_PARAMETERS_PREFIX + i;
    }


    private static AssignationSequence createSyntheticallyNamedParameterSequence(final ExpressionSequence expSeq) {

        final List<Assignation> assignations = new ArrayList<Assignation>(expSeq.size() + 2);

        int argIndex = 0;
        for (final IStandardExpression expression : expSeq.getExpressions()) {
            final IStandardExpression parameterName =
                    Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(UNNAMED_PARAMETERS_PREFIX + argIndex++));
            assignations.add(new Assignation(parameterName, expression));
        }

        return new AssignationSequence(assignations);

    }



    private FragmentSelectionUtils() {
        super();
    }
}
