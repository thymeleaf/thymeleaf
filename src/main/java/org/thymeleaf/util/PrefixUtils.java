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
package org.thymeleaf.util;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class PrefixUtils {
    
    
    public static String getPrefix(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':'); 
        if (colonPos != -1) {
            return name.substring(0, colonPos);
        }
        return null;
    }
    
    
    public static String getUnprefixed(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':');
        if (colonPos != -1) {
            return name.substring(colonPos + 1);
        }
        return name;
    }
    
    
    public static boolean hasPrefix(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':');
        if (colonPos != -1) {
            return true;
        }
        return false;
    }

    
    
    private PrefixUtils() {
        super();
    }
    
}
