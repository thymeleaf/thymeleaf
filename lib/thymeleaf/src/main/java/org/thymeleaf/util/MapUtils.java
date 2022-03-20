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
import java.util.Map;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class MapUtils {
    
    

    public static int size(final Map<?,?> target) {
        Validate.notNull(target, "Cannot get map size of null");
        return target.size();
    }
    
    
    public static boolean isEmpty(final Map<?,?> target) {
        return target == null || target.isEmpty();
    }

    
    public static <X> boolean containsKey(final Map<? super X,?> target, final X key) {
        Validate.notNull(target, "Cannot execute map containsKey: target is null");
        return target.containsKey(key);
    }
    
    public static <X> boolean containsAllKeys(final Map<? super X,?> target, final X[] keys) {
        Validate.notNull(target, "Cannot execute map containsAllKeys: target is null");
        Validate.notNull(keys, "Cannot execute map containsAllKeys: keys is null");
        return containsAllKeys(target, Arrays.asList(keys));
    }
    
    public static <X> boolean containsAllKeys(final Map<? super X,?> target, final Collection<X> keys) {
        Validate.notNull(target, "Cannot execute map containsAllKeys: target is null");
        Validate.notNull(keys, "Cannot execute map containsAllKeys: keys is null");
        return target.keySet().containsAll(keys); 
    }
    
    
    public static <X> boolean containsValue(final Map<?,? super X> target, final X value) {
        Validate.notNull(target, "Cannot execute map containsValue: target is null");
        return target.containsValue(value);
    }
    
    public static <X> boolean containsAllValues(final Map<?,? super X> target, final X[] values) {
        Validate.notNull(target, "Cannot execute map containsAllValues: target is null");
        Validate.notNull(values, "Cannot execute map containsAllValues: values is null");
        return containsAllValues(target, Arrays.asList(values));
    }
    
    public static <X> boolean containsAllValues(final Map<?,? super X> target, final Collection<X> values) {
        Validate.notNull(target, "Cannot execute map containsAllValues: target is null");
        Validate.notNull(values, "Cannot execute map containsAllValues: values is null");
        return target.values().containsAll(values); 
    }
    
    
    
    private MapUtils() {
        super();
    }
    
    
}
