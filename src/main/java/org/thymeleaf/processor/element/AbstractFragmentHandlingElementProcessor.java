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
package org.thymeleaf.processor.element;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractFragmentHandlingElementProcessor 
        extends AbstractElementProcessor {

    

    public AbstractFragmentHandlingElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractFragmentHandlingElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }



    
    
    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {
        
        final boolean substituteInclusionNode = getSubstituteInclusionNode(arguments, element);
        
        final FragmentAndTarget fragmentAndTarget = getFragmentAndTarget(arguments, element);
        
        final List<Node> fragmentNodes = 
                fragmentAndTarget.extractFragment(
                        arguments.getConfiguration(), arguments.getContext(), arguments.getTemplateRepository());
        
        if (fragmentNodes == null) {
            throw new TemplateProcessingException(
                    "Cannot correctly process \"" + element.getOriginalName() + "\" element. " +
                    "Fragment specification matched null.");
        }

        
        element.clearChildren();
        
        if (substituteInclusionNode) {
            
            element.setChildren(fragmentNodes);
            element.getParent().extractChild(element);
            
        } else {
            
            for (final Node fragmentNode : fragmentNodes) {
                if (!(fragmentNode instanceof NestableNode)) {
                    throw new TemplateProcessingException(
                            "Cannot correctly process \"" + element.getOriginalName() + "\" element. " +
                            "Node returned by fragment specification " +
                            "for inclusion is not a nestable node (" + fragmentNode.getClass().getSimpleName() + ").");
                }
                
                for (final Node newNode : ((NestableNode)fragmentNode).getChildren()) {
                    element.addChild(newNode);
                }
            }
            
        }
        
        doAdditionalElementProcessing(arguments, element);
        
        return ProcessorResult.OK;
            
    }

    
    
    

    protected abstract boolean getSubstituteInclusionNode(
            final Arguments arguments, final Element element);
    
    protected abstract FragmentAndTarget getFragmentAndTarget(
            final Arguments arguments, final Element element);
    
    
    
    @SuppressWarnings("unused")
    protected void doAdditionalElementProcessing(final Arguments arguments, final Element element) {
        // Meant for overriding. Nothing to do.
    }
    
}
