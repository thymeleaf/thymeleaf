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
public final class CDATASection implements Node {

    private static final char[] CDATA_PREFIX = "<![CDATA[".toCharArray();
    private static final char[] CDATA_SUFFIX = "]]>".toCharArray();

    private char[] internalBuffer = null;

    private char[] buffer;
    private int contentOffset;
    private int contentLen;
    private int outerOffset;
    private int outerLen;

    private String cdataSection;
    private String content;

    private ITextRepository textRepository;

    private int line;
    private int col;


    /*
     * Object of this class will use as "single source of truth" the buffer/contentOffset/contentLen/outerOffset/outerLen
     * set of properties, which must always contain the currently valid shape of the CDATA section that is being
     * represented.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     *
     * String artifacts like the 'cdataSection' or 'content' properties should be computed lazily in order to avoid
     * unnecessary conversions to String which would use more memory than needed.
     *
     * The internalBuffer is a reusable structure that will be used in order to allow processors to set String or
     * partial (content-only) values for the CDATA Section -- the complete CDATA section structure will be constructed
     * using this internal buffer structure.
     */


    // Meant to be called only from within the engine
    CDATASection(
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

        setCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

    }



    public CDATASection(final String content) {
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




    public String getCDATASection() {

        if (this.cdataSection == null) {
            this.cdataSection =
                    (this.textRepository != null?
                        this.textRepository.getText(this.buffer, this.outerOffset, this.outerLen) :
                        new String(this.buffer, this.outerOffset, this.outerLen));
        }

        return this.cdataSection;

    }


    public String getContent() {

        if (this.content == null) {

            // By calling getCDATASection() we will compute a String for the entire CDATA Section, so we will
            // save some bytes in memory if we just return a substring of it (substrings don't duplicate the
            // underlying char[])

            this.content =
                    getCDATASection().substring(CDATA_PREFIX.length, (this.cdataSection.length() - CDATA_SUFFIX.length));

        }

        return this.content;

    }




    void setCDATASection(
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

        this.cdataSection = null;
        this.content = null;

        this.line = line;
        this.col = col;

    }




    public void setContent(final String content) {

        if (content == null) {
            throw new IllegalArgumentException("CDATA Section content cannot be null");
        }

        // This only sets the content, therefore we need to use the internal buffer in order to
        // construct a valid (prefixed + suffixed) CDATA Section

        final int contentLen = content.length();
        final int cdataSectionLen = CDATA_PREFIX.length + contentLen + CDATA_SUFFIX.length;

        if (this.internalBuffer == null || this.internalBuffer.length < cdataSectionLen) {
            // We need to create a new internal buffer (note how we try to reuse the existing one if possible)
            this.internalBuffer = new char[cdataSectionLen];
        }

        // We only need to add the prefix if this is new. If not, the existing one will do. If possible, respect the existing one (preserving case)
        if (this.buffer != null) {
            if (this.buffer != this.internalBuffer) {
                System.arraycopy(this.buffer, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
            } // else don't do anything, otherwise we'd overwrite the prefix already existing
        } else {
            System.arraycopy(CDATA_PREFIX, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
        }

        content.getChars(0, contentLen, this.internalBuffer, CDATA_PREFIX.length);
        System.arraycopy(CDATA_SUFFIX, 0, this.internalBuffer, (CDATA_PREFIX.length + contentLen), CDATA_SUFFIX.length);

        this.buffer = this.internalBuffer;
        this.contentOffset = CDATA_PREFIX.length;
        this.contentLen = contentLen;
        this.outerOffset = 0;
        this.outerLen = cdataSectionLen;

        this.cdataSection = null;
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
        MarkupOutput.writeCDATASection(writer, this.buffer, this.outerOffset, this.outerLen);
    }



    public String toString() {
        return getCDATASection();
    }



}
