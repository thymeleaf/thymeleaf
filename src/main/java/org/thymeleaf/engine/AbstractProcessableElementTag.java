/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.PrecedenceProcessorComparator;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractProcessableElementTag
        extends AbstractElementTag implements IProcessableElementTag {


    protected ElementAttributes elementAttributes;

    // Should actually be a set, but given we will need to sort it very often, a list is more handy. Dialect constraints
    // ensure anyway that we will never have duplicates here, because the same processor can never be applied to more than
    // one attribute.
    protected List<IElementProcessor> associatedProcessors = null;
    protected int associatedProcessorsAttributesVersion = Integer.MIN_VALUE; // This ensures a recompute will be performed immediately



    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    AbstractProcessableElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions);
        this.elementAttributes = new ElementAttributes(this.templateMode, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    AbstractProcessableElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, elementName);
        this.elementAttributes = new ElementAttributes(this.templateMode, attributeDefinitions);
    }



    // Meant to be called only from the cloning infrastructure
    protected AbstractProcessableElementTag() {
        super();
    }



    public final IElementAttributes getAttributes() {
        return this.elementAttributes;
    }









    public final void precomputeAssociatedProcessors() {
        if (this.associatedProcessorsAttributesVersion == Integer.MIN_VALUE || this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
    }


    public final boolean hasAssociatedProcessors() {
        if (this.associatedProcessorsAttributesVersion == Integer.MIN_VALUE || this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
        return this.associatedProcessors != null && !this.associatedProcessors.isEmpty();
    }


    public final List<IElementProcessor> getAssociatedProcessorsInOrder() {
        if (this.associatedProcessorsAttributesVersion == Integer.MIN_VALUE || this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
        return (this.associatedProcessors != null? this.associatedProcessors : Collections.EMPTY_LIST);
    }




    void recomputeProcessors() {

        // Something has changed (usually the processors associated with the attributes) so we need to recompute

        if (this.associatedProcessors != null) {
            this.associatedProcessors.clear();
        }

        if (this.elementDefinition.hasAssociatedProcessors) {

            if (this.associatedProcessors == null) {
                this.associatedProcessors = new ArrayList<IElementProcessor>(4);
            }

            this.associatedProcessors.addAll(this.elementDefinition.associatedProcessors);

        }

        int n = this.elementAttributes.attributesSize;
        while (n-- != 0) {

            if (!this.elementAttributes.attributes[n].definition.hasAssociatedProcessors) {
                continue;
            }

            if (this.associatedProcessors == null) {
                this.associatedProcessors = new ArrayList<IElementProcessor>(4);
            }

            for (final IElementProcessor associatedProcessor : this.elementAttributes.attributes[n].definition.associatedProcessors) {

                // We should never have duplicates. The same attribute can never appear twice in an element (parser
                // restrictions + the way this class's 'setAttribute' works), plus a specific processor instance can
                // never appear in more than one dialect, nor be applied to more than one attribute name.

                // Now for each processor, before adding it to the list, we must first determine whether it requires
                // a specific element name and, if so, confirm that it is the same as the name of the element these
                // attributes live at.
                final MatchingElementName matchingElementName = associatedProcessor.getMatchingElementName();
                if (matchingElementName != null && !matchingElementName.matches(this.elementDefinition.elementName)) {
                    continue;
                }

                // Just add the processor to the list
                this.associatedProcessors.add(associatedProcessor);

            }

        }

        if (this.associatedProcessors != null) {
            Collections.sort(this.associatedProcessors, PrecedenceProcessorComparator.INSTANCE);
        }

    }



    protected void resetProcessableElementTag(
            final String elementName,
            final int line, final int col) {

        resetElementTag(elementName, line, col);
        this.elementAttributes.clearAll();
        this.associatedProcessorsAttributesVersion = Integer.MIN_VALUE;

    }





    protected void resetAsCloneOfProcessableElementTag(final AbstractProcessableElementTag original) {
        super.resetAsCloneOfElementTag(original);
        if (this.elementAttributes == null) {
            this.elementAttributes = original.elementAttributes.cloneElementAttributes();
        } else {
            this.elementAttributes.resetAsCloneOf(original.elementAttributes); // not the same as cloning the ElementAttributes object, because we want
        }
        this.associatedProcessors =
                (original.associatedProcessors == null ? null : new ArrayList<IElementProcessor>(original.associatedProcessors)); // It's mutable, so we have to copy the list
        this.associatedProcessorsAttributesVersion = original.associatedProcessorsAttributesVersion;
    }


}
