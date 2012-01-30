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
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.applicability.IApplicabilityFilter;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.processor.attr.AttrProcessResult;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.SetUtils;
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
public abstract class AbstractSpringFieldAttrProcessor 
        extends AbstractAttrProcessor {

    
    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1200);
    public static final String ATTR_NAME = "field";
    
    
    protected static final String INPUT_TAG_NAME = "input";
    protected static final String SELECT_TAG_NAME = "select";
    protected static final String OPTION_TAG_NAME = "option";
    protected static final String TEXTAREA_TAG_NAME = "textarea";
    
    protected static final String INPUT_TYPE_ATTR_NAME = "type";

    
    

    
    
    public AbstractSpringFieldAttrProcessor() {
        super();
    }

    
    public final Set<AttrApplicability> getAttributeApplicabilities() {
        return SetUtils.singletonSet(new AttrApplicability(ATTR_NAME, getApplicabilityFilter()));
    }

    
    protected abstract IApplicabilityFilter getApplicabilityFilter();
    
    
    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    


    public final AttrProcessResult process(
            final Arguments arguments, final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute) {
        
        String attributeValue = attribute.getValue();

        if (attributeValue == null || attributeValue.trim().equals("")) {
            throw new AttrProcessorException("Empty value for \"" +
                    attribute.getName() + "\" attribute not allowed");
        }
        
        attributeValue = attributeValue.trim();

        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments, templateResolution, attributeValue, false);
        
        final Map<String,Object> localVariables = new HashMap<String,Object>();
        localVariables.put(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);
        
        return doProcess(arguments, templateResolution, document, element, attribute, bindStatus, localVariables);
        
    }
    
    

    protected abstract AttrProcessResult doProcess(
            final Arguments arguments, final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final BindStatus bindStatus,
            final Map<String,Object> localVariables);
    

    
    
    
    @SuppressWarnings("unused") // This method is designed to be overridable
    protected String computeId(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Element element, final String name, final boolean sequence) {
        
        String id = element.getAttribute("id");
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
