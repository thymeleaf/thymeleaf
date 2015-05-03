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

import java.io.Serializable;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.integration.PropertyHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.context.WebVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class MVELVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {


    private static final Logger logger = LoggerFactory.getLogger(MVELVariableExpressionEvaluator.class);

    public static final MVELVariableExpressionEvaluator INSTANCE = new MVELVariableExpressionEvaluator();
    private static final String MVEL_CACHE_PREFIX = "{mvel}";







    private MVELVariableExpressionEvaluator() {

        super();

        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        /*
         * INITIALIZE AND REGISTER THE PROPERTY HANDLER
         */
        final MVELVariablesMapPropertyHandler handler = new MVELVariablesMapPropertyHandler();
        PropertyHandlerFactory.registerPropertyHandler(WebVariablesMap.class, handler);
        PropertyHandlerFactory.registerPropertyHandler(VariablesMap.class, handler);

    }





    public final Object evaluate(
            final IProcessingContext processingContext, final String expression,
            final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot) {

        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] MVEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }

            final IEngineConfiguration configuration = processingContext.getConfiguration();

            Object compiledExpression = null;
            ICache<String, Object> cache = null;

            if (configuration != null) {
                final ICacheManager cacheManager = configuration.getCacheManager();
                if (cacheManager != null) {
                    cache = cacheManager.getExpressionCache();
                    if (cache != null) {
                        compiledExpression = (Serializable) cache.get(MVEL_CACHE_PREFIX + expression);
                    }
                }
            }

            if (compiledExpression == null) {
                compiledExpression = MVEL.compileExpression(expression);
                if (cache != null && null != compiledExpression) {
                    cache.put(MVEL_CACHE_PREFIX + expression, compiledExpression);
                }
            }

            // The IExpressionObjects implementation returned by processing contexts that include the Standard
            // Dialects will be lazy in the creation of expression objects (i.e. they won't be created until really
            // needed). But unfortunately, OGNL resolves ALL of the context variables from the specified map when
            // creating the OgnlContext, so even if we have the capacity of not creating the expression objects until
            // we really need them, OGNL will not allow us to do so. Anyway, at least the StandardExpressionObjects
            // implementation will take care of reusing almost all of the objects (except those that depend on the
            // selection target), so that they are not created for each expression -- only for each template.
            final IExpressionObjects contextVariables = processingContext.getExpressionObjects();
            final Map<String,Object> contextVariablesMap = contextVariables.buildMap();

            // We might need to apply restrictions on the request parameters. In the case of OGNL, the only way we
            // can actually communicate with the PropertyAccessor, (MVELVariablesMapPropertyHandler), which is the
            // agent in charge of applying such restrictions, is by adding a context variable that the property accessor
            // can later lookup during evaluation.
            if (expContext.getForbidRequestParameters()) {
                contextVariablesMap.put(MVELVariablesMapPropertyHandler.RESTRICT_REQUEST_PARAMETERS, MVELVariablesMapPropertyHandler.RESTRICT_REQUEST_PARAMETERS);
            } else {
                contextVariablesMap.remove(MVELVariablesMapPropertyHandler.RESTRICT_REQUEST_PARAMETERS);
            }

            // The root object on which we will evaluate expressions will depend on whether a selection target is
            // active or not...
            final Object evaluationRoot =
                    (useSelectionAsRoot?
                            processingContext.getVariablesMap().getSelectionTarget() :
                            processingContext.getVariablesMap());

            // Execute the expression!
            final Object result = MVEL.executeExpression(compiledExpression, evaluationRoot, contextVariablesMap);

            if (!expContext.getPerformTypeConversion()) {
                return result;
            }

            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            return conversionService.convert(processingContext, result, String.class);
            
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Exception evaluating MVEL expression: \"" + expression + "\"", e);
        }
        
    }



    
    

    
    
    
    
    @Override
    public String toString() {
        return "MVEL";
    }
    
    

    
}
