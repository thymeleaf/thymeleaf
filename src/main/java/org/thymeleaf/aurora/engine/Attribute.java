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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class Attribute {


    private static final String DEFAULT_OPERATOR = "=";


    AttributeDefinition definition = null;
    String name = null;
    String operator = DEFAULT_OPERATOR;
    String value = null;
    AttributeValueQuotes quotes = AttributeValueQuotes.DOUBLE;
    int line = -1;
    int col = -1;


    /*
     * Note 1: Objects of this class is not modifiable from outside the engine package.
     *
     * Note 2: An Attribute should not be made responsible for converting non-String values to String, or computing
     * the boolean-ness of attributes or their representation. All these should be the reponsibility of the
     * diverse processors being executed.
     *
     * Note 3: Except for during parsing, attributes should be modified by means of methods in the Attributes class, not
     * directly modifying the fields in Attribute. This is in order to keep white space and quotes consistency, etc.
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

    public AttributeValueQuotes getQuotes() {
        return this.quotes;
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }






    static void writeAttribute(final Attribute attribute, final Writer writer)
            throws IOException {


        /*
         * How an attribute will be written:
         *    - If operator == null : only the attribute name will be written.
         *    - If operator != null AND value == null : the attribute will be written as if its value were the empty string.
         */


        writer.write(attribute.name);
        if (attribute.operator != null) {
            writer.write(attribute.operator);
            switch (attribute.quotes) {
                case DOUBLE:
                    writer.write('"');
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                    writer.write('"');
                    break;
                case SINGLE:
                    writer.write('\'');
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                    writer.write('\'');
                    break;
                case NONE:
                    if (attribute.value != null) {
                        writer.write(attribute.value);
                    }
                    break;
            }
        }

    }


}
