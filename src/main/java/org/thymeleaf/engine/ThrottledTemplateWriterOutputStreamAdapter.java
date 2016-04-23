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
import java.io.OutputStream;
import java.util.Arrays;

import org.thymeleaf.exceptions.TemplateOutputException;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class ThrottledTemplateWriterOutputStreamAdapter
        extends OutputStream
        implements ThrottledTemplateWriter.IThrottledTemplateWriterAdapter {

    private static int OVERFLOW_BUFFER_INCREMENT = 256;

    private final String templateName;
    private final TemplateFlowController flowController;

    private OutputStream os;

    private byte[] overflow;
    private int overflowSize;
    private int maxOverflowSize;

    private boolean unlimited;
    private int limit;


    ThrottledTemplateWriterOutputStreamAdapter(final String templateName, final TemplateFlowController flowController) {
        super();
        this.templateName = templateName;
        this.flowController = flowController;
        this.overflow = null;
        this.overflowSize = 0;
        this.maxOverflowSize = 0;
        this.unlimited = false;
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }

    void setOutputStream(final OutputStream os) {
        this.os = os;
    }


    public boolean isOverflown() {
        return this.overflowSize > 0;
    }

    public boolean isStopped() {
        return this.limit == 0;
    }


    public int getMaxOverflowSize() {
        return this.maxOverflowSize;
    }




    public void allow(final int limit) {

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

        this.flowController.stopProcessing = (this.limit == 0);

        if (this.overflowSize == 0 || this.limit == 0) {
            return;
        }

        try {

            if (this.unlimited || this.limit > this.overflowSize) {
                this.os.write(this.overflow, 0, this.overflowSize);
                if (!this.unlimited) {
                    this.limit -= this.overflowSize;
                }
                this.overflowSize = 0;
                return;
            }

            this.os.write(this.overflow, 0, this.limit);
            if (this.limit < this.overflowSize) {
                System.arraycopy(this.overflow, this.limit, this.overflow, 0, this.overflowSize - this.limit);
            }
            this.overflowSize -= this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;

        } catch (final IOException e) {
            throw new TemplateOutputException(
                    "Exception while trying to write overflowed buffer in throttled template", this.templateName, -1, -1, e);
        }

    }




    @Override
    public void write(final int b) throws IOException {
        if (this.limit == 0) {
            overflow(b);
            return;
        }
        this.os.write(b);
        if (!this.unlimited) {
            this.limit--;
        }
        if (this.limit == 0) {
            this.flowController.stopProcessing = true;
        }
    }


    @Override
    public void write(final byte[] bytes, final int off, final int len) throws IOException {
        if (this.limit == 0) {
            overflow(bytes, off, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.os.write(bytes, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.os.write(bytes, off, this.limit);
        if (this.limit < len) {
            overflow(bytes, off + this.limit, (len - this.limit));
        }
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }


    @Override
    public void write(final byte[] bytes) throws IOException {
        final int len = bytes.length;
        if (this.limit == 0) {
            overflow(bytes, 0, len);
            return;
        }
        if (this.unlimited || this.limit > len) {
            this.os.write(bytes, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            return;
        }
        this.os.write(bytes, 0, this.limit);
        if (this.limit < len) {
            overflow(bytes, this.limit, (len - this.limit));
        }
        this.limit = 0;
        this.flowController.stopProcessing = true;
    }




    private void overflow(final int c) {
        ensureOverflowCapacity(1);
        this.overflow[this.overflowSize] = (byte)c;
        this.overflowSize++;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }


    private void overflow(final byte[] bytes, final int off, final int len) {
        ensureOverflowCapacity(len);
        System.arraycopy(bytes, off, this.overflow, this.overflowSize, len);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }




    private void ensureOverflowCapacity(final int len) {
        if (this.overflow == null) {
            this.overflow = new byte[((len / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT];
            return;
        }
        final int targetLen = this.overflowSize + len;
        if (this.overflow.length < targetLen) {
            this.overflow = Arrays.copyOf(this.overflow, ((targetLen / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT);
        }
    }




    @Override
    public void flush() throws IOException {
        this.os.flush();
    }


    @Override
    public void close() throws IOException {
        this.os.close();
    }



}
