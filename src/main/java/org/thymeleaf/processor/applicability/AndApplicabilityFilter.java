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
public final class AndApplicabilityFilter implements IApplicabilityFilter {
    
    private final IApplicabilityFilter one;
    private final IApplicabilityFilter two;
    private final IApplicabilityFilter[] other;
    
    
    public AndApplicabilityFilter(final IApplicabilityFilter one, final IApplicabilityFilter two, final IApplicabilityFilter... other) {
        super();
        Validate.notNull(one, "First operand cannot be null");
        Validate.notNull(two, "second operand cannot be null");
        this.one = one;
        this.two = two;
        this.other = other;
    }
    
    public IApplicabilityFilter getOne() {
        return this.one;
    }

    public IApplicabilityFilter getTwo() {
        return this.two;
    }

    public IApplicabilityFilter[] getOther() {
        return this.other;
    }

    private boolean hasOther() {
        return this.other != null && this.other.length > 0;
    }
    

    public boolean isApplicableToAttribute(final Element element, final Attr attribute) {
        if (!this.one.isApplicableToAttribute(element, attribute)) {
            return false;
        }
        if (!this.two.isApplicableToAttribute(element, attribute)) {
            return false;
        }
        if (hasOther()) {
            for (int i = 0; i < this.other.length; i++) {
                if (!this.other[i].isApplicableToAttribute(element, attribute)) {
                    return false;
                }
            }
        }
        return true;
    }

    
    public boolean isApplicableToTag(final Element element) {
        if (!this.one.isApplicableToTag(element)) {
            return false;
        }
        if (!this.two.isApplicableToTag(element)) {
            return false;
        }
        if (hasOther()) {
            for (int i = 0; i < this.other.length; i++) {
                if (!this.other[i].isApplicableToTag(element)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    
    public String getStringRepresentation() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("(");
        strBuilder.append(this.one.getStringRepresentation());
        strBuilder.append(" and ");
        strBuilder.append(this.two.getStringRepresentation());
        if (hasOther()) {
            for (int i = 0; i < this.other.length; i++) {
                strBuilder.append(" and ");
                strBuilder.append(this.other[i].getStringRepresentation());
            }
        }
        strBuilder.append(")");
        return strBuilder.toString();
    }

    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
}
