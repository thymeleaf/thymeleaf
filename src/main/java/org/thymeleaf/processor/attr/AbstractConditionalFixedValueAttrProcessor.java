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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;
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

    
    
    
    public AbstractConditionalFixedValueAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractConditionalFixedValueAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Tag tag, final String attributeName) {
        
        final boolean visible = isVisible(arguments, tag, attributeName);
        
        final String targetAttributeName = getTargetAttributeName(arguments, tag, attributeName);

        if (!visible) {
            tag.removeAttribute(attributeName);
            return ProcessorResult.OK;
        }

        // We remove the original attribute first just in case the 
        // attribute to be added has the same name.
        tag.removeAttribute(attributeName);

        final String targetAttributeFixedValue = 
                getTargetAttributeFixedValue(arguments, tag, attributeName);
        
        tag.setAttribute(targetAttributeName, targetAttributeFixedValue);
        
        if (recomputeProcessorsAfterExecution(arguments, tag, attributeName)) {
            tag.setRecomputeProcessorsImmediately(true);
        }
        
        return ProcessorResult.OK;
        
    }


    
    
    protected abstract boolean isVisible(
            final Arguments arguments, final Tag tag, final String attributeName);


    
    
    protected abstract String getTargetAttributeName(final Arguments arguments, 
            final Tag tag, final String attributeName);
    
    protected abstract String getTargetAttributeFixedValue(final Arguments arguments, 
            final Tag tag, final String attributeName);
    
    protected abstract boolean recomputeProcessorsAfterExecution(
            final Arguments arguments, final Tag tag, final String attributeName);
    
}
