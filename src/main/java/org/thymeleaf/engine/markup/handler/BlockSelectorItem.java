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

            while (pos < blockSelectorLen && blockSelector.charAt(pos) != '/') { pos++; }

            final String item = blockSelector.substring(start, pos);
            items.add(new BlockSelectorItem(anyLevel, (caseSensitive? item : item.toLowerCase())));

        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Bad format in block selector: " + blockSelector);
        }

        return items;

    }


}
