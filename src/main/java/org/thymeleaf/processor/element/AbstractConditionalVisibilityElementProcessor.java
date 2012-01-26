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
package org.thymeleaf.processor.element;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractConditionalVisibilityElementProcessor 
        extends AbstractElementProcessor {

    
    
    

    public AbstractConditionalVisibilityElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractConditionalVisibilityElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }






    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {
        
        final boolean visible = 
            isVisible(arguments, element);

        final boolean removeHostElementIfVisible = 
                removeHostElementIfVisible(arguments, element);
        
        final NestableNode parent = element.getParent();
        
        if (!visible) {
            element.clearChildren();
            parent.removeChild(element);
            return ProcessorResult.OK;
        }
        
        if (removeHostElementIfVisible) {
            parent.extractChild(element);
        }
        
        return ProcessorResult.OK;
        
    }


    
    
    protected abstract boolean isVisible(final Arguments arguments, final Element element);

    
    
    protected abstract boolean removeHostElementIfVisible(final Arguments arguments, final Element element);
    

}
