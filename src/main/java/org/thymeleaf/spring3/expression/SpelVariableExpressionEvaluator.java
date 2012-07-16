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
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;

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
    private static final StandardEvaluationContext DEFAULT_EVALUATION_CONTEXT;
    
    
    static {
        DEFAULT_EVALUATION_CONTEXT = new StandardEvaluationContext();
        DEFAULT_EVALUATION_CONTEXT.addPropertyAccessor(VariablesMapPropertyAccessor.INSTANCE);
        DEFAULT_EVALUATION_CONTEXT.addPropertyAccessor(BeansPropertyAccessor.INSTANCE);
    }
    
    
    
    protected SpelVariableExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(final Configuration configuration, final IProcessingContext processingContext, 
            final String spelExpression, final boolean useSelectionAsRoot) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), spelExpression);
        }

        try {
            
            final Map<String,Object> contextVariables = processingContext.getBaseContextVariables();
            
            final Fields fields = new Fields(configuration, processingContext);
            contextVariables.put(FIELDS_EVALUATION_VARIABLE_NAME, fields);
            
            final Map<String,Object> additionalContextVariables = computeAdditionalContextVariables(processingContext);
            if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
                contextVariables.putAll(additionalContextVariables);
            }
            
            final SpelEvaluationContext context = 
                    new SpelEvaluationContext(DEFAULT_EVALUATION_CONTEXT, contextVariables);

            final SpelExpression exp = getExpression(configuration, spelExpression);
            
            final Object evaluationRoot = 
                    (useSelectionAsRoot?
                            processingContext.getExpressionSelectionEvaluationRoot() :
                            processingContext.getExpressionEvaluationRoot());
            
            return exp.getValue(context, evaluationRoot);
            
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


    /*
     * Meant to be overwritten
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            @SuppressWarnings("unused") final IProcessingContext processingContext) {
        return Collections.emptyMap();
    }

    
    
    @Override
    public String toString() {
        return "SpringEL";
    }


}
