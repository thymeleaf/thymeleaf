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
package org.thymeleaf.spring5.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.spring5.util.SpringVersionUtils;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpression;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.SelectionVariableExpression;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.util.StandardExpressionUtils;
import org.thymeleaf.util.ClassLoaderUtils;

/**
 * <p>
 *   Evaluator for variable expressions ({@code ${...}}) in Thymeleaf Standard Expressions, using the
 *   SpringEL expression language.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0.9, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 3.0.3
 *
 */
public class SPELVariableExpressionEvaluator
        implements IStandardVariableExpressionEvaluator {


    public static final SPELVariableExpressionEvaluator INSTANCE = new SPELVariableExpressionEvaluator();

    private static final String EXPRESSION_CACHE_TYPE_SPEL = "spel";
    
    
    private static final Logger logger = LoggerFactory.getLogger(SPELVariableExpressionEvaluator.class);

    private static final SpelExpressionParser PARSER_WITHOUT_COMPILED_SPEL = new SpelExpressionParser();
    private static final SpelExpressionParser PARSER_WITH_COMPILED_SPEL;


    /*
     *  INITIALIZATION OF THE Spring EL parser.
     *  Two parsers will be always initialized: one with expression compilation enabled (if the Spring version allows)
     *  and another one without. Then during template execution we will check which one should be used.
     */
    static {

        SpelExpressionParser spelCompilerExpressionParser = null;
        if (SpringVersionUtils.isSpring41AtLeast()) {
            try {
                // Enable the SpEL compiler, in MIXED mode (not IMMEDIATE) in order to avoid ClassCastExceptions
                // when executing the same compiled expression against targets of different classes.
                final SpelParserConfiguration spelParserConfiguration =
                        new SpelParserConfiguration(
                                SpelCompilerMode.MIXED,
                                ClassLoaderUtils.getClassLoader(SPELVariableExpressionEvaluator.class));
                spelCompilerExpressionParser = new SpelExpressionParser(spelParserConfiguration);
            } catch (final Throwable t) {
                if (logger.isDebugEnabled()) {
                    // We are issuing a WARN even if we checked for DEBUG, but in this case we will log the entire
                    // exception trace (if DEBUG is not available, we will avoid polluting the log).
                    logger.warn(
                            "An error happened during the initialization of the Spring EL expression compiler. " +
                            "However, initialization was completed anyway. Note that compilation of SpEL expressions " +
                            "will not be available even if you configure your Spring dialect to use them.", t);
                } else {
                    logger.warn(
                            "An error happened during the initialization of the Spring EL expression compiler. " +
                            "However, initialization was completed anyway. Note that compilation of SpEL expressions " +
                            "will not be available even if you configure your Spring dialect to use them. For more " +
                            "info, set your log to at least DEBUG level: " + t.getMessage());
                }
            }
        }

        PARSER_WITH_COMPILED_SPEL = spelCompilerExpressionParser;

    }


    protected SPELVariableExpressionEvaluator() {
        super();
    }
    
    
    
    
    public final Object evaluate(
            final IExpressionContext context,
            final IStandardVariableExpression expression,
            final StandardExpressionExecutionContext expContext) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression.getExpression());
        }

        try {

            final String spelExpression = expression.getExpression();
            final boolean useSelectionAsRoot = expression.getUseSelectionAsRoot();

            if (spelExpression == null) {
                throw new TemplateProcessingException("Expression content is null, which is not allowed");
            }

            /*
             * TRY TO DELEGATE EVALUATION TO SPRING IF EXPRESSION IS ON A BOUND OBJECT
             */
            if (expContext.getPerformTypeConversion()) {
                // This is a {{...}} expression, so we should use binding info (if available) for formatting.

                if (useSelectionAsRoot || !isLocalVariableOverriding(context, spelExpression)) {
                    // The "local variable override" check avoids scenarios where a locally defined variable
                    // (e.g. the iterated variable in a th:each) has the same name as a bound object (e.g. a
                    // form-backing bean). If this was not detected, the bound object value would be always used
                    // instead of the local variable's

                    final IThymeleafBindStatus bindStatus =
                            FieldUtils.getBindStatusFromParsedExpression(context, true, useSelectionAsRoot, spelExpression);

                    if (bindStatus != null) {
                        // The expression goes against a bound object! Let Spring do its magic for displaying it...
                        return SpringValueFormatter.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);
                    }

                }

            }

            final IEngineConfiguration configuration = context.getConfiguration();


            /*
             * OBTAIN THE EXPRESSION (SpelExpression OBJECT) FROM THE CACHE, OR PARSE IT
             */
            final ComputedSpelExpression exp = obtainComputedSpelExpression(configuration, expression, spelExpression);


            /*
             * COMPUTE EXPRESSION OBJECTS AND ADDITIONAL CONTEXT VARIABLES MAP
             * The IExpressionObjects implementation returned by processing contexts that include the Standard
             * Dialects will be lazy in the creation of expression objects (i.e. they won't be created until really
             * needed).
             */
            final IExpressionObjects expressionObjects =
                    (exp.mightNeedExpressionObjects? context.getExpressionObjects() : null);


            /*
             * CREATE/OBTAIN THE SPEL EVALUATION CONTEXT OBJECT
             */
            EvaluationContext evaluationContext =
                    (EvaluationContext) context.
                            getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);

            if (evaluationContext == null) {

                // Using a StandardEvaluationContext one as base: we are losing bean resolution and conversion service!!
                //
                // The ideal scenario is that this is created before processing the page, e.g. at the ThymeleafView
                // class, but it can happen that no ThymeleafView is ever called if we are using the Spring-integrated
                // template engine on a standalone (non-web) scenario...
                //
                // Also, note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being
                // thread-safe). That's why we need to create a new EvaluationContext for each request / template
                // execution, even if it is quite expensive to create because of requiring the initialization of
                // several ConcurrentHashMaps.
                evaluationContext = new ThymeleafEvaluationContextWrapper(new StandardEvaluationContext());

                if (context instanceof IEngineContext) {
                    ((IEngineContext)context).setVariable(
                            ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
                }

            } else if (!(evaluationContext instanceof IThymeleafEvaluationContext)) {

                evaluationContext = new ThymeleafEvaluationContextWrapper(evaluationContext);

                if (context instanceof IEngineContext) {
                    ((IEngineContext)context).setVariable(
                            ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
                }

            }


            /*
             * AT THIS POINT, WE ARE SURE IT IS AN IThymeleafEvaluationContext
             *
             * This is needed in order to be sure we can modify the 'requestParametersRestricted' flag and also the
             * expression objects.
             */
            final IThymeleafEvaluationContext thymeleafEvaluationContext = (IThymeleafEvaluationContext) evaluationContext;


            /*
             * CONFIGURE THE IThymeleafEvaluationContext INSTANCE: expression objects and restrictions
             *
             * NOTE this is possible even if the evaluation context object is shared for the whole template execution
             * because evaluation contexts are not thread-safe and are only used in a single template execution
             */
            thymeleafEvaluationContext.setExpressionObjects(expressionObjects);
            thymeleafEvaluationContext.setVariableAccessRestricted(expContext.getRestrictVariableAccess());


            /*
             * RESOLVE THE EVALUATION ROOT
             */
            final ITemplateContext templateContext = (context instanceof ITemplateContext ? (ITemplateContext) context : null);
            final Object evaluationRoot =
                    (useSelectionAsRoot && templateContext != null && templateContext.hasSelectionTarget()?
                            templateContext.getSelectionTarget() : new SPELContextMapWrapper(context, thymeleafEvaluationContext));


            /*
             * If no conversion is to be made, JUST RETURN
             */
            if (!expContext.getPerformTypeConversion()) {
                return exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot);
            }


            /*
             * If a conversion is to be made, OBTAIN THE CONVERSION SERVICE AND EXECUTE IT
             */
            final IStandardConversionService conversionService =
                    StandardExpressions.getConversionService(configuration);

            if (conversionService instanceof SpringStandardConversionService) {
                // The conversion service is a mere bridge with the Spring ConversionService, therefore
                // this makes use of the complete Spring type conversion infrastructure, without needing
                // to manually execute the conversion.
                return exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot, String.class);
            }

            // We need type conversion, but conversion service is not a mere bridge to the Spring one,
            // so we need manual execution.
            final Object result = exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot);
            return conversionService.convert(context, result, String.class);


        } catch (final TemplateProcessingException e) {
            throw e;
        } catch(final Exception e) {
            throw new TemplateProcessingException(
                    "Exception evaluating SpringEL expression: \"" + expression.getExpression() + "\"", e);
        }
        
    }






    private static ComputedSpelExpression obtainComputedSpelExpression(
            final IEngineConfiguration configuration, final IStandardVariableExpression expression, final String spelExpression) {

        if (expression instanceof VariableExpression) {

            final VariableExpression vexpression = (VariableExpression) expression;

            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && cachedExpression instanceof ComputedSpelExpression) {
                return (ComputedSpelExpression) cachedExpression;
            }
            cachedExpression = getExpression(configuration, spelExpression);
            if (cachedExpression != null) {
                vexpression.setCachedExpression(cachedExpression);
            }
            return (ComputedSpelExpression) cachedExpression;

        }

        if (expression instanceof SelectionVariableExpression) {

            final SelectionVariableExpression vexpression = (SelectionVariableExpression) expression;

            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && cachedExpression instanceof ComputedSpelExpression) {
                return (ComputedSpelExpression) cachedExpression;
            }
            cachedExpression = getExpression(configuration, spelExpression);
            if (cachedExpression != null) {
                vexpression.setCachedExpression(cachedExpression);
            }
            return (ComputedSpelExpression) cachedExpression;

        }

        return getExpression(configuration, spelExpression);

    }


    private static ComputedSpelExpression getExpression(final IEngineConfiguration configuration, final String spelExpression) {

        ComputedSpelExpression exp = null;
        ICache<ExpressionCacheKey, Object> cache = null;

        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            cache = cacheManager.getExpressionCache();
            if (cache != null) {
                exp = (ComputedSpelExpression) cache.get(new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_SPEL,spelExpression));
            }
        }

        if (exp == null) {

            // SELECT THE ADEQUATE SpEL EXPRESSION PARSER depending on whether SpEL compilation is enabled
            final SpelExpressionParser spelExpressionParser =
                    PARSER_WITH_COMPILED_SPEL != null && SpringStandardExpressions.isSpringELCompilerEnabled(configuration)?
                            PARSER_WITH_COMPILED_SPEL : PARSER_WITHOUT_COMPILED_SPEL;

            final SpelExpression spelExpressionObject = (SpelExpression) spelExpressionParser.parseExpression(spelExpression);
            final boolean mightNeedExpressionObjects = StandardExpressionUtils.mightNeedExpressionObjects(spelExpression);

            exp = new ComputedSpelExpression(spelExpressionObject, mightNeedExpressionObjects);

            if (cache != null && null != exp) {
                cache.put(new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_SPEL,spelExpression), exp);
            }

        }

        return exp;
        
    }



    private static boolean isLocalVariableOverriding(final IExpressionContext context, final String expression) {

        if (!(context instanceof IEngineContext)) {
            // We don't even have support for local variables!
            return false;
        }

        // NOTE this IEngineContext interface is internal and should not be used in users' code
        final IEngineContext engineContext = (IEngineContext) context;

        final int dotPos = expression.indexOf('.');
        if (dotPos == -1) {
            return false;
        }
        // Once we extract the first part of the expression, we check whether it is a local variable...
        final String expressionFirstComponent = expression.substring(0, dotPos);
        return engineContext.isVariableLocal(expressionFirstComponent);

    }




    @Override
    public String toString() {
        return "SpringEL";
    }



    private static final class ComputedSpelExpression {

        final SpelExpression expression;
        final boolean mightNeedExpressionObjects;

        ComputedSpelExpression(final SpelExpression expression, final boolean mightNeedExpressionObjects) {
            super();
            this.expression = expression;
            this.mightNeedExpressionObjects = mightNeedExpressionObjects;
        }


    }


}
