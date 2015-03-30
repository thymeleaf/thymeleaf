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

        if (this.elementDefinition.hasAssociatedProcessors) {

            if (this.associatedProcessors == null) {
                this.associatedProcessors = new ArrayList<IProcessor>(4);
            }

            this.associatedProcessors.addAll(this.elementDefinition.associatedProcessors);

        }

        int n = this.elementAttributes.attributesSize;
        while (n-- != 0) {

            if (!this.elementAttributes.attributes[n].definition.hasAssociatedProcessors) {
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




    public IProcessorIterator getAssociatedProcessorsIterator() {
        return new ProcessorIterator(this);
    }






    protected final void initializeProcessableElementTagClone(final AbstractProcessableElementTag clone) {
        initializeElementTagClone(clone);
        clone.elementAttributes = this.elementAttributes.cloneElementAttributes();
        clone.associatedProcessors = (this.associatedProcessors == null? null : new ArrayList<IProcessor>(this.associatedProcessors));
        clone.associatedProcessorsAttributesVersion = this.associatedProcessorsAttributesVersion;
    }





    /*
     * This class will take care of iterating the processors in the most optimal way possible. It allows the attributes
     * in the tag to be modified durint iteration, taking new processors into account as soon as they appear, even if
     * they have higher precedence than the last executed processor for the same tag.
     */
    private static final class ProcessorIterator implements IProcessorIterator {

        private final AbstractProcessableElementTag tag;

        // These are the structures used to keep track of the iterated processors, as well as whether they have
        // been visited or not.
        private IProcessor[] processors = null;
        private boolean[] visited = null;
        private int size = 0;

        // These structures are used when we need to recompute already-existing structures, in order to reduce
        // the total amount of processor arrays created during normal operation (attributes might change a lot).
        private IProcessor[] auxProcessors = null;
        private boolean[] auxVisited = null;
        private int auxSize = 0;

        // The version, used to keep track of the tag's attributes and knowing when we have to recompute
        private int attributesVersion = Integer.MIN_VALUE;


        private ProcessorIterator(final AbstractProcessableElementTag tag) {
            super();
            this.tag = tag;
        }



        public IProcessor next() {

            if (this.tag.elementAttributes.version != this.attributesVersion) {
                recompute();
                this.attributesVersion = this.tag.elementAttributes.version;
            }

            // We use 'last' as a starting index in order save some iterations
            int n = this.size;
            int i = 0;
            while (n-- != 0) {
                if (!this.visited[i]) {
                    this.visited[i] = true;
                    return this.processors[i];
                }
                i++;
            }
            return null;

        }


        private void recompute() {

            // Before recomputing the iterator itself, we have to make sure that the associated processors are up-to-date
            if (this.tag.elementAttributes.version != this.tag.associatedProcessorsAttributesVersion) {
                this.tag.recomputeProcessors();
                this.tag.associatedProcessorsAttributesVersion = this.tag.elementAttributes.version;
            }


            if (this.tag.associatedProcessors == null || this.tag.associatedProcessors.isEmpty()) {
                // After recompute, it seems we have no processors to be applied (we might have had before)

                if (this.processors != null) {
                    // We don't mind what we had in the arrays - setting size to 0 will invalidate them
                    this.size = 0;
                } // else there's nothing to do -- we had nothing precomputed, and will still have the same nothing

                return;

            }


            if (this.processors == null) {
                // We had nothing precomputed, but there are associated processors now!

                this.size = this.tag.associatedProcessors.size();
                this.processors = new IProcessor[this.size];
                this.visited = new boolean[this.size];

                this.tag.associatedProcessors.toArray(this.processors); // No need to assign to anything
                Arrays.fill(this.visited, false);

                return;

            }

            // Processors have changed since the last time we used the iterator (attributes changed)

            this.auxSize = this.tag.associatedProcessors.size();
            if (this.auxProcessors == null || this.auxSize > this.auxProcessors.length) {
                // We need new aux arrays (either don't exist, or they are too small)
                this.auxProcessors = new IProcessor[this.auxSize];
                this.auxVisited = new boolean[this.auxSize];
            }

            this.tag.associatedProcessors.toArray(this.auxProcessors); // No need to assign to anything
            // No pre-initialization for the visited array -- we will do it position by position

            // Now we should check the matches between the new and the old iterator processors - we will build
            // on the fact that processors are always ordered by precedence
            int i = 0; // index for the NEW processors
            int j = 0; // index for the OLD processors
            while (i < this.auxSize) {

                if (i >= this.size) {
                    // We know everything in the new array from here on has to be new
                    Arrays.fill(this.auxVisited, i, this.auxSize, false);
                    break;
                }

                if (this.auxProcessors[i] == this.processors[j]) {
                    this.auxVisited[i] = this.visited[j];
                    i++;
                    j++;
                    continue;
                }

                // Doesn't match. Either we have a new processor, or an previous one was removed

                final int comp =
                        PrecedenceProcessorComparator.INSTANCE.compare(this.auxProcessors[i], this.processors[j]);

                if (comp == 0) {
                    // This should never happen. The comparator should make sure the only case in which comp == 0 is when
                    // processors are the same (i.e. the object)

                    throw new IllegalStateException(
                            "Two different registered processors have returned zero as a result of their " +
                                    "comparison, which is forbidden. Offending processors are " +
                                    this.auxProcessors[i].getClass().getName() + " and " +
                                    this.processors[j].getClass().getName());

                } else if (comp < 0) {
                    // The new one has higher precedence (lower value), so it's new

                    this.auxVisited[i] = false; // We need to execute this for sure! (it's new)
                    i++;
                    continue;

                } else { // comp > 0
                    // The old one has higher precedence (lower value), so it's been removed -- just skip
                    j++;
                    continue;

                }

            }

            // Finally, just swap the arrays
            final IProcessor[] swapProcessors = this.auxProcessors;
            final boolean[] swapVisited = this.auxVisited;
            this.auxProcessors = this.processors;
            this.auxVisited = this.visited;
            this.processors = swapProcessors;
            this.visited = swapVisited;
            this.size = this.auxSize;

        }


    }


}
