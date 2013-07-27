/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.dialect;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Standards;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.resolution.ClassLoaderDocTypeResolutionEntry;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.DocTypeTranslation;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring3.expression.SpelVariableExpressionEvaluator;
import org.thymeleaf.spring3.processor.attr.SpringErrorsAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringInputCheckboxFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringInputFileFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringInputGeneralFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringInputPasswordFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringInputRadioFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringMethodAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringObjectAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringOptionFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringSelectFieldAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringSingleRemovableAttributeModifierAttrProcessor;
import org.thymeleaf.spring3.processor.attr.SpringTextareaFieldAttrProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.processor.attr.StandardObjectAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardSingleRemovableAttributeModifierAttrProcessor;

/**
 * <p>
 *   Specialized version of {@link StandardDialect} meant for use in Spring MVC applications.
 * </p>
 * <p>
 *   It includes all attribute processors and features present in the
 *   Standard Dialect (therefore all the Standard Dialect documentation is still valid for this
 *   dialect), but establishes the following differences: 
 * </p>
 * <ul>
 *   <li>The language used for evaluation of expressions is <b>Spring Expression Language</b> instead of OGNL.</li>
 *   <li>Expressions can use an object called <tt>#beans</tt> to access beans in the Application
 *       Context: <tt>${beans.myBean.doSomething()}</tt>.</li>
 *   <li>New attributes for form processing:
 *     <ul>
 *       <li><tt>th:field</tt> for binding form fields to attributes in form-backing beans.</li>
 *       <li><tt>th:errors</tt> for showing form validation errors.</li>
 *       <li>Modification to <tt>th:object</tt> for using it as a form-back bean selection mechanism.</li>
 *     </ul>
 *   </li>
 *   <li>New DTDs for validating template modes:
 *     <ul>
 *       <li><b>XHTML 1.0 Strict</b> : <tt>SYSTEMID "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-2.dtd"</tt></li>
 *       <li><b>XHTML 1.0 Transitional</b> : <tt>SYSTEMID "http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring3-2.dtd"</tt></li>
 *       <li><b>XHTML 1.0 Frameset</b> : <tt>SYSTEMID "http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring3-2.dtd"</tt></li>
 *       <li><b>XHTML 1.1</b> : <tt>SYSTEMID "http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring3-2.dtd"</tt></li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class SpringStandardDialect extends StandardDialect {

    
    public static final DocTypeIdentifier XHTML1_STRICT_THYMELEAFSPRING3_1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-1.dtd");
    public static final DocTypeIdentifier XHTML1_TRANSITIONAL_THYMELEAFSPRING3_1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring3-1.dtd");
    public static final DocTypeIdentifier XHTML1_FRAMESET_THYMELEAFSPRING3_1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring3-1.dtd");
    public static final DocTypeIdentifier XHTML11_THYMELEAFSPRING3_1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring3-1.dtd");

    public static final DocTypeIdentifier XHTML1_STRICT_THYMELEAFSPRING3_2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-2.dtd");
    public static final DocTypeIdentifier XHTML1_TRANSITIONAL_THYMELEAFSPRING3_2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring3-2.dtd");
    public static final DocTypeIdentifier XHTML1_FRAMESET_THYMELEAFSPRING3_2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring3-2.dtd");
    public static final DocTypeIdentifier XHTML11_THYMELEAFSPRING3_2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring3-2.dtd");

    public static final DocTypeIdentifier XHTML1_STRICT_THYMELEAFSPRING3_3_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-3.dtd");
    public static final DocTypeIdentifier XHTML1_TRANSITIONAL_THYMELEAFSPRING3_3_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring3-3.dtd");
    public static final DocTypeIdentifier XHTML1_FRAMESET_THYMELEAFSPRING3_3_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring3-3.dtd");
    public static final DocTypeIdentifier XHTML11_THYMELEAFSPRING3_3_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring3-3.dtd");
    
    
    public static final IDocTypeResolutionEntry XHTML1_STRICT_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-strict-thymeleaf-spring3-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_TRANSITIONAL_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-transitional-thymeleaf-spring3-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_FRAMESET_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-frameset-thymeleaf-spring3-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    public static final IDocTypeResolutionEntry XHTML11_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml11-thymeleaf-spring3-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 


    
    public static final IDocTypeResolutionEntry XHTML1_STRICT_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-strict-thymeleaf-spring3-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_TRANSITIONAL_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-transitional-thymeleaf-spring3-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_FRAMESET_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-frameset-thymeleaf-spring3-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    public static final IDocTypeResolutionEntry XHTML11_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml11-thymeleaf-spring3-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 


    
    public static final IDocTypeResolutionEntry XHTML1_STRICT_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-strict-thymeleaf-spring3-3.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_TRANSITIONAL_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-transitional-thymeleaf-spring3-3.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_FRAMESET_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml1-frameset-thymeleaf-spring3-3.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    public static final IDocTypeResolutionEntry XHTML11_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf-spring3/xhtml11-thymeleaf-spring3-3.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    
    
    
    public static final Set<IDocTypeResolutionEntry> SPRING3_DOC_TYPE_RESOLUTION_ENTRIES;
    
    
    
    public static final IDocTypeTranslation SPRING3_XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_STRICT_PUBLICID, 
                Standards.XHTML_1_STRICT_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_TRANSITIONAL_PUBLICID, 
                Standards.XHTML_1_TRANSITIONAL_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_FRAMESET_PUBLICID, 
                Standards.XHTML_1_FRAMESET_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML11_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_1_SYSTEMID, // SYSTEMID
                Standards.XHTML_11_PUBLICID, 
                Standards.XHTML_11_SYSTEMID);

    
    
    public static final IDocTypeTranslation SPRING3_XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_STRICT_PUBLICID, 
                Standards.XHTML_1_STRICT_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_TRANSITIONAL_PUBLICID, 
                Standards.XHTML_1_TRANSITIONAL_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_FRAMESET_PUBLICID, 
                Standards.XHTML_1_FRAMESET_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML11_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_2_SYSTEMID, // SYSTEMID
                Standards.XHTML_11_PUBLICID, 
                Standards.XHTML_11_SYSTEMID);

    
    
    public static final IDocTypeTranslation SPRING3_XHTML1_STRICT_THYMELEAF_3_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_STRICT_PUBLICID, 
                Standards.XHTML_1_STRICT_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_3_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_TRANSITIONAL_PUBLICID, 
                Standards.XHTML_1_TRANSITIONAL_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML1_FRAMESET_THYMELEAF_3_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_FRAMESET_PUBLICID, 
                Standards.XHTML_1_FRAMESET_SYSTEMID);
    
    public static final IDocTypeTranslation SPRING3_XHTML11_THYMELEAF_3_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAFSPRING3_3_SYSTEMID, // SYSTEMID
                Standards.XHTML_11_PUBLICID, 
                Standards.XHTML_11_SYSTEMID);
    
    

    
    public static final Set<IDocTypeTranslation> SPRING3_DOC_TYPE_TRANSLATIONS =
        Collections.unmodifiableSet(
                new LinkedHashSet<IDocTypeTranslation>(
                        Arrays.asList(new IDocTypeTranslation[] { 
                                SPRING3_XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML11_THYMELEAF_1_DOC_TYPE_TRANSLATION,
                                SPRING3_XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML11_THYMELEAF_2_DOC_TYPE_TRANSLATION,
                                SPRING3_XHTML1_STRICT_THYMELEAF_3_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_TRANSITIONAL_THYMELEAF_3_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML1_FRAMESET_THYMELEAF_3_DOC_TYPE_TRANSLATION, 
                                SPRING3_XHTML11_THYMELEAF_3_DOC_TYPE_TRANSLATION
                                })));
    
    

    

    
    
    static {
        final Set<IDocTypeResolutionEntry> newDocTypeResolutionEntries = new LinkedHashSet<IDocTypeResolutionEntry>();
        newDocTypeResolutionEntries.add(XHTML1_STRICT_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_TRANSITIONAL_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_FRAMESET_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML11_THYMELEAFSPRING3_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_STRICT_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_TRANSITIONAL_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_FRAMESET_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML11_THYMELEAFSPRING3_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_STRICT_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_TRANSITIONAL_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_FRAMESET_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML11_THYMELEAFSPRING3_3_DOC_TYPE_RESOLUTION_ENTRY);
        SPRING3_DOC_TYPE_RESOLUTION_ENTRIES = Collections.unmodifiableSet(newDocTypeResolutionEntries);
    }
    
    
    
    
    
    
    
    public SpringStandardDialect() {
        super();
    }

    


    
    
    @Override
    public Set<IDocTypeTranslation> getDocTypeTranslations() {
        final Set<IDocTypeTranslation> docTypeTranslations = new LinkedHashSet<IDocTypeTranslation>();
        docTypeTranslations.addAll(SPRING3_DOC_TYPE_TRANSLATIONS);
        final Set<IDocTypeTranslation> additionalDocTypeTranslations = getAdditionalDocTypeTranslations();
        if (additionalDocTypeTranslations != null) {
            docTypeTranslations.addAll(additionalDocTypeTranslations);
        }
        return Collections.unmodifiableSet(docTypeTranslations);
    }
    
    @Override
    protected Set<IDocTypeTranslation> getAdditionalDocTypeTranslations() {
        return null;
    }
    
    

    
    @Override
    public Set<IDocTypeResolutionEntry> getSpecificDocTypeResolutionEntries() {
        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = new LinkedHashSet<IDocTypeResolutionEntry>();
        docTypeResolutionEntries.addAll(SPRING3_DOC_TYPE_RESOLUTION_ENTRIES);
        final Set<IDocTypeResolutionEntry> additionalDocTypeResolutionEntries = getAdditionalDocTypeResolutionEntries();
        if (additionalDocTypeResolutionEntries != null) {
            docTypeResolutionEntries.addAll(additionalDocTypeResolutionEntries);
        }
        return Collections.unmodifiableSet(docTypeResolutionEntries);
    }

    
    @Override
    protected Set<IDocTypeResolutionEntry> getAdditionalDocTypeResolutionEntries() {
        return null;
    }

    
    
    

    
    

    @Override
    public Set<IProcessor> getProcessors() {
        
        final Set<IProcessor> processors = createSpringStandardProcessorsSet();
        final Set<IProcessor> dialectAdditionalProcessors = getAdditionalProcessors();

        if (dialectAdditionalProcessors != null) {
            processors.addAll(dialectAdditionalProcessors);
        }
        
        return new LinkedHashSet<IProcessor>(processors);
        
    }
    
    


    
    /**
     * <p>
     *   Create a the set of SpringStandard processors, all of them freshly instanced.
     * </p>
     * 
     * @return the set of SpringStandard processors.
     */
    public static Set<IProcessor> createSpringStandardProcessorsSet() {
        /*
         * It is important that we create new instances here because, if there are
         * several dialects in the TemplateEngine that extend StandardDialect, they should
         * not be returning the exact same instances for their processors in order
         * to allow specific instances to be directly linked with their owner dialect.
         */
        
        final Set<IProcessor> standardProcessors = StandardDialect.createStandardProcessorsSet();
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        
        for (final IProcessor standardProcessor : standardProcessors) {
            // There are several processors we need to remove from the Standard Dialect set
            if (!(standardProcessor instanceof StandardObjectAttrProcessor) &&
                !(standardProcessor instanceof StandardSingleRemovableAttributeModifierAttrProcessor)) {
                processors.add(standardProcessor);
            }
        }
        
        processors.add(new SpringObjectAttrProcessor());
        processors.add(new SpringErrorsAttrProcessor());
        processors.addAll(Arrays.asList(SpringInputGeneralFieldAttrProcessor.PROCESSORS));
        processors.add(new SpringInputPasswordFieldAttrProcessor());
        processors.add(new SpringInputCheckboxFieldAttrProcessor());
        processors.add(new SpringInputRadioFieldAttrProcessor());
        processors.add(new SpringInputFileFieldAttrProcessor());
        processors.add(new SpringMethodAttrProcessor());
        processors.add(new SpringSelectFieldAttrProcessor());
        processors.addAll(Arrays.asList(SpringSingleRemovableAttributeModifierAttrProcessor.PROCESSORS));
        processors.add(new SpringOptionFieldAttrProcessor());
        processors.add(new SpringTextareaFieldAttrProcessor());
        
        return processors;
        
    }
    


    
    

    
    @Override
    public Map<String, Object> getExecutionAttributes() {

        final IStandardVariableExpressionEvaluator expressionEvaluator = SpelVariableExpressionEvaluator.INSTANCE;
        
        final StandardExpressionExecutor executor = StandardExpressionProcessor.createStandardExpressionExecutor(expressionEvaluator);
        final StandardExpressionParser parser = StandardExpressionProcessor.createStandardExpressionParser(executor);
        
        final Map<String,Object> executionAttributes = new LinkedHashMap<String, Object>();
        executionAttributes.put(
                StandardDialect.EXPRESSION_EVALUATOR_EXECUTION_ATTRIBUTE, expressionEvaluator);
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME, executor);
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, parser);
        
        return executionAttributes;
        
    }
    
    

}
