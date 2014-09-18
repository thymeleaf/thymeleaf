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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class MarkupSelectorItems {


    private static final ConcurrentHashMap<String,List<MarkupSelectorItem>> CASE_INSENSITIVE_SELECTOR_ITEMS =
            new ConcurrentHashMap<String, List<MarkupSelectorItem>>(20);
    private static final ConcurrentHashMap<String,List<MarkupSelectorItem>> CASE_SENSITIVE_SELECTOR_ITEMS =
            new ConcurrentHashMap<String, List<MarkupSelectorItem>>(20);
    private static final int SELECTOR_ITEMS_MAX_SIZE = 1000; // Just in case some crazy uses of this are done




    static List<MarkupSelectorItem> forSelector(final boolean caseSensitive, final String selector) {

        if (StringUtils.isEmptyOrWhitespace(selector)) {
            throw new IllegalArgumentException("Selector cannot be null");
        }

        final ConcurrentHashMap<String,List<MarkupSelectorItem>> map =
                (caseSensitive ? CASE_SENSITIVE_SELECTOR_ITEMS : CASE_INSENSITIVE_SELECTOR_ITEMS);

        List<MarkupSelectorItem> items = map.get(selector);
        if (items != null) {
            return items;
        }

        items = MarkupSelectorItem.parseSelector(caseSensitive, selector);

        if (map.size() < SELECTOR_ITEMS_MAX_SIZE) {
            map.putIfAbsent(selector, items);
        }

        return items;

    }




    private MarkupSelectorItems() {
        super();
    }



}
