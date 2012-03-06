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
package org.thymeleaf.processor.attr;

import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractAttributeModifierAttrProcessor extends AbstractAttrProcessor {

    public enum ModificationType { SUBSTITUTION, APPEND, APPEND_WITH_SPACE, PREPEND, PREPEND_WITH_SPACE }
    
    
    


    public AbstractAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }





    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        
        final Map<String,String> modifiedAttributeValues = 
            getModifiedAttributeValues(arguments, element, attributeName); 
        if (modifiedAttributeValues == null) {
            throw new TemplateProcessingException(
                    "Null new attribute value map specified for: \"" + attributeName + "\"");
        }
        
        for (final Map.Entry<String,String> modifiedAttributeEntry : modifiedAttributeValues.entrySet()) {

            final String modifiedAttributeName = modifiedAttributeEntry.getKey();
            String modifiedAttributeValue = modifiedAttributeEntry.getValue();
            String currentAttributeValue = element.getAttributeValue(modifiedAttributeName);
            
            final ModificationType modificationType =
                    getModificationType(arguments, element, attributeName, modifiedAttributeName);
            
            if (currentAttributeValue == null) {
                currentAttributeValue = "";
            }

            if (modifiedAttributeValue == null) {
                modifiedAttributeValue = "";
            }
            
            
            switch (modificationType) {
                case SUBSTITUTION :
                    break;
                case APPEND :
                    modifiedAttributeValue = currentAttributeValue + modifiedAttributeValue;
                    break;
                case APPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        modifiedAttributeValue = currentAttributeValue + " " + modifiedAttributeValue;
                    } else {
                        modifiedAttributeValue = currentAttributeValue + modifiedAttributeValue;
                    }
                    break;
                case PREPEND :
                    modifiedAttributeValue = modifiedAttributeValue + currentAttributeValue;
                    break;
                case PREPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        modifiedAttributeValue = modifiedAttributeValue + " " + currentAttributeValue;
                    } else {
                        modifiedAttributeValue = modifiedAttributeValue + currentAttributeValue;
                    }
                    break;
            }


            final boolean removeAttributeIfEmpty =
                removeAttributeIfEmpty(arguments, element, attributeName, modifiedAttributeName);
            
            // Do NOT use trim() here! Non-thymeleaf attributes set to ' ' could have meaning!
            if (modifiedAttributeValue.equals("") && removeAttributeIfEmpty) {
                element.removeAttribute(modifiedAttributeName);
            } else {
                element.setAttribute(modifiedAttributeName, modifiedAttributeValue);
            }
            
        }
        
        doAdditionalProcess(arguments, element, attributeName);
        
        element.removeAttribute(attributeName);
        
        if (recomputeProcessorsAfterExecution(arguments, element, attributeName)) {
            element.setRecomputeProcessorsImmediately(true);
        }
        
        return ProcessorResult.OK;
        
    }

    
    
    protected abstract Map<String,String> getModifiedAttributeValues(final Arguments arguments, 
            final Element element, final String attributeName);
    

    
    protected abstract ModificationType getModificationType(final Arguments arguments, 
            final Element element, final String attributeName, final String newAttributeName);
    
    
    protected abstract boolean removeAttributeIfEmpty(final Arguments arguments, 
            final Element element, final String attributeName, final String newAttributeName);

    
    protected abstract boolean recomputeProcessorsAfterExecution(
            final Arguments arguments, final Element element, final String attributeName);

    
    
    @SuppressWarnings("unused")
    protected void doAdditionalProcess(
            final Arguments arguments, final Element element, final String attributeName) {
        // Nothing to be done, meant to be overriden
    }
    
    
}
