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
public class AttributeName {

    final String dialectPrefix;
    final String attributeName;
    final String completeNSAttributeName;
    final String completeHtml5CustomAttributeName;




    public static AttributeName forXmlName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Atribute name offset and len must be equal or greater than zero");
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
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(
                            null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen), false);
                }

                return new AttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, attributeNameLen - i),
                        false);
            }

        }

        return new AttributeName(
                null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen), false);

    }



    public static AttributeName forHtmlName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Atribute name offset and len must be equal or greater than zero");
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
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(
                            null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen).toLowerCase(), true);
                }

                if (TextUtil.equals(false, "xml", 0, 3, attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1)))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new AttributeName(
                            null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen).toLowerCase(), true);
                }

                return new AttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))).toLowerCase(),
                        new String(attributeNameBuffer, i, attributeNameLen - i).toLowerCase(),
                        true);
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, attributeNameBuffer, attributeNameOffset, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new AttributeName(
                            null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen).toLowerCase(), true);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new AttributeName(
                            null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen).toLowerCase(), true);

                }
                return new AttributeName(
                        new String(attributeNameBuffer, attributeNameOffset + 5, (i - (attributeNameOffset + 6))).toLowerCase(),
                        new String(attributeNameBuffer, i, attributeNameLen - i).toLowerCase(),
                        true);
            }

        }

        return new AttributeName(
                null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen).toLowerCase(), true);

    }



    public static AttributeName forXmlName(final String attributeName) {

        if (attributeName == null) {
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
                    return new AttributeName(null, attributeName, false);
                }

                return new AttributeName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()),
                        false);
            }

        }

        return new AttributeName(null, attributeName, false);

    }



    public static AttributeName forHtmlName(final String attributeName) {

        if (attributeName == null) {
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
                    return new AttributeName(null, attributeName.toLowerCase(), true);
                }

                if (TextUtil.equals(false, "xml", 0, 3, attributeName, 0, 3)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new AttributeName(null, attributeName.toLowerCase(), true);
                }

                return new AttributeName(
                        attributeName.substring(0, i - 1).toLowerCase(),
                        attributeName.substring(i,attributeName.length()).toLowerCase(),
                        true);
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, attributeName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new AttributeName(null, attributeName.toLowerCase(), true);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new AttributeName(null, attributeName.toLowerCase(), true);

                }
                return new AttributeName(
                        attributeName.substring(5, i - 1).toLowerCase(),
                        attributeName.substring(i, attributeName.length()).toLowerCase(),
                        true);
            }

        }

        return new AttributeName(null, attributeName.toLowerCase(), true);

    }



    public static AttributeName forHtmlName(final String dialectPrefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        return new AttributeName(dialectPrefix, new String(elementNameBuffer, elementNameOffset, elementNameLen), true);
    }



    public static AttributeName forXmlName(final String dialectPrefix, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        return new AttributeName(dialectPrefix, new String(elementNameBuffer, elementNameOffset, elementNameLen), false);
    }



    public static AttributeName forHtmlName(final String dialectPrefix, final String attributeName) {
        return new AttributeName(dialectPrefix, attributeName, true);
    }



    public static AttributeName forXmlName(final String dialectPrefix, final String attributeName) {
        return new AttributeName(dialectPrefix, attributeName, false);
    }





    AttributeName(final String dialectPrefix, final String attributeName, final boolean createHtml5CustomName) {

        super();

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        this.dialectPrefix = dialectPrefix;
        this.attributeName = attributeName;

        this.completeNSAttributeName = (dialectPrefix == null? attributeName : dialectPrefix + ":" + attributeName);

        if (createHtml5CustomName) {
            // Note that, if prefix is null, we are not creating attribute names like "data-{name}" because the
            // fact tha prefix is null means that we want to act on the standard HTML/XML attributes themselves.
            this.completeHtml5CustomAttributeName = (dialectPrefix == null? attributeName : "data-" + dialectPrefix + "-" + attributeName);
        } else {
            this.completeHtml5CustomAttributeName = null;
        }

    }


    public String getDialectPrefix() {
        return this.dialectPrefix;
    }

    public String getAttributeName() {
        return this.attributeName;
    }


    public String getCompleteNSAttributeName() {
        return this.completeNSAttributeName;
    }

    public String getCompleteHtml5CustomAttributeName() {
        return this.completeHtml5CustomAttributeName;
    }



    @Override
    public String toString() {
        // Reference equality is OK (and faster) in this case
        if (this.completeHtml5CustomAttributeName == null || this.completeNSAttributeName == this.completeHtml5CustomAttributeName) {
            return "{" + this.completeNSAttributeName + "}";
        }
        return "{" + this.completeNSAttributeName + "," + this.completeHtml5CustomAttributeName + "}";
    }

}
