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
public final class NotApplicabilityFilter implements IApplicabilityFilter {
    
    private final IApplicabilityFilter applicability;
    
    
    public NotApplicabilityFilter(final IApplicabilityFilter applicability) {
        super();
        Validate.notNull(applicability, "Applicability cannot be null");
        this.applicability = applicability;
    }
    
    public IApplicabilityFilter getApplicability() {
        return this.applicability;
    }


    public boolean isApplicableToAttribute(final Element element, final Attr attribute) {
        return ! this.applicability.isApplicableToAttribute(element, attribute);
    }

    
    public boolean isApplicableToTag(final Element element) {
        return ! this.applicability.isApplicableToTag(element);
    }
    
    
    
    public String getStringRepresentation() {
        return "not " + this.applicability;
    }

    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
}
