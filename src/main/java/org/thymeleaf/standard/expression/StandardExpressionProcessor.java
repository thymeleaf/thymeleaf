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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @deprecated in 2.1.0 for semantic and refactoring reasons. Should use the equivalent
 *             {@link StandardExpressions} instead for obtaining parser and executor instances. Will be removed in 3.0.
 * @since 1.1
 *
 */
@Deprecated
public final class StandardExpressionProcessor {

    /**
     * @deprecated the StandardExpressionProcessor class was deprecated in 2.1.0 for semantic and refactoring reasons.
     *             Should use the equivalent {@link StandardExpressions} instead for obtaining parser and
     *             executor instances. Will be removed in 3.0.
     */
    @Deprecated
    public static final String STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME = "StandardExpressionExecutor";

    /**
     * @deprecated the StandardExpressionProcessor class was deprecated in 2.1.0 for semantic and refactoring reasons.
     *             Should use the equivalent {@link StandardExpressions} instead for obtaining parser and
     *             executor instances. Will be removed in 3.0.
     */
    @Deprecated
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
   
    
    
    private StandardExpressionProcessor() {
        super();
    }



    
    public static Expression parseExpression(final Arguments arguments, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return expressionParser.parseExpression(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static Expression parseExpression(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return expressionParser.parseExpression(configuration, processingContext, input);
    }

    
    
    public static AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean allowParametersWithoutValue) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return expressionParser.parseAssignationSequence(arguments, input, allowParametersWithoutValue);
    }
    
    /**
     * @since 2.0.9
     */
    public static AssignationSequence parseAssignationSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return expressionParser.parseAssignationSequence(configuration, processingContext, input, allowParametersWithoutValue);
    }

    
    
    public static ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return expressionParser.parseExpressionSequence(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static ExpressionSequence parseExpressionSequence(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return expressionParser.parseExpressionSequence(configuration, processingContext, input);
    }

    
    
    public static Each parseEach(final Arguments arguments, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return expressionParser.parseEach(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static Each parseEach(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return expressionParser.parseEach(configuration, processingContext, input);
    }



    public static FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
        return expressionParser.parseFragmentSelection(arguments, input);
    }

    /**
     * @since 2.0.9
     */
    public static FragmentSelection parseFragmentSelection(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        return expressionParser.parseFragmentSelection(configuration, processingContext, input);
    }

    

    
    
    
    
    public static Object executeExpression(final Arguments arguments, final Expression expression) {
        final StandardExpressionExecutor expressionExecutor =
                StandardExpressions.getExpressionExecutor(arguments.getConfiguration());
        return expressionExecutor.executeExpression(arguments, expression);
    }
    
    /**
     * @since 2.0.9
     */
    public static Object executeExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final Expression expression) {
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);
        return expressionExecutor.executeExpression(configuration, processingContext, expression);
    }
    

    
    /**
     * @since 2.0.16
     */
    public static Object executeExpression(final Arguments arguments, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        final StandardExpressionExecutor expressionExecutor =
                StandardExpressions.getExpressionExecutor(arguments.getConfiguration());
        return expressionExecutor.executeExpression(arguments, expression, expContext);
    }
    
    /**
     * @since 2.0.16
     */
    public static Object executeExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);
        return expressionExecutor.executeExpression(configuration, processingContext, expression, expContext);
    }
    
    
    

    

    
    public static Object processExpression(final Arguments arguments, final String input) {
        return executeExpression(arguments, parseExpression(arguments, input));
    }
    
    /**
     * @since 2.0.9
     */
    public static Object processExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {
        return executeExpression(configuration, processingContext, parseExpression(configuration, processingContext, input));
    }
    

    
    /**
     * @since 2.0.16
     */
    public static Object processExpression(final Arguments arguments, final String input,
            final StandardExpressionExecutionContext expContext) {

        final Configuration configuration = arguments.getConfiguration();
        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final Expression expression = expressionParser.parseExpression(configuration, arguments, input);
        return expressionExecutor.executeExpression(configuration, arguments, expression, expContext);

    }
    
    /**
     * @since 2.0.16
     */
    public static Object processExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final String input,
            final StandardExpressionExecutionContext expContext) {

        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final Expression expression = expressionParser.parseExpression(configuration, processingContext, input);
        return expressionExecutor.executeExpression(configuration, processingContext, expression, expContext);

    }







    /**
     * @deprecated the StandardExpressionProcessor class was deprecated in 2.1.0 for semantic and refactoring reasons.
     *             Should use the equivalent {@link StandardExpressions} instead if you want to obtain
     *             parser and/or executor instances registered by the standard dialects, or instance your
     *             parser/executor instances using their constructors directly if you are building your own dialect
     *             including these parser/executors. Will be removed in 3.0.
     */
    @Deprecated
    public static StandardExpressionExecutor createStandardExpressionExecutor(
            final IStandardVariableExpressionEvaluator expressionEvaluator) {
        return new StandardExpressionExecutor(expressionEvaluator);
    }


    /**
     * @deprecated the StandardExpressionProcessor class was deprecated in 2.1.0 for semantic and refactoring reasons.
     *             Should use the equivalent {@link StandardExpressions} instead if you want to obtain
     *             parser and/or executor instances registered by the standard dialects, or instance your
     *             parser/executor instances using their constructors directly if you are building your own dialect
     *             including these parser/executors. Will be removed in 3.0.
     */
    @Deprecated
    public static StandardExpressionParser createStandardExpressionParser(
            final StandardExpressionExecutor executor) {
        return new StandardExpressionParser(executor);
    }
    
}
