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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;


/**
 * <p>
 *   Fast implementation of {@link Writer} that avoids the need to use a thread-safe
 *   {@link StringBuffer}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class FastStringWriter extends Writer {

    private final StringBuilder builder;



    public FastStringWriter() {
        super();
        this.builder = new StringBuilder();
    }


    public FastStringWriter(final int initialSize) {
        super();
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative buffer size");
        }
        this.builder = new StringBuilder(initialSize);
    }




    @Override
    public void write(final int c) {
        this.builder.append((char) c);
    }


    @Override
    public void write(final String str) {
        this.builder.append(str);
    }


    @Override
    public void write(final String str, final int off, final int len)  {
        this.builder.append(str, off, off + len);
    }


    @Override
    public void write(final char[] cbuf) {
        this.builder.append(cbuf, 0, cbuf.length);
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        this.builder.append(cbuf, off, len);
    }



    @Override
    public void flush() throws IOException {
        // Nothing to be flushed
    }


    @Override
    public void close() throws IOException {
        // Nothing to be closed
    }


    @Override
    public String toString() {
        return this.builder.toString();
    }

}
