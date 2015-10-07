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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Standard implementation of the {@link IStandardExpressionParser} interface for parsing Thymeleaf
 *   Standard Expressions.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.1, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressionParser implements IStandardExpressionParser {


    
    
    public StandardExpressionParser() {
        super();
    }


    /**
     * 
     * @param processingContext the processing context object.
     * @param input the expression to be parsed, as an input String.
     * @return the result
     * @since 3.0.0
     */
    public Expression parseExpression(final IProcessingContext processingContext, final String input) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return (Expression) parseExpression(processingContext, input, true);
    }

    /**
     * 
     * @param processingContext processingContext
     * @param input input
     * @param allowParametersWithoutValue allowParametersWithoutValue
     * @return the result
     * @since 3.0.0
     */
    public AssignationSequence parseAssignationSequence(
            final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return AssignationUtils.parseAssignationSequence(processingContext, input, allowParametersWithoutValue);
    }

    /**
     * 
     * @param processingContext processingContext
     * @param input input
     * @return the result
     * @since 3.0.0
     */
    public ExpressionSequence parseExpressionSequence(final IProcessingContext processingContext, final String input) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return ExpressionSequenceUtils.parseExpressionSequence(processingContext, input);
    }

    /**
     * 
     * @param processingContext processingContext
     * @param input input
     * @return the result
     * @since 3.0.0
     */
    public Each parseEach(final IProcessingContext processingContext, final String input) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return EachUtils.parseEach(processingContext, input);
    }

    /**
     * 
     * @param processingContext processingContext
     * @param input input
     * @return the result
     * @since 3.0.0
     */
    public ParsedFragmentSelection parseFragmentSelection(final IProcessingContext processingContext, final String input) {
        return FragmentSelectionUtils.parseFragmentSelection(processingContext, input);
    }



    /**
     * 
     * @param processingContext processingContext
     * @param input input
     * @return the result
     * @since 3.0.0
     */
    public FragmentSignature parseFragmentSignature(final IProcessingContext processingContext, final String input) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return FragmentSignatureUtils.parseFragmentSignature(processingContext.getConfiguration(), input);
    }







    static IStandardExpression parseExpression(final IProcessingContext processingContext, final String input, final boolean preprocess) {

        final IEngineConfiguration configuration = processingContext.getConfiguration();
        if (configuration == null) {
            throw new IllegalArgumentException("Engine Configuration returned by Processing Context returned null, which is forbidden");
        }

        final String preprocessedInput =
            (preprocess? StandardExpressionPreprocessor.preprocess(processingContext, input) : input);

        final IStandardExpression cachedExpression =
                ExpressionCache.getExpressionFromCache(configuration, preprocessedInput);
        if (cachedExpression != null) {
            return cachedExpression;
        }

        final Expression expression = Expression.parse(preprocessedInput.trim());
        
        if (expression == null) {
            throw new TemplateProcessingException("Could not parse as expression: \"" + input + "\"");
        }
        
        ExpressionCache.putExpressionIntoCache(configuration, preprocessedInput, expression);

        return expression;
        
    }







    
    
    @Override
    public String toString() {
        return "Standard Expression Parser";
    }
    
    
}
