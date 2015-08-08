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
public final class StandardTextInliner implements IInliner {

    public static final StandardTextInliner INSTANCE = new StandardTextInliner();


    private StandardTextInliner() {
        super();
    }

    public String getName() {
        return StandardTextInliner.class.getSimpleName();
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
         * First we will quickly check whether inling is needed at all, so that we don't spend more time on this if
         * not required.
         */
        if (!StandardInlineUtils.mightNeedInlining(text)) {
            return text;
        }


        /*
         * Once we are quite sure that we will need to execute some inlined expressions, let's do it!
         */
        return StandardInlineUtils.performInlining(text);

    }

}
