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
package org.thymeleaf.templateparser.markup.decoupled;

import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * <p>
 *   Default implementation of the {@link IDecoupledTemplateLogicResolver} interface.
 * </p>
 * <p>
 *   This class computes a {@link ITemplateResource} for the decoupled template logic by resolving a resource considered
 *   <em>relative</em> to the template resource (see {@link ITemplateResource#relative(String)}).
 * </p>
 * <p>
 *   By default, the relative location resolved will be formed as
 *   {@code resource.getBaseName() + DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX} (see
 *   {@link ITemplateResource#getBaseName()} and {@link #DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX}.
 *   So for a template resource {@code /WEB-INF/templates/main.html}, the {@code main.th.xml} relative
 *   location will be used to call {@link ITemplateResource#relative(String)}.
 * </p>
 * <p>
 *   However this can be modified by specifying different {@code prefix} and {@code suffix} values so that, if a
 *   {@code prefix} with value {@code "../logic/"} is specified, for a template resource
 *   {@code /WEB-INF/templates/main.html}, the {@code ../viewlogic/main.th.xml} relative path will be resolved, normally
 *   resulting in the {@code /WEB-INF/viewlogic/main.th.xml} resource.
 * </p>
 * <p>
 *   This class is <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardDecoupledTemplateLogicResolver implements IDecoupledTemplateLogicResolver {

    /**
     * <p>
     *   Default suffix applied to the relative resources resolved: {@value}
     * </p>
     */
    public static final String DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX = ".th.xml";


    private String prefix = null;
    private String suffix = DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX;




    public StandardDecoupledTemplateLogicResolver() {
        super();
    }




    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }




    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }




    public ITemplateResource resolveDecoupledTemplateLogic(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final ITemplateResource resource, final TemplateMode templateMode) {

        String relativeLocation = resource.getBaseName();
        if (this.prefix != null) {
            relativeLocation = this.prefix + relativeLocation;
        }
        if (this.suffix != null) {
            relativeLocation = relativeLocation + this.suffix;
        }

        return resource.relative(relativeLocation);

    }


}
