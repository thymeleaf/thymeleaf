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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class StandardExpressionParser implements IStandardExpressionParser {


    
    
    public StandardExpressionParser() {
        super();
    }


    /**
     * @deprecated since 2.1.0. Deprecated in favour of
     *       {@link #parseExpression(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}.
     *       Will be removed in 3.0.
     */
    @Deprecated
    public Expression parseExpression(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return (Expression) parseExpression(arguments.getConfiguration(), arguments, input, true);
    }
    
    /**
     * @since 2.0.9
     */
    public Expression parseExpression(final Configuration configuration,
                                      final IProcessingContext processingContext, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return (Expression) parseExpression(configuration, processingContext, input, true);
    }



    /**
     * @deprecated since 2.1.0. Deprecated in favour of
     *       {@link #parseAssignationSequence(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String, boolean)}.
     *       Will be removed in 3.0.
     */
    @Deprecated
    public AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean allowParametersWithoutValue) {
        return parseAssignationSequence(arguments.getConfiguration(), arguments, input, allowParametersWithoutValue);
    }
    
    /**
     * @since 2.0.9
     */
    public AssignationSequence parseAssignationSequence(final Configuration configuration,
                                                        final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        return AssignationUtils.parseAssignationSequence(configuration, processingContext, input, allowParametersWithoutValue);
    }



    /**
     * @deprecated since 2.1.0. Deprecated in favour of
     *       {@link #parseExpressionSequence(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}.
     *       Will be removed in 3.0.
     */
    @Deprecated
    public ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        return parseExpressionSequence(arguments.getConfiguration(), arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public ExpressionSequence parseExpressionSequence(final Configuration configuration,
                                                      final IProcessingContext processingContext, final String input) {
        return ExpressionSequenceUtils.parseExpressionSequence(configuration, processingContext, input);
    }



    /**
     * @deprecated since 2.1.0. Deprecated in favour of
     *       {@link #parseEach(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}.
     *       Will be removed in 3.0.
     */
    @Deprecated
    public Each parseEach(final Arguments arguments, final String input) {
        return parseEach(arguments.getConfiguration(), arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public Each parseEach(final Configuration configuration,
                          final IProcessingContext processingContext, final String input) {
        return EachUtils.parseEach(configuration, processingContext, input);
    }



    /**
     * @deprecated since 2.1.0. Deprecated in favour of
     *       {@link #parseFragmentSelection(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}.
     *       Will be removed in 3.0.
     */
    @Deprecated
    public FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        return parseFragmentSelection(arguments.getConfiguration(), arguments, input);
    }
    
    /**
     * @since 2.0.9
     */
    public FragmentSelection parseFragmentSelection(
            final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return FragmentSelectionUtils.parseFragmentSelection(configuration, processingContext, input);
    }



    /**
     * @since 2.1.0
     */
    public FragmentSignature parseFragmentSignature(
            final Configuration configuration, final IProcessingContext processingContext, final String input) {
        return FragmentSignatureUtils.parseFragmentSignature(configuration, input);
    }







    static IStandardExpression parseExpression(final Configuration configuration,
               final IProcessingContext processingContext, final String input, final boolean preprocess) {

        final String preprocessedInput =
            (preprocess? StandardExpressionPreprocessor.preprocess(configuration, processingContext, input) : input);

        if (configuration != null) {
            final IStandardExpression cachedExpression =
                    ExpressionCache.getExpressionFromCache(configuration, preprocessedInput);
            if (cachedExpression != null) {
                return cachedExpression;
            }
        }
        
        final Expression expression = Expression.parse(preprocessedInput.trim());
        
        if (expression == null) {
            throw new TemplateProcessingException("Could not parse as expression: \"" + input + "\"");
        }
        
        if (configuration != null) {
            ExpressionCache.putExpressionIntoCache(configuration, preprocessedInput, expression);
        }
        
        return expression;
        
    }







    
    
    @Override
    public String toString() {
        return "Standard Expression Parser";
    }
    
    
}
