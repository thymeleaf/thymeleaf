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

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TTLCacheEntryValidity;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractTemplateResolver}
 *   and creates {@link StringTemplateResource} instances for template resources.
 * </p>
 * <p>
 *   This template resolvers will consider the {@code template} being resolved as the template itself,
 *   this is, its contents. No external file or resource will be therefore accessed.
 * </p>
 * <p>
 *   This template resolver will consider its resolved templates <strong>non-cacheable</strong> by default,
 *   given its nature of being used for resolving arbitrary {@code String} objects.
 * </p>
 * <p>
 *   Also, the {@link TemplateMode#HTML} template mode will be used by default.
 * </p>
 * <p>
 *   Note <strong>this is the default template resolver</strong> that {@link org.thymeleaf.TemplateEngine}
 *   instances will use if no other resolvers are configured.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StringTemplateResolver extends AbstractTemplateResolver {


    /**
     * <p>
     *   Default template mode: {@link TemplateMode#HTML}
     * </p>
     */
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;

    /**
     * <p>
     *   Default value for the <i>cacheable</i> flag: {@value}
     * </p>
     */
    public static final boolean DEFAULT_CACHEABLE = false;

    /**
     * <p>
     *   Default value for the cache TTL: null. This means the parsed template will live in
     *   cache until removed by LRU (because of being the oldest entry).
     * </p>
     */
    public static final Long DEFAULT_CACHE_TTL_MS = null;



    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean cacheable = DEFAULT_CACHEABLE;
    private Long cacheTTLMs = DEFAULT_CACHE_TTL_MS;



    /**
     * <p>
     *   Creates a new instance of this template resolver.
     * </p>
     */
    public StringTemplateResolver() {
        super();
    }






    /**
     * <p>
     *   Returns the template mode to be applied to templates resolved by
     *   this template resolver.
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
     *   Returns whether templates resolved by this resolver have to be considered
     *   cacheable or not.
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



    @Override
    public void setUseDecoupledLogic(final boolean useDecoupledLogic) {
        if (useDecoupledLogic) {
            throw new ConfigurationException("The 'useDecoupledLogic' flag is not allowed for String template resolution");
        }
        super.setUseDecoupledLogic(useDecoupledLogic);
    }




    @Override
    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return new StringTemplateResource(template);
    }


    @Override
    protected TemplateMode computeTemplateMode(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return this.templateMode;
    }


    @Override
    protected ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {

        if (isCacheable()) {
            if (this.cacheTTLMs != null) {
                return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidCacheEntryValidity.INSTANCE;
        }
        return NonCacheableCacheEntryValidity.INSTANCE;

    }


}
