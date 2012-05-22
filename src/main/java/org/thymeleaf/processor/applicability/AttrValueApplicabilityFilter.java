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
package org.thymeleaf.processor.applicability;

import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class AttrValueApplicabilityFilter implements IApplicabilityFilter {
    
    private final String attrName;
    private final String attrValue;
    
    
    public AttrValueApplicabilityFilter(final String attrName, final String attrValue) {
        super();
        Validate.notNull(attrName, "Attribute name cannot be null");
        this.attrName = attrName;
        this.attrValue = attrValue;
    }
    
    public String getAttrName() {
        return this.attrName;
    }

    public String getAttrValue() {
        return this.attrValue;
    }



    public boolean isApplicableToAttribute(final Element element, final Attr attribute) {
        return isApplicableToTag(element);
    }

    
    public boolean isApplicableToTag(final Element element) {
        
        final Attr attr = DOMUtils.findAttribute(this.attrName, element);
        if (attr == null) {
            return (this.attrValue == null);
        }
        
        final String attribValue = attr.getValue();
        if (attribValue == null) {
            return (this.attrValue == null);
        }
        
        return this.attrValue.equals(attribValue);
        
    }
    
    
    
    public String getStringRepresentation() {
        return this.attrName + "='" + this.attrValue + "'";
    }
    
    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    


}
