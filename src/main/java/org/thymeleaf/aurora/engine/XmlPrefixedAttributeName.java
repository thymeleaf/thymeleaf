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
public class XmlPrefixedAttributeName extends XmlAttributeName {

    final String prefix;
    final String completeNamespacedAttributeName;
    final String[] completeAttributeNames;




    XmlPrefixedAttributeName(final String prefix, final String attributeName) {

        super(attributeName);

        if (prefix == null || prefix.length() == 0) {
            throw new IllegalArgumentException("Attribute prefix cannot be null or empty");
        }

        this.prefix = prefix;

        this.completeNamespacedAttributeName = prefix + ":" + this.attributeName;
        this.completeAttributeNames = new String[] { this.completeNamespacedAttributeName };

    }


    @Override
    public boolean isPrefixed() {
        return true;
    }


    public String getPrefix() {
        return this.prefix;
    }


    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }

    @Override
    public String[] getCompleteAttributeNames() {
        return this.completeAttributeNames;
    }



    @Override
    public String toString() {
        return "{" + this.completeNamespacedAttributeName + "}";
    }

}
