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
package org.thymeleaf.aurora.processor.cdatasection;

import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.text.ITextRepository;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class CDATASection {

    private char[] buffer;
    private int contentOffset;
    private int contentLen;
    private int outerOffset;
    private int outerLen;

    private String cdataSection;
    private String content;

    private int line;
    private int col;

    private ITextRepository textRepository;


    public CDATASection() {
        super();
    }


    public CDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        this(null, buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);

    }


    public CDATASection(
            final ITextRepository textRepository,
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {

        super();

        this.textRepository = textRepository;

        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;
        this.line = line;
        this.col = col;

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

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }



    public String getCdataSection() {
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
            this.content =
                    (this.textRepository != null?
                            this.textRepository.getText(this.buffer, this.contentOffset, this.contentLen) :
                            new String(this.buffer, this.contentOffset, this.contentLen));
        }
        return this.content;
    }



    public void setCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen) {
        this.buffer = buffer;
        this.con
    }


    public void setCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen) {
        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;
    }


    void setCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;
        this.line = line;
        this.col = col;
    }





    public void setCdataSection(final String cdataSection) {
        this.cdataSection = cdataSection;
    }

    public void setContent(final String content) {
        this.content = content;
    }


    public ITextRepository getTextRepository() {
        return this.textRepository;
    }

    public void setTextRepository(final ITextRepository textRepository) {
        this.textRepository = textRepository;
    }

}
