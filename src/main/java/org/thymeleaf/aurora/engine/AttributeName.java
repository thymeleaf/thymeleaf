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
public class AttributeName {

    private final String dialectPrefix;
    private final String attributeName;
    private final String completeNSAttributeName;
    private final String completeDataAttributeName;


    public AttributeName(final String dialectPrefix, final String attributeName) {

        super();

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        this.dialectPrefix = dialectPrefix;
        this.attributeName = attributeName;

        this.completeNSAttributeName = (dialectPrefix == null? attributeName : dialectPrefix + ":" + attributeName);

        // Note that, if prefix is null, we are not creating attribute names like "data-{name}" because the
        // fact tha prefix is null means that we want to act on the standard HTML/XML attributes themselves.
        this.completeDataAttributeName = (dialectPrefix == null? attributeName : "data-" + dialectPrefix + "-" + attributeName);

    }


    public String getDialectPrefix() {
        return this.dialectPrefix;
    }

    public String getAttributeName() {
        return this.attributeName;
    }


    public String getCompleteNSAttributeName() {
        return this.completeNSAttributeName;
    }

    public String getCompleteDataAttributeName() {
        return this.completeDataAttributeName;
    }

}
