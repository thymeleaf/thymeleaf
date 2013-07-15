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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractFragmentHandlingAttrProcessor;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractStandardFragmentHandlingAttrProcessor 
        extends AbstractFragmentHandlingAttrProcessor {

    
    
    

    
    protected AbstractStandardFragmentHandlingAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardFragmentHandlingAttrProcessor(final String attributeName) {
        super(attributeName);
    }


    

    
    @Override
    protected final FragmentAndTarget getFragmentAndTarget(final Arguments arguments,
            final Element element, final String attributeName, final String attributeValue, 
            final boolean substituteInclusionNode) {

        final String targetAttributeName = 
                getTargetAttributeName(arguments, element, attributeName, attributeValue);
        
        return StandardFragmentProcessor.computeStandardFragmentSpec(
                arguments.getConfiguration(), arguments, attributeValue, null, targetAttributeName,
                !substituteInclusionNode);
        
    }

    
    
    protected abstract String getTargetAttributeName(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue);

    
}
