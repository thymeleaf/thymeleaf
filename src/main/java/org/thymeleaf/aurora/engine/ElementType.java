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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public enum ElementType {

    // See http://www.w3.org/html/wg/drafts/html/master/syntax.html#elements-0

    VOID(false), RAW_TEXT(true), ESCAPABLE_RAW_TEXT(true), FOREIGN(true), NORMAL(true);

    private final boolean hasBody;

    private ElementType(final boolean hasBody) {
        this.hasBody = hasBody;
    }

    public boolean hasBody() {
        return this.hasBody;
    }

}
