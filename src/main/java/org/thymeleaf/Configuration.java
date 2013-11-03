/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.Serializable;
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

import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.processor.ProcessorAndContext;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   General configuration class, containing all the configuration items
 *   for a {@link TemplateEngine}, including all the info for every configured
 *   {@link IDialect}.
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
public final class Configuration {


    /**
     * @deprecated Deprecated in 2.1.0. Create a new instance of the StandardDialect using its constructors instead.
     *             Will be removed in 3.0
     */
    @Deprecated
    public static final IDialect STANDARD_THYMELEAF_DIALECT = new StandardDialect();

    private static final TemplateResolverComparator TEMPLATE_RESOLVER_COMPARATOR = new TemplateResolverComparator();
    private static final MessageResolverComparator MESSAGE_RESOLVER_COMPARATOR = new MessageResolverComparator();

    private Set<DialectConfiguration> dialectConfigurations = null;
    private Map<String,IDialect> dialectsByPrefix = null;
    private Set<IDialect> dialectSet = null;
    
    private Set<ITemplateResolver> templateResolvers = new LinkedHashSet<ITemplateResolver>(3);
    private Set<IMessageResolver> messageResolvers = new LinkedHashSet<IMessageResolver>(3);
    private Set<ITemplateModeHandler> templateModeHandlers = new LinkedHashSet<ITemplateModeHandler>(8);
    
    private ICacheManager cacheManager = null;
    
    private Map<String,Set<ProcessorAndContext>> mergedSpecificProcessorsByElementName;
    private Map<String,Set<ProcessorAndContext>> mergedSpecificProcessorsByAttributeName;
    private Map<Class<? extends Node>, Set<ProcessorAndContext>> mergedNonSpecificProcessorsByNodeClass;

    private Map<String,Object> mergedExecutionAttributes = null;
    private Set<IDocTypeResolutionEntry> mergedDocTypeResolutionEntries = null;
    private Set<IDocTypeTranslation> mergedDocTypeTranslations = null;

    private final Map<String,ITemplateModeHandler> templateModeHandlersByName = new HashMap<String,ITemplateModeHandler>(8,1.0f);
    
    private Set<IMessageResolver> defaultMessageResolvers = null;
    private Set<ITemplateModeHandler> defaultTemplateModeHandlers = null;
    
    private volatile boolean initialized;
    
    

    
    public Configuration() {
        
        super();

        final StandardDialect standardDialect = new StandardDialect();

        this.dialectConfigurations = new LinkedHashSet<DialectConfiguration>(4);
        this.dialectConfigurations.add(
                new DialectConfiguration(standardDialect.getPrefix(), standardDialect));
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
             * Initialize "dialects by prefix" map and the "dialect set"
             */
            this.dialectsByPrefix = new LinkedHashMap<String, IDialect>(4,1.0f);
            for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
                this.dialectsByPrefix.put(dialectConfiguration.getPrefix(), dialectConfiguration.getDialect());
            }
            this.dialectsByPrefix = Collections.unmodifiableMap(this.dialectsByPrefix);
            this.dialectSet = Collections.unmodifiableSet(new LinkedHashSet<IDialect>(this.dialectsByPrefix.values()));
            
            
            /*
             * Merge dialects
             */
            final MergedDialectArtifacts mergedDialectArtifacts = mergeDialects(this.dialectConfigurations);
            this.mergedSpecificProcessorsByElementName =
                Collections.unmodifiableMap(mergedDialectArtifacts.getSpecificProcessorsByElementName());
            this.mergedSpecificProcessorsByAttributeName =
                Collections.unmodifiableMap(mergedDialectArtifacts.getSpecificProcessorsByAttributeName());
            this.mergedNonSpecificProcessorsByNodeClass =
                Collections.unmodifiableMap(mergedDialectArtifacts.getMergedNonSpecificProcessorsByNodeClass());
            this.mergedExecutionAttributes =
                Collections.unmodifiableMap(mergedDialectArtifacts.getExecutionAttributes());
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
            for (final ITemplateResolver templateResolver : templateResolversList) {
                templateResolver.initialize();
            }
            Collections.sort(templateResolversList, TEMPLATE_RESOLVER_COMPARATOR);
            this.templateResolvers = new LinkedHashSet<ITemplateResolver>(templateResolversList);

            
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
            
            for (final IMessageResolver messageResolver : this.messageResolvers) {
                messageResolver.initialize();
            }
            
            final List<IMessageResolver> messageResolversList = 
                new ArrayList<IMessageResolver>(this.messageResolvers);
            
            Collections.sort(messageResolversList, MESSAGE_RESOLVER_COMPARATOR);

            this.messageResolvers = new LinkedHashSet<IMessageResolver>(messageResolversList);

            
            /*
             * Checking template mode handlers
             */
            if (this.templateModeHandlers == null) {
                throw new ConfigurationException("Cannot initialize: template mode handlers set is null");
            }
            if (this.templateModeHandlers.size() == 0) {
                // No message resolvers have been set, so default initialization will be performed
                if (this.defaultTemplateModeHandlers == null || this.defaultTemplateModeHandlers.size() == 0) {
                    throw new ConfigurationException(
                            "Cannot initialize: no template mode handlers have been set and " +
                            "no default template mode handlers have been set either.");
                }
                this.templateModeHandlers = this.defaultTemplateModeHandlers;
            }

            for (final ITemplateModeHandler handler : this.templateModeHandlers) {
                if (this.templateModeHandlersByName.containsKey(handler.getTemplateModeName())) {
                    throw new ConfigurationException(
                            "More than one handler configured for template mode \"" + handler.getTemplateModeName() + "\"");
                }
                if (handler.getTemplateParser() == null) {
                    throw new ConfigurationException(
                            "Null parser returned by handler for template mode \"" + handler.getTemplateModeName() + "\"");
                }
                this.templateModeHandlersByName.put(handler.getTemplateModeName(), handler);
            }
            
            
            /*
             * Mark as initialized
             */
            this.initialized = true;
            
        }
       
    }
    
    
    
    void printConfiguration() {
        ConfigurationPrinterHelper.printConfiguration(
                this.dialectConfigurations, this.templateResolvers, this.messageResolvers,
                this.cacheManager, this.templateModeHandlers);
    }
    
    
    
    
    
    
    public ICacheManager getCacheManager() {
        return this.cacheManager;
    }
    
    
    public void setCacheManager(final ICacheManager cacheManager) {
        // Can be set to null (= no caches)
        checkNotInitialized();
        this.cacheManager = cacheManager;
    }

 
    
    
    
    public Set<IDialect> getDialectSet() {
        if (!isInitialized()) {
            // If we haven't initialized yet, compute
            return Collections.unmodifiableSet(new LinkedHashSet<IDialect>(getDialects().values()));
        }
        return this.dialectSet;
    }

    
    public Map<String,IDialect> getDialects() {
        if (!isInitialized()) {
            // If we haven't initialized yet, compute
            final Map<String,IDialect> dialects = new LinkedHashMap<String, IDialect>(4, 1.0f);
            for (final DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
                dialects.put(dialectConfiguration.getPrefix(), dialectConfiguration.getDialect());
            }
            return Collections.unmodifiableMap(dialects);
        }
        return this.dialectsByPrefix;
    }
    
    public void setDialects(final Map<String,IDialect> dialects) {
        checkNotInitialized();
        Validate.notNull(dialects, "Dialect set cannot be null");
        Validate.isTrue(dialects.size() > 0, "Dialect set cannot be empty");
        this.dialectConfigurations.clear();
        for (final Map.Entry<String,IDialect> dialectEntry : dialects.entrySet()) {
            this.dialectConfigurations.add(new DialectConfiguration(dialectEntry.getKey(), dialectEntry.getValue()));
        }
    }
    
    public void setDialect(final IDialect dialect) {
        checkNotInitialized();
        Validate.notNull(dialect, "Dialect set cannot be null");
        this.dialectConfigurations.clear();
        this.dialectConfigurations.add(new DialectConfiguration(dialect.getPrefix(), dialect));
    }
    
    public void addDialect(final String prefix, final IDialect dialect) {
        checkNotInitialized();
        Validate.notNull(dialect, "Dialect set cannot be null");
        this.dialectConfigurations.add(new DialectConfiguration(prefix, dialect));
    }
    
    public void clearDialects() {
        checkNotInitialized();
        this.dialectConfigurations.clear();
    }
    
    
    
    
    public Set<ITemplateResolver> getTemplateResolvers() {
        return Collections.unmodifiableSet(this.templateResolvers);
    }
    
    public void setTemplateResolvers(final Set<? extends ITemplateResolver> templateResolvers) {
        checkNotInitialized();
        Validate.notNull(templateResolvers, "Template Resolver set cannot be null");
        Validate.isTrue(templateResolvers.size() > 0, "Template Resolver set cannot be empty");
        Validate.containsNoNulls(templateResolvers, "Template Resolver set cannot contain any nulls");
        this.templateResolvers = new LinkedHashSet<ITemplateResolver>(templateResolvers);
    }
    
    public void addTemplateResolver(final ITemplateResolver templateResolver) {
        checkNotInitialized();
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        this.templateResolvers.add(templateResolver);
    }
    
    public void setTemplateResolver(final ITemplateResolver templateResolver) {
        checkNotInitialized();
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        this.templateResolvers = Collections.singleton(templateResolver);
    }

    
    
    
    
    public Set<IMessageResolver> getMessageResolvers() {
        return Collections.unmodifiableSet(this.messageResolvers);
    }
    
    public void setMessageResolvers(final Set<? extends IMessageResolver> messageResolvers) {
        checkNotInitialized();
        Validate.notNull(messageResolvers, "Message Resolver set cannot be null");
        Validate.isTrue(messageResolvers.size() > 0, "Message Resolver set cannot be empty");
        Validate.containsNoNulls(messageResolvers, "Message Resolver set cannot contain any nulls");
        this.messageResolvers = new LinkedHashSet<IMessageResolver>(messageResolvers);
    }
    
    public void addMessageResolver(final IMessageResolver messageResolver) {
        checkNotInitialized();
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        this.messageResolvers.add(messageResolver);
    }
    
    public void setMessageResolver(final IMessageResolver messageResolver) {
        checkNotInitialized();
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        this.messageResolvers = Collections.singleton(messageResolver);
    }
    
    
    
    
    public void setDefaultMessageResolvers(final Set<? extends IMessageResolver> defaultMessageResolvers) {
        checkNotInitialized();
        Validate.notNull(defaultMessageResolvers, "Default Message Resolver set cannot be null");
        Validate.isTrue(defaultMessageResolvers.size() > 0, "Default Message Resolver set cannot be empty");
        Validate.containsNoNulls(defaultMessageResolvers, "Default Message Resolver set cannot contain any nulls");
        this.defaultMessageResolvers = new LinkedHashSet<IMessageResolver>(defaultMessageResolvers);
    }
    
    
    
    
    
    
    public Set<ITemplateModeHandler> getTemplateModeHandlers() {
        return Collections.unmodifiableSet(this.templateModeHandlers);
    }
    
    public ITemplateModeHandler getTemplateModeHandler(final String templateMode) {
        return this.templateModeHandlersByName.get(templateMode);
    }
    
    public void setTemplateModeHandlers(final Set<? extends ITemplateModeHandler> templateModeHandlers) {
        checkNotInitialized();
        Validate.notNull(templateModeHandlers, "Template Mode Handler set cannot be null");
        Validate.isTrue(templateModeHandlers.size() > 0, "Template Mode Handler set cannot be empty");
        Validate.containsNoNulls(templateModeHandlers, "Template Mode Handler set cannot contain any nulls");
        this.templateModeHandlers = new LinkedHashSet<ITemplateModeHandler>(templateModeHandlers);
    }
    
    public void addTemplateModeHandler(final ITemplateModeHandler templateModeHandler) {
        checkNotInitialized();
        Validate.notNull(templateModeHandler, "Template Mode Handler cannot be null");
        this.templateModeHandlers.add(templateModeHandler);
    }
    
    
    public void setDefaultTemplateModeHandlers(final Set<? extends ITemplateModeHandler> defaultTemplateModeHandlers) {
        checkNotInitialized();
        Validate.notNull(defaultTemplateModeHandlers, "Default Template Mode Handler set cannot be null");
        Validate.isTrue(defaultTemplateModeHandlers.size() > 0, "Default Template Mode Handler set cannot be empty");
        Validate.containsNoNulls(defaultTemplateModeHandlers, "Default Template Mode Handler set cannot contain any nulls");
        this.defaultTemplateModeHandlers = new LinkedHashSet<ITemplateModeHandler>(defaultTemplateModeHandlers);
    }
    
    
    
    
    
    
    public Set<IDocTypeTranslation> getDocTypeTranslations() {
        checkInitialized();
        return this.mergedDocTypeTranslations;
    }

    
    public IDocTypeTranslation getDocTypeTranslationBySource(final String publicID, final String systemID) {
        checkInitialized();
        for (final IDocTypeTranslation translation : this.mergedDocTypeTranslations) {
            if (translation.getSourcePublicID().matches(publicID) && translation.getSourceSystemID().matches(systemID)) {
                return translation;
            }
        }
        return null;
    }
    
    
    public Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
        checkInitialized();
        return this.mergedDocTypeResolutionEntries;
    }


    
    
    
    
    /**
     * <p>
     *   Computes all the processors that should be applied to a specific {@link Node}.
     * </p>
     * <p>
     *   Results are returned ordered by precedence.
     * </p>
     * 
     * @param node the node to compute
     * @return an ArrayList with the list of processors, as {@link ProcessorAndContext} objects.
     */
    public ArrayList<ProcessorAndContext> computeProcessorsForNode(final Node node) {
        
        if (node instanceof NestableAttributeHolderNode) {

            final ArrayList<ProcessorAndContext> processors = new ArrayList<ProcessorAndContext>(2);
            
            final NestableAttributeHolderNode nestableNode = (NestableAttributeHolderNode) node;

            if (node instanceof Element) {
                
                final Element element = (Element) nestableNode;
                
                final Set<ProcessorAndContext> processorsForElementName = 
                        this.mergedSpecificProcessorsByElementName.get(element.getNormalizedName());
                if (processorsForElementName != null) {
                    for (final ProcessorAndContext processorAndContext : processorsForElementName) {
                        if (processorAndContext.matches(node)) {
                            processors.add(processorAndContext);
                        }
                    }
                }
                
            }

            final String[] normalizedAttributeNames = nestableNode.unsafeGetAttributeNormalizedNames();
            final int normalizedAttributesLen = nestableNode.numAttributes();
            for (int i = 0; i < normalizedAttributesLen; i++) {
                final String normalizedAttributeName = normalizedAttributeNames[i];
                final Set<ProcessorAndContext> processorsForAttributeName = 
                        this.mergedSpecificProcessorsByAttributeName.get(normalizedAttributeName);
                if (processorsForAttributeName != null) {
                    for (final ProcessorAndContext processorAndContext : processorsForAttributeName) {
                        if (processorAndContext.matches(node)) {
                            processors.add(processorAndContext);
                        }
                    }
                }
            }
        
            final Set<ProcessorAndContext> applicableNonSpecificProcessors = 
                    getApplicableNonSpecificProcessorsToNodeClass(NestableAttributeHolderNode.class);
            if (applicableNonSpecificProcessors != null) {
                for (final ProcessorAndContext processorAndContext : applicableNonSpecificProcessors) {
                    if (processorAndContext.matches(node)) {
                        processors.add(processorAndContext);
                    }
                }
            }

            if (processors.size() > 1) {
                // Order (usually by precedence)
                Collections.sort(processors);
            }

            return processors;
            
        }

        //
        // NODE IS NOT AN ELEMENT...
        //

        final Set<ProcessorAndContext> applicableNonSpecificProcessors = 
                getApplicableNonSpecificProcessorsToNodeClass(node.getClass());
        
        if (applicableNonSpecificProcessors != null) {
        
            final ArrayList<ProcessorAndContext> processors = new ArrayList<ProcessorAndContext>(2);
        
            for (final ProcessorAndContext processorAndContext : applicableNonSpecificProcessors) {
                if (processorAndContext.matches(node)) {
                    processors.add(processorAndContext);
                }
            }

            // Order (usually by precedence)
            Collections.sort(processors);
            
            return processors;
            
        }
        
        // No processors to be returned
        return null;
        
    }
    
    


    
    
    public Map<String,Object> getExecutionAttributes() {
        checkInitialized();
        return this.mergedExecutionAttributes;
    }

    
    public Set<String> getAllPrefixes() {
        checkInitialized();
        return this.dialectsByPrefix.keySet();
    }

    
    public boolean isPrefixManaged(final String prefix) {
        return this.dialectsByPrefix.containsKey(prefix);
    }

    
    
    private Set<ProcessorAndContext> getApplicableNonSpecificProcessorsToNodeClass(final Class<? extends Node> nodeClass) {
        
        Set<ProcessorAndContext> result = null;
        for (final Map.Entry<Class<? extends Node>, Set<ProcessorAndContext>> entry : this.mergedNonSpecificProcessorsByNodeClass.entrySet()) {
            final Class<? extends Node> entryNodeClass = entry.getKey();
            if (entryNodeClass.isAssignableFrom(nodeClass)) {
                if (result == null) {
                    result = new HashSet<ProcessorAndContext>(2);
                }
                result.addAll(entry.getValue());
            }
        }
        return result;
        
    }
    
    
    private static MergedDialectArtifacts mergeDialects(final Set<DialectConfiguration> dialectConfigurations) {
        
        if (dialectConfigurations == null || dialectConfigurations.isEmpty()) {
            throw new ConfigurationException("No dialect has been specified");
        }
        
        final Map<String,Set<ProcessorAndContext>> specificProcessorsByElementName = new HashMap<String, Set<ProcessorAndContext>>(20);
        final Map<String,Set<ProcessorAndContext>> specificProcessorsByAttributeName = new HashMap<String, Set<ProcessorAndContext>>(20);
        final Map<Class<? extends Node>, Set<ProcessorAndContext>> nonSpecificProcessorsByNodeClass = new HashMap<Class<? extends Node>, Set<ProcessorAndContext>>(20);
        final Map<String,Object> executionAttributes = new HashMap<String, Object>(20);
        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = new HashSet<IDocTypeResolutionEntry>(20);
        final Set<IDocTypeTranslation> docTypeTranslations = new HashSet<IDocTypeTranslation>(20);

        if (dialectConfigurations.size() == 1) {
            // No conflicts possible!
            
            final DialectConfiguration dialectConfiguration = dialectConfigurations.iterator().next();
            final IDialect dialect = dialectConfiguration.getDialect();

            specificProcessorsByElementName.putAll(dialectConfiguration.unsafeGetSpecificProcessorsByElementName());
            specificProcessorsByAttributeName.putAll(dialectConfiguration.unsafeGetSpecificProcessorsByAttributeName());
            nonSpecificProcessorsByNodeClass.putAll(dialectConfiguration.unsafeGetNonSpecificProcessorsByNodeClass());

            executionAttributes.putAll(dialectConfiguration.getExecutionAttributes());
            docTypeResolutionEntries.addAll(dialect.getDocTypeResolutionEntries());
            docTypeTranslations.addAll(dialect.getDocTypeTranslations());
            
            return new MergedDialectArtifacts(
                    specificProcessorsByElementName, specificProcessorsByAttributeName, nonSpecificProcessorsByNodeClass,
                    executionAttributes, dialect.getDocTypeResolutionEntries(), dialect.getDocTypeTranslations());
            
        }
        
        
        /*
         * THERE ARE MORE THAN ONE DIALECT: MERGE THEM
         */
        final Set<Class<? extends IDialect>> mergedDialectClasses = new HashSet<Class<? extends IDialect>>(5,1.0f);
        
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
             * Aggregate all the processors assigned to a specific element name
             */
            specificProcessorsByElementName.putAll(dialectConfiguration.unsafeGetSpecificProcessorsByElementName());
            

            
            /*
             * Aggregate all the processors assigned to a specific attribute name
             */
            specificProcessorsByAttributeName.putAll(dialectConfiguration.unsafeGetSpecificProcessorsByAttributeName());
            

            
            /*
             * Aggregate all the processors not assigned to a specific attribute or element name
             */
            nonSpecificProcessorsByNodeClass.putAll(dialectConfiguration.unsafeGetNonSpecificProcessorsByNodeClass());
            

            /*
             * Merge execution attributes
             */
            executionAttributes.putAll(dialectConfiguration.getExecutionAttributes());

            
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
                    
                    final boolean publicIDMatches;
                    if (dialectDocTypeResolutionEntryPublicID == null) {
                        publicIDMatches = (docTypeResolutionEntryPublicID == null);
                    } else {
                        publicIDMatches = (docTypeResolutionEntryPublicID != null && docTypeResolutionEntryPublicID.equals(dialectDocTypeResolutionEntryPublicID));
                    }

                    final boolean systemIDMatches;
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

                        final boolean targetPublicIDMatches;
                        if (dialectDocTypeTranslationTargetPublicID == null) {
                            targetPublicIDMatches = (docTypeTranslationTargetPublicID == null);
                        } else {
                            targetPublicIDMatches = (docTypeTranslationTargetPublicID != null && docTypeTranslationTargetPublicID.equals(dialectDocTypeTranslationTargetPublicID));
                        }

                        final boolean targetSystemIDMatches;
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
                specificProcessorsByElementName, specificProcessorsByAttributeName, nonSpecificProcessorsByNodeClass,
                executionAttributes, docTypeResolutionEntries, docTypeTranslations);
        
    }
    
    
    
    
    private static final class MergedDialectArtifacts {
        
        private final Map<String,Set<ProcessorAndContext>> specificProcessorsByElementName;
        private final Map<String,Set<ProcessorAndContext>> specificProcessorsByAttributeName;
        private final Map<Class<? extends Node>, Set<ProcessorAndContext>> nonSpecificProcessorsByNodeClass;
        private final Map<String,Object> executionAttributes;
        private final Set<IDocTypeResolutionEntry> docTypeResolutionEntries;
        private final Set<IDocTypeTranslation> docTypeTranslations;
        
        
        MergedDialectArtifacts(
                final Map<String,Set<ProcessorAndContext>> specificProcessorsByElementName,
                final Map<String,Set<ProcessorAndContext>> specificProcessorsByAttributeName,
                final Map<Class<? extends Node>,Set<ProcessorAndContext>> nonSpecificProcessorsByNodeClass,
                final Map<String,Object> executionAttributes,
                final Set<IDocTypeResolutionEntry> docTypeResolutionEntries,
                final Set<IDocTypeTranslation> docTypeTranslations) {
            super();
            this.specificProcessorsByElementName = specificProcessorsByElementName;
            this.specificProcessorsByAttributeName = specificProcessorsByAttributeName;
            this.nonSpecificProcessorsByNodeClass = nonSpecificProcessorsByNodeClass;
            this.executionAttributes = executionAttributes;
            this.docTypeResolutionEntries = docTypeResolutionEntries;
            this.docTypeTranslations = docTypeTranslations;
        }
        
        public Map<String, Set<ProcessorAndContext>> getSpecificProcessorsByElementName() {
            return this.specificProcessorsByElementName;
        }

        public Map<String, Set<ProcessorAndContext>> getSpecificProcessorsByAttributeName() {
            return this.specificProcessorsByAttributeName;
        }

        public Map<Class<? extends Node>, Set<ProcessorAndContext>> getMergedNonSpecificProcessorsByNodeClass() {
            return this.nonSpecificProcessorsByNodeClass;
        }

        public Map<String,Object> getExecutionAttributes() {
            return this.executionAttributes;
        }

        public Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
            return this.docTypeResolutionEntries;
        }

        public Set<IDocTypeTranslation> getDocTypeTranslations() {
            return this.docTypeTranslations;
        }
        
    }
    


    private static class TemplateResolverComparator implements Comparator<ITemplateResolver>, Serializable {

        private static final long serialVersionUID = -4959505530260386645L;

        TemplateResolverComparator() {
            super();
        }

        public int compare(final ITemplateResolver o1, final ITemplateResolver o2) {
            if (o1.getOrder() == null) {
                return -1;
            }
            if (o2.getOrder() == null) {
                return 1;
            }
            return o1.getOrder().compareTo(o2.getOrder());
        }

    }


    private static class MessageResolverComparator implements Comparator<IMessageResolver>, Serializable {

        private static final long serialVersionUID = 4700426328261944024L;

        MessageResolverComparator() {
            super();
        }

        public int compare(final IMessageResolver o1, final IMessageResolver o2) {
            if (o1.getOrder() == null) {
                return -1;
            }
            if (o2.getOrder() == null) {
                return 1;
            }
            return o1.getOrder().compareTo(o2.getOrder());
        }

    }

}
