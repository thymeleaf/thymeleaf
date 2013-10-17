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

import javax.servlet.http.HttpServletRequest;
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
    
    
    private final HttpServletRequest request;
    
    

    WebSessionVariablesMap(final HttpServletRequest request) {
        super(1, 1.0f);
        this.request = request;
    }

    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public int size() {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return 0;
        }
        int size = 0;
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNames.nextElement();
            size++;
        }
        return size;
    }

    
    
    @Override
    public boolean isEmpty() {
        final HttpSession session = this.request.getSession(false);
        return session == null || !session.getAttributeNames().hasMoreElements();
    }

    
    
    @Override
    public Object get(final Object key) {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute((String)key);
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(final Object key) {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return false;
        }
        final Enumeration<String> attributeNames = session.getAttributeNames();
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
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            throw new IllegalStateException(
                    "Cannot put attribute into session: no HTTP session exists!");
        }
        session.setAttribute(key, value);
        return value;
    }

    
    
    @Override
    public void putAll(final Map<? extends String, ?> m) {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            throw new IllegalStateException(
                    "Cannot put attributes into session: no HTTP session exists!");
        }
        for (final Map.Entry<? extends String, ?> mEntry : m.entrySet()) {
            session.setAttribute(mEntry.getKey(), mEntry.getValue());
        }
    }

    
    
    @Override
    public Object remove(final Object key) {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            throw new IllegalStateException(
                    "Cannot remove attribute from session: no HTTP session exists!");
        }
        final Object value = session.getAttribute((String)key);
        session.removeAttribute((String)key);
        return value;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            throw new IllegalStateException(
                    "Cannot remove attribute from session: no HTTP session exists!");
        }
        final List<String> attributeNamesList = new ArrayList<String>(5);
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNamesList.add(attributeNames.nextElement()); 
        }
        for (final String attributeName : attributeNamesList) {
            session.removeAttribute(attributeName);
        }
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(final Object value) {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return false;
        }
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = session.getAttribute(attributeName);
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
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return Collections.emptySet();
        }
        final Set<String> keySet = new LinkedHashSet<String>(5);
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            keySet.add(attributeNames.nextElement());
        }
        return keySet;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> values() {
        final HttpSession session = this.request.getSession(false);
        if (session == null) {
            return Collections.emptyList();
        }
        final List<Object> values = new ArrayList<Object>(5);
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            values.add(session.getAttribute(attributeName));
        }
        return values;
    }

    
    
    @Override
    public Set<java.util.Map.Entry<String,Object>> entrySet() {
        final HttpSession session = this.request.getSession(false);
        return getAttributeMap(session).entrySet();
    }

    
    
    @Override
    public String toString() {
        final HttpSession session = this.request.getSession(false);
        return getAttributeMap(session).toString();
    }

    

    @Override
    public int hashCode() {
        final HttpSession session = this.request.getSession(false);
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + session.hashCode();
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
        final HttpSession session = this.request.getSession(false);
        final HttpSession otherSession = other.request.getSession(false);
        if (session == null) {
            if (otherSession != null) {
                return false;
            }
        } else if (!session.equals(otherSession)) {
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
