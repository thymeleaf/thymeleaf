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
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IText;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.text.IWritableCharSequence;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class Text extends AbstractTemplateEvent implements IText, IEngineTemplateEvent {

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private CharSequence text;

    private int length = -1;

    private Boolean whitespace;
    private Boolean inlineable;



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
    Text(final ITextRepository textRepository, final CharSequence text) {
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

        // this.text is a String
        return this.text.toString();

    }




    public int length() {
        if (this.length == -1 && this.text != null) {
            this.length = this.text.length();
        }
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

        super.resetTemplateEvent(templateName, line, col);

        this.buffer = buffer;
        this.offset = offset;

        this.length = len;

        this.text = null;

        this.whitespace = null;
        this.inlineable = null;

    }




    public void setText(final CharSequence text) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        super.resetTemplateEvent(null, -1, -1);

        this.text = text;

        // we will leave this to be computed lazily so that we favor the use of lazy CharSequence implementations
        this.length = -1;

        this.buffer = null;
        this.offset = -1;

        this.whitespace = null;
        this.inlineable = null;

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        if (this.buffer != null) {
            // Using the 'buffer write' is the first option because it is normally faster and requires less
            // resources than writing String objects
            writer.write(this.buffer, this.offset, this.length);
        } else {
            if (this.text instanceof IWritableCharSequence) {
                // In the special case we are using a writable CharSequence, we will avoid creating a String
                // for the whole content
                ((IWritableCharSequence) this.text).write(writer);
            } else {
                writer.write(this.text.toString());
            }
        }
    }



    public Text cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final Text clone = new Text(this.textRepository);
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final Text original) {

        super.resetAsCloneOfTemplateEvent(original);
        this.buffer = null;
        this.offset = -1;
        this.text = (original.text == null? original.getText() : original.text); // No buffer cloning!
        this.length = original.length;
        this.whitespace = original.whitespace;
        this.inlineable = original.inlineable;

    }


    // Meant to be called only from within the engine
    static Text asEngineText(
            final IEngineConfiguration configuration, final IText text, final boolean cloneAlways) {

        if (text instanceof Text) {
            if (cloneAlways) {
                return ((Text) text).cloneEvent();
            }
            return (Text) text;
        }

        final Text newInstance = new Text(configuration.getTextRepository());
        newInstance.buffer = null;
        newInstance.offset = -1;
        newInstance.text = text.getText();
        newInstance.length = newInstance.text.length();
        newInstance.whitespace = null;
        newInstance.inlineable = null;
        newInstance.resetTemplateEvent(text.getTemplateName(), text.getLine(), text.getCol());
        return newInstance;

    }





    boolean isWhitespace() {
        if (this.whitespace == null) {
            computeContentFlags();
        }
        return this.whitespace.booleanValue();
    }


    boolean isInlineable() {
        if (this.inlineable == null) {
            computeContentFlags();
        }
        return this.inlineable.booleanValue();
    }



    void computeContentFlags() {
        if (this.buffer != null) {
            computeContentFlags(this.buffer, this.offset, this.length);
        } else {
            computeContentFlags(this.text);
        }
    }



    private void computeContentFlags(final CharSequence text) {

        this.whitespace = null;
        this.inlineable = null;

        int n = length(); // This will leave length computed in case it's needed afterwards

        if (n == 0) {
            this.whitespace = Boolean.FALSE; // empty texts are NOT whitespace
            this.inlineable = Boolean.FALSE;
            return;
        }

        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != 0 && this.inlineable == null) {
            c1 = text.charAt(n);
            if (c1 != ' ' && c1 != '\n') { // shortcut - most characters in many templates are just whitespace.
                if (this.whitespace == null && !Character.isWhitespace(c1)) {
                    this.whitespace = Boolean.FALSE;
                }
                if (c1 == ']' && c0 == ']') {
                    inline = 1;
                } else if (c1 == ')' && c0 == ']') {
                    inline = 2;
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    this.inlineable = Boolean.TRUE;
                } else if (inline == 2 && c1 == '[' && c0 == '(') {
                    this.inlineable = Boolean.TRUE;
                }
            }
            c0 = c1;
        }

        // Not having the contrary been proved, apply the defaults
        this.whitespace = (this.whitespace == null? Boolean.TRUE : this.whitespace);
        this.inlineable = (this.inlineable == null? Boolean.FALSE : this.inlineable);

    }


    private void computeContentFlags(final char[] buffer, final int off, final int len) {

        this.whitespace = null;
        this.inlineable = null;

        int n = off + len;

        if (len == 0) {
            this.whitespace = Boolean.FALSE; // empty texts are NOT whitespace
            this.inlineable = Boolean.FALSE;
            return;
        }

        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != off && this.inlineable == null) {
            c1 = buffer[n];
            if (c1 != ' ' && c1 != '\n') { // shortcut - most characters in many templates are just whitespace.
                if (this.whitespace == null && !Character.isWhitespace(c1)) {
                    this.whitespace = Boolean.FALSE;
                }
                if (c1 == ']' && c0 == ']') {
                    inline = 1;
                } else if (c1 == ')' && c0 == ']') {
                    inline = 2;
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    this.inlineable = Boolean.TRUE;
                } else if (inline == 2 && c1 == '[' && c0 == '(') {
                    this.inlineable = Boolean.TRUE;
                }
            }
            c0 = c1;
        }

        // Not having the contrary been proved, apply the defaults
        this.whitespace = (this.whitespace == null? Boolean.TRUE : this.whitespace);
        this.inlineable = (this.inlineable == null? Boolean.FALSE : this.inlineable);

    }





    @Override
    public final String toString() {
        return getText();
    }


}
