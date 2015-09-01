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
package org.thymeleaf.engine;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class AttributeNames {



    public static AttributeName forName(
            final TemplateMode templateMode, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static AttributeName forName(final TemplateMode templateMode, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeName);
        }

        if (templateMode.isText()) {
            return forTextName(attributeName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static AttributeName forName(
            final TemplateMode templateMode, final String prefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static AttributeName forName(final TemplateMode templateMode, final String prefix, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, attributeName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, attributeName);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, attributeName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static TextAttributeName forTextName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return TextAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static XMLAttributeName forXMLName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return XMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static HTMLAttributeName forHTMLName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        boolean inData = false;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset)) ||
                    TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset))) {
                    // 'xml' and 'xmlns' are not a valid dialect prefix in HTML mode
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return HTMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

            if (!inData && c == '-') {
                if (i == attributeNameOffset + 5 && TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1)))) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }
            }

            if (inData && c == '-') {
                if (i == attributeNameOffset + 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

                }
                return HTMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset + 5, (i - (attributeNameOffset + 6))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static TextAttributeName forTextName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = attributeName.length();
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return TextAttributeName.forName(null, attributeName);
                }

                return TextAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return TextAttributeName.forName(null, attributeName);

    }



    public static XMLAttributeName forXMLName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = attributeName.length();
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return XMLAttributeName.forName(null, attributeName);
                }

                return XMLAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return XMLAttributeName.forName(null, attributeName);

    }



    public static HTMLAttributeName forHTMLName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = attributeName.length();
        boolean inData = false;
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return HTMLAttributeName.forName(null, attributeName);
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeName, 0, i) ||
                    TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, attributeName, 0, i)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return HTMLAttributeName.forName(null, attributeName);
                }

                return HTMLAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i,attributeName.length()));
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return HTMLAttributeName.forName(null, attributeName);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return HTMLAttributeName.forName(null, attributeName);

                }
                return HTMLAttributeName.forName(
                        attributeName.substring(5, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return HTMLAttributeName.forName(null, attributeName);

    }



    public static TextAttributeName forTextName(final String prefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forTextName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        return TextAttributeName.forName(prefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static XMLAttributeName forXMLName(final String prefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        return XMLAttributeName.forName(prefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static HTMLAttributeName forHTMLName(final String prefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        return HTMLAttributeName.forName(prefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static TextAttributeName forTextName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forTextName(attributeName);
        }
        return TextAttributeName.forName(prefix, attributeName);
    }



    public static XMLAttributeName forXMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(attributeName);
        }
        return XMLAttributeName.forName(prefix, attributeName);
    }



    public static HTMLAttributeName forHTMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(attributeName);
        }
        return HTMLAttributeName.forName(prefix, attributeName);
    }





    private AttributeNames() {
        super();
    }
    
    

}
