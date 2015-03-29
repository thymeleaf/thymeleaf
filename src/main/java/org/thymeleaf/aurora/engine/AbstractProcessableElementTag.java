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
package org.thymeleaf.aurora.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.TemplateProcessingException;

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
    protected List<IProcessor> associatedProcessors = null;
    protected int associatedProcessorsAttributesVersion = Integer.MIN_VALUE; // This ensures a recompute will be performed immediately


    // This allows the implementation of a (package-protected) iteration system that allows the possibility to track which
    // processors have been already executed and which haven't, even if we have changed the processors in the middle.
    // This can be implemented via the getAssociatedProcessors() call, which is public, but it wouldn't be as performant as
    // this way, which is meant to be used only from inside the engine itself.
    private IProcessor[] processorIteratorProcessors = null;
    private boolean[] processorIteratorVisited = null;
    private int processorIteratorSize = 0;
    private int processorIteratorCurrent = -1;
    private int processorIteratorAttibutesVersion = Integer.MIN_VALUE;



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






    @Override
    protected void reset(
            final String elementName,
            final int line, final int col) {

        super.reset(elementName, line, col);
        this.elementAttributes.clearAll();

    }







    public final boolean hasAssociatedProcessors() {
        if (this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
        return this.associatedProcessors != null && !this.associatedProcessors.isEmpty();
    }


    public final List<IProcessor> getAssociatedProcessors() {
        if (this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }
        return (this.associatedProcessors != null? this.associatedProcessors : Collections.EMPTY_LIST);
    }




    private void recomputeProcessors() {

        // Something has changed (usually the processors associated with the attributes) so we need to recompute

        if (this.associatedProcessors != null) {
            this.associatedProcessors.clear();
        }

        if (!this.elementDefinition.associatedProcessors.isEmpty()) {

            if (this.associatedProcessors == null) {
                this.associatedProcessors = new ArrayList<IProcessor>(4);
            }

            this.associatedProcessors.addAll(this.elementDefinition.associatedProcessors);

        }

        int n = this.elementAttributes.attributesSize;
        while (n-- != 0) {

            if (this.elementAttributes.attributes[n].definition.associatedProcessors.isEmpty()) {
                continue;
            }

            if (this.associatedProcessors == null) {
                this.associatedProcessors = new ArrayList<IProcessor>(4);
            }

            for (final IProcessor associatedProcessor : this.elementAttributes.attributes[n].definition.associatedProcessors) {

                // We should never have duplicates. The same attribute can never appear twice in an element (parser
                // restrictions + the way this class's 'setAttribute' works), plus a specific processor instance can
                // never appear in more than one dialect, nor be applied to more than one attribute name.

                // Now for each processor, before adding it to the list, we must first determine whether it requires
                // a specific element name and, if so, confirm that it is the same as the name of the element these
                // attributes live at.
                if (associatedProcessor instanceof IElementProcessor) {
                    final MatchingElementName matchingElementName = ((IElementProcessor)associatedProcessor).getMatchingElementName();
                    if (matchingElementName != null && !matchingElementName.matches(this.elementDefinition.elementName)) {
                        continue;
                    }
                } else if (associatedProcessor instanceof INodeProcessor) {
                    final MatchingElementName matchingElementName = ((INodeProcessor)associatedProcessor).getMatchingElementName();
                    if (matchingElementName != null && !matchingElementName.matches(this.elementDefinition.elementName)) {
                        continue;
                    }
                } else {
                    throw new TemplateProcessingException(
                            "Attribute Definition has been set a processor implementing an interface other than " +
                                    IElementProcessor.class + " or " + INodeProcessor.class + ", which is forbidden.");
                }

                // Just add the processor to the list
                this.associatedProcessors.add(associatedProcessor);

            }

        }

        if (this.associatedProcessors != null) {
            Collections.sort(this.associatedProcessors, PrecedenceProcessorComparator.INSTANCE);
        }

    }





    void processorIteratorReset() {

        if (this.elementAttributes.version != this.processorIteratorAttibutesVersion) {
            recomputeProcessorIterator();
            this.processorIteratorAttibutesVersion = this.elementAttributes.version;
        }
        this.processorIteratorCurrent = 0;
        Arrays.fill(this.processorIteratorVisited, false);

    }




    IProcessor processorIteratorNext() {

        if (this.elementAttributes.version != this.processorIteratorAttibutesVersion) {
            recomputeProcessorIterator();
            this.processorIteratorAttibutesVersion = this.elementAttributes.version;
        }

        int n = this.processorIteratorSize;
        int i = 0;
        while (n-- != 0) {
            if (!this.processorIteratorVisited[i++]) {
                return this.processorIteratorProcessors[i - 1];
            }
        }
        return null;

    }



    private void recomputeProcessorIterator() {

        // Before recomputing the iterator itself, we have to make sure that the associated processors are up-to-date
        if (this.elementAttributes.version != this.associatedProcessorsAttributesVersion) {
            recomputeProcessors();
            this.associatedProcessorsAttributesVersion = this.elementAttributes.version;
        }

        if (this.processorIteratorProcessors == null) {

            final int iteratorLen = (this.associatedProcessors == null? 0 : this.associatedProcessors.size());

            this.processorIteratorProcessors = new IProcessor[iteratorLen];
            this.processorIteratorVisited = new boolean[iteratorLen];
            this.processorIteratorCurrent = 0;
            this.processorIteratorSize = iteratorLen;

            for (int i = 0; i < iteratorLen; i++) {
                this.processorIteratorProcessors[i] = this.associatedProcessors.get(i);
                this.processorIteratorVisited[i] = false;
            }

            this.processorIteratorAttibutesVersion = this.associatedProcessorsAttributesVersion;

            return;

        }

        // Processors have changed since the last time we used the iterator (attributes changed)

        this.processorIteratorCurrent = 0;


    }






    protected final void initializeProcessableElementTagClone(final AbstractProcessableElementTag clone) {
        initializeElementTagClone(clone);
        clone.elementAttributes = this.elementAttributes.cloneElementAttributes();
        clone.associatedProcessors = (this.associatedProcessors == null? null : new ArrayList<IProcessor>(this.associatedProcessors));
        clone.associatedProcessorsAttributesVersion = this.associatedProcessorsAttributesVersion;
    }


}
