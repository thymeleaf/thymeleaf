/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.text;



/*
 * In text parsers, this class is package-protected for simplicity reasons: handlers don't need to make use of it at all
 *
 * @author Daniel Fernandez
 *
 * @since 3.0.0
 *
 */
final class TextParseStatus {

    int offset;
    int line;
    int col;
    boolean inStructure;
    boolean inCommentLine;
    char literalMarker;



    TextParseStatus() {
        super();
    }

}
