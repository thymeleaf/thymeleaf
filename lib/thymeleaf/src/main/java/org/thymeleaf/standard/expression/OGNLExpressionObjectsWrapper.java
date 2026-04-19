/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class OGNLExpressionObjectsWrapper extends HashMap<String, Object> {

    private static final Set<String> RESTRICTED_EXPRESSION_OBJECT_NAMES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    StandardExpressionObjectFactory.CONTEXT_EXPRESSION_OBJECT_NAME,
                    StandardExpressionObjectFactory.VARIABLES_EXPRESSION_OBJECT_NAME,
                    StandardExpressionObjectFactory.ROOT_EXPRESSION_OBJECT_NAME,
                    StandardExpressionObjectFactory.THIS_EXPRESSION_OBJECT_NAME,
                    StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME)));

    private final IExpressionObjects expressionObjects;


    static boolean isRestricted(final String name) {
        return RESTRICTED_EXPRESSION_OBJECT_NAMES.contains(name);
    }


    OGNLExpressionObjectsWrapper(final IExpressionObjects expressionObjects) {
        super(5);
        this.expressionObjects = expressionObjects;
    }


    @Override
    public int size() {
        return super.size() + this.expressionObjects.size();
    }

    @Override
    public boolean isEmpty() {
        return this.expressionObjects.size() == 0 && super.isEmpty();
    }

    @Override
    public Object get(final Object key) {
        if (this.expressionObjects.containsObject(key.toString())) {
            if (restrictExpressionObjects()) {
                if (RESTRICTED_EXPRESSION_OBJECT_NAMES.contains(key)) {
                    throw new TemplateProcessingException(
                            String.format("Access to variable '%s' is forbidden in this context.", key));
                }
            }
            return this.expressionObjects.getObject(key.toString());
        }
        return super.get(key);

    }

    @Override
    public boolean containsKey(final Object key) {
        return this.expressionObjects.containsObject(key.toString()) || super.containsKey(key);
    }


    private boolean restrictExpressionObjects() {
        return super.containsKey(OGNLContextPropertyAccessor.RESTRICT_EXPRESSION_OBJECTS);
    }


    @Override
    public Object put(final String key, final Object value) {
        if (this.expressionObjects.containsObject(key.toString())) {
            throw new IllegalArgumentException(
                    "Cannot put entry with key \"" + key + "\" into Expression Objects wrapper map: key matches the " +
                    "name of one of the expression objects");
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ?> m) {
        // This will call put, and therefore perform the key name check
        super.putAll(m);
    }

    @Override
    public Object remove(final Object key) {
        if (this.expressionObjects.containsObject(key.toString())) {
            throw new IllegalArgumentException(
                    "Cannot remove entry with key \"" + key + "\" from Expression Objects wrapper map: key matches the " +
                    "name of one of the expression objects");
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear Expression Objects wrapper map");
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException("Cannot perform by-value search on Expression Objects wrapper map");
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException("Cannot clone Expression Objects wrapper map");
    }

    @Override
    public Set<String> keySet() {
        if (super.isEmpty()) {
            return this.expressionObjects.getObjectNames();
        }
        final Set<String> keys = new LinkedHashSet<String>(this.expressionObjects.getObjectNames());
        keys.addAll(super.keySet());
        return keys;
    }

    @Override
    public Collection<Object> values() {
        return super.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException(
                "Cannot retrieve a complete entry set for Expression Objects wrapper map. Get a key set instead");
    }

    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException(
                "Cannot execute equals operation on Expression Objects wrapper map");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException(
                "Cannot execute hashCode operation on Expression Objects wrapper map");
    }

    @Override
    public String toString() {
        return "{EXPRESSION OBJECTS WRAPPER MAP FOR KEYS: " + keySet() + "}";
    }

}
