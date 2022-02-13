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

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0 (equivalent to the one existing since 1.0)
 *
 */
public final class IterationStatusVar {

    int index;
    Integer size; // it can be null if we don't know the size of the iterated object beforehand!
    Object current;

    IterationStatusVar() {
        super();
    }

    public int getIndex() {
        return this.index;
    }

    public int getCount() {
        return this.index + 1;
    }

    public boolean hasSize() {
        return this.size != null;
    }

    public Integer getSize() {
        return this.size;
    }

    public Object getCurrent() {
        return this.current;
    }

    public boolean isEven() {
        // We start counting in 1 in order to be consistent with :nth-child(odd) and :nth-child(even) CSS selectors
        return ((this.index + 1) % 2 == 0);
    }

    public boolean isOdd() {
        return !isEven();
    }

    public boolean isFirst() {
        return (this.index == 0);
    }

    public boolean isLast() {
        return (this.index == this.size - 1);
    }

    @Override
    public String toString() {
        return "{index = " + this.index + ", count = " + (this.index + 1) +
                ", size = " + this.size + ", current = " + (this.current == null? "null" : this.current.toString()) + "}";
    }

}
