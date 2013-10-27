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

/**
 * Specifies another template and the fragment within that other template to
 * include at this point, eg:
 * <p>
 * &lt;div th:include="templateName :: fragmentName"&gt;&lt;div&gt;
 * <p>
 * The above will find the template named templateName, and add the fragment
 * named fragmentName as a child of this element.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.0.9
 */
public class StandardIncludeFragmentAttrProcessor 
        extends AbstractStandardFragmentHandlingAttrProcessor {

    public static final int ATTR_PRECEDENCE = 100;
    public static final String ATTR_NAME = "include";
    public static final String FRAGMENT_ATTR_NAME = StandardFragmentAttrProcessor.ATTR_NAME;
    
    
    
    
    public StandardIncludeFragmentAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getFragmentSignatureUnprefixedAttributeName(
            final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue) {

        return FRAGMENT_ATTR_NAME;

    }



    @Override
    protected boolean getRemoveHostNode(
            final Arguments arguments,
            final Element element, final String attributeName, final String attributeValue) {
        // th:include does not substitute the inclusion node
        return false;
    }



    
}
