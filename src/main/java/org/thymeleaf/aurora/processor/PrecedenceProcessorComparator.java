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
package org.thymeleaf.aurora.processor;

import java.util.Comparator;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class PrecedenceProcessorComparator implements Comparator<IProcessor> {

    public static final PrecedenceProcessorComparator INSTANCE = new PrecedenceProcessorComparator();


    PrecedenceProcessorComparator() {
        super();
    }


    public int compare(final IProcessor o1, final IProcessor o2) {
        if (o1 == o2) {
            // This is the only case in which the comparison of two processors will return 0
            return 0;
        }
        final int precedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
        if (precedenceComp != 0) {
            return precedenceComp;
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
