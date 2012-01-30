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

import java.util.Collections;
import java.util.Map;

import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ExpressionEvaluationException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.CacheMap;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class OgnlExpressionEvaluator 
        implements IStandardExpressionEvaluator {
    

    public static final OgnlExpressionEvaluator INSTANCE = new OgnlExpressionEvaluator();

    
    private static final Logger logger = LoggerFactory.getLogger(OgnlExpressionEvaluator.class);
    
    private static final int CACHE_MAX_SIZE = 500;
    
    private static final CacheMap<String, Object> CACHE = 
            new CacheMap<String, Object>("OgnlExpressionEvaluator.CACHE", true, 100, CACHE_MAX_SIZE);


    
    public final Object evaluate(final Arguments arguments, 
            final TemplateResolution templateResolution, final String expression, final Object root) {
       
        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }
            
            Object expressionTree = CACHE.get(expression);
            if (expressionTree == null) {
                expressionTree = ognl.Ognl.parseExpression(expression);
                CACHE.put(expression, expressionTree);
            }

            final Map<String,Object> contextVariables = arguments.computeBaseContextVariables(templateResolution);
            
            final Map<String,Object> additionalContextVariables =
                computeAdditionalContextVariables(arguments, templateResolution);
            if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
                contextVariables.putAll(additionalContextVariables);
            }
            
            return ognl.Ognl.getValue(expressionTree, contextVariables, root);
            
        } catch (final OgnlException e) {
            throw new ExpressionEvaluationException(
                    "Exception evaluating OGNL expression", templateResolution.getTemplateName(), expression, e);
        }
        
    }



    
    /*
     * Meant to be overwritten
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            @SuppressWarnings("unused") final Arguments arguments, 
            @SuppressWarnings("unused") final TemplateResolution templateResolution) {
        return Collections.emptyMap();
    }
    
    
    
    
    
    private OgnlExpressionEvaluator() {
        super();
    }

    
    
    
    
    @Override
    public String toString() {
        return "OGNL";
    }
    
}
