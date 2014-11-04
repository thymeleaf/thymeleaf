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

import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.aurora.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class CDATASection {

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
    CDATASection(final ITextRepository textRepository) {

        super();

        if (textRepository == null) {
            throw new IllegalArgumentException("Text Repository cannot be null");
        }

        this.textRepository = textRepository;

        this.buffer = null;
        this.contentOffset = -1;
        this.contentLen = -1;
        this.outerOffset = -1;
        this.outerLen = -1;

        this.cdataSection = null;
        this.content = null;

    }



    public CDATASection(final String content) {
        super();
        initializeFromContent(content);
    }


    public CDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen) {
        super();
        initializeFromCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen);
    }



    // Meant to be called only from within the engine
    void setTextRepository(final ITextRepository textRepository) {
        if (textRepository == null) {
            throw new IllegalArgumentException("Text Repository cannot be null");
        }
        this.textRepository = textRepository;
    }


    public char[] getBuffer() {
        return this.buffer;
    }

    public int getContentOffset() {
        return this.contentOffset;
    }

    public int getContentLen() {
        return this.contentLen;
    }

    public int getOuterOffset() {
        return this.outerOffset;
    }

    public int getOuterLen() {
        return this.outerLen;
    }



    public String getCDATASection() {

        if (this.buffer == null) {
            // Should never happen, but just in case
            return null;
        }

        if (this.cdataSection == null) {
            this.cdataSection =
                    (this.textRepository != null?
                        this.textRepository.getText(this.buffer, this.outerOffset, this.outerLen) :
                        new String(this.buffer, this.outerOffset, this.outerLen));
        }

        return this.cdataSection;

    }


    public String getContent() {

        if (this.buffer == null) {
            // Should never happen, but just in case
            return null;
        }

        if (this.content == null) {

            if (this.cdataSection == null) {

                this.content =
                        (this.textRepository != null ?
                                this.textRepository.getText(this.buffer, this.contentOffset, this.contentLen) :
                                new String(this.buffer, this.contentOffset, this.contentLen));

            } else {
                // We already have a String for the entire CDATA Section, so we will save some bytes in memory if
                // we just return a substring of it (substrings don't duplicate the underlying char[])

                this.content =
                        this.cdataSection.substring(CDATA_PREFIX.length, (this.cdataSection.length() - CDATA_SUFFIX.length));
            }

        }

        return this.content;

    }





    public void setCDATASection(
            final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset, final int outerLen) {
        initializeFromCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen);
    }


    public void setCDATASection(final String cdataSection) {
        initializeFromCDATASection(cdataSection);
    }




    public void setContent(final char[] buffer, final int contentOffset, final int contentLen) {
        initializeFromContent(buffer, contentOffset, contentLen);
    }


    public void setContent(final String content) {
        initializeFromContent(content);
    }





    private void initializeFromContent(final String content) {

        if (content == null) {
            throw new IllegalArgumentException("CDATA Section content cannot be null");
        }

        // This only sets the content, therefore we need to use the internal buffer in order to
        // construct a valid (prefixed + suffixed) CDATA Section

        final int contentLen = content.length();
        final int cdataSectionLen = CDATA_PREFIX.length + contentLen + CDATA_SUFFIX.length;

        if (this.internalBuffer == null || this.internalBuffer.length < cdataSectionLen) {
            // We need to create a new internal buffer (note how we try to reuse the internal one if possible)
            this.internalBuffer = new char[cdataSectionLen];
            // We only need to add the prefix if this is new. If not, the existing one will do. If possible, respect the existing one (preserving case)
            if (this.buffer != null) {
                System.arraycopy(this.buffer, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
            } else {
                System.arraycopy(CDATA_PREFIX, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
            }
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

    }


    private void initializeFromContent(final char[] buffer, final int contentOffset, final int contentLen) {

        if (buffer == null) {
            throw new IllegalArgumentException("CDATA Section content buffer cannot be null");
        }
        if (contentOffset < 0 || contentLen < 0) {
            throw new IllegalArgumentException("CDATA Section content offset and len must be >= 0");
        }
        if (contentOffset + contentLen > buffer.length) {
            throw new IllegalArgumentException("CDATA Section was specified with invalid bounds. Buffer is not long enough");
        }

        // This only sets the content, therefore we need to use the internal buffer in order to
        // construct a valid (prefixed + suffixed) CDATA Section

        final int cdataSectionLen = CDATA_PREFIX.length + contentLen + CDATA_SUFFIX.length;

        if (this.internalBuffer == null || this.internalBuffer.length < cdataSectionLen) {
            // We need to create a new internal buffer (note how we try to reuse the internal one if possible)
            this.internalBuffer = new char[cdataSectionLen];
            // We only need to add the prefix if this is new. If not, the existing one will do. If possible, respect the existing one (preservingcase)
            if (this.buffer != null) {
                System.arraycopy(this.buffer, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
            } else {
                System.arraycopy(CDATA_PREFIX, 0, this.internalBuffer, 0, CDATA_PREFIX.length);
            }
        }

        System.arraycopy(buffer, contentOffset, this.internalBuffer, CDATA_PREFIX.length, contentLen);
        System.arraycopy(CDATA_SUFFIX, 0, this.internalBuffer, (CDATA_PREFIX.length + contentLen), CDATA_SUFFIX.length);

        this.buffer = this.internalBuffer;
        this.contentOffset = CDATA_PREFIX.length;
        this.contentLen = contentLen;
        this.outerOffset = 0;
        this.outerLen = cdataSectionLen;

        this.cdataSection = null;
        this.content = null;

    }


    private void initializeFromCDATASection(final String cdataSection) {

        if (cdataSection == null) {
            throw new IllegalArgumentException("CDATA Section cannot be null");
        }

        // We need to check that this is a valid CDATA section (note only the prefix needs to be case insensitive)
        if (!TextUtil.startsWith(false, cdataSection, CDATA_PREFIX) || !TextUtil.endsWith(true, cdataSection, CDATA_SUFFIX)) {
            throw new IllegalArgumentException(
                    "CDATA Section must start with '" + (new String(CDATA_PREFIX)) + "' and end with '" + (new String(CDATA_PREFIX)) + "'");
        }

        final int cdataSectionLen = cdataSection.length();

        if (this.internalBuffer == null || this.internalBuffer.length < cdataSectionLen) {
            // We need to create a new internal buffer (note how we try to reuse the internal one if possible)
            this.internalBuffer = new char[cdataSectionLen];
        }

        cdataSection.getChars(0, cdataSectionLen, this.internalBuffer, 0);

        this.buffer = this.internalBuffer;
        this.contentOffset = CDATA_PREFIX.length;
        this.contentLen = cdataSectionLen - (CDATA_PREFIX.length + CDATA_SUFFIX.length);
        this.outerOffset = 0;
        this.outerLen = cdataSectionLen;

        this.cdataSection = cdataSection;
        this.content = null;

    }


    private void initializeFromCDATASection(
            final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset, final int outerLen) {

        if (buffer == null) {
            throw new IllegalArgumentException("CDATA Section buffer cannot be null");
        }
        if (contentOffset < 0 || contentLen < 0 || outerOffset < 0 || outerLen < 0) {
            throw new IllegalArgumentException("CDATA Section offsets and lens must be >= 0");
        }
        if (outerOffset + outerLen > buffer.length) {
            throw new IllegalArgumentException("CDATA Section was specified with invalid bounds. Buffer is not long enough");
        }
        if (contentOffset < outerOffset || contentLen > outerLen || contentOffset + contentLen > outerOffset + outerLen) {
            throw new IllegalArgumentException("CDATA Section content must be contained within the 'outer' limits");
        }

        // We need to check that this is a valid CDATA section (note only the prefix needs to be case insensitive)
        if (!TextUtil.startsWith(false, buffer, outerOffset, outerLen, CDATA_PREFIX, 0, CDATA_PREFIX.length) ||
                !TextUtil.endsWith(true, buffer, outerOffset, outerLen, CDATA_SUFFIX, 0, CDATA_SUFFIX.length)) {
            throw new IllegalArgumentException(
                    "CDATA Section must start with '" + (new String(CDATA_PREFIX)) + "' and end with '" + (new String(CDATA_PREFIX)) + "'");
        }

        // Set all the data. No need to reset the internalBuffer as the intention is precisely to reuse it
        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;

        this.cdataSection = null;
        this.content = null;

    }


}
