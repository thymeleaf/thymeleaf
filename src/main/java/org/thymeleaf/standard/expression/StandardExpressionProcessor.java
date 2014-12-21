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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @deprecated the StandardExpressionProcessor class was deprecated in 2.1.0 for semantic and refactoring reasons.
 *             Should use the equivalent {@link StandardExpressions} instead if you want to obtain
 *             parser instances registered by the standard dialects, or instance your
 *             parser instances using their constructors directly if you are building your own dialect
 *             including these parserss. As for expression execution, this is no longer managed by executor
 *             objects (also deprecated) but by the expressions themselves. Will be removed in 3.0.
 * @since 1.1
 *
 */
@Deprecated
public final class StandardExpressionProcessor {

    @Deprecated
    public static final String STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME = "StandardExpressionExecutor";

    @Deprecated
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
   
    
    
    private StandardExpressionProcessor() {
        super();
    }




    @Deprecated
    public static Expression parseExpression(final Arguments arguments, final String input) {
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return (Expression) expressionParser.parseExpression(arguments.getConfiguration(), arguments, input);
    }
    
    @Deprecated
    public static Expression parseExpression(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return (Expression) expressionParser.parseExpression(configuration, processingContext, input);
    }



    @Deprecated
    public static AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean allowParametersWithoutValue) {
        return AssignationUtils.parseAssignationSequence(arguments.getConfiguration(), arguments, input, allowParametersWithoutValue);
    }
    
    @Deprecated
    public static AssignationSequence parseAssignationSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        return AssignationUtils.parseAssignationSequence(configuration, processingContext, input, allowParametersWithoutValue);
    }



    @Deprecated
    public static ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        return ExpressionSequenceUtils.parseExpressionSequence(arguments.getConfiguration(), arguments, input);
    }
    
    @Deprecated
    public static ExpressionSequence parseExpressionSequence(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return ExpressionSequenceUtils.parseExpressionSequence(configuration, processingContext, input);
    }



    @Deprecated
    public static Each parseEach(final Arguments arguments, final String input) {
        return EachUtils.parseEach(arguments.getConfiguration(), arguments, input);
    }
    
    @Deprecated
    public static Each parseEach(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return EachUtils.parseEach(configuration, processingContext, input);
    }



    @Deprecated
    public static FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        return FragmentSelectionUtils.parseFragmentSelection(arguments.getConfiguration(), arguments, input);
    }

    @Deprecated
    public static FragmentSelection parseFragmentSelection(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return FragmentSelectionUtils.parseFragmentSelection(configuration, processingContext, input);
    }







    @Deprecated
    public static Object executeExpression(final Arguments arguments, final Expression expression) {
        Validate.notNull(arguments, "Arguments cannot be null");
        return expression.execute(arguments.getConfiguration(), arguments);
    }
    
    @Deprecated
    public static Object executeExpression(final Configuration configuration,
            final IProcessingContext processingContext, final Expression expression) {
        return expression.execute(configuration, processingContext);
    }
    

    
    @Deprecated
    public static Object executeExpression(final Arguments arguments, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        Validate.notNull(arguments, "Arguments cannot be null");
        return expression.execute(arguments.getConfiguration(), arguments, expContext);
    }
    
    @Deprecated
    public static Object executeExpression(final Configuration configuration,
            final IProcessingContext processingContext, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        return expression.execute(configuration, processingContext, expContext);
    }







    @Deprecated
    public static Object processExpression(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        final Expression expression = parseExpression(arguments, input);
        return expression.execute(arguments.getConfiguration(), arguments);
    }
    
    @Deprecated
    public static Object processExpression(final Configuration configuration,
            final IProcessingContext processingContext, final String input) {
        final Expression expression = parseExpression(configuration, processingContext, input);
        return expression.execute(configuration, processingContext);
    }
    

    
    @Deprecated
    public static Object processExpression(final Arguments arguments, final String input,
            final StandardExpressionExecutionContext expContext) {
        Validate.notNull(arguments, "Arguments cannot be null");
        final Expression expression = parseExpression(arguments, input);
        return expression.execute(arguments.getConfiguration(), arguments, expContext);
    }
    
    @Deprecated
    public static Object processExpression(final Configuration configuration,
            final IProcessingContext processingContext, final String input,
            final StandardExpressionExecutionContext expContext) {
        final Expression expression = parseExpression(configuration, processingContext, input);
        return expression.execute(configuration, processingContext, expContext);
    }







    @Deprecated
    public static StandardExpressionExecutor createStandardExpressionExecutor(
            final IStandardVariableExpressionEvaluator expressionEvaluator) {
        return new StandardExpressionExecutor(expressionEvaluator);
    }


    @Deprecated
    public static StandardExpressionParser createStandardExpressionParser(
            final StandardExpressionExecutor executor) {
        return new StandardExpressionParser();
    }
    
}
