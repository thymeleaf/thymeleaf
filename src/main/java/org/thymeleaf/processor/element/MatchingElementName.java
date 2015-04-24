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
package org.thymeleaf.processor.element;

import org.thymeleaf.engine.ElementName;
import org.thymeleaf.engine.HTMLElementName;
import org.thymeleaf.engine.XMLElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtil;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MatchingElementName {

    private final TemplateMode templateMode;
    private final ElementName matchingElementName;
    private final String matchingAllElementsWithPrefix;
    private final boolean matchingAllElements;



    public static MatchingElementName forElementName(final TemplateMode templateMode, final ElementName matchingElementName) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(matchingElementName, "Matching element name cannot be null");
        if (templateMode.isHTML() && !(matchingElementName instanceof HTMLElementName)) {
            throw new IllegalArgumentException("Element names for HTML template mode must be of class " + HTMLElementName.class.getName());
        } else if (templateMode.isXML() && !(matchingElementName instanceof XMLElementName)) {
            throw new IllegalArgumentException("Element names for XML template mode must be of class " + XMLElementName.class.getName());
        }
        return new MatchingElementName(templateMode, matchingElementName, null, false);
    }


    public static MatchingElementName forAllElementsWithPrefix(final TemplateMode templateMode, final String matchingAllElementsWithPrefix) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        // Prefix can actually be null -> match all elements with no prefix
        return new MatchingElementName(templateMode, null, matchingAllElementsWithPrefix, false);
    }


    public static MatchingElementName forAllElements(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingElementName(templateMode, null, null, true);
    }



    private MatchingElementName(
            final TemplateMode templateMode, final ElementName matchingElementName,
            final String matchingAllElementsWithPrefix, final boolean matchingAllElements) {
        super();
        this.templateMode = templateMode;
        this.matchingElementName = matchingElementName;
        this.matchingAllElementsWithPrefix = matchingAllElementsWithPrefix;
        this.matchingAllElements = matchingAllElements;
    }




    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public ElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public String getMatchingAllElementsWithPrefix() {
        return this.matchingAllElementsWithPrefix;
    }


    public boolean isMatchingAllElements() {
        return this.matchingAllElements;
    }




    public boolean matches(final ElementName elementName) {

        Validate.notNull(elementName, "Element name cannot be null");

        if (this.matchingElementName == null) {

            if (templateMode.isHTML() && !(elementName instanceof HTMLElementName)) {
                return false;
            } else if (templateMode.isXML() && !(elementName instanceof XMLElementName)) {
                return false;
            } else if (templateMode.isText()) {
                // Nothing to do with text and matching elements!
                return false;
            }

            if (this.matchingAllElements) {
                return true;
            }

            if (this.matchingAllElementsWithPrefix == null) {
                return elementName.getPrefix() == null;
            }

            final String elementNamePrefix = elementName.getPrefix();
            if (elementNamePrefix == null) {
                return false; // we already checked we are not matching nulls
            }

            return TextUtil.equals(!this.templateMode.isHTML(), this.matchingAllElementsWithPrefix, elementNamePrefix);

        }

        return this.matchingElementName.equals(elementName);

    }




    @Override
    public String toString() {
        if (this.matchingElementName == null) {
            if (this.matchingAllElements) {
                return "*";
            }
            if (this.matchingAllElementsWithPrefix == null) {
                return "[^:]*";
            }
            return this.matchingAllElementsWithPrefix + ":*";
        }
        return matchingElementName.toString();
    }

}
