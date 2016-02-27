/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;

/**
 * <p>
 *   Utility class containing methods that answer questions about the contents or features
 *   of specific event objects.
 * </p>
 * <p>
 *   Meant for <strong>internal use only</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class EngineEventUtils {



    public static boolean isWhitespace(final IText text) {

        if (text == null) {
            return false;
        }

        if (text instanceof Text) {
            return ((Text) text).isWhitespace();
        }

        return computeWhitespace(text);

    }


    public static boolean isWhitespace(final ICDATASection cdataSection) {

        if (cdataSection == null) {
            return false;
        }

        if (cdataSection instanceof CDATASection) {
            return ((CDATASection) cdataSection).isWhitespace();
        }

        return computeWhitespace(cdataSection.getContent());

    }


    public static boolean isWhitespace(final IComment comment) {

        if (comment == null) {
            return false;
        }

        if (comment instanceof Comment) {
            return ((Comment) comment).isWhitespace();
        }

        return computeWhitespace(comment.getContent());

    }



    public static boolean isInlineable(final IText text) {

        if (text == null) {
            return false;
        }

        if (text instanceof Text) {
            return ((Text) text).isInlineable();
        }

        return computeInlineable(text);

    }


    public static boolean isInlineable(final ICDATASection cdataSection) {

        if (cdataSection == null) {
            return false;
        }

        if (cdataSection instanceof CDATASection) {
            return ((CDATASection) cdataSection).isInlineable();
        }

        return computeInlineable(cdataSection.getContent());

    }


    public static boolean isInlineable(final IComment comment) {

        if (comment == null) {
            return false;
        }

        if (comment instanceof Comment) {
            return ((Comment) comment).isInlineable();
        }

        return computeInlineable(comment.getContent());

    }







    private static boolean computeWhitespace(final CharSequence text) {
        int n = text.length();
        if (n == 0) {
            return false;
        }
        char c;
        while (n-- != 0) {
            c = text.charAt(n);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }


    private static boolean computeInlineable(final CharSequence text) {
        int n = text.length();
        if (n == 0) {
            return false;
        }
        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != 0) {
            c1 = text.charAt(n);
            if (c1 == ']' && c0 == ']') {
                inline = 1;
            } else if (c1 == ')' && c0 == ']') {
                inline = 2;
            } else if (inline == 1 && c1 == '[' && c0 == '[') {
                return true;
            } else if (inline == 2 && c1 == '[' && c0 == '(') {
                return true;
            }
            c0 = c1;
        }
        return false;
    }




    private EngineEventUtils() {
        super();
    }

}
