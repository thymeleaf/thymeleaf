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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.unbescape.html.HtmlEscape;
import org.unbescape.java.JavaEscape;
import org.unbescape.javascript.JavaScriptEscape;


/**
 * <p>
 * Utility methods for String objects.
 * </p>
 * <p>
 * This class is used as a basis for the methods offered by
 * {@link org.thymeleaf.expression.Strings}, which in turn are the
 * methods offered by the {@code #strings} utility object in variable
 * expressions.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Le Roux Bernard
 * @author Soraya S&aacute;nchez Labandeira
 * @since 1.0
 */
public final class StringUtils {

    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();



    /**
     * <p>
     * Performs a null-safe {@code toString()} operation.
     * </p>
     *
     * @param target the object on which toString will be executed
     * @return the result of calling {@code target.toString()} if target is not null,
     *         {@code null} if target is null.
     * @since 2.0.12
     */
    public static String toString(final Object target) {
        if (target == null) {
            return null;
        }
        return target.toString();
    }



    public static String abbreviate(final Object target, final int maxSize) {

        Validate.isTrue(maxSize >= 3, "Maximum size must be greater or equal to 3");

        if (target == null) {
            return null;
        }

        final String str = target.toString();
        if (str.length() <= maxSize) {
            return str;
        }

        final StringBuilder strBuilder = new StringBuilder(maxSize + 2);
        strBuilder.append(str, 0, maxSize - 3);
        strBuilder.append("...");
        return strBuilder.toString();

    }



    /**
     * 
     * @param first first
     * @param second second
     * @return the result
     * @since 2.0.16
     */
    public static Boolean equals(final Object first, final Object second) {

        if (first == null && second == null) {
            return Boolean.TRUE;
        }
        if (first == null || second == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(first.toString().equals(second.toString()));

    }

    /**
     * 
     * @param first first
     * @param second second
     * @return the result
     * @since 2.0.16
     */
    public static Boolean equalsIgnoreCase(final Object first, final Object second) {
        if (first == null && second == null) {
            return Boolean.TRUE;
        }
        if (first == null || second == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(first.toString().equalsIgnoreCase(second.toString()));
    }

    public static Boolean contains(final Object target, final String fragment) {

        Validate.notNull(target, "Cannot apply contains on null");
        Validate.notNull(fragment, "Fragment cannot be null");

        return Boolean.valueOf(target.toString().contains(fragment));

    }



    public static Boolean containsIgnoreCase(final Object target, final String fragment, final Locale locale) {

        Validate.notNull(target, "Cannot apply containsIgnoreCase on null");
        Validate.notNull(fragment, "Fragment cannot be null");
        Validate.notNull(locale, "Locale cannot be null");

        return Boolean.valueOf(target.toString().toUpperCase(locale).contains(fragment.toUpperCase(locale)));

    }



    public static Boolean startsWith(final Object target, final String prefix) {

        Validate.notNull(target, "Cannot apply startsWith on null");
        Validate.notNull(prefix, "Prefix cannot be null");

        return Boolean.valueOf(target.toString().startsWith(prefix));

    }



    public static Boolean endsWith(final Object target, final String suffix) {

        Validate.notNull(target, "Cannot apply endsWith on null");
        Validate.notNull(suffix, "Suffix cannot be null");

        return Boolean.valueOf(target.toString().endsWith(suffix));

    }



    public static String substring(final Object target, final int beginIndex, final int endIndex) {

        if (target == null) {
            return null;
        }
        Validate.isTrue(beginIndex >= 0, "Begin index must be >= 0");

        // The String constructor is called on purpose to avoid problems from
        // creating substrings out of large amounts of long Strings (creating
        // a substring does not free the memory occupied by the original String).
        return new String(target.toString().substring(beginIndex, endIndex));

    }


    /**
     * <p>
     * copy a part of target start beginIndex to the end of target.
     * If non-String object, toString() will be called.
     * </p>
     *
     * @param target source of the copy.
     * @param beginIndex index where the copy start.
     * @return part of target, or {@code null} if target is null.
     * @since 1.1.2
     */
    public static String substring(final Object target, final int beginIndex) {

        if (target == null) {
            return null;
        }
        final String str = target.toString();
        final int len = str.length();
        Validate.isTrue(beginIndex >= 0 && beginIndex < len, "beginIndex must be >= 0 and < " + len);

        // Note this might not free the original string's char[] if using Java < Java7u6
        return str.substring(beginIndex);
    }



    public static String substringAfter(final Object target, final String substr) {

        Validate.notNull(substr, "Parameter substring cannot be null");

        if (target == null) {
            return null;
        }

        final String str = target.toString();
        final int index = str.indexOf(substr);
        if (index < 0) {
            return null;
        }

        // Note this might not free the original string's char[] if using Java < Java7u6
        return str.substring(index + substr.length());

    }



    public static String substringBefore(final Object target, final String substr) {

        Validate.notNull(substr, "Parameter substring cannot be null");

        if (target == null) {
            return null;
        }

        final String str = target.toString();
        final int index = str.indexOf(substr);
        if (index < 0) {
            return null;
        }

        // The String constructor is called on purpose to avoid problems from
        // creating substrings out of large amounts of long Strings (creating
        // a substring does not free the memory occupied by the original String).
        return new String(str.substring(0, index));

    }



    public static String prepend(final Object target, final String prefix) {
        Validate.notNull(prefix, "Prefix cannot be null");
        if (target == null) {
            return null;
        }
        return prefix + target;
    }



    public static String append(final Object target, final String suffix) {
        Validate.notNull(suffix, "Suffix cannot be null");
        if (target == null) {
            return null;
        }
        return target + suffix;
    }


    /**
     * 
     * @param target target
     * @param times times
     * @return the result
     * @since 2.1.0
     */
    public static String repeat(final Object target, final int times) {
        if (target == null) {
            return null;
        }
        final String str = target.toString();
        final StringBuilder strBuilder = new StringBuilder(str.length() * times + 10);
        for (int i = 0; i < times; i++) {
            strBuilder.append(str);
        }
        return strBuilder.toString();
    }


    /**
     * 
     * @param values values
     * @return the result
     * @since 2.0.16
     */
    public static String concat(final Object... values) {
        return concatReplaceNulls("", values);
    }

    /**
     * 
     * @param nullValue nullValue
     * @param values values
     * @return the result
     * @since 2.0.16
     */
    public static String concatReplaceNulls(final String nullValue, final Object... values) {

        if (values == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (final Object value : values) {
            if (value == null) {
                sb.append(nullValue);
            } else {
                sb.append(value.toString());
            }
        }

        return sb.toString();

    }

    public static Integer indexOf(final Object target, final String fragment) {

        Validate.notNull(target, "Cannot apply indexOf on null");
        Validate.notNull(fragment, "Fragment cannot be null");

        return Integer.valueOf(target.toString().indexOf(fragment));

    }



    /**
     * 
     * @param target target
     * @return the result
     * @since 2.1.0
     */
    public static boolean isEmpty(final String target) {
        return target == null || target.length() == 0;
    }



    public static boolean isEmptyOrWhitespace(final String target) {
        if (target == null) {
            return true;
        }
        final int targetLen = target.length();
        if (targetLen == 0) {
            return true;
        }
        final char c0 = target.charAt(0);
        if ((c0 >= 'a' && c0 <= 'z') || (c0 >= 'A' && c0 <= 'Z')) {
            // Fail fast, by quickly checking first char without executing Character.isWhitespace(...)
            return false;
        }
        for (int i = 0; i < targetLen; i++) {
            final char c = target.charAt(i);
            if (c != ' ' && !Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }



    public static String join(final Object[] target, final String separator) {

        Validate.notNull(separator, "Separator cannot be null");

        if (target == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        if (target.length > 0) {
            sb.append(target[0]);
            for (int i = 1; i < target.length; i++) {
                sb.append(separator);
                sb.append(target[i]);
            }
        }
        return sb.toString();
    }



    public static String join(final Iterable<?> target, final String separator) {

        Validate.notNull(separator, "Separator cannot be null");

        if (target == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        final Iterator<?> it = target.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(separator);
                sb.append(it.next());
            }
        }
        return sb.toString();

    }


    public static String join(final Iterable<?> target, final char separator) {

        if (target == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        final Iterator<?> it = target.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(separator);
                sb.append(it.next());
            }
        }
        return sb.toString();

    }



    public static String[] split(final Object target, final String separator) {

        Validate.notNull(separator, "Separator cannot be null");

        if (target == null) {
            return null;
        }

        final StringTokenizer strTok = new StringTokenizer(target.toString(), separator);
        final int size = strTok.countTokens();
        final String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = strTok.nextToken();
        }
        return array;
    }



    public static Integer length(final Object target) {

        Validate.notNull(target, "Cannot apply length on null");

        return Integer.valueOf(target.toString().length());

    }



    public static String replace(final Object target, final String before, final String after) {

        Validate.notNull(before, "Parameter \"before\" cannot be null");
        Validate.notNull(after, "Parameter \"after\" cannot be null");

        if (target == null) {
            return null;
        }

        final String targetStr = target.toString();
        final int targetStrLen = targetStr.length();
        final int beforeLen = before.length();

        if (targetStrLen == 0 || beforeLen == 0) {
            return targetStr;
        }

        int index = targetStr.indexOf(before);
        if (index < 0) {
            return targetStr;
        }

        final StringBuilder stringBuilder = new StringBuilder(targetStrLen + 10);

        int lastPos = 0;
        while (index >= 0) {

            stringBuilder.append(targetStr, lastPos, index);
            stringBuilder.append(after);
            lastPos = index + beforeLen;
            index = targetStr.indexOf(before, lastPos);

        }

        stringBuilder.append(targetStr, lastPos, targetStrLen);

        return stringBuilder.toString();

    }



    public static String toUpperCase(final Object target, final Locale locale) {

        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }
        return target.toString().toUpperCase(locale);

    }



    public static String toLowerCase(final Object target, final Locale locale) {

        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }
        return target.toString().toLowerCase(locale);

    }



    public static String trim(final Object target) {
        if (target == null) {
            return null;
        }
        return target.toString().trim();
    }


    /**
     * Removes all whitespaces and control chars at all positions, and
     * transforms to lower case.
     *
     * @param target the string to be packed
     * @return the packed string
     *
     * @since 3.0.12
     */
    public static String pack(final String target) {
        if (target == null) {
            return null;
        }
        final int targetLen = target.length();
        StringBuilder strBuilder = null;
        char c;
        for (int i = 0; i < targetLen; i++) {
            c = target.charAt(i);
            if (Character.isWhitespace(c) || c <= ' ') {
                if (strBuilder == null) {
                    strBuilder = new StringBuilder();
                    strBuilder.append(target, 0, i);
                }
            } else {
                if (strBuilder != null) {
                    strBuilder.append(c);
                }
            }
        }
        return (strBuilder == null) ? target.toLowerCase() : strBuilder.toString().toLowerCase();
    }


    /**
     * <p>
     * Convert the first letter of target to uppercase (title-case, in fact).
     * </p>
     *
     * @param target the String to be capitalized. If non-String object, toString()
     * will be called.
     * @return String the result of capitalizing the target.
     * @since 1.1.2
     */
    public static String capitalize(final Object target) {

        if (target == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder(target.toString());
        if (result.length() > 0) {
            result.setCharAt(0, Character.toTitleCase(result.charAt(0)));
        }
        return result.toString();

    }



    /**
     * <p>
     * Convert the first letter of target to lowercase.
     * </p>
     *
     * @param target the String to be uncapitalized. If non-String object, toString()
     * will be called.
     * @return String the result of uncapitalizing the target.
     * @since 1.1.2
     */
    public static String unCapitalize(final Object target) {

        if (target == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder(target.toString());

        if (result.length() > 0) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }

        return result.toString();
    }



    private static int findNextWord(final char[] buffer, final int idx, final char[] delimiterChars) {

        final int len = buffer.length;

        if (idx < 0 || idx >= len) {
            return -1;
        }

        boolean foundDelimiters = (idx == 0);
        int i = idx;
        while (i < len) {
            final char ch = buffer[i];
            final boolean isDelimiter =
                    (delimiterChars == null ?
                            (Character.isWhitespace(ch)) :
                            (Arrays.binarySearch(delimiterChars, ch) >= 0));
            if (isDelimiter) {
                foundDelimiters = true;
            } else {
                if (foundDelimiters) {
                    return i;
                }
            }
            i++;
        }

        return -1;

    }



    /**
     * <p>
     * Convert all the first letter of the words of target
     * to uppercase (title-case, in fact).
     * The default delimiter characters between the words
     * are the whitespace characters
     * (see Characters.IsWhiteSpace method in the Java doc).
     * </p>
     *
     * @param target the String to be capitalized. If non-String object, toString()
     * will be called.
     * @return String the result of capitalizing the target.
     * @since 1.1.2
     */
    public static String capitalizeWords(final Object target) {
        return capitalizeWords(target, null);
    }



    /**
     * <p>
     * Convert all the first letter of the words of target to uppercase
     * (title-case, in fact), using the specified delimiter chars for determining
     * word ends/starts.
     * </p>
     *
     * @param target the String to be capitalized. If non-String object, toString()
     * will be called.
     * @param delimiters delimiters of the words. If non-String object, toString()
     * will be called.
     * @return String the result of capitalizing the target.
     * @since 1.1.2
     */
    public static String capitalizeWords(final Object target, final Object delimiters) {

        if (target == null) {
            return null;
        }

        final char[] buffer = target.toString().toCharArray();
        final char[] delimiterChars =
                (delimiters == null ? null : delimiters.toString().toCharArray());
        if (delimiterChars != null) {
            // needed in order to use binarySearch
            Arrays.sort(delimiterChars);
        }

        int idx = 0;

        idx = findNextWord(buffer, idx, delimiterChars);
        while (idx != -1) {
            buffer[idx] = Character.toTitleCase(buffer[idx]);
            idx++;
            idx = findNextWord(buffer, idx, delimiterChars);
        }

        return new String(buffer);

    }



    /**
     * <p>
     * XML-escapes the specified text.
     * </p>
     *
     * @param target the text to be escaped
     * @return the escaped text.
     * @since 2.0.9
     */
    public static String escapeXml(final Object target) {
        if (target == null) {
            return null;
        }
        return HtmlEscape.escapeHtml4Xml(target.toString());
    }



    /**
     * <p>
     * Escapes the specified target text as required for JavaScript code.
     * </p>
     *
     * @param target the text to be escaped
     * @return the escaped text.
     * @since 2.0.11
     */
    public static String escapeJavaScript(final Object target) {
        if (target == null) {
            return null;
        }
        return JavaScriptEscape.escapeJavaScript(target.toString());
    }



    /**
     * <p>
     * Escapes the specified target text as required for Java code.
     * </p>
     *
     * @param target the text to be escaped
     * @return the escaped text.
     * @since 2.0.11
     */
    public static String escapeJava(final Object target) {
        if (target == null) {
            return null;
        }
        return JavaEscape.escapeJava(target.toString());
    }



    /**
     * <p>
     * Un-escapes the specified JavaScript-escaped target text back to normal form.
     * </p>
     *
     * @param target the text to be unescaped
     * @return the unescaped text.
     * @since 2.0.11
     */
    public static String unescapeJavaScript(final Object target) {
        if (target == null) {
            return null;
        }
        return JavaScriptEscape.unescapeJavaScript(target.toString());
    }



    /**
     * <p>
     * Un-escapes the specified Java-escaped target text back to normal form.
     * </p>
     *
     * @param target the text to be unescaped
     * @return the unescaped text.
     * @since 2.0.11
     */
    public static String unescapeJava(final Object target) {
        if (target == null) {
            return null;
        }
        return JavaEscape.unescapeJava(target.toString());
    }



    /**
     * <p>
     *   Sanitizes the specified HTML markup so that only a small set of inline text formatting elements
     *   is kept: {@code b}, {@code strong}, {@code i}, {@code em}, {@code u}, {@code s}, {@code del},
     *   {@code ins}, {@code mark}, {@code small}, {@code sub}, {@code sup}, {@code span} and {@code br}.
     * </p>
     * <p>
     *   These elements are output without attributes, except for a {@code class} attribute in
     *   {@code span} elements, which is kept only if it contains nothing but class names made of ASCII
     *   letters, digits, {@code '-'} and {@code '_'}. Up to 10 levels of nested elements are output.
     *   Other elements are removed but their text is kept, except for elements such as {@code script}
     *   or {@code style}, which are removed together with their contents. Comments, CDATA sections,
     *   DOCTYPE clauses, XML declarations and processing instructions are removed.
     * </p>
     * <p>
     *   The output is built from the elements and texts found in the input, never copied from it: text
     *   is unescaped and then HTML-escaped, and all elements are closed. Input that cannot be parsed as
     *   markup is output entirely as (HTML-escaped) text.
     * </p>
     * <p>
     *   This method is meant for keeping basic text formatting in texts such as messages.
     * </p>
     *
     * @param target the markup to be sanitized
     * @return the sanitized markup.
     * @since 3.1.6
     */
    public static String sanitizeHtmlFormatting(final Object target) {
        if (target == null) {
            return null;
        }
        return HtmlFormattingSanitizer.sanitize(target.toString());
    }



    public static String randomAlphanumeric(final int count) {
        final StringBuilder strBuilder = new StringBuilder(count);
        final int anLen = ALPHA_NUMERIC.length();
        synchronized (RANDOM) {
            for (int i = 0; i < count; i++) {
                strBuilder.append(ALPHA_NUMERIC.charAt(RANDOM.nextInt(anLen)));
            }
        }
        return strBuilder.toString();
    }



    private StringUtils() {
        super();
    }




    private static final class HtmlFormattingSanitizer extends AbstractMarkupHandler {

        private static final int MAX_DEPTH = 10;

        private static final Set<String> ALLOWED_ELEMENT_NAMES =
                new HashSet<String>(Arrays.asList(
                        "b", "strong", "i", "em", "u", "s", "del", "ins", "mark", "small", "sub", "sup", "span"));
        private static final String BR_ELEMENT_NAME = "br";
        private static final String SPAN_ELEMENT_NAME = "span";
        private static final String CLASS_ATTRIBUTE_NAME = "class";

        private static final Set<String> DROPPED_CONTENT_ELEMENT_NAMES =
                new HashSet<String>(Arrays.asList(
                        "script", "style", "textarea", "title", "template", "iframe", "object", "noscript",
                        "nostyle", "noembed", "noframes", "frame", "frameset", "svg", "math", "xmp", "listing",
                        "plaintext"));

        private static final MarkupParser PARSER;

        static {
            final ParseConfiguration configuration = ParseConfiguration.htmlConfiguration();
            configuration.setElementBalancing(ParseConfiguration.ElementBalancing.NO_BALANCING);
            configuration.setNoUnmatchedCloseElementsRequired(false);
            configuration.setUniqueAttributesInElementRequired(false);
            configuration.setXmlWellFormedAttributeValuesRequired(false);
            PARSER = new MarkupParser(configuration);
        }


        private final StringBuilder output;
        private final StringBuilder pendingText;
        private final List<String> openElementNames;
        private final List<Boolean> openElementsOutput;
        private final Map<String,int[]> openElementCounts;
        private int outputDepth;

        private String droppedContentElementName;
        private int droppedContentDepth;

        private String currentElementName;
        private String currentClassValue;


        static String sanitize(final String markup) {
            final HtmlFormattingSanitizer sanitizer = new HtmlFormattingSanitizer(markup.length());
            try {
                PARSER.parse(markup, sanitizer);
            } catch (final ParseException e) {
                return HtmlEscape.escapeHtml4Xml(removeInvalidChars(HtmlEscape.unescapeHtml(markup)));
            }
            sanitizer.flushText();
            sanitizer.closeElements(0);
            return sanitizer.output.toString();
        }


        private HtmlFormattingSanitizer(final int length) {
            super();
            this.output = new StringBuilder(length + 16);
            this.pendingText = new StringBuilder();
            this.openElementNames = new ArrayList<String>(MAX_DEPTH);
            this.openElementsOutput = new ArrayList<Boolean>(MAX_DEPTH);
            this.openElementCounts = new HashMap<String,int[]>(ALLOWED_ELEMENT_NAMES.size() * 2);
            this.outputDepth = 0;
            this.droppedContentElementName = null;
            this.droppedContentDepth = 0;
        }


        @Override
        public void handleText(
                final char[] buffer, final int offset, final int len, final int line, final int col) {
            if (this.droppedContentDepth == 0) {
                this.pendingText.append(buffer, offset, len);
            }
        }

        @Override
        public void handleOpenElementStart(
                final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col) {
            startElement(buffer, nameOffset, nameLen);
        }

        @Override
        public void handleStandaloneElementStart(
                final char[] buffer, final int nameOffset, final int nameLen,
                final boolean minimized, final int line, final int col) {
            startElement(buffer, nameOffset, nameLen);
        }

        @Override
        public void handleAttribute(
                final char[] buffer,
                final int nameOffset, final int nameLen, final int nameLine, final int nameCol,
                final int operatorOffset, final int operatorLen, final int operatorLine, final int operatorCol,
                final int valueContentOffset, final int valueContentLen,
                final int valueOuterOffset, final int valueOuterLen, final int valueLine, final int valueCol) {
            if (this.droppedContentDepth == 0
                    && SPAN_ELEMENT_NAME.equals(this.currentElementName)
                    && this.currentClassValue == null
                    && CLASS_ATTRIBUTE_NAME.equals(toLowerCaseAscii(buffer, nameOffset, nameLen))) {
                this.currentClassValue =
                        computeClassValue(HtmlEscape.unescapeHtml(new String(buffer, valueContentOffset, valueContentLen)));
            }
        }

        @Override
        public void handleOpenElementEnd(
                final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col) {
            endStartTag(false);
        }

        @Override
        public void handleStandaloneElementEnd(
                final char[] buffer, final int nameOffset, final int nameLen,
                final boolean minimized, final int line, final int col) {
            endStartTag(true);
        }

        @Override
        public void handleCloseElementStart(
                final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col) {
            closeElement(toLowerCaseAscii(buffer, nameOffset, nameLen));
        }

        @Override
        public void handleUnmatchedCloseElementStart(
                final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col) {
            closeElement(toLowerCaseAscii(buffer, nameOffset, nameLen));
        }


        private void startElement(final char[] buffer, final int nameOffset, final int nameLen) {
            flushText();
            this.currentElementName = toLowerCaseAscii(buffer, nameOffset, nameLen);
            this.currentClassValue = null;
        }

        private void endStartTag(final boolean standalone) {

            final String elementName = this.currentElementName;
            final String classValue = this.currentClassValue;
            this.currentElementName = null;
            this.currentClassValue = null;

            if (this.droppedContentDepth > 0) {
                if (!standalone && this.droppedContentElementName.equals(elementName)) {
                    this.droppedContentDepth++;
                }
                return;
            }
            if (elementName == null) {
                return;
            }
            if (DROPPED_CONTENT_ELEMENT_NAMES.contains(elementName)) {
                if (!standalone) {
                    this.droppedContentElementName = elementName;
                    this.droppedContentDepth = 1;
                }
                return;
            }
            if (BR_ELEMENT_NAME.equals(elementName)) {
                this.output.append("<br />");
                return;
            }
            if (!ALLOWED_ELEMENT_NAMES.contains(elementName)) {
                return;
            }

            final boolean outputElement = (this.outputDepth < MAX_DEPTH);
            if (outputElement) {
                this.output.append('<').append(elementName);
                if (classValue != null) {
                    this.output.append(" class=\"").append(classValue).append('"');
                }
                this.output.append('>');
            }
            if (standalone) {
                if (outputElement) {
                    this.output.append("</").append(elementName).append('>');
                }
                return;
            }
            this.openElementNames.add(elementName);
            this.openElementsOutput.add(Boolean.valueOf(outputElement));
            final int[] count = this.openElementCounts.get(elementName);
            if (count == null) {
                this.openElementCounts.put(elementName, new int[] { 1 });
            } else {
                count[0]++;
            }
            if (outputElement) {
                this.outputDepth++;
            }

        }

        private void closeElement(final String elementName) {

            if (this.droppedContentDepth > 0) {
                if (this.droppedContentElementName.equals(elementName)) {
                    this.droppedContentDepth--;
                    if (this.droppedContentDepth == 0) {
                        this.droppedContentElementName = null;
                    }
                }
                return;
            }

            flushText();

            if (elementName == null) {
                return;
            }
            // Counting open elements avoids searching the stack for elements that are not open, so that the cost
            // of searching it is limited to the elements that are closed as a result
            final int[] count = this.openElementCounts.get(elementName);
            if (count == null || count[0] == 0) {
                return;
            }
            closeElements(this.openElementNames.lastIndexOf(elementName));

        }

        private void closeElements(final int index) {
            for (int i = this.openElementNames.size() - 1; i >= index; i--) {
                final String elementName = this.openElementNames.remove(i);
                this.openElementCounts.get(elementName)[0]--;
                if (this.openElementsOutput.remove(i).booleanValue()) {
                    this.output.append("</").append(elementName).append('>');
                    this.outputDepth--;
                }
            }
        }

        private void flushText() {
            if (this.pendingText.length() > 0) {
                this.output.append(
                        HtmlEscape.escapeHtml4Xml(removeInvalidChars(HtmlEscape.unescapeHtml(this.pendingText.toString()))));
                this.pendingText.setLength(0);
            }
        }


        private static String toLowerCaseAscii(final char[] buffer, final int offset, final int len) {
            final char[] name = new char[len];
            for (int i = 0; i < len; i++) {
                final char c = buffer[offset + i];
                if (c >= 'A' && c <= 'Z') {
                    name[i] = (char) (c + ('a' - 'A'));
                } else if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                    name[i] = c;
                } else {
                    return null;
                }
            }
            return new String(name);
        }

        private static String computeClassValue(final String value) {
            final StringBuilder classValue = new StringBuilder(value.length());
            final StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
            while (tokenizer.hasMoreTokens()) {
                final String className = tokenizer.nextToken();
                for (int i = 0; i < className.length(); i++) {
                    final char c = className.charAt(i);
                    if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                            || c == '-' || c == '_')) {
                        return null;
                    }
                }
                if (classValue.length() > 0) {
                    classValue.append(' ');
                }
                classValue.append(className);
            }
            return (classValue.length() == 0 ? null : classValue.toString());
        }

        private static String removeInvalidChars(final String text) {
            StringBuilder result = null;
            final int textLen = text.length();
            int i = 0;
            while (i < textLen) {
                final int codePoint = text.codePointAt(i);
                final int codePointLen = Character.charCount(codePoint);
                final boolean valid =
                        (codePoint >= 0x20 || codePoint == '\t' || codePoint == '\n' || codePoint == '\r')
                        && !(codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE)
                        && !(codePoint >= 0xFDD0 && codePoint <= 0xFDEF)
                        && (codePoint & 0xFFFE) != 0xFFFE;
                if (!valid && result == null) {
                    result = new StringBuilder(textLen);
                    result.append(text, 0, i);
                }
                if (valid && result != null) {
                    result.append(text, i, i + codePointLen);
                }
                i += codePointLen;
            }
            return (result == null ? text : result.toString());
        }

    }


}
