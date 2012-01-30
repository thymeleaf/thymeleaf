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
package org.thymeleaf.processor.attr;

import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.DOMUtils;
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
public abstract class AbstractAttributeModifierAttrProcessor 
        extends AbstractAttrProcessor {

    public enum ModificationType { SUBSTITUTION, APPEND, APPEND_WITH_SPACE, PREPEND, PREPEND_WITH_SPACE }
    
    
    
    public AbstractAttributeModifierAttrProcessor() {
        super();
    }

    
    
    
    public final AttrProcessResult process(final Arguments arguments, final TemplateResolution templateResolution,
            final Document document, final Element element, 
            final Attr attribute) {
        
        final String attributeName = attribute.getName();
        final String normalizedAttributeName =
            (attributeName == null? null : attributeName.toLowerCase());
        
        String attributeValue = attribute.getValue();

        if (attributeValue == null || attributeValue.trim().equals("")) {
            throw new AttrProcessorException("Empty value for \"" + attribute.getName() + "\" attribute not allowed");
        }
        
        attributeValue = attributeValue.trim();
        
        final Map<String,String> newAttributeValues = 
            getNewAttributeValues(arguments, templateResolution, document, element, attribute, normalizedAttributeName, attributeValue); 
        if (newAttributeValues == null) {
            throw new AttrProcessorException(
                    "Null new attribute value map specified for: \"" + attributeValue + "\"");
        }
        
        for (final Map.Entry<String,String> newAttributeEntry : newAttributeValues.entrySet()) {

            final String newAttributeName = newAttributeEntry.getKey();
            final String normalizedNewAttributeName = 
                (newAttributeName == null? null : newAttributeName.toLowerCase());
            
            String newAttributeValue = newAttributeEntry.getValue();

            final Attr currentAttribute = DOMUtils.findAttribute(normalizedNewAttributeName, element);
            String currentAttributeValue = "";
            if (currentAttribute != null && currentAttribute.getValue() != null) {
                currentAttributeValue = currentAttribute.getValue();
            }

            if (newAttributeValue == null) {
                newAttributeValue = "";
            }
            
            
            final ModificationType modificationType =
                getModificationType(arguments, templateResolution, document, element, currentAttribute, normalizedAttributeName, currentAttributeValue, normalizedNewAttributeName);
            
            switch (modificationType) {
                case SUBSTITUTION :
                    break;
                case APPEND :
                    newAttributeValue = currentAttributeValue + newAttributeValue;
                    break;
                case APPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        newAttributeValue = currentAttributeValue + " " + newAttributeValue;
                    } else {
                        newAttributeValue = currentAttributeValue + newAttributeValue;
                    }
                    break;
                case PREPEND :
                    newAttributeValue = newAttributeValue + currentAttributeValue;
                    break;
                case PREPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        newAttributeValue = newAttributeValue + " " + currentAttributeValue;
                    } else {
                        newAttributeValue = newAttributeValue + currentAttributeValue;
                    }
                    break;
            }


            final boolean removeAttributeIfEmpty =
                removeAttributeIfEmpty(arguments, templateResolution, document, element, currentAttribute, normalizedAttributeName, currentAttributeValue, newAttributeName);
            
            // Do NOT use trim() here! Non-thymeleaf attributes set to ' ' could have meaning!
            if (newAttributeValue.equals("") && removeAttributeIfEmpty) {
                
                if (currentAttribute != null) {
                    element.removeAttributeNode(currentAttribute);
                }
                
            } else {
                
                final Attr newNode = document.createAttribute(newAttributeName);
                newNode.setValue(newAttributeValue);
                element.setAttributeNode(newNode);
                
            }
            
        }
        
        return AttrProcessResult.REMOVE_ATTRIBUTE;
        
    }

    
    
    protected abstract Map<String,String> getNewAttributeValues(final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, final String attributeValue);
    

    
    protected abstract ModificationType getModificationType(final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, 
            final String attributeValue, final String newAttributeName);
    
    
    protected abstract boolean removeAttributeIfEmpty(final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, 
            final String attributeValue, final String newAttributeName);
    
}
