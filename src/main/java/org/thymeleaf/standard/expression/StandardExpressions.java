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

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class StandardExpressions {


    public static final String STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME = "StandardExpressionExecutor";
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
    public static final String STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME = "StandardConversionService";



    private StandardExpressions() {
        super();
    }



    
    public static Expression parseExpression(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseExpression(configuration, processingContext, input);
    }

    
    
    public static AssignationSequence parseAssignationSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        return getParserAttribute(configuration).parseAssignationSequence(configuration, processingContext, input, allowParametersWithoutValue);
    }

    
    
    public static ExpressionSequence parseExpressionSequence(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseExpressionSequence(configuration, processingContext, input);
    }

    
    
    public static Each parseEach(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseEach(configuration, processingContext, input);
    }



    public static FragmentSelection parseFragmentSelection(final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return getParserAttribute(configuration).parseFragmentSelection(configuration, processingContext, input);
    }



    public static FragmentSignature parseFragmentSignature(final Configuration configuration, final String input) {
        return getParserAttribute(configuration).parseFragmentSignature(configuration, input);
    }

    

    
    
    
    
    public static Object executeExpression(final Configuration configuration,
            final IProcessingContext processingContext, final Expression expression) {
        return getExecutorAttribute(configuration).executeExpression(configuration, processingContext, expression);
    }
    

    
    public static Object executeExpression(final Configuration configuration,
            final IProcessingContext processingContext, final Expression expression,
            final StandardExpressionExecutionContext expContext) {
        return getExecutorAttribute(configuration).executeExpression(configuration, processingContext, expression, expContext);
    }
    
    
    

    

    
    public static Object processExpression(final Configuration configuration,
            final IProcessingContext processingContext, final String input) {
        return executeExpression(configuration, processingContext, parseExpression(configuration, processingContext, input));
    }
    

    
    public static Object processExpression(final Configuration configuration,
            final IProcessingContext processingContext, final String input,
            final StandardExpressionExecutionContext expContext) {
        return executeExpression(
                configuration, 
                processingContext, 
                parseExpression(configuration, processingContext, input),
                expContext);
    }






    public static boolean canConvert(final Configuration configuration,
                                     final Class<?> sourceClass, final Class<?> targetClass) {
        return getConversionService(configuration).canConvert(sourceClass, targetClass);
    }



    public static <S,T> T convert(final Configuration configuration,
                                  final S object, final Class<? super S> sourceClass, final Class<T> targetClass) {
        return getConversionService(configuration).convert(object, sourceClass, targetClass);
    }


    
    
    
    
    private static StandardExpressionParser getParserAttribute(final Configuration configuration) {
        final Object parser =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME);
        if (parser == null || (!(parser instanceof StandardExpressionParser))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Parser has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
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
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + StandardExpressionExecutor.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME + "\"");
        }
        return (StandardExpressionExecutor) executor;
    }





    private static IStandardConversionService getConversionService(final Configuration configuration) {
        final Object conversionService =
                configuration.getExecutionAttributes().get(STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME);
        if (conversionService == null || (!(conversionService instanceof IStandardConversionService))) {
            throw new TemplateProcessingException(
                    "No Standard Conversion Service has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardConversionService.class.getName() + " with name " +
                    "\"" + STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardConversionService) conversionService;
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
