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
package org.thymeleaf.spring5;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.DataDrivenTemplateIterator;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.reactive.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.reactive.SpringWebReactiveEngineContextFactory;
import org.thymeleaf.spring5.linkbuilder.reactive.SpringWebReactiveLinkBuilder;
import org.thymeleaf.util.LoggingUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class SpringWebReactiveTemplateEngine
        extends SpringTemplateEngine
        implements ISpringWebReactiveTemplateEngine {


    private static final Logger logger = LoggerFactory.getLogger(SpringWebReactiveTemplateEngine.class);
    private static final String LOG_CATEGORY_FULL_OUTPUT = SpringWebReactiveTemplateEngine.class.getName() + ".OUTPUT.FULL";
    private static final String LOG_CATEGORY_CHUNKED_OUTPUT = SpringWebReactiveTemplateEngine.class.getName() + ".OUTPUT.CHUNKED";
    private static final String LOG_CATEGORY_DATADRIVEN_INPUT = SpringWebReactiveTemplateEngine.class.getName() + ".INPUT.DATADRIVEN";
    private static final String LOG_CATEGORY_DATADRIVEN_OUTPUT = SpringWebReactiveTemplateEngine.class.getName() + ".OUTPUT.DATADRIVEN";




    public SpringWebReactiveTemplateEngine() {

        super();
        // In Spring Web Reactive environments, we will need to use a special context factory in order to
        // use an environment-tailored implementation of IEngineContext.
        this.setEngineContextFactory(new SpringWebReactiveEngineContextFactory());
        // In Spring Web Reactive environments, we will need to use a special link builder able to adapt
        // the creation of URLs as a result of @{...} expressions in a way that makes sense in this
        // environment.
        this.setLinkBuilder(new SpringWebReactiveLinkBuilder());

    }




    @Override
    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final Charset charset) {
        return processStream(template, markupSelectors, context, bufferFactory, charset, Integer.MAX_VALUE);
    }


    @Override
    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final Charset charset, final int responseMaxChunkSizeBytes) {

        /*
         * PERFORM VALIDATIONS
         */
        if (template == null) {
            return Flux.error(new IllegalArgumentException("Template cannot be null"));
        }
        if (context == null) {
            return Flux.error(new IllegalArgumentException("Context cannot be null"));
        }
        if (bufferFactory == null) {
            return Flux.error(new IllegalArgumentException("Buffer Factory cannot be null"));
        }
        if (charset == null) {
            return Flux.error(new IllegalArgumentException("Charset cannot be null"));
        }

        if (responseMaxChunkSizeBytes == 0) {
            return Flux.error(new IllegalArgumentException("Max Chunk Size cannot be zero"));
        }

        // Normalize the chunk size in bytes (MAX_VALUE == no limit)
        final int chunkSizeBytes = (responseMaxChunkSizeBytes < 0? Integer.MAX_VALUE : responseMaxChunkSizeBytes);

        /*
         * CHECK FOR DATA-DRIVEN EXECUTION
         */
        try {
            final String dataDriverVariableName = findDataDriverInModel(context);
            if (dataDriverVariableName != null) {
                // We should be executing in data-driven mode
                return createDataDrivenStream(
                        template, markupSelectors, context, dataDriverVariableName, bufferFactory, charset, chunkSizeBytes);
            }
        } catch (final Throwable t) {
            return Flux.error(t);
        }

        /*
         * IS THERE A LIMIT IN BUFFER SIZE? if not, given we are not data-driven, we should switch to FULL
         */
        if (chunkSizeBytes == Integer.MAX_VALUE) {
            // No limit on buffer size, so there is no reason to throttle: using FULL mode instead.
            return createFullStream(template, markupSelectors, context, bufferFactory, charset);
        }

        /*
         * CREATE A CHUNKED STREAM
         */
        return createChunkedStream(
                template, markupSelectors, context, bufferFactory, charset, responseMaxChunkSizeBytes);

    }




    private Mono<DataBuffer> createFullStream(
            final String templateName, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final Charset charset) {

        final Mono<DataBuffer> stream =
                Mono.create(
                        subscriber -> {

                            if (logger.isTraceEnabled()) {
                                logger.trace("[THYMELEAF][{}] STARTING STREAM PROCESS (FULL MODE) OF TEMPLATE \"{}\" WITH LOCALE {}",
                                        new Object[]{TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName), context.getLocale()});
                            }

                            final DataBuffer dataBuffer = bufferFactory.allocateBuffer();
                            // OutputStreamWriter object have an 8K buffer, but process(...) will flush it at the end
                            final OutputStreamWriter writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);

                            try {

                                process(templateName, markupSelectors, context, writer);

                            } catch (final Throwable t) {
                                logger.error(
                                        String.format(
                                                "[THYMELEAF][%s] Exception processing template \"%s\": %s",
                                                new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName), t.getMessage()}),
                                        t);
                                subscriber.error(t);
                                return;
                            }

                            final int bytesProduced = dataBuffer.readableByteCount();

                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        "[THYMELEAF][{}] FINISHED STREAM PROCESS (FULL MODE) OF TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES",
                                        new Object[]{
                                                TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName),
                                                context.getLocale(), Integer.valueOf(bytesProduced)});
                            }

                            // This is a Mono<?>, so no need to call "next()" or "complete()"
                            subscriber.success(dataBuffer);

                        });

        // Will add some logging to the data stream
        return stream.log(LOG_CATEGORY_FULL_OUTPUT, Level.FINEST);

    }




    private Flux<DataBuffer> createChunkedStream(
            final String templateName, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final Charset charset, final int responseMaxChunkSizeBytes) {

        final Flux<DataBuffer> stream =
                Flux.generate(

                        // Using the throttledProcessor as state in this Flux.generate allows us to delay the
                        // initialization of the throttled processor until the last moment, when output generation
                        // is really requested.
                        () -> new CountingThrottledTemplateProcessor(processThrottled(templateName, markupSelectors, context)),

                        // This stream will execute, in a one-by-one (non-interleaved) fashion, the following code
                        // for each back-pressure request coming from downstream. Each of these steps (chunks) will
                        // execute the throttled processor once and return its result as a DataBuffer object.
                        (throttledProcessor, emitter) -> {

                            throttledProcessor.startChunk();

                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        "[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (CHUNKED MODE, IDENTIFIER " +
                                                "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}",
                                        new Object[]{
                                                TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                LoggingUtils.loggifyTemplateName(templateName), context.getLocale()});
                            }

                            final DataBuffer buffer = bufferFactory.allocateBuffer(responseMaxChunkSizeBytes);

                            final int bytesProduced;
                            try {
                                bytesProduced =
                                        throttledProcessor.process(responseMaxChunkSizeBytes, buffer.asOutputStream(), charset);
                            } catch (final Throwable t) {
                                emitter.error(t);
                                return null;
                            }

                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        "[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (CHUNKED MODE, IDENTIFIER " +
                                                "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES",
                                        new Object[]{
                                                TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced)});
                            }

                            emitter.next(buffer);

                            if (throttledProcessor.isFinished()) {

                                if (logger.isTraceEnabled()) {
                                    logger.trace(
                                            "[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (CHUNKED MODE, IDENTIFIER " +
                                                    "\"{}\") FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED A TOTAL OF {} BYTES IN {} CHUNKS",
                                            new Object[]{
                                                    TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                    throttledProcessor.getProcessorIdentifier(),
                                                    LoggingUtils.loggifyTemplateName(templateName), context.getLocale(),
                                                    Long.valueOf(throttledProcessor.getTotalBytesProduced()),
                                                    Integer.valueOf(throttledProcessor.getChunkCount() + 1)});
                                }

                                emitter.complete();

                            }

                            return throttledProcessor;

                        });

        // Will add some logging to the data stream
        return stream.log(LOG_CATEGORY_CHUNKED_OUTPUT, Level.FINEST);

    }




    private Flux<DataBuffer> createDataDrivenStream(
            final String templateName, final Set<String> markupSelectors, final IContext context,
            final String dataDriverVariableName, final DataBufferFactory bufferFactory, final Charset charset,
            final int responseMaxChunkSizeBytes) {

        // STEP 1: Obtain the data-driver variable
        final IReactiveDataDriverContextVariable dataDriver =
                (IReactiveDataDriverContextVariable) context.getVariable(dataDriverVariableName);

        // STEP 2: Replace the data driver variable with a DataDrivenTemplateIterator
        final DataDrivenTemplateIterator dataDrivenIterator = new DataDrivenTemplateIterator();
        if (context instanceof AbstractContext) {
            ((AbstractContext)context).setVariable(dataDriverVariableName, dataDrivenIterator);
        } else if (context instanceof IEngineContext) {
            ((IEngineContext)context).setVariable(dataDriverVariableName, dataDrivenIterator);
        } else {
            final Exception e =
                    new IllegalArgumentException(
                            "In order to execute in Data-Driven mode, context must be of a " +
                            "known mutable implementation: it should either extend " +
                            AbstractContext.class.getName() + " or implement the " +
                            IEngineContext.class.getName() + " interface");
            return Flux.error(e);
        }

        // STEP 3: Create the data stream buffers, plus add some logging in order to know how the stream is being used
        final Flux<List<Object>> dataDrivenBufferedStream =
                Flux.from(dataDriver.getDataStream())
                        .buffer(dataDriver.getBufferSizeElements())
                        .log(LOG_CATEGORY_DATADRIVEN_INPUT, Level.FINEST);

        // STEP 4: Initialize the (throttled) template engine for each subscriber (normally there will only be one)
        final Flux<DataDrivenFluxStep> dataDrivenWithContextStream =
                Flux.using(

                        // Using the throttledProcessor as state in this Flux.using allows us to delay the
                        // initialization of the throttled processor until the last moment, when output generation
                        // is really requested.
                        () -> new CountingThrottledTemplateProcessor(processThrottled(templateName, markupSelectors, context)),

                        // This flux will be made by concatenating a step for the head (template before data-driven
                        // iteration), several steps for the data-driven iteration, and finally a tail (template
                        // after data-driven iteration).
                        throttledProcessor ->
                                Flux.concat(
                                        Mono.just(DataDrivenFluxStep.forHead(throttledProcessor)),
                                        dataDrivenBufferedStream.map(values -> DataDrivenFluxStep.forBuffer(throttledProcessor, values)),
                                        Mono.just(DataDrivenFluxStep.forTail(throttledProcessor))
                                ),

                        // No need to explicitly dispose the throttled template processor.
                        throttledProcessor -> { /* Nothing to be done here! */ });

        // STEP 5: React to each buffer of published data by creating one or many (concatMap) DataBuffers containing
        //         the result of processing only that buffer.
        final Flux<DataBuffer> stream =
                dataDrivenWithContextStream.concatMap(
                        step -> Flux.generate(
                                () -> {
                                    if (step.isHead()) {
                                        // Feed with no elements - we just want to output the part of the
                                        // template that goes before the iteration of the data driver.
                                        dataDrivenIterator.feedBuffer(Collections.emptyList());
                                    } else if (step.isDataBuffer()){
                                        // Value-based execution: we have values and we want to iterate them
                                        dataDrivenIterator.feedBuffer(step.getValues());
                                    } else { // step.isTail()
                                        // Signal feeding complete, indicating this is just meant to output the
                                        // rest of the template after the iteration of the data driver. Note there
                                        // is a case when this phase will still provoke the output of an iteration,
                                        // and this is when the number of iterations is exactly ONE. In this case,
                                        // it won't be possible to determine the iteration type (ZERO, ONE, MULTIPLE)
                                        // until we close it with this 'feedingComplete()'
                                        dataDrivenIterator.feedingComplete();
                                    }
                                    return step;
                                },
                                (fluxStep, emitter) -> {

                                    final CountingThrottledTemplateProcessor throttledProcessor = fluxStep.getThrottledProcessor();

                                    throttledProcessor.startChunk();

                                    if (logger.isTraceEnabled()) {
                                        logger.trace(
                                                "[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, IDENTIFIER " +
                                                        "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}",
                                                new Object[]{
                                                        TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                        throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                        LoggingUtils.loggifyTemplateName(templateName), context.getLocale()});
                                    }

                                    final DataBuffer buffer =
                                            (responseMaxChunkSizeBytes != Integer.MAX_VALUE?
                                                    bufferFactory.allocateBuffer(responseMaxChunkSizeBytes) :
                                                    bufferFactory.allocateBuffer());

                                    final int bytesProduced;
                                    try {
                                        bytesProduced =
                                            throttledProcessor.process(responseMaxChunkSizeBytes, buffer.asOutputStream(), charset);
                                    } catch (final Throwable t) {
                                        emitter.error(t);
                                        return null;
                                    }

                                    if (logger.isTraceEnabled()) {
                                        logger.trace(
                                                "[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, IDENTIFIER " +
                                                        "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES",
                                                new Object[]{
                                                        TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                        throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                        LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced)});
                                    }

                                    // Buffer created, send it to the output channels
                                    emitter.next(buffer);

                                    // Now it's time to determine if we should execute another time for the same
                                    // data-driven step or rather we should consider we have done everything possible
                                    // for this step (e.g. produced all markup for a data stream buffer) and just
                                    // emit "complete" and go for the next step.
                                    if (throttledProcessor.isFinished()) {

                                        if (logger.isTraceEnabled()) {
                                            logger.trace(
                                                    "[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (DATA-DRIVEN MODE, IDENTIFIER " +
                                                            "\"{}\") FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED A TOTAL OF {} BYTES IN {} CHUNKS",
                                                    new Object[]{
                                                            TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                            throttledProcessor.getProcessorIdentifier(),
                                                            LoggingUtils.loggifyTemplateName(templateName), context.getLocale(),
                                                            Long.valueOf(throttledProcessor.getTotalBytesProduced()),
                                                            Integer.valueOf(throttledProcessor.getChunkCount() + 1)});
                                        }

                                        // We have finished executing the template, which can happen after
                                        // finishing iterating all data driver values, or also if we are at the
                                        // first execution and there was no need to use the data driver at all
                                        emitter.complete();

                                    } else {

                                        if (fluxStep.isHead() && dataDrivenIterator.hasBeenQueried()) {

                                            // We know everything before the data driven iteration has already been
                                            // processed because the iterator has been used at least once (i.e. its
                                            // 'hasNext()' or 'next()' method have been called at least once).
                                            emitter.complete();

                                        } else if (fluxStep.isDataBuffer() && !dataDrivenIterator.continueBufferExecution()) {
                                            // We have finished executing this buffer of items and we can go for the
                                            // next one or maybe the tail.
                                            emitter.complete();
                                        }
                                        // fluxStep.isTail(): nothing to do, as the only reason we would have to emit
                                        // 'complete' at the tail step would be throttledProcessor.isFinished(), which
                                        // has been already checked.

                                    }

                                    return fluxStep;

                                })

                );

        // Will add some logging to the data flow
        return stream.log(LOG_CATEGORY_DATADRIVEN_OUTPUT, Level.FINEST);

    }




    private static String findDataDriverInModel(final IContext context) {

        // In SpringWebReactiveExpressionContext (used most of the times), variables are backed by a
        // Map<String,Object>. So this iteration on all the names and many "getVariable()" calls
        // shouldn't be an issue perf-wise.

        String dataDriverVariableName = null;
        final Set<String> contextVariableNames = context.getVariableNames();

        for (final String contextVariableName : contextVariableNames) {

            final Object contextVariableValue = context.getVariable(contextVariableName);
            if (contextVariableValue instanceof IReactiveDataDriverContextVariable) {
                if (dataDriverVariableName != null) {
                    throw new TemplateProcessingException(
                            "Only one data-driver variable is allowed to be specified as a model attribute, but " +
                            "at least two have been identified: '" + dataDriverVariableName + "' " +
                            "and '" + contextVariableName + "'");
                }
                dataDriverVariableName = contextVariableName;
            }

        }

        return dataDriverVariableName;

    }





    /*
     * This internal class is meant to be used in multi-step streams so that an account on the total
     * number of bytes and steps/chunks can be kept.
     *
     * NOTE there is no need to synchronize these variables, even if different steps/chunks might be executed
     * (non-concurrently) by different threads, because Reactive Streams implementations like Reactor should
     * take care to establish the adequate thread synchronization/memory barriers at their asynchronous boundaries,
     * thus avoiding thread visibility issues.
     */
    static class CountingThrottledTemplateProcessor {

        private final IThrottledTemplateProcessor throttledProcessor;
        private int chunkCount;
        private long totalBytesProduced;

        CountingThrottledTemplateProcessor(final IThrottledTemplateProcessor throttledProcessor) {
            super();
            this.throttledProcessor = throttledProcessor;
            this.chunkCount = -1; // First chunk will be considered number 0
            this.totalBytesProduced = 0L;
        }

        int process(final int maxOutputInBytes, final OutputStream outputStream, final Charset charset) {
            final int chunkBytes = this.throttledProcessor.process(maxOutputInBytes, outputStream, charset);
            this.totalBytesProduced += chunkBytes;
            return chunkBytes;
        }

        String getProcessorIdentifier() {
            return this.throttledProcessor.getProcessorIdentifier();
        }

        boolean isFinished() {
            return this.throttledProcessor.isFinished();
        }

        void startChunk() {
            this.chunkCount++;
        }

        int getChunkCount() {
            return this.chunkCount;
        }

        long getTotalBytesProduced() {
            return this.totalBytesProduced;
        }

    }


    /*
     * This internal class is used for keeping the accounting of the different steps in a data-driven stream:
     * head (no value, template before the data-driven iteration), buffer (values, data-driven iteration), and
     * tail (no value, template after the data-driven iteration).
     *
     * NOTE there is no need to synchronize these variables, even if different steps/chunks might be executed
     * (non-concurrently) by different threads, because Reactive Streams implementations like Reactor should
     * take care to establish the adequate thread synchronization/memory barriers at their asynchronous boundaries,
     * thus avoiding thread visibility issues.
     */
    static final class DataDrivenFluxStep {

        private final CountingThrottledTemplateProcessor throttledProcessor;
        private final List<Object> values;
        private final boolean head;
        private final boolean tail;


        static DataDrivenFluxStep forHead(final CountingThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, true, false);
        }

        static DataDrivenFluxStep forBuffer(final CountingThrottledTemplateProcessor throttledProcessor, final List<Object> values) {
            return new DataDrivenFluxStep(throttledProcessor, values, false, false);
        }

        static DataDrivenFluxStep forTail(final CountingThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, false, true);
        }

        private DataDrivenFluxStep(
                final CountingThrottledTemplateProcessor throttledProcessor, final List<Object> values,
                final boolean head, final boolean tail) {
            super();
            this.throttledProcessor = throttledProcessor;
            this.values = values;
            this.head = head;
            this.tail = tail;
        }

        CountingThrottledTemplateProcessor getThrottledProcessor() {
            return this.throttledProcessor;
        }

        List<Object> getValues() {
            return this.values;
        }

        boolean isHead() {
            return this.head;
        }

        boolean isDataBuffer() {
            return this.values != null;
        }

        boolean isTail() {
            return this.tail;
        }

    }



}
