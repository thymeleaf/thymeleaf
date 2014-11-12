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
public class HtmlPrefixedAttributeName extends PrefixedAttributeName {

    final String completeNamespacedAttributeName;
    final String completeHtml5CustomAttributeName;
    final String[] completeAttributeNames;




    HtmlPrefixedAttributeName(final String prefix, final String attributeName) {

        super((prefix == null? null : prefix.toLowerCase()), (attributeName == null? null : attributeName.toLowerCase()));
        // Prefix CANNOT be null
        this.completeNamespacedAttributeName = this.prefix + ":" + this.attributeName;
        this.completeHtml5CustomAttributeName = "data-" + this.prefix + "-" + this.attributeName;

        this.completeAttributeNames =
                new String[] { this.completeNamespacedAttributeName, this.completeHtml5CustomAttributeName };

    }


    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }

    public String getCompleteHtml5CustomAttributeName() {
        return this.completeHtml5CustomAttributeName;
    }

    @Override
    public String[] getCompleteAttributeNames() {
        return this.completeAttributeNames;
    }



    @Override
    public String toString() {
        return "{" + this.completeNamespacedAttributeName + "," + this.completeHtml5CustomAttributeName + "}";
    }

}
