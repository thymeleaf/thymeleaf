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
package org.thymeleaf.text;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class NoCacheTextRepository implements ITextRepository {


    public NoCacheTextRepository() {
        super();
    }


    public String getText(final CharSequence text) {
        if (text == null) {
            return null;
        }
        return text.toString();
    }


    public String getText(final CharSequence text0, final CharSequence text1) {
        if (text0 == null) {
            return getText(text1);
        }
        if (text1 == null) {
            return getText(text0);
        }
        return text0.toString() + text1.toString();
    }


    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2) {
        if (text0 == null) {
            return getText(text1, text2);
        }
        if (text1 == null) {
            return getText(text0, text2);
        }
        if (text2 == null) {
            return getText(text0, text1);
        }
        return text0.toString() + text1.toString() + text2.toString();
    }


    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2, final CharSequence text3) {
        if (text0 == null) {
            return getText(text1, text2, text3);
        }
        if (text1 == null) {
            return getText(text0, text2, text3);
        }
        if (text2 == null) {
            return getText(text0, text1, text3);
        }
        if (text3 == null) {
            return getText(text0, text1, text2);
        }
        return text0.toString() + text1.toString() + text2.toString() + text3.toString();
    }


    public String getText(final char[] text, final int offset, final int len) {
        return new String(text, offset, len);
    }

}
