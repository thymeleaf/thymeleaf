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
public enum StackedMarkupEventType {

    TEXT, COMMENT, CDATA_SECTION, XML_DECLARATION, DOCTYPE_CLAUSE, PROCESSING_INSTRUCTION, ATTRIBUTE,
    STANDALONE_ELEMENT_START, STANDALONE_ELEMENT_END, OPEN_ELEMENT_START, OPEN_ELEMENT_END,
    CLOSE_ELEMENT_START, CLOSE_ELEMENT_END, AUTO_CLOSE_ELEMENT_START, AUTO_CLOSE_ELEMENT_END,
    UNMATCHED_CLOSE_ELEMENT_START, UNMATCHED_CLOSE_ELEMENT_END, ELEMENT_INNER_WHITESPACE;

}
