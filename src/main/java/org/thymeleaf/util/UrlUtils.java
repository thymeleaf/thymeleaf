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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * <p>
 *     General utilities for URI/URLs.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.3
 * @deprecated in 2.1.4, and replaced by the use of Unbescape's <kbd>UriEscape</kbd> class.
 *
 */
@Deprecated
public final class UrlUtils {


    private static enum EncodeType {

        PATH {
            @Override
            public boolean isAllowed(final char c) {
                return isPchar(c) || '/' == c;
            }
        },

        PATH_SEGMENT {
            @Override
            public boolean isAllowed(final char c) {
                return isPchar(c);
            }
        };

        public abstract boolean isAllowed(final char c);

        /*
         * Specification of 'pchar' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isPchar(final char c) {
            return isUnreserved(c) || isSubDelim(c) || ':' == c || '@' == c;
        }

        /*
         * Specification of 'unreserved' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isUnreserved(final char c) {
            return isAlpha(c) || isDigit(c) || '-' == c || '.' == c || '_' == c || '~' == c;
        }

        /*
         * Specification of 'reserved' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isReserved(final char c) {
            return isGenDelim(c) || isSubDelim(c);
        }

        /*
         * Specification of 'sub-delims' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isSubDelim(final char c) {
            return '!' == c || '$' == c || '&' == c || '\'' == c || '(' == c || ')' == c || '*' == c || '+' == c ||
                    ',' == c || ';' == c || '=' == c;
        }

        /*
         * Specification of 'gen-delims' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isGenDelim(final char c) {
            return ':' == c || '/' == c || '?' == c || '#' == c || '[' == c || ']' == c || '@' == c;
        }

        /*
         * Character.isLetter() is not used here because it would include
         * non a-to-z letters.
         */
        private static boolean isAlpha(final char c) {
            return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
        }

        /*
         * Character.isDigit() is not used here because it would include
         * non 0-to-9 numbers like i.e. arabic or indian numbers.
         */
        private static boolean isDigit(final char c) {
            return c >= '0' && c <= '9';
        }

    }



    /**
     * <p>
     *   Encodes the given URL query parameter, using the 'UTF-8' encoding.
     * </p>
     *
     * @param queryParam the query parameter to be encoded
     * @return the encoding result
     */
    public static String encodeQueryParam(final String queryParam) {
        if (queryParam == null) {
            return null;
        }
        if (queryParam.length() == 0) {
            return queryParam;
        }
        try {
            return URLEncoder.encode(queryParam, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            // This should never happen as 'UTF-8' will always exist.
            throw new TemplateProcessingException("Exception while processing URL encoding", e);
        }
    }



    /**
     * <p>
     *   Encodes the given String as a section of a URL path. This section might include '/'
     *   level separators, which will be kept.
     * </p>
     *
     * @param path the path to be encoded
     * @return the encoding result
     */
    public static String encodePath(final String path) {
        return encodeString(path, EncodeType.PATH);
    }



    /**
     * <p>
     *   Encodes the given String as a segment of a URL path. This section cannot include '/'
     *   level separators, so any instances of this char will be encoded.
     * </p>
     *
     * @param pathSegment the path segment to be encoded
     * @return the encoding result
     */
    public static String encodePathSegment(final String pathSegment) {
        return encodeString(pathSegment, EncodeType.PATH_SEGMENT);
    }



    static String encodeString(final String target, final EncodeType type) {
        if (target == null) {
            return target;
        }
        final int targetLen = target.length();
        for (int i = 0; i < targetLen; i++) {
            final char c = target.charAt(i);
            if (!type.isAllowed(c)) {
                return doEncodeString(target, type);
            }
        }
        // We avoid creating new String objects if it is not needed
        return target;
    }



    private static String doEncodeString(final String target, final EncodeType type) {

        final int targetLen = target.length();
        final StringBuilder strBuilder = new StringBuilder(targetLen + 5);
        for (int i = 0; i < targetLen; i++) {
            final char c = target.charAt(i);
            if (type.isAllowed(c)) {
                strBuilder.append(c);
            } else {
                final byte[] charAsBytes;
                try {
                    charAsBytes = new String(new char[] {c}).getBytes("UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    // This should never happen as 'UTF-8' will always exist.
                    throw new TemplateProcessingException("Exception while processing URL encoding", e);
                }
                for (final byte b : charAsBytes) {
                    strBuilder.append('%');
                    final char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                    final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                    strBuilder.append(hex1);
                    strBuilder.append(hex2);
                }
            }
        }
        return strBuilder.toString();
    }




    private UrlUtils() {
        super();
    }
    

}
