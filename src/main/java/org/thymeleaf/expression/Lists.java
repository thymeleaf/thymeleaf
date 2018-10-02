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
import java.util.Comparator;
import java.util.List;

import org.thymeleaf.util.ListUtils;


/**
 * <p>
 *   Expression Object for performing list operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #lists}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Lists {
    

    public List<?> toList(final Object target) {
        return ListUtils.toList(target);
    }

    
    public int size(final List<?> target) {
        return ListUtils.size(target);
    }
    
    
    public boolean isEmpty(final List<?> target) {
        return ListUtils.isEmpty(target);
    }
    
    
    public boolean contains(final List<?> target, final Object element) {
        return ListUtils.contains(target, element);
    }
    
    
    public boolean containsAll(final List<?> target, final Object[] elements) {
        return ListUtils.containsAll(target, elements);
    }
    
    
    public boolean containsAll(final List<?> target, final Collection<?> elements) {
        return ListUtils.containsAll(target, elements);
    }
    
    
    public <T extends Comparable<? super T>> List<T> sort(final List<T> list) {
        return ListUtils.sort(list);
    }
    
    public <T> List<T> sort(final List<T> list, final Comparator<? super T> c) {
        return ListUtils.sort(list, c);
    }
    
    
    
    
    public Lists() {
        super();
    }
    
}
