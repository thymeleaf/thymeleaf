/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Arrays;

import org.thymeleaf.exceptions.TemplateOutputException;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class ThrottledTemplateWriterWriterAdapter
        extends Writer
        implements ThrottledTemplateWriter.IThrottledTemplateWriterAdapter {

    // Given we will be directly writing chars we will use a 256-char buffer as a sensible, approximate
    // measure of the amount of overflow we will need, given the only influencing factor for us is
    // the size of the structures being written to this writer (elements, texts, etc.)
    private static int OVERFLOW_BUFFER_INCREMENT = 256;

    private final String templateName;
    private final TemplateFlowController flowController;

    private Writer writer;

    private char[] overflow;
    private int overflowSize;
    private int maxOverflowSize;
    private int overflowGrowCount;

    private boolean unlimited;
    private int limit;
    private int writtenCount;


    ThrottledTemplateWriterWriterAdapter(final String templateName, final TemplateFlowController flowController) {
        super();
        this.templateName = templateName;
        this.flowController = flowController;
        this.overflow = null;
        this.overflowSize = 0;
        this.maxOverflowSize = 0;
        this.overflowGrowCount = 0;
        this.unlimited = false;
        this.limit = 0;
        this.writtenCount = 0;
        this.flowController.stopProcessing = true;
    }

    void setWriter(final Writer writer) {
        this.writer = writer;
        this.writtenCount = 0;
    }


    public boolean isOverflown() {
        return this.overflowSize > 0;
    }

    public boolean isStopped() {
        return this.limit == 0;
    }


    public int getWrittenCount() {
        return this.writtenCount;
    }


    public int getMaxOverflowSize() {
        return this.maxOverflowSize;
    }


    public int getOverflowGrowCount() {
        return this.overflowGrowCount;
    }




    public void allow(final int limit) {

        if (limit == Integer.MAX_VALUE || limit < 0) {
            this.unlimited = true;
            this.limit = -1;
        } else {
            this.unlimited = false;
            this.limit = limit;
        }

        this.flowController.stopProcessing = (this.limit == 0);

        if (this.overflowSize == 0 || this.limit == 0) {
            return;
        }

        try {

            if (this.unlimited || this.limit > this.overflowSize) {
                this.writer.write(this.overflow, 0, this.overflowSize);
                if (!this.unlimited) {
                    this.limit -= this.overflowSize;
                }
                this.writtenCount += this.overflowSize;
                this.overflowSize = 0;
                return;
            }

            this.writer.write(this.overflow, 0, this.limit);
            if (this.limit < this.overflowSize) {
                System.arraycopy(this.overflow, this.limit, this.overflow, 0, this.overflowSize - this.limit);
            }
            this.overflowSize -= this.limit;
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;

        } catch (final IOException e) {
            throw new TemplateOutputException(
                    "Exception while trying to write overflowed buffer in throttled template", this.templateName, -1, -1, e);
        }

    }



    @Override
    public void write(final int c) throws IOException {
        if (this.limit == 0) {
            overflow(c);
            return;
        }
        this.writer.write(c);
        if (!this.unlimited) {
            this.limit--;
        }
        this.writtenCount++;
        if (this.limit == 0) {
            this.flowController.stopProcessing = true;
        }
    }


    @Override
    public void write(final String str) throws IOException {
        final int len = str.length();
        if (this.limit == 0) {
            overflow(str, 0, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.writer.write(str, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
            return;
        }
        this.writer.write(str, 0, this.limit);
        if (this.limit < len) {
            overflow(str, this.limit, (len - this.limit));
        }
        this.writtenCount += this.limit;
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }


    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        if (this.limit == 0) {
            overflow(str, off, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.writer.write(str, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
            return;
        }
        this.writer.write(str, off, this.limit);
        if (this.limit < len) {
            overflow(str, off + this.limit, (len - this.limit));
        }
        this.writtenCount += this.limit;
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }


    @Override
    public void write(final char[] cbuf) throws IOException {
        final int len = cbuf.length;
        if (this.limit == 0) {
            overflow(cbuf, 0, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.writer.write(cbuf, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
            return;
        }
        this.writer.write(cbuf, 0, this.limit);
        if (this.limit < len) {
            overflow(cbuf, this.limit, (len - this.limit));
        }
        this.writtenCount += this.limit;
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.limit == 0) {
            overflow(cbuf, off, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.writer.write(cbuf, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
            return;
        }
        this.writer.write(cbuf, off, this.limit);
        if (this.limit < len) {
            overflow(cbuf, off + this.limit, (len - this.limit));
        }
        this.writtenCount += this.limit;
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }




    private void overflow(final int c) {
        ensureOverflowCapacity(1);
        this.overflow[this.overflowSize] = (char)c;
        this.overflowSize++;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }


    private void overflow(final String str, final int off, final int len) {
        ensureOverflowCapacity(len);
        str.getChars(off, off + len, this.overflow, this.overflowSize);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }


    private void overflow(final char[] cbuf, final int off, final int len) {
        ensureOverflowCapacity(len);
        System.arraycopy(cbuf, off, this.overflow, this.overflowSize, len);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }




    private void ensureOverflowCapacity(final int len) {
        if (this.overflow == null) {
            this.overflow = new char[((len / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT];
            return;
        }
        final int targetLen = this.overflowSize + len;
        if (this.overflow.length < targetLen) {
            this.overflow = Arrays.copyOf(this.overflow, ((targetLen / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT);
            this.overflowGrowCount++;
        }
    }




    @Override
    public void flush() throws IOException {
        // No need to control overflow here. The fact that this has overflow will be used as a flag to determine
        // that further write operations are actually needed by means of the isOverflown() method.
        this.writer.flush();
    }


    @Override
    public void close() throws IOException {
        // This will normally be NEVER called, as Thymeleaf will not call close() on its Writers/OutputStreams
        // (only flush() is guaranteed to be called at the end).
        this.writer.close();
    }



}
