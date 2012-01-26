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

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractSingleAttributeModifierAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {

    
    
    public AbstractSingleAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }



    public AbstractSingleAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }



    @Override
    protected final Map<String,String> getModifiedAttributeValues(
            final Arguments arguments, final Element element, 
            final String attributeName) {
        
        final String name = getTargetAttributeName(arguments, element, attributeName);
        final String value = getTargetAttributeValue(arguments, element, attributeName);
        
        final Map<String,String> valuesMap = new HashMap<String,String>();
        valuesMap.put(name, value);
        return valuesMap;
        
    }
    

    
    protected abstract String getTargetAttributeName(
            final Arguments arguments, final Element element, 
            final String attributeName);
    
    
    
    protected abstract String getTargetAttributeValue(
            final Arguments arguments, final Element element, 
            final String attributeName);
    
    
}
