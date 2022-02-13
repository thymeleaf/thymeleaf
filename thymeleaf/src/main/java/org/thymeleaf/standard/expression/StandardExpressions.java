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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;


/**
 * <p>
 *   Utility class for the easy obtention of objects relevant to the parsing and execution of Thymeleaf
 *   Standard Expressions.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressions {


    /**
     * Name used for registering the <i>Standard Variable Expression Evaluator</i> object as an
     * <i>execution attribute</i> at the Standard Dialects.
     */
    public static final String STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME = "StandardVariableExpressionEvaluator";

    /**
     * Name used for registering the <i>Standard Expression Parser</i> object as an
     * <i>execution attribute</i> at the Standard Dialects.
     */
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";

    /**
     * Name used for registering the <i>Standard Conversion Service</i> object as an
     * <i>execution attribute</i> at the Standard Dialects.
     */
    public static final String STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME = "StandardConversionService";




    private StandardExpressions() {
        super();
    }


    /**
     * <p>
     *   Obtain the expression parser (implementation of {@link IStandardExpressionParser}) registered by
     *   the Standard Dialect that is being currently used.
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return the parser object.
     */
    public static IStandardExpressionParser getExpressionParser(final IEngineConfiguration configuration) {
        final Object parser =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME);
        if (parser == null || (!(parser instanceof IStandardExpressionParser))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Parser has been registered as an execution argument. " +
                    "This is a requirement for using Standard Expressions, and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardExpressionParser.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardExpressionParser) parser;
    }



    /**
     * <p>
     *   Obtain the variable expression evaluator (implementation of {@link IStandardVariableExpressionEvaluator})
     *   registered by the Standard Dialect that is being currently used.
     * </p>
     * <p>
     *   Normally, there should be no need to obtain this object from the developers' code (only internally from
     *   {@link IStandardExpression} implementations).
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return the variable expression evaluator object.
     */
    public static IStandardVariableExpressionEvaluator getVariableExpressionEvaluator(final IEngineConfiguration configuration) {
        final Object expressionEvaluator =
                configuration.getExecutionAttributes().get(STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME);
        if (expressionEvaluator == null || (!(expressionEvaluator instanceof IStandardVariableExpressionEvaluator))) {
            throw new TemplateProcessingException(
                    "No Standard Variable Expression Evaluator has been registered as an execution argument. " +
                    "This is a requirement for using Standard Expressions, and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardVariableExpressionEvaluator.class.getName() + " with name " +
                    "\"" + STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardVariableExpressionEvaluator) expressionEvaluator;
    }





    /**
     * <p>
     *   Obtain the conversion service (implementation of {@link IStandardConversionService}) registered by
     *   the Standard Dialect that is being currently used.
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return the conversion service object.
     */
    public static IStandardConversionService getConversionService(final IEngineConfiguration configuration) {
        final Object conversionService =
                configuration.getExecutionAttributes().get(STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME);
        if (conversionService == null || (!(conversionService instanceof IStandardConversionService))) {
            throw new TemplateProcessingException(
                    "No Standard Conversion Service has been registered as an execution argument. " +
                    "This is a requirement for using Standard Expressions, and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardConversionService.class.getName() + " with name " +
                    "\"" + STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardConversionService) conversionService;
    }


}
