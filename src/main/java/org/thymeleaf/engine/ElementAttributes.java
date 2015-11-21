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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementAttributes implements IElementAttributes {

    private static final int DEFAULT_ATTRIBUTES_SIZE = 4;

    private final TemplateMode templateMode;
    private final AttributeDefinitions attributeDefinitions;

    int version = 0; // Used for determining things like e.g. if we need to recompute processors

    ElementAttribute[] attributes = null;
    AttributeName[] attributeNames = null;
    int attributesSize = 0;

    private InnerWhiteSpace[] innerWhiteSpaces = null;
    private int innerWhiteSpacesSize = 0;

    // Meant to cache the list of attribute names
    private List<String> allCompleteNames = null;
    private List<AttributeName> allAttributeNames = null;
    private int computedNamesVersion = 0;



    // Meant to be called only from the element / element tag constructors or the corresponding cloning methods
    ElementAttributes(final TemplateMode templateMode, final AttributeDefinitions attributeDefinitions) {
        super();
        this.templateMode = templateMode;
        this.attributeDefinitions = attributeDefinitions;
    }


    public final int size() {
        return this.attributesSize;
    }




    private void updateNameLists() {
        if (this.allCompleteNames == null || this.allAttributeNames == null || this.computedNamesVersion != this.version) {
            this.allCompleteNames = new ArrayList<String>(this.attributesSize + 1);
            this.allAttributeNames = new ArrayList<AttributeName>(this.attributesSize + 1);
            for (int i = 0; i < this.attributesSize; i++) {
                this.allCompleteNames.add(this.attributes[i].name);
                this.allAttributeNames.add(this.attributes[i].definition.attributeName);
            }
            this.allCompleteNames = Collections.unmodifiableList(this.allCompleteNames);
            this.allAttributeNames = Collections.unmodifiableList(this.allAttributeNames);
            this.computedNamesVersion = this.version;
        }
    }


    public List<String> getAllCompleteNames() {
        updateNameLists();
        return this.allCompleteNames;
    }

    public List<AttributeName> getAllAttributeNames() {
        updateNameLists();
        return this.allAttributeNames;
    }



    private int searchAttribute(final String completeName) {
        // We will first try exact match on the names with which the attributes appear on template, as an optimization
        // on the base case (use the AttributeDefinition).
        int n = this.attributesSize;
        while (n-- != 0) {
            if (this.attributes[n].name.equals(completeName)) {
                return n;
            }
        }
        // Not found that way - before discarding, let's search using AttributeDefinitions
        return searchAttribute(computeAttributeDefinition(completeName).attributeName);
    }


    private int searchAttribute(final String prefix, final String name) {
        if (prefix == null || prefix.length() == 0) {
            // Optimization: searchAttribute(name) might be faster if we are able to avoid using AttributeDefinition
            return searchAttribute(name);
        }
        return searchAttribute(computeAttributeDefinition(prefix, name).attributeName);
    }


    private int searchAttribute(final AttributeName attributeName) {
        int n = this.attributesSize;
        while (n-- != 0) {
            if (this.attributeNames[n].equals(attributeName)) {
                return n;
            }
        }
        return -1;
    }




    public final boolean hasAttribute(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        return searchAttribute(completeName) >= 0;
    }


    public final boolean hasAttribute(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        return searchAttribute(prefix, name) >= 0;
    }


    public final boolean hasAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        return searchAttribute(attributeName) >= 0;
    }




    public final String getCompleteName(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].name;
    }


    public final String getCompleteName(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].name;
    }


    public final String getCompleteName(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].name;
    }




    public final String getValue(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].value;
    }


    public final String getValue(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].value;
    }


    public final String getValue(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].value;
    }




    public final AttributeDefinition getAttributeDefinition(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].definition;
    }


    public final AttributeDefinition getAttributeDefinition(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].definition;
    }


    public final AttributeDefinition getAttributeDefinition(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].definition;
    }




    public final ValueQuotes getValueQuotes(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].valueQuotes;
    }


    public final ValueQuotes getValueQuotes(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].valueQuotes;
    }


    public final ValueQuotes getValueQuotes(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos].valueQuotes;
    }




    public final boolean hasLocation(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        return pos >= 0 && this.attributes[pos].line != -1 && this.attributes[pos].col != -1;
    }


    public final boolean hasLocation(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        return pos >= 0 && this.attributes[pos].line != -1 && this.attributes[pos].col != -1;
    }


    public final boolean hasLocation(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        return pos >= 0 && this.attributes[pos].line != -1 && this.attributes[pos].col != -1;
    }




    public final int getLine(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].line;
    }


    public final int getLine(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].line;
    }


    public final int getLine(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].line;
    }




    public final int getCol(final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(completeName);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].col;
    }


    public final int getCol(final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(prefix, name);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].col;
    }


    public final int getCol(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return -1;
        }
        return this.attributes[pos].col;
    }





    public final void clearAll() {
        this.attributesSize = 0;
        this.innerWhiteSpacesSize = 0;
        this.version++;
    }






    public final void setAttribute(final String completeName, final String value) {
        setAttribute(completeName, null, value, null, -1, -1, true);
    }


    public final void setAttribute(final String completeName, final String value, final ValueQuotes valueQuotes) {
        Validate.isTrue(
                !(ValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(ValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        setAttribute(completeName, null, value, valueQuotes, -1, -1, true);
    }


    // Meant to be used from within the engine
    final void setAttribute(
            final String name, final String operator, final String value, final ValueQuotes valueQuotes,
            final int line, final int col, final boolean autoWhiteSpace) {

        Validate.notNull(name, "Attribute name cannot be null");

        Validate.isTrue(value != null || this.templateMode != TemplateMode.XML, "Cannot set null-value attributes in XML template mode");

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
            existingAttribute.reset(existingAttribute.definition, name, operator, value, valueQuotes, line, col);

            this.version++;

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

        newAttribute.reset(
                computeAttributeDefinition(name),
                name,
                (operator == null? ElementAttribute.DEFAULT_OPERATOR : operator),
                value,
                (valueQuotes == null? IElementAttributes.ValueQuotes.DOUBLE : valueQuotes),
                line, col);

        this.attributeNames[this.attributesSize] = newAttribute.definition.getAttributeName();


        if (autoWhiteSpace) {
            // We should be adding whitespace automatically (a ' ' before the attribute)
            insertInnerWhiteSpace(this.attributesSize, " ");
        } else if (this.innerWhiteSpacesSize == this.attributesSize) {
            // This means we have inserted two attributes with no white space in between, so let's add an empty one
            insertInnerWhiteSpace(this.attributesSize, "");
        }

        this.attributesSize++;

        this.version++;

    }






    public final void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value) {
        replaceAttribute(oldName, completeNewName, null, value, null, -1, -1, true);
    }


    public final void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value, final ValueQuotes valueQuotes) {
        Validate.isTrue(
                !(ValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(ValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        replaceAttribute(oldName, completeNewName, null, value, valueQuotes, -1, -1, true);
    }


    // Meant to be used from within the engine
    final void replaceAttribute(
            final AttributeName oldName, final String completeNewName, final String operator, final String value, final ValueQuotes valueQuotes,
            final int line, final int col, final boolean autoWhiteSpace) {

        Validate.notNull(oldName, "Attribute old name cannot be null");
        Validate.notNull(completeNewName, "Attribute new name cannot be null");

        Validate.isTrue(value != null || this.templateMode != TemplateMode.XML, "Cannot set null-value attributes in XML template mode");

        if (this.attributes == null) {

            setAttribute(completeNewName, operator, value, valueQuotes, line, col, autoWhiteSpace);
            // no need to remove the old one -- we didn't have any attributes!
            return;

        }


        int existingIdx = searchAttribute(completeNewName);
        if (existingIdx >= 0) {
            // New Attribute already exists! Must simply change its properties

            final ElementAttribute existingAttribute = this.attributes[existingIdx];
            existingAttribute.reset(existingAttribute.definition, completeNewName, operator, value, valueQuotes, line, col);

            this.version++;

            removeAttribute(oldName); // We will remove the old one as we don't need its position

            return;

        }


        existingIdx = searchAttribute(oldName);
        if (existingIdx >= 0) {
            // Old attribute already exists! Must simply change its properties

            final ElementAttribute existingAttribute = this.attributes[existingIdx];
            existingAttribute.reset(computeAttributeDefinition(completeNewName), completeNewName, operator, value, valueQuotes, line, col);

            this.attributeNames[existingIdx] = existingAttribute.definition.getAttributeName();

            this.version++;

            // No need to remove the old one as we have just rewritten it

            return;

        }

        // Neither the old nor the new attribute seem to exist, so this should work exactly as a 'set' operation
        setAttribute(completeNewName, operator, value, valueQuotes, line, col, autoWhiteSpace);

    }






    public final void removeAttribute(final String prefix, final String name) {

        Validate.notNull(name, "Attribute name cannot be null");

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return;
        }

        final int attrIdx = searchAttribute(prefix, name);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return;
        }

        removeAttribute(attrIdx);

    }


    public final void removeAttribute(final String completeName) {

        Validate.notNull(completeName, "Attribute name cannot be null");

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return;
        }

        final int attrIdx = searchAttribute(completeName);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return;
        }

        removeAttribute(attrIdx);

    }


    public final void removeAttribute(final AttributeName attributeName) {

        Validate.notNull(attributeName, "Attribute name cannot be null");

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return;
        }

        final int attrIdx = searchAttribute(attributeName);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return;
        }

        removeAttribute(attrIdx);

    }


    private void removeAttribute(final int attrIdx) {

        if (attrIdx + 1 == this.attributesSize) {
            // If it's the last attribute, discard it simply changing the size

            this.attributesSize--;

            // Checks on related whitespaces
            if (attrIdx + 2 == this.innerWhiteSpacesSize) {
                // If there was a whitespace after the last attribute, this should still be kept as last whitespace,
                // replacing the one previous to the removed attribute so that we don't end up with things like a
                // '>' symbol appearing on a line on its own
                final InnerWhiteSpace removedInnerWhiteSpace = this.innerWhiteSpaces[attrIdx];
                this.innerWhiteSpaces[attrIdx] = this.innerWhiteSpaces[attrIdx + 1];
                this.innerWhiteSpaces[attrIdx + 1] = removedInnerWhiteSpace;
                this.innerWhiteSpacesSize--;
            } else if (attrIdx + 1 == this.innerWhiteSpacesSize) {
                // If there was a whitespace before the last attribute but not after, we should remove that whitespace
                // so that we don't end up with things like a '>' symbol appearing on a line on its own
                this.innerWhiteSpacesSize--;
            } // else: If we have managed white spaces properly, there should be no other possibilities

            this.version++;

            return;

        }

        final ElementAttribute removedAttribute = this.attributes[attrIdx];

        // Move all attributes after the removed one to fill the hole
        System.arraycopy(this.attributes, attrIdx + 1, this.attributes, attrIdx, (this.attributesSize - (attrIdx + 1)));
        System.arraycopy(this.attributeNames, attrIdx + 1, this.attributeNames, attrIdx, (this.attributesSize - (attrIdx + 1)));

        // Place the removed attribute at the end, so that it can be reused
        this.attributes[this.attributesSize - 1] = removedAttribute;


        this.attributesSize--;

        // Let's see if we have to remove a corresponding whitespace (corresponding == the one after the attribute)
        // We already know it's not the last attribute, so we don't have to care about being the last whitespace or not
        if (attrIdx + 1 < this.innerWhiteSpacesSize) {
            final InnerWhiteSpace removedInnerWhiteSpace = this.innerWhiteSpaces[attrIdx + 1];
            System.arraycopy(this.innerWhiteSpaces, attrIdx + 2, this.innerWhiteSpaces, attrIdx + 1, (this.innerWhiteSpacesSize - (attrIdx + 2)));
            this.innerWhiteSpaces[this.innerWhiteSpacesSize - 1] = removedInnerWhiteSpace;
            this.innerWhiteSpacesSize--;
        }

        this.version++;

    }




    private AttributeDefinition computeAttributeDefinition(final String completeAttributeName) {
        switch (this.templateMode) {
            case HTML:
                return this.attributeDefinitions.forHTMLName(completeAttributeName);
            case XML:
                return this.attributeDefinitions.forXMLName(completeAttributeName);
            case TEXT:
                return this.attributeDefinitions.forTextName(completeAttributeName);
            case JAVASCRIPT:
                return this.attributeDefinitions.forJavaScriptName(completeAttributeName);
            case CSS:
                return this.attributeDefinitions.forCSSName(completeAttributeName);
            case RAW:
                // fall-through
            default:
                throw new IllegalArgumentException("Attribute definitions cannot be created for template mode: " + this.templateMode);
        }
    }

    private AttributeDefinition computeAttributeDefinition(final String prefix, final String attributeName) {
        switch (this.templateMode) {
            case HTML:
                return this.attributeDefinitions.forHTMLName(prefix, attributeName);
            case XML:
                return this.attributeDefinitions.forXMLName(prefix, attributeName);
            case TEXT:
                return this.attributeDefinitions.forTextName(prefix, attributeName);
            case JAVASCRIPT:
                return this.attributeDefinitions.forJavaScriptName(prefix, attributeName);
            case CSS:
                return this.attributeDefinitions.forCSSName(prefix, attributeName);
            case RAW:
                // fall-through
            default:
                throw new IllegalArgumentException("Attribute definitions cannot be created for template mode: " + this.templateMode);
        }
    }





    final void addInnerWhiteSpace(final String whiteSpace) {
        insertInnerWhiteSpace(this.innerWhiteSpacesSize, whiteSpace);
    }


    final void insertInnerWhiteSpace(final int pos, final String whiteSpace) {

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








    public final void write(final Writer writer) throws IOException {

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
            clone.resetAsCloneOf(this);
            return clone;
        }

        void resetAsCloneOf(final InnerWhiteSpace from) {
            this.whiteSpace = from.whiteSpace;
        }

    }




    ElementAttributes cloneElementAttributes() {

        final ElementAttributes clone = new ElementAttributes(this.templateMode, this.attributeDefinitions);
        clone.resetAsCloneOf(this);
        return clone;

    }




    void resetAsCloneOf(final ElementAttributes from) {

        /*
         * This can only be called for the ElementAttributes implementation, and not for the IElementAttributes
         * interface, because it needs to access the internal structures. In any other cases, clone should be used.
         */

        if (from.templateMode != this.templateMode || from.attributeDefinitions != this.attributeDefinitions) {
            throw new IllegalStateException(
                    "Cannot copy element attributes: the ElementAttributes object to copy from does not " +
                    "contain exactly the same TemplateMode and AttributeDefinitions objects, which should never " +
                    "happen.");
        }

        this.attributesSize = from.attributesSize;
        if (from.attributesSize > 0) {

            if (this.attributes == null) {
                // We need new arrays as the 'from' attributes wouldn't fit

                this.attributes = new ElementAttribute[Math.max(from.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];
                this.attributeNames = new AttributeName[Math.max(from.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];

            } else if (this.attributes.length < from.attributesSize) {
                // We need to adjust the size of our arrays

                final ElementAttribute[] newAttributes = new ElementAttribute[Math.max(from.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];
                final AttributeName[] newAttributeNames = new AttributeName[Math.max(from.attributesSize, DEFAULT_ATTRIBUTES_SIZE)];
                System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
                // No need to initialize the names - they're immutable (sort of) and will be assigned in the following block
                this.attributes = newAttributes;
                this.attributeNames = newAttributeNames;

            }

            int n = from.attributesSize;
            while (n-- != 0) {
                if (this.attributes[n] == null) {
                    this.attributes[n] = from.attributes[n].cloneElementAttribute();
                } else {
                    this.attributes[n].resetAsCloneOf(from.attributes[n]);
                }
                this.attributeNames[n] = from.attributeNames[n];
            }

        }


        this.innerWhiteSpacesSize = from.innerWhiteSpacesSize;
        if (from.innerWhiteSpacesSize > 0) {

            if (this.innerWhiteSpaces == null) {
                // We need new arrays as the 'from' attributes wouldn't fit

                this.innerWhiteSpaces = new InnerWhiteSpace[Math.max(from.innerWhiteSpacesSize, DEFAULT_ATTRIBUTES_SIZE)];

            } else if (this.innerWhiteSpaces.length < from.innerWhiteSpacesSize) {
                // We need to adjust the size of our arrays

                final InnerWhiteSpace[] newInnerWhiteSpaces = new InnerWhiteSpace[Math.max(from.innerWhiteSpacesSize, DEFAULT_ATTRIBUTES_SIZE)];
                System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, this.innerWhiteSpaces.length);
                this.innerWhiteSpaces = newInnerWhiteSpaces;

            }

            int n = from.innerWhiteSpacesSize;
            while (n-- != 0) {
                if (this.innerWhiteSpaces[n] == null) {
                    this.innerWhiteSpaces[n] = from.innerWhiteSpaces[n].cloneInnerWhiteSpace();
                } else {
                    this.innerWhiteSpaces[n].resetAsCloneOf(from.innerWhiteSpaces[n]);
                }
            }

        }

        this.allCompleteNames = from.allCompleteNames; // Can do this because it's either null or an unmodifiable list
        this.allAttributeNames = from.allAttributeNames; // Can do this because it's either null or an unmodifiable list

        // We will set the versions to the same as the from in the understanding that this method will only be called
        // internally from methods that perform a complete copy/cloning of the containing tags, therefore copying also
        // the versions living in the tags themselves (and other related structures like e.g. iterators)
        this.version = from.version;
        this.computedNamesVersion = from.computedNamesVersion;

    }




    public final String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception processing String form of ElementAttributes", e);
        }
        return stringWriter.toString();
    }


}
