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
package org.thymeleaf.standard;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExecutionAttributesDialect;
import org.thymeleaf.dialect.IExpressionObjectsDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.OGNLVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardActionTagProcessor;
import org.thymeleaf.standard.processor.StandardAltTitleTagProcessor;
import org.thymeleaf.standard.processor.StandardAssertTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrappendTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;
import org.thymeleaf.standard.processor.StandardBlockTagProcessor;
import org.thymeleaf.standard.processor.StandardCaseTagProcessor;
import org.thymeleaf.standard.processor.StandardClassappendTagProcessor;
import org.thymeleaf.standard.processor.StandardConditionalCommentProcessor;
import org.thymeleaf.standard.processor.StandardConditionalFixedValueTagProcessor;
import org.thymeleaf.standard.processor.StandardDOMEventAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardEachTagProcessor;
import org.thymeleaf.standard.processor.StandardFragmentTagProcessor;
import org.thymeleaf.standard.processor.StandardHrefTagProcessor;
import org.thymeleaf.standard.processor.StandardIfTagProcessor;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineEnablementDocumentProcessor;
import org.thymeleaf.standard.processor.StandardInlineTagProcessor;
import org.thymeleaf.standard.processor.StandardInliningTextProcessor;
import org.thymeleaf.standard.processor.StandardInsertTagProcessor;
import org.thymeleaf.standard.processor.StandardLangXmlLangTagProcessor;
import org.thymeleaf.standard.processor.StandardMethodTagProcessor;
import org.thymeleaf.standard.processor.StandardNonRemovableAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.standard.processor.StandardRemovableAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.standard.processor.StandardReplaceTagProcessor;
import org.thymeleaf.standard.processor.StandardSrcTagProcessor;
import org.thymeleaf.standard.processor.StandardStyleappendTagProcessor;
import org.thymeleaf.standard.processor.StandardSubstituteByTagProcessor;
import org.thymeleaf.standard.processor.StandardSwitchTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.standard.processor.StandardTranslationDocTypeProcessor;
import org.thymeleaf.standard.processor.StandardUnlessTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;
import org.thymeleaf.standard.processor.StandardValueTagProcessor;
import org.thymeleaf.standard.processor.StandardWithTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlBaseTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlLangTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlSpaceTagProcessor;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public class StandardDialect
            extends AbstractProcessorDialect
            implements IExecutionAttributesDialect, IExpressionObjectsDialect {

    private static final String NAME = "Standard";
    private static final String PREFIX = "th";


    private final IExpressionObjectFactory STANDARD_EXPRESSION_OBJECTS_FACTORY = new StandardExpressionObjectFactory();


    // We will avoid setting this variableExpressionEvaluator variable to "OgnlVariableExprtessionEvalutator.INSTANCE"
    // in order to not cause this OGNL-related class to initialize, therefore introducing a forced dependency on OGNL
    // to Spring users (who don't need OGNL at all).
    private IStandardVariableExpressionEvaluator variableExpressionEvaluator = null;
    private IStandardExpressionParser expressionParser = new StandardExpressionParser();
    private IStandardConversionService conversionService = new StandardConversionService();






    public StandardDialect() {
        super(NAME, PREFIX);
    }


    /*
     * Meant to be overridden by dialects that do almost the same as this, changing bits here and there
     * (e.g. SpringStandardDialect)
     */
    protected StandardDialect(final String name, final String prefix) {
        super(name, prefix);
    }




    /**
     * <p>
     *   Returns the variable expression evaluator (implementation of {@link IStandardVariableExpressionEvaluator})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This is used for executing all ${...} and *{...} expressions in Thymeleaf Standard Expressions.
     * </p>
     * <p>
     *   This will be {@link OGNLVariableExpressionEvaluator} by default. When using the Spring Standard
     *   Dialect, this will be a SpringEL-based implementation.
     * </p>
     *
     * @return the Standard Variable Expression Evaluator object.
     * @since 2.1.0
     */
    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        if (this.variableExpressionEvaluator == null) {
            this.variableExpressionEvaluator = new OGNLVariableExpressionEvaluator(true);
        }
        return this.variableExpressionEvaluator;
    }


    /**
     * <p>
     *   Sets the variable expression evaluator (implementation of {@link IStandardVariableExpressionEvaluator})
     *   that should be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This is used for executing all ${...} and *{...} expressions in Thymeleaf Standard Expressions.
     * </p>
     * <p>
     *   This will be an {@link OGNLVariableExpressionEvaluator} by default. When using the Spring Standard
     *   Dialect, this will be a SpringEL-based implementation.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param variableExpressionEvaluator the new Standard Variable Expression Evaluator object. Cannot be null.
     * @since 2.1.0
     */
    public void setVariableExpressionEvaluator(final IStandardVariableExpressionEvaluator variableExpressionEvaluator) {
        Validate.notNull(variableExpressionEvaluator, "Standard Variable Expression Evaluator cannot be null");
        this.variableExpressionEvaluator = variableExpressionEvaluator;
    }


    /**
     * <p>
     *   Returns the Thymeleaf Standard Expression parser (implementation of {@link IStandardExpressionParser})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardExpressionParser} by default.
     * </p>
     *
     * @return the Standard Expression Parser object.
     * @since 2.1.0
     */
    public IStandardExpressionParser getExpressionParser() {
        return this.expressionParser;
    }


    /**
     * <p>
     *   Sets the Thymeleaf Standard Expression parser (implementation of {@link IStandardExpressionParser})
     *   that should be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardExpressionParser} by default.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param expressionParser the Standard Expression Parser object to be used. Cannot be null.
     * @since 2.1.0
     */
    public void setExpressionParser(final IStandardExpressionParser expressionParser) {
        Validate.notNull(expressionParser, "Standard Expression Parser cannot be null");
        this.expressionParser = expressionParser;
    }


    /**
     * <p>
     *   Returns the Standard Conversion Service (implementation of {@link IStandardConversionService})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardConversionService} by default. In Spring environments, this will default
     *   to an implementation delegating on Spring's own ConversionService implementation.
     * </p>
     *
     * @return the Standard Conversion Service object.
     * @since 2.1.0
     */
    public IStandardConversionService getConversionService() {
        return this.conversionService;
    }


    /**
     * <p>
     *   Sets the Standard Conversion Service (implementation of {@link IStandardConversionService})
     *   that should to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardConversionService} by default. In Spring environments, this will default
     *   to an implementation delegating on Spring's own ConversionService implementation.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param conversionService the Standard ConversionService object to be used. Cannot be null.
     * @since 2.1.0
     */
    public void setConversionService(final IStandardConversionService conversionService) {
        Validate.notNull(conversionService, "Standard Conversion Service cannot be null");
        this.conversionService = conversionService;
    }









    public Map<String, Object> getExecutionAttributes() {

        final Map<String,Object> executionAttributes = new HashMap<String, Object>(5, 1.0f);
        executionAttributes.put(
                StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, getVariableExpressionEvaluator());
        executionAttributes.put(
                StandardExpressions.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, getExpressionParser());
        executionAttributes.put(
                StandardExpressions.STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME, getConversionService());

        return executionAttributes;

    }




    public IExpressionObjectFactory getExpressionObjectFactory() {
        return STANDARD_EXPRESSION_OBJECTS_FACTORY;
    }




    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        return createStandardProcessorsSet(dialectPrefix);
    }




    /**
     * <p>
     *   Create a the set of Standard processors, all of them freshly instanced.
     * </p>
     *
     * @return the set of Standard processors.
     */
    public static Set<IProcessor> createStandardProcessorsSet(final String dialectPrefix) {
        /*
         * It is important that we create new instances here because, if there are
         * several dialects in the TemplateEngine that extend StandardDialect, they should
         * not be returning the exact same instances for their processors in order
         * to allow specific instances to be directly linked with their owner dialect.
         */
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();

        /*
         * ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardActionTagProcessor(dialectPrefix));
        processors.add(new StandardAltTitleTagProcessor(dialectPrefix));
        processors.add(new StandardAssertTagProcessor(dialectPrefix));
        processors.add(new StandardAttrTagProcessor(dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(dialectPrefix));
        processors.add(new StandardCaseTagProcessor(dialectPrefix));
        processors.add(new StandardClassappendTagProcessor(dialectPrefix));
        for (final String attrName : StandardConditionalFixedValueTagProcessor.ATTR_NAMES) {
            processors.add(new StandardConditionalFixedValueTagProcessor(dialectPrefix, attrName));
        }
        for (final String attrName : StandardDOMEventAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardEachTagProcessor(dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(dialectPrefix));
        processors.add(new StandardHrefTagProcessor(dialectPrefix));
        processors.add(new StandardIfTagProcessor(dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(dialectPrefix));
        processors.add(new StandardInlineTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(dialectPrefix));
        processors.add(new StandardLangXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardMethodTagProcessor(dialectPrefix));
        for (final String attrName : StandardNonRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardNonRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardObjectTagProcessor(dialectPrefix));
        for (final String attrName : StandardRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardRemoveTagProcessor(dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(dialectPrefix));
        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new StandardStyleappendTagProcessor(dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(dialectPrefix));
        processors.add(new StandardTextTagProcessor(dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(dialectPrefix));
        processors.add(new StandardUtextTagProcessor(dialectPrefix));
        processors.add(new StandardValueTagProcessor(dialectPrefix));
        processors.add(new StandardWithTagProcessor(dialectPrefix));
        processors.add(new StandardXmlBaseTagProcessor(dialectPrefix));
        processors.add(new StandardXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardXmlSpaceTagProcessor(dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(dialectPrefix));
//        processors.add(new StandardDefaultAttributesTagProcessor());

        /*
         * ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(dialectPrefix));

        /*
         * TEXT PROCESSORS
         */
        processors.add(new StandardInliningTextProcessor());

        /*
         * DOCTYPE PROCESSORS
         */
        processors.add(new StandardTranslationDocTypeProcessor());

        /*
         * COMMENT PROCESSORS
         */
        processors.add(new StandardConditionalCommentProcessor());

        /*
         * DOCUMENT PROCESSORS
         */
        processors.add(new StandardInlineEnablementDocumentProcessor());

        return processors;
    }


}
