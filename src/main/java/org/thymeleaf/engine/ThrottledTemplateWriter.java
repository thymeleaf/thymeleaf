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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
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

    private final String templateName;
    private final TemplateFlowController flowController;

    private IThrottledTemplateWriterAdapter adapter;
    private Writer writer;


    ThrottledTemplateWriter(final String templateName, final TemplateFlowController flowController) {
        super();
        this.templateName = templateName;
        this.flowController = flowController;
        this.adapter = null;
        this.writer = null;
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


    void setOutput(final OutputStream outputStream, final Charset charset) {
        if (this.adapter != null && this.adapter instanceof ThrottledTemplateWriterWriterAdapter) {
            throw new TemplateOutputException(
                    "The throttled processor has already been initialized to use char-based output (Writer), " +
                    "but an OutputStream has been specified.", this.templateName, -1, -1, null);
        }
        if (this.adapter == null) {
            this.adapter = new ThrottledTemplateWriterOutputStreamAdapter(this.templateName, this.flowController);
            this.writer = new OutputStreamWriter((ThrottledTemplateWriterOutputStreamAdapter)this.adapter, charset);
        }
        ((ThrottledTemplateWriterOutputStreamAdapter)this.adapter).setOutputStream(outputStream);
    }




    boolean isOverflown() {
        return this.adapter.isOverflown();
    }

    boolean isStopped() {
        return this.adapter.isStopped();
    }


    int getMaxOverflowSize() {
        return this.adapter.getMaxOverflowSize();
    }


    void allow(final int limit) {
        this.adapter.allow(limit);
    }



    @Override
    public void write(final int c) throws IOException {
        this.writer.write(c);
    }


    @Override
    public void write(final String str) throws IOException {
        this.writer.write(str);
    }


    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.writer.write(str, off, len);
    }


    @Override
    public void write(final char[] cbuf) throws IOException {
        this.writer.write(cbuf);
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
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
        int getMaxOverflowSize();
        void allow(final int limit);

    }


}
