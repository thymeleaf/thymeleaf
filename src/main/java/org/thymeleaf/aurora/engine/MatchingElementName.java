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

import java.util.Arrays;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MatchingElementName {

    final ElementName matchingElementName;
    final String matchingAllWithPrefix;




    public MatchingElementName(final ElementName matchingElementName) {
        super();
        Validate.notNull(matchingElementName, "Matching element name cannot be null");
        this.matchingElementName = matchingElementName;
        this.matchingAllWithPrefix = null;
    }


    public MatchingElementName(final String matchingAllWithPrefix) {
        super();
        // Prefix can actually be null (matching all elements with no prefix)
        this.matchingAllWithPrefix = matchingAllWithPrefix;
        this.matchingElementName = null;
    }


    public ElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public String getMatchingAllWithPrefix() {
        return this.matchingAllWithPrefix;
    }


    public boolean matches(final ElementName elementName) {
        Validate.notNull(elementName, "Element name cannot be null");
        if (this.matchingElementName == null) {
            if (this.matchingAllWithPrefix == null) {
                return elementName.getPrefix() == null;
            }
            this.matchingAllWithPrefix.equals(elementName.getPrefix());
        }
        return this.matchingElementName.equals(elementName);
    }


}
