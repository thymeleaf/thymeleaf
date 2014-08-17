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

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
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
public final class SpringSelectFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    

    
    public SpringSelectFieldAttrProcessor() {
        super(ATTR_NAME,
              SELECT_TAG_NAME);
    }




    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, element, name, false);

        final boolean multiple = element.hasAttribute("multiple");
        
        final NestableNode parent = element.getParent();
        
        final Element inputElement = (Element) element.cloneNode(parent, false);
        inputElement.removeAttribute(attributeName);
        
        inputElement.setAttribute("id", id);
        inputElement.setAttribute("name", name);
        inputElement.setAllNodeLocalVariables(localVariables);

        processOptionChildren(inputElement, attributeName, attributeValue);
        
        parent.insertBefore(element, inputElement);
        parent.removeChild(element);
        

        if (multiple && !isDisabled(inputElement)) {

            final String hiddenName = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name;
            final Element hiddenElement = new Element("input");
            hiddenElement.setAttribute("type", "hidden");
            hiddenElement.setAttribute("name", hiddenName);
            hiddenElement.setAttribute(
                    "value",
                    RequestDataValueProcessorUtils.processFormFieldValue(
                            arguments.getConfiguration(), arguments, hiddenName, "1", "hidden"));

            hiddenElement.setAllNodeLocalVariables(localVariables);
            
            parent.insertAfter(inputElement, hiddenElement);
            
        }
        
        
        return ProcessorResult.OK;
        
    }


    
    private void processOptionChildren(
            final Element inputTag, final String selectAttrName, final String selectAttrValue) {
        
        final List<Node> children = inputTag.getChildren();
        
        for (final Node child : children) {
            
            if (child != null && child instanceof Element) {
                
                final Element childTag = (Element) child;
                final String childTagName = childTag.getNormalizedName();

                childTag.setProcessable(true);
                
                if ("option".equals(childTagName)) {
                    
                    if (childTag.hasAttribute(selectAttrName)) { // has attribute
                        
                        final String selectAttrInChildValue = childTag.getAttributeValue(selectAttrName);
                        
                        if (selectAttrInChildValue != null) {
                            if (!selectAttrValue.equals(selectAttrInChildValue)) {
                                throw new TemplateProcessingException(
                                        "If specified (which is not required), attribute " +
                                        "\"" + selectAttrName + "\" in \"option\" tag must have " +
                                        "exactly the same value as in its containing \"select\" " +
                                        "tag");
                            }
                        }
                        
                    } else {
                        childTag.setAttribute(selectAttrName, selectAttrValue);
                        childTag.setRecomputeProcessorsImmediately(true);
                    }
                    
                } else if ("optgroup".equals(childTagName)) {

                    processOptionChildren(childTag, selectAttrName, selectAttrValue);
                    
                }
                
            }
            
        }
        
    }


    private static final boolean isDisabled(final Element inputElement) {
        // Disabled = attribute "disabled" exists
        return inputElement.hasNormalizedAttribute("disabled");
    }


}
