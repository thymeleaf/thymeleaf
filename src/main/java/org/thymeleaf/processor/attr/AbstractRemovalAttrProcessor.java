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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 * @deprecated Deprecated in 2.1.0, in favour of {@link AbstractMarkupRemovalAttrProcessor}. Will be removed in 3.0.
 *
 */
@Deprecated
public abstract class AbstractRemovalAttrProcessor 
        extends AbstractAttrProcessor {
    
    


    private final String removeAll; 
    private final String removeAllButFirst; 
    private final String removeElement; 
    private final String removeBody;






    protected AbstractRemovalAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        
        super(matcher);
        
        this.removeAll = getRemoveAllAttrValue();
        this.removeAllButFirst = getRemoveAllButFirstAttrValue();
        this.removeElement = getRemoveElementAttrValue();
        this.removeBody = getRemoveBodyAttrValue();
        
        validateValues();
        
    }






    protected AbstractRemovalAttrProcessor(final String attributeName) {
        
        super(attributeName);
        
        this.removeAll = getRemoveAllAttrValue();
        this.removeAllButFirst = getRemoveAllButFirstAttrValue();
        this.removeElement = getRemoveElementAttrValue();
        this.removeBody = getRemoveBodyAttrValue();
        
        validateValues();
        
    }






    private void validateValues() {
        
        Validate.notEmpty(this.removeAll, "Attribute value for \"remove all\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeAllButFirst, "Attribute value for \"remove all but first\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeElement, "Attribute value for \"remove element\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeBody, "Attribute value for \"remove body\" cannot be null or empty in processor " + this.getClass().getName());
        
        Validate.isTrue(!this.removeAll.equals(this.removeElement), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeAll.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeElement.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        
    }




    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        if (attributeValue != null) {
            final String value = attributeValue.trim();
            if (this.removeAll.equals(value)) {
                element.getParent().removeChild(element);
                return ProcessorResult.OK;
            }
            if (this.removeAllButFirst.equals(value)) {
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
            }
            if (this.removeElement.equals(value)) {
                element.getParent().extractChild(element);
                return ProcessorResult.OK;
            }
            if (this.removeBody.equals(value)) {
                element.clearChildren();
                element.removeAttribute(attributeName);
                return ProcessorResult.OK;
            }
        }
        
        throw new TemplateProcessingException(
                "Unrecognized value for \"" + attributeName + "\": " +
        		"only \"" + this.removeElement + "\", \"" + this.removeBody + "\" and \"" + this.removeAll + "\" are allowed.");
        
    }


    protected abstract String getRemoveAllAttrValue();

    protected abstract String getRemoveAllButFirstAttrValue();
    
    protected abstract String getRemoveBodyAttrValue();
    
    protected abstract String getRemoveElementAttrValue();

    
}
