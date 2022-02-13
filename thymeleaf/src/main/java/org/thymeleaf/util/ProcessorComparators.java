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
package org.thymeleaf.util;

import java.util.Comparator;

import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorComparators {


    public static final Comparator<IProcessor> PROCESSOR_COMPARATOR = new ProcessorPrecedenceComparator();
    public static final Comparator<IPreProcessor> PRE_PROCESSOR_COMPARATOR = new PreProcessorPrecedenceComparator();
    public static final Comparator<IPostProcessor> POST_PROCESSOR_COMPARATOR = new PostProcessorPrecedenceComparator();




    private ProcessorComparators() {
        super();
    }



    private static final class ProcessorPrecedenceComparator implements Comparator<IProcessor> {


        ProcessorPrecedenceComparator() {
            super();
        }


        public int compare(final IProcessor o1, final IProcessor o2) {
            if (o1 == o2) {
                // This is the only case in which the comparison of two processors will return 0
                return 0;
            }
            if (o1 instanceof ProcessorConfigurationUtils.AbstractProcessorWrapper && o2 instanceof ProcessorConfigurationUtils.AbstractProcessorWrapper) {
                return compareWrapped((ProcessorConfigurationUtils.AbstractProcessorWrapper)o1, (ProcessorConfigurationUtils.AbstractProcessorWrapper)o2);
            }
            final int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0
        }


        /*
         * Processors are wrapped and therefore we can apply dialect precedence
         */
        private int compareWrapped(final ProcessorConfigurationUtils.AbstractProcessorWrapper o1w, final ProcessorConfigurationUtils.AbstractProcessorWrapper o2w) {

            final int dialectPrecedenceComp = compareInts(o1w.getDialectPrecedence(), o2w.getDialectPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }

            final IProcessor o1 = o1w.unwrap();
            final IProcessor o2 = o2w.unwrap();

            final int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0

        }


        private static int compareInts(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }

    }




    private static final class PreProcessorPrecedenceComparator implements Comparator<IPreProcessor> {


        PreProcessorPrecedenceComparator() {
            super();
        }


        public int compare(final IPreProcessor o1, final IPreProcessor o2) {
            if (o1 == o2) {
                // This is the only case in which the comparison of two processors will return 0
                return 0;
            }
            if (o1 instanceof ProcessorConfigurationUtils.PreProcessorWrapper && o2 instanceof ProcessorConfigurationUtils.PreProcessorWrapper) {
                return compareWrapped((ProcessorConfigurationUtils.PreProcessorWrapper)o1, (ProcessorConfigurationUtils.PreProcessorWrapper)o2);
            }
            final int preProcessorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (preProcessorPrecedenceComp != 0) {
                return preProcessorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0
        }


        /*
         * Processors are wrapped and therefore we can apply dialect precedence
         */
        private int compareWrapped(final ProcessorConfigurationUtils.PreProcessorWrapper o1w, final ProcessorConfigurationUtils.PreProcessorWrapper o2w) {

            final int dialectPrecedenceComp = compareInts(o1w.getDialect().getDialectProcessorPrecedence(), o2w.getDialect().getDialectProcessorPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }

            final IPreProcessor o1 = o1w.unwrap();
            final IPreProcessor o2 = o2w.unwrap();

            final int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0

        }


        private static int compareInts(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }

    }




    private static final class PostProcessorPrecedenceComparator implements Comparator<IPostProcessor> {


        PostProcessorPrecedenceComparator() {
            super();
        }


        public int compare(final IPostProcessor o1, final IPostProcessor o2) {
            if (o1 == o2) {
                // This is the only case in which the comparison of two processors will return 0
                return 0;
            }
            if (o1 instanceof ProcessorConfigurationUtils.PostProcessorWrapper && o2 instanceof ProcessorConfigurationUtils.PostProcessorWrapper) {
                return compareWrapped((ProcessorConfigurationUtils.PostProcessorWrapper)o1, (ProcessorConfigurationUtils.PostProcessorWrapper)o2);
            }
            final int postProcessorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (postProcessorPrecedenceComp != 0) {
                return postProcessorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0
        }


        /*
         * Processors are wrapped and therefore we can apply dialect precedence
         */
        private int compareWrapped(final ProcessorConfigurationUtils.PostProcessorWrapper o1w, final ProcessorConfigurationUtils.PostProcessorWrapper o2w) {

            final int dialectPrecedenceComp = compareInts(o1w.getDialect().getDialectProcessorPrecedence(), o2w.getDialect().getDialectProcessorPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }

            final IPostProcessor o1 = o1w.unwrap();
            final IPostProcessor o2 = o2w.unwrap();

            final int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            final int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2)); // Cannot be 0

        }


        private static int compareInts(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }

    }

}
