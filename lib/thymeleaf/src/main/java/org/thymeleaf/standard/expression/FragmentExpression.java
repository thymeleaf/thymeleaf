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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateInputException;
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
public final class FragmentExpression extends SimpleExpression {

    private static final Logger logger = LoggerFactory.getLogger(FragmentExpression.class);

    private static final long serialVersionUID = -130371297698708001L;

    /**
     * This constant contains the {@link FragmentExpression} object representing the EMPTY FRAGMENT ({@code ~{}})
     */
    public static final FragmentExpression EMPTY_FRAGMENT_EXPRESSION = new FragmentExpression();

    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";
    private static final String SEPARATOR = "::";
    static final String UNNAMED_PARAMETERS_PREFIX = "_arg";


    public static final char SELECTOR = '~';


    private static final Pattern FRAGMENT_PATTERN =
        Pattern.compile("^\\s*~\\{(.*?)\\}\\s*$", Pattern.DOTALL);




    private final IStandardExpression templateName;
    private final IStandardExpression fragmentSelector;
    private final AssignationSequence parameters;
    private final boolean syntheticParameters;



    public FragmentExpression(
            final IStandardExpression templateName, final IStandardExpression fragmentSelector,
            final AssignationSequence parameters, final boolean syntheticParameters) {
        super();
        // templateName can be null if fragment is to be executed on the current template, but then there should
        // at least be a fragment selector
        if (templateName == null && fragmentSelector == null) {
            throw new IllegalArgumentException(
                    "Fragment Expression cannot have null template name and null fragment selector");
        }
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
        this.syntheticParameters = (this.parameters != null && this.parameters.size() > 0 && syntheticParameters);
    }


    // Creates the empty Fragment Expression
    private FragmentExpression() {
        super();
        this.templateName = null;
        this.fragmentSelector = null;
        this.parameters = null;
        this.syntheticParameters = false;
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

    public boolean hasSyntheticParameters() {
        return this.syntheticParameters;
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





    public static FragmentExpression parseFragmentExpression(final String input) {
        final Matcher matcher = FRAGMENT_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        final String expression = matcher.group(1);
        if (StringUtils.isEmptyOrWhitespace(expression)) {
            return EMPTY_FRAGMENT_EXPRESSION;
        }
        return parseFragmentExpressionContent(expression.trim());
    }




    static FragmentExpression parseFragmentExpressionContent(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return EMPTY_FRAGMENT_EXPRESSION;
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

            // When parsing this, we don't allow parameters without value because we would be mistakenly
            // parsing as parameter names what in fact are values for synthetically named parameters.
            final AssignationSequence parametersAsSeq =
                    AssignationUtils.internalParseAssignationSequence(parametersStr, false);

            if (parametersAsSeq != null) {
                return new FragmentExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeq, false);
            }

            // Parameters wheren't parsable as an assignation sequence. So we should try parsing as Expression
            // sequence and create a synthetically named parameter sequence with the expressions in the sequence as
            // values.

            final ExpressionSequence parametersExpSeq =
                    ExpressionSequenceUtils.internalParseExpressionSequence(parametersStr);

            if (parametersExpSeq != null) {
                final AssignationSequence parametersAsSeqFromExp =
                        createSyntheticallyNamedParameterSequence(parametersExpSeq);
                return new FragmentExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeqFromExp, true);
            }

            // The parameters str is not parsable neither as an assignation sequence nor as an expression sequence,
            // so we can come to the conclusion it is wrong.

            return null;

        }

        return new FragmentExpression(templateNameExpression, fragmentSpecExpression, null, false);

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










    static Fragment executeFragmentExpression(
            final IExpressionContext context, final FragmentExpression expression) {

        if (!(context instanceof ITemplateContext)) {
            throw new TemplateProcessingException(
                    "Cannot evaluate expression \"" + expression + "\". Fragment expressions " +
                    "can only be evaluated in a template-processing environment (as a part of an in-template expression) " +
                    "where processing context is an implementation of " + ITemplateContext.class.getClass() + ", which it isn't (" +
                    context.getClass().getName() + ")");
        }

        if (expression == EMPTY_FRAGMENT_EXPRESSION) {
            return Fragment.EMPTY_FRAGMENT;
        }

        return resolveExecutedFragmentExpression(
                (ITemplateContext) context,
                createExecutedFragmentExpression(context, expression),
                // By default we will NOT consider a non existing template a failure, so that we give the system the chance
                // to return null here (in exchange for a call to resource.exists()). So this false will always be
                // applied in scenarios such as when the fragment expression is used as a parameter in a larger
                // fragment expression: "template : fragment (part=~{expr})"
                false);
    }


    /**
     * <p>
     *   Create the executed fragment expression.
     * </p>
     *
     * @param context the context
     * @param expression the expresson
     * @param expContext the expression context
     * @return the executed fragment expression
     *
     * @deprecated Deprecated in 3.0.9. Use the version without "expContext" itself, as all FragmentExpressions should
     *             be executed in RESTRICTED mode (no request parameter use allowed).
     */
    @Deprecated
    public static ExecutedFragmentExpression createExecutedFragmentExpression(
            final IExpressionContext context,
            final FragmentExpression expression, final StandardExpressionExecutionContext expContext) {
        return doCreateExecutedFragmentExpression(context, expression, expContext);
    }


    public static ExecutedFragmentExpression createExecutedFragmentExpression(
            final IExpressionContext context, final FragmentExpression expression) {
        // All FragmentExpressions will be executed as RESTRICTED (no URL parameters allowed)
        return doCreateExecutedFragmentExpression(context, expression, StandardExpressionExecutionContext.RESTRICTED);
    }


    private static ExecutedFragmentExpression doCreateExecutedFragmentExpression(
            final IExpressionContext context,
            final FragmentExpression expression, final StandardExpressionExecutionContext expContext) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(expression, "Fragment Expression cannot be null");

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating fragment: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }


        /*
         * CHECK THE EMPTY EXPRESSION FRAGMENT
         */
        if (expression == EMPTY_FRAGMENT_EXPRESSION) {
            return ExecutedFragmentExpression.EMPTY_EXECUTED_FRAGMENT_EXPRESSION;
        }


        /*
         * COMPUTE THE TEMPLATE NAME
         */
        final IStandardExpression templateNameExpression = expression.getTemplateName();
        final Object templateNameExpressionResult;
        if (templateNameExpression != null) {
            // Note we will apply restricted variable access for resolving template names in fragment specs. This
            // protects against the possibility of code injection attacks from request parameters.
            templateNameExpressionResult = templateNameExpression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
            // We actually DO allow this expression to return null. It can be a way to signal we want to execute the
            // fragment on the current template.
        } else {
            // If template name expression is null, we will execute the fragment on the "current" template
            templateNameExpressionResult = null;
        }


        /*
         * RESOLVE FRAGMENT PARAMETERS if specified (null if not)
         */
        final Map<String, Object> fragmentParameters =
                createExecutedFragmentExpressionParameters(context, expression.getParameters(), expression.hasSyntheticParameters(), expContext);


        /*
         * COMPUTE THE FRAGMENT SELECTOR
         */
        final Object fragmentSelectorExpressionResult;
        if (expression.hasFragmentSelector()) {
            // Note we will apply restricted variable access for resolving fragment selectors (but will honor legacy behaviour)
            fragmentSelectorExpressionResult = expression.getFragmentSelector().execute(context, expContext);
            // We actually DO allow this expression to return null. It can be a way to signal we want to use the entire
            // template as a fragment
        } else {
            fragmentSelectorExpressionResult = null;
        }


        /*
         * CREATE the final resulting object
         */
        return new ExecutedFragmentExpression(
                expression,
                templateNameExpressionResult, fragmentSelectorExpressionResult,
                fragmentParameters, expression.hasSyntheticParameters());

    }



    private static Map<String,Object> createExecutedFragmentExpressionParameters(
            final IExpressionContext context,
            final AssignationSequence parameters, final boolean syntheticParameters,
            final StandardExpressionExecutionContext expContext) {

        if (parameters == null || parameters.size() == 0) {
            return null;
        }

        final Map<String,Object> parameterValues = new HashMap<String, Object>(parameters.size() + 2);
        final List<Assignation> assignationValues = parameters.getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignation = assignationValues.get(i);

            final IStandardExpression parameterNameExpr = assignation.getLeft();

            final String parameterName;
            if (!syntheticParameters) {
                final Object parameterNameValue = parameterNameExpr.execute(context, expContext);
                parameterName = (parameterNameValue == null ? null : parameterNameValue.toString());
            } else {
                // Parameters are synthetic so we know this is a mere literal like "_argX", no need to perform an exec
                parameterName = ((TextLiteralExpression)parameterNameExpr).getValue().getValue();
            }

            final IStandardExpression parameterValueExpr = assignation.getRight();
            final Object parameterValueValue = parameterValueExpr.execute(context, expContext);

            parameterValues.put(parameterName, parameterValueValue);

        }

        return parameterValues;

    }





    public static Fragment resolveExecutedFragmentExpression(
            final ITemplateContext context, final ExecutedFragmentExpression executedFragmentExpression,
            final boolean failIfNotExists) {

        if (executedFragmentExpression == ExecutedFragmentExpression.EMPTY_EXECUTED_FRAGMENT_EXPRESSION) {
            return Fragment.EMPTY_FRAGMENT;
        }

        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * COMPUTE template name as String
         */
        String templateName = resolveTemplateName(executedFragmentExpression);


        /*
         * COMPUTE fragment selector as String
         */
        final Set<String> fragments = resolveFragments(executedFragmentExpression);


        /*
         * RESOLVE THE FRAGMENT MODEL by using the TemplateManager. This means the fragment will be parsed and maybe
         * cached, and we will be returned an immutable model object
         */
        List<String> templateNameStack = null;
        // scan the template stack if template name is 'this' or an empty name is being used
        if (StringUtils.isEmptyOrWhitespace(templateName)) {

            if (fragments == null || fragments.isEmpty()) {
                return null;
            }

            templateNameStack = new ArrayList<String>(3);
            for (int i = context.getTemplateStack().size() - 1; i >= 0; i--) {
                templateNameStack.add(context.getTemplateStack().get(i).getTemplate());
            }
            templateName = templateNameStack.get(0);
        }

        TemplateModel fragmentModel;
        int i = 0;
        do {
            fragmentModel =
                    configuration.getTemplateManager().parseStandalone(
                            context, templateName, fragments,
                            null,             // we will not force the template mode
                            true,             // use the cache if possible, fragments are from template files
                            failIfNotExists); // depending on the scenario we will consider a non exiting template a fail or not
            i++;
        } while (fragmentModel != null &&               // template not found (only if resolver configuration allows)
                fragmentModel.size() <= 2 &&            // template found, but selector not found
                templateNameStack != null &&            // we have more templates to look into
                i < templateNameStack.size() &&         // we have more templates to look into
                (templateName = templateNameStack.get(i)) != null);  //post test -- need to parse at least 1x

        if (fragmentModel == null) {
            // FragmentExpressions can actually return null, so that this null value can be used in standard expressions
            // such as "~{template} ? ~{default}"
            return null;
        }


        /*
         * We should now check if the resolved fragment actually exists or not (we know the template exists but,
         * did the fragment actually return anything at all?
         */

        final boolean fragmentIsEmpty = (fragmentModel.size() == 2); //only templatestart/end

        if (fragmentIsEmpty) {
            // Fragment is empty, so we should take action: depending on whether we are allowing this or not, we should
            // either fail or simply return null
            if (failIfNotExists) {
                throw new TemplateInputException(
                        "Error resolving fragment: \"" + executedFragmentExpression.fragmentExpression.getStringRepresentation() + "\": " +
                        "template or fragment could not be resolved");
            }
            return null;
        }


        /*
         * RETURN the expected Fragment object
         */
        return new Fragment(fragmentModel, executedFragmentExpression.fragmentParameters, executedFragmentExpression.syntheticParameters);

    }




    public static String resolveTemplateName(final ExecutedFragmentExpression executedFragmentExpression) {

        final Object templateNameObject = executedFragmentExpression.templateNameExpressionResult;
        if (templateNameObject == null) {
            // If template name expression result is null, we will execute the fragment on the "current" template
            return null;
        }
        final String evaluatedTemplateName = templateNameObject.toString();
        if (TEMPLATE_NAME_CURRENT_TEMPLATE.equals(evaluatedTemplateName)) {
            // Template name is "this" and therefore we are including a fragment from the same template.
            return null;
        }
        return templateNameObject.toString();

    }




    public static Set<String> resolveFragments(final ExecutedFragmentExpression executedFragmentExpression) {

        final Object fragmentSelectorObject = executedFragmentExpression.fragmentSelectorExpressionResult;
        if (fragmentSelectorObject != null) {

            String fragmentSelector = fragmentSelectorObject.toString();

            if (fragmentSelector.length() > 3 &&
                    fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' &&
                    fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
                // For legacy compatibility reasons, we allow fragment DOM Selector expressions to be specified
                // between brackets. Just remove them.
                fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1).trim();
            }

            if (fragmentSelector.trim().length() > 0) {
                return Collections.singleton(fragmentSelector);
            }

        }

        return null;

    }





    public static final class ExecutedFragmentExpression {

        public static final ExecutedFragmentExpression EMPTY_EXECUTED_FRAGMENT_EXPRESSION =
                new ExecutedFragmentExpression(EMPTY_FRAGMENT_EXPRESSION, null, null, null, false);

        private final FragmentExpression fragmentExpression;
        private final Object templateNameExpressionResult;
        private final Object fragmentSelectorExpressionResult;
        private final Map<String,Object> fragmentParameters;
        private final boolean syntheticParameters;

        ExecutedFragmentExpression(
                final FragmentExpression fragmentExpression,
                final Object templateNameExpressionResult, final Object fragmentSelectorExpressionResult,
                final Map<String,Object> fragmentParameters, final boolean syntheticParameters) {
            super();
            this.fragmentExpression = fragmentExpression;
            this.templateNameExpressionResult = templateNameExpressionResult;
            this.fragmentSelectorExpressionResult = fragmentSelectorExpressionResult;
            this.fragmentParameters = fragmentParameters;
            this.syntheticParameters = syntheticParameters;
        }

        FragmentExpression getFragmentExpression() {
            return this.fragmentExpression;
        }

        public Object getTemplateNameExpressionResult() {
            return this.templateNameExpressionResult;
        }

        public Object getFragmentSelectorExpressionResult() {
            return this.fragmentSelectorExpressionResult;
        }

        public Map<String, Object> getFragmentParameters() {
            return this.fragmentParameters;
        }

        public boolean hasSyntheticParameters() {
            return this.syntheticParameters;
        }

    }

    
}
