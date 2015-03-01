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
import java.util.Arrays;

import org.thymeleaf.aurora.util.TextUtil;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class ElementAttributes {

    public enum ValueQuotes { DOUBLE, SINGLE, NONE }

    private static final int DEFAULT_ATTRIBUTES_SIZE = 3;

    private final boolean caseSensitive;

    protected ElementAttribute[] attributes = null;
    protected AttributeName[] attributeNames = null;
    protected int attributesSize = 0;

    protected InnerWhiteSpace[] innerWhiteSpaces = null;
    protected int innerWhiteSpacesSize = 0;




    protected ElementAttributes(final boolean caseSensitive) {
        super();
        this.caseSensitive = caseSensitive;
    }



    public int size() {
        return this.attributesSize;
    }



    private int searchAttribute(final String completeName) {
        int n = this.attributesSize;
        while (n-- != 0) {
            final String[] completeAttributeNames = this.attributeNames[n].completeAttributeNames;
            for (final String completeAttributeName : completeAttributeNames) {
                if (TextUtil.equals(this.caseSensitive, completeAttributeName, completeName)) {
                    return n;
                }
            }
        }
        return -1;
    }


    private int searchAttribute(final String prefix, final String name) {
        int n = this.attributesSize;
        while (n-- != 0) {
            if (TextUtil.equals(this.caseSensitive, this.attributeNames[n].prefix, prefix) &&
                    TextUtil.equals(this.caseSensitive, this.attributeNames[n].attributeName, name)) {
                return n;
            }
        }
        return -1;
    }




    public boolean hasAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        return searchAttribute(completeName) >= 0;
    }


    public boolean hasAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        return searchAttribute(prefix, name) >= 0;
    }




    public String getValue(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].value;
    }


    public String getValue(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].value;
    }





    public void clearAll() {
        this.attributesSize = 0;
        this.innerWhiteSpacesSize = 0;
    }




    public void setAttribute(final String name, final String value) {
        setAttribute(name, null, value, null, -1, -1, true);
    }


    public void setAttribute(final String name, final String value, final ValueQuotes valueQuotes) {
        Validate.isTrue(
                !(ValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        setAttribute(name, null, value, valueQuotes, -1, -1, true);
    }


    // Meant to be used from within the engine
    void setAttribute(
            final String name, final String operator, final String value, final ValueQuotes valueQuotes,
            final int line, final int col, final boolean autoWhiteSpace) {

        Validate.notNull(name, "Attribute name cannot be null");

        if (this.attributes == null) {
            // We had no attributes array yet, create it
            this.attributes = new ElementAttribute[DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(this.attributes, null);
            this.attributeNames = new AttributeName[DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(this.attributeNames, null);
            this.attributesSize = 0;
        }


        final int existingIdx = searchAttribute(name);
        if (existingIdx >= 0) {
            // Attribute already exists! Must simply change its properties (might include a name case change!)

            final ElementAttribute existingAttribute = this.attributes[existingIdx];
            existingAttribute.setElementAttribute(name, operator, value, valueQuotes, line, col);

            return;

        }

        // Attribute DOES NOT exist, so we have to add it

        if (this.attributesSize == this.attributes.length) {

            // We've already filled the attributes array, so we need to grow it (and the attribute names array too)

            final ElementAttribute[] newAttributes = new ElementAttribute[this.attributes.length + DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(newAttributes, null);
            System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributesSize);
            this.attributes = newAttributes;

            final AttributeName[] newAttributeNames = new AttributeName[this.attributeNames.length + DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(newAttributeNames, null);
            System.arraycopy(this.attributeNames, 0, newAttributeNames, 0, this.attributesSize);
            this.attributeNames = newAttributeNames;

        }

        // Obtain a reference to the Attribute object that we are going to fill with data
        ElementAttribute newAttribute = this.attributes[this.attributesSize];
        if (newAttribute == null) {
            // The Attribute object didn't exist yet : create it

            newAttribute = new ElementAttribute();
            this.attributes[this.attributesSize] = newAttribute;

        }

        newAttribute.setElementAttribute(
                getAttributeDefinition(name), name, operator, value, valueQuotes, line, col);

        this.attributeNames[this.attributesSize] = newAttribute.definition.getAttributeName();


        if (autoWhiteSpace) {
            // We should be adding whitespace automatically (a ' ' before the attribute)
            insertInnerWhiteSpace(this.attributesSize, " ");
        } else if (this.innerWhiteSpacesSize == this.attributesSize) {
            // This means we have inserted two attributes with no white space in between, so let's add an empty one
            insertInnerWhiteSpace(this.attributesSize, "");
        }

        this.attributesSize++;

    }




    public void removeAttribute(final String name) {

        Validate.notNull(name, "Attribute name cannot be null");

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return;
        }

        final int existingIdx = searchAttribute(name);
        if (existingIdx < 0) {
            // Attribute does not exist. Just exit
            return;
        }

        if (existingIdx + 1 == this.attributesSize) {
            // If it's the last attribute, discard it simply changing the size

            this.attributesSize--;

            // Checks on related whitespaces
            if (existingIdx + 2 == this.innerWhiteSpacesSize) {
                // If there was a whitespace after the last attribute, this should still be kept as last whitespace,
                // replacing the one previous to the removed attribute so that we don't end up with things like a
                // '>' symbol appearing on a line on its own
                final InnerWhiteSpace removedInnerWhiteSpace = this.innerWhiteSpaces[existingIdx];
                this.innerWhiteSpaces[existingIdx] = this.innerWhiteSpaces[existingIdx + 1];
                this.innerWhiteSpaces[existingIdx + 1] = removedInnerWhiteSpace;
                this.innerWhiteSpacesSize--;
            } else if (existingIdx + 1 == this.innerWhiteSpacesSize) {
                // If there was a whitespace before the last attribute but not after, we should remove that whitespace
                // so that we don't end up with things like a '>' symbol appearing on a line on its own
                this.innerWhiteSpacesSize--;
            } // else: If we have managed white spaces properly, there should be no other possibilities

            return;

        }

        final ElementAttribute removedAttribute = this.attributes[existingIdx];

        // Move all attributes after the removed one to fill the hole
        System.arraycopy(this.attributes, existingIdx + 1, this.attributes, existingIdx, (this.attributesSize - (existingIdx + 1)));
        System.arraycopy(this.attributeNames, existingIdx + 1, this.attributeNames, existingIdx, (this.attributesSize - (existingIdx + 1)));

        // Place the removed attribute at the end, so that it can be reused
        this.attributes[this.attributesSize - 1] = removedAttribute;


        this.attributesSize--;

        // Let's see if we have to remove a corresponding whitespace (corresponding == the one after the attribute)
        // We already know it's not the last attribute, so we don't have to care about being the last whitespace or not
        if (existingIdx + 1 < this.innerWhiteSpacesSize) {
            final InnerWhiteSpace removedInnerWhiteSpace = this.innerWhiteSpaces[existingIdx + 1];
            System.arraycopy(this.innerWhiteSpaces, existingIdx + 2, this.innerWhiteSpaces, existingIdx + 1, (this.innerWhiteSpacesSize - (existingIdx + 2)));
            this.innerWhiteSpaces[this.innerWhiteSpacesSize - 1] = removedInnerWhiteSpace;
            this.innerWhiteSpacesSize--;
        }

    }




    protected abstract AttributeDefinition getAttributeDefinition(final String name);





    void addInnerWhiteSpace(final String whiteSpace) {
        insertInnerWhiteSpace(this.innerWhiteSpacesSize, whiteSpace);
    }


    void insertInnerWhiteSpace(final int pos, final String whiteSpace) {

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

        if (pos < this.innerWhiteSpacesSize) {
            // If pos > size, it will simply be added at the end
            System.arraycopy(this.innerWhiteSpaces, pos, this.innerWhiteSpaces, pos + 1, (this.innerWhiteSpacesSize - pos));
            this.innerWhiteSpaces[pos] = newInnerWhiteSpace;
        }


        this.innerWhiteSpacesSize++;

    }





    public void write(final Writer writer) throws IOException {

        int n = this.attributesSize;
        int i = 0;

        // Write the attributes, with their corresponding inner whitespaces if they exist
        while (n-- != 0) {
            if (i < this.innerWhiteSpacesSize) {
                writer.write(this.innerWhiteSpaces[i].whiteSpace);
            } else {
                // For some reason we don't have a whitespace, so we use the default white space
                writer.write(' ');
            }
            this.attributes[i].write(writer);
            i++;
        }

        // There might be a final whitespace after the last attribute
        if (i < this.innerWhiteSpacesSize) {
            writer.write(this.innerWhiteSpaces[i].whiteSpace);
        }

    }




    static final class InnerWhiteSpace {

        String whiteSpace;

        InnerWhiteSpace() {
            super();
        }

        InnerWhiteSpace cloneInnerWhiteSpace() {
            final InnerWhiteSpace clone = new InnerWhiteSpace();
            clone.whiteSpace = this.whiteSpace;
            return clone;
        }

    }




    protected abstract ElementAttributes cloneElementAttributes();


    protected void cloneElementAttributeProperties(final ElementAttributes clone) {

        if (this.attributesSize > 0) {
            clone.attributes = new ElementAttribute[Math.max(this.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];
            Arrays.fill(clone.attributes, null);
            clone.attributeNames = new AttributeName[Math.max(this.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];
            Arrays.fill(clone.attributeNames, null);
            int n = this.attributesSize;
            while (n-- != 0) {
                clone.attributes[n] = this.attributes[n].cloneElementAttribute();
                clone.attributeNames[n] = this.attributeNames[n];
            }
            clone.attributesSize = this.attributesSize;
        } else {
            clone.attributes = null;
            clone.attributeNames = null;
            clone.attributesSize = 0;
        }

        if (this.innerWhiteSpacesSize > 0) {
            clone.innerWhiteSpaces = new InnerWhiteSpace[Math.max(this.innerWhiteSpacesSize, DEFAULT_ATTRIBUTES_SIZE)];
            Arrays.fill(clone.innerWhiteSpaces, null);
            int n = this.innerWhiteSpacesSize;
            while (n-- != 0) {
                clone.innerWhiteSpaces[n] = this.innerWhiteSpaces[n].cloneInnerWhiteSpace();
            }
            clone.innerWhiteSpacesSize = this.innerWhiteSpacesSize;
        } else {
            clone.innerWhiteSpaces = null;
            clone.innerWhiteSpacesSize = 0;
        }

    }




    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception processing String form of ElementAttributes", e);
        }
        return stringWriter.toString();
    }


}
