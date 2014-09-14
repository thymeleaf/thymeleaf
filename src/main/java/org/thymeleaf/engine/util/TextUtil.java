/*
 * =============================================================================
 * 
 *   Copyright (c) 2012, The ATTOPARSER team (http://www.attoparser.org)
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
package org.thymeleaf.engine.util;


/**
 * <p>
 *   Utility class for <kbd>char[]</kbd> operations (mainly matching/comparing)
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TextUtil {




    public static final boolean equals(final boolean caseSensitive, final String text1, final String text2) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        return (caseSensitive? text1.equals(text2) : text1.equalsIgnoreCase(text2));

    }


    public static final boolean equals(final boolean caseSensitive, final String text1, final char[] text2) {
        return equals(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }

    public static final boolean equals(final boolean caseSensitive, final char[] text1, final char[] text2) {
        return equals(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }



    public static final boolean equals(
            final boolean caseSensitive,
            final char[] text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text buffer being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        if (text1Len != text2Len) {
            return false;
        }

        char c1, c2;

        for (int i = 0; i < text1Len; i++) { // Both lens are equal, so using one will suffice

            c1 = text1[text1Offset + i];
            c2 = text2[text2Offset + i];

            if (c1 == c2) {
                continue;
            }

            if (!caseSensitive) {

                final char u1 = Character.toUpperCase(c1);
                final char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }

            }

            return false;

        }

        return true;

    }



    public static final boolean equals(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        if (text1Len != text2Len) {
            return false;
        }

        char c1, c2;

        for (int i = 0; i < text1Len; i++) { // Both lens are equal, so using one will suffice

            c1 = text1.charAt(text1Offset + i);
            c2 = text2[text2Offset + i];

            if (c1 == c2) {
                continue;
            }

            if (!caseSensitive) {

                final char u1 = Character.toUpperCase(c1);
                final char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }

            }

            return false;

        }

        return true;

    }



    public static final boolean equals(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final String text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        if (text1Len != text2Len) {
            return false;
        }

        char c1, c2;

        for (int i = 0; i < text1Len; i++) { // Both lens are equal, so using one will suffice

            c1 = text1.charAt(text1Offset + i);
            c2 = text2.charAt(text2Offset + i);

            if (c1 == c2) {
                continue;
            }

            if (!caseSensitive) {

                final char u1 = Character.toUpperCase(c1);
                final char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }

            }

            return false;

        }

        return true;

    }










    public static final int compareTo(final boolean caseSensitive, final String text1, final String text2) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        return (caseSensitive? text1.compareTo(text2) : text1.compareToIgnoreCase(text2));

    }


    public static final int compareTo(final boolean caseSensitive, final String text1, final char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }

    public static final int compareTo(final boolean caseSensitive, final char[] text1, final char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }



    public static final int compareTo(
            final boolean caseSensitive,
            final char[] text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text buffer being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        final int min = Math.min(text1Len, text2Len);

        char c1, c2;
        for (int i = 0; i < min; i++) {

            c1 = text1[text1Offset + i];
            c2 = text2[text2Offset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return c1 - c2;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {
                    // We check both upper and lower case because that is how String#compareToIgnoreCase() is defined.
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }

                }

            }

        }

        return text1Len - text2Len;

    }



    public static final int compareTo(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        final int min = Math.min(text1Len, text2Len);

        char c1, c2;
        for (int i = 0; i < min; i++) {

            c1 = text1.charAt(text1Offset + i);
            c2 = text2[text2Offset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return c1 - c2;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {
                    // We check both upper and lower case because that is how String#compareToIgnoreCase() is defined.
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }

                }

            }

        }

        return text1Len - text2Len;

    }



    public static final int compareTo(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final String text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        final int min = Math.min(text1Len, text2Len);

        char c1, c2;
        for (int i = 0; i < min; i++) {

            c1 = text1.charAt(text1Offset + i);
            c2 = text2.charAt(text2Offset + i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return c1 - c2;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {
                    // We check both upper and lower case because that is how String#compareToIgnoreCase() is defined.
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }

                }

            }

        }

        return text1Len - text2Len;

    }



    private TextUtil() {
        super();
    }

}