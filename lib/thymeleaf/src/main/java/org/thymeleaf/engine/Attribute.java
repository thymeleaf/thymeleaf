/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.util.FastStringWriter;

/*
 * Engine implementation of IAttribute.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class Attribute implements IAttribute {

    static final String DEFAULT_OPERATOR = "=";

    /*
     * Note: An Attribute should not be made responsible for converting non-String values to String, or computing
     * the boolean-ness of attributes or their representation. All these should be the responsibility of the
     * diverse processors being executed. This class is a raw representation of what appears/will appear on template.
     */


    final AttributeDefinition definition;
    final String completeName;
    final String operator; // can be null
    final String value;
    final AttributeValueQuotes valueQuotes;
    final String templateName;
    final int line;
    final int col;

    private volatile IStandardExpression standardExpression = null;



    Attribute(
            final AttributeDefinition definition,
            final String completeName,
            final String operator,
            final String value,
            final AttributeValueQuotes valueQuotes,
            final String templateName,
            final int line,
            final int col) {
        super();
        this.definition = definition;
        this.completeName = completeName;
        this.value = value;
        if (value == null) {
            this.operator = null;
        } else {
            if (operator == null) {
                this.operator = DEFAULT_OPERATOR;
            } else {
                this.operator = operator;
            }
        }
        if (value == null) {
            // Null value will always have null quotes
            this.valueQuotes = null;
        } else {
            if (valueQuotes == null) {
                this.valueQuotes = AttributeValueQuotes.DOUBLE;
            } else if (valueQuotes == AttributeValueQuotes.NONE && value.length() == 0) {
                // We will not allow no quotes with the empty string
                this.valueQuotes = AttributeValueQuotes.DOUBLE;
            } else {
                this.valueQuotes = valueQuotes;
            }
        }
        this.templateName = templateName;
        this.line = line;
        this.col = col;
    }





    public AttributeDefinition getAttributeDefinition() {
        return this.definition;
    }

    public String getAttributeCompleteName() {
        return this.completeName;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public AttributeValueQuotes getValueQuotes() {
        return this.valueQuotes;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public final boolean hasLocation() {
        return (this.templateName != null && this.line != -1 && this.col != -1);
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }


    IStandardExpression getCachedStandardExpression() {
        return this.standardExpression;
    }

    void setCachedStandardExpression(final IStandardExpression standardExpression) {
        this.standardExpression = standardExpression;
    }



    /*
     * This method allows the easy creation of instances derivate from this one but keeping some specific fields
     */
    Attribute modify(
            final AttributeDefinition definition,
            final String completeName,
            final String value,
            final AttributeValueQuotes valueQuotes) {

        return new Attribute(
                (definition == null? this.definition : definition),
                (completeName == null? this.completeName : completeName),
                this.operator,
                value, // This is not keepable
                (valueQuotes == null? this.valueQuotes : valueQuotes),
                this.templateName,
                this.line,
                this.col);
    }




    public void write(final Writer writer) throws IOException {

        /*
         * How an attribute will be written:
         *    - If value == null : only the attribute name will be written.
         *    - If value != null : the attribute will be written according to its quotes
         */

        writer.write(this.completeName);
        if (this.value != null) {
            writer.write(this.operator);
            if (this.valueQuotes == null) {
                writer.write(this.value);
            } else {
                switch (this.valueQuotes) {
                    case DOUBLE:
                        writer.write('"');
                        writer.write(this.value);
                        writer.write('"');
                        break;
                    case SINGLE:
                        writer.write('\'');
                        writer.write(this.value);
                        writer.write('\'');
                        break;
                    case NONE:
                        writer.write(this.value);
                        break;
                }
            }
        }

    }




    @Override
    public String toString() {
        final Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            // Should never happen!
            throw new TemplateProcessingException("Error computing attribute representation", e);
        }
        return stringWriter.toString();
    }



}
