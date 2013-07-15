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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.DialectAwareProcessingContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;




/**
 * <p>
 *   Container class for a pair of <i>template name</i> + <i>fragment spec</i>,
 *   capable of executing the fragment spec on the specified template, after
 *   obtaining the template from {@link TemplateRepository}.
 * </p>
 * <p>
 *   Note that the specified template can be null, in which case the fragment will
 *   be considered to be executed on the current template (obtained from the
 *   IProcessingContext argument in
 *   {@link #extractFragment(Configuration, IProcessingContext, TemplateRepository)},
 *   which will therefore need to be an instance of
 *   {@link org.thymeleaf.Arguments}).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class FragmentAndTarget {
    
    private final String templateName;
    private final IFragmentSpec fragmentSpec;
    

    /**
     * <p>
     *   Create a new instance of this class.
     * </p>
     * 
     * @param templateName the name of the template that will be resolved and parsed, null if
     *                     fragment is to be executed on the current template.
     * @param fragmentSpec the fragment spec that will be applied to the template, once parsed.
     */
    public FragmentAndTarget(final String templateName, final IFragmentSpec fragmentSpec) {
        super();
        // templateName can be null if target template is the current one
        Validate.notNull(fragmentSpec, "Fragment spec cannot be null or empty");
        this.templateName = templateName;
        this.fragmentSpec = fragmentSpec;
    }

    

    /**
     * <p>
     *   Returns the name of the template that will be resolved and parsed.
     * </p>
     * 
     * @return the template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }


    
    /**
     * <p>
     *   Returns the {@link IFragmentSpec} that will be applied to the template.
     * </p>
     * 
     * @return the fragment spec.
     */
    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }
    

    
    /**
     * <p>
     *   Read the specified template from {@link TemplateRepository}, and then apply
     *   the {@link IFragmentSpec} to the result of parsing it (the template).
     * </p>
     * <p>
     *   If an {@link IProcessingContext} instance is available, using 
     *   {@link #extractFragment(Configuration, IProcessingContext, TemplateRepository)}
     *   should be preferred to using this method. If this one is used, the
     *   specified {@link IContext} object will be converted into a 
     *   {@link DialectAwareProcessingContext} instance.
     * </p>
     * 
     * @param configuration the configuration to be used for resolving the template and
     *        processing the fragment spec.
     * @param context the context to be used for resolving and parsing the template (
     *        after being converted to an {@link IProcessingContext} implementation.
     * @param templateRepository the template repository to be asked for the template.
     * @return the result of parsing + applying the fragment spec.
     * @deprecated Should use {@link #extractFragment(Configuration, IProcessingContext, TemplateRepository)}
     *             instead. Will be removed in 2.1.5
     */
    @Deprecated
    public List<Node> extractFragment(
            final Configuration configuration, final IContext context, final TemplateRepository templateRepository) {
        return extractFragment(
                configuration, 
                new DialectAwareProcessingContext(context, configuration.getDialectSet()), 
                templateRepository);
    }


    
    /**
     * <p>
     *   Read the specified template from {@link TemplateRepository}, and then apply
     *   the {@link IFragmentSpec} to the result of parsing it (the template).
     * </p>
     * <p>
     *   In order to execute on the current template (templateName == null), the <tt>context</tt>
     *   argument will need to be an {@link org.thymeleaf.Arguments} object.
     * </p>
     *
     * @param configuration the configuration to be used for resolving the template and
     *        processing the fragment spec.
     * @param context the processing context to be used for resolving and parsing the template.
     * @param templateRepository the template repository to be asked for the template.
     * @return the result of parsing + applying the fragment spec.
     */
    public List<Node> extractFragment(
            final Configuration configuration, final IProcessingContext context, final TemplateRepository templateRepository) {

        String targetTemplateName = getTemplateName();
        if (targetTemplateName == null) {
            if (context != null && context instanceof Arguments) {
                targetTemplateName = ((Arguments)context).getTemplateName();
            } else {
                throw new TemplateProcessingException(
                        "In order to extract fragment from current template (templateName == null), processing context " +
                        "must be a non-null instance of the Arguments class (but is: " +
                        (context == null? null : context.getClass().getName()) + ")");
            }
        }

        final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(configuration, targetTemplateName, context);
        
        final Template parsedFragmentTemplate = 
                templateRepository.getTemplate(fragmentTemplateProcessingParameters);
        
        return this.fragmentSpec.extractFragment(configuration, parsedFragmentTemplate.getDocument().getChildren());
        
    }
    

}

