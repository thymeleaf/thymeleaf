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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    
    public static final String CACHE_LOGGER_NAME = TemplateEngine.class.getName() + ".CACHE";
    protected static final Logger logger = LoggerFactory.getLogger(CACHE_LOGGER_NAME);

    private static final long REPORT_INTERVAL = 300000L; // 5 minutes
    private static long lastExecution = System.currentTimeMillis();

    private static final List<WeakReference<CacheMap<?, ?>>> caches = new ArrayList<WeakReference<CacheMap<?,?>>>();
    
    private final String name;
    private final boolean useSoftReferences;
    private final int maxSize;
    private final CacheDataContainer<K,V> dataContainer;
    private final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker;

    private final AtomicLong getCount;
    private final AtomicLong putCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    private final ReadWriteLock lock;
    private final Lock rLock;
    private final Lock wLock;
    
    

    
    
    
    

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity) {
        this(name, useSoftReferences, initialCapacity, -1, null);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker) {
        this(name, useSoftReferences, initialCapacity, -1, entryValidityChecker);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final int maxSize) {
        this(name, useSoftReferences, initialCapacity, maxSize, null);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final int maxSize, 
            final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker) {
        this(name, useSoftReferences, initialCapacity, 0.75f, maxSize, entryValidityChecker);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor) {
        this(name, useSoftReferences, initialCapacity, loadFactor, -1, null);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker) {
        this(name, useSoftReferences, initialCapacity, loadFactor, -1, entryValidityChecker);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final int maxSize) {
        this(name, useSoftReferences, initialCapacity, loadFactor, maxSize, null);
    }

    public CacheMap(final String name, final boolean useSoftReferences, 
            final int initialCapacity, final float loadFactor,
            final int maxSize, final ICacheMapEntryValidityChecker<? super K, ? super V> entryValidityChecker) {
        
        super();

        Validate.notEmpty(name, "Name cannot be null or empty");
        Validate.isTrue(initialCapacity > 0, "Initial capacity must be > 0");
        Validate.isTrue(loadFactor >= 0.0f && loadFactor <= 1.0f, "Load factor must be between 0 and 1");
        Validate.isTrue(maxSize != 0, "Cache Map max size must be either -1 (no limit) or > 0");
        
        this.name = name;
        this.useSoftReferences = useSoftReferences;
        this.maxSize = maxSize;
        this.entryValidityChecker = entryValidityChecker;
        
        this.dataContainer =
                new CacheDataContainer<K,V>(this.name, initialCapacity, loadFactor, maxSize);
                
        this.getCount = new AtomicLong(0);
        this.putCount = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
        
        this.lock = new ReentrantReadWriteLock();
        this.rLock = this.lock.readLock();
        this.wLock = this.lock.writeLock();

        synchronized(CacheMap.class) {
            caches.add(new WeakReference<CacheMap<?,?>>(this));
            Collections.sort(caches, new Comparator<WeakReference<CacheMap<?,?>>>() {
                public int compare(final WeakReference<CacheMap<?, ?>> o1Ref, final WeakReference<CacheMap<?, ?>> o2Ref) {
                    final CacheMap<?,?> o1 = o1Ref.get();
                    final CacheMap<?,?> o2 = o2Ref.get();
                    if (o1 == null) {
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
        
        if (this.maxSize < 0) {
            logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache map {}. Soft references {}.", 
                    this.name, (this.useSoftReferences? "are used" : "not used"));
        } else {
            logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache map {}. Max size: {}. Soft references {}.", 
                    new Object[] {this.name, Integer.valueOf(this.maxSize), (this.useSoftReferences? "are used" : "not used")});
        }
        
    }


    
    
    // -----

    
    
    public V put(final K key, final V value) {

        incrementReportEntity(this.putCount);
        
        final CacheEntry<V> entry = new CacheEntry<V>(value, this.useSoftReferences);
        Integer newSize = null;
        this.wLock.lock();
        try {
            this.dataContainer.put(key, entry);
            newSize = Integer.valueOf(this.dataContainer.size());
        } finally {
            this.wLock.unlock();
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "[THYMELEAF][{}][{}][{}][CACHE_ADD][{}] Adding cache entry in cache map \"{}\" for key \"{}\". New size is {}.", 
                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, newSize, this.name, key, newSize});
        }
        
        checkReportExecution();
        return value;
        
    }
    

    
    public V get(final K key) {
        return get(key, this.entryValidityChecker);
    }
    

    
    public V get(final K key, final ICacheMapEntryValidityChecker<? super K, ? super V> validityChecker) {
        
        incrementReportEntity(this.getCount);
        
        this.rLock.lock();
        CacheEntry<V> resultEntry = null;
        try {
            resultEntry = this.dataContainer.get(key);
        } finally {
            this.rLock.unlock();
        }
        
        if (resultEntry == null) {
            incrementReportEntity(this.missCount);
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_MISS] Cache miss in cache map \"{}\" for key \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
            }
            checkReportExecution();
            return null;
        }

        final V resultValue = 
                resultEntry.getValueIfStillValid(this.name, key, validityChecker);
        if (resultValue == null) {
            Integer newSize = null;
            this.wLock.lock();
            try {
                this.dataContainer.remove(key);
                newSize = Integer.valueOf(this.dataContainer.size());
            } finally {
                this.wLock.unlock();
            }
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_REMOVE][{}] Removing cache entry in cache map \"{}\" (Entry \"{}\" is not valid anymore). New size is {}.",
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, newSize, this.name, key, newSize});
                logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_MISS] Cache miss in cache map \"{}\" for key \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
            }
            incrementReportEntity(this.missCount);
            checkReportExecution();
            return null;
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "[THYMELEAF][{}][{}][{}][CACHE_HIT] Cache hit in cache map \"{}\" for key \"{}\".", 
                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.name, this.name, key});
        }
        
        incrementReportEntity(this.hitCount);
        checkReportExecution();
        return resultValue;
        
    }

    

    public void clear() {
        
        this.wLock.lock();
        try {
            this.dataContainer.clear();
        } finally {
            this.wLock.unlock();
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][0] Removing ALL cache entries in cache map \"{}\". New size is 0.", 
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name});
        }
        
    }
    
    
    
    public void clearKey(final K key) {

        CacheEntry<V> e = null;
        Integer newSize = null;
        this.wLock.lock();
        try {
            e = this.dataContainer.remove(key);
            newSize = Integer.valueOf(this.dataContainer.size());
        } finally {
            this.wLock.unlock();
        }
        
        if (logger.isTraceEnabled() && null != e) {
            logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][{}] Removed cache entry in cache map \"{}\" for key \"{}\". New size is {}.", 
                    new Object[] {TemplateEngine.threadIndex(), this.name, newSize, this.name, key, newSize});
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

    
    private static void incrementReportEntity(final AtomicLong entity) {
        if (logger.isTraceEnabled()) {
            entity.incrementAndGet();
        }
    }

    
    private static void checkReportExecution() {
        
        if (logger.isTraceEnabled()) { // fail fast
            
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - lastExecution) >= REPORT_INTERVAL) { // first check without need to sync
                synchronized (CacheMap.class) {
                    if ((currentTime - lastExecution) >= REPORT_INTERVAL) {
                        outputReport();
                        lastExecution = currentTime;
                    }
                }
            }
            
        }
        
    }
    
    
    
    // Should be called from a synchronized block on the class's semaphore
    private static void outputReport() {
            
        // Size, Puts, Gets, Hits, Misses, Name
        final String format = "\n%12s  %8s  %12s  %12s  %12s  %s";

        final StringBuilder sb = new StringBuilder("===== CacheMap Report =====");

        sb.append(String.format(format, "Size", "Puts", "Gets", "Hits", "Misses", "Name"));

        for(final WeakReference<CacheMap<?, ?>> ref : caches) {
            
            final CacheMap<?, ?> cacheMap = ref.get();
            if(cacheMap == null) {
                continue;
            }
            
            sb.append(String.format(format,
                Integer.valueOf(cacheMap.size()),
                Long.valueOf(cacheMap.putCount.get()),
                Long.valueOf(cacheMap.getCount.get()),
                Long.valueOf(cacheMap.hitCount.get()),
                Long.valueOf(cacheMap.missCount.get()),
                cacheMap.name));

        }

        sb.append("\n=====================");

        logger.trace(sb.toString());
        
    }
    





    final static class CacheDataContainer<K,V> extends LinkedHashMap<K,CacheEntry<V>> {

        private static final long serialVersionUID = -5181808911951171469L;

        private final String cacheMapName;
        private final boolean sizeLimit;
        private final int maxSize;


        public CacheDataContainer(final String cacheMapName, 
                final int initialCapacity, final float loadFactor, final int maxSize) {
            super(initialCapacity, loadFactor, true);
            this.cacheMapName = cacheMapName;
            this.maxSize = maxSize;
            this.sizeLimit = (maxSize < 0? false : true);
        }


        @Override
        protected boolean removeEldestEntry(final Map.Entry<K,CacheEntry<V>> eldest) {
            if (!this.sizeLimit) {
                return false;
            }
            final boolean shouldRemove = size() > this.maxSize;
            if (shouldRemove && CacheMap.logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}][{}][{}][CACHE_REMOVE][{}] Max size exceeded for cache map \"{}\". Removing entry for key \"{}\". New size is {}.", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), this.cacheMapName, Integer.valueOf(size() - 1), this.cacheMapName, eldest.getKey(), Integer.valueOf(size() - 1)});
            }
            return shouldRemove;
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

        public <K> V getValueIfStillValid(final String cacheMapName, final K key, final ICacheMapEntryValidityChecker<? super K, ? super V> checker) {

            final V cachedValue = this.cachedValueReference.get();

            if (cachedValue == null) {
                // The soft reference has been cleared by GC -> Memory could be running low
                CacheMap.logger.warn(
                        "[THYMELEAF][{}][*][CACHE_MEMHIGH] Some entries at cache map \"{}\" " +
                        "seem to have been sacrificed by the Garbage Collector, and this " +
                        "could mean JVM memory is running low (although this depends on your VM's " +
                        "specific implementation). Please check your memory settings and, if you are using" +
                        "the HotSpot VM, check that you are running it in 'server' mode.",
                        cacheMapName, TemplateEngine.threadIndex());
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
