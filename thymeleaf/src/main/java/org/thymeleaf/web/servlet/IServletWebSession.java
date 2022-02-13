/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2021, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.web.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebSession;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 * 
 */
public interface IServletWebSession extends IWebSession {

    public Enumeration<String> getAttributeNames();


    @Override
    default boolean containsAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
        // Attribute maps in the servlet specification do not allow null values (setting null = remove)
        return getAttributeValue(name) != null;
    }

    @Override
    default int getAttributeCount() {
        int count = 0;
        final Enumeration<String> attributeNamesEnum = getAttributeNames();
        while (attributeNamesEnum.hasMoreElements()) {
            attributeNamesEnum.nextElement();
            count++;
        }
        return count;
    }

    @Override
    default Set<String> getAllAttributeNames() {
        final Set<String> attributeNames = new LinkedHashSet<String>(10);
        final Enumeration<String> attributeNamesEnum = getAttributeNames();
        while (attributeNamesEnum.hasMoreElements()) {
            attributeNames.add(attributeNamesEnum.nextElement());
        }
        return Collections.unmodifiableSet(attributeNames);
    }

    @Override
    default Map<String, Object> getAttributeMap() {
        final Map<String, Object> attributeMap = new LinkedHashMap<String, Object>(10);
        final Enumeration<String> attributeNamesEnum = getAttributeNames();
        String attributeName;
        while (attributeNamesEnum.hasMoreElements()) {
            attributeName = attributeNamesEnum.nextElement();
            attributeMap.put(attributeName, getAttributeValue(attributeName));
        }
        return Collections.unmodifiableMap(attributeMap);
    }

    @Override
    default void removeAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
        this.setAttributeValue(name, null);
    }

    public Object getNativeSessionObject();

}
