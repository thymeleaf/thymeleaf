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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class XMLAttributeName extends AttributeName {

    final String completeNamespacedAttributeName;



    XMLAttributeName(final String attributeName) {
        this(null, attributeName);
    }


    XMLAttributeName(final String prefix, final String attributeName) {

        super(prefix, attributeName);

        if (this.prefix == null || this.prefix.length() == 0) {

            this.completeNamespacedAttributeName = this.attributeName;

            // The completeAttributeNames array is assigned correctly at the parent class in this case

        } else {

            this.completeNamespacedAttributeName = this.prefix + ":" + this.attributeName;

            this.completeAttributeNames =
                    new String[] { this.completeNamespacedAttributeName };

        }

    }

}
