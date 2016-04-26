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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.thymeleaf.engine.IteratedGatheringModelProcessable.IterationType;

/**
 * <p>
 *   Throttled implementation of {@link Iterator}, meant to be used in scenarios when an iterated
 *   context variable is allowed to be in control of the engine's throttling (i.e. the engine's execution
 *   is <em>data-driven</em>).
 * </p>
 * <p>
 *   A common scenario for this would be reactive systems executing the template engine as a part of a
 *   flow obtaining data from a data source, so that as the data is obtained, a part of the template is output
 *   containing that part of the data.
 * </p>
 * <p>
 *   This class is meant for <strong>internal use only</strong> from the diverse integrations of Thymeleaf in
 *   reactive architectures. There is normally no reason why a user would have to use this class directly.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class DataDrivenTemplateIterator implements Iterator<Object> {

    private final List<Object> values;
    private IterationType iterationType;
    private boolean feedingComplete;


    public DataDrivenTemplateIterator() {
        super();
        this.values = new ArrayList<Object>(10);
        this.iterationType = null;
        this.feedingComplete = false;
    }


    @Override
    public boolean hasNext() {
        if (this.iterationType == null) {
            throw new IllegalStateException("hasNext(): Throttled iterator has not yet computed the iteration type");
        }
        return !this.values.isEmpty();
    }


    @Override
    public Object next() {

        if (this.iterationType == null) {
            throw new IllegalStateException("next(): Throttled iterator has not yet computed the iteration type");
        }

        if (this.values.isEmpty()) {
            throw new NoSuchElementException();
        }

        final Object value = this.values.get(0);
        this.values.remove(0);
        return value;

    }


    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported in Throttled Iterator");
    }


    IterationType getIterationType() {
        return this.iterationType;
    }


    boolean isPaused() {
        if (this.iterationType != null) {
            if (!this.values.isEmpty() || this.feedingComplete) {
                return false;
            }
        }
        return true;
    }


    public boolean continueBufferExecution() {
        return this.iterationType != null && !this.values.isEmpty();
    }


    public void feedBuffer(final List<Object> newElements) {
        this.values.addAll(newElements);
        if (this.iterationType == null && this.values.size() >= 2) {
            this.iterationType = IterationType.MULTIPLE;
        }
    }

    public void feedingComplete() {
        this.feedingComplete = true;
        if (this.iterationType == null) {
            if (this.values.isEmpty()) {
                this.iterationType = IterationType.ZERO;
            } else {
                this.iterationType = IterationType.ONE;
            }
        }
    }


}
