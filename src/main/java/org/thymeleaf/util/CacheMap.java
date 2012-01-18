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

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;


/**
 * <p>
 *   Generic cache map implementation.
 * </p>
 * <p>
 *   Features:
 * </p>
 * <ul>
 *   <li>A specific logger is maintained for these maps: <tt>org.thymeleaf.TemplateEngine.CACHE</tt></li>
 *   <li>These maps apply an LRU policy and allow setting a <tt>maxSize</tt> 
 *       parameter, in which case the oldest non-used entry will be removed from
 *       cache if maximum size is reached. Setting <tt>maxSize</tt> to <tt>-1</tt>
 *       is equivalent to not setting it at all (no limit will be established).</li>
 *   <li>Can be set to use {@link SoftReference}s in order to make the cache 
 *       memory-sensitive. If this parameter is set to true, the Garbage Collector will
 *       be able to remove entries from the cache if it is needed, before raising
 *       an out-of-memory error (although this depends on the specific VM implementation).</li>
 *   <li>These maps are completely thread-safe. A read-write lock is used that
 *       allows concurrent read operations but prevents write operations to be executed
 *       concurrently with any other read or write operation.</li>
 *   <li>An {@link ICacheMapEntryValidityChecker} object can be specified when building a cache map
 *       (and also when executing specific <tt>get</tt> operations) in order to ensure that the returned
 *       entry/ies not only exist at the cache, but also can be considered <i>valid</i> according
 *       to the criteria defined in these checker implementations.</li> 
 *   <li>These maps do not create any threads, so their use is perfectly safe in 
 *       thread-sensitive scenarios like EJB containers or some cloud PaaS environments.</li>
 *   <li>If the <i>cache logger</i> is set to <i>trace</i>, a <i>cache report</i>
 *       will be output every 5 minutes explaining the status of every existing cache map. Due
 *       to the fact of no separate threads being created for reporting, this report will only 
 *       checked to be produced when any <tt>get</tt> or <tt>set</tt> operations are executed 
 *       against the cache map, so this 5-minute period can be longer if no operations are 
 *       executed.</li>
 * </ul>
 * 
 *
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 *
 * @since 1.1.3
 *
 * @param <K> The type of the cache map keys
 * @param <V> The type of the cache map values
 */
public final class CacheMap<K, V> implements Serializable {

    private static final long serialVersionUID = 4399112135561524032L;

    
//    public static final String CACHE_LOGGER_NAME = TemplateEngine.class.getName() + ".CACHE";
//    protected static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class.getName() + ".CACHE");

    private static final long REPORT_INTERVAL = 300000L; // 5 minutes
    private static final String REPORT_FORMAT = 
            "[THYMELEAF][*][*][*][CACHE_REPORT] %12s elements | %8s puts | %12s gets | %12s hits | %12s misses - [%s]";
    private volatile long lastExecution = System.currentTimeMillis();
    
    private final String name;
    private final boolean useSoftReferences;
    private final int maxSize;
    private final CacheDataContainer<K,V> dataContainer;
    private final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker;

    private final boolean traceExecution;
    private final Logger logger;
    
    private final AtomicLong getCount;
    private final AtomicLong putCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;
    
    

    
    
    
    

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final boolean traceExecution,
            final Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, null, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker, 
            final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, entryValidityChecker, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final int maxSize, 
            final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, maxSize, null, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final int maxSize, 
            final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker, 
            final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, 0.75f, maxSize, entryValidityChecker, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor, 
            final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, loadFactor, -1, null, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker, 
            final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, loadFactor, -1, entryValidityChecker, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final int maxSize, final boolean traceExecution, final Logger logger) {
        this(name, useSoftReferences, initialCapacity, loadFactor, maxSize, null, traceExecution, logger);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final int maxSize, final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker, 
            final boolean traceExecution, final Logger logger) {
        
        super();

        Validate.notEmpty(name, "Name cannot be null or empty");
        Validate.isTrue(initialCapacity > 0, "Initial capacity must be > 0");
        Validate.isTrue(loadFactor >= 0.0f && loadFactor <= 1.0f, "Load factor must be between 0 and 1");
        Validate.isTrue(maxSize != 0, "Cache Map max size must be either -1 (no limit) or > 0");
        if (traceExecution) {
            Validate.notNull(logger, "If the 'trace execution' flag is set, logger cannot be null");
        }
        
        this.name = name;
        this.useSoftReferences = useSoftReferences;
        this.maxSize = maxSize;
        this.entryValidityChecker = entryValidityChecker;
        
        this.traceExecution = traceExecution;
        this.logger = logger;
        
        this.dataContainer = 
                new CacheDataContainer<K,V>(this.name, initialCapacity, loadFactor, maxSize, this.traceExecution, this.logger);
        
        this.getCount = new AtomicLong(0);
        this.putCount = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);

        if (this.logger != null) {
            if (this.maxSize < 0) {
                this.logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache map {}. Soft references {}.", 
                        this.name, (this.useSoftReferences? "are used" : "not used"));
            } else {
                this.logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache map {}. Max size: {}. Soft references {}.", 
                        new Object[] {this.name, Integer.valueOf(this.maxSize), (this.useSoftReferences? "are used" : "not used")});
            }
        }
        
    }


    
    
    // -----

    
    
    public void put(final K key, final V value) {

        incrementReportEntity(this.putCount);
        
        final CacheEntry<V> entry = new CacheEntry<V>(value, this.useSoftReferences);
        
        // newSize will be -1 if traceExecution is false
        int newSize = this.dataContainer.put(key, entry);
        
        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][{}][CACHE_ADD][{}] Adding cache entry in cache map \"{}\" for key \"{}\". New size is {}.", 
                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
        }
        
        outputReportIfNeeded();
        
    }
    

    
    public V get(final K key) {
        return get(key, this.entryValidityChecker);
    }
    

    
    public V get(final K key, final ICacheMapEntryValidityChecker<? super K, ? super V> validityChecker) {
        
        incrementReportEntity(this.getCount);
        
        final CacheEntry<V> resultEntry = this.dataContainer.get(key);
        
        if (resultEntry == null) {
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_MISS] Cache miss in cache map \"{}\" for key \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
            }
            outputReportIfNeeded();
            return null;
        }

        final V resultValue = 
                resultEntry.getValueIfStillValid(this.name, key, validityChecker, this.traceExecution, this.logger);
        if (resultValue == null) {
            final int newSize = this.dataContainer.remove(key);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_REMOVE][{}] Removing cache entry in cache map \"{}\" (Entry \"{}\" is not valid anymore). New size is {}.",
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
                this.logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_MISS] Cache miss in cache map \"{}\" for key \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
            }
            incrementReportEntity(this.missCount);
            outputReportIfNeeded();
            return null;
        }
        
        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][{}][CACHE_HIT] Cache hit in cache map \"{}\" for key \"{}\".", 
                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
        }
        
        incrementReportEntity(this.hitCount);
        outputReportIfNeeded();
        return resultValue;
        
    }

    

    public void clear() {
        
        this.dataContainer.clear();
        
        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][0] Removing ALL cache entries in cache map \"{}\". New size is 0.", 
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name});
        }
        
    }
    
    
    
    public void clearKey(final K key) {

        final int newSize = this.dataContainer.remove(key);
        
        if (this.traceExecution && newSize != -1) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][{}] Removed cache entry in cache map \"{}\" for key \"{}\". New size is {}.", 
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

    
    
    // -----

    
    private void incrementReportEntity(final AtomicLong entity) {
        if (this.traceExecution) {
            entity.incrementAndGet();
        }
    }

    
    private void outputReportIfNeeded() {
        
        if (this.traceExecution) { // fail fast
            
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) { // first check without need to sync
                synchronized (this) {
                    if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) { // double-checking OK thanks to volatile
                        this.logger.trace(
                                String.format(REPORT_FORMAT,
                                        Integer.valueOf(size()),
                                        Long.valueOf(this.putCount.get()),
                                        Long.valueOf(this.getCount.get()),
                                        Long.valueOf(this.hitCount.get()),
                                        Long.valueOf(this.missCount.get()),
                                        this.name));
                        this.lastExecution = currentTime;
                    }
                }
            }
            
        }
        
    }
    
    




    final static class CacheDataContainer<K,V> implements Serializable {

        private static final long serialVersionUID = -7836660946715420768L;
        
        private final String name;
        private final boolean sizeLimit;
        private final int maxSize;
        private final boolean traceExecution;
        private final Logger logger;
        
        private final ConcurrentHashMap<K,CacheEntry<V>> container;
        private final Object[] fifo;
        private int fifoPointer;


        public CacheDataContainer(final String name, final int initialCapacity, final float loadFactor, 
                final int maxSize, final boolean traceExecution, final Logger logger) {
            
            super();

            this.name = name;
            this.container = new ConcurrentHashMap<K,CacheEntry<V>>(initialCapacity, loadFactor);
            this.maxSize = maxSize;
            this.sizeLimit = (maxSize < 0? false : true);
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
                                    "[THYMELEAF][{}][{}][{}][CACHE_REMOVE][{}] Max size exceeded for cache map \"{}\". Removing entry for key \"{}\". New size is {}.", 
                                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, newSize, this.name, removedKey, newSize});
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
            // FIFO is not updated, not a real benefit in doing it.
            this.container.remove(key);
            return -1;
        }

        
        private synchronized int removeWithTracing(final K key) {
            // FIFO is not updated, not a real benefit in doing it.
            final CacheEntry<V> removed = this.container.remove(key);
            if (removed == null) {
                // When tracing is active, this means nothing was removed
                return -1;
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




    final static class CacheEntry<V> {

        private final SoftReference<V> cachedValueReference;
        private final long creationTimeInMillis;
        
        // Although we will use the reference for normal operation for cleaner code, this
        // variable will act as an "anchor" to avoid the value to be cleaned if we don't
        // want the reference type to be "soft"
        @SuppressWarnings("unused") 
        private final V cachedValueAnchor;
        

        public CacheEntry(final V cachedValue, final boolean useSoftReferences) {

            super();

            this.cachedValueReference = new SoftReference<V>(cachedValue);
            this.cachedValueAnchor = (!useSoftReferences? cachedValue : null);
            this.creationTimeInMillis = System.currentTimeMillis();

        }

        public <K> V getValueIfStillValid(final String cacheMapName, 
                final K key, final ICacheMapEntryValidityChecker<? super K, ? super V> checker,
                final boolean traceExecution, final Logger logger) {

            final V cachedValue = this.cachedValueReference.get();

            if (cachedValue == null) {
                // The soft reference has been cleared by GC -> Memory could be running low
                if (traceExecution) {
                    logger.trace(
                            "[THYMELEAF][{}][*][{}][CACHE_DELETED_REFERENCES] Some entries at cache map \"{}\" " +
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
     * @since 1.1.3
     *
     * @param <K> The type of the cache map keys
     * @param <V> The type of the cache map values
     */
    public static interface ICacheMapEntryValidityChecker<K,V> extends Serializable {

        public boolean checkIsValueStillValid(final K key, final V value, final long entryCreationTimestamp);

    }


}
