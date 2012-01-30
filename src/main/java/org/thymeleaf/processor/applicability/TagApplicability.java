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
import org.w3c.dom.Element;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class TagApplicability {
    
    private final String tagName;
    private final IApplicabilityFilter applicabilityFilter;
    
    
    
    
    
    public static Set<TagApplicability> createSetForTagName(final String tagName) {
        return SetUtils.singletonSet(new TagApplicability(tagName));
    }

    
    public static Set<TagApplicability> createSetForTagNames(final String[] tagNames) {
        return createSetForTagNames(Arrays.asList(tagNames));
    }
    
    
    public static Set<TagApplicability> createSetForTagNames(final Collection<String> tagNames) {
        final Set<TagApplicability> applicabilities = new HashSet<TagApplicability>();
        for (final String tagName : tagNames) {
            applicabilities.add(new TagApplicability(tagName));
        }
        return applicabilities;
    }

    
    
    public TagApplicability(final String tagName) {
        super();
        Validate.notEmpty(tagName, "Tag name cannot be null");
        this.tagName = tagName;
        this.applicabilityFilter = null;
    }

    
    public TagApplicability(final String tagName, final IApplicabilityFilter applicabilityFilter) {
        super();
        Validate.notEmpty(tagName, "Tag name cannot be null");
        Validate.notNull(applicabilityFilter, "Applicability filter cannot be null");
        this.tagName = tagName;
        this.applicabilityFilter = applicabilityFilter;
    }
    
    
    
    public String getTagName() {
        return this.tagName;
    }
    
    public IApplicabilityFilter getApplicabilityFilter() {
        return this.applicabilityFilter;
    }

    
    public boolean hasFilter() {
        return this.applicabilityFilter != null;
    }

    

    public boolean isFilterApplicableToTag(final Element element) {
        if (this.applicabilityFilter == null) {
            return true;
        }
        return this.applicabilityFilter.isApplicableToTag(element);
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
        result = prime * result + this.tagName.hashCode();
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
        final TagApplicability other = (TagApplicability) obj;
        if (this.applicabilityFilter == null) {
            if (other.applicabilityFilter != null) {
                return false;
            }
        } else if (!this.applicabilityFilter.equals(other.applicabilityFilter)) {
            return false;
        }
        return this.tagName.equals(other.tagName);
    }
    
    
}
