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
public abstract class AbstractLocalVariableDefinitionAttrProcessor 
        extends AbstractAttrProcessor {
    
    
    
    
    
    protected AbstractLocalVariableDefinitionAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    protected AbstractLocalVariableDefinitionAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        
        final Map<String,Object> newLocalVariables = 
            getNewLocalVariables(arguments, element, attributeName);
        if (newLocalVariables == null) {
            throw new TemplateProcessingException("Null variables map for \"" + attributeName + "\" attribute not allowed");
        }

        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(newLocalVariables);
        
    }
    
    
    
    protected abstract Map<String,Object> getNewLocalVariables(
            final Arguments arguments, final Element element, final String attributeName);
    
    
}
