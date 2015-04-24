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
public class ElementNames {



    public static ElementName forName(
            final TemplateMode templateMode, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Element Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()?
                forHTMLName(elementNameBuffer, elementNameOffset, elementNameLen) :
                forXMLName(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static ElementName forName(final TemplateMode templateMode, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Element Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()? forHTMLName(elementName) : forXMLName(elementName));

    }



    public static ElementName forName(
            final TemplateMode templateMode, final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Element Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()?
                forHTMLName(prefix, elementNameBuffer, elementNameOffset, elementNameLen) :
                forXMLName(prefix, elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static ElementName forName(final TemplateMode templateMode, final String prefix, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (!templateMode.isHTML() && !templateMode.isXML()) {
            throw new IllegalArgumentException("Cannot create Element Name for template modes other than HTML or XML");
        }

        return (templateMode.isHTML()? forHTMLName(prefix, elementName) : forXMLName(prefix, elementName));

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

        if (elementNameBuffer == null|| elementNameLen == 0) {
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

                if (TextUtil.equals(false, "xml:", 0, 4, elementNameBuffer, elementNameOffset, (i - elementNameOffset)) ||
                    TextUtil.equals(false, "xmlns:", 0, 6, elementNameBuffer, elementNameOffset, (i - elementNameOffset))) {
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



    public static XMLElementName forXMLName(final String elementName) {

        if (elementName == null|| elementName.length() == 0) {
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

        if (elementName == null|| elementName.length() == 0) {
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

                if (TextUtil.equals(false, "xml:", 0, 4, elementName, 0, i) ||
                    TextUtil.equals(false, "xmlns:", 0, 6, elementName, 0, i)) {
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



    public static HTMLElementName forHTMLName(final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null|| elementNameLen == 0) {
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



    public static XMLElementName forXMLName(final String prefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null|| elementNameLen == 0) {
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



    public static HTMLElementName forHTMLName(final String prefix, final String elementName) {
        if (elementName == null|| elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forHTMLName(elementName);
        }
        return new HTMLElementName(prefix, elementName);
    }



    public static XMLElementName forXMLName(final String prefix, final String elementName) {
        if (elementName == null|| elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return forXMLName(elementName);
        }
        return new XMLElementName(prefix, elementName);
    }





    
    private ElementNames() {
        super();
    }
    

}
