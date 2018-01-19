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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.util.IWritableCharSequence;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractTextualTemplateEvent extends AbstractTemplateEvent implements IEngineTemplateEvent {

    private final CharSequence contentCharSeq;
    private final String contentStr;
    private final int contentLength;

    private volatile String computedContentStr = null;
    private volatile int computedContentLength = -1;
    private volatile Boolean computedContentIsWhitespace = null;
    private volatile Boolean computedContentIsInlineable = null;



    AbstractTextualTemplateEvent(final CharSequence content) {
        super();
        this.contentCharSeq = content;
        if (content != null && content instanceof String) {
            this.contentStr = (String)content;
            this.contentLength = content.length();
        } else {
            this.contentStr = null;
            this.contentLength = -1;
        }
    }


    AbstractTextualTemplateEvent(final CharSequence content, final String templateName, final int line, final int col) {
        super(templateName, line, col);
        this.contentCharSeq = content;
        if (content != null && content instanceof String) {
            this.contentStr = (String)content;
            this.contentLength = content.length();
        } else {
            this.contentStr = null;
            this.contentLength = -1;
        }
    }




    protected final String getContentText() {

        if (this.contentStr != null || this.contentCharSeq == null) {
            return this.contentStr;
        }

        String t = this.computedContentStr;
        if (t == null) {
            this.computedContentStr = t = this.contentCharSeq.toString();
        }
        return t;

    }


    protected final int getContentLength() {

        if (this.contentLength >= 0 || this.contentCharSeq == null) {
            return this.contentLength;
        }

        int l = this.computedContentLength;
        if (l < 0) {
            this.computedContentLength = l = this.contentCharSeq.length();
        }
        return l;

    }


    protected final char charAtContent(final int index) {
        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead
        if (this.contentStr != null) {
            return this.contentStr.charAt(index);
        }
        if (this.computedContentStr != null) {
            // Once the String is computed, this could be faster
            return this.computedContentStr.charAt(index);
        }
        return this.contentCharSeq.charAt(index);
    }


    protected final CharSequence contentSubSequence(final int start, final int end) {
        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead
        if (this.contentStr != null) {
            return this.contentStr.subSequence(start, end);
        }
        if (this.computedContentStr != null) {
            // Once the String is computed, this could be faster
            return this.computedContentStr.subSequence(start, end);
        }
        return this.contentCharSeq.subSequence(start, end);
    }





    final boolean isWhitespace() {
        Boolean w = this.computedContentIsWhitespace;
        if (w == null) {
            this.computedContentIsWhitespace = w = computeWhitespace();
        }
        return w.booleanValue();
    }


    final boolean isInlineable() {
        Boolean i = this.computedContentIsInlineable;
        if (i == null) {
            this.computedContentIsInlineable = i = computeInlineable();
        }
        return i.booleanValue();
    }




    private Boolean computeWhitespace() {

        int n = getContentLength(); // This will leave computedContentLength computed in case it's needed afterwards

        if (n == 0) {
            return Boolean.FALSE; // empty texts are NOT whitespace
        }

        char c;
        while (n-- != 0) {
            c = charAtContent(n);
            if (c != ' ' && c != '\n') { // shortcut - most characters in many templates are just whitespace.
                if (!Character.isWhitespace(c)) {
                    return Boolean.FALSE;
                }
            }
        }

        return Boolean.TRUE;

    }


    private Boolean computeInlineable() {

        int n = getContentLength(); // This will leave computedContentLength computed in case it's needed afterwards

        if (n == 0) {
            return Boolean.FALSE;
        }

        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != 0) {
            c1 = charAtContent(n);
            if (n > 0 && c1 == ']' && c0 == ']') {
                inline = 1;
                n--;
                c1 = charAtContent(n);
            } else if (n > 0 && c1 == ')' && c0 == ']') {
                inline = 2;
                n--;
                c1 = charAtContent(n);
            } else if (inline == 1 && c1 == '[' && c0 == '[') {
                return Boolean.TRUE;
            } else if (inline == 2 && c1 == '[' && c0 == '(') {
                return Boolean.TRUE;
            }
            c0 = c1;
        }

        return Boolean.FALSE;

    }




    public final void writeContent(final Writer writer) throws IOException {
        if (this.contentStr != null) {
            writer.write(this.contentStr);
        } else if (this.computedContentStr != null) {
            writer.write(this.computedContentStr);
        } else if (this.contentCharSeq instanceof IWritableCharSequence) {
            // In the special case we are using a writable CharSequence, we will avoid creating a String
            // for the whole content
            ((IWritableCharSequence) this.contentCharSeq).write(writer);
        } else {
            writer.write(getContentText()); // We write, but make sure we cache the String we create
        }
    }



    @Override
    public String toString() {
        return getContentText();
    }


}
