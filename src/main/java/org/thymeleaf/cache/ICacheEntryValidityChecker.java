package org.thymeleaf.cache;

import java.io.Serializable;

/**
 * <p>
 *   Defines the logic needed to (optionally) validate an entry living in a
 *   {@link CacheMap} before returning it as the result of a <tt>get</tt>
 *   operation. If not valid, the entry will be removed
 *   from the cache (and null will be returned).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 * @param <K> The type of the cache map keys
 * @param <V> The type of the cache map values
 */
public interface ICacheEntryValidityChecker<K,V> extends Serializable {

    public boolean checkIsValueStillValid(final K key, final V value, final long entryCreationTimestamp);

}