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
package org.thymeleaf.spring3.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IContextVariableRestriction;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
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
    
    
    private static final Logger logger = LoggerFactory.getLogger(SpelVariableExpressionEvaluator.class);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static final StandardEvaluationContext DEFAULT_EVALUATION_CONTEXT = new StandardEvaluationContext();
    
    
    
    protected SpelVariableExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(final Configuration configuration, final IProcessingContext processingContext, 
            final String spelExpression, final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), spelExpression);
        }

        try {
    
            final Map<String,Object> contextVariables = 
                    computeContextVariables(configuration, processingContext);
            
            final SpelEvaluationContext evaluationContext = 
                    new SpelEvaluationContext(DEFAULT_EVALUATION_CONTEXT, contextVariables);

            final SpelExpression exp = getExpression(configuration, spelExpression);
            
            final Object evaluationRoot = 
                    (useSelectionAsRoot?
                            processingContext.getExpressionSelectionEvaluationRoot() :
                            processingContext.getExpressionEvaluationRoot());
            
            setVariableRestrictions(expContext, evaluationRoot, contextVariables);
            
            return exp.getValue(evaluationContext, evaluationRoot);
            
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

    
    
    public Map<String,Object> computeContextVariables(
            final Configuration configuration, final IProcessingContext processingContext) {
        
        final Map<String,Object> contextVariables = new HashMap<String, Object>();
        
        final Map<String,Object> expressionObjects = processingContext.getExpressionObjects();
        if (expressionObjects != null) {
            contextVariables.putAll(expressionObjects);
        }
        
        final Fields fields = new Fields(configuration, processingContext);
        contextVariables.put(FIELDS_EVALUATION_VARIABLE_NAME, fields);
        
        final Map<String,Object> additionalContextVariables = computeAdditionalContextVariables(processingContext);
        if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
            contextVariables.putAll(additionalContextVariables);
        }
        
        return contextVariables;
        
    }
    

    
    /*
     * Meant to be overwritten
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            @SuppressWarnings("unused") final IProcessingContext processingContext) {
        return Collections.emptyMap();
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
    
    

    @Override
    public String toString() {
        return "SpringEL";
    }


}
