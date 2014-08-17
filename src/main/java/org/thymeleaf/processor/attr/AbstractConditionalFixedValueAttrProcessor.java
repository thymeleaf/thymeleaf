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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractConditionalFixedValueAttrProcessor 
        extends AbstractAttrProcessor {

    
    
    
    protected AbstractConditionalFixedValueAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    protected AbstractConditionalFixedValueAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        
        final boolean visible = isVisible(arguments, element, attributeName);
        
        final String targetAttributeName = getTargetAttributeName(arguments, element, attributeName);

        if (!visible) {
            element.removeAttribute(targetAttributeName);
            element.removeAttribute(attributeName);
            return ProcessorResult.OK;
        }

        // We remove the original attribute first just in case the 
        // attribute to be added has the same name.
        element.removeAttribute(attributeName);

        final String targetAttributeFixedValue = 
                getTargetAttributeFixedValue(arguments, element, attributeName);
        
        element.setAttribute(targetAttributeName, targetAttributeFixedValue);
        
        if (recomputeProcessorsAfterExecution(arguments, element, attributeName)) {
            element.setRecomputeProcessorsImmediately(true);
        }
        
        return ProcessorResult.OK;
        
    }


    
    
    protected abstract boolean isVisible(
            final Arguments arguments, final Element element, final String attributeName);


    
    
    protected abstract String getTargetAttributeName(final Arguments arguments, 
            final Element element, final String attributeName);
    
    protected abstract String getTargetAttributeFixedValue(final Arguments arguments, 
            final Element element, final String attributeName);
    
    protected abstract boolean recomputeProcessorsAfterExecution(
            final Arguments arguments, final Element element, final String attributeName);
    
}
