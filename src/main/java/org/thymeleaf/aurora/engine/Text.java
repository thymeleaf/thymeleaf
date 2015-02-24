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
package org.thymeleaf.aurora.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class Text implements Node {

    private char[] internalBuffer = null;

    private char[] buffer;
    private int offset;
    private int len;

    private String text;

    private ITextRepository textRepository;

    private int line;
    private int col;


    /*
     * Object of this class will use as "single source of truth" the buffer/offset/len
     * set of properties, which must always contain the currently valid shape of the Text that is being
     * represented.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     *
     * The String 'text' property should be computed lazily in order to avoid unnecessary conversions to String which
     * would use more memory than needed.
     *
     * The internalBuffer is a reusable structure that will be used in order to allow processors to set String
     * values for the Text.
     */


    // Meant to be called only from within the engine
    Text(final ITextRepository textRepository,
         final char[] buffer,
         final int offset, final int len,
         final int line, final int col) {

        super();

        if (textRepository == null) {
            throw new IllegalArgumentException("Text Repository cannot be null");
        }

        this.textRepository = textRepository;

        setText(buffer, offset, len, line, col);

    }



    public Text(final String text) {
        super();
        setText(text);
    }




    char[] getBuffer() {
        return this.buffer;
    }

    int getOffset() {
        return this.offset;
    }

    int getLen() {
        return this.len;
    }




    public String getText() {

        if (this.text == null) {
            this.text =
                    (this.textRepository != null?
                        this.textRepository.getText(this.buffer, this.offset, this.len) :
                        new String(this.buffer, this.offset, this.len));
        }

        return this.text;

    }




    public int length() {
        return this.len;
    }


    public char charAt(final int index) {

        if (index < 0 || index >= this.len) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return this.buffer[this.offset + index];

    }




    void setText(final char[] buffer,
                 final int offset, final int len,
                 final int line, final int col) {

        this.buffer = buffer;
        this.offset = offset;
        this.len = len;

        this.text = null;

        this.line = line;
        this.col = col;

    }




    public void setText(final String text) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        final int textLen = text.length();

        if (this.internalBuffer == null || this.internalBuffer.length < textLen) {
            // We need to create a new internal buffer (note how we try to reuse the internal one if possible)
            this.internalBuffer = new char[textLen];
        }

        text.getChars(0, textLen, this.internalBuffer, 0);

        this.buffer = this.internalBuffer;
        this.offset = 0;
        this.len = textLen;

        this.text = text;

        this.line = -1;
        this.col = -1;

    }




    public boolean hasLocation() {
        return (this.line != -1 && this.col != -1);
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }





    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        MarkupOutput.writeText(writer, this.buffer, this.offset, this.len);
    }



    public String toString() {
        return getText();
    }



}
