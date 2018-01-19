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

import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AttributeName {

    /*
     * NOTE it is VERY important that an AttributeName does NOT contain a TemplateMode, because there is a type
     * of AttributeName (TextAttributeName) that is used for 3 different template modes: TEXT, JAVASCRIPT and CSS
     */

    protected final String prefix;
    protected final String attributeName;
    protected final String[] completeAttributeNames;
    private final int h;





    protected AttributeName(final String prefix, final String attributeName, final String[] completeAttributeNames) {

        super();

        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        // Prefix CAN be null (if the attribute is not prefixed)

        this.prefix = prefix;
        this.attributeName = attributeName;
        this.completeAttributeNames = completeAttributeNames;
        this.h = Arrays.hashCode(this.completeAttributeNames);

    }


    public String getAttributeName() {
        return this.attributeName;
    }

    public boolean isPrefixed() {
        return this.prefix != null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String[] getCompleteAttributeNames() {
        return this.completeAttributeNames;
    }




    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!o.getClass().equals(this.getClass())) {
            return false;
        }

        final AttributeName that = (AttributeName) o;

        if (this.h != that.h) {
            return false;
        }

        if (!this.completeAttributeNames[0].equals(that.completeAttributeNames[0])) {
            // No need to check the other names as we have already checked the class
            return false;
        }

        return true;

    }




    @Override
    public int hashCode() {
        return this.h;
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        strBuilder.append(this.completeAttributeNames[0]);
        for (int i = 1; i < this.completeAttributeNames.length; i++) {
            strBuilder.append(',');
            strBuilder.append(this.completeAttributeNames[i]);
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }

}
