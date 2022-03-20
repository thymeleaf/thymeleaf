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
import java.util.Map;

import org.thymeleaf.util.MapUtils;


/**
 * <p>
 *   Expression Object for performing map operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #maps}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Maps {
    

    public int size(final Map<?,?> target) {
        return MapUtils.size(target);
    }
    
    
    public boolean isEmpty(final Map<?,?> target) {
        return MapUtils.isEmpty(target);
    }
    
    
    public <X> boolean containsKey(final Map<? super X,?> target, final X key) {
        return MapUtils.containsKey(target, key);
    }
    
    public <X> boolean containsValue(final Map<?,? super X> target, final X value) {
        return MapUtils.containsValue(target, value);
    }
    
    
    public <X> boolean containsAllKeys(final Map<? super X,?> target, final X[] keys) {
        return MapUtils.containsAllKeys(target, keys);
    }
    
    
    public <X> boolean containsAllKeys(final Map<? super X,?> target, final Collection<X> keys) {
        return MapUtils.containsAllKeys(target, keys);
    }    
    
    
    public <X> boolean containsAllValues(final Map<?,? super X> target, final X[] values) {
        return MapUtils.containsAllValues(target, values);
    }
    
    
    public <X> boolean containsAllValues(final Map<?,? super X> target, final Collection<X> values) {
        return MapUtils.containsAllValues(target, values);
    }    
    
    
    
    
    public Maps() {
        super();
    }
    
}
