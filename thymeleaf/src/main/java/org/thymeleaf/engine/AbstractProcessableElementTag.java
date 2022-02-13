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
import java.util.Map;

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

    private static final IElementProcessor[] EMPTY_ASSOCIATED_PROCESSORS = new IElementProcessor[0];


    final Attributes attributes;
    // Dialect constraints ensure anyway that we will never have duplicates here, because the same processor can
    // never be applied to more than one attribute.
    private volatile IElementProcessor[] associatedProcessors = null;




    AbstractProcessableElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic);
        this.attributes = attributes;
    }


    AbstractProcessableElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic,
            final String templateName,
            final int line,
            final int col) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic, templateName, line, col);
        this.attributes = attributes;
    }




    public final boolean hasAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(this.templateMode, completeName);
    }


    public final boolean hasAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(this.templateMode, prefix, name);
    }


    public final boolean hasAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(attributeName);
    }


    public final IAttribute getAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(this.templateMode, completeName);
    }


    public final IAttribute getAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(this.templateMode, prefix, name);
    }


    public final IAttribute getAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(attributeName);
    }


    public final String getAttributeValue(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        final Attribute attribute = this.attributes.getAttribute(this.templateMode, completeName);
        return attribute != null? attribute.getValue() : null;
    }


    public final String getAttributeValue(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        final Attribute attribute = this.attributes.getAttribute(this.templateMode, prefix, name);
        return attribute != null? attribute.getValue() : null;
    }


    public final String getAttributeValue(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        final Attribute attribute = this.attributes.getAttribute(attributeName);
        return attribute != null? attribute.getValue() : null;
    }




    public IAttribute[] getAllAttributes() {
        if (this.attributes == null) {
            return Attributes.EMPTY_ATTRIBUTE_ARRAY;
        }
        return this.attributes.getAllAttributes();
    }


    public Map<String,String> getAttributeMap() {
        if (this.attributes == null) {
            return Collections.emptyMap();
        }
        return this.attributes.getAttributeMap();
    }



    IElementProcessor[] getAssociatedProcessors() {

        IElementProcessor[] p = this.associatedProcessors;
        if (p == null) {
            this.associatedProcessors = p = computeProcessors();
        }
        return p;

    }


    boolean hasAssociatedProcessors() {
        return getAssociatedProcessors().length > 0;
    }




    private IElementProcessor[] computeProcessors() {

        final int associatedProcessorCount = (this.attributes != null? this.attributes.getAssociatedProcessorCount() : 0);

        // If there are no processors associated with attributes, this is much easier
        if (this.attributes == null || associatedProcessorCount == 0) {
            return (this.elementDefinition.hasAssociatedProcessors? this.elementDefinition.associatedProcessors : EMPTY_ASSOCIATED_PROCESSORS);
        }

        // At this point we know for sure there are processors associated with attributes
        final int elementProcessorCount =
                (this.elementDefinition.hasAssociatedProcessors? this.elementDefinition.associatedProcessors.length : 0);
        IElementProcessor[] processors = new IElementProcessor[elementProcessorCount + associatedProcessorCount];

        if (elementProcessorCount > 0) {
            System.arraycopy(this.elementDefinition.associatedProcessors, 0, processors, 0, elementProcessorCount);
        }

        int idx = elementProcessorCount;
        int n = this.attributes.attributes.length;
        while (n-- != 0) {

            if (!this.attributes.attributes[n].definition.hasAssociatedProcessors) {
                continue;
            }

            final IElementProcessor[] attributeAssociatedProcessors = this.attributes.attributes[n].definition.associatedProcessors;
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
                processors[idx++] = attributeAssociatedProcessors[i];

            }

        }

        // At the end in some (very few) cases we might have a mismatch because some attribute processors didn't apply
        // due to the element name not matching. In such cases, we need to readjust the array size.
        if (idx < processors.length) {
            processors = Arrays.copyOf(processors, idx);
        }

        if (processors.length > 1) {
            Arrays.sort(processors, ProcessorComparators.PROCESSOR_COMPARATOR);
        }

        return processors;

    }




    abstract AbstractProcessableElementTag setAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeDefinition attributeDefinition, final String completeName,
            final String value, final AttributeValueQuotes valueQuotes);


    abstract AbstractProcessableElementTag replaceAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String completeNewName,
            final String value, final AttributeValueQuotes valueQuotes);


    abstract AbstractProcessableElementTag removeAttribute(final String prefix, final String name);
    abstract AbstractProcessableElementTag removeAttribute(final String completeName);
    abstract AbstractProcessableElementTag removeAttribute(final AttributeName attributeName);



}
