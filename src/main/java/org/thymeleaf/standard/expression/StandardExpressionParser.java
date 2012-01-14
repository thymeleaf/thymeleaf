/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.ExpressionParsingException;
import org.thymeleaf.util.Validate;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class StandardExpressionParser {

    
    
    
    private final StandardExpressionExecutor executor;
    
    
    private static final char PREPROCESS_DELIMITER = '_';
    private static final String PREPROCESS_EVAL = "\\_\\_(.*?)\\_\\_";
    private static final Pattern PREPROCESS_EVAL_PATTERN = Pattern.compile(PREPROCESS_EVAL, Pattern.DOTALL);

    
    private static final ExpressionCache CACHE = new ExpressionCache();
    
    
    
    StandardExpressionParser(final StandardExpressionExecutor executor) {
        super();
        this.executor = executor;
    }



    
    public Expression parseExpression(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseExpression(arguments, input, true);
    }

    
    
    public AssignationSequence parseAssignationSequence(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseAssignationSequence(arguments, input, true);
    }

    
    
    public ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseExpressionSequence(arguments, input, true);
    }

    
    
    public Each parseEach(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseEach(arguments, input, true);
    }
    

    
    public FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseFragmentSelection(arguments, input, true);
    }

    
    
    
    
    
    
    Expression parseExpression(final Arguments arguments, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(arguments, trimmedInput) :
                    trimmedInput);

        final Expression cachedExpression = CACHE.getExpressionFromCache(preprocessedInput);
        if (cachedExpression != null) {
            return cachedExpression;
        }
        
        final Expression expression = Expression.parse(preprocessedInput);
        
        if (expression == null) {
            throw new ExpressionParsingException("Could not parse as expression: \"" + input + "\"");
        }
        
        CACHE.putExpressionIntoCache(preprocessedInput, expression);
        
        return expression;
        
    }

    
    
    AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(arguments, trimmedInput) :
                    trimmedInput);

        final AssignationSequence cachedAssignationSequence = CACHE.getAssignationSequenceFromCache(preprocessedInput);
        if (cachedAssignationSequence != null) {
            return cachedAssignationSequence;
        }
        
        final AssignationSequence assignationSequence = AssignationSequence.parse(preprocessedInput);
        
        if (assignationSequence == null) {
            throw new ExpressionParsingException("Could not parse as assignation sequence: \"" + input + "\"");
        }
        
        CACHE.putAssignationSequenceIntoCache(preprocessedInput, assignationSequence);
        
        return assignationSequence;

    }

    
    
    ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(arguments, trimmedInput) :
                    trimmedInput);

        final ExpressionSequence cachedExpressionSequence = CACHE.getExpressionSequenceFromCache(preprocessedInput);
        if (cachedExpressionSequence != null) {
            return cachedExpressionSequence;
        }
        
        final ExpressionSequence expressionSequence = ExpressionSequence.parse(preprocessedInput);
        
        if (expressionSequence == null) {
            throw new ExpressionParsingException("Could not parse as expression sequence: \"" + input + "\"");
        }
        
        CACHE.putExpressionSequenceIntoCache(preprocessedInput, expressionSequence);
        
        return expressionSequence;

    }

    
    
    Each parseEach(final Arguments arguments, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(arguments, trimmedInput) :
                    trimmedInput);

        final Each cachedEach = CACHE.getEachFromCache(preprocessedInput);
        if (cachedEach != null) {
            return cachedEach;
        }
        
        final Each each = Each.parse(preprocessedInput);
        
        if (each == null) {
            throw new ExpressionParsingException("Could not parse as each: \"" + input + "\"");
        }
        
        CACHE.putEachIntoCache(preprocessedInput, each);
        
        return each;

    }
    

    
    FragmentSelection parseFragmentSelection(final Arguments arguments, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(arguments, trimmedInput) :
                    trimmedInput);

        final FragmentSelection cachedFragmentSelection = CACHE.getFragmentSelectionFromCache(preprocessedInput);
        if (cachedFragmentSelection != null) {
            return cachedFragmentSelection;
        }
        
        final FragmentSelection fragmentSelection = FragmentSelection.parse(preprocessedInput);
        
        if (fragmentSelection == null) {
            throw new ExpressionParsingException("Could not parse as fragment selection: \"" + input + "\"");
        }
        
        CACHE.putFragmentSelectionIntoCache(preprocessedInput, fragmentSelection);
        
        return fragmentSelection;

    }
    
    

    
    
    String preprocess(final Arguments arguments, final String input) {

        if (input.indexOf(PREPROCESS_DELIMITER) == -1) {
            // Fail quick
            return input;
        }
        
        final Matcher matcher = PREPROCESS_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {

            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                final Expression expression = 
                    parseExpression(
                            arguments, matcher.group(1), false);
                if (expression == null) {
                    return null;
                }
                
                final Object result =
                    this.executor.executeExpression(arguments, expression);
                
                strBuilder.append(result);
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString().trim();
            
        }
        
        return input;
        
    }

    
    @Override
    public String toString() {
        return "Standard Expression Parser";
    }
    
    
}
