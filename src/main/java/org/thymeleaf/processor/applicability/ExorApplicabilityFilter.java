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
public final class ExorApplicabilityFilter implements IApplicabilityFilter {
    
    private final IApplicabilityFilter left;
    private final IApplicabilityFilter right;
    
    
    public ExorApplicabilityFilter(final IApplicabilityFilter left, final IApplicabilityFilter right) {
        super();
        Validate.notNull(left, "Left side cannot be null");
        Validate.notNull(right, "Right side cannot be null");
        this.left = left;
        this.right = right;
    }
    
    public IApplicabilityFilter getLeft() {
        return this.left;
    }

    public IApplicabilityFilter getRight() {
        return this.right;
    }


    public boolean isApplicableToAttribute(final Element element, final Attr attribute) {
        if (this.left.isApplicableToAttribute(element, attribute)) {
            return ! this.right.isApplicableToAttribute(element, attribute);
        }
        return this.right.isApplicableToAttribute(element, attribute);
    }

    
    public boolean isApplicableToTag(final Element element) {
        if (this.left.isApplicableToTag(element)) {
            return ! this.right.isApplicableToTag(element);
        }
        return this.right.isApplicableToTag(element);
    }
    
    
    
    public String getStringRepresentation() {
        return this.left + " exor " + this.right;
    }

    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
}
