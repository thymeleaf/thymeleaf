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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SetUtils {
    
    

    public static Set<?> toSet(final Object target) {
        
        Validate.notNull(target, "Cannot convert null to set");
        
        if (target instanceof Set<?>) {
            return (Set<?>) target;
        }
        
        if (target.getClass().isArray()) {
            return new LinkedHashSet<Object>(Arrays.asList((Object[])target));
        }
        
        if (target instanceof Iterable<?>) {
            final Set<Object> elements = new LinkedHashSet<Object>();
            for (final Object element : (Iterable<?>)target) {
                elements.add(element);
            }
            return elements;
        }
        
        throw new IllegalArgumentException(
                "Cannot convert object of class \"" + target.getClass().getName() + "\" to a set");
        
    }

    
    
    public static int size(final Set<?> target) {
        Validate.notNull(target, "Cannot get set size of null");
        return target.size();
    }
    
    
    public static boolean isEmpty(final Set<?> target) {
        return target == null || target.isEmpty();
    }
    
    public static boolean contains(final Set<?> target, final Object element) {
        Validate.notNull(target, "Cannot execute set contains: target is null");
        return target.contains(element);
    }
    
    
    public static boolean containsAll(final Set<?> target, final Object[] elements) {
        Validate.notNull(target, "Cannot execute set containsAll: target is null");
        Validate.notNull(elements, "Cannot execute set containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }
    
    
    public static boolean containsAll(final Set<?> target, final Collection<?> elements) {
        Validate.notNull(target, "Cannot execute set contains: target is null");
        Validate.notNull(elements, "Cannot execute set containsAll: elements is null");
        return target.containsAll(elements);
    }
    
    
    
    public static <X> Set<X> singletonSet(final X element) {
        final Set<X> set = new HashSet<X>(2, 1.0f);
        set.add(element);
        return Collections.unmodifiableSet(set);
    }
    
    
    private SetUtils() {
        super();
    }
    
    
}
