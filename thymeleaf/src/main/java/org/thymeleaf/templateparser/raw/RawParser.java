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
package org.thymeleaf.templateparser.raw;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;


/*
 * The RawParser is very silly: it will just read the resource using its buffers and issue a handleText event.
 *
 * Note that, instead of using AttoParser's IMarkupParser interface, the much simpler ITextHandler is used here instead.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 * 
 */
final class RawParser {



    private final BufferPool pool;







    RawParser(final int poolSize, final int bufferSize) {
        super();
        this.pool = new BufferPool(poolSize, bufferSize);
    }






    public void parse(final String document, final IRawHandler handler)
            throws RawParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        parse(new StringReader(document), handler);
    }




    public void parse(
            final Reader reader, final IRawHandler handler)
            throws RawParseException {

        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        parseDocument(reader, this.pool.poolBufferSize, handler);

    }





    /*
     * This method receiving the buffer size with package visibility allows
     * testing different buffer sizes.
     */
    void parseDocument(final Reader reader, final int suggestedBufferSize, final IRawHandler handler)
            throws RawParseException {

        // We are trying to read the entire resource into the buffer. If it's not big enough,
        // then it will have to be grown. Note grown buffers are not maintained by the pool,
        // so template cache should play a more important role for this, allowing the system to
        // not require the creation of large buffers each time.

        final long parsingStartTimeNanos = System.nanoTime();

        char[] buffer = null;

        try {

            handler.handleDocumentStart(parsingStartTimeNanos, 1, 1);

            int bufferSize = suggestedBufferSize;
            buffer = this.pool.allocateBuffer(bufferSize);

            int bufferContentSize = reader.read(buffer);

            boolean cont = (bufferContentSize != -1);

            while (cont) {

                if (bufferContentSize == bufferSize) {
                    // Buffer is not big enough, double it!

                    char[] newBuffer = null;
                    try {

                        bufferSize *= 2;

                        newBuffer = this.pool.allocateBuffer(bufferSize);
                        System.arraycopy(buffer, 0, newBuffer, 0, bufferContentSize);

                        this.pool.releaseBuffer(buffer);

                        buffer = newBuffer;

                    } catch (final Exception ignored) {
                        this.pool.releaseBuffer(newBuffer);
                    }

                }

                final int read = reader.read(buffer, bufferContentSize, (bufferSize - bufferContentSize));
                if (read != -1) {
                    bufferContentSize += read;
                } else {
                    cont = false;
                }

            }

            handler.handleText(buffer, 0, bufferContentSize, 1, 1);

            final int[] lastLineCol = computeLastLineCol(buffer, bufferContentSize);

            final long parsingEndTimeNanos = System.nanoTime();
            handler.handleDocumentEnd(parsingEndTimeNanos, (parsingEndTimeNanos - parsingStartTimeNanos), lastLineCol[0], lastLineCol[1]);

        } catch (final RawParseException e) {
            throw e;
        } catch (final Exception e) {
            throw new RawParseException(e);
        } finally {
            this.pool.releaseBuffer(buffer);
            try {
                reader.close();
            } catch (final Throwable ignored) {
                // This exception can be safely ignored
            }
        }

    }



    private static int[] computeLastLineCol(final char[] buffer, final int bufferContentSize) {

        if (bufferContentSize == 0) {
            return new int[] {1, 1};
        }

        int line = 1;
        int col = 1;

        char c;

        int lastLineFeed = 0;

        int n = bufferContentSize;
        int i = 0;

        while (n-- != 0) {
            c = buffer[i];
            if (c == '\n') {
                line++;
                lastLineFeed = i;
            }
            i++;
        }

        col = bufferContentSize - lastLineFeed;

        return new int[] {line, col};

    }


    




    /*
     * This class models a pool of buffers, used to keep the amount of
     * large char[] buffer objects required to operate to a minimum.
     *
     * Note this pool never blocks, so if a new buffer is needed and all
     * are currently allocated, a new char[] object is created and returned.
     *
     */
    private static final class BufferPool {

        private final char[][] pool;
        private final boolean[] allocated;
        private final int poolBufferSize;

        private BufferPool(final int poolSize, final int poolBufferSize) {

            super();

            this.pool = new char[poolSize][];
            this.allocated = new boolean[poolSize];
            this.poolBufferSize = poolBufferSize;

            for (int i = 0; i < this.pool.length; i++) {
                this.pool[i] = new char[this.poolBufferSize];
            }
            Arrays.fill(this.allocated, false);

        }

        private synchronized char[] allocateBuffer(final int bufferSize) {
            if (bufferSize != this.poolBufferSize) {
                // We will only pool buffers of the default size. If a different size is required, we just
                // create it without pooling.
                return new char[bufferSize];
            }
            for (int i = 0; i < this.pool.length; i++) {
                if (!this.allocated[i]) {
                    this.allocated[i] = true;
                    return this.pool[i];
                }
            }
            return new char[bufferSize];
        }

        private synchronized void releaseBuffer(final char[] buffer) {
            if (buffer == null) {
                return;
            }
            if (buffer.length != this.poolBufferSize) {
                // This buffer cannot be part of the pool - only buffers with a specific size are contained
                return;
            }
            for (int i = 0; i < this.pool.length; i++) {
                if (this.pool[i] == buffer) {
                    // Found it. Mark it as non-allocated
                    this.allocated[i] = false;
                    return;
                }
            }
            // The buffer wasn't part of our pool. Just return.
        }


    }


}
