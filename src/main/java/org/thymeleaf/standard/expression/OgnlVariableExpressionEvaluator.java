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

import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.context.IProcessingContext;
import org.thymeleaf.aurora.context.IVariablesMap;
import org.thymeleaf.aurora.expression.IExpressionObjects;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.EvaluationUtil;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class OGNLVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {
    
    
    private static final Logger logger = LoggerFactory.getLogger(OGNLVariableExpressionEvaluator.class);

    public static final OGNLVariableExpressionEvaluator INSTANCE = new OGNLVariableExpressionEvaluator();
    private static final String OGNL_CACHE_PREFIX = "{ognl}";


    private static boolean booleanFixApplied = false;







    private OGNLVariableExpressionEvaluator() {

        super();

        /*
         * INITIALIZE AND REGISTER THE PROPERTY ACCESSOR
         */
        final OGNLVariablesMapPropertyAccessor accessor = new OGNLVariablesMapPropertyAccessor();
        OgnlRuntime.setPropertyAccessor(IVariablesMap.class, accessor);

        /*
         * APPLY THE BOOLEAN EVALUATION FIX (so that ${!'false'} evaluates as true)
         */
        if (!booleanFixApplied && shouldApplyOgnlBooleanFix()) {
            applyOgnlBooleanFix();
            booleanFixApplied = true;
        }

    }





    public final Object evaluate(
            final IProcessingContext processingContext, final String expression,
            final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot) {
       
        try {

            // TODO OGNL now has compiled expressions... maybe worth trying?

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }

            final IEngineConfiguration configuration = processingContext.getConfiguration();
            
            Object expressionTree = null;
            ICache<String, Object> cache = null;
            
            if (configuration != null) {
                final ICacheManager cacheManager = configuration.getCacheManager();
                if (cacheManager != null) {
                    cache = cacheManager.getExpressionCache();
                    if (cache != null) {
                        expressionTree = cache.get(OGNL_CACHE_PREFIX + expression);
                    }
                }
            }
            
            if (expressionTree == null) {
                expressionTree = ognl.Ognl.parseExpression(expression);
                if (cache != null && null != expressionTree) {
                    cache.put(OGNL_CACHE_PREFIX + expression, expressionTree);
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
            // can actually communicate with the PropertyAccessor, (OGNLVariablesMapPropertyAccessor), which is the
            // agent in charge of applying such restrictions, is by adding a context variable that the property accessor
            // can later lookup during evaluation.
            if (expContext.getForbidRequestParameters()) {
                contextVariablesMap.put(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
            } else {
                contextVariablesMap.remove(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
            }

            // The root object on which we will evaluate expressions will depend on whether a selection target is
            // active or not...
            final Object evaluationRoot =
                    (useSelectionAsRoot?
                            processingContext.getVariablesMap().getSelectionTarget() :
                            processingContext.getVariablesMap());

            // Execute the expression!
            final Object result = Ognl.getValue(expressionTree, contextVariablesMap, evaluationRoot);

            if (!expContext.getPerformTypeConversion()) {
                return result;
            }

            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            return conversionService.convert(processingContext, result, String.class);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + expression + "\"", e);
        }
        
    }



    
    

    
    
    
    
    @Override
    public String toString() {
        return "OGNL";
    }
    
    
    
    /**
     * <p>
     *   Determines whether a fix should be applied to OGNL in order
     *   to evaluate Strings as booleans in the same way as 
     *   Thymeleaf does ('false', 'off' and 'no' are actually "false"
     *   instead of OGNL's default "true"). 
     * </p>
     * 
     * @return whether the OGNL boolean fix should be applied or not.
     */
    protected boolean shouldApplyOgnlBooleanFix() {
        return true;
    }
        
    
    
    private static void applyOgnlBooleanFix() {
        
        try {
            
            final ClassLoader classLoader = 
                    ClassLoaderUtils.getClassLoader(OGNLVariableExpressionEvaluator.class);
            
            final ClassPool pool = new ClassPool(true);
            pool.insertClassPath(new LoaderClassPath(classLoader));

            final CtClass[] params = new CtClass[] { pool.get(Object.class.getName()) };
            
            // We must load by class name here instead of "OgnlOps.class.getName()" because
            // the latter would cause the class to be loaded and therefore it would not be
            // possible to modify it.
            final CtClass ognlClass = pool.get("ognl.OgnlOps");
            final CtClass fixClass = pool.get(OGNLVariableExpressionEvaluator.class.getName());
            
            final CtMethod ognlMethod = 
                    ognlClass.getDeclaredMethod("booleanValue", params);
            final CtMethod fixMethod = 
                    fixClass.getDeclaredMethod("fixBooleanValue", params);
            
            ognlMethod.setBody(fixMethod, null);
            
            // Pushes the class to the class loader, effectively making it
            // load the modified version instead of the original one. 
            ognlClass.toClass(classLoader, null);
            
        } catch (final Exception e) {
            // Any exceptions here will be consumed and converted into log messages.
            // An exception at this point could be caused by multiple situations that 
            // should not suppose the stop of the framework's initialization.
            // If the fix cannot not applied, an INFO message is issued and initialization
            // continues normally.
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "Thymeleaf was not able to apply a fix on OGNL's boolean evaluation " +
                        "that would have enabled OGNL to evaluate Strings as booleans (e.g. in " +
                        "\"th:if\") in exactly the same way as Thymeleaf itself or Spring EL ('false', " +
                        "'off' and 'no' should be considered \"false\"). This did not stop the " +
                        "initialization process.", e);
            } else {
                logger.info(
                        "Thymeleaf was not able to apply a fix on OGNL's boolean evaluation " +
                        "that would have enabled OGNL to evaluate Strings as booleans (e.g. in " +
                        "\"th:if\") in exactly the same way as Thymeleaf itself or Spring EL ('false', " +
                        "'off' and 'no' should be considered \"false\"). This did not stop the " +
                        "initialization process. Exception raised was " + e.getClass().getName() + 
                        ": " + e.getMessage() + " [Set the log to TRACE for the complete exception stack trace]");
            }
        }
        
    }
        
        
    static boolean fixBooleanValue(final Object value) {
        // This specifies how evaluation to boolean should be done *INSIDE* OGNL expressions, so the conversion
        // service does not really apply at this point (it will be applied later, on the Standard -not OGNL- expr.)
        return EvaluationUtil.evaluateAsBoolean(value);
    }
    
    
}
