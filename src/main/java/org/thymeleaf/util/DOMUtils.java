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
package org.thymeleaf.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1.2
 *
 */
public final class DOMUtils {
    
    
    
    public static Node extractFragmentByAttributevalue(
            final Document document, final String tagName, final String attributeName, final String attributeValue) {

        Validate.notNull(document, "Document cannot be null");
        // Tag name CAN be null (in that case, all tags will be searched)
        Validate.notNull(attributeName, "Attribute name cannot be null");
        Validate.notNull(attributeValue, "Attribute value cannot be null");
        
        final String normalizedTagName =
            (tagName == null? null : tagName.toLowerCase());
        final String normalizedAttributeName =
            (attributeName == null? null : attributeName.toLowerCase());
        
        return processNode(document.getDocumentElement(), normalizedTagName, normalizedAttributeName, attributeValue);
        
    }
    
    
    private static Node processNode(
            final Node node, final String tagName, final String attributeName, final String attributeValue) {
        
        if (!(node instanceof Element)) {
            return null;
        }
        
        final Element element = (Element) node;
        final String elementName = element.getTagName();
        final String normalizedElementName =
            (elementName == null? null : elementName.toLowerCase());
        
        final Attr attribute = findAttribute(attributeName, element);
        if (attribute != null && 
                (tagName == null || tagName.equals(normalizedElementName))) {
            final String currentAttributeValue = attribute.getValue();
            if (currentAttributeValue != null && currentAttributeValue.trim().equals(attributeValue)) {
                return node;
            }
        }
        
        final NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node childResult = processNode(children.item(i), tagName, attributeName, attributeValue);
            if (childResult != null) {
                return childResult;
            }
        }
        
        return null;
        
    }

    

    
    
    public static Attr findAttribute(final String normalizedAttrName, final Element element) {
        
        Validate.notNull(normalizedAttrName, "Normalized attribute name cannot be null");
        Validate.notNull(element, "Element cannot be null");

        // Try to return quickly
        final Attr firstAttempt = element.getAttributeNode(normalizedAttrName);
        if (firstAttempt != null) {
            return firstAttempt;
        }
        
        final NamedNodeMap attributes = element.getAttributes();
        final int attributesLen = attributes.getLength();
        
        for (int i = 0; i < attributesLen; i++) {
            final Attr attribute = (Attr) attributes.item(i);
            final String attributeName = attribute.getName();
            final String normalizedAttributeName =
                (attributeName == null? null : attributeName.toLowerCase());
            if (normalizedAttrName.equals(normalizedAttributeName)) {
                return attribute;
            }
        }
        
        return null;
        
    }

    
    
    private DOMUtils() {
        super();
    }
    
}
