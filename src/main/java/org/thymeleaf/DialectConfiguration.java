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
package org.thymeleaf;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.applicability.TagApplicability;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.tag.ITagProcessor;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class DialectConfiguration {
    
    private final String prefix;
    private final IDialect dialect;
    
    private String prefixColon;
    
    private boolean lenient;
    private Set<IDocTypeTranslation> docTypeTranslations;
    private Set<IDocTypeResolutionEntry> docTypeResolutionEntries;
    
    private Set<IAttrProcessor> attrProcessors;
    private Set<String> processedAttrNames;
    private Map<String,Map<AttrApplicability,IAttrProcessor>> attrProcessorsByAttrName;
    
    private Set<ITagProcessor> tagProcessors;
    private Set<String> processedTagNames;
    private Map<String,Map<TagApplicability,ITagProcessor>> tagProcessorsByTagName;
    
    private Map<String,Object> executionAttributes;
    
    private boolean initialized;

    
    

    
    DialectConfiguration(final String prefix, final IDialect dialect) {
        super();
        // Prefix CAN be null
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefix = (prefix == null)? null : prefix.toLowerCase();
        this.dialect = dialect;
        this.initialized = false;
    }



    
    
    private boolean isInitialized() {
        return this.initialized;
    }
    
    private void checkInitialized() {
        if (!isInitialized()) {
            throw new NotInitializedException("Configuration has not been initialized");
        }
    }
    
    
    
    
    
    
    public synchronized void initialize() {
    
        if (!isInitialized()) {
            
            /*
             * Initializing prefix
             */
            this.prefixColon =
                (this.prefix == null || this.prefix.trim().equals("")? null : this.prefix + ":");
            
            
            /*
             * Initializing tag processors
             */
            this.tagProcessors =
                Collections.unmodifiableSet(new LinkedHashSet<ITagProcessor>(this.dialect.getTagProcessors()));
            Validate.containsNoNulls(this.tagProcessors, "Tag element processors can contain no nulls");
            
            final Map<String,Map<TagApplicability,ITagProcessor>> newElementProcessorsByTagName = new HashMap<String,Map<TagApplicability,ITagProcessor>>();
            final Set<String> newProcessedTagNames = new LinkedHashSet<String>(); 
            for (final ITagProcessor tagProcessor : this.tagProcessors) {
                
                final Set<TagApplicability> tagApplicabilities = tagProcessor.getTagApplicabilities();
                if (tagApplicabilities == null) {
                    throw new ConfigurationException(
                            "Processor of class \"" + tagProcessor.getClass().getName() + "\" " +
                            "returned null tag names set.");
                }
                for (final TagApplicability applicability : tagApplicabilities) {
                    if (applicability == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + tagProcessor.getClass().getName() + "\" " +
                                "returned null tag applicability.");
                    }
                    final String tagName = applicability.getTagName();
                    if (tagName == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + tagProcessor.getClass().getName() + "\" " +
                                "returned null tag name.");
                    }
                    final String completeTagName = completeTagName(tagName);
                    final String normalizedTagName = completeTagName.toLowerCase();
                    
                    Map<TagApplicability,ITagProcessor> elementProcessorsForTagName = newElementProcessorsByTagName.get(normalizedTagName);
                    if (elementProcessorsForTagName == null) {
                        elementProcessorsForTagName = new LinkedHashMap<TagApplicability, ITagProcessor>();
                        newElementProcessorsByTagName.put(normalizedTagName, elementProcessorsForTagName);
                    }
                    
                    if (elementProcessorsForTagName.containsKey(applicability)) {
                        // Given that applicability filter is not completely implemented,
                        // this check is not a complete test for equal keys, but just
                        // a mechanism to ensure that no Tag Processor is overwritten in
                        // the elementProcessorsForTagName map.
                        throw new ConfigurationException(
                                "Cannot specify more than one tag processor for exactly the same applicability: \"" + normalizedTagName + "\"");
                    }
                    newProcessedTagNames.add(normalizedTagName);
                    elementProcessorsForTagName.put(applicability, tagProcessor);
                }
                
            }
            this.processedTagNames = Collections.unmodifiableSet(newProcessedTagNames);
            this.tagProcessorsByTagName = Collections.unmodifiableMap(newElementProcessorsByTagName);

            
            
            /*
             * Initializing attribute processors
             */
            this.attrProcessors =
                Collections.unmodifiableSet(new LinkedHashSet<IAttrProcessor>(this.dialect.getAttrProcessors()));
            Validate.containsNoNulls(this.attrProcessors, "Attr element processors can contain no nulls");
            
            final Map<String,Map<AttrApplicability,IAttrProcessor>> newElementProcessorsByAttrName = new HashMap<String,Map<AttrApplicability,IAttrProcessor>>();
            final Set<String> newProcessedAttrNames = new LinkedHashSet<String>(); 
            for (final IAttrProcessor attrProcessor : this.attrProcessors) {
                
                final Set<AttrApplicability> attrApplicabilities = attrProcessor.getAttributeApplicabilities();
                if (attrApplicabilities == null) {
                    throw new ConfigurationException(
                            "Processor of class \"" + attrProcessor.getClass().getName() + "\" " +
                            "returned null attribute applicabilities set.");
                }
                for (final AttrApplicability applicability : attrApplicabilities) {
                    if (applicability == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + attrProcessor.getClass().getName() + "\" " +
                                "returned null attribute name.");
                    }
                    final String attrName = applicability.getAttrName();
                    if (attrName == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + attrProcessor.getClass().getName() + "\" " +
                                "returned null attribute name.");
                    }
                    final String completeAttrName = completeAttrName(attrName);
                    final String normalizedAttrName = completeAttrName.toLowerCase();
                    
                    Map<AttrApplicability,IAttrProcessor> elementProcessorsForAttrName = newElementProcessorsByAttrName.get(normalizedAttrName);
                    if (elementProcessorsForAttrName == null) {
                        elementProcessorsForAttrName = new LinkedHashMap<AttrApplicability, IAttrProcessor>();
                        newElementProcessorsByAttrName.put(normalizedAttrName, elementProcessorsForAttrName);
                    }
                    
                    if (elementProcessorsForAttrName.containsKey(applicability)) {
                        // Given that applicability filter is not completely implemented,
                        // this check is not a complete test for equal keys, but just
                        // a mechanism to ensure that no Attr Processor is overwritten in
                        // the elementProcessorsForAttrName map.
                        throw new ConfigurationException(
                                "Cannot specify more than one tag processor for exactly the same applicability: \"" + normalizedAttrName + "\"");
                    }
                    newProcessedAttrNames.add(normalizedAttrName);
                    elementProcessorsForAttrName.put(applicability, attrProcessor);
                }
                
            }
            this.processedAttrNames = Collections.unmodifiableSet(newProcessedAttrNames);
            this.attrProcessorsByAttrName = Collections.unmodifiableMap(newElementProcessorsByAttrName);

            
            /*
             * Initializing execution arguments
             */
            this.executionAttributes = new LinkedHashMap<String, Object>();
            this.executionAttributes.putAll(this.dialect.getExecutionAttributes());

            
            /*
             * Initializing XML-specific parameters
             */
            this.lenient = this.dialect.isLenient();

            
            
            /*
             * Configuring DOCTYPE translations
             */
            this.docTypeTranslations = 
                Collections.unmodifiableSet(new LinkedHashSet<IDocTypeTranslation>(this.dialect.getDocTypeTranslations()));
            Validate.containsNoNulls(this.docTypeTranslations, "Document Type translations can contain no nulls");

            validateDocTypeTranslations();
            
            
            /*
             * Configuring DOCTYPE resolution entries
             */
            this.docTypeResolutionEntries =
                Collections.unmodifiableSet(new LinkedHashSet<IDocTypeResolutionEntry>(this.dialect.getDocTypeResolutionEntries()));
            Validate.containsNoNulls(this.docTypeResolutionEntries, "Document Type resolution entries can contain no nulls");

            validateDocTypeResolutionEntries();
            
            
            
            
            /*
             * Mark as initialized
             */
            this.initialized = true;
            
        }
       
    }

    
    
    public synchronized IDialect getDialect() {
        return this.dialect;
    }
    
    
    
    public String getPrefix() {
        checkInitialized();
        return this.prefix;
    }

    
    
    public boolean isLenient() {
        checkInitialized();
        return this.lenient;
    }


    

    
    final Set<IAttrProcessor> getAttrProcessors() {
        return this.attrProcessors;
    }

    final Set<String> getProcessedAttrNames() {
        return this.processedAttrNames;
    }

    final IAttrProcessor getAttrProcessor(final Element element, final Attr attr) {
        
        final String attrName = attr.getName();
        final String normalizedAttrName = 
            (attrName == null? null : attrName.toLowerCase());
            
        final Map<AttrApplicability, IAttrProcessor> applicabilities = 
            this.attrProcessorsByAttrName.get(normalizedAttrName);
        if (applicabilities == null) {
            return null;
        }
        IAttrProcessor processor = null;
        for (final Map.Entry<AttrApplicability,IAttrProcessor> entry : applicabilities.entrySet()) {
            if (entry.getKey().isFilterApplicableToAttribute(element, attr)) {
                if (processor == null) {
                    processor = entry.getValue();
                } else {
                    throw new ConfigurationException(
                            "More than one processor is applicable to the same attribute \"" + attrName + "\": " +
                            processor.getClass().getName() + " and " + entry.getValue().getClass().getName());
                }
            }
        }
        
        return processor;
        
    }

    
    final Map<String,Map<AttrApplicability,IAttrProcessor>> unsafeGetAttrProcessorsByAttrName() {
        return this.attrProcessorsByAttrName;
    }
    
    
    
    
    
    final Set<ITagProcessor> getTagProcessors() {
        return this.tagProcessors;
    }

    final Set<String> getProcessedTagNames() {
        return this.processedTagNames;
    }

    final ITagProcessor getTagProcessor(final Element element) {
        
        final String elementName = element.getTagName();
        final String normalizedElementName = 
            (elementName == null? null : elementName.toLowerCase());
            
        final Map<TagApplicability, ITagProcessor> applicabilities = 
            this.tagProcessorsByTagName.get(normalizedElementName);
        if (applicabilities == null) {
            return null;
        }
        ITagProcessor processor = null;
        for (final Map.Entry<TagApplicability,ITagProcessor> entry : applicabilities.entrySet()) {
            if (entry.getKey().isFilterApplicableToTag(element)) {
                if (processor == null) {
                    processor = entry.getValue();
                } else {
                    throw new ConfigurationException(
                            "More than one processor is applicable to the same tag \"" + elementName + "\": " +
                            processor.getClass().getName() + " and " + entry.getValue().getClass().getName());
                }
            }
        }
        
        return processor;
        
    }

    
    final Map<String,Map<TagApplicability,ITagProcessor>> unsafeGetTagProcessorsByTagName() {
        return this.tagProcessorsByTagName;
    }
    
    

    
    final Map<String,Object> getExecutionAttributes() {
        return this.executionAttributes;
    }
    
    
    
    
    
    
    
    private void validateDocTypeTranslations() {
        
        for (final IDocTypeTranslation translation : this.docTypeTranslations) {
            
            if (translation.getSourcePublicID() == null) {
                throw new ConfigurationException(
                        "Translation specifies a null Source PUBLICID. " +
                        "Document Type identifiers should never be null. " +
                        "Use \"NONE\" if you want to specify a non-existent identifier");
            }
            if (translation.getSourceSystemID() == null) {
                throw new ConfigurationException(
                        "Translation specifies a null Source SYSTEMID. " +
                        "Document Type identifiers should never be null. " +
                        "Use \"NONE\" if you want to specify a non-existent identifier");
            }
            if (translation.getTargetPublicID() == null) {
                throw new ConfigurationException(
                        "Translation specifies a null Target PUBLICID. " +
                        "Document Type identifiers should never be null. " +
                        "Use \"NONE\" if you want to specify a non-existent identifier");
            }
            if (translation.getTargetSystemID() == null) {
                throw new ConfigurationException(
                        "Translation specifies a null Target SYSTEMID. " +
                        "Document Type identifiers should never be null. " +
                        "Use \"NONE\" if you want to specify a non-existent identifier");
            }
            
        }
        
    }

    
    private void validateDocTypeResolutionEntries() {
        
        final Set<IDocTypeResolutionEntry> entriesAlreadyValidated = new LinkedHashSet<IDocTypeResolutionEntry>();
        for (final IDocTypeResolutionEntry entry : this.docTypeResolutionEntries) {
            
            for (final IDocTypeResolutionEntry validatedEntry : entriesAlreadyValidated) {
                if (
                    (
                      validatedEntry.getPublicID().matches(entry.getPublicID()) 
                      &&
                      validatedEntry.getSystemID().matches(entry.getSystemID())
                    )
                    ||
                    (
                      entry.getPublicID().matches(validatedEntry.getPublicID()) 
                      &&
                      entry.getSystemID().matches(validatedEntry.getSystemID())
                    )
                  ) {
                    throw new ConfigurationException(
                            "Dialect specifies at least a couple of Document type resolution " +
                            "entries that would match each other, which would render resolution " +
                            "unpredictable");
                }
            }
            
            entriesAlreadyValidated.add(entry);
            
        }
        
    }

    
   
    
    
    
    
    
    private final String completeAttrName(final String attrName) {
        Validate.notNull(attrName, "Attribute name cannot be null");
        if (this.prefixColon == null) {
            return attrName;
        }
        return this.prefixColon + attrName;
    }
    
    

    private final String completeTagName(final String tagName) {
        Validate.notNull(tagName, "Tag name cannot be null");
        if (this.prefixColon == null) {
            return tagName;
        }
        return this.prefixColon + tagName;
    }

    
    
}
