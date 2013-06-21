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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Specialization of {@link VariablesMap} that forwards read and write
 *   calls to the attributes of a ServletContext. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
class WebServletContextVariablesMap extends VariablesMap<String,Object> {
    
    
    private static final long serialVersionUID = 6338751359164152136L;
    
    
    private final ServletContext servletContext;
    
    

    WebServletContextVariablesMap(final ServletContext servletContext) {
        super(1, 1.0f);
        Validate.notNull(servletContext, "Servlet context cannot be null");
        this.servletContext = servletContext;
    }

    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public int size() {
        int size = 0;
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNames.nextElement();
            size++;
        }
        return size;
    }

    
    
    @Override
    public boolean isEmpty() {
        return !this.servletContext.getAttributeNames().hasMoreElements();
    }

    
    
    @Override
    public Object get(final Object key) {
        return this.servletContext.getAttribute((String)key);
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(final Object key) {
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
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
        this.servletContext.setAttribute(key, value);
        return value;
    }

    
    
    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        for (final Map.Entry<? extends String, ? extends Object> mEntry : m.entrySet()) {
            this.servletContext.setAttribute(mEntry.getKey(), mEntry.getValue());
        }
    }

    
    
    @Override
    public Object remove(final Object key) {
        final Object value = this.servletContext.getAttribute((String)key);
        this.servletContext.removeAttribute((String)key);
        return value;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        final List<String> attributeNamesList = new ArrayList<String>(5);
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
        // We first iterate, then remove, in order to protect the Enumeration.
        while (attributeNames.hasMoreElements()) {
            attributeNamesList.add(attributeNames.nextElement());
        }
        for (final String attributeName : attributeNamesList) {
            this.servletContext.removeAttribute(attributeName);
        }
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(final Object value) {
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = this.servletContext.getAttribute(attributeName);
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
    public Object clone() {
        return new WebServletContextVariablesMap(this.servletContext);
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> keySet() {
        final Set<String> keySet = new LinkedHashSet<String>(5);
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            keySet.add(attributeNames.nextElement());
        }
        return keySet;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> values() {
        final List<Object> values = new ArrayList<Object>(5);
        final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            values.add(this.servletContext.getAttribute(attributeName));
        }
        return values;
    }

    
    
    @Override
    public Set<java.util.Map.Entry<String,Object>> entrySet() {
        return getAttributeMap(this.servletContext).entrySet();
    }

    
    
    @Override
    public String toString() {
        return getAttributeMap(this.servletContext).toString();
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.servletContext.hashCode();
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
        final WebServletContextVariablesMap other = (WebServletContextVariablesMap) obj;
        if (this.servletContext == null) {
            if (other.servletContext != null) {
                return false;
            }
        } else if (!this.servletContext.equals(other.servletContext)) {
            return false;
        }
        return true;
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private static Map<String,Object> getAttributeMap(final ServletContext servletContext) {
        
        final Map<String,Object> attributeMap = new LinkedHashMap<String, Object>(6, 1.0f);
        final Enumeration<String> attributeNames = servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = servletContext.getAttribute(attributeName);
            attributeMap.put(attributeName, attributeValue);
        }
        
        return attributeMap;
        
    }
    
    
    
}
