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
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;

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
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class ThrottledTemplateProcessor implements IThrottledTemplateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TemplateEngine.TIMER_LOGGER_NAME);

    private static final int NANOS_IN_SECOND = 1000000;

    private static final String OUTPUT_TYPE_CHARS = "chars";
    private static final String OUTPUT_TYPE_BYTES = "bytes";


    private final TemplateSpec templateSpec;
    private final IEngineContext context;
    private final TemplateModel templateModel;
    private final ITemplateHandler templateHandler;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final TemplateFlowController flowController;
    private final ThrottledTemplateWriter writer;

    private int offset;
    private boolean eventProcessingFinished;
    private boolean allProcessingFinished;


    public ThrottledTemplateProcessor(
            final TemplateSpec templateSpec,
            final IEngineContext context,
            final TemplateModel templateModel, final ITemplateHandler templateHandler,
            final ProcessorTemplateHandler processorTemplateHandler,
            final TemplateFlowController flowController,
            final ThrottledTemplateWriter writer) {
        super();
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






    public boolean isFinished() {
        return this.allProcessingFinished;
    }




    private boolean computeFinish() {

        if (this.allProcessingFinished) {
            return true;
        }

        this.allProcessingFinished =
                this.eventProcessingFinished && !this.flowController.processorTemplateHandlerPending && !this.writer.isOverflown();

        return this.allProcessingFinished;

    }


    private void reportFinish(final String outputType) {

        if (this.allProcessingFinished) {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}] FINISHED OUTPUT OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}. MAXIMUM OVERFLOW WAS {} {}.",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale(), Integer.valueOf(this.writer.getMaxOverflowSize()), outputType });
            }
        }

    }




    public void processAll(final Writer writer) {
        this.writer.setOutput(writer);
        processAll(OUTPUT_TYPE_CHARS);
    }


    public void processAll(final OutputStream outputStream, final Charset charset) {
        this.writer.setOutput(outputStream, charset);
        processAll(OUTPUT_TYPE_BYTES);
    }



    private void processAll(final String outputType) {

        try {

            if (this.allProcessingFinished) {
                return;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING PROCESS-ALL OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale()});
            }

            final long startNanos = System.nanoTime();

            // Process all overflow and remove the limit
            this.writer.allow(-1);

            // Maybe by processing all overflow we just finished
            if (!computeFinish()) {

                this.offset += this.templateModel.process(this.templateHandler, this.offset, this.flowController);
                EngineContextManager.disposeEngineContext(this.context);
                this.eventProcessingFinished = true;

                computeFinish();

            }

            final long endNanos = System.nanoTime();

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] FINISHED PROCESS-ALL OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale()});
            }

            if (timerLogger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP);
                timerLogger.trace(
                        "[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED (THROTTLED, PROCESS-ALL) IN {} nanoseconds (approx. {}ms)",
                        new Object[]{
                                TemplateEngine.threadIndex(),
                                LoggingUtils.loggifyTemplateName(this.templateSpec.getTemplate()), this.context.getLocale(), elapsed, elapsedMs,
                                this.templateSpec, this.context.getLocale(), elapsed, elapsedMs});
            }

            /*
             * Finally, flush the writer in order to make sure that everything has been written to output
             */
            try {
                this.writer.flush();
            } catch (final IOException e) {
                throw new TemplateOutputException("An error happened while flushing output writer", templateSpec.getTemplate(), -1, -1, e);
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

        } catch (final RuntimeException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw new TemplateProcessingException("Exception processing throttled template", this.templateSpec.toString(), e);

        }

        reportFinish(outputType);

    }




    public void process(final int maxOutputInChars, final Writer writer) {
        this.writer.setOutput(writer);
        process(maxOutputInChars, OUTPUT_TYPE_CHARS);
    }


    public void process(final int maxOutputInChars, final OutputStream outputStream, final Charset charset) {
        this.writer.setOutput(outputStream, charset);
        process(maxOutputInChars, OUTPUT_TYPE_BYTES);
    }


    private void process(final int maxOutput, final String outputType) {

        try {

            if (maxOutput < 0 || maxOutput == Integer.MAX_VALUE) {
                processAll(outputType);
                return;
            }

            if (this.allProcessingFinished || maxOutput == 0) {
                return;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING PROCESS(LIMIT:{} {}) OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, this.templateSpec, this.context.getLocale()});
            }

            final long startNanos = System.nanoTime();

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

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] FINISHED PROCESS(LIMIT:{} {}) OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, this.templateSpec, this.context.getLocale()});
            }

            if (timerLogger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP);
                timerLogger.trace(
                        "[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED (THROTTLED, LIMIT:{} {}) IN {} nanoseconds (approx. {}ms)",
                        new Object[]{
                                TemplateEngine.threadIndex(),
                                LoggingUtils.loggifyTemplateName(this.templateSpec.getTemplate()), this.context.getLocale(), elapsed, elapsedMs,
                                this.templateSpec, this.context.getLocale(), Integer.valueOf(maxOutput), outputType, elapsed, elapsedMs});
            }

            /*
             * Finally, flush the writer in order to make sure that everything has been written to output
             */
            try {
                this.writer.flush();
            } catch (final IOException e) {
                throw new TemplateOutputException("An error happened while flushing output writer", templateSpec.getTemplate(), -1, -1, e);
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

        } catch (final RuntimeException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw new TemplateProcessingException("Exception processing throttled template", this.templateSpec.toString(), e);

        }

        reportFinish(outputType);

    }




}
