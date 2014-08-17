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
package org.thymeleaf.spring3.processor.attr;

import java.util.Collections;
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
 * Binds an input property with the value in the form's backing bean.
 * <p>
 * Values for <tt>th:field</tt> attributes must be selection expressions
 * <tt>(*{...})</tt>, as they will be evaluated on the form backing bean and not
 * on the context variables (model attributes in Spring MVC jargon).
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
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





    protected AbstractSpringFieldAttrProcessor(
            final String attributeName, final String hostTagName) {
        this(attributeName, hostTagName, null);
    }

    protected AbstractSpringFieldAttrProcessor(
            final String attributeName, final String hostTagName, final String hostFilterAttributeName, final String hostFilterAttributeValue) {
        super(new AttributeNameProcessorMatcher(attributeName, hostTagName, hostFilterAttributeName, hostFilterAttributeValue));
    }

    protected AbstractSpringFieldAttrProcessor(
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
            FieldUtils.getBindStatus(arguments.getConfiguration(), arguments, attributeValue);
        
        final Map<String,Object> localVariables =
                Collections.singletonMap(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, (Object)bindStatus);
        
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
        if (!org.thymeleaf.util.StringUtils.isEmptyOrWhitespace(id)) {
            return (StringUtils.hasText(id) ? id : null);
        }

        id = FieldUtils.idFromName(name);
        if (sequence) {
            final Integer count = arguments.getAndIncrementIDSeq(id);
            return id + count.toString();
        }
        return id;
        
    }
    

    

}
