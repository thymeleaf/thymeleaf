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
public abstract class AttributeName {

    final protected String prefix;
    final protected String attributeName;
    protected String[] completeAttributeNames;





    protected AttributeName(final String prefix, final String attributeName) {

        super();

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        // Prefix CAN be null (if the attribute is not prefixed)

        this.prefix = prefix;
        this.attributeName = attributeName;
        this.completeAttributeNames = new String[] { this.attributeName };

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
