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
import java.util.List;

public class OgnlItem {

    private final String name;
    private final OgnlItem child;
    private final List<OgnlItem> items;


    public static OgnlItem sample() {
        return new OgnlItem("item", null, Arrays.asList(new OgnlItem("aa", null, null), new OgnlItem("bb", null, null)));
    }


    public OgnlItem(final String name, final OgnlItem child, final List<OgnlItem> items) {
        super();
        this.name = name;
        this.child = child;
        this.items = items;
    }

    public String getName() {
        return this.name;
    }

    public OgnlItem getChild() {
        return this.child;
    }

    public List<OgnlItem> getItems() {
        return this.items;
    }

    public boolean isActive() {
        return true;
    }

    public String greet(final String value) {
        return "string " + value;
    }

    public String greet(final Object value) {
        return "object " + value;
    }

    public String join(final String... values) {
        return String.join("-", values);
    }

    public long twice(final long value) {
        return value * 2;
    }

}
