/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.util.ProcessorComparators;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class ElementDefinition {

    final ElementName elementName;
    private final Set<IElementProcessor> associatedProcessorsSet;
    final IElementProcessor[] associatedProcessors; // The internal representation is an array for better performance
    final boolean hasAssociatedProcessors;


    ElementDefinition(final ElementName elementName, final Set<IElementProcessor> associatedProcessors) {

        super();

        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        if (associatedProcessors == null) {
            throw new IllegalArgumentException("Associated processors cannot be null");
        }

        this.elementName = elementName;
        this.associatedProcessorsSet = Collections.unmodifiableSet(associatedProcessors);
        this.associatedProcessors = new IElementProcessor[this.associatedProcessorsSet.size()];
        int i = 0;
        for (final IElementProcessor processor : this.associatedProcessorsSet) {
            this.associatedProcessors[i++] = processor;
        }
        Arrays.sort(this.associatedProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        this.hasAssociatedProcessors = this.associatedProcessors.length > 0;

    }


    public final ElementName getElementName() {
        return this.elementName;
    }




    public boolean hasAssociatedProcessors() {
        return this.hasAssociatedProcessors;
    }



    public Set<IElementProcessor> getAssociatedProcessors() {
        return this.associatedProcessorsSet;
    }



    public final String toString() {
        return getElementName().toString();
    }



    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!o.getClass().equals(this.getClass())) {
            return false;
        }

        final ElementDefinition that = (ElementDefinition) o;

        if (!this.elementName.equals(that.elementName)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        return this.elementName.hashCode();
    }

}
