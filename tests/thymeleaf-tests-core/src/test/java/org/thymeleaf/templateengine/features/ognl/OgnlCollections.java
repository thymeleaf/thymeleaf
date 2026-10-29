/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.features.ognl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class OgnlCollections {

    public static List<Integer> listOf() {
        return List.of(1, 2, 3);
    }

    public static List<Integer> unmodifiableList() {
        return Collections.unmodifiableList(Arrays.asList(1, 2));
    }

    public static Map<String,Integer> mapOf() {
        return Map.of("aa", 1);
    }

    private OgnlCollections() {
        super();
    }

}
