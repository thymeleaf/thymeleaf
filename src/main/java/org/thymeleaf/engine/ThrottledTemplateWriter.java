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
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.thymeleaf.exceptions.TemplateOutputException;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
class ThrottledTemplateWriter extends Writer implements IThrottledTemplateWriterControl {

    private final String templateName;
    private final TemplateFlowController flowController;

    private IThrottledTemplateWriterAdapter adapter;
    private Writer writer;

    private boolean flushable;


    ThrottledTemplateWriter(final String templateName, final TemplateFlowController flowController) {
        super();
        this.templateName = templateName;
        this.flowController = flowController;
        this.adapter = null;
        this.writer = null;
        this.flushable = false;
    }




    void setOutput(final Writer writer) {
        if (this.adapter != null && this.adapter instanceof ThrottledTemplateWriterOutputStreamAdapter) {
            throw new TemplateOutputException(
                    "The throttled processor has already been initialized to use byte-based output (OutputStream), " +
                    "but a Writer has been specified.", this.templateName, -1, -1, null);
        }
        if (this.adapter == null) {
            this.adapter = new ThrottledTemplateWriterWriterAdapter(this.templateName, this.flowController);
            this.writer = ((ThrottledTemplateWriterWriterAdapter)this.adapter);
        }
        ((ThrottledTemplateWriterWriterAdapter)this.adapter).setWriter(writer);
    }


    void setOutput(final OutputStream outputStream, final Charset charset, final int maxOutputInBytes) {
        if (this.adapter != null && this.adapter instanceof ThrottledTemplateWriterWriterAdapter) {
            throw new TemplateOutputException(
                    "The throttled processor has already been initialized to use char-based output (Writer), " +
                    "but an OutputStream has been specified.", this.templateName, -1, -1, null);
        }
        if (this.adapter == null) {
            final int adapterOverflowBufferIncrementBytes =
                    (maxOutputInBytes == Integer.MAX_VALUE?
                            128 :
                            // output size could be too small, so we will set a minimum of 16b, and max of 128b
                            Math.min(128, Math.max(16, maxOutputInBytes / 8)));
            this.adapter = new ThrottledTemplateWriterOutputStreamAdapter(this.templateName, this.flowController, adapterOverflowBufferIncrementBytes);
            // We cannot directly use a java.io.OutputStreamWriter here because that class uses a CharsetEncoder
            // underneath that always creates a 8192byte (8KB) buffer, and there is no way to configure that.
            //
            // The problem with such buffer is that we are counting the number of output bytes at the OutputStream
            // wrapper (the adapter we just created), which is set as the output of the OutputStreamWriter, and which
            // does not receive any bytes until the OutputStreamWriter flushes its 8KB buffer. But in a scenario in
            // which, for instance, we only need 100 bytes to complete our output chunk, this would mean we would still
            // have an overflow of more than 8,000 bytes. And that basically renders this whole template throttling
            // mechanism useless.
            //
            // So we will use an alternative construct to OutputStreamWriter, based on a WritableByteChannel. This
            // will basically work in the same way as an OutputStreamWriter, but by building it manually we will be
            // able to specify the size of the buffer to be used.
            //
            // And we do not want the buffer at the Writer -> OutputStream converter to completely disappear, because
            // it actually improves the performance of the converter. So we will use the maxOutputInBytes (the size
            // of the output to be obtained from the throttled template the first time) as an approximate measure
            // of what we will need in subsequent calls, and we will to try to adjust the size of the buffer so
            // that we make the most use of it without needing to flush too often, nor 'losing' chars in the buffer.
            //
            // Last, note that in order to avoid this 'loss of chars' we will combine this with 'flush' calls at the
            // 'isOverflown()' and 'isStopped()' calls.
            final CharsetEncoder charsetEncoder = charset.newEncoder();
            int channelBufferSize =
                    (maxOutputInBytes == Integer.MAX_VALUE?
                            1024 :
                            // Buffers of CharsetEncoders behave strangely (even hanging) when the buffers being
                            // set are too small to house the encoding of some elements (e.g. 1 or 2 bytes). So we
                            // will set a minimum of 64b and a max of 512b.
                            Math.min(512, Math.max(64, adapterOverflowBufferIncrementBytes * 2)));
            final WritableByteChannel channel = Channels.newChannel((ThrottledTemplateWriterOutputStreamAdapter)this.adapter);
            this.writer = Channels.newWriter(channel, charsetEncoder, channelBufferSize);
            // Use of a wrapping BufferedWriter is recommended by OutputStreamWriter javadoc for improving efficiency,
            // avoiding frequent converter invocations (note that the character converter also has its own buffer).
            //this.writer = new BufferedWriter(new OutputStreamWriter((ThrottledTemplateWriterOutputStreamAdapter)this.adapter, charset));
        }
        ((ThrottledTemplateWriterOutputStreamAdapter)this.adapter).setOutputStream(outputStream);
    }




    public boolean isOverflown() throws IOException {
        if (this.flushable) {
            // We need this flushing because OutputStreamWriter bufferizes, and given we might be taking account of
            // the output bytes at an OutputStream implementation in a level below this OutputStreamWriter, we could
            // have the wrong figures until we flush contents.
            this.flush();
            this.flushable = false;
        }
        return this.adapter.isOverflown();
    }

    public boolean isStopped() throws IOException {
        if (this.flushable) {
            // We need this flushing because OutputStreamWriter bufferizes, and given we might be taking account of
            // the output bytes at an OutputStream implementation in a level below this OutputStreamWriter, we could
            // have the wrong figures until we flush contents.
            this.flush();
            this.flushable = false;
        }
        return this.adapter.isStopped();
    }


    public int getWrittenCount() {
        return this.adapter.getWrittenCount();
    }


    public int getMaxOverflowSize() {
        return this.adapter.getMaxOverflowSize();
    }


    public int getOverflowGrowCount() {
        return this.adapter.getOverflowGrowCount();
    }


    void allow(final int limit) {
        this.adapter.allow(limit);
    }



    @Override
    public void write(final int c) throws IOException {
        this.flushable = true;
        this.writer.write(c);
    }


    @Override
    public void write(final String str) throws IOException {
        this.flushable = true;
        this.writer.write(str);
    }


    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.flushable = true;
        this.writer.write(str, off, len);
    }


    @Override
    public void write(final char[] cbuf) throws IOException {
        this.flushable = true;
        this.writer.write(cbuf);
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.flushable = true;
        this.writer.write(cbuf, off, len);
    }


    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }


    @Override
    public void close() throws IOException {
        this.writer.close();
    }




    interface IThrottledTemplateWriterAdapter {

        boolean isOverflown();
        boolean isStopped();
        int getWrittenCount();
        int getMaxOverflowSize();
        int getOverflowGrowCount();
        void allow(final int limit);

    }


}
