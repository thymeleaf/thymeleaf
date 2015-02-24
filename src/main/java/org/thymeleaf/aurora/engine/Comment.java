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
public final class Comment implements Node {

    private static final char[] COMMENT_PREFIX = "<!--".toCharArray();
    private static final char[] COMMENT_SUFFIX = "-->".toCharArray();

    private char[] internalBuffer = null;

    private char[] buffer;
    private int contentOffset;
    private int contentLen;
    private int outerOffset;
    private int outerLen;

    private String comment;
    private String content;

    private ITextRepository textRepository;

    private int line;
    private int col;


    /*
     * Object of this class will use as "single source of truth" the buffer/contentOffset/contentLen/outerOffset/outerLen
     * set of properties, which must always contain the currently valid shape of the Comment that is being
     * represented.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     *
     * String artifacts like the 'comment' or 'content' properties should be computed lazily in order to avoid
     * unnecessary conversions to String which would use more memory than needed.
     *
     * The internalBuffer is a reusable structure that will be used in order to allow processors to set String or
     * partial (content-only) values for the Comment -- the complete Comment structure will be constructed
     * using this internal buffer structure.
     */


    // Meant to be called only from within the engine
    Comment(
            final ITextRepository textRepository,
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        super();

        if (textRepository == null) {
            throw new IllegalArgumentException("Text Repository cannot be null");
        }

        this.textRepository = textRepository;

        setComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

    }



    public Comment(final String content) {
        super();
        setContent(content);
    }




    char[] getBuffer() {
        return this.buffer;
    }

    int getContentOffset() {
        return this.contentOffset;
    }

    int getContentLen() {
        return this.contentLen;
    }

    int getOuterOffset() {
        return this.outerOffset;
    }

    int getOuterLen() {
        return this.outerLen;
    }




    public String getComment() {

        if (this.comment == null) {
            this.comment =
                    (this.textRepository != null?
                        this.textRepository.getText(this.buffer, this.outerOffset, this.outerLen) :
                        new String(this.buffer, this.outerOffset, this.outerLen));
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





    void setComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        // This is only meant to be called internally, so no need to perform a lot of checks on the input validity

        // No need to reset the internalBuffer as the intention is precisely to reuse it
        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;

        this.comment = null;
        this.content = null;

        this.line = line;
        this.col = col;

    }




    public void setContent(final String content) {

        if (content == null) {
            throw new IllegalArgumentException("Comment content cannot be null");
        }

        // This only sets the content, therefore we need to use the internal buffer in order to
        // construct a valid (prefixed + suffixed) Comment

        final int contentLen = content.length();
        final int commentLen = COMMENT_PREFIX.length + contentLen + COMMENT_SUFFIX.length;

        if (this.internalBuffer == null || this.internalBuffer.length < commentLen) {
            // We need to create a new internal buffer (note how we try to reuse the internal one if possible)
            this.internalBuffer = new char[commentLen];
        }

        System.arraycopy(COMMENT_PREFIX, 0, this.internalBuffer, 0, COMMENT_PREFIX.length);
        content.getChars(0, contentLen, this.internalBuffer, COMMENT_PREFIX.length);
        System.arraycopy(COMMENT_SUFFIX, 0, this.internalBuffer, (COMMENT_PREFIX.length + contentLen), COMMENT_SUFFIX.length);

        this.buffer = this.internalBuffer;
        this.contentOffset = COMMENT_PREFIX.length;
        this.contentLen = contentLen;
        this.outerOffset = 0;
        this.outerLen = commentLen;

        this.comment = null;
        this.content = content;

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
        MarkupOutput.writeComment(writer, this.buffer, this.outerOffset, this.outerLen);
    }



    public String toString() {
        return getComment();
    }



}
