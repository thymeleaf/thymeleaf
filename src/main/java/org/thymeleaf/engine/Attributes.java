/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class Attributes {

    private static final String DEFAULT_WHITE_SPACE = " ";

    private static final String[] NO_WHITE_SPACE_ARRAY = new String[0];
    private static final String[] DEFAULT_WHITE_SPACE_ARRAY = new String[] { DEFAULT_WHITE_SPACE };


    static final Attributes EMPTY_ATTRIBUTES = new Attributes(null, null);


    final Attribute[] attributes; // might be null if there are no attributes
    final String[] innerWhiteSpaces; // might be null if there are no attributes and no whitespaces



    Attributes(final Attribute[] attributes, final String[] innerWhiteSpaces) {
        super();
        this.attributes = attributes;
        this.innerWhiteSpaces = innerWhiteSpaces;
    }




    private int searchAttribute(final TemplateMode templateMode, final String completeName) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        // We will first try exact match on the names with which the attributes appear on template, as an optimization
        // on the base case (use the AttributeDefinition).
        int n = this.attributes.length;
        while (n-- != 0) {
            if (this.attributes[n].name.equals(completeName)) {
                return n;
            }
        }
        // Not found that way - before discarding, let's search using AttributeDefinitions
        return searchAttribute(AttributeNames.forName(templateMode, completeName));
    }


    private int searchAttribute(final TemplateMode templateMode, final String prefix, final String name) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        if (prefix == null || prefix.length() == 0) {
            // Optimization: searchAttribute(name) might be faster if we are able to avoid using AttributeDefinition
            return searchAttribute(templateMode, name);
        }
        return searchAttribute(AttributeNames.forName(templateMode, prefix, name));
    }


    private int searchAttribute(final AttributeName attributeName) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        int n = this.attributes.length;
        while (n-- != 0) {
            // AttributeName objects are registered in a repository and are singletons, so == is fine
            if (this.attributes[n].definition.attributeName == attributeName) {
                return n;
            }
        }
        return -1;
    }




    boolean hasAttribute(final TemplateMode templateMode, final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        return searchAttribute(templateMode, completeName) >= 0;
    }


    boolean hasAttribute(final TemplateMode templateMode, final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        return searchAttribute(templateMode, prefix, name) >= 0;
    }


    boolean hasAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        return searchAttribute(attributeName) >= 0;
    }




    Attribute getAttribute(final TemplateMode templateMode, final String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        final int pos = searchAttribute(templateMode, completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }


    Attribute getAttribute(final TemplateMode templateMode, final String prefix, final String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        final int pos = searchAttribute(templateMode, prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }


    Attribute getAttribute(final AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        final int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }





/*
    public final void setAttribute(final String completeName, final String value) {
        setAttribute(null, completeName, null, value, null, -1, -1, true);
    }


    public final void setAttribute(final String completeName, final String value, final AttributeValueQuotes valueQuotes) {
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        setAttribute(null, completeName, null, value, valueQuotes, -1, -1, true);
    }


    public final void setAttribute(
            final AttributeDefinition attributeDefinition, final String completeName, final String value, final AttributeValueQuotes valueQuotes) {
        Validate.notNull(attributeDefinition, "Attribute Definition cannot be null");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        setAttribute(attributeDefinition, completeName, null, value, valueQuotes, -1, -1, true);
    }
*/

    Attributes setAttribute(
            final AttributeDefinitions attributeDefinitions, final TemplateMode templateMode,
            final AttributeDefinition attributeDefinition, final String completeName,
            final String operator, final String value, final AttributeValueQuotes valueQuotes,
            final String templateName, final int line, final int col) {

        // attributeDefinition might be null if it wasn't available at the moment of calling this method
        // (including it is basically an optimization for classes that can work against engine implementations)

        Validate.isTrue(value != null || templateMode != TemplateMode.XML, "Cannot set null-value attributes in XML template mode");

        final int existingIdx =
                (attributeDefinition != null? searchAttribute(attributeDefinition.attributeName) : searchAttribute(templateMode, completeName));

        if (existingIdx >= 0) {
            // Attribute already exists! Must simply change its properties (might include a name case change!)

            final Attribute[] newAttributes = this.attributes.clone();
            newAttributes[existingIdx] =
                    newAttributes[existingIdx].modify(completeName, null, operator, value, valueQuotes, templateName, line, col);

            return new Attributes(newAttributes, this.innerWhiteSpaces);

        }

        final AttributeDefinition newAttributeDefinition =
                (attributeDefinition != null ? attributeDefinition : attributeDefinitions.forName(templateMode, completeName));

        final Attribute newAttribute =
                new Attribute(completeName, newAttributeDefinition, operator, value, valueQuotes, templateName, line, col);


        final Attribute[] newAttributes;
        if (this.attributes != null) {
            newAttributes = new Attribute[this.attributes.length + 1];
            System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
            newAttributes[this.attributes.length] = newAttribute;
        } else {
            newAttributes = new Attribute[] { newAttribute };
        }

        final String[] newInnerWhiteSpaces;
        if (this.innerWhiteSpaces != null) {
            newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length + 1];
            System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, this.innerWhiteSpaces.length);
            if (this.innerWhiteSpaces.length == this.attributes.length) {
                // As many inner white spaces as attributes: no white space is being left after the last attribute
                newInnerWhiteSpaces[this.innerWhiteSpaces.length] = DEFAULT_WHITE_SPACE;
            } else {
                // There is a white space after the last attribute, so we are going to respect it
                newInnerWhiteSpaces[this.innerWhiteSpaces.length] = newInnerWhiteSpaces[this.innerWhiteSpaces.length - 1];
                newInnerWhiteSpaces[this.innerWhiteSpaces.length - 1] = DEFAULT_WHITE_SPACE;
            }
        } else {
            newInnerWhiteSpaces = DEFAULT_WHITE_SPACE_ARRAY;
        }


        return new Attributes(newAttributes, newInnerWhiteSpaces);

    }





/*
    public final void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value) {
        replaceAttribute(oldName, null, completeNewName, null, value, null, -1, -1, true);
    }


    public final void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value, final AttributeValueQuotes valueQuotes) {
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        replaceAttribute(oldName, null, completeNewName, null, value, valueQuotes, -1, -1, true);
    }


    public final void replaceAttribute(final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String completeNewName, final String value, final AttributeValueQuotes valueQuotes) {
        Validate.notNull(newAttributeDefinition, "New Attribute Definition cannot be null");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && this.templateMode == TemplateMode.XML),
                "Cannot set no-quote attributes when in XML template mode");
        Validate.isTrue(
                !(AttributeValueQuotes.NONE.equals(valueQuotes) && value != null && value.length() == 0),
                "Cannot set an empty-string value to an attribute with no quotes");
        replaceAttribute(oldName, newAttributeDefinition, completeNewName, null, value, valueQuotes, -1, -1, true);
    }
*/

    Attributes replaceAttribute(
            final AttributeDefinitions attributeDefinitions, final TemplateMode templateMode,
            final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String newCompleteName,
            final String operator, final String value, final AttributeValueQuotes valueQuotes,
            final String templateName, final int line, final int col) {

        // attributeDefinition might be null if it wasn't available at the moment of calling this method
        // (including it is basically an optimization for classes that can work against engine implementations)

        Validate.isTrue(value != null || templateMode != TemplateMode.XML, "Cannot set null-value attributes in XML template mode");


        if (this.attributes == null || oldName == newAttributeDefinition.attributeName) {
            return setAttribute(attributeDefinitions, templateMode, newAttributeDefinition, newCompleteName, operator, value, valueQuotes, templateName, line, col);
        }


        // First check existence of the old one -- if it does not exist, this is exactly the same as setAttribute
        final int oldIdx = searchAttribute(oldName);
        if (oldIdx < 0) {
            return setAttribute(attributeDefinitions, templateMode, newAttributeDefinition, newCompleteName, operator, value, valueQuotes, templateName, line, col);
        }


        // Now check existence of the new one -- if it does exist, we will try to reuse its fields (even if the old one exists too)
        int existingIdx =
                (newAttributeDefinition != null? searchAttribute(newAttributeDefinition.attributeName) : searchAttribute(templateMode, newCompleteName));
        if (existingIdx >= 0) {

            final Attribute[] newAttributes = new Attribute[this.attributes.length - 1];

            // Do remove the old attribute
            System.arraycopy(this.attributes, 0, newAttributes, 0, oldIdx);
            System.arraycopy(this.attributes, oldIdx + 1, newAttributes, oldIdx, newAttributes.length - oldIdx);

            // Let's compute the index of the white space to be removed. In general, we will remove the white space AFTER
            int iwIdx = oldIdx + 1;
            if (oldIdx + 1 == this.attributes.length) {
                // We've just removed the last attribute --- in this case we prefer to remove the white space BEFORE the
                // removed attribute, even if a final white space exists after this attribute
                iwIdx = oldIdx;
            }

            final String[] newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length - 1];
            System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, iwIdx);
            System.arraycopy(this.innerWhiteSpaces, iwIdx + 1, newInnerWhiteSpaces, iwIdx, newInnerWhiteSpaces.length - iwIdx);


            // After removing the old attribute, the position of the new one might have changed
            if (existingIdx > oldIdx) {
                existingIdx--;
            }

            // Now modify the existing new attribute directly in the new array
            newAttributes[existingIdx] =
                    newAttributes[existingIdx].modify(newCompleteName, null, operator, value, valueQuotes, templateName, line, col);

            return new Attributes(newAttributes, this.innerWhiteSpaces);

        }


        // By now we know the old one exists, but the new one doesn't, so let's simply turn the old one into the new one

        final AttributeDefinition computedNewAttributeDefinition =
                (newAttributeDefinition != null ? newAttributeDefinition : attributeDefinitions.forName(templateMode, newCompleteName));

        final Attribute[] newAttributes = this.attributes.clone();
        newAttributes[oldIdx] =
                newAttributes[oldIdx].modify(newCompleteName, computedNewAttributeDefinition, operator, value, valueQuotes, templateName, line, col);


        return new Attributes(newAttributes, this.innerWhiteSpaces);

    }






    Attributes removeAttribute(final TemplateMode templateMode, final String prefix, final String name) {

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return this;
        }

        final int attrIdx = searchAttribute(templateMode, prefix, name);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return this;
        }

        return removeAttribute(attrIdx);

    }


    Attributes removeAttribute(final TemplateMode templateMode, final String completeName) {

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return this;
        }

        final int attrIdx = searchAttribute(templateMode, completeName);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return this;
        }

        return removeAttribute(attrIdx);

    }


    Attributes removeAttribute(final AttributeName attributeName) {

        if (this.attributes == null) {
            // We have no attribute array, nothing to remove
            return this;
        }

        final int attrIdx = searchAttribute(attributeName);
        if (attrIdx < 0) {
            // Attribute does not exist. Just exit
            return this;
        }

        return removeAttribute(attrIdx);

    }


    private Attributes removeAttribute(final int attrIdx) {

        if (this.attributes.length == 1 && this.innerWhiteSpaces.length == 1) {
            // We are removing the last attribute and there is no extra white space: use the EMPTY constant
            return EMPTY_ATTRIBUTES;
        }


        final Attribute[] newAttributes;
        if (this.attributes.length == 1) {
            newAttributes = null;
        } else {
            newAttributes = new Attribute[this.attributes.length - 1];
            System.arraycopy(this.attributes, 0, newAttributes, 0, attrIdx);
            System.arraycopy(this.attributes, attrIdx + 1, newAttributes, attrIdx, newAttributes.length - attrIdx);
        }


        // Let's compute the index of the white space to be removed. In general, we will remove the white space AFTER
        int iwIdx = attrIdx + 1;
        if (attrIdx + 1 == this.attributes.length) {
            // We've just removed the last attribute --- in this case we prefer to remove the white space BEFORE the
            // removed attribute, even if a final white space exists after this attribute
            iwIdx = attrIdx;
        }

        final String[] newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length - 1];
        System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, iwIdx);
        System.arraycopy(this.innerWhiteSpaces, iwIdx + 1, newInnerWhiteSpaces, iwIdx, newInnerWhiteSpaces.length - iwIdx);


        return new Attributes(newAttributes, newInnerWhiteSpaces);

    }












    void write(final Writer writer) throws IOException {

        if (this.attributes == null) {
            if (this.innerWhiteSpaces != null) {
                // In this case, there will be only one white space
                writer.write(this.innerWhiteSpaces[0]);
            }
            return;
        }

        int i = 0;
        for (; i < this.attributes.length; i++) {
            writer.write(this.innerWhiteSpaces[i]);
            this.attributes[i].write(writer);
        }

        // There might be a final whitespace after the last attribute
        if (i < this.innerWhiteSpaces.length) {
            writer.write(this.innerWhiteSpaces[i]);
        }

    }







    @Override
    public String toString() {
        final Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception processing String form of ElementAttributes", e);
        }
        return stringWriter.toString();
    }


}
