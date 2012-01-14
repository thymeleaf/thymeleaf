/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractRemovalAttrProcessor 
        extends AbstractAttrProcessor {
    
    
    
    
    private final String removeAll; 
    private final String removeAllButFirst; 
    private final String removeTag; 
    private final String removeBody; 
    
    
    
    
    
    
    public AbstractRemovalAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        
        super(matcher);
        
        this.removeAll = getRemoveAllAttrValue();
        this.removeAllButFirst = getRemoveAllButFirstAttrValue();
        this.removeTag = getRemoveTagAttrValue();
        this.removeBody = getRemoveBodyAttrValue();
        
        validateValues();
        
    }






    public AbstractRemovalAttrProcessor(final String attributeName) {
        
        super(attributeName);
        
        this.removeAll = getRemoveAllAttrValue();
        this.removeAllButFirst = getRemoveAllButFirstAttrValue();
        this.removeTag = getRemoveTagAttrValue();
        this.removeBody = getRemoveBodyAttrValue();
        
        validateValues();
        
    }






    private void validateValues() {
        
        Validate.notEmpty(this.removeAll, "Attribute value for \"remove all\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeAllButFirst, "Attribute value for \"remove all but first\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeTag, "Attribute value for \"remove tag\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeBody, "Attribute value for \"remove body\" cannot be null or empty in processor " + this.getClass().getName());
        
        Validate.isTrue(!this.removeAll.equals(this.removeTag), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeAll.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeTag.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        
    }




    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Tag tag, final String attributeName) {

        final String attributeValue = tag.getAttributeValue(attributeName);
        if (attributeValue != null) {
            final String value = attributeValue.trim();
            if (this.removeAll.equals(value)) {
                tag.getParent().removeChild(tag);
                return ProcessorResult.OK;
            }
            if (this.removeAllButFirst.equals(value)) {
                final List<Node> newChildren = new ArrayList<Node>();
                final List<Node> children = tag.getChildren();
                final int childrenLen = children.size();
                int childTagsFound = 0;
                for (int i = 0; i < childrenLen && childTagsFound < 2; i++) {
                    final Node child = children.get(i);
                    if (child instanceof Tag) {
                        childTagsFound++;
                        if (childTagsFound == 1) {
                            newChildren.add(child);
                        }
                    } else {
                        newChildren.add(child);
                    }
                }
                tag.setChildren(newChildren.toArray(new Node[newChildren.size()]));
                tag.removeAttribute(attributeName);
                return ProcessorResult.OK;
            }
            if (this.removeTag.equals(value)) {
                tag.getParent().extractChild(tag);
                return ProcessorResult.OK;
            }
            if (this.removeBody.equals(value)) {
                tag.setChildren(null);
                tag.removeAttribute(attributeName);
                return ProcessorResult.OK;
            }
        }
        
        throw new AttrProcessorException(
                "Unrecognized value for \"" + attributeName + "\": " +
        		"only \"" + this.removeTag + "\", \"" + this.removeBody + "\" and \"" + this.removeAll + "\" are allowed.");
        
    }


    protected abstract String getRemoveAllAttrValue();

    protected abstract String getRemoveAllButFirstAttrValue();
    
    protected abstract String getRemoveBodyAttrValue();
    
    protected abstract String getRemoveTagAttrValue();

    
}
