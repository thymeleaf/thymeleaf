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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.LoggingUtils;

/**
 * <p>
 *   Standard implementation of {@link IThrottledTemplateProcessor}.
 * </p>
 * <p>
 *   This class is for <strong>internal</strong> use only. There is usually no reason why user's code should directly
 *   reference it.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class ThrottledTemplateProcessor implements IThrottledTemplateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TemplateEngine.TIMER_LOGGER_NAME);

    private static final int NANOS_IN_SECOND = 1000000;

    private static final String OUTPUT_TYPE_CHARS = "chars";
    private static final String OUTPUT_TYPE_BYTES = "bytes";

    // We will use an AtomicLong in order to generate identifiers. Even if 100 throttled template processors were
    // generated each millisecond, this would still give us enough unique identifiers for almost 6M years.
    private static final AtomicLong identifierGenerator = new AtomicLong(0L);


    private final String identifier;
    private final TemplateSpec templateSpec;
    private final IEngineContext context;
    private final TemplateModel templateModel;
    private final ITemplateHandler templateHandler;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final TemplateFlowController flowController;
    private final ThrottledTemplateWriter writer;

    private int offset;
    private boolean eventProcessingFinished;

    // This is signaled as volatile so that several threads can ask whether the processor has finished
    // avoiding visibility issues (concurrency should not be an issue because we should NEVER have more than
    // one thread executing the processor at the same time, but thread visibility could still be an issue).
    private volatile boolean allProcessingFinished;


    ThrottledTemplateProcessor(
            final TemplateSpec templateSpec,
            final IEngineContext context,
            final TemplateModel templateModel, final ITemplateHandler templateHandler,
            final ProcessorTemplateHandler processorTemplateHandler,
            final TemplateFlowController flowController,
            final ThrottledTemplateWriter writer) {
        super();
        this.identifier = Long.toString(identifierGenerator.getAndIncrement());
        this.templateSpec = templateSpec;
        this.context = context;
        this.templateModel = templateModel;
        this.templateHandler = templateHandler;
        this.processorTemplateHandler = processorTemplateHandler;
        this.flowController = flowController;
        this.writer = writer;
        this.offset = 0;
        this.eventProcessingFinished = false;
        this.allProcessingFinished = false;
    }



    public IThrottledTemplateWriterControl getThrottledTemplateWriterControl() {
        return this.writer;
    }




    public boolean isFinished() {
        return this.allProcessingFinished;
    }




    private boolean computeFinish() throws IOException {

        if (this.allProcessingFinished) {
            return true;
        }

        final boolean finished =
                this.eventProcessingFinished && !this.flowController.processorTemplateHandlerPending && !this.writer.isOverflown();
        if (finished) {
            // updating the volatile variable (which would apply a memory barrier) is avoided if unneeded
            this.allProcessingFinished = finished;
        }

        return finished;

    }


    private void reportFinish(final String outputType) {

        if (this.allProcessingFinished) {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}] Finished throttled processing of template \"{}\" with locale {}. Maximum overflow was {} {} (overflow buffer grown {} times).",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale(), Integer.valueOf(this.writer.getMaxOverflowSize()), outputType, this.writer.getOverflowGrowCount() });
            }
        }

    }




    @Override
    public String getProcessorIdentifier() {
        return this.identifier;
    }




    @Override
    public TemplateSpec getTemplateSpec() {
        return this.templateSpec;
    }




    @Override
    public int processAll(final Writer writer) {
        this.writer.setOutput(writer);
        return process(Integer.MAX_VALUE, OUTPUT_TYPE_CHARS);
    }


    @Override
    public int processAll(final OutputStream outputStream, final Charset charset) {
        this.writer.setOutput(outputStream, charset, Integer.MAX_VALUE);
        return process(Integer.MAX_VALUE, OUTPUT_TYPE_BYTES);
    }




    @Override
    public int process(final int maxOutputInChars, final Writer writer) {
        this.writer.setOutput(writer);
        return process(maxOutputInChars, OUTPUT_TYPE_CHARS);
    }


    @Override
    public int process(final int maxOutputInBytes, final OutputStream outputStream, final Charset charset) {
        this.writer.setOutput(outputStream, charset, maxOutputInBytes);
        return process(maxOutputInBytes, OUTPUT_TYPE_BYTES);
    }


    private int process(final int maxOutput, final String outputType) {

        int writtenCount = 0;
        try {

            if (this.allProcessingFinished || maxOutput == 0) {
                return 0; // No bytes written
            }

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Starting throttled process (limit:{} {}) of template \"{}\" with locale {}",
                        new Object[]{TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, this.templateSpec, this.context.getLocale()});
            }

            final long startNanos = System.nanoTime();

            // Save the initial written count so that we can know at the end how many bytes were written
            final int initialWrittenCount = this.writer.getWrittenCount();

            // Set the new limit for the writer (might provoke overflow being processed)
            this.writer.allow(maxOutput);

            // Maybe by processing all overflow we just finished
            if (!computeFinish() && !this.writer.isStopped()) {

                if (this.flowController.processorTemplateHandlerPending) {
                    this.processorTemplateHandler.handlePending();
                }

                if (!computeFinish() && !this.writer.isStopped()) {

                    this.offset += this.templateModel.process(this.templateHandler, this.offset, this.flowController);
                    if (this.offset == this.templateModel.size()) {
                        EngineContextManager.disposeEngineContext(this.context);
                        this.eventProcessingFinished = true;
                        computeFinish();
                    }

                }

            }

            final long endNanos = System.nanoTime();

            /*
             * Finally, flush the writer in order to make sure that everything has been written to output
             */
            try {
                this.writer.flush();
            } catch (final IOException e) {
                throw new TemplateOutputException("An error happened while flushing output writer", templateSpec.getTemplate(), -1, -1, e);
            }

            writtenCount = this.writer.getWrittenCount() - initialWrittenCount;

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Finished throttled process (limit:{} {}, output: {} {}) of template \"{}\" with locale {}",
                        new Object[]{TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, Integer.valueOf(writtenCount), outputType, this.templateSpec, this.context.getLocale()});
            }

            if (timerLogger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP);
                timerLogger.trace(
                        "[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED (THROTTLED, LIMIT:{} {}, OUTPUT: {} {}) IN {} nanoseconds (approx. {}ms)",
                        new Object[]{
                                TemplateEngine.threadIndex(),
                                LoggingUtils.loggifyTemplateName(this.templateSpec.getTemplate()), this.context.getLocale(), elapsed, elapsedMs,
                                this.templateSpec, this.context.getLocale(), Integer.valueOf(maxOutput), outputType, Integer.valueOf(writtenCount), outputType, elapsed, elapsedMs});
            }

        } catch (final TemplateOutputException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw e;

        } catch (final TemplateEngineException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw e;

        } catch (final Exception e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw new TemplateProcessingException("Exception processing throttled template", this.templateSpec.toString(), e);

        }

        reportFinish(outputType);

        return writtenCount;

    }




}
