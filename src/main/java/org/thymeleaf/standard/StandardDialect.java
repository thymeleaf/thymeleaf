/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Standards;
import org.thymeleaf.dialect.AbstractXHTMLEnabledDialect;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.resolution.ClassLoaderDocTypeResolutionEntry;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.DocTypeTranslation;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.expression.OgnlExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.processor.attr.StandardAltTitleAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardAttrAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardAttrappendAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardAttrprependAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardClassappendAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardConditionalFixedValueAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardDOMEventAttributeModifierAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardEachAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardFragmentAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardIfAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardIncludeAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardInlineAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardLangXmlLangAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardObjectAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardRemoveAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardSingleNonRemovableAttributeModifierAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardSingleRemovableAttributeModifierAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardSubstituteByAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardUnlessAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardUtextAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardXmlBaseAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardXmlLangAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardXmlSpaceAttrProcessor;
import org.thymeleaf.standard.processor.text.StandardTextInliningTextProcessor;

/**
 * <p>
 *   The Standard Dialect, default implementation of {@link org.thymeleaf.dialect.IDialect}.
 * </p>
 * <ul>
 *   <li><b>Prefix</b>: <tt>th</tt></li>
 *   <li><b>Lenient</b>: <tt>false</tt></li>
 *   <li><b>Attribute processors</b>:
 *         <ul>
 *           <li>{@link StandardAltTitleAttrProcessor}</li>
 *           <li>{@link StandardAttrAttrProcessor}</li>
 *           <li>{@link StandardAttrappendAttrProcessor}</li>
 *           <li>{@link StandardAttrprependAttrProcessor}</li>
 *           <li>{@link StandardClassappendAttrProcessor}</li>
 *           <li>{@link StandardConditionalFixedValueAttrProcessor}</li>
 *           <li>{@link StandardDOMEventAttributeModifierAttrProcessor}</li>
 *           <li>{@link StandardEachAttrProcessor}</li>
 *           <li>{@link StandardFragmentAttrProcessor}</li>
 *           <li>{@link StandardObjectAttrProcessor}</li>
 *           <li>{@link StandardIfAttrProcessor}</li>
 *           <li>{@link StandardInlineAttrProcessor}</li>
 *           <li>{@link StandardUnlessAttrProcessor}</li>
 *           <li>{@link StandardIncludeAttrProcessor}</li>
 *           <li>{@link StandardLangXmlLangAttrProcessor}</li>
 *           <li>{@link StandardRemoveAttrProcessor}</li>
 *           <li>{@link StandardSingleNonRemovableAttributeModifierAttrProcessor}</li>
 *           <li>{@link StandardSingleRemovableAttributeModifierAttrProcessor}</li>
 *           <li>{@link StandardSubstituteByAttrProcessor}</li>
 *           <li>{@link StandardTextAttrProcessor}</li>
 *           <li>{@link StandardUtextAttrProcessor}</li>
 *           <li>{@link StandardWithAttrProcessor}</li>
 *           <li>{@link StandardXmlBaseAttrProcessor}</li>
 *           <li>{@link StandardXmlLangAttrProcessor}</li>
 *           <li>{@link StandardXmlSpaceAttrProcessor}</li>
 *         </ul>
 *       </li>
 *   <li><b>Tag processors</b>: none</li>
 *   <li><b>Execution attributes</b>:
 *         <ul>
 *           <li>"StandardExpressionExecutor": {@link StandardExpressionExecutor} 
 *               with expression evaluator of type {@link OgnlExpressionEvaluator} 
 *               (<tt>OGNL</tt> expression language).</li>
 *           <li>"StandardExpressionParser": {@link StandardExpressionParser}.</li>
 *         </ul>
 *       </li>
 *   <li><b>DOCTYPE translations</b>:</li>
 *   <li><b>DOCTYPE resolution entries</b>:</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class StandardDialect extends AbstractXHTMLEnabledDialect {
    
    
    public static final String PREFIX = "th";
    public static final boolean LENIENT = false;
    
    
    public static final String INLINER_LOCAL_VARIABLE = "%STANDARD_INLINER%";
    
    
    
    public static final DocTypeIdentifier XHTML1_STRICT_THYMELEAF1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-1.dtd");
    public static final DocTypeIdentifier XHTML1_TRANSITIONAL_THYMELEAF1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-1.dtd");
    public static final DocTypeIdentifier XHTML1_FRAMESET_THYMELEAF1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-1.dtd");
    public static final DocTypeIdentifier XHTML11_THYMELEAF1_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-1.dtd");

    public static final DocTypeIdentifier XHTML1_STRICT_THYMELEAF2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-2.dtd");
    public static final DocTypeIdentifier XHTML1_TRANSITIONAL_THYMELEAF2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-2.dtd");
    public static final DocTypeIdentifier XHTML1_FRAMESET_THYMELEAF2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-2.dtd");
    public static final DocTypeIdentifier XHTML11_THYMELEAF2_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-2.dtd");
    
    
    public static final IDocTypeResolutionEntry XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAF1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-strict-thymeleaf-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAF1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-transitional-thymeleaf-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAF1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-frameset-thymeleaf-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    public static final IDocTypeResolutionEntry XHTML11_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAF1_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml11-thymeleaf-1.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    
    
    public static final IDocTypeResolutionEntry XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAF2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-strict-thymeleaf-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAF2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-transitional-thymeleaf-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    
    public static final IDocTypeResolutionEntry XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAF2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml1-frameset-thymeleaf-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 

    public static final IDocTypeResolutionEntry XHTML11_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAF2_SYSTEMID, // SYSTEMID
                "org/thymeleaf/dtd/thymeleaf/xhtml11-thymeleaf-2.dtd"); // CLASS-LOADER-RESOLVABLE RESOURCE 
    

    
    public static final Set<IDocTypeResolutionEntry> DOC_TYPE_RESOLUTION_ENTRIES;
    
    
    
    public static final IDocTypeTranslation XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAF1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_STRICT_PUBLICID, 
                Standards.XHTML_1_STRICT_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAF1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_TRANSITIONAL_PUBLICID, 
                Standards.XHTML_1_TRANSITIONAL_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAF1_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_FRAMESET_PUBLICID, 
                Standards.XHTML_1_FRAMESET_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML11_THYMELEAF_1_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAF1_SYSTEMID, // SYSTEMID
                Standards.XHTML_11_PUBLICID, 
                Standards.XHTML_11_SYSTEMID);

    
    
    public static final IDocTypeTranslation XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_STRICT_THYMELEAF2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_STRICT_PUBLICID, 
                Standards.XHTML_1_STRICT_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_TRANSITIONAL_THYMELEAF2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_TRANSITIONAL_PUBLICID, 
                Standards.XHTML_1_TRANSITIONAL_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML1_FRAMESET_THYMELEAF2_SYSTEMID, // SYSTEMID
                Standards.XHTML_1_FRAMESET_PUBLICID, 
                Standards.XHTML_1_FRAMESET_SYSTEMID);
    
    public static final IDocTypeTranslation XHTML11_THYMELEAF_2_DOC_TYPE_TRANSLATION = 
        new DocTypeTranslation(
                DocTypeIdentifier.NONE, // PUBLICID
                XHTML11_THYMELEAF2_SYSTEMID, // SYSTEMID
                Standards.XHTML_11_PUBLICID, 
                Standards.XHTML_11_SYSTEMID);


    
    public static final Set<IDocTypeTranslation> DOC_TYPE_TRANSLATIONS =
        Collections.unmodifiableSet(
                new LinkedHashSet<IDocTypeTranslation>(
                        Arrays.asList(new IDocTypeTranslation[] { 
                                XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_TRANSLATION, 
                                XHTML11_THYMELEAF_1_DOC_TYPE_TRANSLATION,
                                XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_TRANSLATION, 
                                XHTML11_THYMELEAF_2_DOC_TYPE_TRANSLATION
                                })));

    

    
    
    
    static {
        
        final Set<IDocTypeResolutionEntry> newDocTypeResolutionEntries = new LinkedHashSet<IDocTypeResolutionEntry>();
        newDocTypeResolutionEntries.add(XHTML1_STRICT_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_TRANSITIONAL_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_FRAMESET_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML11_THYMELEAF_1_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_STRICT_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_TRANSITIONAL_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML1_FRAMESET_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY);
        newDocTypeResolutionEntries.add(XHTML11_THYMELEAF_2_DOC_TYPE_RESOLUTION_ENTRY);
        DOC_TYPE_RESOLUTION_ENTRIES = Collections.unmodifiableSet(newDocTypeResolutionEntries);
        
    }
    
    
    
    
    
    
    public StandardDialect() {
        super();
    }
    
    

    
    public String getPrefix() {
        return PREFIX;
    }
    
    
    public boolean isLenient() {
        return LENIENT;
    }



    
    
    @Override
    public Set<IDocTypeTranslation> getDocTypeTranslations() {
        final Set<IDocTypeTranslation> docTypeTranslations = new LinkedHashSet<IDocTypeTranslation>();
        docTypeTranslations.addAll(DOC_TYPE_TRANSLATIONS);
        final Set<IDocTypeTranslation> additionalDocTypeTranslations = getAdditionalDocTypeTranslations();
        if (additionalDocTypeTranslations != null) {
            docTypeTranslations.addAll(additionalDocTypeTranslations);
        }
        return Collections.unmodifiableSet(docTypeTranslations);
    }
    
    protected Set<IDocTypeTranslation> getAdditionalDocTypeTranslations() {
        return null;
    }
    

    
    @Override
    public Set<IDocTypeResolutionEntry> getSpecificDocTypeResolutionEntries() {
        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = new LinkedHashSet<IDocTypeResolutionEntry>();
        docTypeResolutionEntries.addAll(DOC_TYPE_RESOLUTION_ENTRIES);
        final Set<IDocTypeResolutionEntry> additionalDocTypeResolutionEntries = getAdditionalDocTypeResolutionEntries();
        if (additionalDocTypeResolutionEntries != null) {
            docTypeResolutionEntries.addAll(additionalDocTypeResolutionEntries);
        }
        return Collections.unmodifiableSet(docTypeResolutionEntries);
    }
    
    protected Set<IDocTypeResolutionEntry> getAdditionalDocTypeResolutionEntries() {
        return null;
    }


    
    @Override
    public Set<IProcessor> getProcessors() {
        
        final List<IProcessor> processors = new ArrayList<IProcessor>();
        
        final Set<IProcessor> additionalProcessors = getAdditionalProcessors();
        final Set<Class<? extends IProcessor>> removedProcessors = getRemovedProcessors();
        
        for (final IProcessor processor : createStandardProcessorsSet()) {
            if (removedProcessors == null || !removedProcessors.contains(processor.getClass())) {
                processors.add(processor);
            }
        }
        
        if (additionalProcessors != null) {
            processors.addAll(additionalProcessors);
        }
        
        return Collections.unmodifiableSet(new LinkedHashSet<IProcessor>(processors));
        
    }
    
    
    protected Set<IProcessor> getAdditionalProcessors() {
        return null;
    }
    
    protected Set<Class<? extends IProcessor>> getRemovedProcessors() {
        return null;
    }
    

    
    
    
    
    
    @Override
    public Map<String, Object> getExecutionAttributes() {
        
        final StandardExpressionExecutor executor = StandardExpressionProcessor.createStandardExpressionExecutor(OgnlExpressionEvaluator.INSTANCE);
        final StandardExpressionParser parser = StandardExpressionProcessor.createStandardExpressionParser(executor);
        
        final Map<String,Object> executionAttributes = new LinkedHashMap<String, Object>();
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME, executor);
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, parser);
        
        return executionAttributes;
        
    }


    
    
    

    
    
    public static Set<IProcessor> createStandardProcessorsSet() {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new StandardAltTitleAttrProcessor());
        processors.add(new StandardAttrAttrProcessor());
        processors.add(new StandardAttrappendAttrProcessor());
        processors.add(new StandardAttrprependAttrProcessor());
        processors.add(new StandardClassappendAttrProcessor());
        processors.addAll(Arrays.asList(StandardConditionalFixedValueAttrProcessor.PROCESSORS));
        processors.addAll(Arrays.asList(StandardDOMEventAttributeModifierAttrProcessor.PROCESSORS));
        processors.add(new StandardEachAttrProcessor());
        processors.add(new StandardFragmentAttrProcessor());
        processors.add(new StandardObjectAttrProcessor());
        processors.add(new StandardIfAttrProcessor());
        processors.add(new StandardInlineAttrProcessor());
        processors.add(new StandardUnlessAttrProcessor());
        processors.add(new StandardIncludeAttrProcessor());
        processors.add(new StandardLangXmlLangAttrProcessor());
        processors.add(new StandardRemoveAttrProcessor());
        processors.addAll(Arrays.asList(StandardSingleNonRemovableAttributeModifierAttrProcessor.PROCESSORS));
        processors.addAll(Arrays.asList(StandardSingleRemovableAttributeModifierAttrProcessor.PROCESSORS));
        processors.add(new StandardSubstituteByAttrProcessor());
        processors.add(new StandardTextAttrProcessor());
        processors.add(new StandardUtextAttrProcessor());
        processors.add(new StandardWithAttrProcessor());
        processors.add(new StandardXmlBaseAttrProcessor());
        processors.add(new StandardXmlLangAttrProcessor());
        processors.add(new StandardXmlSpaceAttrProcessor());
        processors.add(new StandardTextInliningTextProcessor());
        return processors;
    }


    
}
