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
package org.thymeleaf.standard.inline;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.inline.IInliner;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardXMLInliner implements IInliner {

    public static final StandardXMLInliner INSTANCE = new StandardXMLInliner();


    private StandardXMLInliner() {
        super();
    }

    public String getName() {
        return "STANDARDTEXT";
    }


    public CharSequence inline(final IProcessingContext context, final CharSequence text, final boolean textIsWhitespace) {

        /*
         * If all the text to be inlined is whitespace (we know this from the moment it was parsed), then just return,
         * because there is no way we can do anything with just whitespace
         */
        if (textIsWhitespace) {
            return text;
        }

        /*
         * Fail fast - if the text does not look as an 'inlining candidate' (it should contain at least one '}' symbol,
         * then simply skup
         */
        if (!isInliningCandidate(text)) {
            return text;
        }

        // TODO Check processingContext.getTemplateMode() and, if !isText(), UNESCAPE, process, and finally ESCAPE

//        return "[[[" + text.toString() + "]]]";
return text.toString();

    }


    /*
     * This method quickly checks if a text looks like it would need inlining. At least a '}' should be contained for
     * the text to be considered a 'candidate'
     */
    private static boolean isInliningCandidate(final CharSequence text) {
        int n = text.length();
        while (n-- != 0) {
            if (text.charAt(n) == '}') {
                return true;
            }
        }
        return false;
    }

}
