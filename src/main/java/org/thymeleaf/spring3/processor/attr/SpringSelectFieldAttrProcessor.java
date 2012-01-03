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

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.ProcessorResult;



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
    protected ProcessorResult doProcess(final Arguments arguments, final Tag tag,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, tag, name, false);

        boolean multiple = tag.hasAttribute("multiple");
        
        final NestableNode parent = tag.getParent();
        
        final Tag inputElement = (Tag) tag.cloneNode(parent, false);
        inputElement.removeAttribute(attributeName);
        
        inputElement.setAttribute("id", id);
        inputElement.setAttribute("name", name);
        inputElement.addNodeLocalVariables(localVariables);

        processOptionChildren(inputElement, attributeName, attributeValue);
        
        parent.insertBefore(tag, inputElement);
        parent.removeChild(tag);
        

        if (multiple) {
            
            final Tag hiddenElement = new Tag("input");
            hiddenElement.setAttribute("type", "hidden");
            hiddenElement.setAttribute("name", WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name);
            hiddenElement.setAttribute("value", "1");
            hiddenElement.addNodeLocalVariables(localVariables);
            
            parent.insertAfter(inputElement, hiddenElement);
            
        }
        
        
        return ProcessorResult.OK;
        
    }


    
    private void processOptionChildren(
            final Tag inputTag, final String selectAttrName, final String selectAttrValue) {
        
        final List<Node> children = inputTag.getChildren();
        
        for (final Node child : children) {
            
            if (child != null && child instanceof Tag) {
                
                final Tag childTag = (Tag) child;
                final String childTagName = childTag.getNormalizedName();

                childTag.setSkippable(false);
                
                if (childTagName != null && childTagName.equals("option")) {
                    
                    if (childTag.hasAttribute(selectAttrName)) { // has attribute
                        
                        final String selectAttrInChildValue = childTag.getAttributeValue(selectAttrName);
                        
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
                        childTag.setAttribute(selectAttrName, selectAttrValue);
                        childTag.setRecomputeProcessorsImmediately(true);
                    }
                    
                } else if (childTagName != null && childTagName.equals("optgroup")) {

                    processOptionChildren(childTag, selectAttrName, selectAttrValue);
                    
                }
                
            }
            
        }
        
    }
    

}
