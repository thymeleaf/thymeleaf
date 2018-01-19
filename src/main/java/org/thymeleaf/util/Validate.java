/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import java.util.Collection;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Validate {

    
    public static void notNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final String object, final String message) {
        if (StringUtils.isEmptyOrWhitespace(object)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Collection<?> object, final String message) {
        if (object == null || object.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Object[] object, final String message) {
        if (object == null || object.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void containsNoNulls(final Iterable<?> collection, final String message) {
        for (final Object object : collection) {
            notNull(object, message);
        }
    }
    
    public static void containsNoEmpties(final Iterable<String> collection, final String message) {
        for (final String object : collection) {
            notEmpty(object, message);
        }
    }
    
    public static void containsNoNulls(final Object[] array, final String message) {
        for (final Object object : array) {
            notNull(object, message);
        }
    }
    
    public static void isTrue(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
    
    
    private Validate() {
        super();
        
    }
}
