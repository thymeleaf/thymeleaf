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
package org.thymeleaf.spring4.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.util.FieldUtils;
import org.unbescape.html.HtmlEscape;

/**
 * Works in a similar way to <b>#fields.errors()</b>, but lists all errors for
 * the given field name, separated by a &lt;br/&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class SpringErrorsTagProcessor
        extends AbstractAttrProcessor {

    private static final String ERROR_DELIMITER = "<br />";
    
    public static final int ATTR_PRECEDENCE = 1200;
    public static final String ATTR_NAME = "errors";

    

    
    
    public SpringErrorsTagProcessor() {
        super(ATTR_NAME);
    }


    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    

    @Override
    public ProcessorResult processAttribute(
            final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments.getConfiguration(), arguments, attributeValue);
        
        if (bindStatus.isError()) {
            
            final Map<String,Object> localVariables = new HashMap<String,Object>(2, 1.0f);
            localVariables.put(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);
            
            final StringBuilder strBuilder = new StringBuilder();
            final String[] errorMsgs = bindStatus.getErrorMessages();
            
            for (int i = 0; i < errorMsgs.length; i++) {
                if (i > 0) {
                    strBuilder.append(ERROR_DELIMITER);
                }
                final String displayString = 
                        ValueFormatterWrapper.getDisplayString(errorMsgs[i], false);
                strBuilder.append(HtmlEscape.escapeHtml4Xml(displayString));
            }
            
            // Remove previous element children
            element.clearChildren();

            final Macro errorsNode = new Macro(strBuilder.toString());
            errorsNode.setProcessable(false);

            element.addChild(errorsNode);

            element.removeAttribute(attributeName);
            
            return ProcessorResult.setLocalVariables(localVariables);

        }
        
        element.getParent().removeChild(element);
        
        return ProcessorResult.OK;
        
    }

        
    

}
