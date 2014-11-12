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
package org.thymeleaf.aurora.engine;

import org.thymeleaf.aurora.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class ElementNames {



    public static ElementName forXmlName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null) {
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
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return new XmlPrefixedElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, elementNameLen - i));
            }

        }

        return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static ElementName forHtmlName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null) {
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
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                if (TextUtil.equals(false, "xml", 0, 3, elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1)))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return new HtmlPrefixedElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, elementNameLen - i));
            }

            if (c == '-') {
                if (i == 1) {
                    // '-' was the first char, no prefix there
                    return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

                }
                return new HtmlPrefixedElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, elementNameLen - i));
            }

        }

        return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    public static ElementName forXmlName(final String elementName) {

        if (elementName == null) {
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
                    return new ElementName(elementName);
                }

                return new XmlPrefixedElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return new ElementName(elementName);

    }



    public static ElementName forHtmlName(final String elementName) {

        if (elementName == null) {
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
                    return new ElementName(elementName);
                }

                if (TextUtil.equals(false, "xml", 0, 3, elementName, 0, 3)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new ElementName(elementName);
                }

                return new HtmlPrefixedElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i,elementName.length()));
            }

            if (c == '-') {
                if (i == 1) {
                    // '-' was the first char, no prefix there
                    return new ElementName(elementName);

                }
                return new HtmlPrefixedElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return new ElementName(elementName);

    }



    public static ElementName forHtmlName(final String dialectPrefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        if (dialectPrefix == null) {
            return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
        }
        return new HtmlPrefixedElementName(dialectPrefix, new String(elementNameBuffer, elementNameOffset, elementNameLen));
    }



    public static ElementName forXmlName(final String dialectPrefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        if (dialectPrefix == null) {
            return new ElementName(new String(elementNameBuffer, elementNameOffset, elementNameLen));
        }
        return new XmlPrefixedElementName(dialectPrefix, new String(elementNameBuffer, elementNameOffset, elementNameLen));
    }



    public static ElementName forHtmlName(final String dialectPrefix, final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (dialectPrefix == null) {
            return new ElementName(elementName);
        }
        return new HtmlPrefixedElementName(dialectPrefix, elementName);
    }



    public static ElementName forXmlName(final String dialectPrefix, final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (dialectPrefix == null) {
            return new ElementName(elementName);
        }
        return new XmlPrefixedElementName(dialectPrefix, elementName);
    }





    
    private ElementNames() {
        super();
    }
    

}
