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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExpressionUtils {


    // NOTE thes lists are hard-wired into code, so any change to these sets should be synchronized with changes
    // in the corresponding code for quickly checking the fact that a type name might be in the blocking list.
    private static final Set<String> BLOCKED_PACKAGE_NAME_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "java.", "javax.", "jakarta.", "jdk.",
                    "org.ietf.jgss.", "org.omg.", "org.w3c.dom.", "org.xml.sax.",
                    "com.sun.", "sun."));
    private static final Set<String> ALLOWED_PACKAGE_NAME_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "java.time."));


    private static final Set<String> ALLOWED_JAVA_CLASS_NAMES;
    private static final Set<Class<?>> ALLOWED_JAVA_CLASSES =
            new HashSet<>(Arrays.asList(
                    // java.lang
                    Boolean.class, Byte.class, Character.class, Double.class, Enum.class, Float.class,
                    Integer.class, Long.class, Math.class, Number.class, Short.class, String.class,
                    // java.math
                    BigDecimal.class, BigInteger.class, RoundingMode.class,
                    // java.util
                    ArrayList.class, LinkedList.class, HashMap.class, LinkedHashMap.class, HashSet.class,
                    LinkedHashSet.class, Iterator.class, Enumeration.class, Locale.class, Properties.class,
                    Date.class, Calendar.class, Collection.class));

    private static final Set<String> ALLOWED_JAVA_SUPERS_NAMES;
    private static final Set<Class<?>> ALLOWED_JAVA_SUPERS =
            new HashSet<>(Arrays.asList(
                    // java.util
                    Collection.class, Iterable.class, List.class, Map.class, Map.Entry.class, Set.class,
                    Calendar.class));


    static {
        ALLOWED_JAVA_CLASS_NAMES = ALLOWED_JAVA_CLASSES.stream().map(c -> c.getName()).collect(Collectors.toSet());
        ALLOWED_JAVA_SUPERS_NAMES = ALLOWED_JAVA_SUPERS.stream().map(c -> c.getName()).collect(Collectors.toSet());
    }


    static boolean isJavaPackage(final String typeName) {
        return (typeName.charAt(0) == 'j' && typeName.charAt(4) == '.' && typeName.charAt(1) == 'a'
                && typeName.charAt(2) == 'v' && typeName.charAt(3) == 'a');
    }

    static boolean isBlockedPackage(final String typeName) {
        final char c0 = typeName.charAt(0);
        if (c0 != 'j' && c0 != 'o' && c0 != 'c' && c0 != 's'){ // All blocked packages start with: j, o, c, s
            return false;
        }
        if (c0 == 'c') { // Shortcut for the lot of allowed "com." packages out there. Only com.sun is blocked.
            return typeName.startsWith("com.sun.");
        }
        if (isJavaPackage(typeName)) {
            return !typeName.startsWith("java.time.");
        }
        return BLOCKED_PACKAGE_NAME_PREFIXES.stream().anyMatch(prefix -> typeName.startsWith(prefix));
    }



    public static boolean isTypeAllowed(final String typeName) {

        Validate.notNull(typeName, "Type name cannot be null");

        if (!isBlockedPackage(typeName)) {
            return true;
        }

        // We know the package is blocked, but certain classes and interfaces in blocked packages are allowed
        return ALLOWED_JAVA_CLASS_NAMES.contains(typeName) || ALLOWED_JAVA_SUPERS_NAMES.contains(typeName);

    }



    public static boolean isMemberAllowed(final Object target, final String memberName) {

        Validate.notNull(memberName, "Member name cannot be null");

        if (target == null) {
            return true;
        }

        // Calling Object#getClass() will always be allowed
        if ("getClass".equals(memberName)) {
            return true;
        }

        final boolean isClass = target instanceof Class<?>;
        final Class<?> type = target.getClass();
        final String typeName = type.getName();

        if (!isBlockedPackage(typeName)) {
            return true;
        }

        // We know the package is blocked, but whether we actually call that method call or not depends
        // on whether the class (inside the blocked package) is allowed, or whether the method is declared in
        // an allowed package or interface.

        if (ALLOWED_JAVA_CLASSES.contains(type)) {
            return true;
        }

        // Enums and annotations in blocked packages are OK
        if (type.isEnum() || type.isAnnotation()) {
            return true;
        }

        // If the target itself is a class, that means we are calling a static method on it. And therefore we
        // will need to determine whether the class itself is blocked.
        if (isClass) {
            final String targetTypeName = ((Class<?>) target).getName();
            if (!isBlockedPackage(targetTypeName)) {
                return true;
            }
            if (ALLOWED_JAVA_CLASSES.contains(target)) {
                return true;
            }
            // If target is a blocked class, we will only allow calling "getName"
            return "getName".equals(memberName);
        }

        // Otherwise, we will restrict calls to methods declared in one of the allowed interfaces or superclasses
        return ALLOWED_JAVA_SUPERS.stream()
                .filter(i -> i.isAssignableFrom(type))
                .anyMatch(i -> Arrays.stream(i.getDeclaredMethods()).anyMatch(m -> memberName.equals(m.getName())));

    }



    public static List<String> getBlockedClasses() {
        return BLOCKED_PACKAGE_NAME_PREFIXES.stream()
                .sorted().map(p -> String.format("%s*", p)).collect(Collectors.toList());
    }

    public static List<String> getAllowedClasses() {
        return Stream.concat(
                    Stream.concat(
                        ALLOWED_JAVA_CLASSES.stream().map(c -> c.getName()),
                        ALLOWED_JAVA_SUPERS.stream().map(c -> c.getName())),
                    Stream.of("java.time.*"))
                .sorted().collect(Collectors.toList());
    }


    private ExpressionUtils() {
        super();
    }

}
