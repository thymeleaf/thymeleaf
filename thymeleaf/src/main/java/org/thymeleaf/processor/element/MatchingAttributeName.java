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
package org.thymeleaf.processor.element;

import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.HTMLAttributeName;
import org.thymeleaf.engine.TextAttributeName;
import org.thymeleaf.engine.XMLAttributeName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   This class models the way in which an {@link IElementProcessor} can match an element by one of
 *   its attributes.
 * </p>
 * <p>
 *   It allows the definition of a matching for a specific attribute with a specific prefix (usually that
 *   of the dialect including the processor), for every attribute with a specific prefix, and also for
 *   simply every attribute.
 * </p>
 * <p>
 *   Objects of this class are normally built by the constructors of the processors or their extended abstract
 *   processor classes.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MatchingAttributeName {

    private final TemplateMode templateMode;
    private final AttributeName matchingAttributeName;
    private final String matchingAllAttributesWithPrefix;
    private final boolean matchingAllAttributes;



    public static MatchingAttributeName forAttributeName(final TemplateMode templateMode, final AttributeName matchingAttributeName) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(matchingAttributeName, "Matching attribute name cannot be null");
        if (templateMode == TemplateMode.HTML && !(matchingAttributeName instanceof HTMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for HTML template mode must be of class " + HTMLAttributeName.class.getName());
        } else if (templateMode == TemplateMode.XML && !(matchingAttributeName instanceof XMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for XML template mode must be of class " + XMLAttributeName.class.getName());
        } else if (templateMode.isText() && !(matchingAttributeName instanceof TextAttributeName)) {
            throw new IllegalArgumentException("Attribute names for any text template modes must be of class " + TextAttributeName.class.getName());
        }
        return new MatchingAttributeName(templateMode, matchingAttributeName, null, false);
    }


    public static MatchingAttributeName forAllAttributesWithPrefix(final TemplateMode templateMode, final String matchingAllAttributesWithPrefix) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        // Prefix can actually be null -> match all attributes with no prefix
        return new MatchingAttributeName(templateMode, null, matchingAllAttributesWithPrefix, false);
    }


    public static MatchingAttributeName forAllAttributes(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingAttributeName(templateMode, null, null, true);
    }



    private MatchingAttributeName(
            final TemplateMode templateMode, final AttributeName matchingAttributeName,
            final String matchingAllAttributesWithPrefix, final boolean matchingAllAttributes) {
        super();
        this.templateMode = templateMode;
        this.matchingAttributeName = matchingAttributeName;
        this.matchingAllAttributesWithPrefix = matchingAllAttributesWithPrefix;
        this.matchingAllAttributes = matchingAllAttributes;
    }




    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public AttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }


    public String getMatchingAllAttributesWithPrefix() {
        return this.matchingAllAttributesWithPrefix;
    }


    public boolean isMatchingAllAttributes() {
        return this.matchingAllAttributes;
    }




    public boolean matches(final AttributeName attributeName) {

        Validate.notNull(attributeName, "Attributes name cannot be null");

        if (this.matchingAttributeName == null) {

            if (this.templateMode == TemplateMode.HTML && !(attributeName instanceof HTMLAttributeName)) {
                return false;
            } else if (this.templateMode == TemplateMode.XML && !(attributeName instanceof XMLAttributeName)) {
                return false;
            } else if (this.templateMode.isText() && !(attributeName instanceof TextAttributeName)) {
                return false;
            }

            if (this.matchingAllAttributes) {
                return true;
            }

            if (this.matchingAllAttributesWithPrefix == null) {
                return attributeName.getPrefix() == null;
            }

            final String attributeNamePrefix = attributeName.getPrefix();
            if (attributeNamePrefix == null) {
                return false; // we already checked we are not matching nulls
            }

            return TextUtils.equals(this.templateMode.isCaseSensitive(), this.matchingAllAttributesWithPrefix, attributeNamePrefix);

        }

        return this.matchingAttributeName.equals(attributeName);

    }



    @Override
    public String toString() {
        if (this.matchingAttributeName == null) {
            if (this.matchingAllAttributes) {
                return "*";
            }
            if (this.matchingAllAttributesWithPrefix == null) {
                return "[^:]*";
            }
            return this.matchingAllAttributesWithPrefix + ":*";
        }
        return matchingAttributeName.toString();
    }


}
