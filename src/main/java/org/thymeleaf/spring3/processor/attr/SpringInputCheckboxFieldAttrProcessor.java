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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.SubstitutionTag;
import org.thymeleaf.processor.applicability.AndApplicabilityFilter;
import org.thymeleaf.processor.applicability.AttrValueApplicabilityFilter;
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
public final class SpringInputCheckboxFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    
    public static final String CHECKBOX_INPUT_TYPE_ATTR_VALUE = "checkbox";
    

    public SpringInputCheckboxFieldAttrProcessor() {
        super();
    }

    


    @Override
    protected IApplicabilityFilter getApplicabilityFilter() {
        final IApplicabilityFilter tagApplicabilityFilter = new TagNameApplicabilityFilter(INPUT_TAG_NAME);
        final IApplicabilityFilter typeApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, CHECKBOX_INPUT_TYPE_ATTR_VALUE); 
        return new AndApplicabilityFilter(tagApplicabilityFilter, typeApplicabilityFilter);
    }




    @Override
    protected AttrProcessResult doProcess(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, templateResolution, element, name, true);
        
        String value = null;
        boolean checked = false;

        Object boundValue = bindStatus.getValue();
        final Class<?> valueType = bindStatus.getValueType();


        if (Boolean.class.equals(valueType) || boolean.class.equals(valueType)) {
            
            if (boundValue instanceof String) {
                boundValue = Boolean.valueOf((String) boundValue);
            }
            Boolean booleanValue = (boundValue != null ? (Boolean) boundValue : Boolean.FALSE);
            value = "true";
            checked = booleanValue.booleanValue();
            
        } else {
            
            value = element.getAttribute("value");
            if (value == null) {
                throw new AttrProcessorException(
                        "Attribute \"value\" is required in \"input(checkbox)\" tags " +
                        "when binding to non-boolean values");
            }
            
            checked = SelectedValueComparatorWrapper.isSelected(bindStatus, value);
            
        }
        
        final Element inputElement = (Element) element.cloneNode(true);
        inputElement.removeAttribute(attribute.getName());
        
        inputElement.setAttribute("id", id);
        inputElement.setAttribute("name", name);
        inputElement.setAttribute("value", value);
        if (checked) {
            inputElement.setAttribute("checked", "checked");
        } else {
            inputElement.removeAttribute("checked");
        }
        
        final Element hiddenElement = document.createElement("input");
        hiddenElement.setAttribute("type", "hidden");
        hiddenElement.setAttribute("name", WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name);
        hiddenElement.setAttribute("value", "on");
        
        
        final SubstitutionTag inputTag = SubstitutionTag.forNodeAndLocalVariables(inputElement, localVariables);
        final SubstitutionTag hiddenTag = SubstitutionTag.forNodeAndLocalVariables(hiddenElement, localVariables);
        
        final List<SubstitutionTag> substitutionTags = new ArrayList<SubstitutionTag>();
        substitutionTags.add(inputTag);
        substitutionTags.add(hiddenTag);
        
        return AttrProcessResult.forSubstituteTag(substitutionTags);         
        
    }

    

}
