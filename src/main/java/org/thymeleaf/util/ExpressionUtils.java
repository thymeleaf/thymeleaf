/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ExpressionUtils {

    private static final Set<String> BLACKLISTED_CLASS_NAME_PREFIXES =
            new HashSet<String>(Arrays.asList(
                    "java", "javax", "jakarta", "org.ietf.jgss", "org.omg", "org.w3c.dom", "org.xml.sax"));


    // The whole "java.time.*" package will also be whitelisted
    private static final Set<String> WHITELISTED_JAVA_CLASS_NAMES =
            new HashSet<String>(Arrays.asList(
                    "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Double",
                    "java.lang.Enum", "java.lang.Float", "java.lang.Integer", "java.lang.Long", "java.lang.Math",
                    "java.lang.Number", "java.lang.Short", "java.lang.String",
                    "java.math.BigDecimal", "java.math.BigInteger", "java.math.RoundingMode",
                    "java.util.List", "java.util.Map", "java.util.Map.Entry", "java.util.Set",
                    "java.util.ArrayList", "java.util.LinkedList", "java.util.HashMap", "java.util.LinkedHashMap",
                    "java.util.HashSet", "java.util.LinkedHashSet", "java.util.Iterator", "java.util.Enumeration",
                    "java.util.Locale", "java.util.Properties", "java.util.Date", "java.util.Calendar",
                    "java.util.Collection",
                    "javax.servlet.http.HttpServletRequest",
                    "javax.servlet.http.HttpServletRequestWrapper",
                    "javax.servlet.ServletRequest",
                    "javax.servlet.ServletRequestWrapper",
                    "javax.servlet.HttpSession",
                    "javax.servlet.http.HttpServletResponse",
                    "javax.servlet.ServletResponse"));



    public static boolean isTypeAllowed(final String typeName) {
        Validate.notNull(typeName, "Type name cannot be null");
        final int i0 = typeName.indexOf('.');
        if (i0 >= 0) {
            final String package0 = typeName.substring(0, i0);
            if ("java".equals(package0) || "javax".equals(package0)) { // These are the only prefixes that allows whitelisting
                return WHITELISTED_JAVA_CLASS_NAMES.contains(typeName);
            } else if ("jakarta".equals(package0)) {
                return false;
            } else if ("org".equals(package0)) {
                if (typeName.startsWith("org.ietf.jgss")
                        || typeName.startsWith("org.omg")
                        || typeName.startsWith("org.w3c.dom")
                        || typeName.startsWith("org.xml.sax")) {
                    return false;
                }
            }
            return true;
        }
        // This is safe assuming we have disabled the capability of calling "java.lang" classes without package
        return true;
    }



    public static List<String> getBlacklist() {
        final List<String> blacklist = new ArrayList<String>();
        for (final String prefix : BLACKLISTED_CLASS_NAME_PREFIXES) {
            blacklist.add(String.format("%s.*", prefix));
        }
        return blacklist;
    }

    public static List<String> getWhitelist() {
        final List<String> whitelist = new ArrayList<String>();
        whitelist.addAll(WHITELISTED_JAVA_CLASS_NAMES);
        whitelist.add("java.time.*");
        Collections.sort(whitelist);
        return whitelist;
    }


    private ExpressionUtils() {
        super();
    }

}
