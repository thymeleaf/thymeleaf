package org.thymeleaf.context;

import java.util.*;

/**
 * Wrapper around a {@link VariablesMap} which 'masks' the wrapped map by keeping track of extra puts.
 * Does not support removing entries from the wrapped map through {@see #remove} nor providing true views on the 
 * underlying map through {@see #keySet}, {@see #values} and {@see entrySet} unless the wrapped map is empty. 
 * This is more efficient than making defensive copies up-front for every node.
 */
public class WrappingVariablesMap<K,V> extends VariablesMap<K, V> {

    private VariablesMap<K, V> targetMap;

    public WrappingVariablesMap(VariablesMap<K, V> targetMap) {
        this.targetMap = targetMap;
    }

    @Override
    public int size() {
        return super.size() + targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (super.containsKey(key)) {
            return true;
        }
        return targetMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (super.containsValue(value)) {
            return true;
        }
        return targetMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (super.containsKey(key)) {
            return super.get(key);
        }
        return targetMap.get(key);
    }

    @Override
    public V remove(Object key) {
        if (super.containsKey(key)) {
            return super.remove(key);
        }
        if (targetMap.containsKey(key)) {
            throw new UnsupportedOperationException("Can't remove key from wrapped map in WrappingVariablesMap");
        }
        return null;
    }

    @Override
    public void clear() {
        this.targetMap = new VariablesMap<K, V>(0);
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
        Set<K> targetKeySet = this.targetMap.keySet();
        if (targetKeySet.isEmpty()) {
            return super.keySet();
        }
        Set<K> keySet = new HashSet<K>(targetKeySet);
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
        Collection<V> targetValues = this.targetMap.values();
        if (targetValues.isEmpty()) {
            return super.values();
        }
        Collection<V> values = new ArrayList<V>(targetValues);
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
        Set<Map.Entry<K, V>> targetEntrySet = targetMap.entrySet();
        if (targetEntrySet.isEmpty()) {
            return super.entrySet();
        }
        Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>(targetEntrySet);
        entrySet.addAll(super.entrySet());
        return Collections.unmodifiableSet(entrySet);
    }

}
