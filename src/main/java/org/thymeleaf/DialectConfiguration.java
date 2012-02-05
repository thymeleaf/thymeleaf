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
package org.thymeleaf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorAndContext;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Configuration class for a specific {@link IDialect}.
 * </p>
 * <p>
 *   Except for testing purposes, there is no reason why a user might need to use this 
 *   class directly.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class DialectConfiguration {
    
    private final String prefix;
    private final IDialect dialect;
    
    private boolean lenient;
    private Set<IDocTypeTranslation> docTypeTranslations;
    private Set<IDocTypeResolutionEntry> docTypeResolutionEntries;
    
    private Set<IProcessor> processors;
    
    private Map<String,Set<ProcessorAndContext>> specificProcessorsByElementName;
    private Map<String,Set<ProcessorAndContext>> specificProcessorsByAttributeName;
    private Map<Class<? extends Node>, Set<ProcessorAndContext>> nonSpecificProcessorsByNodeClass;
    
    private Map<String,Object> executionAttributes;
    
    private volatile boolean initialized;

    
    

    
    DialectConfiguration(final String prefix, final IDialect dialect) {
        super();
        // Prefix CAN be null
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefix = Node.normalizeName(prefix);
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
             * Initializing processors
             */
            this.processors =
                Collections.unmodifiableSet(new LinkedHashSet<IProcessor>(this.dialect.getProcessors()));
            Validate.containsNoNulls(this.processors, "Processor set can contain no nulls (dialect: " + this.dialect.getClass().getName() + ")");
            
            final ProcessorMatchingContext context = 
                    new ProcessorMatchingContext(this.dialect, this.prefix);
            
            final Map<String,Set<ProcessorAndContext>> newSpecificProcessorsByElementName = new HashMap<String,Set<ProcessorAndContext>>();
            final Map<String,Set<ProcessorAndContext>> newSpecificProcessorsByAttributeName = new HashMap<String,Set<ProcessorAndContext>>();
            final Map<Class<? extends Node>, Set<ProcessorAndContext>> newNonSpecificProcessorsByNodeClass = new HashMap<Class<? extends Node>, Set<ProcessorAndContext>>();
            
            for (final IProcessor processor : this.processors) {

                final IProcessorMatcher<? extends Node> processorMatcher = processor.getMatcher();
                if (processorMatcher == null) {
                    throw new ConfigurationException(
                            "Processor of class \"" + processor.getClass().getName() + "\" " +
                            "returned null processor matcher.");
                }
                
                if (processorMatcher instanceof IElementNameProcessorMatcher) {
                    // Processor will be indexed as "specific" for one or more element names
                    
                    final String elementName = ((IElementNameProcessorMatcher) processorMatcher).getElementName(context);
                    if (elementName == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + processor.getClass().getName() + "\" " +
                                "returned a null element name as a part of its applicability specifications.");
                    }
                    final String normalizedElementName = Node.normalizeName(elementName);
                    
                    Set<ProcessorAndContext> elementProcessorsForElementName = newSpecificProcessorsByElementName.get(normalizedElementName);
                    if (elementProcessorsForElementName == null) {
                        elementProcessorsForElementName = new HashSet<ProcessorAndContext>();
                        newSpecificProcessorsByElementName.put(normalizedElementName, elementProcessorsForElementName);
                    }
                    
                    elementProcessorsForElementName.add(new ProcessorAndContext(processor,context));
                    
                }
                
                if (processorMatcher instanceof IAttributeNameProcessorMatcher) {
                    // Processor will be indexed as "specific" for one or more attribute names
                    
                    
                    final String attributeName = ((IAttributeNameProcessorMatcher) processorMatcher).getAttributeName(context);
                    if (attributeName == null) {
                        throw new ConfigurationException(
                                "Processor of class \"" + processor.getClass().getName() + "\" " +
                                "returned a null attribute name as a part of its applicability specifications.");
                    }
                    final String normalizedAttributeName = Node.normalizeName(attributeName);
                    
                    Set<ProcessorAndContext> elementProcessorsForAttributeName = newSpecificProcessorsByAttributeName.get(normalizedAttributeName);
                    if (elementProcessorsForAttributeName == null) {
                        elementProcessorsForAttributeName = new HashSet<ProcessorAndContext>();
                        newSpecificProcessorsByAttributeName.put(normalizedAttributeName, elementProcessorsForAttributeName);
                    }
                    
                    elementProcessorsForAttributeName.add(new ProcessorAndContext(processor,context));
                    
                }
                
                if (!(processorMatcher instanceof IElementNameProcessorMatcher) && !(processorMatcher instanceof IAttributeNameProcessorMatcher)) {
                    
                    final Class<? extends Node> appliesTo = processorMatcher.appliesTo();
                        
                    Set<ProcessorAndContext> elementProcessorsForNodeClass = newNonSpecificProcessorsByNodeClass.get(appliesTo);
                    if (elementProcessorsForNodeClass == null) {
                        elementProcessorsForNodeClass = new HashSet<ProcessorAndContext>();
                        newNonSpecificProcessorsByNodeClass.put(appliesTo, elementProcessorsForNodeClass);
                    }
                    
                    elementProcessorsForNodeClass.add(new ProcessorAndContext(processor,context));

                }
                
                
                
            }
            
            this.specificProcessorsByElementName = Collections.unmodifiableMap(newSpecificProcessorsByElementName);
            this.specificProcessorsByAttributeName = Collections.unmodifiableMap(newSpecificProcessorsByAttributeName);
            this.nonSpecificProcessorsByNodeClass = Collections.unmodifiableMap(newNonSpecificProcessorsByNodeClass);            

            
            /*
             * Initializing execution arguments
             */
            this.executionAttributes = new HashMap<String, Object>();
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


    

    
    final Set<IProcessor> getProcessors() {
        return this.processors;
    }


    
    final Map<String,Set<ProcessorAndContext>> unsafeGetSpecificProcessorsByAttributeName() {
        return this.specificProcessorsByAttributeName;
    }
    
    final Map<String,Set<ProcessorAndContext>> unsafeGetSpecificProcessorsByElementName() {
        return this.specificProcessorsByElementName;
    }
    
    final Map<Class<? extends Node>, Set<ProcessorAndContext>> unsafeGetNonSpecificProcessorsByNodeClass() {
        return this.nonSpecificProcessorsByNodeClass;
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

    
    
}
