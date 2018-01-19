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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ListUtils {
    
    

    public static List<?> toList(final Object target) {
        
        Validate.notNull(target, "Cannot convert null to list");
        
        if (target instanceof List<?>) {
            return (List<?>) target;
        }
        
        if (target.getClass().isArray()) {
            return new ArrayList<Object>(Arrays.asList((Object[])target));
        }
        
        if (target instanceof Iterable<?>) {
            final List<Object> elements = new ArrayList<Object>(10);
            for (final Object element : (Iterable<?>)target) {
                elements.add(element);
            }
            return elements;
        }
        
        throw new IllegalArgumentException(
                "Cannot convert object of class \"" + target.getClass().getName() + "\" to a list");
        
    }

    
    
    public static int size(final List<?> target) {
        Validate.notNull(target, "Cannot get list size of null");
        return target.size();
    }
    
    
    public static boolean isEmpty(final List<?> target) {
        return target == null || target.isEmpty();
    }
    
    public static boolean contains(final List<?> target, final Object element) {
        Validate.notNull(target, "Cannot execute list contains: target is null");
        return target.contains(element);
    }
    
    
    public static boolean containsAll(final List<?> target, final Object[] elements) {
        Validate.notNull(target, "Cannot execute list containsAll: target is null");
        Validate.notNull(elements, "Cannot execute list containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }
    
    
    public static boolean containsAll(final List<?> target, final Collection<?> elements) {
        Validate.notNull(target, "Cannot execute list contains: target is null");
        Validate.notNull(elements, "Cannot execute list containsAll: elements is null");
        return target.containsAll(elements);
    }
    
    
    /**
     * <p>
     *   Creates an new instance of the list add the sorted list to it.
     * </p>
     *
     * @param list the list which content should be ordered.
     * @param <T> the type of the list elements.
     * @return a new sorted list.
     * @see java.util.Collections#sort(List)
     */
    public static <T extends Comparable<? super T>> List<T> sort(final List<T> list) {
        Validate.notNull(list, "Cannot execute list sort: list is null");
        final Object[] a = list.toArray();
        Arrays.sort(a);
        
        return fillNewList(a, list.getClass());
    }

    
    /**
     * <p>
     *   Creates an new instance of the list add the sorted list to it.
     * </p>
     *
     * @param list the list which content should be ordered.
     * @param c the comparator.
     * @param <T> the type of the list elements.
     * @return a new sorted list.
     * @see java.util.Collections#sort(List, Comparator)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> sort(final List<T> list, final Comparator<? super T> c) {
        Validate.notNull(list, "Cannot execute list sort: list is null");
        final Object[] a = list.toArray();
        Arrays.sort(a, (Comparator) c);
        
        return fillNewList(a, list.getClass());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T> List<T> fillNewList(
            final Object[] a, final Class<? extends List> listType) {
        List<T> newList;
        try {
            newList = listType.getConstructor().newInstance();
        } catch (final Exception e) {
            newList = new ArrayList<T>(a.length + 2);
        }
        
        for (final Object object : a) {
            newList.add((T)object);
        }
        return newList;
    }
    
    
    private ListUtils() {
        super();
    }
    
    
}
