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
package org.thymeleaf.spring3.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 2.0.9
 *
 */
public class SPELVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {


    public static final SPELVariableExpressionEvaluator INSTANCE = new SPELVariableExpressionEvaluator();

    // The reason we will be using a prefix with the expression cache is in order to separate entries coming
    // from this VariableExpressionEvaluator and those coming from the parsing of assignation sequences,
    // each expressions, fragment selections, etc. See org.thymeleaf.standard.expression.ExpressionCache
    private static final String SPEL_CACHE_PREFIX = "ognl|";
    
    
    private static final Logger logger = LoggerFactory.getLogger(SPELVariableExpressionEvaluator.class);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    
    protected SPELVariableExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(
            final IProcessingContext processingContext, final String spelExpression,
            final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), spelExpression);
        }

        try {

            /*
             * TRY TO DELEGATE EVALUATION TO SPRING IF EXPRESSION IS ON A BOUND OBJECT
             */
            if (expContext.getPerformTypeConversion()) {
                // This is a {{...}} expression, so we should use binding info (if available) for formatting.

                if (useSelectionAsRoot || !isLocalVariableOverriding(processingContext, spelExpression)) {
                    // The "local variable override" check avoids scenarios where a locally defined variable
                    // (e.g. the iterated variable in a th:each) has the same name as a bound object (e.g. a
                    // form-backing bean). If this was not detected, the bound object value would be always used
                    // instead of the local variable's

                    final BindStatus bindStatus =
                            FieldUtils.getBindStatusFromParsedExpression(processingContext, true, useSelectionAsRoot, spelExpression);

                    if (bindStatus != null) {
                        // The expression goes against a bound object! Let Spring do its magic for displaying it...
                        return ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);
                    }

                }

            }

            final IEngineConfiguration configuration = processingContext.getConfiguration();


            /*
             * OBTAIN THE EXPRESSION (SpelExpression OBJECT) FROM THE CACHE, OR PARSE IT
             */
            final SpelExpression exp = getExpression(configuration, spelExpression);


            /*
             * COMPUTE EXPRESSION OBJECTS AND ADDITIONAL CONTEXT VARIABLES MAP
             * The IExpressionObjects implementation returned by processing contexts that include the Standard
             * Dialects will be lazy in the creation of expression objects (i.e. they won't be created until really
             * needed).
             */
            final IExpressionObjects expressionObjects =
                    (mightNeedExpressionObjects(spelExpression)? processingContext.getExpressionObjects() : null);


            /*
             * CREATE/OBTAIN THE SPEL EVALUATION CONTEXT OBJECT
             */
            EvaluationContext baseEvaluationContext =
                    (EvaluationContext) processingContext.getVariablesMap().
                            getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);

            if (baseEvaluationContext == null) {
                // Using a standard one as base: we are losing bean resolution and conversion service!!
                baseEvaluationContext = new StandardEvaluationContext();
            }


            /*
             * WRAP THE EVALUATION CONTEXT INTO A STRUCTURE THAT WILL ALLOW US TO RESOLVE VARIABLES
             */
            // TODO IS this needed? if we don't have variables... why not use directly the baseEvaluationContext?
            final ThymeleafEvaluationContextWrapper evaluationContext =
                    new ThymeleafEvaluationContextWrapper(
                            baseEvaluationContext, expressionObjects, expContext.getForbidRequestParameters());


            /*
             * RESOLVE THE EVALUATION ROOT
             */
            final IVariablesMap variablesMap = processingContext.getVariablesMap();
            final Object evaluationRoot = (useSelectionAsRoot? variablesMap.getSelectionTarget() : variablesMap);


            /*
             * If no conversion is to be made, JUST RETURN
             */
            if (!expContext.getPerformTypeConversion()) {
                return exp.getValue(evaluationContext, evaluationRoot);
            }


            /*
             * If a conversion is to be made, OBTAIN THE CONVERSION SERVICE AND EXECUTE IT
             */
            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            if (conversionService instanceof SpringStandardConversionService) {
                // The conversion service is a mere bridge with the Spring ConversionService, therefore
                // this makes use of the complete Spring type conversion infrastructure, without needing
                // to manually execute the conversion.
                return exp.getValue(evaluationContext, evaluationRoot, String.class);
            }

            // We need type conversion, but conversion service is not a mere bridge to the Spring one,
            // so we need manual execution.
            final Object result = exp.getValue(evaluationContext, evaluationRoot);
            return conversionService.convert(processingContext, result, String.class);


        } catch (final TemplateProcessingException e) {
            throw e;
        } catch(final Exception e) {
            throw new TemplateProcessingException(
                    "Exception evaluating SpringEL expression: \"" + spelExpression + "\"", e);
        }
        
    }


    private static SpelExpression getExpression(final IEngineConfiguration configuration, final String spelExpression) {
        
        SpelExpression exp = null;
        ICache<String, Object> cache = null;

        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            cache = cacheManager.getExpressionCache();
            if (cache != null) {
                exp = (SpelExpression) cache.get(configuration.getTextRepository().getText(SPEL_CACHE_PREFIX, spelExpression));
            }
        }

        if (exp == null) {
            exp = (SpelExpression) PARSER.parseExpression(spelExpression);
            if (cache != null && null != exp) {
                cache.put(configuration.getTextRepository().getText(SPEL_CACHE_PREFIX, spelExpression), exp);
            }
        }

        return exp;
        
    }



    private static boolean isLocalVariableOverriding(final IProcessingContext processingContext, final String expression) {
        if (!processingContext.hasLocalVariables()) {
            return false;
        }
        final int dotPos = expression.indexOf('.');
        if (dotPos == -1) {
            return false;
        }
        final String expressionFirstComponent = expression.substring(0,dotPos);
        return processingContext.hasLocalVariable(expressionFirstComponent);
    }




    private static boolean mightNeedExpressionObjects(final String expression) {
        int n = expression.length();
        while (n-- != 0) {
            if (expression.charAt(n) == '#') {
                return true;
            }
        }
        return false;
    }




    @Override
    public String toString() {
        return "SpringEL";
    }


}
