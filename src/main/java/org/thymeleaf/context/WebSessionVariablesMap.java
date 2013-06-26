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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *   Specialization of {@link VariablesMap} that forwards read and write
 *   calls to the attributes of an HttpSession. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
class WebSessionVariablesMap extends VariablesMap<String,Object> {
    
    
    private static final long serialVersionUID = 3866833203758601975L;
    
    
    private final HttpSession session;
    
    

    WebSessionVariablesMap(final HttpSession session) {
        super(1, 1.0f);
        this.session = session;
    }

    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public int size() {
        if (this.session == null) {
            return 0;
        }
        int size = 0;
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNames.nextElement();
            size++;
        }
        return size;
    }

    
    
    @Override
    public boolean isEmpty() {
        if (this.session == null) {
            return true;
        }
        return !this.session.getAttributeNames().hasMoreElements();
    }

    
    
    @Override
    public Object get(final Object key) {
        if (this.session == null) {
            return null;
        }
        return this.session.getAttribute((String)key);
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(final Object key) {
        if (this.session == null) {
            return false;
        }
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement(); 
            if (key == null) {
                if (attributeName == null) {
                    return true;
                }
            } else {
                if (key.equals(attributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    
    
    @Override
    public Object put(final String key, final Object value) {
        if (this.session == null) {
            throw new IllegalStateException(
                    "Cannot put attribute into session: no HTTP session exists!");
        }
        this.session.setAttribute(key, value);
        return value;
    }

    
    
    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        if (this.session == null) {
            throw new IllegalStateException(
                    "Cannot put attributes into session: no HTTP session exists!");
        }
        for (final Map.Entry<? extends String, ? extends Object> mEntry : m.entrySet()) {
            this.session.setAttribute(mEntry.getKey(), mEntry.getValue());
        }
    }

    
    
    @Override
    public Object remove(final Object key) {
        if (this.session == null) {
            throw new IllegalStateException(
                    "Cannot remove attribute from session: no HTTP session exists!");
        }
        final Object value = this.session.getAttribute((String)key);
        this.session.removeAttribute((String)key);
        return value;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        if (this.session == null) {
            throw new IllegalStateException(
                    "Cannot remove attribute from session: no HTTP session exists!");
        }
        final List<String> attributeNamesList = new ArrayList<String>(5);
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNamesList.add(attributeNames.nextElement()); 
        }
        for (final String attributeName : attributeNamesList) {
            this.session.removeAttribute(attributeName);
        }
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(final Object value) {
        if (this.session == null) {
            return false;
        }
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = this.session.getAttribute(attributeName);
            if (value == null) {
                if (attributeValue == null) {
                    return true;
                }
            } else {
                if (value.equals(attributeValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    
    
    @Override
    public WebSessionVariablesMap clone() {
        return (WebSessionVariablesMap) super.clone();
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> keySet() {
        if (this.session == null) {
            return Collections.emptySet();
        }
        final Set<String> keySet = new LinkedHashSet<String>(5);
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            keySet.add(attributeNames.nextElement());
        }
        return keySet;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> values() {
        if (this.session == null) {
            return Collections.emptyList();
        }
        final List<Object> values = new ArrayList<Object>(5);
        final Enumeration<String> attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            values.add(this.session.getAttribute(attributeName));
        }
        return values;
    }

    
    
    @Override
    public Set<java.util.Map.Entry<String,Object>> entrySet() {
        return getAttributeMap(this.session).entrySet();
    }

    
    
    @Override
    public String toString() {
        return getAttributeMap(this.session).toString();
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.session.hashCode();
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
        final WebSessionVariablesMap other = (WebSessionVariablesMap) obj;
        if (this.session == null) {
            if (other.session != null) {
                return false;
            }
        } else if (!this.session.equals(other.session)) {
            return false;
        }
        return true;
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private static Map<String,Object> getAttributeMap(final HttpSession session) {
        
        if (session == null) {
            return Collections.emptyMap();
        }
        
        final Map<String,Object> attributeMap = new LinkedHashMap<String, Object>(6,1.0f);
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = session.getAttribute(attributeName);
            attributeMap.put(attributeName, attributeValue);
        }
        
        return attributeMap;
        
    }
    
    
    
}
