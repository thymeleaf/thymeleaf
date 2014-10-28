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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public class Attributes {

    private static final int DEFAULT_ATTRIBUTES_SIZE = 3;


    protected Attribute[] attributes = null;
    protected int attributesSize = 0;

    protected InnerWhiteSpace[] innerWhiteSpaces = null;
    protected int innerWhiteSpacesSize = 0;




    protected Attributes() {
        super();
    }


    protected void reset() {
        this.attributesSize = 0;
        this.innerWhiteSpacesSize = 0;
    }



    protected void addAttribute(
            final AttributeDefinition attributeDefinition,
            final String name, final String operator, final String value, final boolean doubleQuoted, final boolean singleQuoted,
            final int line, final int col) {

        if (this.attributes == null) {
            // We had no attributes array yet, create it
            this.attributes = new Attribute[DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(this.attributes, null);
            this.attributesSize = 0;
        }

        if (this.attributesSize == this.attributes.length) {
            // We've already filled the attributes array, so we need to grow it
            final Attribute[] newAttributes = new Attribute[this.attributes.length + DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(newAttributes, null);
            System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributesSize);
            this.attributes = newAttributes;
        }

        // Obtain a reference to the Attribute object that we are going to fill with data
        Attribute newAttribute = this.attributes[this.attributesSize];
        if (newAttribute == null) {
            // The Attribute object didn't exist yet : create it
            newAttribute = new Attribute();
            this.attributes[this.attributesSize] = newAttribute;
        }

        newAttribute.definition = attributeDefinition;
        newAttribute.name = name;
        newAttribute.operator = operator;
        newAttribute.value = value;
        newAttribute.doubleQuoted = doubleQuoted;
        newAttribute.singleQuoted = singleQuoted;
        newAttribute.line = line;
        newAttribute.col = col;

        this.attributesSize++;

    }



    protected void addInnerWhiteSpace(final String whiteSpace, final int line, final int col) {

        if (this.innerWhiteSpaces == null) {
            // We had no whitespace array yet, create it
            this.innerWhiteSpaces = new InnerWhiteSpace[DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(this.innerWhiteSpaces, null);
            this.innerWhiteSpacesSize = 0;
        }

        if (this.innerWhiteSpacesSize == this.innerWhiteSpaces.length) {
            // We've already filled the whitespace array, so we need to grow it
            final InnerWhiteSpace[] newInnerWhiteSpaces = new InnerWhiteSpace[this.innerWhiteSpaces.length + DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(newInnerWhiteSpaces, null);
            System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, this.innerWhiteSpacesSize);
            this.innerWhiteSpaces = newInnerWhiteSpaces;
        }

        // Obtain a reference to the InnerWhiteSpace object that we are going to fill with data
        InnerWhiteSpace newInnerWhiteSpace = this.innerWhiteSpaces[this.innerWhiteSpacesSize];
        if (newInnerWhiteSpace == null) {
            // The InnerWhiteSpace object didn't exist yet : create it
            newInnerWhiteSpace = new InnerWhiteSpace();
            this.innerWhiteSpaces[this.innerWhiteSpacesSize] = newInnerWhiteSpace;
        }

        newInnerWhiteSpace.whiteSpace = whiteSpace;
        newInnerWhiteSpace.line = line;
        newInnerWhiteSpace.col = col;

        this.innerWhiteSpacesSize++;

    }







    protected static void writeAttributes(final Attributes attributes, final Writer writer)
            throws IOException {

        int n = attributes.attributesSize;
        int i = 0;

        // Write the attributes, with their corresponding inner whitespaces if they exist
        while (n-- != 0) {
            if (i < attributes.innerWhiteSpacesSize) {
                writer.write(attributes.innerWhiteSpaces[i].whiteSpace);
            } else {
                // For some reason we don't have a whitespace, so we use the default white space
                writer.write(' ');
            }
            Attribute.writeAttribute(attributes.attributes[i], writer);
            i++;
        }

        // There might be a final whitespace after the last attribute
        if (i < attributes.innerWhiteSpacesSize) {
            writer.write(attributes.innerWhiteSpaces[i].whiteSpace);
        }

    }






    private static final class InnerWhiteSpace {

        String whiteSpace;
        int line;
        int col;

        InnerWhiteSpace() {
            super();
        }

    }




    private static final class Attribute {


        private static final String DEFAULT_OPERATOR = "=";


        AttributeDefinition definition = null;
        String name = null;
        String operator = DEFAULT_OPERATOR;
        String value = null;
        boolean doubleQuoted = false;
        boolean singleQuoted = false;
        int line = -1;
        int col = -1;


        /*
         * Note 1: Objects of this class are not visible from outside the engine package. All modifications on an element's
         * (in fact an element events) attributes should be done through the Attributes class.
         *
         * Note 2: An Attribute should not be made responsible for converting non-String values to String, or computing
         * the boolean-ness of attributes or their representation. All these should be the reponsibility of the
         * diverse processors being executed.
         */


        Attribute() {
            super();
        }


        public AttributeDefinition getDefinition() {
            return this.definition;
        }

        public String getName() {
            return this.name;
        }

        public String getOperator() {
            return this.operator;
        }

        public Object getValue() {
            return this.value;
        }

        public boolean isDoubleQuoted() {
            return this.doubleQuoted;
        }

        public boolean isSingleQuoted() {
            return this.singleQuoted;
        }

        public int getLine() {
            return this.line;
        }

        public int getCol() {
            return this.col;
        }






        private static void writeAttribute(final Attribute attribute, final Writer writer)
                throws IOException {

            /*
             * How an attribute will be written:
             *    - If operator == null : only the attribute name will be written.
             *    - If operator != null AND value == null : the attribute will be written as if its value were the empty string.
             */

            writer.write(attribute.name);
            if (attribute.operator != null) {
                writer.write(attribute.operator);
                if (attribute.doubleQuoted) {
                    writer.write('"');
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                    writer.write('"');
                } else if (attribute.singleQuoted) {
                    writer.write('\'');
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                    writer.write('\'');
                } else {
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                }
            }

        }

    }


}
