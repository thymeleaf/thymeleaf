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
import org.thymeleaf.standard.util.StandardScriptInliningUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardCSSInliner implements IInliner {

    public static final StandardCSSInliner INSTANCE = new StandardCSSInliner();


    private StandardCSSInliner() {
        super();
    }

    public String getName() {
        return "STANDARDDART";
    }


    public CharSequence inline(final IProcessingContext context, final CharSequence text, final boolean textIsWhitespace) {

        /*
         * If all the text to be inlined is whitespace (we know this from the moment it was parsed), then just return,
         * because there is no way we can do anything with just whitespace
         */
        if (textIsWhitespace) {
            return text;
        }

        // We are processing the contents of a JavaScript <script> tag, so we don't need to apply HtmlEscaping of any kind...
        return StandardScriptInliningUtils.inline(
                        context, StandardScriptInliningUtils.StandardScriptInliningLanguage.DART, text.toString());

    }


}
