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
package org.thymeleaf.templateparser.text;

/*
 * This class converts inlined output expressions into their equivalent element events, which makes it possible
 * to cache parsed inlined expressions.
 *
 * Some examples:
 *
 *     [[${someVar}]]            ->     [# th:text="${someVar}"/]          (decomposed into the corresponding events)
 *     [(${someVar})]            ->     [# th:utext="${someVar}"/]         (decomposed into the corresponding events)
 *
 * NOTE: The inlining mechanism is a part of the Standard Dialects, so the conversion performed by this handler
 *       on inlined output expressions should only be applied if one of the Standard Dialects has been configured.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class InlinedOutputExpressionProcessorTextHandler extends AbstractChainedTextHandler {

    private final boolean standardDialectPresent;
    private final String standardDialectPrefix;



    InlinedOutputExpressionProcessorTextHandler(
            final ITextHandler handler, final boolean standardDialectPresent, final String standardDialectPrefix) {
        super(handler);
        this.standardDialectPresent = standardDialectPresent;
        this.standardDialectPrefix = standardDialectPrefix;
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {

        if (!this.standardDialectPresent) {
            getNext().handleText(buffer, offset, len, line, col);
            return;
        }


    }


}