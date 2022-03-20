/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2021, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.spring6.web.webflux;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
final class MultiValueMapUtil {

    static Map<String,String[]> stringToStringArrayMultiMap(final MultiValueMap<String,String> multiValueMap) {
        if (multiValueMap == null) {
            return null;
        }
        final Map<String,String[]> stringArrayMap =
                new LinkedHashMap<String,String[]>(multiValueMap.size() + 1, 1.0f);
        for (final Map.Entry<String, List<String>> multiValueMapEntry : multiValueMap.entrySet()) {
            final List<String> multiValueMapEntryValue = multiValueMapEntry.getValue();
            stringArrayMap.put(
                    multiValueMapEntry.getKey(),
                    multiValueMapEntry.getValue().toArray(new String[multiValueMapEntryValue.size()]));
        }
        return Collections.unmodifiableMap(stringArrayMap);
    }


    static Map<String,String[]> cookieToStringArrayMultiMap(final MultiValueMap<String, HttpCookie> multiValueMap) {
        if (multiValueMap == null) {
            return null;
        }
        final Map<String,String[]> stringArrayMap =
                new LinkedHashMap<String,String[]>(multiValueMap.size() + 1, 1.0f);
        for (final Map.Entry<String, List<HttpCookie>> multiValueMapEntry : multiValueMap.entrySet()) {
            stringArrayMap.put(
                    multiValueMapEntry.getKey(),
                    multiValueMapEntry.getValue().stream().map(HttpCookie::getValue).toArray(String[]::new));
        }
        return Collections.unmodifiableMap(stringArrayMap);
    }


    private MultiValueMapUtil() {
        super();
    }

}
