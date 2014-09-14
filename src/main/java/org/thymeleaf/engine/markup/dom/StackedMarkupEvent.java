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
package org.thymeleaf.engine.markup.dom;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class StackedMarkupEvent {

    /*
     * Objects of this class will represent the events that have happened since the stacking of events
     * was started (by some processor) during markup handling.
     */

    // TODO Probably better to just create new subclasses for each type!

    private final StackedMarkupEventType type;
    private final int line;
    private final int col;
    private final Object[] data;


    public static StackedMarkupEvent forText(final String content, final int line, final int col) {
        return new StackedMarkupEvent(StackedMarkupEventType.TEXT, line, col, content);
    }




    private StackedMarkupEvent(final StackedMarkupEventType type, final int line, final int col, final Object... data) {
        super();
        this.type = type;
        this.line = line;
        this.col = col;
        this.data = data;
    }


}
