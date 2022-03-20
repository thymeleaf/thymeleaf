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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
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





    public Expression parseExpression(
            final IExpressionContext context,
            final String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return (Expression) parseExpression(context, input, true);
    }




    public AssignationSequence parseAssignationSequence(
            final IExpressionContext context,
            final String input, final boolean allowParametersWithoutValue) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return AssignationUtils.parseAssignationSequence(context, input, allowParametersWithoutValue);
    }




    public ExpressionSequence parseExpressionSequence(
            final IExpressionContext context,
            final String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return ExpressionSequenceUtils.parseExpressionSequence(context, input);
    }




    public Each parseEach(
            final IExpressionContext context,
            final String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return EachUtils.parseEach(context, input);
    }




    public FragmentSignature parseFragmentSignature(final IEngineConfiguration configuration, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return FragmentSignatureUtils.parseFragmentSignature(configuration, input);
    }







    static IStandardExpression parseExpression(
            final IExpressionContext context,
            final String input, final boolean preprocess) {

        final IEngineConfiguration configuration = context.getConfiguration();

        final String preprocessedInput =
            (preprocess? StandardExpressionPreprocessor.preprocess(context, input) : input);

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
