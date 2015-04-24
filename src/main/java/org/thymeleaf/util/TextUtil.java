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
package org.thymeleaf.util;


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


    /**
     * <p>
     *   Check equality of two <tt>String</tt> objects. This is equivalent to {@link java.lang.String#equals(Object)}
     *   and {@link java.lang.String#equalsIgnoreCase(String)}.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(final boolean caseSensitive, final String text1, final String text2) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        return (caseSensitive? text1.equals(text2) : text1.equalsIgnoreCase(text2));

    }


    /**
     * <p>
     *   Check equality between a <tt>String</tt> and a <tt>char[]</tt> object.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(final boolean caseSensitive, final String text1, final char[] text2) {
        return equals(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }


    /**
     * <p>
     *   Check equality between two <tt>char[]</tt> objects.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(final boolean caseSensitive, final char[] text1, final char[] text2) {
        return equals(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }


    /**
     * <p>
     *   Check equality between two <tt>char[]</tt> objects, specifying (offset,len) pairs for limiting
     *   the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(
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

        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return true;
        }

        char c1, c2;

        int n = text1Len;
        int i = 0;

        while (n-- != 0) {

            c1 = text1[text1Offset + i];
            c2 = text2[text2Offset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Check equality between a <tt>String</tt> and a <tt>char[]</tt> object, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(
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

        int n = text1Len;
        int i = 0;

        while (n-- != 0) {

            c1 = text1.charAt(text1Offset + i);
            c2 = text2[text2Offset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Check equality between two <tt>String</tt> objects, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return whether both texts are equal or not.
     */
    public static boolean equals(
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

        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return true;
        }

        char c1, c2;

        int n = text1Len;
        int i = 0;

        while (n-- != 0) {

            c1 = text1.charAt(text1Offset + i);
            c2 = text2.charAt(text2Offset + i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }






    /**
     * <p>
     *   Checks whether a text starts with a specified prefix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param prefix the prefix to be searched.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(final boolean caseSensitive, final String text, final String prefix) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        return (caseSensitive? text.startsWith(prefix) : startsWith(caseSensitive, text, 0, text.length(), prefix, 0, prefix.length()));

    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param prefix the prefix to be searched.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(final boolean caseSensitive, final String text, final char[] prefix) {
        return startsWith(caseSensitive, text, 0, text.length(), prefix, 0, prefix.length);
    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param prefix the prefix to be searched.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(final boolean caseSensitive, final char[] text, final char[] prefix) {
        return startsWith(caseSensitive, text, 0, text.length, prefix, 0, prefix.length);
    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param prefix the prefix to be searched.
     * @param prefixOffset the offset of the prefix.
     * @param prefixLen the length of the prefix.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final char[] prefix, final int prefixOffset, final int prefixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        if (textLen < prefixLen) {
            return false;
        }

        char c1, c2;

        int n = prefixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text[textOffset + i];
            c2 = prefix[prefixOffset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param prefix the prefix to be searched.
     * @param prefixOffset the offset of the prefix.
     * @param prefixLen the length of the prefix.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final char[] prefix, final int prefixOffset, final int prefixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        if (textLen < prefixLen) {
            return false;
        }

        char c1, c2;

        int n = prefixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text.charAt(textOffset + i);
            c2 = prefix[prefixOffset + i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param prefix the prefix to be searched.
     * @param prefixOffset the offset of the prefix.
     * @param prefixLen the length of the prefix.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final String prefix, final int prefixOffset, final int prefixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        if (textLen < prefixLen) {
            return false;
        }

        char c1, c2;

        int n = prefixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text[textOffset + i];
            c2 = prefix.charAt(prefixOffset + i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text starts with a specified prefix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for prefixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param prefix the prefix to be searched.
     * @param prefixOffset the offset of the prefix.
     * @param prefixLen the length of the prefix.
     * @return whether the text starts with the prefix or not.
     */
    public static boolean startsWith(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final String prefix, final int prefixOffset, final int prefixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        if (textLen < prefixLen) {
            return false;
        }

        char c1, c2;

        int n = prefixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text.charAt(textOffset + i);
            c2 = prefix.charAt(prefixOffset + i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }






    /**
     * <p>
     *   Checks whether a text ends with a specified suffix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param suffix the suffix to be searched.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(final boolean caseSensitive, final String text, final String suffix) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        return (caseSensitive? text.endsWith(suffix) : endsWith(caseSensitive, text, 0, text.length(), suffix, 0, suffix.length()));

    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param suffix the suffix to be searched.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(final boolean caseSensitive, final String text, final char[] suffix) {
        return endsWith(caseSensitive, text, 0, text.length(), suffix, 0, suffix.length);
    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param suffix the suffix to be searched.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(final boolean caseSensitive, final char[] text, final char[] suffix) {
        return endsWith(caseSensitive, text, 0, text.length, suffix, 0, suffix.length);
    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param suffix the suffix to be searched.
     * @param suffixOffset the offset of the suffix.
     * @param suffixLen the length of the suffix.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final char[] suffix, final int suffixOffset, final int suffixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        if (textLen < suffixLen) {
            return false;
        }

        final int textReverseOffset = textOffset + textLen - 1;
        final int suffixReverseOffset = suffixOffset + suffixLen - 1;

        char c1, c2;

        int n = suffixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text[textReverseOffset - i];
            c2 = suffix[suffixReverseOffset - i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param suffix the suffix to be searched.
     * @param suffixOffset the offset of the suffix.
     * @param suffixLen the length of the suffix.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final char[] suffix, final int suffixOffset, final int suffixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        if (textLen < suffixLen) {
            return false;
        }

        final int textReverseOffset = textOffset + textLen - 1;
        final int suffixReverseOffset = suffixOffset + suffixLen - 1;

        char c1, c2;

        int n = suffixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text.charAt(textReverseOffset - i);
            c2 = suffix[suffixReverseOffset - i];

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param suffix the suffix to be searched.
     * @param suffixOffset the offset of the suffix.
     * @param suffixLen the length of the suffix.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final String suffix, final int suffixOffset, final int suffixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        if (textLen < suffixLen) {
            return false;
        }

        final int textReverseOffset = textOffset + textLen - 1;
        final int suffixReverseOffset = suffixOffset + suffixLen - 1;

        char c1, c2;

        int n = suffixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text[textReverseOffset - i];
            c2 = suffix.charAt(suffixReverseOffset - i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }


    /**
     * <p>
     *   Checks whether a text ends with a specified suffix, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for suffixes.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param suffix the suffix to be searched.
     * @param suffixOffset the offset of the suffix.
     * @param suffixLen the length of the suffix.
     * @return whether the text ends with the suffix or not.
     */
    public static boolean endsWith(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final String suffix, final int suffixOffset, final int suffixLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        if (textLen < suffixLen) {
            return false;
        }

        final int textReverseOffset = textOffset + textLen - 1;
        final int suffixReverseOffset = suffixOffset + suffixLen - 1;

        char c1, c2;

        int n = suffixLen;
        int i = 0;

        while (n-- != 0) {

            c1 = text.charAt(textReverseOffset - i);
            c2 = suffix.charAt(suffixReverseOffset - i);

            if (c1 != c2) {

                if (caseSensitive) {
                    return false;
                }

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);

                if (c1 != c2) {

                    // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                    // See String#regionMatches(boolean,int,String,int,int)
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }

                }

            }

            i++;

        }

        return true;

    }






    /**
     * <p>
     *   Checks whether a text contains a specific fragment.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param fragment the fragment to be searched.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(final boolean caseSensitive, final String text, final String fragment) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        return (caseSensitive? text.contains(fragment) : contains(caseSensitive, text, 0, text.length(), fragment, 0, fragment.length()));

    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param fragment the fragment to be searched.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(final boolean caseSensitive, final String text, final char[] fragment) {
        return contains(caseSensitive, text, 0, text.length(), fragment, 0, fragment.length);
    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param fragment the fragment to be searched.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(final boolean caseSensitive, final char[] text, final char[] fragment) {
        return contains(caseSensitive, text, 0, text.length, fragment, 0, fragment.length);
    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param fragment the fragment to be searched.
     * @param fragmentOffset the offset of the fragment.
     * @param fragmentLen the length of the fragment.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final char[] fragment, final int fragmentOffset, final int fragmentLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }

        char c1, c2;

        for (int i = 0,j = 0; i < textLen; i++) {

            c1 = text[textOffset + i];
            c2 = fragment[fragmentOffset + j];

            if (c1 == c2) {
                if (++j == fragmentLen) {
                    return true;
                }
                continue;
            }

            if (!caseSensitive) {

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 == c2) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

            }

            j = 0;

        }

        return false;

    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param fragment the fragment to be searched.
     * @param fragmentOffset the offset of the fragment.
     * @param fragmentLen the length of the fragment.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final char[] fragment, final int fragmentOffset, final int fragmentLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }

        char c1, c2;

        for (int i = 0,j = 0; i < textLen; i++) {

            c1 = text.charAt(textOffset + i);
            c2 = fragment[fragmentOffset + j];

            if (c1 == c2) {
                if (++j == fragmentLen) {
                    return true;
                }
                continue;
            }

            if (!caseSensitive) {

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 == c2) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

            }

            j = 0;

        }

        return false;

    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param fragment the fragment to be searched.
     * @param fragmentOffset the offset of the fragment.
     * @param fragmentLen the length of the fragment.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(
            final boolean caseSensitive,
            final char[] text, final int textOffset, final int textLen,
            final String fragment, final int fragmentOffset, final int fragmentLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }

        char c1, c2;

        for (int i = 0,j = 0; i < textLen; i++) {

            c1 = text[textOffset + i];
            c2 = fragment.charAt(fragmentOffset + j);

            if (c1 == c2) {
                if (++j == fragmentLen) {
                    return true;
                }
                continue;
            }

            if (!caseSensitive) {

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 == c2) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

            }

            j = 0;

        }

        return false;

    }


    /**
     * <p>
     *   Checks whether a text contains a specific fragment, specifying (offset,len) pairs
     *   for limiting the fragments to be checked.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text the text to be checked for fragments.
     * @param textOffset the offset of the text.
     * @param textLen the length of the text.
     * @param fragment the fragment to be searched.
     * @param fragmentOffset the offset of the fragment.
     * @param fragmentLen the length of the fragment.
     * @return whether the text contains the fragment or not.
     */
    public static boolean contains(
            final boolean caseSensitive,
            final String text, final int textOffset, final int textLen,
            final String fragment, final int fragmentOffset, final int fragmentLen) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }

        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }

        char c1, c2;

        for (int i = 0,j = 0; i < textLen; i++) {

            c1 = text.charAt(textOffset + i);
            c2 = fragment.charAt(fragmentOffset + j);

            if (c1 == c2) {
                if (++j == fragmentLen) {
                    return true;
                }
                continue;
            }

            if (!caseSensitive) {

                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 == c2) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

                // We check both upper and lower case because that is how String#equalsIgnoreCase() is defined.
                // See String#regionMatches(boolean,int,String,int,int)
                if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
                    if (++j == fragmentLen) {
                        return true;
                    }
                    continue;
                }

            }

            j = 0;

        }

        return false;

    }






    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(final boolean caseSensitive, final String text1, final String text2) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        return (caseSensitive? text1.compareTo(text2) : text1.compareToIgnoreCase(text2));

    }


    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(final boolean caseSensitive, final String text1, final char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }


    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text2 the second text to be compared.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(final boolean caseSensitive, final char[] text1, final char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }


    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(
            final boolean caseSensitive,
            final char[] text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text buffer being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return 0;
        }

        char c1, c2;

        int n = Math.min(text1Len, text2Len);
        int i = 0;

        while (n-- != 0) {

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

            i++;

        }

        return text1Len - text2Len;

    }


    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final char[] text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }

        char c1, c2;

        int n = Math.min(text1Len, text2Len);
        int i = 0;

        while (n-- != 0) {

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

            i++;

        }

        return text1Len - text2Len;

    }


    /**
     * <p>
     *   Compares two texts lexicographically.
     * </p>
     * <p>
     *   The comparison is based on the Unicode value of each character in the strings. The character sequence
     *   represented by the first text object is compared lexicographically to the character sequence represented
     *   by the second text.
     * </p>
     * <p>
     *   The result is a negative integer if the first text lexicographically precedes the second text. The
     *   result is a positive integer if the first text lexicographically follows the second text. The result
     *   is zero if the texts are equal.
     * </p>
     * <p>
     *   This method works in a way equivalent to that of the {@link java.lang.String#compareTo(String)}
     *   and {@link java.lang.String#compareToIgnoreCase(String)} methods.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param text1 the first text to be compared.
     * @param text1Offset the offset of the first text.
     * @param text1Len the length of the first text.
     * @param text2 the second text to be compared.
     * @param text2Offset the offset of the second text.
     * @param text2Len the length of the second text.
     * @return the value <tt>0</tt> if both texts are equal; a value less than <tt>0</tt> if the first text
     *         is lexicographically less than the second text; and a value greater than <tt>0</tt> if the
     *         first text is lexicographically greater than the second text.
     */
    public static int compareTo(
            final boolean caseSensitive,
            final String text1, final int text1Offset, final int text1Len,
            final String text2, final int text2Offset, final int text2Len) {

        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }

        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return 0;
        }

        char c1, c2;

        int n = Math.min(text1Len, text2Len);
        int i = 0;

        while (n-- != 0) {

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

            i++;

        }

        return text1Len - text2Len;

    }






    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive, final char[][] values,
            final char[] text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive, final char[][] values,
            final String text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive, final String[] values,
            final char[] text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive, final String[] values,
            final String text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param valuesOffset the offset to be applied to the texts array so that search only takes part in a fragment
     *                     of it.
     * @param valuesLen the length of the fragment of the texts array in which search will take part.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive,
            final char[][] values, final int valuesOffset, final int valuesLen,
            final char[] text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;

        int mid, cmp;
        char[] midVal;

        while (low <= high) {

            mid = (low + high) >>> 1;
            midVal = values[mid];

            cmp = TextUtil.compareTo(caseSensitive, midVal, 0, midVal.length, text, textOffset, textLen);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param valuesOffset the offset to be applied to the texts array so that search only takes part in a fragment
     *                     of it.
     * @param valuesLen the length of the fragment of the texts array in which search will take part.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive,
            final char[][] values, final int valuesOffset, final int valuesLen,
            final String text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;

        int mid, cmp;
        char[] midVal;

        while (low <= high) {

            mid = (low + high) >>> 1;
            midVal = values[mid];

            cmp = TextUtil.compareTo(caseSensitive, text, textOffset, textLen, midVal, 0, midVal.length);

            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param valuesOffset the offset to be applied to the texts array so that search only takes part in a fragment
     *                     of it.
     * @param valuesLen the length of the fragment of the texts array in which search will take part.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive,
            final String[] values, final int valuesOffset, final int valuesLen,
            final char[] text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;

        int mid, cmp;
        String midVal;

        while (low <= high) {

            mid = (low + high) >>> 1;
            midVal = values[mid];

            cmp = TextUtil.compareTo(caseSensitive, midVal, 0, midVal.length(), text, textOffset, textLen);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

    }


    /**
     * <p>
     *   Searches the specified array of texts (<tt>values</tt>) for the specified text &mdash;or a fragment, using an
     *   (offset,len) specification&mdash; using the binary search algorithm.
     * </p>
     * <p>
     *   Note the specified <tt>values</tt> parameter <strong>must be lexicographically ordered</strong>.
     * </p>
     *
     * @param caseSensitive whether the comparison must be done in a case-sensitive or case-insensitive way.
     * @param values the array of texts inside which the specified text will be searched.
     *               Note that it must be <strong>ordered</strong>.
     * @param valuesOffset the offset to be applied to the texts array so that search only takes part in a fragment
     *                     of it.
     * @param valuesLen the length of the fragment of the texts array in which search will take part.
     * @param text the text to search.
     * @param textOffset the offset of the text to search.
     * @param textLen the length of the text to search.
     * @return index of the search key, if it is contained in the values array; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point</i> is defined as the point at
     *         which the key would be inserted into the array. Note that this guarantees that the return value will
     *         be &gt;= 0 if and only if the key is found.
     */
    public static int binarySearch(
            final boolean caseSensitive,
            final String[] values, final int valuesOffset, final int valuesLen,
            final String text, final int textOffset, final int textLen) {

        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;

        int mid, cmp;
        String midVal;

        while (low <= high) {

            mid = (low + high) >>> 1;
            midVal = values[mid];

            cmp = TextUtil.compareTo(caseSensitive, text, textOffset, textLen, midVal, 0, midVal.length());

            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

    }









    public static int hashCode(final char[] text, final int textOffset, final int textLen) {
        // This basically mimics what the String.hashCode() method does, without the need to
        // convert the char[] into a new String object
        // If the text to compute was already a String, it would be better to directly call
        // its 'hashCode()' method, because Strings cache their hash codes.
        // ---------------------------------------
        // NOTE: Even if relying on the specific implementation of String.hashCode() might seem
        //       a potential issue for cross-platform compatibility, the fact is that the
        //       implementation of String.hashCode() is actually a part of the Java Specification
        //       since Java 1.2, and its internal workings are explained in the JavaDoc for the
        //       String.hashCode() method.
        // ---------------------------------------
        int h = 0;
        int off = textOffset;
        for (int i = 0; i < textLen; i++) {
            h = 31*h + text[off++];
        }
        return h;
    }


    public static int hashCode(final CharSequence text) {
        // This basically mimics what the String.hashCode() method does, without the need to
        // convert the CharSequence into a new String object (unless it is a String already,
        // in which case String.hashCode() is used directly.
        // If the text to compute was already a String, it would be better to directly call
        // its 'hashCode()' method, because Strings cache their hash codes.
        // ---------------------------------------
        // NOTE: Even if relying on the specific implementation of String.hashCode() might seem
        //       a potential issue for cross-platform compatibility, the fact is that the
        //       implementation of String.hashCode() is actually a part of the Java Specification
        //       since Java 1.2, and its internal workings are explained in the JavaDoc for the
        //       String.hashCode() method.
        // ---------------------------------------
        if (text instanceof String) {
            return text.hashCode();
        }
        int h = 0;
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            h = 31*h + text.charAt(i);
        }
        return h;
    }




    private TextUtil() {
        super();
    }

}