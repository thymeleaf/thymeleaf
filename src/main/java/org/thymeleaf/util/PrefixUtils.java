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
package org.thymeleaf.util;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 * @deprecated The PrefixUtils class has been deprecated in 2.1.0. From then on, the
 *             {@link org.thymeleaf.dom.Element} and {@link org.thymeleaf.dom.Attribute}
 *             classes are responsible for managing and combining dialect prefixes as required.
 *
 */
@Deprecated
public final class PrefixUtils {


    @Deprecated
    public static String getPrefix(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':'); 
        if (colonPos != -1) {
            return name.substring(0, colonPos);
        }
        return null;
    }


    @Deprecated
    public static String getUnprefixed(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':');
        if (colonPos != -1) {
            return name.substring(colonPos + 1);
        }
        return name;
    }


    @Deprecated
    public static boolean hasPrefix(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final int colonPos = name.indexOf(':');
        return colonPos != -1;
    }

    
    
    private PrefixUtils() {
        super();
    }
    
}
