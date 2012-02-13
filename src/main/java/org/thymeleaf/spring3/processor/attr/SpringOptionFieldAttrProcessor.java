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
package org.thymeleaf.spring3.processor.attr;

import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.ProcessorResult;



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
        super(ATTR_NAME,
              OPTION_TAG_NAME);
    }




    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {

        final String value = element.getAttributeValue("value");
        if (value == null) {
            throw new TemplateProcessingException(
                    "Attribute \"value\" is required in \"option\" tags");
        }
        
        final boolean selected = 
            SelectedValueComparatorWrapper.isSelected(bindStatus, value);

        element.setAttribute("value", value);
        if (selected) {
            element.setAttribute("selected", "selected");
        } else {
            element.removeAttribute("selected");
        }
        element.removeAttribute(attributeName);
        
        
        return ProcessorResult.setLocalVariables(localVariables);
        
    }

    

}
