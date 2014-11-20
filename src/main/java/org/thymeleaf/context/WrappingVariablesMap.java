/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *   Wrapper around a {@link VariablesMap} which 'masks' the wrapped map by keeping track of extra puts.
 * </p>
 * <p>
 *   Does not support removing entries from the wrapped map through {@link #remove(Object)} nor providing true views on the
 *   underlying map through {@link #keySet()}, {@link #values()} and {@link #entrySet()} unless the wrapped map is empty.
 *   This is more efficient than making defensive copies up-front for every node.
 * </p>
 *
 * @author Joris Kuipers
 * 
 * @since 2.1.4
 *
 */
class WrappingVariablesMap<K,V> extends VariablesMap<K, V> {

    private static final long serialVersionUID = 5894245538819382467L;

    private final VariablesMap<K, V> targetMap;
    private HashSet<K> targetRemovedKeys;



    WrappingVariablesMap(VariablesMap<K, V> targetMap) {
        super();
        this.targetMap = targetMap;
        this.targetRemovedKeys = null;
    }


    @Override
    public int size() {
        return super.size() + this.targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && this.targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (super.containsKey(key)) {
            return true;
        }
        return this.targetMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (super.containsValue(value)) {
            return true;
        }
        return this.targetMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (super.containsKey(key)) {
            return super.get(key);
        }
        return this.targetMap.get(key);
    }

    @Override
    public V remove(Object key) {
        if (super.containsKey(key)) {
            return super.remove(key);
        }
        if (this.targetRemovedKeys != null && this.targetRemovedKeys.contains(key)) {
            return null;
        }
        if (this.targetMap.containsKey(key)) {
            if (this.targetRemovedKeys == null) {
                this.targetRemovedKeys = new HashSet<K>(3);
            }
            this.targetRemovedKeys.add((K) key);
        }
        return null;
    }

    @Override
    public void clear() {
        if (this.targetRemovedKeys == null) {
            this.targetRemovedKeys = new HashSet<K>(3);
        }
        this.targetRemovedKeys.addAll(this.targetMap.keySet());
        super.clear();
    }




    /**
     * This violates the Map contract because it returns a read-only Set, not a mutable Set
     * that reflects the underlying table, unless the wrapped map's key set is empty.
     *
     * @return immutable set containing the union of the wrapped map's keys and this map's keys,
     *         or the real key set of this map if the wrapped map is empty.
     */
    @Override
    public Set<K> keySet() {
        final Set<K> targetKeySet = this.targetMap.keySet();
        if (targetKeySet.isEmpty()) {
            return super.keySet();
        }
        final Set<K> keySet = new HashSet<K>(targetKeySet);
        keySet.addAll(super.keySet());
        return Collections.unmodifiableSet(keySet);
    }

    /**
     * This violates the Map contract because it returns a read-only collection, not a mutable one
     * that reflects the underlying table, unless the wrapped map's values collection is empty.
     *
     * @return immutable collection containing the union of the wrapped map's values and this map's values,
     *         or the real values collection of this map if the wrapped map is empty.
     */
    @Override
    public Collection<V> values() {
        final Collection<V> targetValues = this.targetMap.values();
        if (targetValues.isEmpty()) {
            return super.values();
        }
        final Collection<V> values = new ArrayList<V>(targetValues);
        values.addAll(super.values());
        return Collections.unmodifiableCollection(values);
    }

    /**
     * This violates the Map contract because it returns a read-only Set, not a mutable Sne
     * that reflects the underlying table, unless the wrapped map is empty.
     *
     * @return immutable Set containing the union of the wrapped map's entrySet and this map's entrySet,
     *         or the real entry set of this map if the wrapped map is empty
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> targetEntrySet = targetMap.entrySet();
        if (targetEntrySet.isEmpty()) {
            return super.entrySet();
        }
        final Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>(targetEntrySet);
        entrySet.addAll(super.entrySet());
        return Collections.unmodifiableSet(entrySet);
    }



    public WrappingVariablesMap clone() {
        return (WrappingVariablesMap) super.clone();
    }

}
