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
public final class TextAttributeName extends AttributeName {

    final String completeNamespacedAttributeName;



    static TextAttributeName forName(final String prefix, final String attributeName) {

        final boolean hasPrefix = prefix != null && prefix.length() > 0;

        final String completeNamespacedAttributeName;
        final String[] completeAttributeNames;

        if (hasPrefix) {

            completeNamespacedAttributeName = prefix + ":" + attributeName;
            completeAttributeNames = new String[] { completeNamespacedAttributeName };

        } else {

            completeNamespacedAttributeName = attributeName;
            completeAttributeNames = new String[] { attributeName };

        }

        return new TextAttributeName(
                prefix, attributeName,
                completeNamespacedAttributeName, completeAttributeNames);

    }


    private TextAttributeName(
            final String prefix, final String attributeName,
            final String completeNamespacedAttributeName,
            final String[] completeAttributeNames) {

        super(prefix, attributeName, completeAttributeNames);
        this.completeNamespacedAttributeName = completeNamespacedAttributeName;

    }


    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }

}
