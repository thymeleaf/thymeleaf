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
import java.io.StringWriter;
import java.io.Writer;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementAttribute {


    public enum ElementAttributeValueQuotes { DOUBLE, SINGLE, NONE }


    static final String DEFAULT_OPERATOR = "=";

    /*
     * Note: An Attribute should not be made responsible for converting non-String values to String, or computing
     * the boolean-ness of attributes or their representation. All these should be the responsibility of the
     * diverse processors being executed. This class is a raw representation of what appears/will appear on markup.
     */


    private AttributeDefinition definition = null;
    String name = null;
    private String operator = DEFAULT_OPERATOR;
    private String value = null;
    private ElementAttributeValueQuotes valueQuotes = null;
    private int line = -1;
    private int col = -1;



    ElementAttribute() {
        super();
    }


    // No public constructor! Attribute instances should always be created from the corresponding Attributes
    // instances, because these are responsible for setting the right attribute definition, name, etc.


    public AttributeDefinition getDefinition() {
        return this.definition;
    }

    public String getName() {
        return this.name;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public ElementAttributeValueQuotes getValueQuotes() {
        return this.valueQuotes;
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }



    // Used internally, only from the engine
    void setElementAttribute(
            final AttributeDefinition definition,
            final String name, final String operator, final String value,
            final ElementAttributeValueQuotes valueQuotes,
            final int line, final int col) {

        this.definition = definition;
        this.name = name;
        this.operator = operator;
        this.value = value;
        this.valueQuotes = valueQuotes;
        this.line = line;
        this.col = col;

    }



    // Used internally, only from the engine
    void setElementAttribute(
            final String name, final String operator, final String value,
            final ElementAttributeValueQuotes valueQuotes,
            final int line, final int col) {

        this.name = name;
        // This method is only called for modifying existing attributes, so we will be removing the operator
        // when someone removes the value of an attribute, as it is the most expected behaviour
        this.operator = (this.value != null && value == null? null : operator);
        if (this.valueQuotes == null || valueQuotes != null) {
            // This method is only called for modifying existing attributes, so we try not to modify quotes if they
            // already exist. Anyway, if we are setting a value for an attribute that had none, we will be
            // initializing quotes as double
            this.valueQuotes = (this.value == null && value != null? ElementAttributeValueQuotes.DOUBLE : valueQuotes);
        }
        this.value = value;
        this.line = line;
        this.col = col;

    }


    public void setValue(final String value) {
        this.value = value;
        // line and col are no longer valid, as the attribute no longer represents what appeared on the template
        this.line = -1;
        this.col = -1;
    }

    public void setValueQuotes(final ElementAttributeValueQuotes valueQuotes) {
        // valueQuotes == null will basically be the same as NONE
        this.valueQuotes = valueQuotes;
        // line and col are no longer valid, as the attribute no longer represents what appeared on the template
        this.line = -1;
        this.col = -1;
    }




    public void write(final Writer writer) throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        /*
         * How an attribute will be written:
         *    - If operator == null : only the attribute name will be written.
         *    - If operator != null AND value == null : the attribute will be written as if its value were the empty string.
         */

        writer.write(this.name);
        if (this.operator != null) {
            writer.write(this.operator);
            if (this.valueQuotes == null) {
                if (this.value != null) {
                    writer.write(this.value);
                }
            } else {
                switch (this.valueQuotes) {
                    case DOUBLE:
                        writer.write('"');
                        if (this.value != null) {
                            writer.write(this.value);
                        }
                        writer.write('"');
                        break;
                    case SINGLE:
                        writer.write('\'');
                        if (this.value != null) {
                            writer.write(this.value);
                        }
                        writer.write('\'');
                        break;
                    case NONE:
                        if (this.value != null) {
                            writer.write(this.value);
                        }
                        break;
                }
            }
        }

    }


    ElementAttribute cloneElementAttribute() {
        final ElementAttribute clone = new ElementAttribute();
        clone.definition = this.definition;
        clone.name = this.name;
        clone.operator = this.operator;
        clone.value = this.value;
        clone.valueQuotes = this.valueQuotes;
        clone.line = this.line;
        clone.col = this.col;
        return clone;
    }


    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            // Should never happen!
            throw new TemplateProcessingException("Error computing attribute representation", e);
        }
        return stringWriter.toString();
    }



}
