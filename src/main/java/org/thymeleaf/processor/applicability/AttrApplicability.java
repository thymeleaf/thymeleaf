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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.util.SetUtils;
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
public final class AttrApplicability {
    
    private final String attrName;
    private final IApplicabilityFilter applicabilityFilter;
    
    

    
    public static Set<AttrApplicability> createSetForAttrName(final String attrName) {
        return SetUtils.singletonSet(new AttrApplicability(attrName));
    }

    
    public static Set<AttrApplicability> createSetForAttrNames(final String[] attrNames) {
        return createSetForAttrNames(Arrays.asList(attrNames));
    }
    
    
    public static Set<AttrApplicability> createSetForAttrNames(final Collection<String> attrNames) {
        final Set<AttrApplicability> applicabilities = new HashSet<AttrApplicability>();
        for (final String attrName : attrNames) {
            applicabilities.add(new AttrApplicability(attrName));
        }
        return applicabilities;
    }
    
    
    
    public AttrApplicability(final String attrName) {
        super();
        Validate.notEmpty(attrName, "Attribute name cannot be null");
        this.attrName = attrName;
        this.applicabilityFilter = null;
    }

    
    public AttrApplicability(final String attrName, final IApplicabilityFilter applicabilityFilter) {
        super();
        Validate.notEmpty(attrName, "Attribute name cannot be null");
        Validate.notNull(applicabilityFilter, "Applicability filter cannot be null");
        this.attrName = attrName;
        this.applicabilityFilter = applicabilityFilter;
    }
    
    
    public String getAttrName() {
        return this.attrName;
    }
    
    public IApplicabilityFilter getApplicabilityFilter() {
        return this.applicabilityFilter;
    }

    
    public boolean hasFilter() {
        return this.applicabilityFilter != null;
    }
    
    

    public boolean isFilterApplicableToAttribute(final Element element, final Attr attribute) {
        if (this.applicabilityFilter == null) {
            return true;
        }
        return this.applicabilityFilter.isApplicableToAttribute(element, attribute);
    }
    
    
    public String getFilterStringRepresentation() {
        if (this.applicabilityFilter == null) {
            return "";
        }
        return this.applicabilityFilter.getStringRepresentation();
    }
    


    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.applicabilityFilter == null) ? 0 : this.applicabilityFilter.hashCode());
        result = prime * result + this.attrName.hashCode();
        return result;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AttrApplicability other = (AttrApplicability) obj;
        if (this.applicabilityFilter == null) {
            if (other.applicabilityFilter != null) {
                return false;
            }
        } else if (!this.applicabilityFilter.equals(other.applicabilityFilter)) {
            return false;
        }
        return this.attrName.equals(other.attrName);
    }
    
}
