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
package org.thymeleaf.spring3.processor.attr;

import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.applicability.IApplicabilityFilter;
import org.thymeleaf.processor.applicability.TagNameApplicabilityFilter;
import org.thymeleaf.processor.attr.AttrProcessResult;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringOptionFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    

    public SpringOptionFieldAttrProcessor() {
        super();
    }

    


    @Override
    protected IApplicabilityFilter getApplicabilityFilter() {
        return new TagNameApplicabilityFilter(OPTION_TAG_NAME);
    }




    @Override
    protected AttrProcessResult doProcess(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {

        final String value = element.getAttribute("value");
        if (value == null) {
            throw new AttrProcessorException(
                    "Attribute \"value\" is required in \"input(radio)\" tags");
        }
        
        final boolean selected = 
            SelectedValueComparatorWrapper.isSelected(bindStatus, value);

        element.setAttribute("value", value);
        if (selected) {
            element.setAttribute("selected", "selected");
        } else {
            element.removeAttribute("selected");
        }
        
        
        return AttrProcessResult.forRemoveAttribute(localVariables);
        
    }

    

}
