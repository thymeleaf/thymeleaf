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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.templateparser.ITemplateParser;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringErrorsAttrProcessor 
        extends AbstractAttrProcessor {

    private static final String ERROR_DELIMITER = "<br />";
    
    public static final int ATTR_PRECEDENCE = 1200;
    public static final String ATTR_NAME = "errors";

    

    
    
    public SpringErrorsAttrProcessor() {
        super(ATTR_NAME);
    }


    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    

    @Override
    public ProcessorResult processAttribute(
            final Arguments arguments, final Tag tag, final String attributeName) {
        
        final String attributeValue = tag.getAttributeValue(attributeName);
        
        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments, attributeValue, true);
        
        if (bindStatus.isError()) {
            
            final Map<String,Object> localVariables = new HashMap<String,Object>();
            localVariables.put(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);
            
            final StringBuilder strBuilder = new StringBuilder();
            final String[] errorMsgs = bindStatus.getErrorMessages();
            
            for (int i = 0; i < errorMsgs.length; i++) {
                if (i > 0) {
                    strBuilder.append(ERROR_DELIMITER);
                }
                strBuilder.append(
                        ValueFormatterWrapper.getDisplayString(errorMsgs[i], false));
            }
            
            // Remove previous tag children
            tag.clearChildren();
            
            
            final Configuration configuration = arguments.getConfiguration();
            
            final ITemplateParser templateParser =
                    configuration.getTemplateModeHandler(
                            arguments.getTemplateResolution().getTemplateMode()).getTemplateParser();
            
            // Use the parser to obtain a DOM from the String
            final List<Node> fragNodes = templateParser.parseFragment(configuration, strBuilder.toString());

            for (final Node child : fragNodes) {
                child.setSkippable(true);
                tag.addChild(child);
            }
            
            tag.removeAttribute(attributeName);
            
            return ProcessorResult.setLocalVariables(localVariables);
            
        }
        
        tag.getParent().removeChild(tag);
        
        return ProcessorResult.OK;
        
    }

        
    

}
