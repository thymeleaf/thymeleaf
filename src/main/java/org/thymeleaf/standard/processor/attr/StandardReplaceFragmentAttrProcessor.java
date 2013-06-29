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
import org.thymeleaf.util.PrefixUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class StandardReplaceFragmentAttrProcessor
        extends AbstractStandardFragmentHandlingAttrProcessor {

    public static final int ATTR_PRECEDENCE = 100;
    public static final String ATTR_NAME = "replace";
    public static final String FRAGMENT_ATTR_NAME = StandardFragmentAttrProcessor.ATTR_NAME;




    public StandardReplaceFragmentAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue) {
        
        if (attributeName != null) {
            final String prefix = PrefixUtils.getPrefix(attributeName);
            if (prefix != null) {
                return prefix + ':' + FRAGMENT_ATTR_NAME;
            }
        }
        return FRAGMENT_ATTR_NAME;
        
    }



    @Override
    protected boolean getSubstituteInclusionNode(
            final Arguments arguments, 
            final Element element, final String attributeName, final String attributeValue) {
        // th:replace does replace the inclusion node
        return true;
    }



    
}
