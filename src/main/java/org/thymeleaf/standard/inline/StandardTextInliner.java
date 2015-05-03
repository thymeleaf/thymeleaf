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
import org.thymeleaf.inline.ITextInliner;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardTextInliner implements ITextInliner {

    public static final StandardTextInliner INSTANCE = new StandardTextInliner();


    private StandardTextInliner() {
        super();
    }

    public String getName() {
        return "STANDARDTEXT";
    }


    public CharSequence inline(final IProcessingContext context, final CharSequence text) {

        boolean candidate = false;
        int n = text.length();
        while (n-- != 0) {
            if (text.charAt(n) == '}') {
                candidate = true;
            }
        }

        if (candidate) {
            System.out.println("INLINING CANDIDATE: " + text.toString());
        }

        return text;

    }


}
