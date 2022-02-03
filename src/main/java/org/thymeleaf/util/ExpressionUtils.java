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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ExpressionUtils {

    private static final Set<String> BLOCKED_CLASS_NAME_PREFIXES =
            new HashSet<String>(Arrays.asList(
                    "java.lang.Runtime", "java.lang.Thread", "java.lang.Class", "java.lang.ClassLoader",
                    "java.lang.Runnable", "java.lang.reflect.Executable",
                    "java.util.concurrent.Future", "java.util.concurrent.FutureTask",
                    "java.util.concurrent.RunnableFuture", "java.util.concurrent.ListenableFuture",
                    "java.util.concurrent.Executor",
                    "java.sql.DriverManager"));



    public static boolean isTypeAllowed(final String typeName) {
        Validate.notNull(typeName, "Type name cannot be null");
        final int i0 = typeName.indexOf('.');
        if (i0 >= 0) {
            final String package0 = typeName.substring(0, i0);
            if ("java".equals(package0)) { // This is the only prefix that might be blocked
                for (final String prefix : BLOCKED_CLASS_NAME_PREFIXES) {
                    if (typeName.startsWith(prefix)) {
                        return false;
                    }
                }
            }
        }
        // This is safe assuming we have disabled the capability of calling "java.lang" classes without package
        return true;
    }



    public static List<String> getBlockedClasses() {
        final List<String> blocked = new ArrayList<String>();
        blocked.addAll(BLOCKED_CLASS_NAME_PREFIXES);
        return blocked;
    }


    private ExpressionUtils() {
        super();
    }

}
