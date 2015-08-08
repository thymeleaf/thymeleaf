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

import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class ElementName {

    final protected String prefix;
    final protected String elementName;
    protected String[] completeElementNames;




    protected ElementName(final String prefix, final String elementName) {

        super();

        if (elementName == null || (elementName.length() > 0 && elementName.trim().length() == 0)) { // can be the empty string (e.g. in text modes)
            throw new IllegalArgumentException("Element name cannot be null");
        }


        // Prefix CAN be null (if the element is not prefixed)

        this.prefix = prefix;
        this.elementName = elementName;
        this.completeElementNames = new String[] { this.elementName };

    }


    public String getElementName() {
        return this.elementName;
    }

    public boolean isPrefixed() {
        return this.prefix != null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String[] getCompleteElementNames() {
        return this.completeElementNames;
    }




    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementName)) {
            return false;
        }

        final ElementName that = (ElementName) o;

        if (!Arrays.equals(this.completeElementNames, that.completeElementNames)) {
            return false;
        }

        return true;

    }




    @Override
    public int hashCode() {
        return Arrays.hashCode(this.completeElementNames);
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        strBuilder.append(this.completeElementNames[0]);
        for (int i = 1; i < this.completeElementNames.length; i++) {
            strBuilder.append(',');
            strBuilder.append(this.completeElementNames[i]);
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }

}
