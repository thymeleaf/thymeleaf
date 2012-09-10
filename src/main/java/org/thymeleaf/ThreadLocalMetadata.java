/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.Validate;

/*
 * Holds the Thread-Local metadata needed at several points in the
 * execution flow.
 * 
 * Also, takes care of nested executions by using a Deque instead of
 * a single instance when executing.
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.13
 */
class ThreadLocalMetadata<T> {

    protected static final Logger logger = LoggerFactory.getLogger(ThreadLocalMetadata.class);
    
    private final ThreadLocal<ThreadLocalMetadataContainer<T>> threadLocal;
    
    
    public ThreadLocalMetadata() {
        super();
        this.threadLocal = new ThreadLocal<ThreadLocalMetadataContainer<T>>();
    }

    private void checkInitialized() {
        if (this.threadLocal.get() == null) {
            this.threadLocal.set(new ThreadLocalMetadataContainer<T>());
        }
    }
    
    void add(final T value) {
        checkInitialized();
        this.threadLocal.get().pushValue(value);
    }
    
    void remove() {
        checkInitialized();
        this.threadLocal.get().popValue();
    }
    
    public T get() {
        checkInitialized();
        return this.threadLocal.get().getValue();
    }
    
    
    
    protected static class ThreadLocalMetadataContainer<T> {
        
        private T single = null;
        private Deque<T> multiple = null;
        
        
        public ThreadLocalMetadataContainer() {
            super();
        }
        
        public void pushValue(final T value) {
            Validate.notNull(value, "Value cannot be null");
            if (this.single == null && this.multiple == null) {
                this.single = value;
            } else if (this.multiple == null) {
                this.multiple = new ArrayDeque<T>();
                this.multiple.addLast(this.single);
                this.multiple.addLast(value);
                this.single = null;
            } else if (this.multiple != null) {
                this.multiple.addLast(value);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("ThreadLocalContainer is in an inconsistent state in 'pushValue' operation!");
                }
            }
        }
        
        public T popValue() {
            if (this.single != null && this.multiple == null) {
                final T value = this.single;
                this.single = null;
                return value;
            } else if (this.single == null && this.multiple != null) {
                final T value = this.multiple.pollLast();
                if (this.multiple.size() == 1) {
                    this.single = this.multiple.getLast();
                    this.multiple = null;
                }
                return value;
            }
            if (logger.isWarnEnabled()) {
                logger.warn("ThreadLocalContainer is in an inconsistent state in 'popValue' operation!");
            }
            return null;
        }
        
        public T getValue() {
            if (this.single != null && this.multiple == null) {
                return this.single;
            } else if (this.single == null && this.multiple != null) {
                return this.multiple.getLast();
            }
            return null;
        }
        
    }
    
}