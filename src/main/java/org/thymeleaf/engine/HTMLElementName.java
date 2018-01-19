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
public final class HTMLElementName extends ElementName {

    final String completeNamespacedElementName;
    final String completeHTML5ElementName;




    static HTMLElementName forName(final String prefix, final String elementName) {

        final boolean hasPrefix = prefix != null && prefix.length() > 0;

        // No-suffix element names are allowed by the HTML5 Custom Element specification ("<tag->" is valid),
        // so we will just normalize to the empty string so that we treat them in the same way as no-name
        // elements in the textual template modes.
        final String nameElementName =
                (elementName == null || elementName.length() == 0)? "" : elementName.toLowerCase();

        final String namePrefix;
        final String completeNamespacedElementName;
        final String completeHTML5ElementName;
        final String[] completeAttributeNames;

        if (hasPrefix) {

            namePrefix = prefix.toLowerCase();
            completeNamespacedElementName = namePrefix + ":" + nameElementName;
            completeHTML5ElementName = namePrefix + "-" + nameElementName;
            completeAttributeNames = new String[] { completeNamespacedElementName, completeHTML5ElementName };

        } else {

            namePrefix = null;
            completeNamespacedElementName = nameElementName;
            completeHTML5ElementName = nameElementName;
            completeAttributeNames = new String[] { nameElementName };

        }

        return new HTMLElementName(
                namePrefix, nameElementName,
                completeNamespacedElementName, completeHTML5ElementName, completeAttributeNames);

    }


    private HTMLElementName(
            final String prefix, final String elementName,
            final String completeNamespacedElementName,
            final String completeHTML5ElementName,
            final String[] completeElementNames) {

        super(prefix, elementName, completeElementNames);

        this.completeNamespacedElementName = completeNamespacedElementName;
        this.completeHTML5ElementName = completeHTML5ElementName;

    }



    public String getCompleteNamespacedElementName() {
        return this.completeNamespacedElementName;
    }

    public String getCompleteHTML5ElementName() {
        return this.completeHTML5ElementName;
    }


}
