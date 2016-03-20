/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ProcessorComparators;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractProcessableElementTag
        extends AbstractElementTag implements IProcessableElementTag {


    final Attributes attributes;

    // Dialect constraints ensure anyway that we will never have duplicates here, because the same processor can
    // never be applied to more than one attribute.
    protected IElementProcessor[] associatedProcessors = null;
    protected int associatedProcessorsSize = 0;
    protected int associatedProcessorsAttributesVersion = Integer.MIN_VALUE; // This ensures a recompute will be performed immediately




    AbstractProcessableElementTag(
            final TemplateMode templateMode,
            final String elementName,
            final ElementDefinition elementDefinition,
            final Attributes attributes,
            final boolean synthetic,
            final String templateName,
            final int line,
            final int col) {
        super(templateMode, elementDefinition, elementName, synthetic, templateName, line, col);
        this.attributes = attributes;
    }




    public final boolean hasAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        return this.attributes.hasAttribute(this.templateMode, completeName);
    }


    public final boolean hasAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        return this.attributes.hasAttribute(this.templateMode, prefix, name);
    }


    public final boolean hasAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        return this.attributes.hasAttribute(attributeName);
    }


    public final IAttribute getAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        return this.attributes.getAttribute(this.templateMode, completeName);
    }


    public final IAttribute getAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        return this.attributes.getAttribute(this.templateMode, prefix, name);
    }


    public final IAttribute getAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        return this.attributes.getAttribute(attributeName);
    }




    public abstract AbstractProcessableElementTag setAttribute(final String completeName, final String value);


    public abstract AbstractProcessableElementTag setAttribute(final String completeName, final String value, final AttributeValueQuotes valueQuotes);


    abstract AbstractProcessableElementTag setAttribute(
            final AttributeDefinition attributeDefinition, final String completeName, final String value, final AttributeValueQuotes valueQuotes);




    public abstract AbstractProcessableElementTag replaceAttribute(final AttributeName oldName, final String completeNewName, final String value);


    public abstract AbstractProcessableElementTag replaceAttribute(final AttributeName oldName, final String completeNewName, final String value, final AttributeValueQuotes valueQuotes);


    abstract AbstractProcessableElementTag replaceAttribute(
            final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String completeNewName, final String value, final AttributeValueQuotes valueQuotes);




    public abstract AbstractProcessableElementTag removeAttribute(final String prefix, final String name);


    public abstract AbstractProcessableElementTag removeAttribute(final String completeName);


    public abstract AbstractProcessableElementTag removeAttribute(final AttributeName attributeName);





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
        return this.associatedProcessors != null && this.associatedProcessorsSize > 0;
    }


    public final List<IElementProcessor> getAssociatedProcessorsInOrder() {
        if (this.associatedProcessorsAttributesVersion == Integer.MIN_VALUE || this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
        if (this.associatedProcessors == null || this.associatedProcessorsSize == 0) {
            return Collections.EMPTY_LIST;
        }
        final List<IElementProcessor> associatedProcessorsList = new ArrayList<IElementProcessor>(this.associatedProcessorsSize + 1);
        for (int i = 0; i < this.associatedProcessorsSize; i++) {
            associatedProcessorsList.add(this.associatedProcessors[i]);
        }
        return associatedProcessorsList;
    }




    final void recomputeProcessors() {

        // Something has changed (usually the processors associated with the attributes) so we need to recompute

        this.associatedProcessorsSize = 0;

        if (this.elementDefinition.hasAssociatedProcessors) {
            addAssociatedProcessors(this.elementDefinition.associatedProcessors, this.elementDefinition.associatedProcessors.length);
        }

        int n = this.elementAttributes.attributesSize;
        while (n-- != 0) {

            if (!this.elementAttributes.attributes[n].definition.hasAssociatedProcessors) {
                continue;
            }

            final IElementProcessor[] attributeAssociatedProcessors = this.elementAttributes.attributes[n].definition.associatedProcessors;
            for (int i = 0; i < attributeAssociatedProcessors.length; i++) {

                // We should never have duplicates. The same attribute can never appear twice in an element (parser
                // restrictions + the way this class's 'setAttribute' works), plus a specific processor instance can
                // never appear in more than one dialect, nor be applied to more than one attribute name.

                // Now for each processor, before adding it to the list, we must first determine whether it requires
                // a specific element name and, if so, confirm that it is the same as the name of the element these
                // attributes live at.
                final MatchingElementName matchingElementName = attributeAssociatedProcessors[i].getMatchingElementName();
                if (matchingElementName != null && !matchingElementName.matches(this.elementDefinition.elementName)) {
                    continue;
                }

                // Just add the processor to the list
                addAssociatedProcessor(attributeAssociatedProcessors[i]);

            }

        }

        if (this.associatedProcessors != null) {
            if (this.associatedProcessorsSize > 1) {
                Arrays.sort(this.associatedProcessors, 0, this.associatedProcessorsSize, ProcessorComparators.PROCESSOR_COMPARATOR);
            }
        }

    }


    // We will use this method in the recomputing code instead of adding the new processors directly in order to take
    // care of the length of the array (in case it has to be grown)
    private void addAssociatedProcessor(final IElementProcessor elementProcessor) {

        if (this.associatedProcessors == null || this.associatedProcessorsSize == this.associatedProcessors.length) {
            // We need to grow the structures
            final IElementProcessor[] newAssociatedProcessors =
                    new IElementProcessor[this.associatedProcessorsSize + DEFAULT_ASSOCIATED_PROCESSORS_LENGTH];
            if (this.associatedProcessors != null) {
                System.arraycopy(this.associatedProcessors, 0, newAssociatedProcessors, 0, this.associatedProcessors.length);
            }
            this.associatedProcessors = newAssociatedProcessors;
        }

        this.associatedProcessors[this.associatedProcessorsSize] = elementProcessor;
        this.associatedProcessorsSize++;

    }


    private void addAssociatedProcessors(final IElementProcessor[] elementProcessors, final int elementProcessorsSize) {

        final int requiredLen = this.associatedProcessorsSize + elementProcessorsSize;

        if (this.associatedProcessors == null || requiredLen > this.associatedProcessors.length) {
            // We need to grow the structures
            final IElementProcessor[] newAssociatedProcessors = new IElementProcessor[requiredLen + DEFAULT_ASSOCIATED_PROCESSORS_LENGTH];
            if (this.associatedProcessors != null) {
                System.arraycopy(this.associatedProcessors, 0, newAssociatedProcessors, 0, this.associatedProcessors.length);
            }
            this.associatedProcessors = newAssociatedProcessors;
        }

        System.arraycopy(elementProcessors, 0, this.associatedProcessors, this.associatedProcessorsSize, elementProcessorsSize);
        this.associatedProcessorsSize += elementProcessorsSize;

    }



    


}
