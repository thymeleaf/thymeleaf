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

import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;

/*
 * This class performs the processing of inlined expressions in text modes. This means searching for inlined expressions
 * such as [${someVar}] (unescaped) or [[${someVar}]] (escaped) and replacing them by the sequence of events
 * corresponding to escaped/unescaped block elements with th:text/th:utext attributes.
 *
 * Some examples:
 *
 *     [${someVar}]           ->     [# th:utext="${someVar}"]
 *     [[${someVar}]]         ->     [# th:text="${someVar}"]
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class InlinedExpressionProcessorTextHandler extends AbstractChainedTextHandler {

    private static final char[] textAttributeName = StandardTextTagProcessor.ATTR_NAME.toCharArray();
    private static final char[] utextAttributeName = StandardUtextTagProcessor.ATTR_NAME.toCharArray();
    private final char[] prefix;

    // This reusable buffer will be used in order to output the th:text/th:utext attributes used to simulate the evaluation of expressions
    private char[] attributeBuffer = null;



    InlinedExpressionProcessorTextHandler(final ITextHandler handler, final String standardDialectPrefix) {

        super(handler);

        // standardDialectPrefix can be null, in which case we don't have to care about commented expressions (only elements)
        if (standardDialectPrefix != null) {
            this.prefix = new char[standardDialectPrefix.length() + 1];
            standardDialectPrefix.getChars(0, standardDialectPrefix.length(), this.prefix, 0);
            this.prefix[standardDialectPrefix.length()] = ':';
        } else {
            this.prefix = null;
        }


    }

}