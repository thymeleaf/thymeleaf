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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class HTMLAttributeName extends AttributeName {

    final String completeNamespacedAttributeName;
    final String completeHTML5AttributeName;



    static HTMLAttributeName forName(final String prefix, final String attributeName) {

        final boolean hasPrefix = prefix != null && prefix.length() > 0;

        final String nameAttributeName =
                (attributeName == null || attributeName.length() == 0)? null : attributeName.toLowerCase();

        final String namePrefix;
        final String completeNamespacedAttributeName;
        final String completeHTML5AttributeName;
        final String[] completeAttributeNames;

        if (hasPrefix) {

            namePrefix = prefix.toLowerCase();
            completeNamespacedAttributeName = namePrefix + ":" + nameAttributeName;
            completeHTML5AttributeName = "data-" + namePrefix + "-" + nameAttributeName;
            completeAttributeNames = new String[] { completeNamespacedAttributeName, completeHTML5AttributeName };

        } else {

            namePrefix = null;
            completeNamespacedAttributeName = nameAttributeName;
            completeHTML5AttributeName = nameAttributeName;
            completeAttributeNames = new String[] { nameAttributeName };

        }

        return new HTMLAttributeName(
                namePrefix, nameAttributeName,
                completeNamespacedAttributeName, completeHTML5AttributeName, completeAttributeNames);

    }


    private HTMLAttributeName(
            final String prefix, final String attributeName,
            final String completeNamespacedAttributeName,
            final String completeHTML5AttributeName,
            final String[] completeAttributeNames) {

        super(prefix, attributeName, completeAttributeNames);

        this.completeNamespacedAttributeName = completeNamespacedAttributeName;
        this.completeHTML5AttributeName = completeHTML5AttributeName;

    }


    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }

    public String getCompleteHTML5AttributeName() {
        return this.completeHTML5AttributeName;
    }



}
