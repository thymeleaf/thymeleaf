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
package org.thymeleaf.templateparser.text;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.attoparser.HtmlMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.MarkupEventProcessor;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resource.CharArrayResource;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.ReaderResource;
import org.thymeleaf.resource.StringResource;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.ThymeleafTemplateReader;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TextTemplateParser implements ITemplateParser {





    private final BufferPool pool;









    public TextTemplateParser() {
        this(DEFAULT_POOL_SIZE, DEFAULT_BUFFER_SIZE);
    }


    public TextTemplateParser(final int bufferPoolSize, final int bufferSize) {
        super();
        this.pool = new BufferPool(bufferPoolSize, bufferSize);
    }




    /*
     * -------------------
     * PARSE METHODS
     * -------------------
     */



    public final void parseTemplate(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final String[] selectors,
            final ITemplateHandler templateHandler) {
        parse(configuration, templateMode, templateResource, true, selectors, templateHandler);
    }


    public final void parseFragment(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final String[] selectors,
            final ITemplateHandler templateHandler) {
        parse(configuration, templateMode, templateResource, false, selectors, templateHandler);
    }



    private void parse(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final boolean topLevel,
            final String[] selectors,
            final ITemplateHandler templateHandler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(templateResource, "Template Resource cannot be null");
        Validate.notNull(templateHandler, "Template Handler cannot be null");

        if (!templateMode.isText()) {
            throw new IllegalArgumentException(
                    "An unsupported template mode has been specified for the TEXT parser: " + templateMode);
        }

        if (selectors != null && selectors.length > 0) {
            throw new IllegalArgumentException(
                    "Markup selectors have been specified for a template in " + templateMode + " template mode, " +
                    "but this is not allowed. Markup selectors are only allowed in " + TemplateMode.HTML + " or " +
                    TemplateMode.XML + " template modes");
        }


        final String templateResourceName = templateResource.getName();

        try {

            // The final step of the handler chain will be the adapter that will convert attoparser's handler chain to thymeleaf's.
            final IMarkupHandler handler =
                    new TemplateHandlerAdapterMarkupHandler(
                            templateResourceName,
                            topLevel,
                            templateHandler,
                            configuration.getTextRepository(),
                            configuration.getElementDefinitions(),
                            configuration.getAttributeDefinitions(),
                            templateMode);

            // Each type of resource will require a different parser method to be called.
            final Reader templateReader;
            if (templateResource instanceof ReaderResource) {

                templateReader = new ThymeleafTemplateReader(((ReaderResource)templateResource).getContent());

            } else if (templateResource instanceof StringResource) {

                templateReader = new ThymeleafTemplateReader(new StringReader(((StringResource)templateResource).getContent()));

            } else if (templateResource instanceof CharArrayResource) {

                final CharArrayResource charArrayResource = (CharArrayResource) templateResource;
                final CharArrayReader charArrayReader =
                        new CharArrayReader(charArrayResource.getContent(), charArrayResource.getOffset(), charArrayResource.getLen());
                templateReader = new ThymeleafTemplateReader(charArrayReader);

            } else {

                throw new IllegalArgumentException(
                        "Cannot parse: unrecognized " + IResource.class.getSimpleName() + " implementation: " + templateResource.getClass().getName());

            }

            doParse(templateReader, handler);


        } catch (final ParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, templateResource.getName(), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, templateResource.getName(), e);
        }

    }



    private void doParse(final Reader reader, final IMarkupHandler handler) throws ParseException {

        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        final IMarkupHandler markupHandler =
                (ParseConfiguration.ParsingMode.HTML.equals(this.configuration.getMode()) ?
                        new HtmlMarkupHandler(handler) : handler);

        markupHandler.setParseConfiguration(this.configuration);

        final ParseStatus status = new ParseStatus();
        markupHandler.setParseStatus(status);

        final ParseSelection selection = new ParseSelection();
        markupHandler.setParseSelection(selection);

        // We will not report directly to the handler, but instead to an intermediate class that will be in
        // charge of applying the required markup logic and rules, according to the specified configuration
        final MarkupEventProcessor eventProcessor =
                new MarkupEventProcessor(markupHandler, status, this.configuration);

        // We don't already have a suitable char[] buffer, so we specify null for it and expect the parser
        // to use one of its pooled buffers.
        parseDocument(reader, this.pool.poolBufferSize, eventProcessor, status);

    }










    /*
     * This method receiving the buffer size with package visibility allows
     * testing different buffer sizes.
     */
    void parseDocument(
            final Reader reader, final int suggestedBufferSize,
            final MarkupEventProcessor eventProcessor, final ParseStatus status)
            throws org.attoparser.ParseException {


        final long parsingStartTimeNanos = System.nanoTime();

        char[] buffer = null;

        try {

            eventProcessor.processDocumentStart(parsingStartTimeNanos, 1, 1);

            int bufferSize = suggestedBufferSize;
            buffer = this.pool.allocateBuffer(bufferSize);

            int bufferContentSize = reader.read(buffer);

            boolean cont = (bufferContentSize != -1);

            status.offset = -1;
            status.line = 1;
            status.col = 1;
            status.inStructure = false;
            status.parsingDisabled = true;
            status.parsingDisabledLimitSequence = null;
            status.autoCloseRequired = null;
            status.autoCloseLimits = null;

            while (cont) {

                parseBuffer(buffer, 0, bufferContentSize, eventProcessor, status);

                int readOffset = 0;
                int readLen = bufferSize;

                if (status.offset == 0) {

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

                    // it's possible for two reads to occur in a row and 1) read less than the bufferSize and 2)
                    // still not find the next tag/end of structure
                    readOffset = bufferContentSize;
                    readLen = bufferSize - readOffset;

                } else if (status.offset < bufferContentSize) {

                    System.arraycopy(buffer, status.offset, buffer, 0, bufferContentSize - status.offset);

                    readOffset = bufferContentSize - status.offset;
                    readLen = bufferSize - readOffset;

                    status.offset = 0;
                    bufferContentSize = readOffset;

                }

                final int read = reader.read(buffer, readOffset, readLen);
                if (read != -1) {
                    bufferContentSize = readOffset + read;
                } else {
                    cont = false;
                }

            }

            // Iteration done, now it's time to clean up in case we still have some text to be notified

            int lastLine = status.line;
            int lastCol = status.col;

            final int lastStart = status.offset;
            final int lastLen = bufferContentSize - lastStart;

            if (lastLen > 0) {

                if (status.inStructure) {
                    throw new org.attoparser.ParseException(
                            "Incomplete structure: \"" + new String(buffer, lastStart, lastLen) + "\"", status.line, status.col);
                }

                eventProcessor.processText(buffer, lastStart, lastLen, status.line, status.col);

                // As we have produced an additional text event, we need to fast-forward the
                // lastLine and lastCol position to include the last text structure.
                for (int i = lastStart; i < (lastStart + lastLen); i++) {
                    final char c = buffer[i];
                    if (c == '\n') {
                        lastLine++;
                        lastCol = 1;
                    } else {
                        lastCol++;
                    }

                }

            }

            final long parsingEndTimeNanos = System.nanoTime();
            eventProcessor.processDocumentEnd(parsingEndTimeNanos, (parsingEndTimeNanos - parsingStartTimeNanos), lastLine, lastCol);

        } catch (final org.attoparser.ParseException e) {
            throw e;
        } catch (final Exception e) {
            throw new org.attoparser.ParseException(e);
        } finally {
            this.pool.releaseBuffer(buffer);
            try {
                reader.close();
            } catch (final Throwable ignored) {
                // This exception can be safely ignored
            }
        }

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
