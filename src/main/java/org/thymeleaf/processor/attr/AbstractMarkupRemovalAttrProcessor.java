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

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public abstract class AbstractMarkupRemovalAttrProcessor
        extends AbstractAttrProcessor {



    protected static enum RemovalType { ALL, ALLBUTFIRST, ELEMENT, BODY, NONE }



    protected AbstractMarkupRemovalAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }



    protected AbstractMarkupRemovalAttrProcessor(final String attributeName) {
        super(attributeName);
    }




    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {

        final RemovalType removalType = getRemovalType(arguments, element, attributeName);

        if (removalType == null) {
            return ProcessorResult.OK;
        }

        switch (removalType) {

            case NONE:
                return ProcessorResult.OK;

            case ALL:
                element.getParent().removeChild(element);
                return ProcessorResult.OK;

            case ALLBUTFIRST:
                final List<Node> children = element.getChildren();
                final List<Node> newChildren = new ArrayList<Node>(children.size());
                boolean childElementFound = false;
                for (final Node child : children) {
                    if (child instanceof Element) {
                        if (!childElementFound) {
                            newChildren.add(child);
                            childElementFound = true;
                        }
                    } else {
                        newChildren.add(child);
                    }
                }
                element.setChildren(newChildren);
                element.removeAttribute(attributeName);
                return ProcessorResult.OK;

            case ELEMENT:
                element.getParent().extractChild(element);
                return ProcessorResult.OK;

            case BODY:
                element.clearChildren();
                element.removeAttribute(attributeName);
                return ProcessorResult.OK;

        }

        return ProcessorResult.OK;

    }


    protected abstract RemovalType getRemovalType(final Arguments arguments, final Element element, final String attributeName);

}
