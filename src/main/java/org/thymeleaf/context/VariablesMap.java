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
package org.thymeleaf.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.MapPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;

/**
 * <p>
 *   Special implementation of the {@link Map} interface that
 *   will be used for containing context variables in {@link IContext} 
 *   implementations.
 * </p>
 * <p>
 *   Constructors in this class mimic those in {@link HashMap} and
 *   have the same meaning.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Michal Kreuzman
 * 
 * @since 1.0
 *
 */
public class VariablesMap<K,V> extends HashMap<K,V> {

    private static final long serialVersionUID = 6785956724279950873L;

    private List<IContextVariableRestriction> restrictions = null;

    
    
    static {
        OgnlRuntime.setPropertyAccessor(VariablesMap.class, new VariablesMapPropertyAccessor());
    }

    
    
    
    public VariablesMap() {
        super();
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


    
    
    public List<IContextVariableRestriction> getRestrictions() {
        return this.restrictions;
    }

    public void setRestrictions(List<IContextVariableRestriction> restrictions) {
        this.restrictions = restrictions;
    }
    
    

    @Override
    public V get(final Object key) {

        if (this.restrictions != null && !this.restrictions.isEmpty()) {
            for (final IContextVariableRestriction restriction : this.restrictions) {
                if (restriction != null) {
                    restriction.checkAccess(this, (String)key);
                }
            }
        }
        
        return super.get(key);

    }

    
    
    
    /**
     * Extension of {@code MapPropertyAccessor} that handles getting of size
     * property. When there is entry with key "size" it is returned instead of
     * size property from {@code VariablesMap}. Otherwise this property accessor
     * works exactly same like {@code MapPropertyAccessor}.
     * 
     * @author Michal Kreuzman
     * 
     * @see MapPropertyAccessor
     * 
     * @since 2.0
     */
    private static class VariablesMapPropertyAccessor extends MapPropertyAccessor {
        
        private static final String RESERVED_SIZE_PROPERTY_NAME = "size";

        VariablesMapPropertyAccessor() {
            super();
        }
        
        @Override
        @SuppressWarnings("rawtypes")
        public Object getProperty(final Map context, final Object target, final Object name) throws OgnlException {
            
            if (!RESERVED_SIZE_PROPERTY_NAME.equals(name)) {
                return super.getProperty(context, target, name);
            }

            if (!(target instanceof VariablesMap)) {
                throw new IllegalStateException(
                        "Wrong target type. This property accessor is only usable for VariableMap class.");
            }

            final Map map = (Map) target;
            Object result = map.get(RESERVED_SIZE_PROPERTY_NAME);
            if (result == null) {
                result = Integer.valueOf(map.size());
            }
            return result;
            
        }
        
    }
    
}
