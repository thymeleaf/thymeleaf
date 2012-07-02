/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class ElementAndAttributeNameFragmentSpec implements IFragmentSpec {
    
    private final String elementName;
    private final String attributeName;
    private final String attributeValue;
    
    
    
    
    public ElementAndAttributeNameFragmentSpec(
            final String elementName, 
            final String attributeName, 
            final String attributeValue) {
        
        super();
        
        // Either fragment or element name CAN be null (but not both). If element name is
        // null, no check will be done on the containing element.
        Validate.isTrue(!(StringUtils.isEmpty(elementName).booleanValue() && 
                          StringUtils.isEmpty(attributeName).booleanValue()), 
                "Either element name of attribute name must not be null or empty");
        
        // If attribute name has been specified, a value must have been specified too.
        if (!StringUtils.isEmpty(attributeName).booleanValue()) {
            Validate.notEmpty(attributeValue, "Fragment attribute value cannot be null or empty");
        }
        
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        
    }


    public String getElementName() {
        return this.elementName;
    }

    
    public String getAttributeName() {
        return this.attributeName;
    }


    public String getAttributeValue() {
        return this.attributeValue;
    }


    
    
    
    public final List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        
        return DOMUtils.extractFragmentByElementAndAttributeValue(
                nodes, this.elementName, this.attributeName, this.attributeValue);
        
    }


    
    
    @Override
    public String toString() {
        return "(ELEMENT: " + this.elementName + " | ATTRIBUTE: " + this.attributeName +"=\"" + this.attributeValue + "\")";
    }

    
    
}

