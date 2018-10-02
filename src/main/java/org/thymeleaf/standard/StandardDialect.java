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
package org.thymeleaf.standard;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
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
import org.thymeleaf.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.standard.processor.StandardEachTagProcessor;
import org.thymeleaf.standard.processor.StandardFragmentTagProcessor;
import org.thymeleaf.standard.processor.StandardHrefTagProcessor;
import org.thymeleaf.standard.processor.StandardIfTagProcessor;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineEnablementTemplateBoundariesProcessor;
import org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineTextualTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineXMLTagProcessor;
import org.thymeleaf.standard.processor.StandardInliningCDATASectionProcessor;
import org.thymeleaf.standard.processor.StandardInliningCommentProcessor;
import org.thymeleaf.standard.processor.StandardInliningTextProcessor;
import org.thymeleaf.standard.processor.StandardInsertTagProcessor;
import org.thymeleaf.standard.processor.StandardLangXmlLangTagProcessor;
import org.thymeleaf.standard.processor.StandardMethodTagProcessor;
import org.thymeleaf.standard.processor.StandardNonRemovableAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.standard.processor.StandardRefAttributeTagProcessor;
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
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardCSSSerializer;
import org.thymeleaf.standard.serializer.StandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Standard Dialect. This is the class containing the implementation of Thymeleaf Standard Dialect, including all
 *   {@code th:*} processors, expression objects, etc.
 * </p>
 * <p>
 *   Note this dialect uses <strong>OGNL</strong> as an expression language. There is a Spring-integrated version
 *   of the Standard Dialect called the <em>SpringStandard Dialect</em> at the {@code thymeleaf-spring*} packages,
 *   which uses SpringEL as an expression language.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardDialect
            extends AbstractProcessorDialect
            implements IExecutionAttributeDialect, IExpressionObjectDialect {

    public static final String NAME = "Standard";
    public static final String PREFIX = "th";
    public static final int PROCESSOR_PRECEDENCE = 1000;


    // We will avoid setting this variableExpressionEvaluator variable to "OgnlVariableExpressionEvaluator.INSTANCE"
    // in order to not cause this OGNL-related class to initialize, therefore introducing a forced dependency on OGNL
    // to Spring users (who don't need OGNL at all).
    private IStandardVariableExpressionEvaluator variableExpressionEvaluator = null;

    // These variables will be initialized lazily if needed (because no value is set to them via their setters)
    // This should improve startup times (esp. Jackson for the JS serializer) and avoid losing time initializing
    // objects that might not be used after all if they are overridden via setter.
    private IStandardExpressionParser expressionParser = null;
    private IStandardConversionService conversionService = null;
    private IStandardJavaScriptSerializer javaScriptSerializer = null;
    private IStandardCSSSerializer cssSerializer = null;

    // Note this is not settable - just lazily initialized
    private IExpressionObjectFactory expressionObjectFactory = null;






    public StandardDialect() {
        super(NAME, PREFIX, PROCESSOR_PRECEDENCE);
    }


    /*
     * Meant to be overridden by dialects that do almost the same as this, changing bits here and there
     * (e.g. SpringStandardDialect)
     */
    protected StandardDialect(final String name, final String prefix, final int processorPrecedence) {
        super(name, prefix, processorPrecedence);
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
     */
    public IStandardExpressionParser getExpressionParser() {
        if (this.expressionParser == null) {
            this.expressionParser = new StandardExpressionParser();
        }
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
     */
    public IStandardConversionService getConversionService() {
        if (this.conversionService == null) {
            this.conversionService = new StandardConversionService();
        }
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
     */
    public void setConversionService(final IStandardConversionService conversionService) {
        Validate.notNull(conversionService, "Standard Conversion Service cannot be null");
        this.conversionService = conversionService;
    }


    /**
     * <p>
     *   Returns the Standard JavaScript Serializer (implementation of {@link IStandardJavaScriptSerializer})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardJavaScriptSerializer} by default.
     * </p>
     *
     * @return the Standard JavaScript Serializer object.
     */
    public IStandardJavaScriptSerializer getJavaScriptSerializer() {
        if (this.javaScriptSerializer == null) {
            this.javaScriptSerializer = new StandardJavaScriptSerializer(true);
        }
        return this.javaScriptSerializer;
    }


    /**
     * <p>
     *   Sets the Standard JavaScript Serializer (implementation of {@link IStandardJavaScriptSerializer})
     *   that should to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardJavaScriptSerializer} by default.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param javaScriptSerializer the Standard JavaScript Serializer object to be used. Cannot be null.
     */
    public void setJavaScriptSerializer(final IStandardJavaScriptSerializer javaScriptSerializer) {
        Validate.notNull(javaScriptSerializer, "Standard JavaScript Serializer cannot be null");
        this.javaScriptSerializer = javaScriptSerializer;
    }


    /**
     * <p>
     *   Returns the Standard CSS Serializer (implementation of {@link IStandardCSSSerializer})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardCSSSerializer} by default.
     * </p>
     *
     * @return the Standard CSS Serializer object.
     */
    public IStandardCSSSerializer getCSSSerializer() {
        if (this.cssSerializer == null) {
            this.cssSerializer = new StandardCSSSerializer();
        }
        return this.cssSerializer;
    }


    /**
     * <p>
     *   Sets the Standard CSS Serializer (implementation of {@link IStandardCSSSerializer})
     *   that should to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardCSSSerializer} by default.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param cssSerializer the Standard CSS Serializer object to be used. Cannot be null.
     */
    public void setCSSSerializer(final IStandardCSSSerializer cssSerializer) {
        Validate.notNull(cssSerializer, "Standard CSS Serializer cannot be null");
        this.cssSerializer = cssSerializer;
    }









    public Map<String, Object> getExecutionAttributes() {

        final Map<String,Object> executionAttributes = new HashMap<String, Object>(5, 1.0f);
        executionAttributes.put(
                StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, getVariableExpressionEvaluator());
        executionAttributes.put(
                StandardExpressions.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, getExpressionParser());
        executionAttributes.put(
                StandardExpressions.STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME, getConversionService());
        executionAttributes.put(
                StandardSerializers.STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME, getJavaScriptSerializer());
        executionAttributes.put(
                StandardSerializers.STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME, getCSSSerializer());

        return executionAttributes;

    }




    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new StandardExpressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }




    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        return createStandardProcessorsSet(dialectPrefix);
    }




    /**
     * <p>
     *   Create a the set of Standard processors, all of them freshly instanced.
     * </p>
     *
     * @param dialectPrefix the prefix established for the Standard Dialect, needed for initialization
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
         * ------------------------
         * ------------------------
         * HTML TEMPLATE MODE
         * ------------------------
         * ------------------------
         */


        /*
         * HTML: ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardActionTagProcessor(dialectPrefix));
        processors.add(new StandardAltTitleTagProcessor(dialectPrefix));
        processors.add(new StandardAssertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardClassappendTagProcessor(dialectPrefix));
        for (final String attrName : StandardConditionalFixedValueTagProcessor.ATTR_NAMES) {
            processors.add(new StandardConditionalFixedValueTagProcessor(dialectPrefix, attrName));
        }
        for (final String attrName : StandardDOMEventAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardDOMEventAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardEachTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardHrefTagProcessor(dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardInlineHTMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardLangXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardMethodTagProcessor(dialectPrefix));
        for (final String attrName : StandardNonRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardNonRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardObjectTagProcessor(TemplateMode.HTML, dialectPrefix));
        for (final String attrName : StandardRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }
        processors.add(new StandardRemoveTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new StandardStyleappendTagProcessor(dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardValueTagProcessor(dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardXmlBaseTagProcessor(dialectPrefix));
        processors.add(new StandardXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardXmlSpaceTagProcessor(dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.HTML, dialectPrefix));

        /*
         * HTML: ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(TemplateMode.HTML, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));

        /*
         * HTML: TEXT PROCESSORS
         *
         * NOTE the ability of the Standard Inlining mechanism to directly write to output instead of generating
         * internal Strings relies on the fact that there is only ONE ITextProcessor instance for each
         * template mode in the StandardDialect (see AbstractStandardInliner for details). So if new processors
         * are added here, it should be for a really compelling reason.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningTextProcessor(TemplateMode.HTML));

        /*
         * HTML: CDATASection PROCESSORS
         *
         * NOTE as happens with text processors, adding a processor here would convert models in non-reshapable.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.HTML));

        /*
         * HTML: DOCTYPE PROCESSORS
         */
        processors.add(new StandardTranslationDocTypeProcessor());

        /*
         * HTML: COMMENT PROCESSORS
         *
         * NOTE as happens with text processors, adding a processor here would convert models in non-reshapable.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningCommentProcessor(TemplateMode.HTML));
        processors.add(new StandardConditionalCommentProcessor());

        /*
         * HTML: TEMPLATE BOUNDARIES PROCESSORS
         */
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.HTML));



        /*
         * ------------------------
         * ------------------------
         * XML TEMPLATE MODE
         * ------------------------
         * ------------------------
         */


        /*
         * XML: ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardAssertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardInlineXMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.XML, dialectPrefix));

        /*
         * XML: ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(TemplateMode.XML, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));

        /*
         * XML: TEXT PROCESSORS
         *
         * NOTE the ability of the Standard Inlining mechanism to directly write to output instead of generating
         * internal Strings relies on the fact that there is only ONE ITextProcessor instance for each template mode
         * in the StandardDialect (see AbstractStandardInliner for details). So if new processors are added here,
         * it should be for a really compelling reason.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningTextProcessor(TemplateMode.XML));

        /*
         * XML: CDATASection PROCESSORS
         *
         * NOTE as happens with text processors, adding a processor here would convert models in non-reshapable.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.XML));

        /*
         * XML: COMMENT PROCESSORS
         *
         * NOTE as happens with text processors, adding a processor here would convert models in non-reshapable.
         * See EngineConfiguration#isModelReshapable()
         */
        processors.add(new StandardInliningCommentProcessor(TemplateMode.XML));

        /*
         * XML: TEMPLATE BOUNDARIES PROCESSORS
         */
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.XML));



        /*
         * ------------------------
         * ------------------------
         * TEXT TEMPLATE MODE
         * ------------------------
         * ------------------------
         */


        /*
         * TEXT: ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardAssertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.TEXT, dialectPrefix));
        // No th:fragment attribute in text modes: no fragment selection available!
        processors.add(new StandardIfTagProcessor(TemplateMode.TEXT, dialectPrefix));
        // No th:include to be added here, as it is already deprecated since 3.0
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.TEXT, dialectPrefix));
        // No th:substituteby to be added here, as it is already deprecated since 2.1
        processors.add(new StandardSwitchTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.TEXT, dialectPrefix));

        /*
         * TEXT: ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, null, "")); // With no name, will process [# th....] elements

        /*
         * TEXT: TEXT PROCESSORS
         *
         * NOTE the ability of the Standard Inlining mechanism to directly write to output instead of generating
         * internal Strings relies on the fact that there is only ONE ITextProcessor instance for each template mode
         * in the StandardDialect (see AbstractStandardInliner for details). So if new processors are added here,
         * it should be for a really compelling reason.
         */
        processors.add(new StandardInliningTextProcessor(TemplateMode.TEXT));

        /*
         * TEXT: TEMPLATE BOUNDARIES PROCESSORS
         */
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.TEXT));



        /*
         * ------------------------
         * ------------------------
         * JAVASCRIPT TEMPLATE MODE
         * ------------------------
         * ------------------------
         */


        /*
         * JAVASCRIPT: ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardAssertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        // No th:fragment attribute in text modes: no fragment selection available!
        processors.add(new StandardIfTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        // No th:include to be added here, as it is already deprecated since 3.0
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        // No th:substituteby to be added here, as it is already deprecated since 2.1
        processors.add(new StandardSwitchTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));

        /*
         * JAVASCRIPT: ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, null, "")); // With no name, will process [# th....] elements

        /*
         * JAVASCRIPT: TEXT PROCESSORS
         *
         * NOTE the ability of the Standard Inlining mechanism to directly write to output instead of generating
         * internal Strings relies on the fact that there is only ONE ITextProcessor instance for each template mode
         * in the StandardDialect (see AbstractStandardInliner for details). So if new processors are added here,
         * it should be for a really compelling reason.
         */
        processors.add(new StandardInliningTextProcessor(TemplateMode.JAVASCRIPT));

        /*
         * JAVASCRIPT: TEMPLATE BOUNDARIES PROCESSORS
         */
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.JAVASCRIPT));



        /*
         * ------------------------
         * ------------------------
         * CSS TEMPLATE MODE
         * ------------------------
         * ------------------------
         */


        /*
         * CSS: ATTRIBUTE TAG PROCESSORS
         */
        processors.add(new StandardAssertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.CSS, dialectPrefix));
        // No th:fragment attribute in text modes: no fragment selection available!
        processors.add(new StandardIfTagProcessor(TemplateMode.CSS, dialectPrefix));
        // No th:include to be added here, as it is already deprecated since 3.0
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.CSS, dialectPrefix));
        // No th:substituteby to be added here, as it is already deprecated since 2.1
        processors.add(new StandardSwitchTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.CSS, dialectPrefix));

        /*
         * CSS: ELEMENT TAG PROCESSORS
         */
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, null, "")); // With no name, will process [# th....] elements

        /*
         * CSS: TEXT PROCESSORS
         *
         * NOTE the ability of the Standard Inlining mechanism to directly write to output instead of generating
         * internal Strings relies on the fact that there is only ONE ITextProcessor instance for each template mode
         * in the StandardDialect (see AbstractStandardInliner for details). So if new processors are added here,
         * it should be for a really compelling reason.
         */
        processors.add(new StandardInliningTextProcessor(TemplateMode.CSS));

        /*
         * CSS: TEMPLATE BOUNDARIES PROCESSORS
         */
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.CSS));


        /*
         * ------------------------
         * ------------------------
         * RAW TEMPLATE MODE
         * ------------------------
         * ------------------------
         */

        // No processors defined for template mode. Note only TextProcessors would be possible in this template mode,
        // given the entire templates are considered Text.


        return processors;

    }


}
