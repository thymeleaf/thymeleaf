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
public final class TextElementName extends ElementName {

    final String completeNamespacedElementName;



    static TextElementName forName(final String prefix, final String elementName) {

        final boolean hasPrefix = prefix != null && prefix.length() > 0;

        final String completeNamespacedElementName;
        final String[] completeElementNames;

        if (hasPrefix) {

            completeNamespacedElementName = prefix + ":" + elementName;
            completeElementNames = new String[] { completeNamespacedElementName };

        } else {

            completeNamespacedElementName = elementName;
            completeElementNames = new String[] { elementName };

        }

        return new TextElementName(
                prefix, elementName,
                completeNamespacedElementName, completeElementNames);

    }


    private TextElementName(
            final String prefix, final String elementName,
            final String completeNamespacedElementName,
            final String[] completeElementNames) {

        super(prefix, elementName, completeElementNames);
        this.completeNamespacedElementName = completeNamespacedElementName;

    }



    public String getCompleteNamespacedElementName() {
        return this.completeNamespacedElementName;
    }

}
