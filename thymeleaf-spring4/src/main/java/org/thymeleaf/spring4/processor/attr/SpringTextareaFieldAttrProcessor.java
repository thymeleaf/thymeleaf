/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringTextareaFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    

    
    public SpringTextareaFieldAttrProcessor() {
        super(ATTR_NAME,
              TEXTAREA_TAG_NAME);
    }




    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, element, name, false);
        
        final String value = ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);

        final String processedValue =
                RequestDataValueProcessorUtils.processFormFieldValue(
                        arguments.getConfiguration(), arguments, name, value, "textarea");

        element.setAttribute("id", id);
        element.setAttribute("name", name);
        
        final Text text = new Text(processedValue == null? "" : processedValue);

        element.clearChildren();
        element.addChild(text);
        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);         
        
    }

    

}
