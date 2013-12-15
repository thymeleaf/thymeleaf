/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.processor.attr;

import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringInputRadioFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    
    public static final String RADIO_INPUT_TYPE_ATTR_VALUE = "radio";
    


    
    public SpringInputRadioFieldAttrProcessor() {
        super(ATTR_NAME,
              INPUT_TAG_NAME,
              INPUT_TYPE_ATTR_NAME,
              RADIO_INPUT_TYPE_ATTR_VALUE);
    }




    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, element, name, true);
        
        final String value = element.getAttributeValue("value");
        if (value == null) {
            throw new TemplateProcessingException(
                    "Attribute \"value\" is required in \"input(radio)\" tags");
        }
        
        final boolean checked = 
            SelectedValueComparatorWrapper.isSelected(bindStatus, value);
        
        
        element.setAttribute("id", id);
        element.setAttribute("name", name);
        element.setAttribute(
                "value",
                RequestDataValueProcessorUtils.processFormFieldValue(
                        arguments.getConfiguration(), arguments, name, value, "radio"));

        if (checked) {
            element.setAttribute("checked", "checked");
        } else {
            element.removeAttribute("checked");
        }
        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);         
        
    }

    

}
