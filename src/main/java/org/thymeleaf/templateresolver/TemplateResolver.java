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
package org.thymeleaf.templateresolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.PatternSpec;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Basic implementation of {@link ITemplateResolver}.
 * </p>
 * <p>
 *   This class can be used both directly and as a parent class for other Template
 *   Resolver implementations.
 * </p>
 * <p>
 *   Unless overriden, this class will always apply the following validity to 
 *   template resolutions:
 * </p>
 * <ul>
 *   <li>If not cacheable: {@link NonCacheableTemplateResolutionValidity}.</li>
 *   <li>If cacheable and TTL not set: {@link AlwaysValidTemplateResolutionValidity}.</li>
 *   <li>If cacheable and TTL set: {@link TTLTemplateResolutionValidity}.</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class TemplateResolver 
        extends AbstractTemplateResolver {

    
    /**
     * <p>
     *   Default template mode: XHTML
     * </p>
     */
    public static final String DEFAULT_TEMPLATE_MODE = 
            StandardTemplateModeHandlers.XHTML.getTemplateModeName();
    
    /**
     * <p>
     *   Default value for the <i>cacheable</i> flag: true.
     * </p>
     */
    public static final boolean DEFAULT_CACHEABLE = true;
    
    /**
     * <p>
     *   Default value for the cache TTL: null. This means the parsed template will live in
     *   cache until removed by LRU (because of being the oldest entry).
     * </p>
     */
    public static final Long DEFAULT_CACHE_TTL_MS = null;

    
    private String prefix = null;
    private String suffix = null;
    private String characterEncoding = null;
    private String templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean cacheable = DEFAULT_CACHEABLE;
    private Long cacheTTLMs = null;
    private IResourceResolver resourceResolver = null;
    
    private final HashMap<String,String> templateAliases = new HashMap<String, String>(8);
    
    private final PatternSpec xmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec validXmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec xhtmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec validXhtmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec legacyHtml5TemplateModePatternSpec = new PatternSpec();
    private final PatternSpec html5TemplateModePatternSpec = new PatternSpec();
    
    private final PatternSpec cacheablePatternSpec = new PatternSpec();
    private final PatternSpec nonCacheablePatternSpec = new PatternSpec();
    
    
                   
    public TemplateResolver() {
        super();
    }


    /**
     * <p>
     *   Initialize this template resolver.
     * </p>
     * <p>
     *   Once initialized the configuration parameters of this template resolvers
     *   cannot be changed.
     * </p>
     * <p>
     *   Initialization is automatically triggered by the Template Engine before
     *   processing the first template.
     * </p>
     */
    @Override
    protected final synchronized void initializeSpecific() {
        
        if (!isInitialized()) {
            
            /*
             * Checking Resource Resolver
             */
            if (this.resourceResolver == null) {
                throw new ConfigurationException("Cannot initialize template resolver: a resource resolver has not been set");
            }
            
            /*
             *  Initialize pattern specs to avoid further modifications
             */
            this.xmlTemplateModePatternSpec.initialize();
            this.validXmlTemplateModePatternSpec.initialize();
            this.xhtmlTemplateModePatternSpec.initialize();
            this.validXhtmlTemplateModePatternSpec.initialize();
            this.legacyHtml5TemplateModePatternSpec.initialize();
            this.html5TemplateModePatternSpec.initialize();
            this.cacheablePatternSpec.initialize();
            this.nonCacheablePatternSpec.initialize();

            
            initializeSpecificAdditional();
            
        }

    }

    
    /**
     * <p>
     *   Initialize specific aspects of a subclass. This method is called during initialization
     *   of TemplateResolver ({@link #initialize()}) and is meant for being overridden by subclasses. 
     * </p>
     */
    protected void initializeSpecificAdditional() {
        // Nothing to be executed here. Meant for extension
    }
    
    
    
    /**
     * <p>
     *   Returns the (optional) prefix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>. 
     * </p>
     * 
     * @return the prefix.
     */
    public String getPrefix() {
        checkInitialized();
        return this.prefix;
    }
    
    
    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the prefix
     */
    protected final String unsafeGetPrefix() {
        return this.prefix;
    }


    /**
     * <p>
     *   Sets a new (optional) prefix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>.
     * </p>
     * 
     * @param prefix the prefix to be set.
     */
    public void setPrefix(final String prefix) {
        checkNotInitialized();
        this.prefix = prefix;
    }
    

    /**
     * <p>
     *   Returns the (optional) suffix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>. 
     * </p>
     * 
     * @return the suffix.
     */
    public final String getSuffix() {
        checkInitialized();
        return this.suffix;
    }


    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the suffix
     */
    protected final String unsafeGetSuffix() {
        return this.suffix;
    }


    /**
     * <p>
     *   Sets a new (optional) suffix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>.
     * </p>
     * 
     * @param suffix the suffix to be set.
     */
    public void setSuffix(final String suffix) {
        checkNotInitialized();
        this.suffix = suffix;
    }
    

    /**
     * <p>
     *   Returns the character encoding to be used for reading template resources
     *   resolved by this template resolver.
     * </p>
     * 
     * @return the character encoding.
     */
    public final String getCharacterEncoding() {
        checkInitialized();
        return this.characterEncoding;
    }


    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the character encoding
     */
    protected final String unsafeGetCharacterEncoding() {
        return this.characterEncoding;
    }


    /**
     * <p>
     *   Sets a new character encoding for reading template resources.
     * </p>
     * 
     * @param characterEncoding the character encoding to be used.
     */
    public void setCharacterEncoding(final String characterEncoding) {
        checkNotInitialized();
        this.characterEncoding = characterEncoding;
    }
    

    /**
     * <p>
     *   Returns the template mode to be applied to templates resolved by
     *   this template resolver.
     * </p>
     * <p>
     *   If <i>template mode patterns</i> (see {@link #setXhtmlTemplateModePatterns(Set)}, 
     *   {@link #setHtml5TemplateModePatterns(Set)}, etc.) are also set, they have higher
     *   priority than the template mode set here (this would act as a <i>default</i>).
     * </p>
     * 
     * @return the template mode to be used.
     */
    public final String getTemplateMode() {
        checkInitialized();
        return this.templateMode;
    }


    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the template mode
     */
    protected final String unsafeGetTemplateMode() {
        return this.templateMode;
    }


    /**
     * <p>
     *   Sets the template mode to be applied to templates resolved by this resolver.
     * </p>
     * <p>
     *   The set of available template modes is variable, as these can be established
     *   by the user by means of adding {@link org.thymeleaf.templatemode.TemplateModeHandler}
     *   objects to the engine. Nevertheless, there is a <i>standard</i> set of
     *   template modes defined by the {@link StandardTemplateModeHandlers} class.
     * </p>
     * <p>
     *   If <i>template mode patterns</i> (see {@link #setXhtmlTemplateModePatterns(Set)}, 
     *   {@link #setHtml5TemplateModePatterns(Set)}, etc.) are also set, they have higher
     *   priority than the template mode set here (this would act as a <i>default</i>).
     * </p>
     * 
     * @param templateMode the template mode.
     */
    public void setTemplateMode(final String templateMode) {
        checkNotInitialized();
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        this.templateMode = templateMode;
    }



    /**
     * <p>
     *   Returns whether templates resolved by this resolver have to be considered
     *   cacheable or not.
     * </p>
     * <p>
     *   If <i>cacheable patterns</i> (see {@link #setCacheablePatterns(Set)})
     *   are also set, they have higher priority than the value set here (this
     *   would act as a <i>default</i>).
     * </p>
     * 
     * @return whether templates resolved are cacheable or not.
     */
    public final boolean isCacheable() {
        checkInitialized();
        return this.cacheable;
    }


    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return whether templates resolved are cacheable or not. 
     */
    protected final boolean unsafeIsCacheable() {
        return this.cacheable;
    }


    /**
     * <p>
     *   Sets a new value for the <i>cacheable</i> flag.
     * </p>
     * <p>
     *   If <i>cacheable patterns</i> (see {@link #setCacheablePatterns(Set)})
     *   are also set, they have higher priority than the value set here (this
     *   would act as a <i>default</i>).
     * </p>
     * 
     * @param cacheable whether resolved patterns should be considered cacheable or not.
     */
    public void setCacheable(final boolean cacheable) {
        checkNotInitialized();
        this.cacheable = cacheable;
    }
    

    /**
     * <p>
     *   Returns the TTL (Time To Live) in cache of templates resolved by this
     *   resolver. 
     * </p>
     * <p>
     *   If a template is resolved as <i>cacheable</i> but cache TTL is null,
     *   this means the template will live in cache until evicted by LRU
     *   (Least Recently Used) algorithm for being the oldest entry in cache.
     * </p>
     * 
     * @return the cache TTL for resolved templates.
     */
    public final Long getCacheTTLMs() {
        checkInitialized();
        return this.cacheTTLMs;
    }


    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the cache TTl for resolved templates. 
     */
    protected final Long unsafeGetCacheTTLMs() {
        return this.cacheTTLMs;
    }


    /**
     * <p>
     *   Sets a new value for the cache TTL for resolved templates.
     * </p>
     * <p>
     *   If a template is resolved as <i>cacheable</i> but cache TTL is null,
     *   this means the template will live in cache until evicted by LRU
     *   (Least Recently Used) algorithm for being the oldest entry in cache.
     * </p>
     * 
     * @param cacheTTLMs the new cache TTL, or null for using natural LRU eviction.
     */
    public void setCacheTTLMs(final Long cacheTTLMs) {
        checkNotInitialized();
        this.cacheTTLMs = cacheTTLMs;
    }

    
    /**
     * <p>
     *   Returns the currently configured template aliases.
     * </p>
     * <p>
     *   Template aliases allow the use of several (and probably shorter)
     *   names for templates.
     * </p>
     * <p>
     *   Aliases are applied to template names <b>before</b> prefix/suffix.
     * </p>
     * 
     * @return the map of template aliases.
     */
    public final Map<String, String> getTemplateAliases() {
        checkInitialized();
        return Collections.unmodifiableMap(this.templateAliases);
    }


    /**
     * <p>
     *   Sets all the new template aliases to be used.
     * </p>
     * <p>
     *   Template aliases allow the use of several (and probably shorter)
     *   names for templates.
     * </p>
     * <p>
     *   Aliases are applied to template names <b>before</b> prefix/suffix.
     * </p>
     * 
     * @param templateAliases the new template aliases.
     */
    public void setTemplateAliases(final Map<String,String> templateAliases) {
        checkNotInitialized();
        if (templateAliases != null) {
            this.templateAliases.putAll(templateAliases);
        }
    }
    
    
    /**
     * <p>
     *   Adds a new template alias to the currently configured ones.
     * </p>
     * 
     * @param alias the new alias name
     * @param templateName the name of the template the alias will be applied to
     */
    public void addTemplateAlias(final String alias, final String templateName) {
        checkNotInitialized();
        Validate.notNull(alias, "Alias cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        this.templateAliases.put(alias, templateName);
    }


    /**
     * <p>
     *   Removes all currently configured template aliases.
     * </p>
     */
    public void clearTemplateAliases() {
        checkNotInitialized();
        this.templateAliases.clear();
    }



    

    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the XML
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getXmlTemplateModePatternSpec() {
        checkInitialized();
        return this.xmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the XML
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getXmlTemplateModePatterns() {
        checkInitialized();
        return this.xmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the XML
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newXmlTemplatesModePatterns the new patterns
     */
    public final void setXmlTemplateModePatterns(final Set<String> newXmlTemplatesModePatterns) {
        checkNotInitialized();
        this.xmlTemplateModePatternSpec.setPatterns(newXmlTemplatesModePatterns);
    }
    


    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the VALIDXML (validated XML)
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getValidXmlTemplateModePatternSpec() {
        checkInitialized();
        return this.validXmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the VALIDXML (validated XML)
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getValidXmlTemplateModePatterns() {
        checkInitialized();
        return this.validXmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the VALIDXML (validated XML)
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newValidXmlTemplatesModePatterns the new patterns
     */
    public final void setValidXmlTemplateModePatterns(final Set<String> newValidXmlTemplatesModePatterns) {
        checkNotInitialized();
        this.validXmlTemplateModePatternSpec.setPatterns(newValidXmlTemplatesModePatterns);
    }
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the XHTML
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getXhtmlTemplateModePatternSpec() {
        checkInitialized();
        return this.xhtmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the XHTML
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXhtmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getXhtmlTemplateModePatterns() {
        checkInitialized();
        return this.xhtmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the XHTML
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXhtmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newXhtmlTemplatesModePatterns the new patterns
     */
    public final void setXhtmlTemplateModePatterns(final Set<String> newXhtmlTemplatesModePatterns) {
        checkNotInitialized();
        this.xhtmlTemplateModePatternSpec.setPatterns(newXhtmlTemplatesModePatterns);
    }
    
    
    

    
    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the VALIDXHTML (validated XHTML)
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getValidXhtmlTemplateModePatternSpec() {
        checkInitialized();
        return this.validXhtmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the VALIDXHTML (validated XHTML)
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXhtmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getValidXhtmlTemplateModePatterns() {
        checkInitialized();
        return this.validXhtmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the VALIDXHTML (validated XHTML)
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXhtmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newValidXhtmlTemplatesModePatterns the new patterns
     */
    public final void setValidXhtmlTemplateModePatterns(final Set<String> newValidXhtmlTemplatesModePatterns) {
        checkNotInitialized();
        this.validXhtmlTemplateModePatternSpec.setPatterns(newValidXhtmlTemplatesModePatterns);
    }
    
    
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getLegacyHtml5TemplateModePatternSpec() {
        checkInitialized();
        return this.legacyHtml5TemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getLegacyHtml5TemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getLegacyHtml5TemplateModePatterns() {
        checkInitialized();
        return this.legacyHtml5TemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getLegacyHtml5TemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newLegacyHtml5TemplatesModePatterns the new patterns
     */
    public final void setLegacyHtml5TemplateModePatterns(final Set<String> newLegacyHtml5TemplatesModePatterns) {
        checkNotInitialized();
        this.legacyHtml5TemplateModePatternSpec.setPatterns(newLegacyHtml5TemplatesModePatterns);
    }
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the HTML5 (correct, XML-formed HTML5)
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getHtml5TemplateModePatternSpec() {
        checkInitialized();
        return this.html5TemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the HTML5 (correct, XML-formed HTML5)
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtml5TemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getHtml5TemplateModePatterns() {
        checkInitialized();
        return this.html5TemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the HTML5 (correct, XML-formed HTML5)
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtml5TemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newHtml5TemplatesModePatterns the new patterns
     */
    public final void setHtml5TemplateModePatterns(final Set<String> newHtml5TemplatesModePatterns) {
        checkNotInitialized();
        this.html5TemplateModePatternSpec.setPatterns(newHtml5TemplatesModePatterns);
    }
    
    
    
    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing which
     *   templates have to be considered <i>cacheable</i>.
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getCacheablePatternSpec() {
        checkInitialized();
        return this.cacheablePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> (as String) specified for establishing which
     *   templates have to be considered <i>cacheable</i>.
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getCacheablePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the patterns
     */
    public final Set<String> getCacheablePatterns() {
        checkInitialized();
        return this.cacheablePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing which
     *   templates have to be considered <i>cacheable</i>
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getCacheablePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param cacheablePatterns the new patterns
     */
    public final void setCacheablePatterns(final Set<String> cacheablePatterns) {
        checkNotInitialized();
        this.cacheablePatternSpec.setPatterns(cacheablePatterns);
    }
    
    
    
    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing which
     *   templates have to be considered <i>non cacheable</i>.
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getNonCacheablePatternSpec() {
        checkInitialized();
        return this.nonCacheablePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> (as String) specified for establishing which
     *   templates have to be considered <i>non cacheable</i>.
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getNonCacheablePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the patterns
     */
    public final Set<String> getNonCacheablePatterns() {
        checkInitialized();
        return this.nonCacheablePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing which
     *   templates have to be considered <i>non cacheable</i>
     * </p>
     * <p>
     *   These patterns have higher precedence than the <i>cacheable</i>
     *   flag (see {@link #setCacheable(boolean)}). Such flag can be considered
     *   a <i>default value</i> after <i>cacheable patterns</i> and
     *   <i>non-cacheable patterns</i> have been applied. 
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getNonCacheablePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param nonCacheablePatterns the new patterns
     */
    public final void setNonCacheablePatterns(final Set<String> nonCacheablePatterns) {
        checkNotInitialized();
        this.nonCacheablePatternSpec.setPatterns(nonCacheablePatterns);
    }
    
    


    
    
    /**
     * <p>
     *   Returns the Resource Resolver (implementation of {@link IResourceResolver}) that will
     *   be used to resolve the <i>resource names</i> that are assigned to templates resolved
     *   by this template resolver. 
     * </p>
     * 
     * @return the resource resolver
     */
    public IResourceResolver getResourceResolver() {
        checkInitialized();
        return this.resourceResolver;
    }

    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the resource resolver
     */
    protected IResourceResolver unsafeGetResourceResolver() {
        return this.resourceResolver;
    }

    /**
     * <p>
     *   Sets the resource resolver to be included into {@link TemplateResolution} results.
     * </p>
     * 
     * @param resourceResolver the new resource resolver.
     */
    public void setResourceResolver(final IResourceResolver resourceResolver) {
        checkNotInitialized();
        this.resourceResolver = resourceResolver;
    }
    
    
    
    
    
    
    @Override
    protected String computeResourceName(final TemplateProcessingParameters templateProcessingParameters) {

        checkInitialized();

        final String templateName = templateProcessingParameters.getTemplateName();
        
        Validate.notNull(templateName, "Template name cannot be null");
        
        String unaliasedName = this.templateAliases.get(templateName);
        if (unaliasedName == null) {
            unaliasedName = templateName;
        }
        
        final StringBuilder resourceName = new StringBuilder();
        if (!StringUtils.isEmptyOrWhitespace(this.getPrefix())) {
            resourceName.append(this.getPrefix());
        }
        resourceName.append(unaliasedName);
        if (!StringUtils.isEmptyOrWhitespace(this.suffix)) {
            resourceName.append(this.suffix);
        }
        
        return resourceName.toString();
        
    }
    
    
    
    
    

    @Override
    protected String computeTemplateMode(final TemplateProcessingParameters templateProcessingParameters) {
    
        final String templateName = templateProcessingParameters.getTemplateName();
        
        if (this.xmlTemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.XML.getTemplateModeName();
        }
        if (this.validXmlTemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.VALIDXML.getTemplateModeName();
        }
        if (this.xhtmlTemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.XHTML.getTemplateModeName();
        }
        if (this.validXhtmlTemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.VALIDXHTML.getTemplateModeName();
        }
        if (this.legacyHtml5TemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.LEGACYHTML5.getTemplateModeName();
        }
        if (this.html5TemplateModePatternSpec.matches(templateName)) {
            return StandardTemplateModeHandlers.HTML5.getTemplateModeName();
        }
        return unsafeGetTemplateMode();
    }
    
    
    

    @Override
    protected ITemplateResolutionValidity computeValidity(final TemplateProcessingParameters templateProcessingParameters) {
    
        final String templateName = templateProcessingParameters.getTemplateName();
        
        if (this.cacheablePatternSpec.matches(templateName)) {
            if (this.cacheTTLMs != null) {
                return new TTLTemplateResolutionValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidTemplateResolutionValidity.INSTANCE;
        }
        if (this.nonCacheablePatternSpec.matches(templateName)) {
            return NonCacheableTemplateResolutionValidity.INSTANCE;
        }
        
        if (unsafeIsCacheable()) {
            if (this.cacheTTLMs != null) {
                return new TTLTemplateResolutionValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidTemplateResolutionValidity.INSTANCE;
        }
        return NonCacheableTemplateResolutionValidity.INSTANCE;
        
    }


    
    
    @Override
    protected IResourceResolver computeResourceResolver(final TemplateProcessingParameters templateProcessingParameters) {
        return this.resourceResolver;
    }

    

    @Override
    protected String computeCharacterEncoding(final TemplateProcessingParameters templateProcessingParameters) {
        return this.characterEncoding;
    }
    
    
    
}
