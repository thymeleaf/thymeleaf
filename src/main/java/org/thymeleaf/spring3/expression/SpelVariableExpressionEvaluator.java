/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IContextVariableRestriction;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.StandardVariableRestrictions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 2.0.9
 *
 */
public class SpelVariableExpressionEvaluator 
        implements IStandardVariableExpressionEvaluator {


    public static final SpelVariableExpressionEvaluator INSTANCE = new SpelVariableExpressionEvaluator();
    private static final String SPEL_CACHE_PREFIX = "{spel}";
    
    
    public static final String FIELDS_EVALUATION_VARIABLE_NAME = "fields";
    public static final String THEMES_EVALUATION_VARIABLE_NAME = "themes";
    
    private static final Logger logger = LoggerFactory.getLogger(SpelVariableExpressionEvaluator.class);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    
    protected SpelVariableExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(final Configuration configuration, final IProcessingContext processingContext, 
            final String spelExpression, final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), spelExpression);
        }

        try {


            if (expContext.getPerformTypeConversion()) {
                // This is a *{{...}} expression, so we should use binding info (if available) for formatting.

                if (useSelectionAsRoot || !isLocalVariableOverriding(processingContext, spelExpression)) {
                    // The "local variable override" check avoid scenarios where a locally defined variable
                    // (e.g. the iterated variable in a th:each) has the same name as a bound object (e.g. a
                    // form-backing bean). If this was not detected, the bound object value would be always used
                    // instead of the local variable's

                    final BindStatus bindStatus =
                            FieldUtils.getBindStatusFromParsedExpression(
                                    configuration, processingContext, useSelectionAsRoot, spelExpression);

                    if (bindStatus != null) {
                        // The expression goes against a bound object! Let Spring do its magic for displaying it...
                        return ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);
                    }

                }

            }

            final Map<String,Object> contextVariables =
                    computeExpressionObjects(configuration, processingContext);

            EvaluationContext baseEvaluationContext =
                    (EvaluationContext) processingContext.getContext().getVariables().
                            get(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);

            if (baseEvaluationContext == null) {
                // Using a standard one as base: we are losing bean resolution and conversion service!!
                baseEvaluationContext = new StandardEvaluationContext();
            }

            final ThymeleafEvaluationContextWrapper evaluationContext =
                    new ThymeleafEvaluationContextWrapper(baseEvaluationContext, contextVariables);

            final SpelExpression exp = getExpression(configuration, spelExpression);
            
            final Object evaluationRoot = 
                    (useSelectionAsRoot?
                            processingContext.getExpressionSelectionEvaluationRoot() :
                            processingContext.getExpressionEvaluationRoot());
            
            setVariableRestrictions(expContext, evaluationRoot, contextVariables);

            final Object result = exp.getValue(evaluationContext, evaluationRoot);

            if (!expContext.getPerformTypeConversion()) {
                return result;
            }

            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            return conversionService.convert(configuration, processingContext, result, String.class);


        } catch (final TemplateProcessingException e) {
            throw e;
        } catch(final Exception e) {
            throw new TemplateProcessingException(
                    "Exception evaluating SpringEL expression: \"" + spelExpression + "\"", e);
        }
        
    }


    private static SpelExpression getExpression(final Configuration configuration, final String spelExpression) {
        
        SpelExpression exp = null;
        ICache<String, Object> cache = null;
        
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            cache = cacheManager.getExpressionCache();
            if (cache != null) {
                exp = (SpelExpression) cache.get(SPEL_CACHE_PREFIX + spelExpression);
            }
        }
        
        if (exp == null) {
            exp = (SpelExpression) PARSER.parseExpression(spelExpression);
            if (cache != null && null != exp) {
                cache.put(SPEL_CACHE_PREFIX + spelExpression, exp);
            }
        }

        return exp;
        
    }

    
    public Map<String,Object> computeExpressionObjects(
            final Configuration configuration, final IProcessingContext processingContext) {

        // IProcessingContext.getExpressionObjects() cannot return null, and cannot return an unmodifiable object
        final Map<String,Object> expressionObjects = processingContext.getExpressionObjects();

        final Fields fields = new Fields(configuration, processingContext);
        expressionObjects.put(FIELDS_EVALUATION_VARIABLE_NAME, fields);
        
        if (processingContext.getContext() instanceof IWebContext) {
            final VariablesMap<String,Object> variables = processingContext.getContext().getVariables();
            if (!variables.containsKey(THEMES_EVALUATION_VARIABLE_NAME)) {
                variables.put(THEMES_EVALUATION_VARIABLE_NAME, new Themes(processingContext));
            }
            expressionObjects.put(THEMES_EVALUATION_VARIABLE_NAME, variables.get(THEMES_EVALUATION_VARIABLE_NAME));
        }
        
        final Map<String,Object> additionalExpressionObjects = computeAdditionalExpressionObjects(processingContext);
        if (additionalExpressionObjects != null) {
            expressionObjects.putAll(additionalExpressionObjects);
        }
        
        return expressionObjects;
        
    }
    

    
    /*
     * Meant to be overwritten
     */
    protected Map<String,Object> computeAdditionalExpressionObjects(
            @SuppressWarnings("unused") final IProcessingContext processingContext) {
        return null;
    }

    
    
    protected void setVariableRestrictions(final StandardExpressionExecutionContext expContext, 
            final Object evaluationRoot, final Map<String,Object> contextVariables) {

        final List<IContextVariableRestriction> restrictions =
                (expContext.getForbidRequestParameters()?
                        StandardVariableRestrictions.REQUEST_PARAMETERS_FORBIDDEN : null);

        final Object context = contextVariables.get(ExpressionEvaluatorObjects.CONTEXT_VARIABLE_NAME);
        if (context != null && context instanceof IContext) {
            final VariablesMap<?,?> variablesMap = ((IContext)context).getVariables();
            variablesMap.setRestrictions(restrictions);
        }
        if (evaluationRoot != null && evaluationRoot instanceof VariablesMap<?,?>) {
            ((VariablesMap<?,?>)evaluationRoot).setRestrictions(restrictions);
        }
        
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




    @Override
    public String toString() {
        return "SpringEL";
    }


}
