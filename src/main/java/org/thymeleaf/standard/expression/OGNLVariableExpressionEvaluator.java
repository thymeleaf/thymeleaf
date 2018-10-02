/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.util.StandardExpressionUtils;

/**
 * <p>
 *   Evaluator for variable expressions ({@code ${...}}) in Thymeleaf Standard Expressions, using the
 *   OGNL expression language.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0.9, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class OGNLVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {
    
    
    private static final Logger logger = LoggerFactory.getLogger(OGNLVariableExpressionEvaluator.class);

    private static final String EXPRESSION_CACHE_TYPE_OGNL = "ognl";


    private static Map<String,Object> CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS =
            (Map<String,Object>) (Map<?,?>)Collections.singletonMap(
                    OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS,
                    OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);


    private final boolean applyOGNLShortcuts;




    public OGNLVariableExpressionEvaluator(final boolean applyOGNLShortcuts) {

        super();

        this.applyOGNLShortcuts = applyOGNLShortcuts;

        /*
         * INITIALIZE AND REGISTER THE PROPERTY ACCESSOR
         */
        final OGNLContextPropertyAccessor accessor = new OGNLContextPropertyAccessor();
        OgnlRuntime.setPropertyAccessor(IContext.class, accessor);

    }




    public final Object evaluate(
            final IExpressionContext context,
            final IStandardVariableExpression expression,
            final StandardExpressionExecutionContext expContext) {
        return evaluate(context, expression, expContext, this.applyOGNLShortcuts);
    }




    private static Object evaluate(
        final IExpressionContext context,
        final IStandardVariableExpression expression,
        final StandardExpressionExecutionContext expContext,
        final boolean applyOGNLShortcuts) {
       
        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression.getExpression());
            }

            final IEngineConfiguration configuration = context.getConfiguration();

            final String exp = expression.getExpression();
            final boolean useSelectionAsRoot = expression.getUseSelectionAsRoot();

            if (exp == null) {
                throw new TemplateProcessingException("Expression content is null, which is not allowed");
            }

            final ComputedOGNLExpression parsedExpression =
                    obtainComputedOGNLExpression(configuration, expression, exp, applyOGNLShortcuts);

            final Map<String,Object> contextVariablesMap;
            if (parsedExpression.mightNeedExpressionObjects) {

                // The IExpressionObjects implementation returned by processing contexts that include the Standard
                // Dialects will be lazy in the creation of expression objects (i.e. they won't be created until really
                // needed). And in order for this behaviour to be accepted by OGNL, we will be wrapping this object
                // inside an implementation of Map<String,Object>, which will afterwards be fed to the constructor
                // of an OgnlContext object.

                // Note this will never happen with shortcut expressions, as the '#' character with which all
                // expression object names start is not allowed by the OGNLShortcutExpression parser.

                final IExpressionObjects expressionObjects = context.getExpressionObjects();
                contextVariablesMap = new OGNLExpressionObjectsWrapper(expressionObjects, expContext.getRestrictVariableAccess());

                // We might need to apply restrictions on the request parameters. In the case of OGNL, the only way we
                // can actually communicate with the PropertyAccessor, (OGNLVariablesMapPropertyAccessor), which is the
                // agent in charge of applying such restrictions, is by adding a context variable that the property accessor
                // can later lookup during evaluation.
                if (expContext.getRestrictVariableAccess()) {
                    contextVariablesMap.put(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                } else {
                    contextVariablesMap.remove(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
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
            final ITemplateContext templateContext = (context instanceof ITemplateContext ? (ITemplateContext) context : null);
            final Object evaluationRoot =
                    (useSelectionAsRoot && templateContext != null && templateContext.hasSelectionTarget()? templateContext.getSelectionTarget() : templateContext);

            // Execute the expression!
            final Object result;
            try {
                result = executeExpression(configuration, parsedExpression.expression, contextVariablesMap, evaluationRoot);
            } catch (final OGNLShortcutExpression.OGNLShortcutExpressionNotApplicableException notApplicable) {
                // We tried to apply shortcuts, but it is not possible for this expression even if it parsed OK,
                // so we need to empty the cache and try again disabling shortcuts. Once processed for the first time,
                // an OGNL (non-shortcut) parsed expression will already be cached and this exception will not be
                // thrown again
                invalidateComputedOGNLExpression(configuration, expression, exp);
                return evaluate(context, expression, expContext, false);
            }

            if (!expContext.getPerformTypeConversion()) {
                return result;
            }

            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            return conversionService.convert(context, result, String.class);
            
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + expression.getExpression() + "\"", e);
        }
        
    }



    
    private static ComputedOGNLExpression obtainComputedOGNLExpression(
            final IEngineConfiguration configuration, final IStandardVariableExpression expression, final String exp,
            final boolean applyOGNLShortcuts) throws OgnlException {

        if (expression instanceof VariableExpression) {

            final VariableExpression vexpression = (VariableExpression) expression;

            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && cachedExpression instanceof ComputedOGNLExpression) {
                return (ComputedOGNLExpression) cachedExpression;
            }
            cachedExpression = parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);
            if (cachedExpression != null) {
                vexpression.setCachedExpression(cachedExpression);
            }
            return (ComputedOGNLExpression) cachedExpression;

        }

        if (expression instanceof SelectionVariableExpression) {

            final SelectionVariableExpression vexpression = (SelectionVariableExpression) expression;

            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && cachedExpression instanceof ComputedOGNLExpression) {
                return (ComputedOGNLExpression) cachedExpression;
            }
            cachedExpression = parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);
            if (cachedExpression != null) {
                vexpression.setCachedExpression(cachedExpression);
            }
            return (ComputedOGNLExpression) cachedExpression;

        }

        return parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);

    }


    private static ComputedOGNLExpression parseComputedOGNLExpression(
            final IEngineConfiguration configuration, final String exp, final boolean applyOGNLShortcuts)
            throws OgnlException {

        ComputedOGNLExpression parsedExpression =
                (ComputedOGNLExpression) ExpressionCache.getFromCache(configuration, exp, EXPRESSION_CACHE_TYPE_OGNL);
        if (parsedExpression != null) {
            return parsedExpression;
        }
        // The result of parsing might be an OGNL expression AST or a ShortcutOGNLExpression (for simple cases)
        parsedExpression = parseExpression(exp, applyOGNLShortcuts);
        ExpressionCache.putIntoCache(configuration, exp, parsedExpression, EXPRESSION_CACHE_TYPE_OGNL);
        return parsedExpression;

    }


    private static void invalidateComputedOGNLExpression(
            final IEngineConfiguration configuration, final IStandardVariableExpression expression, final String exp) {

        if (expression instanceof VariableExpression) {
            final VariableExpression vexpression = (VariableExpression) expression;
            vexpression.setCachedExpression(null);
        } else if (expression instanceof SelectionVariableExpression) {
            final SelectionVariableExpression vexpression = (SelectionVariableExpression) expression;
            vexpression.setCachedExpression(null);
        }
        ExpressionCache.removeFromCache(configuration, exp, EXPRESSION_CACHE_TYPE_OGNL);

    }
    
    
    
    
    @Override
    public String toString() {
        return "OGNL";
    }








    private static ComputedOGNLExpression parseExpression(final String expression, final boolean applyOGNLShortcuts)
            throws OgnlException {

        final boolean mightNeedExpressionObjects = StandardExpressionUtils.mightNeedExpressionObjects(expression);

        if (applyOGNLShortcuts) {
            final String[] parsedExpression = OGNLShortcutExpression.parse(expression);
            if (parsedExpression != null) {
                return new ComputedOGNLExpression(new OGNLShortcutExpression(parsedExpression), mightNeedExpressionObjects);
            }
        }

        return new ComputedOGNLExpression(ognl.Ognl.parseExpression(expression), mightNeedExpressionObjects);
        
    }



    private static Object executeExpression(
            final IEngineConfiguration configuration, final Object parsedExpression,
            final Map<String,Object> context, final Object root)
            throws Exception {

        if (parsedExpression instanceof OGNLShortcutExpression) {
            return ((OGNLShortcutExpression) parsedExpression).evaluate(configuration, context, root);
        }

        // We create the OgnlContext here instead of just sending the Map as context because that prevents OGNL from
        // creating the OgnlContext empty and then setting the context Map variables one by one
        final OgnlContext ognlContext = new OgnlContext(context);
        return ognl.Ognl.getValue(parsedExpression, ognlContext, root);

    }




    private static final class ComputedOGNLExpression {

        final Object expression;
        final boolean mightNeedExpressionObjects;

        ComputedOGNLExpression(final Object expression, final boolean mightNeedExpressionObjects) {
            super();
            this.expression = expression;
            this.mightNeedExpressionObjects = mightNeedExpressionObjects;
        }


    }



}
