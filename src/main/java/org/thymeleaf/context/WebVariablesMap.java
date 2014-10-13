/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *   Specialization of {@link VariablesMap} that forwards {@link #get(Object)}
 *   and {@link #put(Object, Object)} calls to a contained 
 *   HttpServletRequest. 
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.9
 *
 */
class WebVariablesMap extends VariablesMap<String,Object> {


    private static final long serialVersionUID = 3862067921983550180L;



    /**
     * <p>
     *   Name of the variable that contains the request parameters.
     * </p>
     */
    public static final String PARAM_VARIABLE_NAME = "param";

    /**
     * <p>
     *   Name of the variable that contains the session attributes.
     * </p>
     */
    public static final String SESSION_VARIABLE_NAME = "session";

    /**
     * <p>
     *   Name of the variable that contains the application (servlet context)
     *   attributes.
     * </p>
     */
    public static final String APPLICATION_VARIABLE_NAME = "application";

    
    /*
     * ---------------------------------------------------------------------------
     * THIS MAP FORWARDS ALL OPERATIONS TO THE UNDERLYING REQUEST, EXCEPT
     * FOR THE request, session AND application VARIABLES, WHICH ARE MAINTAINED
     * DIRECTLY AT THE EXTENDED HASHMAP.
     * ---------------------------------------------------------------------------
     */


    private final HttpServletRequest request;
    private final ServletContext servletContext;


    private final WebRequestParamsVariablesMap requestParamsVariablesMap;
    private final WebSessionVariablesMap sessionVariablesMap;
    private final WebServletContextVariablesMap servletContextVariablesMap;

    private final Set<String> attributeNames = new HashSet<String>();




    WebVariablesMap(final HttpServletRequest request, final ServletContext servletContext,
                    final Map<? extends String, ?> m) {

        super((m == null? 4 : m.size() + 4), 1.0f);

        this.request = request;
        this.servletContext = servletContext;

        this.requestParamsVariablesMap = new WebRequestParamsVariablesMap(this.request);
        this.sessionVariablesMap = new WebSessionVariablesMap(this.request);
        this.servletContextVariablesMap = new WebServletContextVariablesMap(this.servletContext);

        super.put(APPLICATION_VARIABLE_NAME, this.servletContextVariablesMap);
        super.put(PARAM_VARIABLE_NAME, this.requestParamsVariablesMap);
        super.put(SESSION_VARIABLE_NAME, this.sessionVariablesMap);

        // cache the attribute names, as they're expensive to get from the request 1000s of times per request
        Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            this.attributeNames.add(names.nextElement());
        }

        if (m != null) {
            // This must be done at the end because it relies on the request having been already set.
            putAll(m);
        }

    }




    public WebRequestParamsVariablesMap getRequestParamsVariablesMap() {
        return this.requestParamsVariablesMap;
    }



    public WebSessionVariablesMap getSessionVariablesMap() {
        return this.sessionVariablesMap;
    }



    public WebServletContextVariablesMap getServletContextVariablesMap() {
        return this.servletContextVariablesMap;
    }




    @Override
    @SuppressWarnings("unchecked")
    public int size() {
        return attributeNames.size() + 3; // session, param, application
    }



    @Override
    public boolean isEmpty() {
        return false; // at least 3 elements (session, param, application)
    }



    @Override
    public Object get(final Object key) {
        if (isReservedVariableName((String)key)) {
            return super.get(key);
        }
        return this.request.getAttribute((String)key);
    }



    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(final Object key) {
        if (isReservedVariableName((String)key)) {
            return true;
        }
        return attributeNames.contains(key);
    }



    @Override
    public Object put(final String key, final Object value) {
        if (isReservedVariableName(key)) {
            throw new IllegalArgumentException(
                    "Putting a context variable with name \"" + key + "\" is forbidden, as it is " +
                    "a reserved variable name.");
        }
        this.request.setAttribute(key, value);
        this.attributeNames.add(key);
        return value;
    }



    @Override
    public void putAll(final Map<? extends String, ?> m) {
        for (final Map.Entry<? extends String, ?> mEntry : m.entrySet()) {
            put(mEntry.getKey(), mEntry.getValue());
        }
    }



    @Override
    public Object remove(final Object key) {
        if (isReservedVariableName((String)key)) {
            throw new IllegalArgumentException(
                    "Removing context variable \"" + key + "\" is forbidden, as it is " +
                    "a reserved variable name.");
        }
        final Object value = this.request.getAttribute((String)key);
        this.request.removeAttribute((String)key);
        this.attributeNames.remove(key);
        return value;
    }



    @Override
    public void clear() {
        throw new UnsupportedOperationException(
                "Web variable context map cannot be completely cleared.");
    }



    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(final Object value) {
        if (value instanceof VariablesMap<?,?>) {
            //noinspection ObjectEquality
            if (value == this.requestParamsVariablesMap ||
                value == this.sessionVariablesMap ||
                value == this.servletContextVariablesMap) {
                return true;
            }
        }
        for (String attributeName : attributeNames) {
            final Object attributeValue = this.request.getAttribute(attributeName);
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
    @SuppressWarnings("unchecked")
    public Set<String> keySet() {
        final Set<String> keySet = new HashSet<String>(attributeNames.size() + 3);
        keySet.addAll(this.attributeNames);
        keySet.addAll(super.keySet());
        return keySet;
    }



    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> values() {
        final List<Object> values = new ArrayList<Object>(this.attributeNames.size() + 3);
        for (String attributeName : attributeNames) {
            values.add(this.request.getAttribute(attributeName));
        }
        values.addAll(super.values());
        return values;
    }



    @Override
    public Set<java.util.Map.Entry<String,Object>> entrySet() {
        final Map<String, Object> attributeMap = new HashMap<String, Object>(this.attributeNames.size() + 3);
        for (String attributeName : attributeNames) {
            attributeMap.put(attributeName, request.getAttribute(attributeName));
        }
        for (final Map.Entry<String, Object> superEntry : super.entrySet()) {
            attributeMap.put(superEntry.getKey(), superEntry.getValue());
        }
        return attributeMap.entrySet();
    }



    @Override
    public String toString() {
        final Map<String,Object> attributeMap = getAttributeMap(this.request);
        for (final Map.Entry<String,Object> superEntry : super.entrySet()) {
            attributeMap.put(superEntry.getKey(), superEntry.getValue());
        }
        return attributeMap.toString();
    }







    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.request.hashCode();
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
        final WebVariablesMap other = (WebVariablesMap) obj;
        if (this.request == null) {
            if (other.request != null) {
                return false;
            }
        } else if (!this.request.equals(other.request)) {
            return false;
        }
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
    private static Map<String,Object> getAttributeMap(final HttpServletRequest request) {

        final Map<String,Object> attributeMap = new HashMap<String, Object>();
        final Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object attributeValue = request.getAttribute(attributeName);
            attributeMap.put(attributeName, attributeValue);
        }

        return attributeMap;

    }





    private static boolean isReservedVariableName(final String name) {
        return PARAM_VARIABLE_NAME.equals(name) ||
               SESSION_VARIABLE_NAME.equals(name) ||
               APPLICATION_VARIABLE_NAME.equals(name);
    }


    public WebVariablesMap clone() {
        return (WebVariablesMap) super.clone();
    }
}
