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

import java.io.Serializable;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import org.thymeleaf.aurora.model.IProcessableElementTag;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.PrecedenceProcessorComparator;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class ProcessorIterator {


    private InternalEngineProcessorIterator internalImpl = null;
    private GenericEngineProcessorIterator genericImpl = null;
    private IProcessorIteratorImpl impl;


    ProcessorIterator() {
        super();
    }



    void reset(final IProcessableElementTag tag) {
        if (tag instanceof AbstractProcessableElementTag) {
            if (this.internalImpl == null) {
                this.internalImpl = new InternalEngineProcessorIterator();
            }
            this.internalImpl.reset((AbstractProcessableElementTag)tag);
            this.impl = this.internalImpl;
        } else {
            if (this.genericImpl == null) {
                this.genericImpl = new GenericEngineProcessorIterator();
            }
            this.genericImpl.reset(tag);
            this.impl = this.genericImpl;
        }
    }

    IProcessor next() {
        return this.impl.next();
    }



    private interface IProcessorIteratorImpl {
        IProcessor next();
    }



    /*
     * This class will take care of iterating the processors in the most optimal way possible. It allows the attributes
     * in the tag to be modified durint iteration, taking new processors into account as soon as they appear, even if
     * they have higher precedence than the last executed processor for the same tag.
     */
    private static class InternalEngineProcessorIterator implements IProcessorIteratorImpl {


        private AbstractProcessableElementTag tag = null;

        private int last = -1;

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


        InternalEngineProcessorIterator() {
            super();
        }


        void reset(final AbstractProcessableElementTag tag) {
            this.tag = tag;
            this.size = 0;
            this.last = -1;
        }


        public IProcessor next() {

            if (this.tag.elementAttributes.version != this.attributesVersion) {
                recompute();
                this.attributesVersion = this.tag.elementAttributes.version;
                this.last = -1;
            }

            // We use 'last' as a starting index in order save some iterations (except after recomputes)
            int n = this.size;
            int i = this.last + 1;
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
                this.processors = new IProcessor[Math.max(this.size, 4)]; // minimum size = 4
                this.visited = new boolean[Math.max(this.size, 4)]; // minimum size = 4

                this.tag.associatedProcessors.toArray(this.processors); // No need to assign to anything
                Arrays.fill(this.visited, false);

                return;

            }

            // Processors have changed since the last time we used the iterator (attributes changed),
            // so we need to use the 'aux' structures in order to recompute processors and then swap.

            this.auxSize = this.tag.associatedProcessors.size();
            if (this.auxProcessors == null || this.auxSize > this.auxProcessors.length) {
                // We need new aux arrays (either don't exist, or they are too small)
                this.auxProcessors = new IProcessor[Math.max(this.auxSize, 4)];
                this.auxVisited = new boolean[Math.max(this.auxSize, 4)];
            }

            this.tag.associatedProcessors.toArray(this.auxProcessors); // No need to assign to anything
            // No pre-initialization for the visited array -- we will do it position by position

            // Now we should check the matches between the new and the old iterator processors - we will build
            // on the fact that processors are always ordered by precedence
            int i = 0; // index for the NEW processors
            int j = 0; // index for the OLD processors
            while (i < this.auxSize) {

                if (i >= this.size) {
                    // We know everything in the new array from here on has to be new. Might also be that we
                    // just did a reset (this.size == 0), and we are going to consider every new processor
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
                    // continue

                } else { // comp > 0
                    // The old one has higher precedence (lower value), so it's been removed -- just skip
                    j++;
                    // continue

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



    /*
     * This implementation will be much less optimal than the Internal Engine one, but this is normal because we
     * are dealing with an implementation of IProcessableElementTag that we don't know, so we cannot directly access
     * its internals.
     */
    private static class GenericEngineProcessorIterator implements IProcessorIteratorImpl {

        private IProcessableElementTag tag = null;
        private final ProcessorCounter alreadyCounted = new ProcessorCounter();

        GenericEngineProcessorIterator() {
            super();
        }


        void reset(final IProcessableElementTag tag) {
            this.tag = tag;
            this.alreadyCounted.clear();
        }


        public IProcessor next() {

            final List<IProcessor> processors = this.tag.getAssociatedProcessorsInOrder();

            if (processors == null || processors.isEmpty()) {
                return null;
            }

            int i = 0;
            int n = processors.size();
            while (n-- != 0) {
                final IProcessor processor = processors.get(i++);
                if (!this.alreadyCounted.isAlreadyCounted(processor)) {
                    this.alreadyCounted.count(processor);
                    return processor;
                }
            }
            return null;

        }




        private final class ProcessorCounter implements Serializable {

            private static final long serialVersionUID = -6965348731301112911L;

            private final IdentityHashMap<IProcessor, Object> counted;

            ProcessorCounter() {
                super();
                this.counted = new IdentityHashMap<IProcessor, Object>(10);
            }

            void count(final IProcessor object) {
                this.counted.put(object, null);
            }

            boolean isAlreadyCounted(final IProcessor object) {
                return this.counted.containsKey(object);
            }

            void clear() {
                this.counted.clear();
            }

        }

    }

}
