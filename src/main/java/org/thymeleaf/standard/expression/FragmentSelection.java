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
package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.StringUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class FragmentSelection implements Serializable {

    
    private static final long serialVersionUID = -5310313871594922690L;
    
    private static final String OPERATOR = "::";

    private static final char FRAGMENT_SELECTOR_PARAMETERS_START = '(';
    private static final char FRAGMENT_SELECTOR_PARAMETERS_END = ')';

    private static final String UNNAMED_PARAMETERS_PREFIX = "_arg";

    
    private final Expression templateName;
    private final Expression fragmentSelector;
    private final AssignationSequence parameters;

    
    
    private FragmentSelection(final Expression templateName, final Expression fragmentSelector,
            final AssignationSequence parameters) {
        super();
        // templateName can be null if fragment is to be executed on the current template
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
    }

    
    
    public Expression getTemplateName() {
        return this.templateName;
    }

    public Expression getFragmentSelector() {
        return this.fragmentSelector;
    }
    
    public boolean hasFragmentSelector() {
        return this.fragmentSelector != null;
    }

    public AssignationSequence getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }


    public boolean hasSyntheticParameters(
            final Configuration configuration, final IProcessingContext processingContext,
            final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        // The parameter sequence will be considered "synthetically named" if its variable names are all synthetic
        // (see the "variableNamesAreSynthetic" method for more info).

        if (this.parameters == null) {
            return false;
        }

        final Set<String> variableNames = new HashSet<String>(this.parameters.size() + 2);
        for (final Assignation assignation : this.parameters.getAssignations()) {

            final Expression variableNameExpr = assignation.getLeft();
            final Object variableNameValue =
                    Expression.execute(configuration, processingContext, variableNameExpr, expressionEvaluator, expContext);

            final String variableName = (variableNameValue == null? null : variableNameValue.toString());

            variableNames.add(variableName);

        }

        return variableNamesAreSynthetic(variableNames);

    }



    public String getStringRepresentation() {

        final String templateNameStringRepresentation =
                (this.templateName != null? this.templateName.getStringRepresentation() : "");

        final String templateSelectionParameters;
        if (this.parameters == null && this.parameters.size() > 0) {
            templateSelectionParameters = "";
        } else {
            final StringBuilder paramBuilder = new StringBuilder();
            paramBuilder.append(' ');
            paramBuilder.append(FRAGMENT_SELECTOR_PARAMETERS_START);
            paramBuilder.append(StringUtils.join(this.parameters.getAssignations(), ','));
            paramBuilder.append(FRAGMENT_SELECTOR_PARAMETERS_END);
            templateSelectionParameters = paramBuilder.toString();
        }

        if (this.fragmentSelector == null) {
            return templateNameStringRepresentation + templateSelectionParameters;
        }
        return templateNameStringRepresentation + " " +
                OPERATOR + " " +
                this.fragmentSelector.getStringRepresentation() + templateSelectionParameters;
    }



    @Override
    public String toString() {
        return getStringRepresentation();
    }





    static FragmentSelection parse(final String input) {

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
            final AssignationSequence parametersAsSeq = AssignationSequence.parse(parametersStr,false);

            if (parametersAsSeq != null) {
                return new FragmentSelection(templateNameExpression, fragmentSpecExpression, parametersAsSeq);
            }

            // Parameters wheren't parseable as an assignation sequence. So we should try parsing as Expression
            // sequence and create a synthetically named parameter sequence with the expressions in the sequence as
            // values.

            final ExpressionSequence parametersExpSeq = ExpressionSequence.parse(parametersStr);

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
                    // We have closed a parenthesis at level 0, this is what we were looking for.
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






    static boolean variableNamesAreSynthetic(final Set<String> variableNames) {

        // Variable names in an assignation sequence will be considered "synthetic" if all variable names
        // start by "_arg", followed by a number. This will mean they have been automatically
        // assigned when parsed because no names were assigned.

        for (final String variableName : variableNames) {

            if (variableName == null) {
                return false;
            }

            if (!variableName.startsWith(UNNAMED_PARAMETERS_PREFIX)) {
                return false;
            }
            final int variableNameLen = variableName.length();
            for (int i = UNNAMED_PARAMETERS_PREFIX.length(); i < variableNameLen; i++) {
                final char c = variableName.charAt(i);
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }

        return true;

    }


    private static AssignationSequence createSyntheticallyNamedParameterSequence(final ExpressionSequence expSeq) {

        final List<Assignation> assignations = new ArrayList<Assignation>(expSeq.size() + 2);

        int argIndex = 0;
        for (final Expression expression : expSeq.getExpressions()) {
            final Expression parameterName =
                    Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(UNNAMED_PARAMETERS_PREFIX + argIndex));
            assignations.add(new Assignation(parameterName, expression));
        }

        return new AssignationSequence(assignations);

    }



}
