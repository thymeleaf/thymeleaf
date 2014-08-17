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
package org.thymeleaf.processor.element;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractMarkupSubstitutionElementProcessor 
        extends AbstractElementProcessor {
    
    
    

    protected AbstractMarkupSubstitutionElementProcessor(final String elementName) {
        super(elementName);
    }
    
    protected AbstractMarkupSubstitutionElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }


    
    
    
    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {

        final List<Node> substitutes = getMarkupSubstitutes(arguments, element);
        
        final NestableNode parent = element.getParent();
        
        for (final Node node : substitutes) {
            parent.insertBefore(element, node);
        }
        parent.removeChild(element);
        
        return ProcessorResult.OK;
        
    }

    
    
    protected abstract List<Node> getMarkupSubstitutes(final Arguments arguments, final Element element);

    
}
