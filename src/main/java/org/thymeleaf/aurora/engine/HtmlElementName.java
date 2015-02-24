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
public final class HtmlElementName extends ElementName {

    final String completeNamespacedElementName;
    final String completeHtml5ElementName;




    HtmlElementName(final String elementName) {
        this(null, elementName);
    }


    HtmlElementName(final String prefix, final String elementName) {

        super(prefix == null || prefix.length() == 0? null : prefix.toLowerCase(),
              elementName == null || elementName.length() == 0? null : elementName.toLowerCase());

        if (this.prefix == null || this.prefix.length() == 0) {

            this.completeNamespacedElementName = this.elementName;
            this.completeHtml5ElementName = this.elementName;

            // The completeAttributeNames array is assigned correctly at the parent class in this case

        } else {

            this.completeNamespacedElementName = this.prefix + ":" + this.elementName;
            this.completeHtml5ElementName = this.prefix + "-" + this.elementName;

            this.completeElementNames =
                    new String[] { this.completeNamespacedElementName, this.completeHtml5ElementName };

        }

    }



    public String getCompleteNamespacedElementName() {
        return this.completeNamespacedElementName;
    }

    public String getCompleteHtml5ElementName() {
        return this.completeHtml5ElementName;
    }


}
