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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ArrayUtils {
    
    

    public static Object[] toArray(final Object target) {
        return toArray(null, target);
    }

    public static Object[] toStringArray(final Object target) {
        return toArray(String.class, target);
    }

    public static Object[] toIntegerArray(final Object target) {
        return toArray(Integer.class, target);
    }

    public static Object[] toLongArray(final Object target) {
        return toArray(Long.class, target);
    }

    public static Object[] toDoubleArray(final Object target) {
        return toArray(Double.class, target);
    }

    public static Object[] toFloatArray(final Object target) {
        return toArray(Float.class, target);
    }

    public static Object[] toBooleanArray(final Object target) {
        return toArray(Boolean.class, target);
    }

    
    public static int length(final Object[] target) {
        Validate.notNull(target, "Cannot get array length of null");
        return target.length;
    }
    
    
    public static boolean isEmpty(final Object[] target) {
        return target == null || target.length <= 0;
    }
    
    
    public static boolean contains(final Object[] target, final Object element) {

        Validate.notNull(target, "Cannot execute array contains: target is null");

        if (element == null) {
            for (final Object targetElement : target) {
                if (targetElement == null) {
                    return true;
                }
            }
            return false;
        }

        for (final Object targetElement : target) {
            if (element.equals(targetElement)) {
                return true;
            }
        }

        return false;

    }
    
    
    public static boolean containsAll(final Object[] target, final Object[] elements) {
        Validate.notNull(target, "Cannot execute array containsAll: target is null");
        Validate.notNull(elements, "Cannot execute array containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }
    
    
    public static boolean containsAll(final Object[] target, final Collection<?> elements) {
        Validate.notNull(target, "Cannot execute array contains: target is null");
        Validate.notNull(elements, "Cannot execute array containsAll: elements is null");
        final Set<?> remainingElements = new HashSet<Object>(elements);
        remainingElements.removeAll(Arrays.asList(target));
        return remainingElements.isEmpty();
    }
    
    

    
    private static Object[] toArray(final Class<?> componentClass, final Object target) {
        
        Validate.notNull(target, "Cannot convert null to array");
        
        if (target.getClass().isArray()) {
            
            if (componentClass == null) {
                return (Object[]) target;
            }
            
            final Class<?> targetComponentClass = target.getClass().getComponentType();
            if (componentClass.isAssignableFrom(targetComponentClass)) {
                return (Object[]) target;
            }
            
            throw new IllegalArgumentException(
                    "Cannot convert object of class \"" + targetComponentClass.getName() + "[]\" to an array" + 
                    " of " + componentClass.getClass().getSimpleName());
            
        }
        
        if (target instanceof Iterable<?>) {
            
            Class<?> computedComponentClass = null;
            final Iterable<?> iterableTarget = (Iterable<?>)target;
            final List<Object> elements = new ArrayList<Object>(5); // init capacity guessed - not know from iterable.
            
            for (final Object element : iterableTarget) {
                if (componentClass == null && element != null) {
                    if (computedComponentClass == null) {
                        computedComponentClass = element.getClass();
                    } else if (!computedComponentClass.equals(Object.class)) {
                        if (!computedComponentClass.equals(element.getClass())) {
                            computedComponentClass = Object.class;
                        }
                    }
                }
                elements.add(element);
            }
            
            if (computedComponentClass == null) {
                computedComponentClass = (componentClass != null? componentClass : Object.class);
            }
            
            final Object[] result = 
                (Object[]) Array.newInstance(computedComponentClass, elements.size());
            
            return elements.toArray(result);
            
        }
        
        throw new IllegalArgumentException(
                "Cannot convert object of class \"" + target.getClass().getName() + "\" to an array" +
                (componentClass == null? "" : (" of " + componentClass.getClass().getSimpleName())));
        
    }

    
    
    
    @SuppressWarnings("unchecked")
    public static <T,X> X[] copyOf(final T[] original, final int newLength, final Class<? extends X[]> newType) {
        final X[] newArray = 
                (newType == (Object)Object[].class)?
                        (X[]) new Object[newLength] : 
                        (X[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newLength));
        return newArray;
    }
    
    
    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(final T[] original, final int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }
    
    
    public static char[] copyOf(final char[] original, final int newLength) {
        final char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
    
    
    public static char[] copyOfRange(final char[] original, final int from, final int to) {
        final int newLength = (to - from);
        if (newLength < 0) {
            throw new IllegalArgumentException("Cannot copy array range with indexes " + from + " and " + to);
        }
        final char[] copy = new char[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }



    
    
    private ArrayUtils() {
        super();
    }
    
    
}
