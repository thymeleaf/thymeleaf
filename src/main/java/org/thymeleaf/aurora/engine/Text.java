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
public final class Text implements IText {

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private String text;

    private int length;

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


    // Meant to be called only from within the engine
    Text(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



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

        return this.text;

    }




    public int length() {
        return this.length;
    }


    public char charAt(final int index) {

        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }

        if (this.buffer != null) {
            return this.buffer[this.offset + index];
        }

        return this.text.charAt(index);

    }




    void setText(final char[] buffer,
                 final int offset, final int len,
                 final int line, final int col) {

        this.buffer = buffer;
        this.offset = offset;

        this.length = len;

        this.text = null;

        this.line = line;
        this.col = col;

    }




    public void setText(final String text) {

        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        this.text = text;

        this.length = text.length();

        this.buffer = null;
        this.offset = 0;

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
        if (this.buffer != null) {
            // Using the 'buffer write' is the first option because it is normally faster and requires less
            // resources than writing String objects
            writer.write(this.buffer, this.offset, this.length);
        } else {
            writer.write(this.text);
        }
    }



    public String toString() {
        return getText();
    }



    public Text cloneNode() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final Text clone = new Text(this.textRepository);
        clone.buffer = null;
        clone.offset = -1;
        clone.text = getText();
        clone.length = this.length;
        clone.line = this.line;
        clone.col = this.col;
        return clone;
    }

}
