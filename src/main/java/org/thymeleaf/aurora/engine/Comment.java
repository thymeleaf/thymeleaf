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
public final class Comment implements IComment {

    private static final char[] COMMENT_PREFIX = "<!--".toCharArray();
    private static final char[] COMMENT_SUFFIX = "-->".toCharArray();

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private String comment;
    private String content;

    private int commentLength;
    private int contentLength;

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
    Comment(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



    // Meant to be called only from the model factory
    Comment(final ITextRepository textRepository, final String content) {
        super();
        this.textRepository = textRepository;
        setContent(content);
    }






    public String getComment() {

        // Either we have a non-null comment and/or content, or a non-null buffer specification (char[],offset,len)

        if (this.comment == null) {
            if (this.content == null) {
                this.comment = this.textRepository.getText(this.buffer, this.offset, this.commentLength);
            } else {
                final StringBuilder strBuilder =
                        new StringBuilder(this.contentLength + COMMENT_PREFIX.length + COMMENT_SUFFIX.length);
                strBuilder.append(COMMENT_PREFIX);
                strBuilder.append(this.content);
                strBuilder.append(COMMENT_SUFFIX);
                this.comment = this.textRepository.getText(strBuilder);
            }
        }

        return this.comment;

    }


    public String getContent() {

        if (this.content == null) {

            // By calling getComment() we will compute a String for the entire Comment, so we will
            // save some bytes in memory if we just return a substring of it (substrings don't duplicate the
            // underlying char[])

            this.content =
                    getComment().substring(COMMENT_PREFIX.length, (this.comment.length() - COMMENT_SUFFIX.length));

        }

        return this.content;

    }




    public int length() {
        return this.commentLength;
    }


    public char charAt(final int index) {

        if (index < 0 || index >= this.commentLength) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }

        if (this.buffer != null) {
            return this.buffer[this.offset + index];
        }

        if (this.comment != null) {
            return this.comment.charAt(index);
        }
        return getComment().charAt(index); // Force the computation of the comment property

    }





    void setComment(
            final char[] buffer,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        // This is only meant to be called internally, so no need to perform a lot of checks on the input validity

        this.buffer = buffer;
        this.offset = outerOffset;

        this.commentLength = outerLen;
        this.contentLength = this.commentLength - COMMENT_PREFIX.length - COMMENT_SUFFIX.length;

        this.comment = null;
        this.content = null;

        this.line = line;
        this.col = col;

    }




    public void setContent(final String content) {

        if (content == null) {
            throw new IllegalArgumentException("Comment content cannot be null");
        }

        this.content = content;
        this.comment = null;

        this.contentLength = content.length();
        this.commentLength = COMMENT_PREFIX.length + this.contentLength + COMMENT_SUFFIX.length;

        this.buffer = null;
        this.offset = -1;

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
            writer.write(this.buffer, this.offset, this.commentLength);
        } else if (this.comment != null) {
            writer.write(this.comment);
        } else { // this.content != null
            writer.write(COMMENT_PREFIX);
            writer.write(this.content);
            writer.write(COMMENT_SUFFIX);
        }
    }



    public String toString() {
        return getComment();
    }





    public Comment cloneNode() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final Comment clone = new Comment(this.textRepository);
        clone.buffer = null;
        clone.offset = -1;
        clone.comment = getComment();
        clone.content = getContent();
        clone.commentLength = this.commentLength;
        clone.contentLength = this.contentLength;
        clone.line = this.line;
        clone.col = this.col;
        return clone;
    }



}
