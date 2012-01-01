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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Le Roux Bernard
 * 
 * @since 1.0
 *
 */
public final class StringUtils {
    


    
    public static String abbreviate(final Object target, final int maxSize) {
        
        Validate.notNull(target, "Cannot apply abbreviation on null");
        Validate.isTrue(maxSize >= 3, "Maximum size must be greater or equal to 3");

        final String str = target.toString();
        if (str.length() <= maxSize) {
            return str;
        }
        
        return str.substring(0, maxSize - 3) + "...";
        
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
        
        Validate.notNull(target, "Cannot apply substring on null");
        Validate.isTrue(beginIndex >= 0, "Begin index must be >= 0");
        
        // The String constructor is called on purpose to avoid problems from
        // creating substrings out of large amounts of long Strings (creating
        // a substring does not free the memory occupied by the original String).
        return new String(target.toString().substring(beginIndex, endIndex));
        
    }
    
    
    /**
     * <p>
     *  copy a part of target start beginIndex to the end of target. 
     *  If non-String object, toString() will be called.      
     * </p>
     * @param target      source of the copy.
     * @param beginIndex  index where the copy start.
     * 
     * @return part of target.
     *
     * @since 1.1.2
     * 
     */    
    public static String substring(final Object target, final int beginIndex) {
        
        Validate.notNull(target, "Cannot apply substring on null");
        final String str = target.toString();
        final int len = str.length();
        Validate.isTrue(beginIndex >= 0 && beginIndex < len, "beginIndex must be >= 0 and < "+len);
        
        // The String constructor is called on purpose to avoid problems from
        // creating substrings out of large amounts of long Strings (creating
        // a substring does not free the memory occupied by the original String).
        return new String(str.substring(beginIndex));
    }


    
    
    public static String substringAfter(final Object target, final String substr) {
        
        Validate.notNull(target, "Cannot apply substringAfter on null");
        Validate.notNull(substr, "Parameter substring cannot be null");
        
        final String str = target.toString();
        final int index = str.indexOf(substr);
        if (index < 0) {
            return null;
        }

        // The String constructor is called on purpose to avoid problems from
        // creating substrings out of large amounts of long Strings (creating
        // a substring does not free the memory occupied by the original String).
        return new String(str.substring(index + substr.length()));
        
    }
    

    
    
    public static String substringBefore(final Object target, final String substr) {
        
        Validate.notNull(target, "Cannot apply substringBefore on null");
        Validate.notNull(substr, "Parameter substring cannot be null");
        
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
        Validate.notNull(target, "Cannot apply prepend on null");
        Validate.notNull(prefix, "Prefix cannot be null");
        return prefix + target;
    }
    

    
    
    public static String append(final Object target, final String suffix) {
        Validate.notNull(target, "Cannot apply append on null");
        Validate.notNull(suffix, "Suffix cannot be null");
        return target + suffix;
    }

    
    
    
    public static Integer indexOf(final Object target, final String fragment) {
        
        Validate.notNull(target, "Cannot apply indexOf on null");
        Validate.notNull(fragment, "Fragment cannot be null");
        
        return Integer.valueOf(target.toString().indexOf(fragment));
        
    }
    
    
    public static Boolean isEmpty(final Object target) {
        return Boolean.valueOf((target == null || target.toString().trim().equals("")));
    }

    
    
    public static String join(final Object[] target, final String separator) {
        
        Validate.notNull(target, "Cannot apply join on null");
        Validate.notNull(separator, "Separator cannot be null");
        
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
        
        Validate.notNull(target, "Cannot apply join on null");
        Validate.notNull(separator, "Separator cannot be null");
        
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
        
        Validate.notNull(target, "Cannot apply split on null");
        Validate.notNull(separator, "Separator cannot be null");
        
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
        
        Validate.notNull(target, "Cannot apply replace on null");
        Validate.notNull(before, "Parameter \"before\" cannot be null");
        Validate.notNull(after, "Parameter \"after\" cannot be null");
        
        return target.toString().replace(before,after);
        
    }

    
    
    
    public static String toUpperCase(final Object target, final Locale locale) {
        
        Validate.notNull(target, "Cannot apply toUpperCase on null");
        Validate.notNull(locale, "Locale cannot be null");

        return target.toString().toUpperCase(locale);
        
    }
    

    
    
    public static String toLowerCase(final Object target, final Locale locale) {
        
        Validate.notNull(target, "Cannot apply toLowerCase on null");
        Validate.notNull(locale, "Locale cannot be null");

        return target.toString().toLowerCase(locale);
        
    }
    

    
    
    public static String trim(final Object target) {
        Validate.notNull(target, "Cannot apply trim on null");
        return target.toString().trim();
    }


    /**
     * <p>
     *   Convert the first letter of target to uppercase (title-case, in fact).
     * </p>
     * 
     * @param target the String to be capitalized. If non-String object, toString() 
     *               will be called.
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public static String capitalize(final Object target) {
        
        Validate.notNull(target, "Cannot apply capitalize on null");
        StringBuilder result = new StringBuilder(target.toString());    
        if (result.length() > 0) {
            result.setCharAt(0, Character.toTitleCase(result.charAt(0)));
        }
        return result.toString();
        
    }


    
    /**
     * <p>
     *   Convert the first letter of target to lowercase.
     * </p>
     * 
     * @param target the String to be uncapitalized. If non-String object, toString() 
     *               will be called. 
     * 
     * @return String the result of uncapitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public static String unCapitalize(final Object target) {
        
        Validate.notNull(target, "Cannot apply unCapitalize on null");
        StringBuilder result = new StringBuilder(target.toString());    
        
        if (result.length() > 0) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }
        
        return result.toString();
    }
    

    
    
    private static int findNextWord(final char[] buffer, int idx, final char[] delimiterChars) {
        
        final int len = buffer.length;
        
        if (idx < 0 || idx >= len) {
            return -1;
        }
        
        boolean foundDelimiters = (idx == 0);
        int i = idx;
        while (i < len) {
            char ch = buffer[i];
            final boolean isDelimiter = 
                (delimiterChars == null?
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
     *   Convert all the first letter of the words of target 
     *    to uppercase (title-case, in fact).
     *   The default delimiter characters between the words 
     *   are the whitespace characters 
     *   (see Characters.IsWhiteSpace method in the Java doc).
     * </p>
     * 
     * @param target the String to be capitalized. If non-String object, toString() 
     *               will be called. 
     * 
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public static String capitalizeWords(final Object target) {
        return capitalizeWords(target, null);
    }
     
   

    /**
     * <p>
     *   Convert all the first letter of the words of target to uppercase 
     *   (title-case, in fact), using the specified delimiter chars for determining
     *   word ends/starts.
     * </p>
     * 
     * @param target the String to be capitalized. If non-String object, toString() 
     *               will be called. 
     * @param delimiters delimiters of the words. If non-String object, toString()
     *                   will be called. 
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public static String capitalizeWords(final Object target, final Object delimiters) {
    
        Validate.notNull(target, "Cannot apply capitalizeWords on null");
        
        char[] buffer = target.toString().toCharArray();
        char[] delimiterChars =
            (delimiters == null? null : delimiters.toString().toCharArray());
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
    

    
    
    private StringUtils() {
        super();
    }
    

    
}
