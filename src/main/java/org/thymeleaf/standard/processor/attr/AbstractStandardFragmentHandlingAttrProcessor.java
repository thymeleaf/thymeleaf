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
package org.thymeleaf.standard.processor.attr;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractFragmentHandlingAttrProcessor;
import org.thymeleaf.standard.fragment.StandardFragment;
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
    protected final List<Node> computeFragment(
            final Arguments arguments, final Element element, final String attributeName, final String attributeValue) {

        final String dialectPrefix = Attribute.getPrefixFromAttributeName(attributeName);

        final String fragmentSignatureAttributeName =
                getFragmentSignatureUnprefixedAttributeName(arguments, element, attributeName, attributeValue);

        final StandardFragment fragment =
                StandardFragmentProcessor.computeStandardFragmentSpec(
                        arguments.getConfiguration(), arguments, attributeValue, dialectPrefix, fragmentSignatureAttributeName);

        final List<Node> extractedNodes =
                fragment.extractFragment(arguments.getConfiguration(), arguments, arguments.getTemplateRepository());

        final boolean removeHostNode = getRemoveHostNode(arguments, element, attributeName, attributeValue);

        // If fragment is a whole document (no selection inside), we should never remove its parent node/s
        // Besides, we know that StandardFragmentProcessor.computeStandardFragmentSpec only creates two types of
        // IFragmentSpec objects: WholeFragmentSpec and DOMSelectorFragmentSpec.
        final boolean isWholeDocument = (fragment.getFragmentSpec() instanceof WholeFragmentSpec);

        if (extractedNodes == null || removeHostNode || isWholeDocument) {
            return extractedNodes;
        }

        // Host node is NOT to be removed, therefore what should be removed is the top-level elements of the returned
        // nodes.

        final Element containerElement = new Element("container");

        for (final Node extractedNode : extractedNodes) {
            // This is done in this indirect way in order to preserver internal structures like e.g. local variables.
            containerElement.addChild(extractedNode);
            containerElement.extractChild(extractedNode);
        }

        final List<Node> extractedChildren = containerElement.getChildren();
        containerElement.clearChildren();

        return extractedChildren;

    }



    protected abstract String getFragmentSignatureUnprefixedAttributeName(
            final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue);


}
