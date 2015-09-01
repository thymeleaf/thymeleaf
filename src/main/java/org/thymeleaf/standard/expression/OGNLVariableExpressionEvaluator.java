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
import org.thymeleaf.standard.util.StandardExpressionUtils;

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
            if (StandardExpressionUtils.mightNeedExpressionObjects(expression)) {

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
                if (expContext.getRestrictVariableAccess()) {
                    contextVariablesMap.put(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                } else {
                    contextVariablesMap.remove(OGNLVariablesMapPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                }

            } else {

                if (expContext.getRestrictVariableAccess()) {
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
            return ((OGNLShortcutExpression) parsedExpression).evaluate(processingContext, context, root);
        }

        // We create the OgnlContext here instead of just sending the Map as context because that prevents OGNL from
        // creating the OgnlContext empty and then setting the context Map variables one by one
        final OgnlContext ognlContext = new OgnlContext(context);
        return ognl.Ognl.getValue(parsedExpression, ognlContext, root);

    }



}
