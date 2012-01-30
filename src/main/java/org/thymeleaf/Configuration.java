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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.tag.ITagProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.ITemplateResolver;
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
public final class Configuration {
    

    public static final int DEFAULT_PARSED_TEMPLATE_CACHE_SIZE = 20;
    public static final IDialect STANDARD_THYMELEAF_DIALECT = new StandardDialect();

    
    private Set<DialectConfiguration> dialectConfigurations = null;
    
    private Set<ITemplateResolver> templateResolvers = new LinkedHashSet<ITemplateResolver>();
    private Set<IMessageResolver> messageResolvers = new LinkedHashSet<IMessageResolver>();
    
    private Map<String,Set<DialectConfiguration>> dialectsByTagName = null;
    private Map<String,Set<DialectConfiguration>> dialectsByAttrName = null;
    
    private Map<String,Object> mergedExecutionAttributes = new LinkedHashMap<String, Object>();
    private Map<String,Boolean> mergedLenienciesByPrefix = null;
    private Set<IDocTypeResolutionEntry> mergedDocTypeResolutionEntries = null;
    private Set<IDocTypeTranslation> mergedDocTypeTranslations = null;
    
    private int parsedTemplateCacheSize;
    
    private Set<IMessageResolver> defaultMessageResolvers = null;
    
    private boolean initialized;
    
    
    

    
    Configuration() {
        
        super();
        
        this.dialectConfigurations = new LinkedHashSet<DialectConfiguration>();
        this.dialectConfigurations.add(
                new DialectConfiguration(STANDARD_THYMELEAF_DIALECT.getPrefix(), STANDARD_THYMELEAF_DIALECT));
        this.parsedTemplateCacheSize = DEFAULT_PARSED_TEMPLATE_CACHE_SIZE;
        this.initialized = false;
        
    }



    
    
    private boolean isInitialized() {
        return this.initialized;
    }

    
    private void checkNotInitialized() {
        if (isInitialized()) {
            throw new AlreadyInitializedException(
                    "Cannot modify template engine configuration when it has already been initialized");
        }
    }
    
    private void checkInitialized() {
        if (!isInitialized()) {
            throw new NotInitializedException("Configuration has not been initialized");
        }
    }
    
    
    
    
    
    
    public synchronized void initialize() {
    
        if (!isInitialized()) {
            
            
            /*
             * Checking dialects exist
             */
            if (this.dialectConfigurations == null) {
                throw new ConfigurationException("Cannot initialize: a dialect has not been set");
            }

            
            /*
             * Initialize dialect configurations
             */
            for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
                dialectConfiguration.initialize();
            }
            
            
            /*
             * Merge dialects
             */
            final MergedDialectArtifacts mergedDialectArtifacts = mergeDialects(this.dialectConfigurations);
            this.dialectsByTagName =
                Collections.unmodifiableMap(mergedDialectArtifacts.getDialectsByTagName());
            this.dialectsByAttrName =
                Collections.unmodifiableMap(mergedDialectArtifacts.getDialectsByAttrName());
            this.mergedExecutionAttributes =
                Collections.unmodifiableMap(mergedDialectArtifacts.getExecutionAttributes());
            this.mergedLenienciesByPrefix = 
                Collections.unmodifiableMap(mergedDialectArtifacts.getLeniencyByPrefix());
            this.mergedDocTypeResolutionEntries = 
                Collections.unmodifiableSet(mergedDialectArtifacts.getDocTypeResolutionEntries());
            this.mergedDocTypeTranslations = 
                Collections.unmodifiableSet(mergedDialectArtifacts.getDocTypeTranslations());
            
            
            /*
             * Checking template resolvers
             */
            if (this.templateResolvers == null || this.templateResolvers.size() <= 0) {
                throw new ConfigurationException("Cannot initialize: no template resolvers have been set");
            }
            final List<ITemplateResolver> templateResolversList = 
                new ArrayList<ITemplateResolver>(this.templateResolvers);
            Collections.sort(templateResolversList, 
                    new Comparator<ITemplateResolver>() {
                        public int compare(final ITemplateResolver o1, final ITemplateResolver o2) {
                            if (o1.getOrder() == null) {
                                return -1;
                            }
                            if (o2.getOrder() == null) {
                                return 1;
                            }
                            return o1.getOrder().compareTo(o2.getOrder());
                        }
            });
            this.templateResolvers = new LinkedHashSet<ITemplateResolver>(templateResolversList);
            for (final ITemplateResolver templateResolver : this.templateResolvers) {
                templateResolver.initialize();
            }

            
            /*
             * Checking message resolvers
             */
            if (this.messageResolvers == null) {
                throw new ConfigurationException("Cannot initialize: message resolvers set is null");
            }
            if (this.messageResolvers.size() == 0) {
                // No message resolvers have been set, so default initialization will be performed
                if (this.defaultMessageResolvers == null || this.defaultMessageResolvers.size() == 0) {
                    throw new ConfigurationException(
                            "Cannot initialize: no message resolvers have been set and " +
                            "no default message resolvers have been set either.");
                }
                this.messageResolvers = this.defaultMessageResolvers;
            }
            final List<IMessageResolver> messageResolversList = 
                new ArrayList<IMessageResolver>(this.messageResolvers);
            Collections.sort(messageResolversList, 
                    new Comparator<IMessageResolver>() {
                        public int compare(final IMessageResolver o1, final IMessageResolver o2) {
                            if (o1.getOrder() == null) {
                                return -1;
                            }
                            if (o2.getOrder() == null) {
                                return 1;
                            }
                            return o1.getOrder().compareTo(o2.getOrder());
                        }
            });
            this.messageResolvers = new LinkedHashSet<IMessageResolver>(messageResolversList);
            for (final IMessageResolver messageResolver : this.messageResolvers) {
                messageResolver.initialize();
            }
            
            
            
            /*
             * Mark as initialized
             */
            this.initialized = true;
            
        }
       
    }
    
    
    
    synchronized void printConfiguration() {
        ConfigurationPrinterHelper.printConfiguration(
                this.dialectConfigurations, this.mergedLenienciesByPrefix,
                this.templateResolvers, this.messageResolvers, 
                this.parsedTemplateCacheSize);
    }
    
    
    
    
    
    
    
    public synchronized Map<String,IDialect> getDialects() {
        final Map<String,IDialect> dialects = new LinkedHashMap<String,IDialect>();
        for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
            dialects.put(dialectConfiguration.getPrefix(), dialectConfiguration.getDialect());
        }
        return dialects;
    }
    
    synchronized void setDialects(final Map<String,IDialect> dialects) {
        checkNotInitialized();
        Validate.notNull(dialects, "Dialect set cannot be null");
        Validate.isTrue(dialects.size() > 0, "Dialect set cannot be empty");
        this.dialectConfigurations.clear();
        for (final Map.Entry<String,IDialect> dialectEntry : dialects.entrySet()) {
            this.dialectConfigurations.add(new DialectConfiguration(dialectEntry.getKey(), dialectEntry.getValue()));
        }
    }
    
    synchronized void setDialect(final IDialect dialect) {
        checkNotInitialized();
        Validate.notNull(dialect, "Dialect set cannot be null");
        this.dialectConfigurations.clear();
        this.dialectConfigurations.add(new DialectConfiguration(dialect.getPrefix(), dialect));
    }
    
    synchronized void addDialect(final String prefix, final IDialect dialect) {
        checkNotInitialized();
        Validate.notNull(dialect, "Dialect set cannot be null");
        this.dialectConfigurations.add(new DialectConfiguration(prefix, dialect));
    }
    
    synchronized void clearDialects() {
        checkNotInitialized();
        this.dialectConfigurations.clear();
    }
    
    
    
    
    public synchronized Set<ITemplateResolver> getTemplateResolvers() {
        return Collections.unmodifiableSet(this.templateResolvers);
    }
    
    synchronized void setTemplateResolvers(final Set<? extends ITemplateResolver> templateResolvers) {
        checkNotInitialized();
        Validate.notNull(templateResolvers, "Template Resolver set cannot be null");
        Validate.isTrue(templateResolvers.size() > 0, "Template Resolver set cannot be empty");
        Validate.containsNoNulls(templateResolvers, "Template Resolver set cannot contain any nulls");
        this.templateResolvers = new LinkedHashSet<ITemplateResolver>(templateResolvers);
    }
    
    synchronized void addTemplateResolver(final ITemplateResolver templateResolver) {
        checkNotInitialized();
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        this.templateResolvers.add(templateResolver);
    }
    
    synchronized void setTemplateResolver(final ITemplateResolver templateResolver) {
        checkNotInitialized();
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        this.templateResolvers = Collections.singleton(templateResolver);
    }

    
    
    
    
    public synchronized Set<IMessageResolver> getMessageResolvers() {
        return Collections.unmodifiableSet(this.messageResolvers);
    }
    
    synchronized void setMessageResolvers(final Set<? extends IMessageResolver> messageResolvers) {
        checkNotInitialized();
        Validate.notNull(messageResolvers, "Message Resolver set cannot be null");
        Validate.isTrue(messageResolvers.size() > 0, "Message Resolver set cannot be empty");
        Validate.containsNoNulls(messageResolvers, "Message Resolver set cannot contain any nulls");
        this.messageResolvers = new LinkedHashSet<IMessageResolver>(messageResolvers);
    }
    
    synchronized void addMessageResolver(final IMessageResolver messageResolver) {
        checkNotInitialized();
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        this.messageResolvers.add(messageResolver);
    }
    
    synchronized void setMessageResolver(final IMessageResolver messageResolver) {
        checkNotInitialized();
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        this.messageResolvers = Collections.singleton(messageResolver);
    }
    
    
    
    
    synchronized void setDefaultMessageResolvers(final Set<? extends IMessageResolver> defaultMessageResolvers) {
        checkNotInitialized();
        Validate.notNull(defaultMessageResolvers, "Default Message Resolver set cannot be null");
        Validate.isTrue(defaultMessageResolvers.size() > 0, "Default Message Resolver set cannot be empty");
        Validate.containsNoNulls(defaultMessageResolvers, "Default Message Resolver set cannot contain any nulls");
        this.defaultMessageResolvers = new LinkedHashSet<IMessageResolver>(defaultMessageResolvers);
    }
    
    
    
    
    
    
    public synchronized int getParsedTemplateCacheSize() {
        return this.parsedTemplateCacheSize;
    }
    
    synchronized void setParsedTemplateCacheSize(final int parsedTemplateCacheSize) {
        checkNotInitialized();
        this.parsedTemplateCacheSize = parsedTemplateCacheSize;
    }
    
    
    
    
    
    
    public final Set<IDocTypeTranslation> getDocTypeTranslations() {
        checkInitialized();
        return this.mergedDocTypeTranslations;
    }

    
    public final IDocTypeTranslation getDocTypeTranslationBySource(final String publicID, final String systemID) {
        checkInitialized();
        for (final IDocTypeTranslation translation : this.mergedDocTypeTranslations) {
            if (translation.getSourcePublicID().matches(publicID) && translation.getSourceSystemID().matches(systemID)) {
                return translation;
            }
        }
        return null;
    }
    
    
    public final Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
        checkInitialized();
        return this.mergedDocTypeResolutionEntries;
    }


    
    public Set<IAttrProcessor> getAttrProcessors(final Class<? extends IDialect> dialectClass) {
        Validate.notNull(dialectClass, "Dialect class cannot be null");
        for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
            if (dialectConfiguration.getDialect().getClass().equals(dialectClass)) {
                return dialectConfiguration.getAttrProcessors();
            }
        }
        throw new ConfigurationException("Dialect " + dialectClass.getName() + " has not been configured");
    }

    public Set<String> getProcessedAttrNames() {
        return this.dialectsByAttrName.keySet();
    }

    final Set<DialectConfiguration> getDialectsForAttrName(final String attrName) {
        return this.dialectsByAttrName.get(attrName);
    }

    final IAttrProcessor getAttrProcessor(final Set<DialectConfiguration> applicableDialectConfigurations, final Element element, final Attr attr) {
        IAttrProcessor attrProcessor = null;
        for (final DialectConfiguration dialectConfiguration : applicableDialectConfigurations) {
            final IAttrProcessor dialectAttrProcessor = dialectConfiguration.getAttrProcessor(element, attr);
            if (dialectAttrProcessor != null) {
                if (attrProcessor == null) {
                    attrProcessor = dialectAttrProcessor;
                } else {
                    throw new ConfigurationException(
                            "More than one processor is applicable to the same attr \"" + attr.getName() + "\": " +
                            attrProcessor.getClass().getName() + " and " + dialectAttrProcessor.getClass().getName());
                }
            }
        }
        return attrProcessor;
    }

    
    
    public Set<ITagProcessor> getTagProcessors(final Class<? extends IDialect> dialectClass) {
        Validate.notNull(dialectClass, "Dialect class cannot be null");
        for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
            if (dialectConfiguration.getDialect().getClass().equals(dialectClass)) {
                return dialectConfiguration.getTagProcessors();
            }
        }
        throw new ConfigurationException("Dialect " + dialectClass.getName() + " has not been configured");
    }

    public Set<String> getProcessedTagNames() {
        return this.dialectsByTagName.keySet();
    }

    final Set<DialectConfiguration> getDialectsForTagName(final String tagName) {
        return this.dialectsByTagName.get(tagName);
    }

    final ITagProcessor getTagProcessor(final Set<DialectConfiguration> applicableDialectConfigurations, final Element element) {
        ITagProcessor tagProcessor = null;
        for (final DialectConfiguration dialectConfiguration : applicableDialectConfigurations) {
            final ITagProcessor dialectTagProcessor = dialectConfiguration.getTagProcessor(element);
            if (dialectTagProcessor != null) {
                if (tagProcessor == null) {
                    tagProcessor = dialectTagProcessor;
                } else {
                    throw new ConfigurationException(
                            "More than one processor is applicable to the same tag \"" + element.getTagName() + "\": " +
                            tagProcessor.getClass().getName() + " and " + dialectTagProcessor.getClass().getName());
                }
            }
        }
        return tagProcessor;
    }


    
    
    public Map<String,Object> getExecutionAttributes() {
        checkInitialized();
        return this.mergedExecutionAttributes;
    }

    
    public Set<String> getAllPrefixes() {
        checkInitialized();
        return this.mergedLenienciesByPrefix.keySet();
    }
    
    
    public boolean isLenient(final String prefix) {
        checkInitialized();
        final Boolean leniency = this.mergedLenienciesByPrefix.get(prefix);
        if (leniency == null) {
            throw new ConfigurationException(
                    "Cannot compute leniency for prefix \"" + prefix + "\": No dialect/s " +
                    "have been configured for such prefix."); 
        }
        return leniency.booleanValue();
    }
    
    
    public boolean isPrefixManaged(final String prefix) {
        return this.mergedLenienciesByPrefix.containsKey(prefix);
    }
    
    
    public final String getXmlNsAttrName(final String prefix) {
        return (prefix == null || prefix.trim().equals("")? null : "xmlns:" + prefix);
    }

    
    

    
    
    private static MergedDialectArtifacts mergeDialects(final Set<DialectConfiguration> dialectConfigurations) {
        
        if (dialectConfigurations == null || dialectConfigurations.isEmpty()) {
            throw new ConfigurationException("No dialect has been specified");
        }
        
        final Map<String,Set<DialectConfiguration>> dialectsByTagName = new LinkedHashMap<String, Set<DialectConfiguration>>();
        final Map<String,Set<DialectConfiguration>> dialectsByAttrName = new LinkedHashMap<String, Set<DialectConfiguration>>();
        final Map<String,Object> executionAttributes = new LinkedHashMap<String, Object>();
        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = new HashSet<IDocTypeResolutionEntry>();
        final Set<IDocTypeTranslation> docTypeTranslations = new HashSet<IDocTypeTranslation>();
        final Map<String,Boolean> leniencyByPrefix = new HashMap<String, Boolean>();
        
        if (dialectConfigurations.size() == 1) {
            // No conflicts possible!
            
            final DialectConfiguration dialectConfiguration = dialectConfigurations.iterator().next();
            final IDialect dialect = dialectConfiguration.getDialect();
            
            for (final String processedTagName : dialectConfiguration.getProcessedTagNames()) {
                dialectsByTagName.put(processedTagName, Collections.singleton(dialectConfiguration));
            }
            for (final String processedAttrName : dialectConfiguration.getProcessedAttrNames()) {
                dialectsByAttrName.put(processedAttrName, Collections.singleton(dialectConfiguration));
            }
            executionAttributes.putAll(dialectConfiguration.getExecutionAttributes());
            leniencyByPrefix.put(dialectConfiguration.getPrefix(), Boolean.valueOf(dialectConfiguration.isLenient()));
            docTypeResolutionEntries.addAll(dialect.getDocTypeResolutionEntries());
            docTypeTranslations.addAll(dialect.getDocTypeTranslations());
            
            return new MergedDialectArtifacts(
                    dialectsByTagName, dialectsByAttrName, executionAttributes,
                    leniencyByPrefix, dialect.getDocTypeResolutionEntries(), dialect.getDocTypeTranslations());
            
        }
        
        
        /*
         * THERE ARE MORE THAN ONE DIALECT: MERGE THEM
         */
        final Set<Class<? extends IDialect>> mergedDialectClasses = new HashSet<Class<? extends IDialect>>();
        
        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {

            
            final IDialect dialect = dialectConfiguration.getDialect();

            /*
             * Check the dialect is not being repeated
             */
            if (mergedDialectClasses.contains(dialect.getClass())) {
                throw new ConfigurationException(
                        "Dialect is declared twice: " + dialect.getClass().getName());
            }

            
            
            /*
             * Build maps linking tag names to the dialect configurations corresponding
             * to dialects that can process each of those tag names.
             */
            
            for (final String dialectProcessedTagName : dialectConfiguration.getProcessedTagNames()) {
                Set<DialectConfiguration> dialectConfigurationsForTagName = dialectsByTagName.get(dialectProcessedTagName);
                if (dialectConfigurationsForTagName == null) {
                    dialectConfigurationsForTagName = new LinkedHashSet<DialectConfiguration>();
                    dialectsByTagName.put(dialectProcessedTagName, dialectConfigurationsForTagName);
                }
                dialectConfigurationsForTagName.add(dialectConfiguration);
            }
            

            
            /*
             * Build maps linking tag names to the dialect configurations corresponding
             * to dialects that can process each of those tag names.
             */
            
            for (final String dialectProcessedAttrName : dialectConfiguration.getProcessedAttrNames()) {
                Set<DialectConfiguration> dialectConfigurationsForAttrName = dialectsByAttrName.get(dialectProcessedAttrName);
                if (dialectConfigurationsForAttrName == null) {
                    dialectConfigurationsForAttrName = new LinkedHashSet<DialectConfiguration>();
                    dialectsByAttrName.put(dialectProcessedAttrName, dialectConfigurationsForAttrName);
                }
                dialectConfigurationsForAttrName.add(dialectConfiguration);
            }
            

            /*
             * Merge execution attributes
             */
            executionAttributes.putAll(dialectConfiguration.getExecutionAttributes());
            
            
            /*
             * Merge leniency flags per prefix. A prefix will be considered to be "lenient" 
             * if at least one of the dialects configured with that prefix is lenient.
             */
            
            final String prefix = dialectConfiguration.getPrefix();
            if (leniencyByPrefix.containsKey(prefix)) {
                leniencyByPrefix.put(prefix, Boolean.valueOf(dialectConfiguration.isLenient() || leniencyByPrefix.get(prefix).booleanValue()));
            } else {
                leniencyByPrefix.put(prefix, Boolean.valueOf(dialectConfiguration.isLenient()));
            }
            
            
            /*
             * Check that two dialects do not specify conflicting DOCTYPE resolution entries
             * for the same PUBLIC and SYSTEM IDs. 
             */
            
            final Set<IDocTypeResolutionEntry> dialectDocTypeResolutionEntries = 
                dialect.getDocTypeResolutionEntries();

            for (final IDocTypeResolutionEntry dialectDocTypeResolutionEntry : dialectDocTypeResolutionEntries) {
                
                boolean addDialectDocTypeResolutionEntry = true;
                
                final DocTypeIdentifier dialectDocTypeResolutionEntryPublicID = dialectDocTypeResolutionEntry.getPublicID();
                final DocTypeIdentifier dialectDocTypeResolutionEntrySystemID = dialectDocTypeResolutionEntry.getSystemID();

                for (final IDocTypeResolutionEntry docTypeResolutionEntry : docTypeResolutionEntries) {
                    
                    final DocTypeIdentifier docTypeResolutionEntryPublicID = docTypeResolutionEntry.getPublicID();
                    final DocTypeIdentifier docTypeResolutionEntrySystemID = docTypeResolutionEntry.getSystemID();
                    
                    boolean publicIDMatches = false;
                    boolean systemIDMatches = false;
                    
                    if (dialectDocTypeResolutionEntryPublicID == null) {
                        publicIDMatches = (docTypeResolutionEntryPublicID == null);
                    } else {
                        publicIDMatches = (docTypeResolutionEntryPublicID != null && docTypeResolutionEntryPublicID.equals(dialectDocTypeResolutionEntryPublicID));
                    }
                    
                    if (dialectDocTypeResolutionEntrySystemID == null) {
                        systemIDMatches = (docTypeResolutionEntrySystemID == null);
                    } else {
                        systemIDMatches = (docTypeResolutionEntrySystemID != null && docTypeResolutionEntrySystemID.equals(dialectDocTypeResolutionEntrySystemID));
                    }
                    
                    if (publicIDMatches && systemIDMatches) {
                        if (!dialectDocTypeResolutionEntry.equals(docTypeResolutionEntry)) {
                            throw new ConfigurationException(
                                    "Cannot initialize: two dialects provide different (non-equal) " +
                                    "DOCTYPE resolution entries for PUBLICID \"" + docTypeResolutionEntryPublicID + 
                                    "\" and SYSTEMID \"" + docTypeResolutionEntrySystemID + "\"");
                        }
                        // No need to repeat an entry (even if it is a Set!)
                        addDialectDocTypeResolutionEntry = false;
                    }
                    
                }
                
                if (addDialectDocTypeResolutionEntry) {
                    docTypeResolutionEntries.add(dialectDocTypeResolutionEntry);
                }
                
            }

            
            
            /*
             * Check that two dialects do not specify conflicting DOCTYPE translations
             * for the same PUBLIC and SYSTEM IDs. 
             */
            
            final Set<IDocTypeTranslation> dialectDocTypeTranslations = dialect.getDocTypeTranslations();

            for (final IDocTypeTranslation dialectDocTypeTranslation : dialectDocTypeTranslations) {
                
                boolean addDialectDocTypeTranslation = true;
                
                final DocTypeIdentifier dialectDocTypeTranslationSourcePublicID = dialectDocTypeTranslation.getSourcePublicID();
                final DocTypeIdentifier dialectDocTypeTranslationSourceSystemID = dialectDocTypeTranslation.getSourceSystemID();
                final DocTypeIdentifier dialectDocTypeTranslationTargetPublicID = dialectDocTypeTranslation.getTargetPublicID();
                final DocTypeIdentifier dialectDocTypeTranslationTargetSystemID = dialectDocTypeTranslation.getTargetSystemID();

                for (final IDocTypeTranslation docTypeTranslation : docTypeTranslations) {
                    
                    final DocTypeIdentifier docTypeTranslationSourcePublicID = docTypeTranslation.getSourcePublicID();
                    final DocTypeIdentifier docTypeTranslationSourceSystemID = docTypeTranslation.getSourceSystemID();
                    final DocTypeIdentifier docTypeTranslationTargetPublicID = docTypeTranslation.getTargetPublicID();
                    final DocTypeIdentifier docTypeTranslationTargetSystemID = docTypeTranslation.getTargetSystemID();
                    
                    boolean sourcePublicIDMatches = false;
                    boolean sourceSystemIDMatches = false;
                    
                    if (dialectDocTypeTranslationSourcePublicID == null) {
                        sourcePublicIDMatches = (docTypeTranslationSourcePublicID == null);
                    } else {
                        sourcePublicIDMatches = (docTypeTranslationSourcePublicID != null && docTypeTranslationSourcePublicID.equals(dialectDocTypeTranslationSourcePublicID));
                    }
                    
                    if (dialectDocTypeTranslationSourceSystemID == null) {
                        sourceSystemIDMatches = (docTypeTranslationSourceSystemID == null);
                    } else {
                        sourceSystemIDMatches = (docTypeTranslationSourceSystemID != null && docTypeTranslationSourceSystemID.equals(dialectDocTypeTranslationSourceSystemID));
                    }
                    
                    if (sourcePublicIDMatches && sourceSystemIDMatches) {

                        boolean targetPublicIDMatches = false;
                        boolean targetSystemIDMatches = false;
                        
                        if (dialectDocTypeTranslationTargetPublicID == null) {
                            targetPublicIDMatches = (docTypeTranslationTargetPublicID == null);
                        } else {
                            targetPublicIDMatches = (docTypeTranslationTargetPublicID != null && docTypeTranslationTargetPublicID.equals(dialectDocTypeTranslationTargetPublicID));
                        }
                        
                        if (dialectDocTypeTranslationTargetSystemID == null) {
                            targetSystemIDMatches = (docTypeTranslationTargetSystemID == null);
                        } else {
                            targetSystemIDMatches = (docTypeTranslationTargetSystemID != null && docTypeTranslationTargetSystemID.equals(dialectDocTypeTranslationTargetSystemID));
                        }
                        
                        if (!targetPublicIDMatches || !targetSystemIDMatches) {
                            throw new ConfigurationException(
                                    "Cannot initialize: two dialects provide different (non-equal) " +
                                    "DOCTYPE translations for PUBLICID \"" + docTypeTranslationSourcePublicID + 
                                    "\" and SYSTEMID \"" + docTypeTranslationSourceSystemID + "\"");
                        }
                        
                        // No need to repeat a translation (even if it is a Set!)
                        addDialectDocTypeTranslation = false;
                        
                    }
                    
                }
                
                if (addDialectDocTypeTranslation) {
                    docTypeTranslations.add(dialectDocTypeTranslation);
                }
                
            }
            
            mergedDialectClasses.add(dialect.getClass());
            
        }
        
        return new MergedDialectArtifacts(
                dialectsByTagName, dialectsByAttrName, executionAttributes,
                leniencyByPrefix, docTypeResolutionEntries, docTypeTranslations);
        
    }
    
    
    
    
    private static final class MergedDialectArtifacts {
        
        private final Map<String,Set<DialectConfiguration>> dialectsByTagName;
        private final Map<String,Set<DialectConfiguration>> dialectsByAttrName;
        private final Map<String,Object> executionAttributes;
        private final Map<String,Boolean> leniencyByPrefix;
        private final Set<IDocTypeResolutionEntry> docTypeResolutionEntries;
        private final Set<IDocTypeTranslation> docTypeTranslations;
        
        
        public MergedDialectArtifacts(
                final Map<String,Set<DialectConfiguration>> dialectsByTagName,
                final Map<String,Set<DialectConfiguration>> dialectsByAttrName,
                final Map<String,Object> executionAttributes,
                final Map<String,Boolean> leniencyByPrefix,
                final Set<IDocTypeResolutionEntry> docTypeResolutionEntries,
                final Set<IDocTypeTranslation> docTypeTranslations) {
            super();
            this.dialectsByTagName = dialectsByTagName;
            this.dialectsByAttrName = dialectsByAttrName;
            this.executionAttributes = executionAttributes;
            this.leniencyByPrefix = leniencyByPrefix;
            this.docTypeResolutionEntries = docTypeResolutionEntries;
            this.docTypeTranslations = docTypeTranslations;
        }
        
        public Map<String, Set<DialectConfiguration>> getDialectsByTagName() {
            return this.dialectsByTagName;
        }

        public Map<String, Set<DialectConfiguration>> getDialectsByAttrName() {
            return this.dialectsByAttrName;
        }
        
        public Map<String,Object> getExecutionAttributes() {
            return this.executionAttributes;
        }

        public Map<String, Boolean> getLeniencyByPrefix() {
            return this.leniencyByPrefix;
        }

        public Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
            return this.docTypeResolutionEntries;
        }

        public Set<IDocTypeTranslation> getDocTypeTranslations() {
            return this.docTypeTranslations;
        }
        
    }
    
    

    
}
