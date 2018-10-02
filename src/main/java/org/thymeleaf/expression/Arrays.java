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
package org.thymeleaf.expression;

import java.util.Collection;

import org.thymeleaf.util.ArrayUtils;


/**
 * <p>
 *   Expression Object for performing array operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #arrays}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Arrays {
    

    public Object[] toArray(final Object target) {
        return ArrayUtils.toArray(target);
    }

    public Object[] toStringArray(final Object target) {
        return ArrayUtils.toStringArray(target);
    }

    public Object[] toIntegerArray(final Object target) {
        return ArrayUtils.toIntegerArray(target);
    }

    public Object[] toLongArray(final Object target) {
        return ArrayUtils.toLongArray(target);
    }

    public Object[] toDoubleArray(final Object target) {
        return ArrayUtils.toDoubleArray(target);
    }

    public Object[] toFloatArray(final Object target) {
        return ArrayUtils.toFloatArray(target);
    }

    public Object[] toBooleanArray(final Object target) {
        return ArrayUtils.toBooleanArray(target);
    }

    
    public int length(final Object[] target) {
        return ArrayUtils.length(target);
    }
    
    
    public boolean isEmpty(final Object[] target) {
        return ArrayUtils.isEmpty(target);
    }
    
    
    public boolean contains(final Object[] target, final Object element) {
        return ArrayUtils.contains(target, element);
    }
    
    
    public boolean containsAll(final Object[] target, final Object[] elements) {
        return ArrayUtils.containsAll(target, elements);
    }
    
    
    public boolean containsAll(final Object[] target, final Collection<?> elements) {
        return ArrayUtils.containsAll(target, elements);
    }    
    
    
    
    
    public Arrays() {
        super();
    }
    
}
