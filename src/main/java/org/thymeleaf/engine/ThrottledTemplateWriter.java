/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
final class ThrottledTemplateWriter extends Writer {

    private static int OVERFLOW_BUFFER_INCREMENT = 512;

    private final String templateName;
    private final Writer writer;

    private char[] overflow;
    private int overflowSize;

    private boolean unlimited;
    private int limit;


    public ThrottledTemplateWriter(final String templateName, final Writer writer) {
        super();
        this.templateName = templateName;
        this.writer = writer;
        this.overflow = null;
        this.overflowSize = 0;
        this.unlimited = false;
        this.limit = 0;
    }


    boolean isOverflowed() {
        return this.overflowSize > 0;
    }


    void allow(final int limit) {

        if (limit == Integer.MAX_VALUE || limit < 0) {
            this.unlimited = true;
            this.limit = -1;
        } else {
            this.unlimited = false;
            if (this.limit < 0) {
                this.limit = limit;
            } else {
                this.limit += limit;
            }
        }

        if (this.overflowSize == 0 || this.limit == 0) {
            return;
        }

        try {

            if (this.unlimited || this.limit >= this.overflowSize) {
                this.writer.write(this.overflow, 0, this.overflowSize);
                if (!this.unlimited) {
                    this.limit -= this.overflowSize;
                }
                this.overflowSize = 0;
                return;
            }

            this.writer.write(this.overflow, 0, this.limit);
            System.arraycopy(this.overflow, this.limit, this.overflow, 0, this.overflowSize - this.limit);
            this.overflowSize -= this.limit;
            this.limit = 0;

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
        if (this.unlimited) {
            this.limit--;
        }
    }


    @Override
    public void write(final String str) throws IOException {
        final int len = str.length();
        if (this.limit == 0) {
            overflow(str, 0, len);
            return;
        }
        if (this.unlimited || this.limit >= len) {
            this.writer.write(str, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.writer.write(str, 0, this.limit);
        overflow(str, this.limit, (len - this.limit));
        this.limit = 0;
    }


    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        if (this.limit == 0) {
            overflow(str, off, len);
            return;
        }
        if (this.unlimited || this.limit >= len) {
            this.writer.write(str, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.writer.write(str, off, this.limit);
        overflow(str, off + this.limit, (len - this.limit));
        this.limit = 0;
    }


    @Override
    public void write(final char[] cbuf) throws IOException {
        final int len = cbuf.length;
        if (this.limit == 0) {
            overflow(cbuf, 0, len);
            return;
        }
        if (this.unlimited || this.limit >= len) {
            this.writer.write(cbuf, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.writer.write(cbuf, 0, this.limit);
        overflow(cbuf, this.limit, (len - this.limit));
        this.limit = 0;
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.limit == 0) {
            overflow(cbuf, off, len);
            return;
        }
        if (this.unlimited || this.limit >= len) {
            this.writer.write(cbuf, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.writer.write(cbuf, off, this.limit);
        overflow(cbuf, off + this.limit, (len - this.limit));
        this.limit = 0;
    }




    private void overflow(final int c) throws IOException {
        ensureOverflowCapacity(1);
        this.overflow[this.overflowSize] = (char)c;
        this.overflowSize++;
    }


    private void overflow(final String str, final int off, final int len) throws IOException {
        ensureOverflowCapacity(len);
        str.getChars(off, off + len, this.overflow, this.overflowSize);
        this.overflowSize += len;
    }


    private void overflow(final char[] cbuf, final int off, final int len) throws IOException {
        ensureOverflowCapacity(len);
        System.arraycopy(cbuf, off, this.overflow, this.overflowSize, len);
        this.overflowSize += len;
    }




    private void ensureOverflowCapacity(final int len) {
        if (this.overflow == null) {
            this.overflow = new char[((len / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT];
            return;
        }
        final int targetLen = this.overflowSize + len;
        if (this.overflow.length < targetLen) {
            this.overflow = Arrays.copyOf(this.overflow, ((targetLen / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT);
        }
    }




    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }


    @Override
    public void close() throws IOException {
        this.writer.close();
    }



}
