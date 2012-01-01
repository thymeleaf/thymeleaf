/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
        Validate.notNull(target, "Cannot execute array isEmpty: target is null");
        return target.length > 0;
    }
    
    
    public static boolean contains(final Object[] target, final Object element) {
        Validate.notNull(target, "Cannot execute array contains: target is null");
        for (final Object targetElement : target) {
            if (targetElement == null) {
                if (element == null) {
                    return true;
                }
            } else {
                if (element != null && targetElement.equals(element)) {
                    return true;
                }
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
            final List<Object> elements = new ArrayList<Object>();
            
            for (final Object element : (Iterable<?>)target) {
                if (componentClass == null) {
                    if (element != null && computedComponentClass == null) {
                        computedComponentClass = element.getClass();
                    } else if (element != null && computedComponentClass != null && !computedComponentClass.equals(Object.class)) {
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
    
    
    
    
    private ArrayUtils() {
        super();
    }
    
    
}
