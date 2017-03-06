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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


/**
 * <p>
 *   Specialized wrapper implementing the logic required to convert normal output
 *   to SSE events.
 * </p>
 * <p>
 *   See the <a href="https://www.w3.org/TR/2009/WD-eventsource-20091029/">W3C SSE Specification</a>.
 * </p>
 * <p>
 *   This wraps another {@link OutputStream} instance, adding prefixes to each line of output
 *   as required.
 * </p>
 * <p>
 *   Note this class is for <strong>internal use only</strong>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.4
 *
 */
public final class SSEOutputStream extends OutputStream {

    private final static Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
    private final static Charset CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1");

    private final static String SSE_ID_PREFIX = "id: ";
    private final static String SSE_EVENT_PREFIX = "event: ";
    private final static String SSE_DATA_PREFIX = "data: ";

    private final static byte[] SSE_ID_PREFIX_UTF_8 = SSE_ID_PREFIX.getBytes(CHARSET_UTF_8);
    private final static byte[] SSE_ID_PREFIX_ISO_8859_1 = SSE_ID_PREFIX.getBytes(CHARSET_ISO_8859_1);
    private final static byte[] SSE_EVENT_PREFIX_UTF_8 = SSE_EVENT_PREFIX.getBytes(CHARSET_UTF_8);
    private final static byte[] SSE_EVENT_PREFIX_ISO_8859_1 = SSE_EVENT_PREFIX.getBytes(CHARSET_ISO_8859_1);
    private final static byte[] SSE_DATA_PREFIX_UTF_8 = SSE_DATA_PREFIX.getBytes(CHARSET_UTF_8);
    private final static byte[] SSE_DATA_PREFIX_ISO_8859_1 = SSE_DATA_PREFIX.getBytes(CHARSET_ISO_8859_1);

    private final OutputStream outputStream;
    private final Charset charset;

    // We will use these in order to avoid creating these prefixes byte[]'s once and again for the most common encodings
    private final byte[] sse_id_prefix;
    private final byte[] sse_event_prefix;
    private final byte[] sse_data_prefix;

    private boolean eventHasMeta = false;
    private boolean newEvent = true;



    public SSEOutputStream(final OutputStream outputStream, final Charset charset) {
        super();
        if (outputStream == null) {
            throw new IllegalArgumentException("OutputStream cannot be null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Charset cannot be null");
        }
        this.outputStream = outputStream;
        this.charset = charset;
        if (this.charset.equals(CHARSET_UTF_8)) {
            this.sse_id_prefix = SSE_ID_PREFIX_UTF_8;
            this.sse_event_prefix = SSE_EVENT_PREFIX_UTF_8;
            this.sse_data_prefix = SSE_DATA_PREFIX_UTF_8;
        } else if (this.charset.equals(CHARSET_ISO_8859_1)) {
            this.sse_id_prefix = SSE_ID_PREFIX_ISO_8859_1;
            this.sse_event_prefix = SSE_EVENT_PREFIX_ISO_8859_1;
            this.sse_data_prefix = SSE_DATA_PREFIX_ISO_8859_1;
        } else {
            final byte[] newline = "\n".getBytes(this.charset);
            if (newline.length != 1 || newline[0] != 0xA) {
                // We will base line-breaking in the appearance of line feeds as (char)10, so we cannot allow
                // encodings in which line feed is signaled by more than one byte (UTF-16, UTF-16BE, UTF-16LE) or
                // any others where the line feed character is not represented by byte 0xA.
                throw new UnsupportedOperationException(
                    "Encoding \"" + this.charset + "\" is not allowed for Server-Sent Events (SSE). Only encodings " +
                    "that encode the new line character U+000A as a single byte with value 10 can be used. " +
                    "Examples: UTF-8, ISO889-1, Shift-JIS, etc.");
            }
            this.sse_id_prefix = SSE_ID_PREFIX.getBytes(this.charset);
            this.sse_event_prefix = SSE_EVENT_PREFIX.getBytes(this.charset);
            this.sse_data_prefix = SSE_DATA_PREFIX.getBytes(this.charset);
        }
    }


    public void startEvent(final String id, final String event) throws IOException {
        this.eventHasMeta = false;
        if (id != null) {
            // Write the "id" field
            if (id.indexOf('\n') != -1) {
                throw new IllegalArgumentException("ID for SSE event cannot contain a newline (\\n) character");
            }
            this.outputStream.write(this.sse_id_prefix);
            this.outputStream.write(id.getBytes(this.charset));
            this.outputStream.write(0xA);
            this.eventHasMeta = true;
        }
        if (event != null) {
            // Write the "event" field
            if (event.indexOf('\n') != -1) {
                throw new IllegalArgumentException("Event for SSE event cannot contain a newline (\\n) character");
            }
            this.outputStream.write(this.sse_event_prefix);
            this.outputStream.write(event.getBytes(this.charset));
            this.outputStream.write(0xA);
            this.eventHasMeta = true;
        }
        this.newEvent = true;
    }


    public void endEvent() throws IOException {
        if (!this.newEvent) {
            this.outputStream.write(0xA);
            this.outputStream.write(0xA);
        } else if (this.eventHasMeta) {
            // If we only wrote meta, we still need an additional line feed to separate from the next event
            this.outputStream.write(0xA);
        }
    }


    @Override
    public void write(final int b) throws IOException {

        if (this.newEvent) {
            this.outputStream.write(this.sse_data_prefix);
            this.newEvent = false;
        }

        this.outputStream.write(b);
        if (b == 0xA) {
            // This is a line feed, so we need to write the prefix afterwards
            this.outputStream.write(this.sse_data_prefix);
        }

    }


    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {

        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        if (this.newEvent) {
            this.outputStream.write(this.sse_data_prefix);
            this.newEvent = false;
        }

        byte c;
        int i = off;
        int x = i;
        final int maxi = (off + len);
        while (i < maxi) {
            c = b[i++];
            if (c == 0xA) {
                // This is a line feed, so we write everything until this point, then the prefix, then we continue
                this.outputStream.write(b, x, (i - x));
                this.outputStream.write(this.sse_data_prefix);
                x = i;
            }
        }
        // Finally we write whatever is left at the original buffer
        if (x < i) {
            this.outputStream.write(b, x, (i - x));
        }

    }


    @Override
    public void flush() throws IOException {
        this.outputStream.flush();
    }


    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }



}
