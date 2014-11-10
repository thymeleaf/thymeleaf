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
public class ElementName {

    private final String dialectPrefix;
    private final String elementName;
    private final String completeNSElementName;
    private final String completeHtml5StandatdElementName;





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
                    return new ElementName(
                            null, new String(elementNameBuffer, elementNameOffset, elementNameLen), false);
                }

                return new ElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, elementNameLen - i),
                        false);
            }

        }

        return new ElementName(
                null, new String(elementNameBuffer, elementNameOffset, elementNameLen), false);

    }



    public static ElementName forHtmlName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Atribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        boolean inData = false;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new ElementName(
                            null, new String(elementNameBuffer, elementNameOffset, elementNameLen).toLowerCase(), true);
                }

                if (TextUtil.equals(false, "xml", 0, 3, elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1)))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new ElementName(
                            null, new String(elementNameBuffer, elementNameOffset, elementNameLen).toLowerCase(), true);
                }

                return new ElementName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))).toLowerCase(),
                        new String(elementNameBuffer, i, elementNameLen - i).toLowerCase(),
                        true);
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, elementNameBuffer, elementNameOffset, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new ElementName(
                            null, new String(elementNameBuffer, elementNameOffset, elementNameLen).toLowerCase(), true);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new ElementName(
                            null, new String(elementNameBuffer, elementNameOffset, elementNameLen).toLowerCase(), true);

                }
                return new ElementName(
                        new String(elementNameBuffer, elementNameOffset + 5, (i - (elementNameOffset + 6))).toLowerCase(),
                        new String(elementNameBuffer, i, elementNameLen - i).toLowerCase(),
                        true);
            }

        }

        return new ElementName(
                null, new String(elementNameBuffer, elementNameOffset, elementNameLen).toLowerCase(), true);

    }



    public static ElementName forXmlName(final String elementName) {

        if (elementName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
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
                    return new ElementName(null, elementName, false);
                }

                return new ElementName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()),
                        false);
            }

        }

        return new ElementName(null, elementName, false);

    }



    public static ElementName forHtmlName(final String elementName) {

        if (elementName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = elementName.length();
        boolean inData = false;
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new ElementName(null, elementName.toLowerCase(), true);
                }

                if (TextUtil.equals(false, "xml", 0, 3, elementName, 0, 3)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new ElementName(null, elementName.toLowerCase(), true);
                }

                return new ElementName(
                        elementName.substring(0, i - 1).toLowerCase(),
                        elementName.substring(i,elementName.length()).toLowerCase(),
                        true);
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, elementName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new ElementName(null, elementName.toLowerCase(), true);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new ElementName(null, elementName.toLowerCase(), true);

                }
                return new ElementName(
                        elementName.substring(5, i - 1).toLowerCase(),
                        elementName.substring(i, elementName.length()).toLowerCase(),
                        true);
            }

        }

        return new ElementName(null, elementName.toLowerCase(), true);

    }



    public static ElementName forHtmlName(final String dialectPrefix, final String elementName) {
        return new ElementName(dialectPrefix, elementName, true);
    }



    public static ElementName forXmlName(final String dialectPrefix, final String elementName) {
        return new ElementName(dialectPrefix, elementName, false);
    }






    public ElementName(final String dialectPrefix, final String elementName, final boolean createHtml5StandardName) {

        super();

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }

        this.dialectPrefix = dialectPrefix;
        this.elementName = elementName;

        this.completeNSElementName = (dialectPrefix == null? elementName : dialectPrefix + ":" + elementName);
        this.completeHtml5StandatdElementName = (dialectPrefix == null? elementName : dialectPrefix + "-" + elementName);

    }


    public String getDialectPrefix() {
        return this.dialectPrefix;
    }

    public String getAttributeName() {
        return this.elementName;
    }


    public String getCompleteNSElementName() {
        return this.completeNSElementName;
    }

    public String getCompleteHtml5StandatdElementName() {
        return this.completeHtml5StandatdElementName;
    }

}
