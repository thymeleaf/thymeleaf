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

import org.thymeleaf.aurora.model.ICDATASection;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class CDATASection implements ICDATASection {

    private static final char[] CDATA_PREFIX = "<![CDATA[".toCharArray();
    private static final char[] CDATA_SUFFIX = "]]>".toCharArray();

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private String cdataSection;
    private String content;

    private int cdataSectionLength;
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
    CDATASection(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



    CDATASection(final ITextRepository textRepository, final String content) {
        super();
        this.textRepository = textRepository;
        setContent(content);
    }






    public String getCDATASection() {

        // Either we have a non-null cdata and/or content, or a non-null buffer specification (char[],offset,len)

        if (this.cdataSection == null) {
            if (this.content == null) {
                this.cdataSection = this.textRepository.getText(this.buffer, this.offset, this.cdataSectionLength);
            } else {
                final StringBuilder strBuilder =
                        new StringBuilder(this.contentLength + CDATA_PREFIX.length + CDATA_SUFFIX.length);
                strBuilder.append(CDATA_PREFIX);
                strBuilder.append(this.content);
                strBuilder.append(CDATA_SUFFIX);
                this.cdataSection = this.textRepository.getText(strBuilder);
            }
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




    public int length() {
        return this.cdataSectionLength;
    }


    public char charAt(final int index) {

        if (index < 0 || index >= this.cdataSectionLength) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }

        if (this.buffer != null) {
            return this.buffer[this.offset + index];
        }

        if (this.cdataSection != null) {
            return this.cdataSection.charAt(index);
        }
        return getCDATASection().charAt(index); // Force the computation of the cdataSection property

    }




    void setCDATASection(
            final char[] buffer,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        // This is only meant to be called internally, so no need to perform a lot of checks on the input validity

        this.buffer = buffer;
        this.offset = outerOffset;

        this.cdataSectionLength = outerLen;
        this.contentLength = this.cdataSectionLength - CDATA_PREFIX.length - CDATA_SUFFIX.length;

        this.cdataSection = null;
        this.content = null;

        this.line = line;
        this.col = col;

    }




    public void setContent(final String content) {

        if (content == null) {
            throw new IllegalArgumentException("CDATA Section content cannot be null");
        }

        this.content = content;
        this.cdataSection = null;

        this.contentLength = content.length();
        this.cdataSectionLength = CDATA_PREFIX.length + this.contentLength + CDATA_SUFFIX.length;

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





    // Meant to be called only from within the engine
    void setFromCDATASection(final ICDATASection cdataSection) {

        this.buffer = null;
        this.offset = -1;
        this.cdataSection = cdataSection.getCDATASection();
        this.content = cdataSection.getContent();
        this.cdataSectionLength = this.cdataSection.length();
        this.contentLength = this.content.length();
        this.line = cdataSection.getLine();
        this.col = cdataSection.getCol();

    }




    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        if (this.buffer != null) {
            // Using the 'buffer write' is the first option because it is normally faster and requires less
            // resources than writing String objects
            writer.write(this.buffer, this.offset, this.cdataSectionLength);
        } else if (this.cdataSection != null) {
            writer.write(this.cdataSection);
        } else { // this.content != null
            writer.write(CDATA_PREFIX);
            writer.write(this.content);
            writer.write(CDATA_SUFFIX);
        }
    }



    public String toString() {
        return getCDATASection();
    }





    public CDATASection cloneNode() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final CDATASection clone = new CDATASection(this.textRepository);
        clone.buffer = null;
        clone.offset = -1;
        clone.cdataSection = getCDATASection();
        clone.content = getContent();
        clone.cdataSectionLength = this.cdataSectionLength;
        clone.contentLength = this.contentLength;
        clone.line = this.line;
        clone.col = this.col;
        return clone;
    }


}
