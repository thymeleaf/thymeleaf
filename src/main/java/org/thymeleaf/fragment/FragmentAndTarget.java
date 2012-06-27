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

import org.thymeleaf.Configuration;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class FragmentAndTarget {
    
    private final String templateName;
    private final IFragmentSpec fragmentSpec;
    
    
    public FragmentAndTarget(final String templateName, final IFragmentSpec fragmentSpec) {
        super();
        Validate.notEmpty(templateName, "Template name cannot be null or empty");
        Validate.notNull(fragmentSpec, "Fragment spec cannot be null or empty");
        this.templateName = templateName;
        this.fragmentSpec = fragmentSpec;
    }

    

    public String getTemplateName() {
        return this.templateName;
    }


    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }
    

    
    
    public final List<Node> extractFragment(
            final Configuration configuration, final IContext context, final TemplateRepository templateRepository) {

        final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(configuration, getTemplateName(), context);
        
        final Template parsedFragmentTemplate = 
                templateRepository.getTemplate(fragmentTemplateProcessingParameters);
        
        return this.fragmentSpec.extractFragment(configuration, parsedFragmentTemplate.getDocument().getChildren());
        
    }
    

}

