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
package org.thymeleaf.spring3.expression;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ExpressionEvaluationException;
import org.thymeleaf.standard.expression.IStandardExpressionEvaluator;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.CacheMap;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.1
 *
 */
public class SpelExpressionEvaluator 
        implements IStandardExpressionEvaluator {


    public static final SpelExpressionEvaluator INSTANCE = new SpelExpressionEvaluator();
    
    
    public static final String FIELDS_EVALUATION_VARIABLE_NAME = "fields";
    
    
    private static final Logger logger = LoggerFactory.getLogger(SpelExpressionEvaluator.class);
    
    private static final int EXPRESSION_CACHE_SIZE = 500;

    private static final CacheMap<String, SpelExpression> CACHE =
        new CacheMap<String, SpelExpression>("SpelExpressionEvaluator.CACHE", true, 100, EXPRESSION_CACHE_SIZE);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static final StandardEvaluationContext DEFAULT_EVALUATION_CONTEXT;
    
    
    static {
        DEFAULT_EVALUATION_CONTEXT = new StandardEvaluationContext();
        DEFAULT_EVALUATION_CONTEXT.addPropertyAccessor(VariablesMapPropertyAccessor.INSTANCE);
        DEFAULT_EVALUATION_CONTEXT.addPropertyAccessor(BeansPropertyAccessor.INSTANCE);
    }
    
    
    
    private SpelExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final String spelExpression, final Object root) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), spelExpression);
        }

        try {
            
            final Map<String,Object> contextVariables = arguments.computeBaseContextVariables(templateResolution);
            
            final Fields fields = new Fields(arguments, templateResolution);
            contextVariables.put(FIELDS_EVALUATION_VARIABLE_NAME, fields);
            
            final Map<String,Object> additionalContextVariables =
                computeAdditionalContextVariables(arguments, templateResolution);
            if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
                contextVariables.putAll(additionalContextVariables);
            }
            
            final SpelEvaluationContext context = 
                    new SpelEvaluationContext(DEFAULT_EVALUATION_CONTEXT, contextVariables);

            final SpelExpression exp = getExpression(spelExpression);
            
            return exp.getValue(context, root);
            
        } catch(Exception e) {
            throw new ExpressionEvaluationException(
                    "Exception evaluating SpEL expression", templateResolution.getTemplateName(), spelExpression, e);
        }
        
    }


    private static SpelExpression getExpression(final String spelExpression) {
        final SpelExpression cachedExpression = CACHE.get(spelExpression);
        if (cachedExpression != null) {
            return cachedExpression;
        }
        final SpelExpression exp = (SpelExpression) PARSER.parseExpression(spelExpression);
        if (null != exp) {
            CACHE.put(spelExpression, exp);
        }
        return exp;
    }


    /*
     * Meant to be overwritten
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            @SuppressWarnings("unused") final Arguments arguments, @SuppressWarnings("unused") final TemplateResolution templateResolution) {
        return Collections.emptyMap();
    }

    
    
    @Override
    public String toString() {
        return "SpringEL";
    }


}
