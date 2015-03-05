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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AttributeDefinition {

    protected final AttributeName attributeName;

    AttributeDefinition(final AttributeName attributeName) {

        super();

        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }

        this.attributeName = attributeName;

    }


    public final AttributeName getAttributeName() {
        return this.attributeName;
    }


    public final String toString() {
        return getAttributeName().toString();
    }



    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!o.getClass().equals(this.getClass())) {
            return false;
        }

        final AttributeDefinition that = (AttributeDefinition) o;

        if (!this.attributeName.equals(that.attributeName)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        return attributeName.hashCode();
    }

}
