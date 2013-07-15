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
import org.thymeleaf.exceptions.TemplateProcessingException;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class StandardExpressionProcessor {

    
    public static final String STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME = "StandardExpressionExecutor";
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
   
    
    
    private StandardExpressionProcessor() {
        super();
    }



    
    public static Expression parseExpression(final Arguments arguments, final String input) {
        return getParserAttribute(arguments.getConfiguration()).parseExpression(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static Expression parseExpression(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseExpression(configuration, processingContext, input);
    }

    
    
    public static AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean allowParametersWithoutValue) {
        return getParserAttribute(arguments.getConfiguration()).parseAssignationSequence(arguments, input, allowParametersWithoutValue);
    }
    
    /**
     * @since 2.0.9
     */
    public static AssignationSequence parseAssignationSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        return getParserAttribute(configuration).parseAssignationSequence(configuration, processingContext, input, allowParametersWithoutValue);
    }

    
    
    public static ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        return getParserAttribute(arguments.getConfiguration()).parseExpressionSequence(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static ExpressionSequence parseExpressionSequence(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseExpressionSequence(configuration, processingContext, input);
    }

    
    
    public static Each parseEach(final Arguments arguments, final String input) {
        return getParserAttribute(arguments.getConfiguration()).parseEach(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static Each parseEach(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseEach(configuration, processingContext, input);
    }
    

    
    public static FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        return getParserAttribute(arguments.getConfiguration()).parseFragmentSelection(arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public static FragmentSelection parseFragmentSelection(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseFragmentSelection(configuration, processingContext, input);
    }

    

    
    
    
    
    public static Object executeExpression(final Arguments arguments, final Expression expression) {
        return getExecutorAttribute(arguments.getConfiguration()).executeExpression(arguments, expression);
    }
    
    /**
     * @since 2.0.9
     */
    public static Object executeExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final Expression expression) {
        return getExecutorAttribute(configuration).executeExpression(
                configuration, processingContext, expression);
    }
    

    
    /**
     * @since 2.0.16
     */
    public static Object executeExpression(final Arguments arguments, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        return getExecutorAttribute(arguments.getConfiguration()).executeExpression(
                arguments, expression, expContext);
    }
    
    /**
     * @since 2.0.16
     */
    public static Object executeExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        return getExecutorAttribute(configuration).executeExpression(
                configuration, processingContext, expression, expContext);
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
        return executeExpression(
                arguments, 
                parseExpression(arguments, input), 
                expContext);
    }
    
    /**
     * @since 2.0.16
     */
    public static Object processExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final String input,
            final StandardExpressionExecutionContext expContext) {
        return executeExpression(
                configuration, 
                processingContext, 
                parseExpression(configuration, processingContext, input),
                expContext);
    }

    
    
    
    
    
    private static StandardExpressionParser getParserAttribute(final Configuration configuration) {
        final Object parser =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME);
        if (parser == null || (!(parser instanceof StandardExpressionParser))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Parser has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressionProcessor.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + StandardExpressionParser.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME + "\"");
        }
        return (StandardExpressionParser) parser;
    }

    
    
    
    private static StandardExpressionExecutor getExecutorAttribute(final Configuration configuration) {
        final Object executor =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME);
        if (executor == null || (!(executor instanceof StandardExpressionExecutor))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Executor has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressionProcessor.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + StandardExpressionExecutor.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME + "\"");
        }
        return (StandardExpressionExecutor) executor;
    }


    
    
    public static StandardExpressionExecutor createStandardExpressionExecutor(
            final IStandardVariableExpressionEvaluator expressionEvaluator) {
        return new StandardExpressionExecutor(expressionEvaluator);
    }

    
    public static StandardExpressionParser createStandardExpressionParser(
            final StandardExpressionExecutor executor) {
        return new StandardExpressionParser(executor);
    }
    
}
