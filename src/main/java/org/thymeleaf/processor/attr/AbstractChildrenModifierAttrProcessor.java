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

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractChildrenModifierAttrProcessor 
        extends AbstractAttrProcessor {
    
    
    
    protected AbstractChildrenModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    protected AbstractChildrenModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {

        
        final List<Node> modifiedChildren = 
            getModifiedChildren(arguments, element, attributeName);
        
        element.clearChildren();
        
        if (modifiedChildren != null) {
            element.setChildren(modifiedChildren);
        }
        
        element.removeAttribute(attributeName);
        
        if (getReplaceHostElement(arguments, element, attributeName)) {
            element.getParent().extractChild(element);
        }
        
        return ProcessorResult.OK;
        
    }

    
    
    protected abstract List<Node> getModifiedChildren(
            final Arguments arguments, final Element element, final String attributeName);

    
    
    @SuppressWarnings("unused")
    protected boolean getReplaceHostElement(
            final Arguments arguments, final Element element, final String attributeName) {
        // Meant to be overriden if the host element has to be replaced
        return false;
    }

    
}
