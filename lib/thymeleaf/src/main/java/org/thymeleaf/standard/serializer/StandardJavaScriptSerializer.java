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
package org.thymeleaf.standard.serializer;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.DateUtils;
import org.unbescape.json.JsonEscape;
import org.unbescape.json.JsonEscapeLevel;
import org.unbescape.json.JsonEscapeType;


/**
 * <p>
 *   Default implementation of the {@link IStandardJavaScriptSerializer}.
 * </p>
 * <p>
 *   This implementation will delegate serialization to the
 *   <a href="http://wiki.fasterxml.com/JacksonHome">Jackson JSON processor library</a> if it is found in the
 *   classpath. If not, it will default to a custom implementation that produces similar results (but is less
 *   flexible).
 * </p>
 * <p>
 *   If a Thymeleaf application uses JavaScript template processing in a significant amount of templates or
 *   situations, the use of Jackson (2.6+) is recommended.
 * </p>
 * <p>
 *   Note that, even if Jackson is present in the classpath, its usage can be prevented by means of the
 *   {@code useJacksonIfAvailable} constructor flag.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardJavaScriptSerializer implements IStandardJavaScriptSerializer {

    private static final Logger logger = LoggerFactory.getLogger(StandardJavaScriptSerializer.class);


    private final IStandardJavaScriptSerializer delegate;



    private String computeJacksonPackageNameIfPresent() {
        // We will try to know whether Jackson is present in a way that is as resilient as possible with
        // dependency package renaming, so we will return the package name.
        try {
            final Class<?> objectMapperClass = ObjectMapper.class;
            final String objectMapperPackageName = objectMapperClass.getPackage().getName();
            return objectMapperPackageName.substring(0, objectMapperPackageName.length() - ".databind".length());
        } catch (final Throwable ignored) {
            // Nothing bad - simply Jackson 2 is not in the classpath
            return null;
        }
    }


    private static String computeJackson3PackageNameIfPresent() {
        // Jackson 3 relocated its packages from 'com.fasterxml.jackson' to 'tools.jackson'.
        // We detect it via reflection to avoid any compile-time dependency on Jackson 3 classes.
        try {
            final Class<?> objectMapperClass = ClassLoaderUtils.findClass("tools.jackson.databind.ObjectMapper");
            if (objectMapperClass == null) {
                return null;
            }
            final String objectMapperPackageName = objectMapperClass.getPackage().getName();
            return objectMapperPackageName.substring(0, objectMapperPackageName.length() - ".databind".length());
        } catch (final Throwable ignored) {
            return null;
        }
    }



    public StandardJavaScriptSerializer(final boolean useJacksonIfAvailable) {

        super();

        IStandardJavaScriptSerializer newDelegate = null;

        if (useJacksonIfAvailable) {

            // Try Jackson 2 first (com.fasterxml.jackson.*)
            final String jackson2Prefix = computeJacksonPackageNameIfPresent();
            if (jackson2Prefix != null) {
                try {
                    newDelegate = new JacksonStandardJavaScriptSerializer(jackson2Prefix);
                } catch (final Exception e) {
                    handleErrorLoggingOnJacksonInitialization(e);
                } catch (final NoSuchMethodError e) {
                    handleErrorLoggingOnJacksonInitialization(e);
                }
            }

            // If Jackson 2 was not found or failed to initialize, try Jackson 3 (tools.jackson.*)
            if (newDelegate == null) {
                final String jackson3Prefix = computeJackson3PackageNameIfPresent();
                if (jackson3Prefix != null) {
                    try {
                        newDelegate = new Jackson3StandardJavaScriptSerializer(jackson3Prefix);
                    } catch (final Exception e) {
                        handleErrorLoggingOnJacksonInitialization(e);
                    }
                }
            }

        }

        if (newDelegate == null) {
            // Jackson could not be used, so we will use a default StandardJavaScriptSerializer (non-Jackson)
            newDelegate = new DefaultStandardJavaScriptSerializer();
        }

        this.delegate = newDelegate;
    }




    public void serializeValue(final Object object, final Writer writer) {
        this.delegate.serializeValue(object, writer);
    }







    private static final class JacksonStandardJavaScriptSerializer implements IStandardJavaScriptSerializer {

        private final ObjectMapper mapper;


        JacksonStandardJavaScriptSerializer(final String jacksonPrefix) {

            super();

            this.mapper = new ObjectMapper();
            this.mapper.findAndRegisterModules();
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            this.mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            this.mapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
            this.mapper.getFactory().setCharacterEscapes(new JacksonThymeleafCharacterEscapes());
            this.mapper.setDateFormat(new JacksonThymeleafISO8601DateFormat());

            /*
             * Now try to (conditionally) initialize support for Jackson serialization of JSR310 (java.time) objects,
             * by making use of the 'jackson-datatype-jsr310' optional dependency.
             */
            final Class<?> javaTimeModuleClass =
                    ClassLoaderUtils.findClass(jacksonPrefix + ".datatype.jsr310.JavaTimeModule");
            if (javaTimeModuleClass != null) {
                // JSR310 support for Jackson is present in classpath
                try {
                    this.mapper.registerModule((Module)javaTimeModuleClass.newInstance());
                    this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                } catch (final InstantiationException e) {
                    throw new ConfigurationException("Exception while trying to initialize JSR310 support for Jackson", e);
                } catch (final IllegalAccessException e) {
                    throw new ConfigurationException("Exception while trying to initialize JSR310 support for Jackson", e);
                }
            }

        }


        public void serializeValue(final Object object, final Writer writer) {
            try {
                this.mapper.writeValue(writer, object);
            } catch (final IOException e) {
                throw new TemplateProcessingException(
                        "An exception was raised while trying to serialize object to JavaScript using Jackson", e);
            }
        }

    }





    /*
     * Serializer that uses Jackson 3 (tools.jackson.*) when it is present on the classpath instead of Jackson 2.
     * All Jackson 3 classes are accessed via reflection to avoid any compile-time dependency on Jackson 3,
     * keeping the build dependency on Jackson 2 unchanged.
     *
     * Jackson 3 made ObjectMapper largely immutable: configuration must be applied via MapperBuilder before
     * calling build(). The constructor therefore tries JsonMapper.builder() first (the idiomatic Jackson 3
     * approach, which lets us call defaultDateFormat(...) on the builder), and falls back to direct
     * ObjectMapper instantiation for any Jackson 3 build that still supports mutable setters.
     *
     * The output is post-processed to escape '/' as '\/' and '&' as '\u0026', providing the same XSS safety
     * guarantees as the Jackson 2 serializer (which achieves this via JacksonThymeleafCharacterEscapes).
     * Using a StringWriter intermediate buffer also avoids the need to configure AUTO_CLOSE_TARGET on
     * the Jackson 3 ObjectMapper, since closing a StringWriter is a no-op.
     */
    private static final class Jackson3StandardJavaScriptSerializer implements IStandardJavaScriptSerializer {

        private final Object mapper;
        private final Method writeValueMethod;


        Jackson3StandardJavaScriptSerializer(final String jacksonPrefix) throws Exception {

            // Prefer builder-based construction: Jackson 3 requires configuration to be applied
            // on the MapperBuilder before build(), including the date format.
            Object newMapper = tryBuildViaJsonMapperBuilder(jacksonPrefix);

            if (newMapper == null) {
                // Fallback: direct ObjectMapper instantiation for Jackson 3 versions that still
                // allow mutable post-construction configuration.
                final Class<?> mapperClass = ClassLoaderUtils.findClass(jacksonPrefix + ".databind.ObjectMapper");
                if (mapperClass == null) {
                    throw new ConfigurationException(
                            "Cannot find ObjectMapper class for Jackson 3 at package prefix: " + jacksonPrefix);
                }
                newMapper = mapperClass.getDeclaredConstructor().newInstance();
                safeInvokeVoid(newMapper, "findAndRegisterModules");
                safeInvokeVoid(newMapper, "findAndAddModules");
                safeConfigureFeature(newMapper, jacksonPrefix + ".databind.SerializationFeature",
                        "WRITE_DATES_AS_TIMESTAMPS", false);
                // setDateFormat was removed in Jackson 3; try it anyway for forward-compatibility.
                try {
                    newMapper.getClass().getMethod("setDateFormat", DateFormat.class)
                            .invoke(newMapper, new JacksonThymeleafISO8601DateFormat());
                } catch (final Exception ignored) {}
            }

            safeConfigureEscapeNonAscii(newMapper.getClass(), jacksonPrefix, newMapper);

            this.mapper = newMapper;
            this.writeValueMethod = this.mapper.getClass().getMethod("writeValue", Writer.class, Object.class);
        }


        private static Object tryBuildViaJsonMapperBuilder(final String jacksonPrefix) {
            try {
                // tools.jackson.databind.json.JsonMapper is the concrete entry point in Jackson 3.
                // JsonMapper.builder() returns a MapperBuilder that accepts pre-build configuration.
                final Class<?> jsonMapperClass =
                        ClassLoaderUtils.findClass(jacksonPrefix + ".databind.json.JsonMapper");
                if (jsonMapperClass == null) {
                    return null;
                }
                final Object builder = jsonMapperClass.getMethod("builder").invoke(null);
                final Class<?> builderClass = builder.getClass();

                // Set ISO 8601 date format (with milliseconds + RFC 3339 colon timezone offset).
                // MapperBuilder.defaultDateFormat(DateFormat) must be called before build().
                // This is the critical call; if it fails the whole method returns null.
                builderClass.getMethod("defaultDateFormat", DateFormat.class)
                        .invoke(builder, new JacksonThymeleafISO8601DateFormat());

                // Disable WRITE_DATES_AS_TIMESTAMPS so java.util.Date is rendered as a string.
                // MapperBuilder.disable(SerializationFeature...) is a varargs method - best-effort.
                try {
                    final Class<?> serFeatureClass =
                            ClassLoaderUtils.findClass(jacksonPrefix + ".databind.SerializationFeature");
                    if (serFeatureClass != null) {
                        final Object writeDatesFeature =
                                getEnumConstant(serFeatureClass, "WRITE_DATES_AS_TIMESTAMPS");
                        if (writeDatesFeature != null) {
                            // Varargs reflection: the method takes SerializationFeature[], not SerializationFeature.
                            final Object featureArray =
                                    java.lang.reflect.Array.newInstance(serFeatureClass, 1);
                            java.lang.reflect.Array.set(featureArray, 0, writeDatesFeature);
                            builderClass.getMethod("disable", featureArray.getClass())
                                    .invoke(builder, featureArray);
                        }
                    }
                } catch (final Exception ignored) {}

                // Register modules (e.g. JavaTimeModule for java.time.* types) - best-effort.
                try {
                    builderClass.getMethod("findAndAddModules").invoke(builder);
                } catch (final Exception ignored) {}

                return builderClass.getMethod("build").invoke(builder);

            } catch (final Exception e) {
                return null;
            }
        }


        @Override
        public void serializeValue(final Object object, final Writer writer) {
            try {
                // Buffer to a StringWriter so that Jackson closing the writer is harmless,
                // and to allow post-processing for XSS-safe escaping of '/' and '&'.
                final StringWriter sw = new StringWriter(256);
                this.writeValueMethod.invoke(this.mapper, sw, object);
                final String json = sw.toString();
                // Escape '&' -> '\u0026' and '/' -> '\/' for the same XSS safety as the Jackson 2 path.
                writer.write(json.replace("&", "\\u0026").replace("/", "\\/"));
            } catch (final InvocationTargetException e) {
                final Throwable cause = e.getCause() != null ? e.getCause() : e;
                throw new TemplateProcessingException(
                        "An exception was raised while trying to serialize object to JavaScript using Jackson 3",
                        cause instanceof Exception ? (Exception) cause : e);
            } catch (final Exception e) {
                throw new TemplateProcessingException(
                        "An exception was raised while trying to serialize object to JavaScript using Jackson 3", e);
            }
        }


        private static void safeInvokeVoid(final Object instance, final String methodName) {
            try {
                instance.getClass().getMethod(methodName).invoke(instance);
            } catch (final Exception ignored) {
                // Best-effort configuration - not critical
            }
        }


        private static void safeConfigureFeature(
                final Object instance, final String featureClassName,
                final String featureConstant, final boolean enable) {
            try {
                final Class<?> featureClass = ClassLoaderUtils.findClass(featureClassName);
                if (featureClass == null) {
                    return;
                }
                final Object featureValue = getEnumConstant(featureClass, featureConstant);
                if (featureValue == null) {
                    return;
                }
                // Try configure(Feature, boolean) first (available in both Jackson 2 and some Jackson 3 builds).
                try {
                    instance.getClass().getMethod("configure", featureClass, boolean.class)
                            .invoke(instance, featureValue, enable);
                    return;
                } catch (final NoSuchMethodException ignored) {}
                // Fall back to enable(Feature) / disable(Feature).
                final String methodName = enable ? "enable" : "disable";
                instance.getClass().getMethod(methodName, featureClass).invoke(instance, featureValue);
            } catch (final Exception ignored) {
                // Best-effort configuration - not critical
            }
        }


        private static void safeConfigureEscapeNonAscii(
                final Class<?> mapperClass, final String jacksonPrefix, final Object mapper) {
            try {
                // JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature() -> StreamWriteFeature instance
                // Then: mapper.getFactory().configure(streamWriteFeature, true)
                final Class<?> jsonWriteFeatureClass =
                        ClassLoaderUtils.findClass(jacksonPrefix + ".core.json.JsonWriteFeature");
                if (jsonWriteFeatureClass == null) {
                    return;
                }
                final Object escapeNonAscii = getEnumConstant(jsonWriteFeatureClass, "ESCAPE_NON_ASCII");
                if (escapeNonAscii == null) {
                    return;
                }
                final Method mappedFeatureMethod = jsonWriteFeatureClass.getMethod("mappedFeature");
                final Object streamWriteFeature = mappedFeatureMethod.invoke(escapeNonAscii);
                if (streamWriteFeature == null) {
                    return;
                }
                final Object factory = mapperClass.getMethod("getFactory").invoke(mapper);
                factory.getClass()
                        .getMethod("configure", streamWriteFeature.getClass(), boolean.class)
                        .invoke(factory, streamWriteFeature, true);
            } catch (final Exception ignored) {
                // Best-effort configuration - not critical
            }
        }


        private static Object getEnumConstant(final Class<?> enumClass, final String name) {
            for (final Object constant : enumClass.getEnumConstants()) {
                if (((Enum<?>) constant).name().equals(name)) {
                    return constant;
                }
            }
            return null;
        }


    }




    /*
     * This DateFormat implementation replaces the standard Jackson date serialization mechanism for ISO6801 dates,
     * with the aim of making Jackson output dates in a way that is at the same time ECMAScript-valid and also
     * as compatible with non-Jackson JavaScript serialization infrastructure in Thymeleaf as possible. For this:
     *
     *   * The default Jackson behaviour of outputting all dates as GMT is disabled.
     *   * The default Jackson format adding timezone as '+0800' is modified, as ECMAScript requires '+08:00'
     *
     * On the latter point, see https://github.com/FasterXML/jackson-databind/issues/1020
     */
    private static final class JacksonThymeleafISO8601DateFormat extends DateFormat {

        private static final long serialVersionUID = 1354081220093875129L;

        /*
         * This SimpleDateFormat defines an almost-ISO8601 formatter.
         *
         * The correct ISO8601 format would be "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", but the "X" pattern (which outputs the
         * timezone as "+02:00" or "Z" instead of "+0200") was not added until Java SE 7. So the use of this
         * SimpleDateFormat object requires additional post-processing.
         *
         * SimpleDateFormat objects are NOT thread-safe, but it is here being used from another DateFormat
         * implementation, so we must suppose that it is the use of this DateFormat wrapper that will be
         * adequately synchronized by Jackson.
         */
        private SimpleDateFormat dateFormat;


        JacksonThymeleafISO8601DateFormat() {
            super();
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
            setCalendar(this.dateFormat.getCalendar());
            setNumberFormat(this.dateFormat.getNumberFormat());
        }


        @Override
        public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
            final StringBuffer formatted = this.dateFormat.format(date, toAppendTo, fieldPosition);
            formatted.insert(26, ':');
            return formatted;
        }


        @Override
        public Date parse(final String source, final ParsePosition pos) {
            throw new UnsupportedOperationException(
                    "JacksonThymeleafISO8601DateFormat should never be asked for a 'parse' operation");
        }


        @Override
        public Object clone() {
            JacksonThymeleafISO8601DateFormat other = (JacksonThymeleafISO8601DateFormat) super.clone();
            other.dateFormat = (SimpleDateFormat) dateFormat.clone();
            return other;
        }


    }



    /*
     * This CharacterEscapes implementation makes sure that the slash ('/') and ampersand ('&') characters
     * are also escaped, which is not standard Jackson behaviour.
     *
     * Escaping '/' covers against the possible premature closing of <script> tags inside inlined JavaScript
     * literals, thus preventing code injection in templates being processed by browsers as HTML.
     *
     * Escaping '&' covers against the injection of XHTML-escaped code which might prematurely close the
     * inlined JavaScript literals and even the container <script> tags in templates being processed
     * by browsers as XHTML.
     *
     * Note that, unfortunately Jackson's escape customization mechanism offers no way to only escape '/' when it
     * is preceded by '<', so that only '</' is escaped. Therefore, all '/' need to be escaped. Which is a
     * difference with the default Unbescape-based mechanism.
     */
    private static final class JacksonThymeleafCharacterEscapes extends CharacterEscapes {

        private static final int[] CHARACTER_ESCAPES;
        private static final SerializableString SLASH_ESCAPE;
        private static final SerializableString AMPERSAND_ESCAPE;

        static {

            CHARACTER_ESCAPES = CharacterEscapes.standardAsciiEscapesForJSON();
            CHARACTER_ESCAPES['/'] = CharacterEscapes.ESCAPE_CUSTOM;
            CHARACTER_ESCAPES['&'] = CharacterEscapes.ESCAPE_CUSTOM;

            SLASH_ESCAPE = new SerializedString("\\/");
            AMPERSAND_ESCAPE = new SerializedString("\\u0026");

        }

        JacksonThymeleafCharacterEscapes() {
            super();
        }

        @Override
        public int[] getEscapeCodesForAscii() {
            return CHARACTER_ESCAPES;
        }

        @Override
        public SerializableString getEscapeSequence(final int ch) {
            if (ch == '/') {
                return SLASH_ESCAPE;
            }
            if (ch == '&') {
                return AMPERSAND_ESCAPE;
            }
            return null;
        }

    }




    private static final class DefaultStandardJavaScriptSerializer implements IStandardJavaScriptSerializer {


        public void serializeValue(final Object object, final Writer writer) {
            try {
                writeValue(writer, object);
            } catch (final IOException e) {
                throw new TemplateProcessingException(
                        "An exception was raised while trying to serialize object to JavaScript using the default serializer", e);
            }
        }


        private static void writeValue(final Writer writer, final Object object) throws IOException {
            if (object == null) {
                writeNull(writer);
                return;
            }
            if (object instanceof CharSequence) {
                writeString(writer, object.toString());
                return;
            }
            if (object instanceof Character) {
                writeString(writer, object.toString());
                return;
            }
            if (object instanceof Number) {
                writeNumber(writer, (Number) object);
                return;
            }
            if (object instanceof Boolean) {
                writeBoolean(writer, (Boolean) object);
                return;
            }
            if (object instanceof Date) {
                writeDate(writer, (Date) object);
                return;
            }
            if (object instanceof Calendar) {
                writeDate(writer, ((Calendar) object).getTime());
                return;
            }
            if (object.getClass().isArray()) {
                writeArray(writer, object);
                return;
            }
            if (object instanceof Collection<?>) {
                writeCollection(writer, (Collection<?>) object);
                return;
            }
            if (object instanceof Map<?,?>) {
                writeMap(writer, (Map<?, ?>) object);
                return;
            }
            if (object instanceof Enum<?>) {
                writeEnum(writer, object);
                return;
            }
            // Java Records (Java 16+): must be checked before bean introspection because
            // Introspector does not recognise record accessor methods (which follow no 'getX()' convention).
            // Class.isRecord() is accessed via reflection to maintain Java 8 source compatibility.
            if (isRecord(object.getClass())) {
                writeRecord(writer, object);
                return;
            }
            writeObject(writer, object);
        }


        private static void writeNull(final Writer writer) throws IOException {
            writer.write("null");
        }


        private static void writeString(final Writer writer, final String str) throws IOException {
        /*
         * Note we will be using JSON escape instead of JavaScript escape. They are basically (99%) interchangeable
         * once we have established that our literals use double-quotes (") and not single-quotes, and this allows us
         * to avoid escaping single-quotes and therefore be more consistent with Jackson-based serialization, which
         * is obviously JSON-based.
         */
            writer.write('"');
            writer.write(JsonEscape.escapeJson(str, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET));
            writer.write('"');
        }


        private static void writeNumber(final Writer writer, final Number number) throws IOException {
            writer.write(number.toString());
        }


        private static void writeBoolean(final Writer writer, final Boolean bool) throws IOException {
            writer.write(bool.toString());
        }


        private static void writeDate(final Writer writer, final Date date) throws IOException {
            writer.write('"');
            writer.write(DateUtils.formatISO(date));
            writer.write('"');
        }


        private static void writeArray(final Writer writer, final Object arrayObj) throws IOException {
            writer.write('[');
            if (arrayObj instanceof Object[]) {
                final Object[] array = (Object[]) arrayObj;
                boolean first = true;
                for (final Object element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, element);
                }
            } else if (arrayObj instanceof boolean[]) {
                final boolean[] array = (boolean[]) arrayObj;
                boolean first = true;
                for (final boolean element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Boolean.valueOf(element));
                }
            } else if (arrayObj instanceof byte[]) {
                final byte[] array = (byte[]) arrayObj;
                boolean first = true;
                for (final byte element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Byte.valueOf(element));
                }
            } else if (arrayObj instanceof short[]) {
                final short[] array = (short[]) arrayObj;
                boolean first = true;
                for (final short element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Short.valueOf(element));
                }
            } else if (arrayObj instanceof int[]) {
                final int[] array = (int[]) arrayObj;
                boolean first = true;
                for (final int element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Integer.valueOf(element));
                }
            } else if (arrayObj instanceof long[]) {
                final long[] array = (long[]) arrayObj;
                boolean first = true;
                for (final long element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Long.valueOf(element));
                }
            } else if (arrayObj instanceof float[]) {
                final float[] array = (float[]) arrayObj;
                boolean first = true;
                for (final float element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Float.valueOf(element));
                }
            } else if (arrayObj instanceof double[]) {
                final double[] array = (double[]) arrayObj;
                boolean first = true;
                for (final double element: array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(',');
                    }
                    writeValue(writer, Double.valueOf(element));
                }
            } else {
                throw new IllegalArgumentException("Cannot write value \"" + arrayObj + "\" of class " + arrayObj.getClass().getName() + " as an array");
            }
            writer.write(']');
        }


        private static void writeCollection(final Writer writer, final Collection<?> collection) throws IOException {
            writer.write('[');
            boolean first = true;
            for (final Object element: collection) {
                if (first) {
                    first = false;
                } else {
                    writer.write(',');
                }
                writeValue(writer, element);
            }
            writer.write(']');
        }


        private static void writeMap(final Writer writer, final Map<?,?> map) throws IOException {
            writer.write('{');
            boolean first = true;
            for (final Map.Entry<?,?> entry: map.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    writer.write(',');
                }
                writeKeyValue(writer, entry.getKey(), entry.getValue());
            }
            writer.write('}');
        }


        private static void writeKeyValue(final Writer writer, final Object key, final Object value) throws IOException {
            writeValue(writer, key);
            writer.write(':');
            writeValue(writer, value);
        }


        private static void writeObject(final Writer writer, final Object object) throws IOException {
            try {
                final PropertyDescriptor[] descriptors =
                        Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
                final Map<String,Object> properties = new LinkedHashMap<String, Object>(descriptors.length + 1, 1.0f);
                for (final PropertyDescriptor descriptor : descriptors) {
                    final Method readMethod =  descriptor.getReadMethod();
                    if (readMethod != null) {
                        final String name = descriptor.getName();
                        if (!"class".equals(name.toLowerCase())) {
                            final Object value = readMethod.invoke(object);
                            properties.put(name, value);
                        }
                    }
                }
                writeMap(writer, properties);
            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e);
            } catch (final InvocationTargetException e) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e);
            } catch (final IntrospectionException e) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e);
            }
        }



        private static boolean isRecord(final Class<?> clazz) {
            // Class.isRecord() was added in Java 16; use reflection to keep Java 8 source compatibility.
            try {
                return Boolean.TRUE.equals(Class.class.getMethod("isRecord").invoke(clazz));
            } catch (final NoSuchMethodException ignored) {
                return false; // Running on Java < 16
            } catch (final Exception ignored) {
                return false;
            }
        }


        private static void writeRecord(final Writer writer, final Object object) throws IOException {
            // Class.getRecordComponents() and RecordComponent.getName()/getAccessor() added in Java 16.
            // All accessed via reflection to maintain Java 8 source compatibility.
            try {
                final Object[] components =
                        (Object[]) Class.class.getMethod("getRecordComponents").invoke(object.getClass());
                if (components == null || components.length == 0) {
                    writeMap(writer, new LinkedHashMap<String, Object>(0));
                    return;
                }
                final Map<String, Object> properties = new LinkedHashMap<String, Object>(components.length + 1, 1.0f);
                for (final Object component : components) {
                    final Class<?> componentClass = component.getClass();
                    final String name = (String) componentClass.getMethod("getName").invoke(component);
                    final Method accessor = (Method) componentClass.getMethod("getAccessor").invoke(component);
                    properties.put(name, accessor.invoke(object));
                }
                writeMap(writer, properties);
            } catch (final InvocationTargetException e) {
                throw new IllegalArgumentException(
                        "Could not read record components of class " + object.getClass().getName(), e.getCause());
            } catch (final Exception e) {
                throw new IllegalArgumentException(
                        "Could not read record components of class " + object.getClass().getName(), e);
            }
        }


        private static void writeEnum(final Writer writer, final Object object) throws IOException {

            final Enum<?> enumObject = (Enum<?>) object;
            writeString(writer, enumObject.toString());

        }


    }

    private void handleErrorLoggingOnJacksonInitialization(Throwable e)  {
        final String warningMessage =
                "[THYMELEAF] Could not initialize Jackson-based serializer even if the Jackson library was " +
                        "detected to be present at the classpath. Please make sure you are adding the " +
                        "jackson-databind module to your classpath, and that version is >= 2.5.0. " +
                        "THYMELEAF INITIALIZATION WILL CONTINUE, but Jackson will not be used for JavaScript " +
                        "serialization.";

        if (logger.isDebugEnabled()) {
            logger.warn(warningMessage, e);
        } else {
            // We will avoid an ugly exception trace appearing through the logs as WARN unless DEBUG is enabled
            logger.warn(
                    warningMessage + " Set the log to DEBUG to see a complete exception trace. Exception " +
                            "message is: " + e.getMessage());
        }
    }
}
