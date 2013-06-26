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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Specialization of {@link VariablesMap} that forwards read
 *   calls to the parameters (not attributes) of an HttpServletRequest. 
 * </p>
 * <p>
 *   Any write call (like {@link #put(Object, Object)}) will result in an exception.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
class WebRequestParamsVariablesMap extends VariablesMap<String,String[]> {
    
    
    private static final long serialVersionUID = 5989404108258200389L;
    
    
    private final Map<String,String[]> parameterMap;
    
    

    @SuppressWarnings("unchecked")
    WebRequestParamsVariablesMap(final HttpServletRequest request) {
        super(1, 1.0f);
        Validate.notNull(request, "Request cannot be null");
        this.parameterMap = request.getParameterMap();
    }

    

    @Override
    public int size() {
        return this.parameterMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.parameterMap.isEmpty();
    }

    @Override
    public String[] get(final Object key) {
        return this.parameterMap.get(key);
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.parameterMap.containsKey(key);
    }

    @Override
    public String[] put(final String key, final String[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String[]> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.parameterMap.containsValue(value);
    }

    @Override
    public WebRequestParamsVariablesMap clone() {
        return (WebRequestParamsVariablesMap) super.clone();
    }

    @Override
    public Set<String> keySet() {
        return this.parameterMap.keySet();
    }

    @Override
    public Collection<String[]> values() {
        return this.parameterMap.values();
    }

    @Override
    public Set<java.util.Map.Entry<String,String[]>> entrySet() {
        return this.parameterMap.entrySet();
    }

    @Override
    public String toString() {
        return this.parameterMap.toString();
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.parameterMap.hashCode();
        return result;
    }


    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebRequestParamsVariablesMap other = (WebRequestParamsVariablesMap) obj;
        if (this.parameterMap == null) {
            if (other.parameterMap != null) {
                return false;
            }
        } else if (!this.parameterMap.equals(other.parameterMap)) {
            return false;
        }
        return true;
    }
    
    
    
}
