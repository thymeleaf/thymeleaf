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
package org.thymeleaf.aurora.parser;

import org.thymeleaf.aurora.engine.AttributeDefinition;
import org.thymeleaf.aurora.engine.ElementAttributes;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
class ParsedElementAttributes extends ElementAttributes {


    // This class extends Attributes in order to give access to the template adapters to package-protected methods


    ParsedElementAttributes() {
        super();
    }


    @Override
    protected void reset() {
        super.reset();
    }

    @Override
    protected void addAttribute(
            final AttributeDefinition attributeDefinition,
            final String name, final String operator, final String value, final boolean doubleQuoted, final boolean singleQuoted,
            final int line, final int col) {
        super.addAttribute(attributeDefinition, name, operator, value, doubleQuoted, singleQuoted, line, col);
    }

    @Override
    protected void addInnerWhiteSpace(
            final String whiteSpace,
            final int line, final int col) {
        super.addInnerWhiteSpace(whiteSpace, line, col);
    }

}
