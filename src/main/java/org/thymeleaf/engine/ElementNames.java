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
import org.thymeleaf.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class ElementNames {



    public static ElementName forName(
            final TemplateMode templateMode, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static ElementName forName(final TemplateMode templateMode, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementName);
        }

        if (templateMode.isText()) {
            return forTextName(elementName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static ElementName forName(
            final TemplateMode templateMode, final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, elementNameBuffer, elementNameOffset, elementNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static ElementName forName(final TemplateMode templateMode, final String prefix, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, elementName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, elementName);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, elementName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static TextElementName forTextName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return new TextElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return new TextElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return new TextElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static XMLElementName forXMLName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return new XMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return new XMLElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return new XMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static HTMLElementName forHTMLName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return new HTMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                if (TextUtil.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementNameBuffer, elementNameOffset, (i - elementNameOffset)) ||
                    TextUtil.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, elementNameBuffer, elementNameOffset, (i - elementNameOffset))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new HTMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return new HTMLElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

            if (c == '-') {
                if (i == elementNameOffset + 1) {
                    // '-' was the first char, no prefix there
                    return new HTMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

                }
                return new HTMLElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return new HTMLElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static TextElementName forTextName(final String elementName) {

        if (elementName == null) { // In text modes, elementName can actually be the empty string
            throw new IllegalArgumentException("Element name cannot be null");
        }


        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new TextElementName(elementName);
                }

                return new TextElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return new TextElementName(elementName);

    }



    public static XMLElementName forXMLName(final String elementName) {

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new XMLElementName(elementName);
                }

                return new XMLElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return new XMLElementName(elementName);

    }



    public static HTMLElementName forHTMLName(final String elementName) {

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new HTMLElementName(elementName);
                }

                if (TextUtil.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementName, 0, i) ||
                    TextUtil.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, elementName, 0, i)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new HTMLElementName(elementName);
                }

                return new HTMLElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i,elementName.length()));
            }

            if (c == '-') {
                if (i == 1) {
                    // '-' was the first char, no prefix there
                    return new HTMLElementName(elementName);

                }
                return new HTMLElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return new HTMLElementName(elementName);

    }



    public static TextElementName forTextName(final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null) { // Note TEXT element names CAN be empty (only element names, not attribute names)
            throw new IllegalArgumentException("Element name buffer cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forTextName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        return new TextElementName(prefix, new String(elementNameBuffer, elementNameOffset, elementNameLen));
    }



    public static XMLElementName forXMLName(final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        return new XMLElementName(prefix, new String(elementNameBuffer, elementNameOffset, elementNameLen));
    }



    public static HTMLElementName forHTMLName(final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        return new HTMLElementName(prefix, new String(elementNameBuffer, elementNameOffset, elementNameLen));
    }



    public static TextElementName forTextName(final String prefix, final String elementName) {
        if (elementName == null) { // Note TEXT element names CAN be empty (only element names, not attribute names)
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forTextName(elementName);
        }
        return new TextElementName(prefix, elementName);
    }



    public static XMLElementName forXMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(elementName);
        }
        return new XMLElementName(prefix, elementName);
    }



    public static HTMLElementName forHTMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(elementName);
        }
        return new HTMLElementName(prefix, elementName);
    }




    
    private ElementNames() {
        super();
    }
    

}
