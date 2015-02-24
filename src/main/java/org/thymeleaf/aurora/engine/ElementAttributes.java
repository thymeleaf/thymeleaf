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

import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public class ElementAttributes {

    private static final int DEFAULT_ATTRIBUTES_SIZE = 3;


    protected ElementAttribute[] attributes = null;
    protected int attributesSize = 0;

    protected InnerWhiteSpace[] innerWhiteSpaces = null;
    protected int innerWhiteSpacesSize = 0;




    protected ElementAttributes() {
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
            this.attributes = new ElementAttribute[DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(this.attributes, null);
            this.attributesSize = 0;
        }

        if (this.attributesSize == this.attributes.length) {
            // We've already filled the attributes array, so we need to grow it
            final ElementAttribute[] newAttributes = new ElementAttribute[this.attributes.length + DEFAULT_ATTRIBUTES_SIZE];
            Arrays.fill(newAttributes, null);
            System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributesSize);
            this.attributes = newAttributes;
        }

        // Obtain a reference to the Attribute object that we are going to fill with data
        ElementAttribute newAttribute = this.attributes[this.attributesSize];
        if (newAttribute == null) {
            // The Attribute object didn't exist yet : create it
            newAttribute = new ElementAttribute();
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







    static final class InnerWhiteSpace {

        String whiteSpace;
        int line;
        int col;

        InnerWhiteSpace() {
            super();
        }

    }


}
