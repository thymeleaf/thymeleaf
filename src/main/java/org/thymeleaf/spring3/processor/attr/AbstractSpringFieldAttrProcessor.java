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
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.AttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractSpringFieldAttrProcessor 
        extends AbstractAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 1200;
    public static final String ATTR_NAME = "field";
    
    
    protected static final String INPUT_TAG_NAME = "input";
    protected static final String SELECT_TAG_NAME = "select";
    protected static final String OPTION_TAG_NAME = "option";
    protected static final String TEXTAREA_TAG_NAME = "textarea";
    
    protected static final String INPUT_TYPE_ATTR_NAME = "type";

    
    

    
    public AbstractSpringFieldAttrProcessor(
            final String attributeName, final String hostTagName) {
        this(attributeName, hostTagName, null);
    }
    
    public AbstractSpringFieldAttrProcessor(
            final String attributeName, final String hostTagName, final String hostFilterAttributeName, final String hostFilterAttributeValue) {
        super(new AttributeNameProcessorMatcher(attributeName, hostTagName, hostFilterAttributeName, hostFilterAttributeValue));
    }
    
    public AbstractSpringFieldAttrProcessor(
            final String attributeName, final String hostTagName, final Map<String,String> hostTagAttributes) {
        super(new AttributeNameProcessorMatcher(attributeName, hostTagName, hostTagAttributes));
    }

    

    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    


    @Override
    public final ProcessorResult processAttribute(
            final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments, attributeValue, false);
        
        final Map<String,Object> localVariables = new HashMap<String,Object>();
        localVariables.put(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);
        
        return doProcess(arguments, element, attributeName, attributeValue, bindStatus, localVariables);
        
    }
    
    

    protected abstract ProcessorResult doProcess(
            final Arguments arguments, final Element element, final String attributeName, 
            final String attributeValue, final BindStatus bindStatus, 
            final Map<String,Object> localVariables);
    

    
    
    
    // This method is designed to be overridable
    protected String computeId(
            final Arguments arguments, final Element element, final String name, final boolean sequence) {
        
        String id = element.getAttributeValue("id");
        if (id != null && !id.trim().equals("")) {
            String idString = id.toString();
            return (StringUtils.hasText(idString) ? idString : null);
        }

        id = FieldUtils.idFromName(name);
        if (sequence) {
            final Integer count = arguments.getAndIncrementIDSeq(id);
            return id + count.toString();
        }
        return id;
        
    }
    

    

}
