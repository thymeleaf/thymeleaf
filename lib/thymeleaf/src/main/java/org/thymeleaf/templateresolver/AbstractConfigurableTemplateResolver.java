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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ContentTypeUtils;
import org.thymeleaf.util.PatternSpec;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Abstract implementation of {@link ITemplateResolver} extending {@link AbstractTemplateResolver}
 *   and providing a large set of methods for configuring resource name (from template name), template mode,
 *   cache validity and character encoding.
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
 * @since 3.0.0
 *
 */
public abstract class AbstractConfigurableTemplateResolver extends AbstractTemplateResolver {

    
    /**
     * <p>
     *   Default template mode: {@link TemplateMode#HTML}
     * </p>
     */
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;

    /**
     * <p>
     *   Default value for the <i>cacheable</i> flag: {@value}.
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
    private boolean forceSuffix = false;
    private String characterEncoding = null;
    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean forceTemplateMode = false;
    private boolean cacheable = DEFAULT_CACHEABLE;
    private Long cacheTTLMs = DEFAULT_CACHE_TTL_MS;

    private final HashMap<String,String> templateAliases = new HashMap<String, String>(8);
    
    private final PatternSpec xmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec htmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec textTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec javaScriptTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec cssTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec rawTemplateModePatternSpec = new PatternSpec();

    private final PatternSpec cacheablePatternSpec = new PatternSpec();
    private final PatternSpec nonCacheablePatternSpec = new PatternSpec();
    




    public AbstractConfigurableTemplateResolver() {
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
    public final void setPrefix(final String prefix) {
        this.prefix = prefix;
    }


    /**
     * <p>
     *   Returns the (optional) suffix to be added to all template names in order
     *   to convert <i>template names</i> into <i>resource names</i>. 
     * </p>
     * <p>
     *   Note that this suffix may not be applied to the template name if the template name
     *   already ends in a known file name suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}. If this behaviour needs to be overridden so
     *   that suffix is always applied, the {@link #setForceSuffix(boolean)} will need to be set.
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
     * <p>
     *   Note that this suffix may not be applied to the template name if the template name
     *   already ends in a known file name suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}. If this behaviour needs to be overridden so
     *   that suffix is always applied, the {@link #setForceSuffix(boolean)} will need to be set.
     * </p>
     *
     * @param suffix the suffix to be set.
     */
    public final void setSuffix(final String suffix) {
        this.suffix = suffix;
    }


    /**
     * <p>
     *   Returns whether the application of the suffix should be forced on the template
     *   name.
     * </p>
     * <p>
     *   When forced, suffix will be appended to the template name even if the template
     *   name ends in a known suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @return whether the suffix will be forced or not.
     * @since 3.0.6
     */
    public final boolean getForceSuffix() {
        return this.forceSuffix;
    }


    /**
     * <p>
     *   Sets whether the application of the suffix should be forced on the template
     *   name.
     * </p>
     * <p>
     *   When forced, suffix will be appended to the template name even if the template
     *   name ends in a known suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @param forceSuffix whether the suffix should be forced or not.
     * @since 3.0.6
     */
    public final void setForceSuffix(final boolean forceSuffix) {
        this.forceSuffix = forceSuffix;
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
    public final void setCharacterEncoding(final String characterEncoding) {
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
     * <p>
     *   Note that this template mode also may not be applied if the template resource name
     *   ends in a known file name suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}. If this behaviour needs to be overridden so
     *   that template name is always applied, the {@link #setForceTemplateMode(boolean)} will need to be set.
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
     * <p>
     *   Note that this template mode also may not be applied if the template resource name
     *   ends in a known file name suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}. If this behaviour needs to be overridden so
     *   that template name is always applied, the {@link #setForceTemplateMode(boolean)} will need to be set.
     * </p>
     *
     * @param templateMode the template mode.
     */
    public final void setTemplateMode(final TemplateMode templateMode) {
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
     * <p>
     *   Note that this template mode also may not be applied if the template resource name
     *   ends in a known file name suffix: {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}. If this behaviour needs to be overridden so
     *   that template name is always applied, the {@link #setForceTemplateMode(boolean)} will need to be set.
     * </p>
     *
     * @param templateMode the template mode.
     */
    public final void setTemplateMode(final String templateMode) {
        // Setter overload actually goes against the JavaBeans spec, but having this one is good for legacy
        // compatibility reasons. Besides, given the getter returns TemplateMode, intelligent frameworks like
        // Spring will recognized the property as TemplateMode-typed and simply ignore this setter.
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        this.templateMode = TemplateMode.parse(templateMode);
    }


    /**
     * <p>
     *   Returns whether the configured template mode should be forced instead of attempting
     *   a <em>smart</em> template mode resolution based on template resource name.
     * </p>
     * <p>
     *   When forced, the configured template mode ({@link #setTemplateMode(TemplateMode)} will
     *   be applied even if the template resource name ends in a known suffix:
     *   {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @return whether the suffix will be forced or not.
     * @since 3.0.6
     */
    public final boolean getForceTemplateMode() {
        return this.forceTemplateMode;
    }


    /**
     * <p>
     *   Sets whether the configured template mode should be forced instead of attempting
     *   a <em>smart</em> template mode resolution based on template resource name.
     * </p>
     * <p>
     *   When forced, the configured template mode ({@link #setTemplateMode(TemplateMode)} will
     *   be applied even if the template resource name ends in a known suffix:
     *   {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @param forceTemplateMode whether the configured template mode should be forced or not.
     * @since 3.0.6
     */
    public final void setForceTemplateMode(final boolean forceTemplateMode) {
        this.forceTemplateMode = forceTemplateMode;
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
    public final void setCacheable(final boolean cacheable) {
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
    public final void setCacheTTLMs(final Long cacheTTLMs) {
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
    public final void setTemplateAliases(final Map<String,String> templateAliases) {
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
    public final void addTemplateAlias(final String alias, final String templateName) {
        Validate.notNull(alias, "Alias cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        this.templateAliases.put(alias, templateName);
    }


    /**
     * <p>
     *   Removes all currently configured template aliases.
     * </p>
     */
    public final void clearTemplateAliases() {
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
     * @param newXmlTemplateModePatterns the new patterns
     */
    public final void setXmlTemplateModePatterns(final Set<String> newXmlTemplateModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newXmlTemplateModePatterns);
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
     * @param newHtmlTemplateModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setHtmlTemplateModePatterns(final Set<String> newHtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtmlTemplateModePatterns);
    }




    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#JAVASCRIPT}
     *   template mode to resolved templates.
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final PatternSpec getJavaScriptTemplateModePatternSpec() {
        return this.javaScriptTemplateModePatternSpec;
    }

    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#JAVASCRIPT}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getJavaScriptTemplateModePatternSpec()}.getPatterns()
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final Set<String> getJavaScriptTemplateModePatterns() {
        return this.javaScriptTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#JAVASCRIPT}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getJavaScriptTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     *
     * @param newJavaScriptTemplateModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setJavaScriptTemplateModePatterns(final Set<String> newJavaScriptTemplateModePatterns) {
        this.javaScriptTemplateModePatternSpec.setPatterns(newJavaScriptTemplateModePatterns);
    }




    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#CSS}
     *   template mode to resolved templates.
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final PatternSpec getCSSTemplateModePatternSpec() {
        return this.cssTemplateModePatternSpec;
    }

    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#CSS}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getCSSTemplateModePatternSpec()}.getPatterns()
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final Set<String> getCSSTemplateModePatterns() {
        return this.cssTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#CSS}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getCSSTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     *
     * @param newCSSTemplateModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setCSSTemplateModePatterns(final Set<String> newCSSTemplateModePatterns) {
        this.cssTemplateModePatternSpec.setPatterns(newCSSTemplateModePatterns);
    }




    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing the {@link TemplateMode#RAW}
     *   template mode to resolved templates.
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final PatternSpec getRawTemplateModePatternSpec() {
        return this.rawTemplateModePatternSpec;
    }

    /**
     * <p>
     *   Returns the <i>patterns</i> specified for establishing the {@link TemplateMode#RAW}
     *   template mode to resolved templates.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getRawTemplateModePatternSpec()}.getPatterns()
     * </p>
     *
     * @return the pattern spec
     * @since 3.0.0
     */
    public final Set<String> getRawTemplateModePatterns() {
        return this.rawTemplateModePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing the {@link TemplateMode#RAW}
     *   template mode as Strings.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getRawTemplateModePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     *
     * @param newRawTemplateModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setRawTemplateModePatterns(final Set<String> newRawTemplateModePatterns) {
        this.rawTemplateModePatternSpec.setPatterns(newRawTemplateModePatterns);
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
     * @param newTextTemplateModePatterns the new patterns
     * @since 3.0.0
     */
    public final void setTextTemplateModePatterns(final Set<String> newTextTemplateModePatterns) {
        this.textTemplateModePatternSpec.setPatterns(newTextTemplateModePatterns);
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
     * @param newValidXmlTemplateModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setValidXmlTemplateModePatterns(final Set<String> newValidXmlTemplateModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newValidXmlTemplateModePatterns);
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
     * @param newXhtmlTemplateModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setXhtmlTemplateModePatterns(final Set<String> newXhtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newXhtmlTemplateModePatterns);
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
     * @param newValidXhtmlTemplateModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setValidXhtmlTemplateModePatterns(final Set<String> newValidXhtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newValidXhtmlTemplateModePatterns);
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
     * @param newLegacyHtml5TemplateModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setLegacyHtml5TemplateModePatterns(final Set<String> newLegacyHtml5TemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newLegacyHtml5TemplateModePatterns);
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
     * @param newHtml5TemplateModePatterns the new patterns
     * @deprecated Deprecated in 3.0.0. Use the methods for the {@link TemplateMode#XML} template mode instead.
     *             Will be removed in 3.1
     */
    @Deprecated
    public final void setHtml5TemplateModePatterns(final Set<String> newHtml5TemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtml5TemplateModePatterns);
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
     *   Computes the resource name that will be used for resolving, from the template name and other
     *   parameters configured at this <em>configurable</em> resolver.
     * </p>
     * <p>
     *   This method can be overridden by subclasses that need to modify the standard way in which the
     *   name of the template resource is computed by default before passing it to the real resource
     *   resolution mechanism (in method {@link #computeTemplateResource(IEngineConfiguration, String, String, String, String, Map)}
     * </p>
     * <p>
     *   By default, the resource name will be created by first applying the <em>template aliases</em>, and then
     *   adding <em>prefix</em> and <em>suffix</em> to the specified <em>template</em> (template name).
     * </p>
     *
     * @param configuration the engine configuration in use.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template (normally the template name, except for String templates).
     * @param prefix the prefix to be applied.
     * @param suffix the suffix to be applied.
     * @param templateAliases the template aliases map.
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     * @return the resource name that should be used for resolving
     * @deprecated in 3.0.6. Use {@link #computeResourceName(IEngineConfiguration, String, String, String, String, boolean, Map, Map)} instead.
     *             Will be removed in Thymeleaf 3.2.
     */
    @Deprecated
    protected String computeResourceName(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template,
            final String prefix, final String suffix, final Map<String, String> templateAliases,
            final Map<String, Object> templateResolutionAttributes) {

        return computeResourceName(
                configuration, ownerTemplate, template, prefix, suffix, false, templateAliases,
                templateResolutionAttributes);

    }






    /**
     * <p>
     *   Computes the resource name that will be used for resolving, from the template name and other
     *   parameters configured at this <em>configurable</em> resolver.
     * </p>
     * <p>
     *   This method can be overridden by subclasses that need to modify the standard way in which the
     *   name of the template resource is computed by default before passing it to the real resource
     *   resolution mechanism (in method {@link #computeTemplateResource(IEngineConfiguration, String, String, String, String, Map)}
     * </p>
     * <p>
     *   By default, the resource name will be created by first applying the <em>template aliases</em>, and then
     *   adding <em>prefix</em> and <em>suffix</em> to the specified <em>template</em> (template name).
     * </p>
     *
     * @param configuration the engine configuration in use.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template (normally the template name, except for String templates).
     * @param prefix the prefix to be applied.
     * @param suffix the suffix to be applied.
     * @param forceSuffix whether the suffix should be forced or not.
     * @param templateAliases the template aliases map.
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     * @return the resource name that should be used for resolving
     * @since 3.0.6
     */
    protected String computeResourceName(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template,
            final String prefix, final String suffix, final boolean forceSuffix,
            final Map<String, String> templateAliases, final Map<String, Object> templateResolutionAttributes) {

        Validate.notNull(template, "Template name cannot be null");

        String unaliasedName = templateAliases.get(template);
        if (unaliasedName == null) {
            unaliasedName = template;
        }

        final boolean hasPrefix = !StringUtils.isEmptyOrWhitespace(prefix);
        final boolean hasSuffix = !StringUtils.isEmptyOrWhitespace(suffix);

        final boolean shouldApplySuffix =
                hasSuffix && (forceSuffix || !ContentTypeUtils.hasRecognizedFileExtension(unaliasedName));

        if (!hasPrefix && !shouldApplySuffix){
            return unaliasedName;
        }

        if (!hasPrefix) { // shouldApplySuffix
            return unaliasedName + suffix;
        }

        if (!shouldApplySuffix) { // hasPrefix
            return prefix + unaliasedName;
        }

        // hasPrefix && shouldApplySuffix
        return prefix + unaliasedName + suffix;

    }



    

    @Override
    protected TemplateMode computeTemplateMode(
            final IEngineConfiguration configuration, final String ownerTemplate,
            final String template, final Map<String, Object> templateResolutionAttributes) {

        if (this.xmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.XML;
        }
        if (this.htmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.HTML;
        }
        if (this.textTemplateModePatternSpec.matches(template)) {
            return TemplateMode.TEXT;
        }
        if (this.javaScriptTemplateModePatternSpec.matches(template)) {
            return TemplateMode.JAVASCRIPT;
        }
        if (this.cssTemplateModePatternSpec.matches(template)) {
            return TemplateMode.CSS;
        }
        if (this.rawTemplateModePatternSpec.matches(template)) {
            return TemplateMode.RAW;
        }

        if (!this.forceTemplateMode) {

            final String templateResourceName =
                    computeResourceName(
                            configuration, ownerTemplate, template,
                            this.prefix, this.suffix, this.forceSuffix, this.templateAliases,
                            templateResolutionAttributes);

            final TemplateMode autoResolvedTemplateMode =
                    ContentTypeUtils.computeTemplateModeForTemplateName(templateResourceName);
            if (autoResolvedTemplateMode != null) {
                return autoResolvedTemplateMode;
            }

        }

        return getTemplateMode();

    }

    
    

    @Override
    protected ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {

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
    protected final ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        final String resourceName =
                computeResourceName(configuration, ownerTemplate, template, this.prefix, this.suffix, this.forceSuffix, this.templateAliases, templateResolutionAttributes);
        return computeTemplateResource(configuration, ownerTemplate, template, resourceName, this.characterEncoding, templateResolutionAttributes);
    }


    /**
     * <p>
     *   Compute the real resource, once the resource name has been computed using prefix, suffix, and other
     *   configured artifacts.
     * </p>
     *
     * @param configuration the engine configuration in use.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template (normally the template name, except for String templates).
     * @param resourceName the resource name, complete with prefix, suffix, aliases, etc.
     * @param characterEncoding the character encoding to be used for reading the resource.
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     * @return the template resource
     */
    protected abstract ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes);
    
    
    
}
