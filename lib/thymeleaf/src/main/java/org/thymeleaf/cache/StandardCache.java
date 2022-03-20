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
package org.thymeleaf.cache;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.Validate;


/**
 *
 *
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 *
 * @since 2.0.0
 *
 * @param <K> The type of the cache keys
 * @param <V> The type of the cache values
 */
public final class StandardCache<K, V> implements ICache<K,V> {


    private static final long REPORT_INTERVAL = 300000L; // 5 minutes
    private static final String REPORT_FORMAT =
            "[THYMELEAF][*][*][*][CACHE_REPORT] %8s elements | %12s puts | %12s gets | %12s hits | %12s misses | %.2f hit ratio | %.2f miss ratio - [%s]";
    private volatile long lastExecution = System.currentTimeMillis();

    private final String name;
    private final boolean useSoftReferences;
    private final int maxSize;
    private final CacheDataContainer<K,V> dataContainer;
    private final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker;

    private final boolean traceExecution;
    private final boolean enableCounters;

    private final Logger logger;

    private final AtomicLong getCount;
    private final AtomicLong putCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;








    public StandardCache(final String name, final boolean useSoftReferences,
            final int initialCapacity, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, null, logger, false);
    }

    public StandardCache(final String name, final boolean useSoftReferences,
            final int initialCapacity, final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker,
            final Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, entryValidityChecker, logger, false);
    }

    public StandardCache(final String name, final boolean useSoftReferences,
            final int initialCapacity, final int maxSize, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, maxSize, null, logger, false);
    }

    public StandardCache(final String name, final boolean useSoftReferences,
            final int initialCapacity, final int maxSize, final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker,
            final Logger logger) {
        this(name, useSoftReferences, initialCapacity, maxSize, entryValidityChecker, logger, false);
    }

    public StandardCache(final String name, final boolean useSoftReferences,
            final int initialCapacity, final int maxSize, final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker,
            final Logger logger, final boolean enableCounters) {

        super();

        Validate.notEmpty(name, "Name cannot be null or empty");
        Validate.isTrue(initialCapacity > 0, "Initial capacity must be > 0");
        Validate.isTrue(maxSize != 0, "Cache max size must be either -1 (no limit) or > 0");

        this.name = name;
        this.useSoftReferences = useSoftReferences;
        this.maxSize = maxSize;
        this.entryValidityChecker = entryValidityChecker;

        this.logger = logger;
        this.traceExecution = (logger != null && logger.isTraceEnabled());
        this.enableCounters = (this.traceExecution || enableCounters);
        this.dataContainer =
                new CacheDataContainer<K,V>(this.name, initialCapacity, maxSize, this.traceExecution, this.logger);

        this.getCount = new AtomicLong(0);
        this.putCount = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);

        if (this.logger != null) {
            if (this.maxSize < 0) {
                this.logger.trace("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Soft references {}.",
                        this.name, (this.useSoftReferences? "are used" : "not used"));
            } else {
                this.logger.trace("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Max size: {}. Soft references {}.",
                        new Object[] {this.name, Integer.valueOf(this.maxSize), (this.useSoftReferences? "are used" : "not used")});
            }
        }

    }




    // -----



    public void put(final K key, final V value) {

        incrementReportEntity(this.putCount);

        final CacheEntry<V> entry = new CacheEntry<V>(value, this.useSoftReferences);

        // newSize will be -1 if traceExecution is false
        final int newSize = this.dataContainer.put(key, entry);

        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][CACHE_ADD][{}] Adding cache entry in cache \"{}\" for key \"{}\". New size is {}.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
            outputReportIfNeeded();
        }


    }



    public V get(final K key) {
        return get(key, this.entryValidityChecker);
    }



    public V get(final K key, final ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {

        incrementReportEntity(this.getCount);
        final CacheEntry<V> resultEntry = this.dataContainer.get(key);

        if (resultEntry == null) {
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".",
                        new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
                outputReportIfNeeded();
            }
            return null;
        }

        final V resultValue =
                resultEntry.getValueIfStillValid(this.name, key, validityChecker, this.traceExecution, this.logger);
        if (resultValue == null) {
            final int newSize = this.dataContainer.remove(key);
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Removing cache entry in cache \"{}\" (Entry \"{}\" is not valid anymore). New size is {}.",
                        new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".",
                        new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
                outputReportIfNeeded();
            }
            return null;
        }

        incrementReportEntity(this.hitCount);
        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][CACHE_HIT] Cache hit in cache \"{}\" for key \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
            outputReportIfNeeded();
        }

        return resultValue;

    }


    /**
     * <p>
     *   Returns all the keys contained in this cache. Note this method might return keys for entries
     *   that are already invalid, so the result of calling {@link #get(Object)} for these keys might
     *   be {@code null}.
     * </p>
     *
     * @return the complete set of cache keys. Might include keys for already-invalid (non-cleaned) entries.
     * @since 2.1.4
     */
    public Set<K> keySet() {
        return this.dataContainer.keySet();
    }



    public void clear() {

        this.dataContainer.clear();

        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][0] Removing ALL cache entries in cache \"{}\". New size is 0.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name});
        }

    }



    public void clearKey(final K key) {

        final int newSize = this.dataContainer.remove(key);

        if (this.traceExecution && newSize != -1) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][{}] Removed cache entry in cache \"{}\" for key \"{}\". New size is {}.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
        }

    }



    // -----



    public String getName() {
        return this.name;
    }

    public boolean hasMaxSize() {
        return (this.maxSize > 0);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean getUseSoftReferences() {
        return this.useSoftReferences;
    }

    public int size() {
        return this.dataContainer.size();
    }

    public long getPutCount() {
        return this.putCount.get();
    }

    public long getGetCount() {
        return this.getCount.get();
    }

    public long getHitCount() {
        return this.hitCount.get();
    }

    public long getMissCount(){
        return this.missCount.get();
    }


    public double getHitRatio() {
        long hitCount = getHitCount();
        long getCount = getGetCount();

        if (hitCount == 0 || getCount == 0) {
            return 0;
        }

        return (double) hitCount / (double) getCount;
    }

    public double getMissRatio() {
       return 1 - getHitRatio();
    }



    // -----


    private void incrementReportEntity(final AtomicLong entity) {
        if (this.enableCounters) {
            entity.incrementAndGet();
        }
    }


    private void outputReportIfNeeded() {

        final long currentTime = System.currentTimeMillis();
        if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) { // first check without need to sync
            synchronized (this) {
                if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) {
                    long hitCount = getHitCount();
                    long missCount = getMissCount();
                    long putCount = getPutCount();
                    long getCount = getGetCount();

                    double hitRatio = (double) hitCount / (double) getCount;
                    double missRatio = 1 - hitRatio;

                    this.logger.trace(
                            String.format(REPORT_FORMAT,
                                    Integer.valueOf(size()),
                                    putCount,
                                    getCount,
                                    hitCount,
                                    missCount,
                                    hitRatio,
                                    missRatio,
                                    this.name));
                    this.lastExecution = currentTime;
                }
            }
        }

    }






    static final class CacheDataContainer<K,V> {

        private final String name;
        private final boolean sizeLimit;
        private final int maxSize;
        private final boolean traceExecution;
        private final Logger logger;

        private final ConcurrentHashMap<K,CacheEntry<V>> container;
        private final Object[] fifo;
        private int fifoPointer;


        CacheDataContainer(final String name, final int initialCapacity,
                final int maxSize, final boolean traceExecution, final Logger logger) {

            super();

            this.name = name;
            this.container = new ConcurrentHashMap<K,CacheEntry<V>>(initialCapacity, 0.9f, 2);
            this.maxSize = maxSize;
            this.sizeLimit = (maxSize >= 0);
            if (this.sizeLimit) {
                this.fifo = new Object[this.maxSize];
                Arrays.fill(this.fifo, null);
            } else {
                this.fifo = null;
            }
            this.fifoPointer = 0;
            this.traceExecution = traceExecution;
            this.logger = logger;

        }


        public CacheEntry<V> get(final Object key) {
            // FIFO is not used for this --> better performance, but no LRU (only insertion order will apply)
            return this.container.get(key);
        }


        public Set<K> keySet() {
            // This 'strange' cast is needed in order to keep compatibility with Java 6 and 7, when compiling with
            // Java 8. The reason is, the return type of Java 8's ConcurrentHashMap#keySet() changed to a class
            // called KeySetView, implementing java.util.Set but new in Java 8. This made this code throw a
            // java.lang.NoSuchMethodError when executed in Java 6 or 7.
            // By adding the cast, we are binding bytecode not to the specific keySet() method of ConcurrentHashMap,
            // but to the one defined at the java.util.Map interface, which simply returns java.util.Set.
            return ((Map<K,CacheEntry<V>>)this.container).keySet();
        }


        public int put(final K key, final CacheEntry<V> value) {
            if (this.traceExecution) {
                return putWithTracing(key, value);
            }
            return putWithoutTracing(key, value);
        }


        private int putWithoutTracing(final K key, final CacheEntry<V> value) {
            // If we are not tracing, it's better to avoid the size() operation which has
            // some performance implications in ConcurrentHashMap (iteration and counting these maps
            // is slow if they are big)

            final CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing != null) {
                // When not in 'trace' mode, will always return -1
                return -1;
            }

            if (this.sizeLimit) {
                synchronized (this.fifo) {
                    final Object removedKey = this.fifo[this.fifoPointer];
                    if (removedKey != null) {
                        this.container.remove(removedKey);
                    }
                    this.fifo[this.fifoPointer] = key;
                    this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
                }
            }

            return -1;

        }

        private synchronized int putWithTracing(final K key, final CacheEntry<V> value) {

            final CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing == null) {
                if (this.sizeLimit) {
                    final Object removedKey = this.fifo[this.fifoPointer];
                    if (removedKey != null) {
                        final CacheEntry<V> removed = this.container.remove(removedKey);
                        if (removed != null) {
                            final Integer newSize = Integer.valueOf(this.container.size());
                            this.logger.trace(
                                    "[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Max size exceeded for cache \"{}\". Removing entry for key \"{}\". New size is {}.",
                                    new Object[] {TemplateEngine.threadIndex(), this.name, newSize, this.name, removedKey, newSize});
                        }
                    }
                    this.fifo[this.fifoPointer] = key;
                    this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
                }
            }
            return this.container.size();

        }


        public int remove(final K key) {
            if (this.traceExecution) {
                return removeWithTracing(key);
            }
            return removeWithoutTracing(key);
        }


        private int removeWithoutTracing(final K key) {
            // FIFO is also updated to avoid 'removed' keys remaining at FIFO (which could end up reducing cache size to 1)
            final CacheEntry<V> removed = this.container.remove(key);
            if (removed != null) {
                if (this.sizeLimit && key != null) {
                    for (int i = 0; i < this.maxSize; i++) {
                        if (key.equals(this.fifo[i])) {
                            this.fifo[i] = null;
                            break;
                        }
                    }
                }
            }
            return -1;
        }


        private synchronized int removeWithTracing(final K key) {
            // FIFO is also updated to avoid 'removed' keys remaining at FIFO (which could end up reducing cache size to 1)
            final CacheEntry<V> removed = this.container.remove(key);
            if (removed == null) {
                // When tracing is active, this means nothing was removed
                return -1;
            }
            if (this.sizeLimit && key != null) {
                for (int i = 0; i < this.maxSize; i++) {
                    if (key.equals(this.fifo[i])) {
                        this.fifo[i] = null;
                        break;
                    }
                }
            }
            return this.container.size();
        }


        public void clear() {
            this.container.clear();
        }


        public int size() {
            return this.container.size();
        }

    }




    static final class CacheEntry<V> {

        private final SoftReference<V> cachedValueReference;
        private final long creationTimeInMillis;

        // Although we will use the reference for normal operation for cleaner code, this
        // variable will act as an "anchor" to avoid the value to be cleaned if we don't
        // want the reference type to be "soft"
        @SuppressWarnings("unused")
        private final V cachedValueAnchor;


        CacheEntry(final V cachedValue, final boolean useSoftReferences) {

            super();

            this.cachedValueReference = new SoftReference<V>(cachedValue);
            this.cachedValueAnchor = (!useSoftReferences? cachedValue : null);
            this.creationTimeInMillis = System.currentTimeMillis();

        }

        public <K> V getValueIfStillValid(final String cacheMapName,
                final K key, final ICacheEntryValidityChecker<? super K, ? super V> checker,
                final boolean traceExecution, final Logger logger) {

            final V cachedValue = this.cachedValueReference.get();

            if (cachedValue == null) {
                // The soft reference has been cleared by GC -> Memory could be running low
                if (traceExecution) {
                    logger.trace(
                            "[THYMELEAF][{}][*][{}][CACHE_DELETED_REFERENCES] Some entries at cache \"{}\" " +
                            "seem to have been sacrificed by the Garbage Collector (soft references).",
                            new Object[] {TemplateEngine.threadIndex(), cacheMapName, cacheMapName});
                }
                return null;
            }
            if (checker == null || checker.checkIsValueStillValid(key, cachedValue, this.creationTimeInMillis)) {
                return cachedValue;
            }
            return null;
        }

        public long getCreationTimeInMillis() {
            return this.creationTimeInMillis;
        }

    }


}
