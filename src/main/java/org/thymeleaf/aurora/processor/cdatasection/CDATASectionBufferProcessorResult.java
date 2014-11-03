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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class CDATASectionBufferProcessorResult {

    private final char[] buffer;
    private final int contentOffset;
    private final int contentLen;
    private final int outerOffset;
    private final int outerLen;


    public CDATASectionBufferProcessorResult(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen) {
        super();
        this.buffer = buffer;
        this.contentOffset = contentOffset;
        this.contentLen = contentLen;
        this.outerOffset = outerOffset;
        this.outerLen = outerLen;
    }


    public char[] getBuffer() {
        return this.buffer;
    }

    public int getContentLen() {
        return this.contentLen;
    }

    public int getContentOffset() {
        return this.contentOffset;
    }

    public int getOuterOffset() {
        return this.outerOffset;
    }

    public int getOuterLen() {
        return this.outerLen;
    }

}
