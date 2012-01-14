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
package org.thymeleaf.context;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 *   Special implementation of the {@link Map} interface that
 *   will be used for containing context variables in {@link IContext} 
 *   implementations.
 * </p>
 * <p>
 *   Constructors in this class mimic those in {@link LinkedHashMap} and
 *   have the same meaning.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class VariablesMap<K,V> extends LinkedHashMap<K,V> {

    private static final long serialVersionUID = 6785956724279950873L;
    
    

    public VariablesMap() {
        super();
    }

    public VariablesMap(final int initialCapacity, 
            final float loadFactor, final boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public VariablesMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public VariablesMap(final int initialCapacity) {
        super(initialCapacity);
    }

    public VariablesMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }

    
    
    
}
