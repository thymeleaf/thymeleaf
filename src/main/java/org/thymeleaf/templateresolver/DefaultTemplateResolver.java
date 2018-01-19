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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link AbstractTemplateResolver}
 *   and acts as a default template resolver, always returning the same specified text in the form of
 *   a {@link StringTemplateResource} instance.
 * </p>
 * <p>
 *   This template resolver will consider its resolved templates always <strong>cacheable</strong>.
 * </p>
 * <p>
 *   Also, the {@link TemplateMode#HTML} template mode will be used by default.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class DefaultTemplateResolver extends AbstractTemplateResolver {


    /**
     * <p>
     *   Default template mode: {@link TemplateMode#HTML}
     * </p>
     */
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;



    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private String template = "";



    /**
     * <p>
     *   Creates a new instance of this template resolver.
     * </p>
     */
    public DefaultTemplateResolver() {
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
     *   Returns the text that will always be returned by this template resolver as the resolved template.
     * </p>
     *
     * @return the text to be returned as template.
     */
    public String getTemplate() {
        return this.template;
    }


    /**
     * <p>
     *   Set the text that will be returned as the resolved template.
     * </p>
     *
     * @param template the text to be returned as template.
     */
    public void setTemplate(final String template) {
        this.template = template;
    }




    @Override
    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return new StringTemplateResource(this.template);
    }


    @Override
    protected TemplateMode computeTemplateMode(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return this.templateMode;
    }


    @Override
    protected ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        return AlwaysValidCacheEntryValidity.INSTANCE;
    }


}
