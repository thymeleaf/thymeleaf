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

package org.thymeleaf.util;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Deque;
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
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExpressionUtils {


    // NOTE these lists are hard-wired into code, so any change to these sets should be synchronized with changes
    // in the corresponding code for quickly checking the fact that a type name might be in the blocking list.
    private static final Set<String> BLOCKED_ALL_PURPOSES_PACKAGE_NAME_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "java.", "javax.", "jakarta.", "jdk.",
                    "org.ietf.jgss.", "org.omg.", "org.w3c.dom.", "org.xml.sax.",
                    "com.sun.", "sun."));
    private static final Set<String> ALLOWED_ALL_PURPOSES_PACKAGE_NAME_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "java.time."));
    private static final Set<String> BLOCKED_TYPE_REFERENCE_PACKAGE_NAME_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "ch.qos.logback.", "com.squareup.javapoet.",
                    "com.zaxxer.hikari.", "com.fasterxml.jackson.", "tools.jackson.",
                    "groovy.", "io.netty.", "javassist.", "javax0.geci.", "kotlin.",
                    "net.bytebuddy.", "net.sf.cglib.",
                    "org.apache.tomcat.jdbc.", "org.apache.commons.dbcp2.",
                    "org.apache.commons.lang.reflect.", "org.apache.commons.lang3.reflect.",
                    "org.apache.bcel.", "org.apache.logging.", "org.aspectj.",
                    "org.codehaus.groovy.", "org.eclipse.jetty.", "org.glassfish.",
                    "org.javassist.", "org.jboss.", "org.jetbrains.kotlin.", "org.jruby.", "org.junit.",
                    "org.mockito.", "org.mortbay.jetty.", "org.objectweb.asm.", "org.objenesis.",
                    "org.python.", "org.slf4j.", "org.springframework.", "scala."));


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
                    LinkedHashSet.class, Iterator.class, Enumeration.class, Deque.class, Locale.class, Properties.class,
                    Date.class, Calendar.class, Optional.class, OptionalDouble.class, OptionalInt.class,
                    OptionalLong.class, UUID.class, Currency.class,
                    // java.util.concurrent.atomic
                    AtomicBoolean.class, AtomicInteger.class, AtomicIntegerArray.class, AtomicIntegerFieldUpdater.class,
                    AtomicLong.class, AtomicLongArray.class, AtomicLongFieldUpdater.class,
                    AtomicMarkableReference.class, AtomicReference.class, AtomicReferenceArray.class,
                    AtomicReferenceFieldUpdater.class, AtomicStampedReference.class, DoubleAccumulator.class,
                    DoubleAdder.class, LongAccumulator.class, LongAdder.class,
                    // java.sql
                    java.sql.Date.class, Time.class, Timestamp.class));

    private static final Set<String> ALLOWED_JAVA_SUPERS_NAMES;
    private static final Set<Class<?>> ALLOWED_JAVA_SUPERS =
            new HashSet<>(Arrays.asList(
                    // java.lang
                    CharSequence.class,
                    // java.util
                    Collection.class, Iterable.class, Iterator.class, List.class, Map.class, Map.Entry.class, Set.class,
                    Calendar.class, TimeZone.class, Stream.class));

    private static final Set<String> BLOCKED_MEMBER_CALL_JAVA_SUPERS_NAMES =
            new HashSet<>(Arrays.asList(
                    // java.lang
                    "java.lang.ClassLoader",
                    // org.thymeleaf
                    "org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator",
                    "org.thymeleaf.standard.expression.IStandardExpressionParser",
                    "org.thymeleaf.standard.expression.IStandardConversionService",
                    "org.thymeleaf.spring5.context.IThymeleafRequestContext",
                    "org.thymeleaf.spring5.expression.IThymeleafEvaluationContext",
                    "org.thymeleaf.spring6.context.IThymeleafRequestContext",
                    "org.thymeleaf.spring6.expression.IThymeleafEvaluationContext",
                    // org.springframework
                    "org.springframework.web.servlet.support.RequestContext",
                    "org.springframework.web.reactive.result.view.RequestContext",
                    "org.springframework.core.io.ResourceLoader"));
    private static final Set<Class<?>> BLOCKED_MEMBER_CALL_JAVA_SUPERS;

    private static final Set<String> ALLOWED_CLASS_METHODS =
            new HashSet<>(Arrays.asList(
                    "getName", "getSimpleName", "isAssignableFrom", "isInstance",
                    "isInterface", "isPrimitive", "isRecord", "isAnnotation", "isArray", "isEnum"));
    private static final Set<String> BLOCKED_CLASS_METHODS =
            Arrays.stream(Class.class.getDeclaredMethods()).map(Method::getName).collect(Collectors.toSet());


    static {
        ALLOWED_JAVA_CLASS_NAMES = ALLOWED_JAVA_CLASSES.stream().map(Class::getName).collect(Collectors.toSet());
        ALLOWED_JAVA_SUPERS_NAMES = ALLOWED_JAVA_SUPERS.stream().map(Class::getName).collect(Collectors.toSet());
        BLOCKED_MEMBER_CALL_JAVA_SUPERS = BLOCKED_MEMBER_CALL_JAVA_SUPERS_NAMES.stream().
                map(className -> {
                    try {
                        return Optional.of(Class.forName(className));
                    } catch (final ClassNotFoundException e) {
                        return Optional.<Class<?>>empty();
                    }
                }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }


    public static String normalize(final String expression, boolean normalizeCase) {
        if (expression == null) {
            return null;
        }
        final String exp = (normalizeCase ? expression.toLowerCase() : expression);
        StringBuilder strBuilder = null;
        final int expLen = exp.length();
        char c;
        for (int i = 0; i < expLen; i++) {
            c = exp.charAt(i);
            if (c != '\n' && (c < '\u0020' || (c >= '\u007F' && c <= '\u009F') || Character.isWhitespace(c))) {
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(expLen);
                    strBuilder.append(exp, 0, i);
                }
                if (Character.isWhitespace(c)) {
                    // For whitespaces (non-linefeed), we are simplifying to a regular whitespace char
                    strBuilder.append(' ');
                }
            } else if (strBuilder != null) {
                strBuilder.append(c);
            }
        }
        return strBuilder == null ? exp : strBuilder.toString();
    }


    static boolean isJavaPackage(final String typeName) {
        return (typeName.charAt(0) == 'j' && typeName.charAt(4) == '.' && typeName.charAt(1) == 'a'
                && typeName.charAt(2) == 'v' && typeName.charAt(3) == 'a');
    }

    static boolean isTypeBlockedForAllPurposes(final String typeName) {
        if (isJavaPackage(typeName) && ALLOWED_ALL_PURPOSES_PACKAGE_NAME_PREFIXES.stream().anyMatch(typeName::startsWith)) {
            return false;
        }
        return BLOCKED_ALL_PURPOSES_PACKAGE_NAME_PREFIXES.stream().anyMatch(typeName::startsWith);
    }

    static boolean isTypeBlockedForTypeReference(final String typeName) {
        if (isTypeBlockedForAllPurposes(typeName)) {
            return true;
        }
        return BLOCKED_TYPE_REFERENCE_PACKAGE_NAME_PREFIXES.stream().anyMatch(typeName::startsWith);
    }



    public static boolean isTypeForbidden(final String typeName) {

        Validate.notNull(typeName, "Type name cannot be null");

        final String normalizedTypeName = normalize(typeName, false);

        if (!isTypeBlockedForTypeReference(normalizedTypeName)) {
            return false;
        }

        // We know the package is blocked, but certain classes and interfaces in blocked packages are allowed
        return !ALLOWED_JAVA_CLASS_NAMES.contains(normalizedTypeName) && !ALLOWED_JAVA_SUPERS_NAMES.contains(normalizedTypeName);

    }



    static boolean isTypeBlockedForMemberCalls(final Class<?> type) {
        return BLOCKED_MEMBER_CALL_JAVA_SUPERS.stream().anyMatch(i -> i.isAssignableFrom(type));
    }


    static boolean isMemberForbiddenForInstanceOfType(final Class<?> type, final String memberName) {

        Validate.notNull(type, "Type cannot be null");

        final String typeName = type.getName();

        if (!isTypeBlockedForAllPurposes(typeName) && !isTypeBlockedForMemberCalls(type)) {
            return false;
        }

        // We know the package is blocked, so whether we can actually call methods or see fields of it depends
        // on other checks like whether the class (inside the blocked package) is allowed, or whether the method
        // is declared in an allowed package or interface. Also, enums, annotations and proxies are always allowed.

        // Enums and annotations in blocked packages are OK
        if (type.isEnum() || type.isAnnotation()) {
            return false;
        }

        // We will allow methods to be called on JDK-proxied classes. These proxied
        // classes are typically created under "jdk.proxyX" packages so calling methods
        // on them would be forbidden by default if we didn't allow this explicitly.
        if (Proxy.isProxyClass(type)) {
            return false;
        }

        if (ALLOWED_JAVA_CLASSES.contains(type)) {
            return false;
        }

        // Otherwise, we will restrict calls to methods declared in one of the allowed interfaces or superclasses
        return ALLOWED_JAVA_SUPERS.stream()
                .filter(i -> i.isAssignableFrom(type))
                .noneMatch(i -> Arrays.stream(i.getDeclaredMethods()).anyMatch(m -> memberName.equals(m.getName())));

    }



    public static boolean isMemberForbidden(final Object target, final String memberName) {

        Validate.notNull(memberName, "Member name cannot be null");

        if (target == null) {
            return false;
        }

        final String normalizedMemberName = normalize(memberName, false);

        // Calling Object#getClass() or Object#toString() will always be allowed
        if ("getClass".equals(normalizedMemberName) || "toString".equals(normalizedMemberName)) {
            return false;
        }

        // If the target itself is a class, that means we are calling a static method on it. And therefore we
        // will need to determine whether the class itself is blocked and whether the method being called is allowed.
        if (target instanceof Class<?>) {
            final String targetTypeName = ((Class<?>) target).getName();
            return !ALLOWED_CLASS_METHODS.contains(normalizedMemberName) &&
                    (BLOCKED_CLASS_METHODS.contains(normalizedMemberName) || isTypeForbidden(targetTypeName));
        }

        return isMemberForbiddenForInstanceOfType(target.getClass(), normalizedMemberName);

    }


    private ExpressionUtils() {
        super();
    }

}
