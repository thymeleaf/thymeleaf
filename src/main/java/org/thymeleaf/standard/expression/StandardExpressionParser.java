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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.DOMUtils;
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
        return parseExpression(arguments.getConfiguration(), arguments, DOMUtils.unescapeXml(input, true), true);
    }
    
    /**
     * @since 2.0.9
     */
    public Expression parseExpression(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseExpression(configuration, processingContext, DOMUtils.unescapeXml(input, true), true);
    }

    
    
    public AssignationSequence parseAssignationSequence(final Arguments arguments, final String input, final boolean allowParametersWithoutValue) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseAssignationSequence(arguments.getConfiguration(), arguments, DOMUtils.unescapeXml(input, true), true, allowParametersWithoutValue);
    }
    
    /**
     * @since 2.0.9
     */
    public AssignationSequence parseAssignationSequence(final Configuration configuration, 
            final IProcessingContext processingContext, final String input, final boolean allowParametersWithoutValue) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseAssignationSequence(configuration, processingContext, DOMUtils.unescapeXml(input, true), true, allowParametersWithoutValue);
    }

    
    
    public ExpressionSequence parseExpressionSequence(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseExpressionSequence(arguments.getConfiguration(), arguments, DOMUtils.unescapeXml(input, true), true);
    }
    
    /**
     * @since 2.0.9
     */
    public ExpressionSequence parseExpressionSequence(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseExpressionSequence(configuration, processingContext, DOMUtils.unescapeXml(input, true), true);
    }

    
    
    public Each parseEach(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseEach(arguments.getConfiguration(), arguments, DOMUtils.unescapeXml(input, true), true);
    }
    
    /**
     * @since 2.0.9
     */
    public Each parseEach(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseEach(configuration, processingContext, DOMUtils.unescapeXml(input, true), true);
    }
    

    
    public FragmentSelection parseFragmentSelection(final Arguments arguments, final String input) {
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseFragmentSelection(arguments.getConfiguration(), arguments, DOMUtils.unescapeXml(input, true), true);
    }
    
    /**
     * @since 2.0.9
     */
    public FragmentSelection parseFragmentSelection(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return parseFragmentSelection(configuration, processingContext, DOMUtils.unescapeXml(input, true), true);
    }

    
    
    
    
    
    
    Expression parseExpression(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(configuration, processingContext, trimmedInput) :
                    trimmedInput);

        if (configuration != null) {
            final Expression cachedExpression = CACHE.getExpressionFromCache(configuration, preprocessedInput);
            if (cachedExpression != null) {
                return cachedExpression;
            }
        }
        
        final Expression expression = Expression.parse(preprocessedInput);
        
        if (expression == null) {
            throw new TemplateProcessingException("Could not parse as expression: \"" + input + "\"");
        }
        
        if (configuration != null) {
            CACHE.putExpressionIntoCache(configuration, preprocessedInput, expression);
        }
        
        return expression;
        
    }

    
    
    AssignationSequence parseAssignationSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean preprocess, final boolean allowParametersWithoutValue) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(configuration, processingContext, trimmedInput) :
                    trimmedInput);

        if (configuration != null) {
            final AssignationSequence cachedAssignationSequence = CACHE.getAssignationSequenceFromCache(configuration, preprocessedInput);
            if (cachedAssignationSequence != null) {
                return cachedAssignationSequence;
            }
        }
        
        final AssignationSequence assignationSequence = AssignationSequence.parse(preprocessedInput, allowParametersWithoutValue);
        
        if (assignationSequence == null) {
            throw new TemplateProcessingException("Could not parse as assignation sequence: \"" + input + "\"");
        }
        
        if (configuration != null) {
            CACHE.putAssignationSequenceIntoCache(configuration, preprocessedInput, assignationSequence);
        }
        
        return assignationSequence;

    }

    
    
    ExpressionSequence parseExpressionSequence(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(configuration, processingContext, trimmedInput) :
                    trimmedInput);

        if (configuration != null) {
            final ExpressionSequence cachedExpressionSequence = CACHE.getExpressionSequenceFromCache(configuration, preprocessedInput);
            if (cachedExpressionSequence != null) {
                return cachedExpressionSequence;
            }
        }
        
        final ExpressionSequence expressionSequence = ExpressionSequence.parse(preprocessedInput);
        
        if (expressionSequence == null) {
            throw new TemplateProcessingException("Could not parse as expression sequence: \"" + input + "\"");
        }
        
        if (configuration != null) {
            CACHE.putExpressionSequenceIntoCache(configuration, preprocessedInput, expressionSequence);
        }
        
        return expressionSequence;

    }

    
    
    Each parseEach(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(configuration, processingContext, trimmedInput) :
                    trimmedInput);

        if (configuration != null) {
            final Each cachedEach = CACHE.getEachFromCache(configuration, preprocessedInput);
            if (cachedEach != null) {
                return cachedEach;
            }
        }
        
        final Each each = Each.parse(preprocessedInput);
        
        if (each == null) {
            throw new TemplateProcessingException("Could not parse as each: \"" + input + "\"");
        }
        
        if (configuration != null) {
            CACHE.putEachIntoCache(configuration, preprocessedInput, each);
        }
        
        return each;

    }
    

    
    FragmentSelection parseFragmentSelection(final Configuration configuration, final IProcessingContext processingContext, final String input, final boolean preprocess) {
        
        final String trimmedInput = input.trim();
        
        final String preprocessedInput =
            (preprocess?
                    preprocess(configuration, processingContext, trimmedInput) :
                    trimmedInput);

        if (configuration != null) {
            final FragmentSelection cachedFragmentSelection = CACHE.getFragmentSelectionFromCache(configuration, preprocessedInput);
            if (cachedFragmentSelection != null) {
                return cachedFragmentSelection;
            }
        }
        
        final FragmentSelection fragmentSelection = FragmentSelection.parse(preprocessedInput);
        
        if (fragmentSelection == null) {
            throw new TemplateProcessingException("Could not parse as fragment selection: \"" + input + "\"");
        }
        
        if (configuration != null) {
            CACHE.putFragmentSelectionIntoCache(configuration, preprocessedInput, fragmentSelection);
        }
        
        return fragmentSelection;

    }
    
    

    
    
    String preprocess(final Configuration configuration, 
            final IProcessingContext processingContext, final String input) {

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
                            configuration, processingContext, matcher.group(1), false);
                if (expression == null) {
                    return null;
                }
                
                final Object result =
                    this.executor.executeExpression(
                            configuration, processingContext, expression, StandardExpressionExecutionContext.PREPROCESSING);
                
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
