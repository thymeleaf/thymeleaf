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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class ModelExpression extends SimpleExpression {

    private static final Logger logger = LoggerFactory.getLogger(ModelExpression.class);

    private static final long serialVersionUID = -130371297698708001L;

    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";
    private static final String SEPARATOR = "::";
    private static final String UNNAMED_PARAMETERS_PREFIX = "_arg";


    static final char SELECTOR = '~';


    private static final Pattern MODEL_PATTERN =
        Pattern.compile("^\\s*~\\{(.+?)\\}\\s*$", Pattern.DOTALL);




    private final IStandardExpression templateName;
    private final IStandardExpression fragmentSelector;
    private final AssignationSequence parameters;



    public ModelExpression(
            final IStandardExpression templateName, final IStandardExpression fragmentSelector,
            final AssignationSequence parameters) {
        super();
        // templateName can be null if fragment is to be executed on the current template
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
    }



    public IStandardExpression getTemplateName() {
        return this.templateName;
    }

    public IStandardExpression getFragmentSelector() {
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




    @Override
    public String getStringRepresentation() {

        final StringBuilder sb = new StringBuilder();

        sb.append(SELECTOR);
        sb.append(SimpleExpression.EXPRESSION_START_CHAR);

        sb.append(this.templateName != null? this.templateName.getStringRepresentation() : "");

        if (this.fragmentSelector != null) {
            sb.append(' ');
            sb.append(SEPARATOR);
            sb.append(' ');
            sb.append(this.fragmentSelector.getStringRepresentation());
        }

        if (this.parameters != null && this.parameters.size() > 0) {
            sb.append(' ');
            sb.append('(');
            sb.append(StringUtils.join(this.parameters.getAssignations(), ','));
            sb.append(')');
        }

        sb.append(SimpleExpression.EXPRESSION_END_CHAR);

        return sb.toString();

    }





    static ModelExpression parseModelExpression(final String input) {
        final Matcher matcher = MODEL_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        final String expression = matcher.group(1);
        if (StringUtils.isEmptyOrWhitespace(expression)) {
            return null;
        }
        return parseModelExpressionContent(expression.trim());
    }




    static ModelExpression parseModelExpressionContent(final String input) {

        final int lastParenthesesGroupPos = indexOfLastParenthesesGroup(input);

        final String inputWithoutParameters;
        String parametersStr;
        if (lastParenthesesGroupPos != -1) {
            parametersStr = input.substring(lastParenthesesGroupPos).trim();
            inputWithoutParameters = input.substring(0, lastParenthesesGroupPos).trim();
        } else {
            parametersStr = null;
            inputWithoutParameters = input;
        }


        String templateNameStr;
        String fragmentSpecStr;
        final int operatorPos = inputWithoutParameters.indexOf(SEPARATOR);
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
            fragmentSpecStr = inputWithoutParameters.substring(operatorPos + SEPARATOR.length()).trim();
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
                return new ModelExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeq);
            }

            // Parameters wheren't parseable as an assignation sequence. So we should try parsing as Expression
            // sequence and create a synthetically named parameter sequence with the expressions in the sequence as
            // values.

            final ExpressionSequence parametersExpSeq =
                    ExpressionSequenceUtils.internalParseExpressionSequence(parametersStr);

            if (parametersExpSeq != null) {
                final AssignationSequence parametersAsSeqFromExp =
                        createSyntheticallyNamedParameterSequence(parametersExpSeq);
                return new ModelExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeqFromExp);
            }

            // The parameters str is not parsable neither as an assignation sequence nor as an expression sequence,
            // so we can come to the conclusion it is wrong.

            return null;

        }

        return new ModelExpression(templateNameExpression, fragmentSpecExpression, null);

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












    // TODO This should be returning an IModel!!!!
    static Object executeModelExpression(
            final IExpressionContext context,
            final ModelExpression expression, final StandardExpressionExecutionContext expContext) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(expression, "Model Expression cannot be null");

        /*
         * COMPUTE THE TEMPLATE NAME
         */
        final IStandardExpression templateNameExpression = expression.getTemplateName();
        final String templateName;
        if (templateNameExpression != null) {
            // Note we will apply restricted variable access for resolving template names in fragment specs. This
            // protects against the possibility of code injection attacks from request parameters.
            final Object templateNameObject = templateNameExpression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
            if (templateNameObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of template name from spec \"" + expression.getStringRepresentation() + "\" returned null.");
            }
            final String evaluatedTemplateName = templateNameObject.toString();
            if (TEMPLATE_NAME_CURRENT_TEMPLATE.equals(evaluatedTemplateName)) {
                // Template name is "this" and therefore we are including a fragment from the same template.
                templateName = null;
            } else {
                templateName = templateNameObject.toString();
            }
        } else {
            // If template name expression is null, we will execute the fragment on the "current" template
            templateName = null;
        }


        /*
         * RESOLVE FRAGMENT PARAMETERS if specified (null if not)
         */
        final Map<String, Object> fragmentParameters =
                resolveProcessedFragmentParameters(context, expression.getParameters(), expContext);

        /*
         * COMPUTE THE FRAGMENT SELECTOR
         */
        String fragmentSelector = null;
        if (expression.hasFragmentSelector()) {

            final Object fragmentSelectorObject =
                    expression.getFragmentSelector().execute(context, expContext);
            if (fragmentSelectorObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of fragment selector from spec \"" + expression + "\" returned null.");
            }

            fragmentSelector = fragmentSelectorObject.toString();

            if (fragmentSelector.length() > 3 &&
                    fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' &&
                    fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
                // For legacy compatibility reasons, we allow fragment DOM Selector expressions to be specified
                // between brackets. Just remove them.
                fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1).trim();
            }

        }

        return new ProcessedFragmentSelection(templateName, fragmentSelector, fragmentParameters);

    }



    private static Map<String,Object> resolveProcessedFragmentParameters(
            final IExpressionContext context,
            final AssignationSequence parameters, final StandardExpressionExecutionContext expContext) {

        if (parameters == null || parameters.size() == 0) {
            return null;
        }

        final Map<String,Object> parameterValues = new HashMap<String, Object>(parameters.size() + 2);
        final List<Assignation> assignationValues = parameters.getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignation = assignationValues.get(i);

            final IStandardExpression parameterNameExpr = assignation.getLeft();
            final Object parameterNameValue = parameterNameExpr.execute(context, expContext);

            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            final IStandardExpression parameterValueExpr = assignation.getRight();
            final Object parameterValueValue = parameterValueExpr.execute(context, expContext);

            parameterValues.put(parameterName, parameterValueValue);

        }

        return parameterValues;

    }

    
    
}
