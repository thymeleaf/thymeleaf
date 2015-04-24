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

import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.util.TextUtil;

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
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Attribute Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()?
                        forHTMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen) :
                        forXMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static AttributeName forName(final TemplateMode templateMode, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Attribute Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()? forHTMLName(attributeName) : forXMLName(attributeName));

    }



    public static AttributeName forName(
            final TemplateMode templateMode, final String prefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Attribute Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()?
                forHTMLName(prefix, attributeNameBuffer, attributeNameOffset, attributeNameLen) :
                forXMLName(prefix, attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static AttributeName forName(final TemplateMode templateMode, final String prefix, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Attribute Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()? forHTMLName(prefix, attributeName) : forXMLName(prefix, attributeName));

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
                    return new XMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return new XMLAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return new XMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

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
                    return new HTMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                if (TextUtil.equals(false, "xml:", 0, 4, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset)) ||
                    TextUtil.equals(false, "xmlns:", 0, 6, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset))) {
                    // 'xml' and 'xmlns' are not a valid dialect prefix in HTML mode
                    return new HTMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return new HTMLAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

            if (!inData && c == '-') {
                if (i == attributeNameOffset + 5 && TextUtil.equals(false, "data", 0, 4, attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1)))) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new HTMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }
            }

            if (inData && c == '-') {
                if (i == attributeNameOffset + 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new HTMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

                }
                return new HTMLAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset + 5, (i - (attributeNameOffset + 6))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return new HTMLAttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

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
                    return new XMLAttributeName(attributeName);
                }

                return new XMLAttributeName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return new XMLAttributeName(attributeName);

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
                    return new HTMLAttributeName(attributeName);
                }

                if (TextUtil.equals(false, "xml:", 0, 4, attributeName, 0, i) ||
                    TextUtil.equals(false, "xmlns:", 0, 6, attributeName, 0, i)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new HTMLAttributeName(attributeName);
                }

                return new HTMLAttributeName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i,attributeName.length()));
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, attributeName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new HTMLAttributeName(attributeName);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new HTMLAttributeName(attributeName);

                }
                return new HTMLAttributeName(
                        attributeName.substring(5, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return new HTMLAttributeName(attributeName);

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
        return new HTMLAttributeName(prefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
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
        return new XMLAttributeName(prefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static HTMLAttributeName forHTMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(attributeName);
        }
        return new HTMLAttributeName(prefix, attributeName);
    }



    public static XMLAttributeName forXMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(attributeName);
        }
        return new XMLAttributeName(prefix, attributeName);
    }




    

    private AttributeNames() {
        super();
    }
    
    

}
