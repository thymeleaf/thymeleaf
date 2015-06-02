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

import java.util.Collections;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.EvaluationUtil;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9 (reimplemented in 3.0.0)
 *
 */
public final class OGNLVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {
    
    
    private static final Logger logger = LoggerFactory.getLogger(OGNLVariableExpressionEvaluator.class);

    // The reason we will be using a prefix with the expression cache is in order to separate entries coming
    // from this VariableExpressionEvaluator and those coming from the parsing of assignation sequences,
    // each expressions, fragment selections, etc. See org.thymeleaf.standard.expression.ExpressionCache
    private static final String OGNL_CACHE_PREFIX = "ognl|";


    private static Map<String,Object> CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS =
            (Map<String,Object>) (Map<?,?>)Collections.singletonMap(
                    OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS,
                    OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);

    private static boolean booleanFixApplied = false;


    private final boolean applyOGNLShortcuts;




    public OGNLVariableExpressionEvaluator(final boolean applyOGNLShortcuts) {

        super();

        this.applyOGNLShortcuts = applyOGNLShortcuts;

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
        return evaluate(processingContext, expression, expContext, useSelectionAsRoot, this.applyOGNLShortcuts);
    }




    private static Object evaluate(
        final IProcessingContext processingContext, final String expression,
        final StandardExpressionExecutionContext expContext, final boolean useSelectionAsRoot,
        final boolean applyOGNLShortcuts) {
       
        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }

            final IEngineConfiguration configuration = processingContext.getConfiguration();
            
            Object parsedExpression = ExpressionCache.getFromCache(configuration, expression, OGNL_CACHE_PREFIX);
            if (parsedExpression == null) {
                // The result of parsing might be an OGNL expression AST or a ShortcutOGNLExpression (for simple cases)
                parsedExpression = parseExpression(expression, applyOGNLShortcuts);
                ExpressionCache.putIntoCache(configuration, expression, parsedExpression, OGNL_CACHE_PREFIX);
            }

            final Map<String,Object> contextVariablesMap;
            if (mightNeedExpressionObjects(expression)) {

                // The IExpressionObjects implementation returned by processing contexts that include the Standard
                // Dialects will be lazy in the creation of expression objects (i.e. they won't be created until really
                // needed). And in order for this behaviour to be accepted by OGNL, we will be wrapping this object
                // inside an implementation of Map<String,Object>, which will afterwards be fed to the constructor
                // of an OgnlContext object.

                // Note this will never happen with shortcut expressions, as the '#' character with which all
                // expression object names start is not allowed by the OGNLShortcutExpression parser.

                final IExpressionObjects expressionObjects = processingContext.getExpressionObjects();
                contextVariablesMap = new OGNLContextExpressionObjectsWrapper(expressionObjects);

                // We might need to apply restrictions on the request parameters. In the case of OGNL, the only way we
                // can actually communicate with the PropertyAccessor, (OGNLVariablesMapPropertyAccessor), which is the
                // agent in charge of applying such restrictions, is by adding a context variable that the property accessor
                // can later lookup during evaluation.
                if (expContext.getForbidRequestParameters()) {
                    contextVariablesMap.put(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                } else {
                    contextVariablesMap.remove(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                }

            } else {

                if (expContext.getForbidRequestParameters()) {
                    contextVariablesMap = CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS;
                } else {
                    contextVariablesMap = Collections.EMPTY_MAP;
                }

            }


            // The root object on which we will evaluate expressions will depend on whether a selection target is
            // active or not...
            final IVariablesMap variablesMap = processingContext.getVariables();
            final Object evaluationRoot =
                    (useSelectionAsRoot && variablesMap.hasSelectionTarget()? variablesMap.getSelectionTarget() : variablesMap);

            // Execute the expression!
            final Object result;
            try {
                result = executeExpression(processingContext, parsedExpression, contextVariablesMap, evaluationRoot);
            } catch (final OGNLShortcutExpression.OGNLShortcutExpressionNotApplicableException notApplicable) {
                // We tried to apply shortcuts, but it is not possible for this expression even if it parsed OK,
                // so we need to empty the cache and try again disabling shortcuts. Once processed for the first time,
                // an OGNL (non-shortcut) parsed expression will already be cached and this exception will not be
                // thrown again
                ExpressionCache.removeFromCache(configuration, expression, OGNL_CACHE_PREFIX);
                return evaluate(processingContext, expression, expContext, useSelectionAsRoot, false);
            }

            if (!expContext.getPerformTypeConversion()) {
                return result;
            }

            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            return conversionService.convert(processingContext, result, String.class);
            
        } catch (final Exception e) {
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
    







    private static Object parseExpression(final String expression, final boolean applyOGNLShortcuts)
            throws OgnlException {
        if (applyOGNLShortcuts) {
            final String[] parsedExpression = OGNLShortcutExpression.parse(expression);
            if (parsedExpression != null) {
                return new OGNLShortcutExpression(parsedExpression);
            }
        }
        return ognl.Ognl.parseExpression(expression);
    }



    private static Object executeExpression(
            final IProcessingContext processingContext, final Object parsedExpression,
            final Map<String,Object> context, final Object root)
            throws Exception {

        if (parsedExpression instanceof OGNLShortcutExpression) {
            return ((OGNLShortcutExpression) parsedExpression).evaluate(processingContext, root);
        }

        // We create the OgnlContext here instead of just sending the Map as context because that prevents OGNL from
        // creating the OgnlContext empty and then setting the context Map variables one by one
        final OgnlContext ognlContext = new OgnlContext(context);
        return ognl.Ognl.getValue(parsedExpression, ognlContext, root);

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


}
