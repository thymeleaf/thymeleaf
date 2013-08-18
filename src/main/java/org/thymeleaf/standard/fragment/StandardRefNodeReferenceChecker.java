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
package org.thymeleaf.standard.fragment;

import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Implementation of the {@link org.thymeleaf.dom.DOMSelector.INodeReferenceChecker} interface used for looking
 *   for a simple node reference in the form of an attribute (usually <tt>th:ref</tt>).
 * </p>
 * <p>
 *   For example, if the <tt>th</tt> prefix is being used for the Standard Dialect (which is the default), objects
 *   of this class will consider that DOM Selector expressions like <tt>mynode</tt> or <tt>%mynode</tt> will match
 *   <tt>th:ref="mynode"</tt>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class StandardRefNodeReferenceChecker extends DOMSelector.AbstractNodeReferenceChecker {

    private final String refAttributeName;

    public StandardRefNodeReferenceChecker(final String refAttributeName) {
        super();
        Validate.notNull(refAttributeName, "Reference attribute name cannot be null");
        this.refAttributeName = Node.normalizeName(refAttributeName);
    }

    public String getRefAttributeName() {
        return this.refAttributeName;
    }


    public boolean checkReference(final Node node, final String referenceValue) {

        if (node instanceof NestableAttributeHolderNode) {

            final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode) node;

            if (attributeHolderNode.hasNormalizedAttribute(this.refAttributeName)) {
                final String elementAttrValue = attributeHolderNode.getAttributeValue(this.refAttributeName);
                if (elementAttrValue != null) {
                    return elementAttrValue.equals(referenceValue);
                }
            }

        }

        return false;

    }


}
