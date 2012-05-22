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

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private final String removeTag; 
    private final String removeBody; 
    
    
    public AbstractRemovalAttrProcessor() {
        
        super();
        
        this.removeAll = getRemoveAllAttrValue();
        this.removeTag = getRemoveTagAttrValue();
        this.removeBody = getRemoveBodyAttrValue();
        
        Validate.notEmpty(this.removeAll, "Attribute value for \"remove all\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeTag, "Attribute value for \"remove tag\" cannot be null or empty in processor " + this.getClass().getName());
        Validate.notEmpty(this.removeBody, "Attribute value for \"remove body\" cannot be null or empty in processor " + this.getClass().getName());
        
        Validate.isTrue(!this.removeAll.equals(this.removeTag), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeAll.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        Validate.isTrue(!this.removeTag.equals(this.removeBody), "All three attribute values for processor " + this.getClass().getName() + " must be different");
        
    }




    
    
    public final AttrProcessResult process(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute) {

        String value = attribute.getValue();
        if (value != null) {
            value = value.trim();
            if (this.removeAll.equals(value)) {
                return AttrProcessResult.REMOVE_TAG_AND_CHILDREN;
            }
            if (this.removeTag.equals(value)) {
                return AttrProcessResult.REMOVE_TAG;
            }
            if (this.removeBody.equals(value)) {
                return AttrProcessResult.REMOVE_CHILDREN;
            }
        }
        
        throw new AttrProcessorException(
                "Unrecognized value for \"" + attribute.getName() + "\": " +
        		"only \"" + this.removeTag + "\", \"" + this.removeBody + "\" and \"" + this.removeAll + "\" are allowed.");
        
    }


    protected abstract String getRemoveAllAttrValue();
    
    protected abstract String getRemoveBodyAttrValue();
    
    protected abstract String getRemoveTagAttrValue();

    
}
