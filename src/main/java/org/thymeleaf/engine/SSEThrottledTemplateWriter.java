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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.4
 *
 */
class SSEThrottledTemplateWriter extends ThrottledTemplateWriter implements ISSEThrottledTemplateWriterControl {

    private final static char[] SSE_ID_PREFIX = "id: ".toCharArray();
    private final static char[] SSE_EVENT_PREFIX = "event: ".toCharArray();
    private final static char[] SSE_DATA_PREFIX = "data: ".toCharArray();

    private char[] id = null;
    private char[] event = null;

    private boolean eventHasMeta = false;
    private boolean newEvent = true;


    SSEThrottledTemplateWriter(final String templateName, final TemplateFlowController flowController) {
        super(templateName, flowController);
    }




    public void startEvent(final char[] id, final char[] event) {
        // char[] are mutable but this is not an issue as this class is package-protected and the code from
        // which this method is called is under control
        this.newEvent = true;
        this.id = id;
        this.event = event;
    }


    private void doStartEvent() throws IOException {
        this.eventHasMeta = false;
        if (this.event != null) {
            // Write the "event" field
            if (!checkTokenValid(this.event)) {
                throw new IllegalArgumentException("Event for SSE event cannot contain a newline (\\n) character");
            }
            super.write(SSE_EVENT_PREFIX);
            super.write(this.event);
            super.write('\n');
            this.eventHasMeta = true;
        }
        if (this.id != null) {
            // Write the "id" field
            if (!checkTokenValid(this.id)) {
                throw new IllegalArgumentException("ID for SSE event cannot contain a newline (\\n) character");
            }
            super.write(SSE_ID_PREFIX);
            super.write(this.id);
            super.write('\n');
            this.eventHasMeta = true;
        }
    }


    public void endEvent() throws IOException {
        if (!this.newEvent) {
            super.write('\n');
            super.write('\n');
        } else if (this.eventHasMeta) {
            // If we only wrote meta, we still need an additional line feed to separate from the next event
            super.write('\n');
        }
    }



    @Override
    public void write(final int c) throws IOException {

        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }

        super.write(c);
        if (c == '\n') {
            // This is a line feed, so we need to write the prefix afterwards
            super.write(SSE_DATA_PREFIX);
        }

    }


    @Override
    public void write(final String str) throws IOException {
        write(str, 0, str.length());
    }


    @Override
    public void write(final String str, final int off, final int len) throws IOException {

        if (str == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > str.length()) || (len < 0) ||
                ((off + len) > str.length()) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (len == 0) {
            // Even if we are writing nothing, we need to give the underlying buffered implementation the chance to
            // overflow...
            super.write(str, off, len);
            return;
        }

        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }

        char c;
        int i = off;
        int x = i;
        final int maxi = (off + len);
        while (i < maxi) {
            c = str.charAt(i++);
            if (c == '\n') {
                // This is a line feed, so we write everything until this point, then the prefix, then we continue
                super.write(str, x, (i - x));
                super.write(SSE_DATA_PREFIX);
                x = i;
            }
        }
        // Finally we write whatever is left at the original buffer
        if (x < i) {
            super.write(str, x, (i - x));
        }

    }


    @Override
    public void write(final char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {

        if (cbuf == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (len == 0) {
            // Even if we are writing nothing, we need to give the underlying buffered implementation the chance to
            // overflow...
            super.write(cbuf, off, len);
            return;
        }

        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }

        char c;
        int i = off;
        int x = i;
        final int maxi = (off + len);
        while (i < maxi) {
            c = cbuf[i++];
            if (c == '\n') {
                // This is a line feed, so we write everything until this point, then the prefix, then we continue
                super.write(cbuf, x, (i - x));
                super.write(SSE_DATA_PREFIX);
                x = i;
            }
        }
        // Finally we write whatever is left at the original buffer
        if (x < i) {
            super.write(cbuf, x, (i - x));
        }

    }


    // Used to check internally that neither event names nor IDs contain line feeds
    private static boolean checkTokenValid(final char[] token) {
        if (token == null || token.length == 0) {
            return true;
        }
        for (int i = 0; i < token.length; i++) {
            if (token[i] == '\n') {
                return false;
            }

        }
        return true;
    }


}
