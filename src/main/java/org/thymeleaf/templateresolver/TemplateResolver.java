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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TTLCacheEntryValidity;
import org.thymeleaf.context.IContext;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.PatternSpec;
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
 *   <li>If not cacheable: {@link NonCacheableCacheEntryValidity}.</li>
 *   <li>If cacheable and TTL not set: {@link AlwaysValidCacheEntryValidity}.</li>
 *   <li>If cacheable and TTL set: {@link TTLCacheEntryValidity}.</li>
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
     *   Default template mode: {@link TemplateMode#HTML}
     * </p>
     */
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;

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
    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean cacheable = DEFAULT_CACHEABLE;
    private Long cacheTTLMs = null;
    private IResourceResolver resourceResolver = null;
    
    private final HashMap<String,String> templateAliases = new HashMap<String, String>(8);
    
    private final PatternSpec xmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec htmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec textTemplateModePatternSpec = new PatternSpec();
    
    private final PatternSpec cacheablePatternSpec = new PatternSpec();
    private final PatternSpec nonCacheablePatternSpec = new PatternSpec();
    
    
                   
    public TemplateResolver() {
        super();
    }



    
    
    /**
     * <p>
     *   Returns the (optional) prefix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>. 
     * </p>
     * 
     * @return the prefix.
     */
    public final String getPrefix() {
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
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    /**
     * <p>
     *   Sets the template mode to be applied to templates resolved by this resolver.
     * </p>
     * <p>
     *   If <i>template mode patterns</i> (see {@link #setXhtmlTemplateModePatterns(Set)}, 
     *   {@link #setHtml5TemplateModePatterns(Set)}, etc.) are also set, they have higher
     *   priority than the template mode set here (this would act as a <i>default</i>).
     * </p>
     *
     * @param templateMode the template mode.
     */
    public void setTemplateMode(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        // We re-parse the specified template mode so that we make sure we get rid of deprecated values
        this.templateMode = TemplateMode.parse(templateMode.toString());
    }


    /**
     * <p>
     *   Sets the template mode to be applied to templates resolved by this resolver.
     * </p>
     * <p>
     *   Allowed templates modes are defined by the {@link TemplateMode} class.
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
        // Setter overload actually goes against the JavaBeans spec, but having this one is good for legacy
        // compatibility reasons. Besides, given the getter returns TemplateMode, intelligent frameworks like
        // Spring will recognized the property as TemplateMode-typed and simply ignore this setter.
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        this.templateMode = TemplateMode.parse(templateMode);
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
        this.templateAliases.clear();
    }



    

    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#XML}
     *   template mode to resolved templates.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getXmlTemplateModePatternSpec() {
        return this.xmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#XML}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getXmlTemplateModePatterns() {
        return this.xmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#XML}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newXmlTemplatesModePatterns the new patterns
     */
    public final void setXmlTemplateModePatterns(final Set<String> newXmlTemplatesModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newXmlTemplatesModePatterns);
    }




    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#HTML}
     *   template mode to resolved templates.
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final PatternSpec getHtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#HTML}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final Set<String> getHtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#HTML}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     *
     * @param newHtmlTemplatesModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setHtmlTemplateModePatterns(final Set<String> newHtmlTemplatesModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtmlTemplatesModePatterns);
    }




    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#TEXT}
     *   template mode to resolved templates.
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final PatternSpec getTextTemplateModePatternSpec() {
        return this.textTemplateModePatternSpec;
    }

    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#TEXT}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getTextTemplateModePatternSpec()}.getPatterns()
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final Set<String> getTextTemplateModePatterns() {
        return this.textTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#TEXT}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getTextTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     *
     * @param newTextTemplatesModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setTextTemplateModePatterns(final Set<String> newTextTemplatesModePatterns) {
        this.textTemplateModePatternSpec.setPatterns(newTextTemplatesModePatterns);
    }



    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the <strong>deprecated</strong> VALIDXML (validated XML)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#XML} template mode instead.
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final PatternSpec getValidXmlTemplateModePatternSpec() {
        return this.xmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the <strong>deprecated</strong> VALIDXML (validated XML)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#XML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final Set<String> getValidXmlTemplateModePatterns() {
        return this.xmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the <strong>deprecated</strong> VALIDXML (validated XML)
     *   template mode as Strings. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#XML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newValidXmlTemplatesModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setValidXmlTemplateModePatterns(final Set<String> newValidXmlTemplatesModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newValidXmlTemplatesModePatterns);
    }
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the <strong>deprecated</strong> XHTML
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final PatternSpec getXhtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the <strong>deprecated</strong> XHTML
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXhtmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final Set<String> getXhtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the <strong>deprecated</strong> XHTML
     *   template mode as Strings. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getXhtmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newXhtmlTemplatesModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setXhtmlTemplateModePatterns(final Set<String> newXhtmlTemplatesModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newXhtmlTemplatesModePatterns);
    }
    
    
    

    
    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the <strong>deprecated</strong> VALIDXHTML (validated XHTML)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final PatternSpec getValidXhtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the <strong>deprecated</strong> VALIDXHTML (validated XHTML)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXhtmlTemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final Set<String> getValidXhtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the <strong>deprecated</strong> VALIDXHTML (validated XHTML)
     *   template mode as Strings. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getValidXhtmlTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newValidXhtmlTemplatesModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setValidXhtmlTemplateModePatterns(final Set<String> newValidXhtmlTemplatesModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newValidXhtmlTemplatesModePatterns);
    }
    
    
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the <strong>deprecated</strong> LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final PatternSpec getLegacyHtml5TemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the <strong>deprecated</strong> LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getLegacyHtml5TemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final Set<String> getLegacyHtml5TemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the <strong>deprecated</strong> LEGACYHTML5 (non-XML-formed HTML5 that needs HTML-to-XML conversion)
     *   template mode as Strings. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getLegacyHtml5TemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newLegacyHtml5TemplatesModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setLegacyHtml5TemplateModePatterns(final Set<String> newLegacyHtml5TemplatesModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newLegacyHtml5TemplatesModePatterns);
    }
    
    
    

    
    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the <strong>deprecated</strong> HTML5 (correct, XML-formed HTML5)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final PatternSpec getHtml5TemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the <strong>deprecated</strong> HTML5 (correct, XML-formed HTML5)
     *   template mode to resolved templates. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtml5TemplateModePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final Set<String> getHtml5TemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the <strong>deprecated</strong> HTML5 (correct, XML-formed HTML5)
     *   template mode as Strings. Note that, due to the deprecation of this template mode, these patterns
     *   will be applied to the {@link TemplateMode#HTML} template mode instead.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getHtml5TemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param newHtml5TemplatesModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setHtml5TemplateModePatterns(final Set<String> newHtml5TemplatesModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtml5TemplatesModePatterns);
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
        this.resourceResolver = resourceResolver;
    }
    
    
    
    
    
    
    @Override
    protected String computeResourceName(
            final IEngineConfiguration configuration, final IContext context, final String template) {

        Validate.notNull(template, "Template name cannot be null");
        
        String unaliasedName = this.templateAliases.get(template);
        if (unaliasedName == null) {
            unaliasedName = template;
        }

        final boolean hasPrefix = !StringUtils.isEmptyOrWhitespace(this.prefix);
        final boolean hasSuffix = !StringUtils.isEmptyOrWhitespace(this.suffix);

        if (!hasPrefix && !hasSuffix){
            return unaliasedName;
        }

        if (!hasPrefix) { // hasSuffix
            return configuration.getTextRepository().getText(unaliasedName, this.suffix);
        }

        if (!hasSuffix) { // hasPrefix
            return configuration.getTextRepository().getText(this.prefix, unaliasedName);
        }

        // hasPrefix && hasSuffix
        return configuration.getTextRepository().getText(this.prefix, unaliasedName, this.suffix);

    }
    
    
    
    
    

    @Override
    protected TemplateMode computeTemplateMode(
            final IEngineConfiguration configuration, final IContext context, final String template) {
    
        if (this.xmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.XML;
        }
        if (this.htmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.HTML;
        }
        if (this.textTemplateModePatternSpec.matches(template)) {
            return TemplateMode.TEXT;
        }
        return getTemplateMode();
    }
    
    
    

    @Override
    protected ICacheEntryValidity computeValidity(
            final IEngineConfiguration configuration, final IContext context, final String template) {

        if (this.cacheablePatternSpec.matches(template)) {
            if (this.cacheTTLMs != null) {
                return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidCacheEntryValidity.INSTANCE;
        }
        if (this.nonCacheablePatternSpec.matches(template)) {
            return NonCacheableCacheEntryValidity.INSTANCE;
        }
        
        if (isCacheable()) {
            if (this.cacheTTLMs != null) {
                return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidCacheEntryValidity.INSTANCE;
        }
        return NonCacheableCacheEntryValidity.INSTANCE;
        
    }


    
    
    @Override
    protected IResourceResolver computeResourceResolver(
            final IEngineConfiguration configuration, final IContext context, final String template) {
        return this.resourceResolver;
    }

    

    @Override
    protected String computeCharacterEncoding(
            final IEngineConfiguration configuration, final IContext context, final String template) {
        return this.characterEncoding;
    }
    
    
    
}
