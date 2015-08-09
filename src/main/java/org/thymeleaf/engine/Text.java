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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.IText;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.AggregateCharSequence;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class Text
            implements IText, IEngineTemplateHandlerEvent {

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private CharSequence text;

    private int length;

    private Boolean whitespace;
    private Boolean inlineable;

    private String templateName;
    private int line;
    private int col;



    /*
     * Object of this class can contain their data both as a String and as a char[] buffer. The buffer will only
     * be used internally to the 'engine' package, in order to avoid the creation of unnecessary String objects
     * (most times the parsing buffer itself will be used). Computation of the String form will be performed lazily
     * and only if specifically required.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    Text(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



    // Meant to be called only from the model factory
    Text(final ITextRepository textRepository, final String text) {
        super();
        this.textRepository = textRepository;
        setText(text);
    }



    public String getText() {

        // Either we have a non-null text, or a non-null buffer specification (char[],offset,len)

        if (this.text == null) {
            this.text = this.textRepository.getText(this.buffer, this.offset, this.length);
        }

        if (!(this.text instanceof String)) {
            // This is to ensure we only compute once the String from whatever implementation of
            // CharSequence we are using
            this.text = this.text.toString();
        }

        return this.text.toString();

    }




    public int length() {
        return this.length;
    }


    public char charAt(final int index) {

        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead

        if (this.buffer != null) {
            return this.buffer[this.offset + index];
        }

        return this.text.charAt(index);

    }


    public boolean isWhitespace() {

        if (this.whitespace == null) {
            if (this.buffer != null) {
                this.whitespace = Boolean.valueOf(computeIsWhitespace(this.buffer, this.offset, this.length));
            } else {
                this.whitespace = Boolean.valueOf(computeIsWhitespace(this.text));
            }
        }

        return this.whitespace.booleanValue();
    }


    public boolean contains(final CharSequence subsequence) {

        if (this.inlineable == null) {
            if (isWhitespace()) {
                this.inlineable = Boolean.FALSE;
            } else {
                if (this.buffer != null) {
                    this.inlineable = Boolean.valueOf(computeIsInlineable(this.buffer, this.offset, this.length));
                } else {
                    this.inlineable = Boolean.valueOf(computeIsInlineable(this.text));
                }
            }
        }

        return this.inlineable.booleanValue();
    }


    public CharSequence subSequence(final int start, final int end) {

        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead

        if (this.text != null) {
            return this.text.subSequence(start, end);
        }

        int subLen = end - start;
        if (start == 0 && subLen == this.length) {
            return getText();
        }
        return this.textRepository.getText(this.buffer, this.offset + start, subLen);

    }


    void reset(final char[] buffer,
               final int offset, final int len,
               final String templateName, final int line, final int col) {

        this.buffer = buffer;
        this.offset = offset;

        this.length = len;

        this.text = null;

        this.whitespace = null;

        this.templateName = templateName;
        this.line = line;
        this.col = col;

    }




    public void setText(final CharSequence text) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        this.text = text;

        this.length = text.length();

        this.buffer = null;
        this.offset = -1;

        this.whitespace = null;

        this.templateName = null;
        this.line = -1;
        this.col = -1;

    }




    public boolean hasLocation() {
        return (this.templateName != null && this.line != -1 && this.col != -1);
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }




    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        if (this.buffer != null) {
            // Using the 'buffer write' is the first option because it is normally faster and requires less
            // resources than writing String objects
            writer.write(this.buffer, this.offset, this.length);
        } else {
            if (this.text instanceof AggregateCharSequence) {
                // In the special case we are using an AggregateCharSequence, we will avoid creating a String
                // for the whole content
                ((AggregateCharSequence)this.text).write(writer);
            } else {
                writer.write(this.text.toString());
            }
        }
    }



    public String toString() {
        return getText();
    }



    public Text cloneNode() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final Text clone = new Text(this.textRepository);
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final Text original) {

        this.buffer = null;
        this.offset = -1;
        this.text = original.getText(); // Need to call the method in order to force computing -- no buffer cloning!
        this.length = this.text.length();
        this.whitespace = original.whitespace;
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;

    }


    // Meant to be called only from within the engine
    static Text asEngineText(
            final IEngineConfiguration configuration, final IText text, final boolean cloneAlways) {

        if (text instanceof Text) {
            if (cloneAlways) {
                return ((Text) text).cloneNode();
            }
            return (Text) text;
        }

        final Text newInstance = new Text(configuration.getTextRepository());
        newInstance.buffer = null;
        newInstance.offset = -1;
        newInstance.text = text.getText();
        newInstance.length = newInstance.text.length();
        newInstance.whitespace = null;
        newInstance.templateName = text.getTemplateName();
        newInstance.line = text.getLine();
        newInstance.col = text.getCol();
        return newInstance;

    }




    private static boolean computeIsWhitespace(final CharSequence text) {
        int n = text.length();
        if (n == 0) {
            return true;
        }
        final char c0 = text.charAt(0);
        if ((c0 >= 'a' && c0 <= 'z') || (c0 >= 'A' && c0 <= 'Z')) {
            // Fail fast, by quickly checking first char without executing Character.isWhitespace(...)
            return false;
        }
        while (n-- != 0) {
            if (!Character.isWhitespace(text.charAt(n))) {
                return false;
            }
        }
        return true;
    }


    private static boolean computeIsWhitespace(final char[] buffer, final int off, final int len) {
        int n = len;
        if (n == 0) {
            // empty texts are NOT whitespace
            return false;
        }
        final char c0 = buffer[off];
        if ((c0 >= 'a' && c0 <= 'z') || (c0 >= 'A' && c0 <= 'Z')) {
            // Fail fast, by quickly checking first char without executing Character.isWhitespace(...)
            return false;
        }
        while (n-- != 0) {
            if (!Character.isWhitespace(buffer[off + n])) {
                return false;
            }
        }
        return true;
    }




    private static boolean contains(final CharSequence text, final CharSequence subsequence) {
        if (text instanceof String) {
            return ((String)text).contains(subsequence);
        }
        // TODO Use TextUtil here (after converting it to CharSequence)?
        int n = text.length();
        if (n == 0) {
            return false;
        }
        char c;
        while (n-- != 0) {
            c = text.charAt(n);
            if (c == ']' && n > 0) {
                c = text.charAt(n - 1);
                if (c == ']' || c == ')') {
                    // There probably is some kind of [[...]] or [(...)] inlined expression
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean computeIsInlineable(final char[] buffer, final int off, final int len, final CharSequence subsequence) {
        int n = len;
        if (n == 0) {
            return false;
        }
        char c;
        while (n-- != 0) {
            c = buffer[off + n];
            if (c == ']' && n > 0) {
                c = buffer[off + (n - 1)];
                if (c == ']' || c == ')') {
                    // There probably is some kind of [[...]] or [(...)] inlined expression
                    return true;
                }
            }
        }
        return false;
    }


}
