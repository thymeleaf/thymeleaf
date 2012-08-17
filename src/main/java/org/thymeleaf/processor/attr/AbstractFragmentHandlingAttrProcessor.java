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

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractFragmentHandlingAttrProcessor 
        extends AbstractAttrProcessor {

    
    
    
    
    public AbstractFragmentHandlingAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractFragmentHandlingAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        final boolean substituteInclusionNode =
            getSubstituteInclusionNode(arguments, element, attributeName, attributeValue);
        
        final FragmentAndTarget fragmentAndTarget = 
                getFragmentAndTarget(arguments, element, attributeName, attributeValue, substituteInclusionNode);
        
        final List<Node> fragmentNodes = 
                fragmentAndTarget.extractFragment(
                        arguments.getConfiguration(), arguments, arguments.getTemplateRepository());
        
        if (fragmentNodes == null) {
            throw new TemplateProcessingException(
                    "Cannot correctly process \"" + attributeName + "\" attribute. " +
                    "Fragment specification \"" + attributeValue + "\" matched null.");
        }

        
        
        element.clearChildren();
        element.removeAttribute(attributeName);
        
        if (substituteInclusionNode) {
            
            element.setChildren(fragmentNodes);
            element.getParent().extractChild(element);
            
        } else {
         
            for (final Node fragmentNode : fragmentNodes) {
                element.addChild(fragmentNode);
            }
            
        }
        
        return ProcessorResult.OK;
            
    }



    protected abstract boolean getSubstituteInclusionNode(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue);
    
    protected abstract FragmentAndTarget getFragmentAndTarget(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue, 
            final boolean substituteInclusionNode);
    

    
    
}
