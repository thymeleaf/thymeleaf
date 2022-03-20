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

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.util.ProcessorComparators;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class ElementProcessorIterator {

    /*
     * This class will take care of iterating the processors in the most optimal way possible. It allows the attributes
     * in the tag to be modified durint iteration, taking new processors into account as soon as they appear, even if
     * they have higher precedence than the last executed processor for the same tag.
     */


    private int last = -1;

    // These are the structures used to keep track of the iterated processors, as well as whether they have
    // been visited or not.
    private IElementProcessor[] processors = null;
    private boolean[] visited = null;
    private int size = 0;

    // These structures are used when we need to recompute already-existing structures, in order to reduce
    // the total amount of processor arrays created during normal operation (attributes might change a lot).
    private IElementProcessor[] auxProcessors = null;
    private boolean[] auxVisited = null;
    private int auxSize = 0;

    // The version, used to keep track of the tag's attributes and knowing when we have to recompute
    private AbstractProcessableElementTag currentTag = null;

    // This flag will determine if we should return the last processor that we have already returned, or if we just did
    private boolean lastToBeRepeated = false;
    private boolean lastWasRepeated = false;



    ElementProcessorIterator() {
        super();
    }



    void reset() {
        this.size = 0;
        this.last = -1;
        this.currentTag = null;
        this.lastToBeRepeated = false;
        this.lastWasRepeated = false;
    }


    IElementProcessor next(final AbstractProcessableElementTag tag) {

        // It should never happen that after calling 'setLastToBeRepeated' we change tag or modify it before
        // calling 'next' again, so we are fine checking this flag before checking for recomputes
        if (this.lastToBeRepeated) {
            final IElementProcessor repeatedLast = computeRepeatedLast(tag);
            this.lastToBeRepeated = false;
            this.lastWasRepeated = true;
            return repeatedLast;
        }

        this.lastWasRepeated = false;

        if (this.currentTag != tag) { // tags are immutable, so we will use them as a marker of being updated
            recompute(tag);
            this.currentTag = tag;
            this.last = -1;
        }

        if (this.processors == null) {
            return null;
        }

        // We use 'last' as a starting index in order save some iterations (except after recomputes)
        int i = this.last + 1;
        int n = this.size - i;
        while (n-- != 0) {
            if (!this.visited[i]) {
                this.visited[i] = true;
                this.last = i;
                return this.processors[i];
            }
            i++;
        }
        this.last = this.size;
        return null;

    }


    private IElementProcessor computeRepeatedLast(final AbstractProcessableElementTag tag) {

        if (this.currentTag != tag) {
            throw new TemplateProcessingException("Cannot return last processor to be repeated: changes were made and processor recompute is needed!");
        }

        if (this.processors == null) {
            throw new TemplateProcessingException("Cannot return last processor to be repeated: no processors in tag!");
        }

        return this.processors[this.last];

    }


    boolean lastWasRepeated() {
        return this.lastWasRepeated;
    }



    void setLastToBeRepeated(final AbstractProcessableElementTag tag) {

        if (this.currentTag != tag) {
            throw new TemplateProcessingException("Cannot set last processor to be repeated: processor recompute is needed!");
        }

        if (this.processors == null) {
            throw new TemplateProcessingException("Cannot set last processor to be repeated: no processors in tag!");
        }

        this.lastToBeRepeated = true;

    }


    private void recompute(final AbstractProcessableElementTag tag) {

        // Before recomputing the iterator itself, we have to make sure that the associated processors are up-to-date
        final IElementProcessor[] associatedProcessors = tag.getAssociatedProcessors();

        if (associatedProcessors.length == 0) {
            // After recompute, it seems we have no processors to be applied (we might have had before)

            if (this.processors != null) {
                // We don't mind what we had in the arrays - setting size to 0 will invalidate them
                this.size = 0;
            } // else there's nothing to do -- we had nothing precomputed, and will still have the same nothing

            return;

        }


        if (this.processors == null) {
            // We had nothing precomputed, but there are associated processors now!

            this.size = associatedProcessors.length;
            this.processors = new IElementProcessor[Math.max(this.size, 4)]; // minimum size = 4
            this.visited = new boolean[Math.max(this.size, 4)]; // minimum size = 4

            System.arraycopy(associatedProcessors, 0, this.processors, 0, this.size);
            Arrays.fill(this.visited, false);

            return;

        }

        // Processors have changed since the last time we used the iterator (attributes changed),
        // so we need to use the 'aux' structures in order to recompute processors and then swap.

        this.auxSize = associatedProcessors.length;
        if (this.auxProcessors == null || this.auxSize > this.auxProcessors.length) {
            // We need new aux arrays (either don't exist, or they are too small)
            this.auxProcessors = new IElementProcessor[Math.max(this.auxSize, 4)];
            this.auxVisited = new boolean[Math.max(this.auxSize, 4)];
        }

        System.arraycopy(associatedProcessors, 0, this.auxProcessors, 0, this.auxSize);
        // No pre-initialization for the visited array -- we will do it position by position

        // Now we should check the matches between the new and the old iterator processors - we will build
        // on the fact that processors are always ordered by precedence
        int i = 0; // index for the NEW processors
        int j = 0; // index for the OLD processors
        while (i < this.auxSize) {

            if (i >= this.size || j >= this.size) {
                // We know everything in the new array from here on has to be new. Might also be that we
                // just did a resetGathering (this.size == 0), and we are going to consider every new processor
                // as "not visited"
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

            final int comp = ProcessorComparators.PROCESSOR_COMPARATOR.compare(this.auxProcessors[i], this.processors[j]);

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
                // continue

            } else { // comp > 0
                // The old one has higher precedence (lower value), so it's been removed -- just skip
                j++;
                // continue

            }

        }

        // Finally, just swap the arrays
        final IElementProcessor[] swapProcessors = this.auxProcessors;
        final boolean[] swapVisited = this.auxVisited;
        this.auxProcessors = this.processors;
        this.auxVisited = this.visited;
        this.processors = swapProcessors;
        this.visited = swapVisited;
        this.size = this.auxSize;

    }



    void resetAsCloneOf(final ElementProcessorIterator original) {

        this.size = original.size;
        this.last = original.last;
        this.currentTag = original.currentTag;
        this.lastToBeRepeated = original.lastToBeRepeated;
        this.lastWasRepeated = original.lastWasRepeated;

        if (this.size > 0 && original.processors != null) { // original.visited will also be != null
            if (this.processors == null || this.processors.length < this.size) {
                this.processors = new IElementProcessor[this.size];
                this.visited = new boolean[this.size];
            }
            System.arraycopy(original.processors, 0, this.processors, 0, this.size);
            System.arraycopy(original.visited, 0, this.visited, 0, this.size);
        }

    }

}
