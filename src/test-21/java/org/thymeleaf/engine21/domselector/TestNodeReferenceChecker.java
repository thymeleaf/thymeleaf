/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.domselector;

import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;


public class TestNodeReferenceChecker implements DOMSelector.INodeReferenceChecker {

    public static final String REF_ATTRIBUTE_NAME = "ref";

    public TestNodeReferenceChecker() {
        super();
    }



    public boolean checkReference(final Node node, final String referenceValue) {
        if (node instanceof NestableAttributeHolderNode) {
            final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode) node;
            if (attributeHolderNode.hasAttribute(REF_ATTRIBUTE_NAME)) {
                final String refValue = attributeHolderNode.getAttributeValue(REF_ATTRIBUTE_NAME);
                if (referenceValue == null) {
                    return (refValue == null);
                }
                return referenceValue.equals(refValue);
            }
        }
        return false;
    }

}
