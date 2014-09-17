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
package org.thymeleaf.engine.markup.handler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class BlockSelectorItem {

    final boolean anyLevel;
    final String elementName;

    BlockSelectorItem(final boolean anyLevel, final String elementName) {
        this.anyLevel = anyLevel;
        this.elementName = elementName;
    }



    static List<BlockSelectorItem> parseBlockSelector(final String blockSelector, final boolean caseSensitive) {

        final List<BlockSelectorItem> items = new ArrayList<BlockSelectorItem>(5);

        final int blockSelectorLen = blockSelector.length();

        int pos = 0;
        while (pos < blockSelectorLen) {

            int start = pos;

            while (pos < blockSelectorLen && blockSelector.charAt(pos) == '/') { pos++; }

            if (pos > start + 2 || pos >= blockSelectorLen) {
                throw new IllegalArgumentException("Bad format in block selector: " + blockSelector);
            }

            final boolean anyLevel = (pos == start + 2 || pos == start); // else, there's only one '/'
            start = pos;

            int attrLevel = 0;
            while (pos < blockSelectorLen) {

                final char c = blockSelector.charAt(pos);

                if (c == '/' && attrLevel == 0) {
                    break;
                } else if (c == '[') {
                    attrLevel++;
                } else if (c == ']') {
                    attrLevel--;
                }

                pos++;

            }

            final String item = blockSelector.substring(start, pos);
            items.add(parseBlockItem(anyLevel, item, caseSensitive));

        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Bad format in block selector: " + blockSelector);
        }

        return items;

    }



    static BlockSelectorItem parseBlockItem(
            final boolean anyLevel, final String blockItem, final boolean caseSensitive) {



        return new BlockSelectorItem(anyLevel, (caseSensitive ? blockItem : blockItem.toLowerCase()));

    }



/*
    private static final class AttributeCondition {

        static enum Operator {
            EQUALS, NOT_EQUALS, STARTS_WITH, ENDS_WITH;

            static Operator parse(final String operatorStr) {
                if (operatorStr == null) {
                    return null;
                }
                if ("=".equals(operatorStr)) {
                    return EQUALS;
                }
                if ("!=".equals(operatorStr)) {
                    return NOT_EQUALS;
                }
                if ("^=".equals(operatorStr)) {
                    return STARTS_WITH;
                }
                if ("$=".equals(operatorStr)) {
                    return ENDS_WITH;
                }
                return null;
            }

            static String[] extractOperator(final String specification) {
                final int equalsPos = specification.indexOf('=');
                if (equalsPos == -1) {
                    return new String[] {specification.trim(), null, null};
                }
                final char cprev = specification.charAt(equalsPos - 1);
                switch (cprev) {
                    case '!':
                        return new String[] {
                                specification.substring(0, equalsPos - 1).trim(), "!=",
                                specification.substring(equalsPos + 1).trim()};
                    case '^':
                        return new String[] {
                                specification.substring(0, equalsPos - 1).trim(), "^=",
                                specification.substring(equalsPos + 1).trim()};
                    case '$':
                        return new String[] {
                                specification.substring(0, equalsPos - 1).trim(), "$=",
                                specification.substring(equalsPos + 1).trim()};
                    default:
                        return new String[] {
                                specification.substring(0, equalsPos).trim(), "=",
                                specification.substring(equalsPos + 1).trim()};
                }
            }

        }


        private final String name;
        private final Operator operator;
        private final String value;

        AttributeCondition(final String name, final Operator operator, final String value) {
            super();
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        String getName() {
            return this.name;
        }

        Operator getOperator() {
            return this.operator;
        }

        String getValue() {
            return this.value;
        }

    }

*/
}
