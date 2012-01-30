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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.DOMExecution;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.processor.attr.AttrProcessResult;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.templateresolver.TemplateResolution;
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
public final class SpringErrorsAttrProcessor 
        extends AbstractAttrProcessor {

    private static final String ERROR_DELIMITER = "<br />";
    
    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1200);
    public static final String ATTR_NAME = "errors";

    

    
    
    public SpringErrorsAttrProcessor() {
        super();
    }

    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName(ATTR_NAME);
    }

    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    
    
    


    public AttrProcessResult process(
            final Arguments arguments, final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute) {
        
        String attributeValue = attribute.getValue();

        if (attributeValue == null || attributeValue.trim().equals("")) {
            throw new AttrProcessorException("Empty value for \"" +
                    attribute.getName() + "\" attribute not allowed");
        }
        
        attributeValue = attributeValue.trim();
        
        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments, templateResolution, attributeValue, true);
        
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
            final NodeList currentChildren = element.getChildNodes();
            for (int i = 0; i < currentChildren.getLength(); i++) {
                element.removeChild(currentChildren.item(0));
            }
            
            
            // Use the parser to obtain a DOM from the String
            final Node fragNode = 
                arguments.getTemplateParser().parseXMLString(arguments, strBuilder.toString());

            // Imports the nodes from the fragment document to the template one
            final Node node = document.importNode(fragNode, true);
            
            final List<Node> children = new ArrayList<Node>();
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                children.add(nodeList.item(i));
            }
            
            // Add new nodes to container node
            for (final Node child : children) {
                
                // Setting this allows avoiding text inliners processing already generated nodes or text,
                // which in turn avoids code injection.
                DOMExecution.setExecutableTree(child, false);
                
                element.appendChild(child);
                
            }
            
            return AttrProcessResult.forRemoveAttribute(localVariables);
            
        }
        
        return AttrProcessResult.REMOVE_TAG_AND_CHILDREN;
    }

        
    

}
