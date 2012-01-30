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
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.SubstitutionTag;
import org.thymeleaf.processor.applicability.IApplicabilityFilter;
import org.thymeleaf.processor.applicability.TagNameApplicabilityFilter;
import org.thymeleaf.processor.attr.AttrProcessResult;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringSelectFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    

    public SpringSelectFieldAttrProcessor() {
        super();
    }

    


    @Override
    protected IApplicabilityFilter getApplicabilityFilter() {
        return new TagNameApplicabilityFilter(SELECT_TAG_NAME);
    }




    @Override
    protected AttrProcessResult doProcess(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, templateResolution, element, name, false);

        final Attr multipleAttr = DOMUtils.findAttribute("multiple", element); 
        boolean multiple = (multipleAttr != null);
        
        final List<SubstitutionTag> substitutionTags = new ArrayList<SubstitutionTag>();
        
        final Element inputElement = (Element) element.cloneNode(true);
        inputElement.removeAttribute(attribute.getName());
        
        inputElement.setAttribute("id", id);
        inputElement.setAttribute("name", name);

        final String attrName = attribute.getName();
        final String attrValue = attribute.getValue();
        
        processOptionChildren(inputElement, attrName, attrValue);

        final SubstitutionTag inputTag = SubstitutionTag.forNodeAndLocalVariables(inputElement, localVariables);
        substitutionTags.add(inputTag);
        

        if (multiple) {
            
            final Element hiddenElement = document.createElement("input");
            hiddenElement.setAttribute("type", "hidden");
            hiddenElement.setAttribute("name", WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name);
            hiddenElement.setAttribute("value", "1");
            
            final SubstitutionTag hiddenTag = SubstitutionTag.forNodeAndLocalVariables(hiddenElement, localVariables);
            substitutionTags.add(hiddenTag);
            
        }
        
        
        return AttrProcessResult.forSubstituteTag(substitutionTags);
        
    }


    
    private void processOptionChildren(
            final Element element, final String selectAttrName, final String selectAttrValue) {
        
        final NodeList children = element.getChildNodes();
        final int childrenLen = children.getLength();
        
        for (int i = 0; i < childrenLen; i++) {
            
            final Node child = children.item(i);
            if (child != null && child instanceof Element) {
                
                final Element childElement = (Element) child;
                final String childTagName = childElement.getTagName();
                
                if (childTagName != null && childTagName.toLowerCase().equals("option")) {
                    
                    final Attr selectAttrInChild = 
                        DOMUtils.findAttribute(selectAttrName, childElement);
                    
                    if (selectAttrInChild != null) { // has attribute
                        
                        final String selectAttrInChildValue = selectAttrInChild.getValue();
                        
                        if (selectAttrInChildValue != null) {
                            if (!selectAttrValue.equals(selectAttrInChildValue)) {
                                throw new AttrProcessorException(
                                        "If specified (which is not required), attribute " +
                                        "\"" + selectAttrName + "\" in \"option\" tag must have " +
                                        "exactly the same value as in its containing \"select\" " +
                                        "tag");
                            }
                        }
                        
                    } else {
                        childElement.setAttribute(selectAttrName, selectAttrValue);
                    }
                    
                } else if (childTagName != null && childTagName.toLowerCase().equals("optgroup")) {

                    processOptionChildren(childElement, selectAttrName, selectAttrValue);
                    
                }
                
            }
            
        }
        
    }
    

}
