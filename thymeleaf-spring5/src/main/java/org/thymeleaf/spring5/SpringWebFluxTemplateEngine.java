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
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.DataDrivenTemplateIterator;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContextFactory;
import org.thymeleaf.spring5.linkbuilder.webflux.SpringWebFluxLinkBuilder;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.SSEOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.thymeleaf.spring5.SpringWebFluxTemplateEngine.DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_BUFFER;
import static org.thymeleaf.spring5.SpringWebFluxTemplateEngine.DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_HEAD;
import static org.thymeleaf.spring5.SpringWebFluxTemplateEngine.DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_TAIL;


/**
 * <p>
 *   Standard implementation of {@link ISpringWebFluxTemplateEngine}, and default
 *   template engine implementation to be used in Spring WebFlux environments.
 * </p>
 *
 * @see ISpringWebFluxTemplateEngine
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class SpringWebFluxTemplateEngine
        extends SpringTemplateEngine
        implements ISpringWebFluxTemplateEngine {


    private static final Logger logger = LoggerFactory.getLogger(SpringWebFluxTemplateEngine.class);
    private static final String LOG_CATEGORY_FULL_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.FULL";
    private static final String LOG_CATEGORY_CHUNKED_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.CHUNKED";
    private static final String LOG_CATEGORY_DATADRIVEN_INPUT = SpringWebFluxTemplateEngine.class.getName() + ".UPSTREAM.DATA-DRIVEN";
    private static final String LOG_CATEGORY_DATADRIVEN_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.DATA-DRIVEN";




    public SpringWebFluxTemplateEngine() {

        super();
        // In Spring WebFlux environments, we will need to use a special context factory in order to
        // use an environment-tailored implementation of IEngineContext.
        this.setEngineContextFactory(new SpringWebFluxEngineContextFactory());
        // In Spring WebFlux environments, we will need to use a special link builder able to adapt
        // the creation of URLs as a result of @{...} expressions in a way that makes sense in this
        // environment.
        this.setLinkBuilder(new SpringWebFluxLinkBuilder());

    }




    @Override
    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final MediaType mediaType, final Charset charset) {
        return processStream(template, markupSelectors, context, bufferFactory, mediaType, charset, Integer.MAX_VALUE);
    }


    @Override
    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final MediaType mediaType, final Charset charset,
            final int responseMaxChunkSizeBytes) {

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
        if (mediaType == null) {
            return Flux.error(new IllegalArgumentException("Media Type cannot be null"));
        }
        if (charset == null) {
            return Flux.error(new IllegalArgumentException("Charset cannot be null"));
        }

        if (responseMaxChunkSizeBytes == 0) {
            return Flux.error(new IllegalArgumentException("Max Chunk Size cannot be zero"));
        }

        // Normalize the chunk size in bytes (MAX_VALUE == no limit)
        final int chunkSizeBytes = (responseMaxChunkSizeBytes < 0? Integer.MAX_VALUE : responseMaxChunkSizeBytes);

        // Determine whether we have been asked to return data as SSE (Server-Sent Events)
        final boolean sse =  MediaType.TEXT_EVENT_STREAM.includes(mediaType);

        /*
         * CHECK FOR DATA-DRIVEN EXECUTION
         */
        try {
            final String dataDriverVariableName = findDataDriverInModel(context);
            if (dataDriverVariableName != null) {
                // We should be executing in data-driven mode
                return createDataDrivenStream(
                        template, markupSelectors, context, dataDriverVariableName, bufferFactory, charset, chunkSizeBytes, sse);
            }
        } catch (final Throwable t) {
            return Flux.error(t);
        }

        // Check if we need to fail here: If SSE has been requested, a data-driver variable is mandatory
        if (sse) {
            return Flux.error(new TemplateProcessingException(
                    "SSE mode has been requested ('Accept: text/event-stream') but no data-driver variable has been " +
                    "added to the model/context. In order to perform SSE rendering, a variable implementing the " +
                    IReactiveDataDriverContextVariable.class.getName() + " interface is required."));
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

        final Flux<DataBuffer> stream = Flux.generate(

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
                                "[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (CHUNKED MODE, THROTTLER ID " +
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
                                "[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (CHUNKED MODE, THROTTLER ID " +
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
                                    "[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (CHUNKED MODE, THROTTLER ID " +
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
            final int responseMaxChunkSizeBytes, final boolean sse) {

        // STEP 1: Obtain the data-driver variable
        final IReactiveDataDriverContextVariable dataDriver =
                (IReactiveDataDriverContextVariable) context.getVariable(dataDriverVariableName);
        final int bufferSizeElements = dataDriver.getBufferSizeElements();


        // STEP 2: Replace the data driver variable with a DataDrivenTemplateIterator
        final DataDrivenTemplateIterator dataDrivenIterator = new DataDrivenTemplateIterator();
        final IContext wrappedContext = applyDataDriverWrapper(context, dataDriverVariableName, dataDrivenIterator);


        // STEP 3: Create the data stream buffers, plus add some logging in order to know how the stream is being used
        final Flux<List<Object>> dataDrivenBufferedStream =
                Flux.from(dataDriver.getDataStream())
                        .buffer(bufferSizeElements)
                        .log(LOG_CATEGORY_DATADRIVEN_INPUT, Level.FINEST);


        // STEP 4: Initialize the (throttled) template engine for each subscriber (normally there will only be one)
        final Flux<DataDrivenFluxStep> dataDrivenWithContextStream = Flux.using(

                // Using the throttledProcessor as state in this Flux.using allows us to delay the
                // initialization of the throttled processor until the last moment, when output generation
                // is really requested.
                () -> new CountingThrottledTemplateProcessor(processThrottled(templateName, markupSelectors, wrappedContext)),

                // This flux will be made by concatenating a phase for the head (template before data-driven
                // iteration), another phase composed of most possibly several steps for the data-driven iteration,
                // and finally a tail phase (template after data-driven iteration).
                //
                // But this concatenation will be done from a Flux created with Flux.generate, so that we have the
                // opportunity to check if the processor has already signaled that it has finished, and in such
                // case we might be able to avoid the subscription to the upstream data driver if its iteration is
                // not needed at the template.
                throttledProcessor -> Flux.concat(Flux.generate(
                        () -> DATA_DRIVEN_PHASE_HEAD,
                        (phase, emitter) -> {

                            // Check if the processor has already signaled it has finished, in which case we
                            // might be able to avoid the BUFFER phase (if no iteration of the data-driver is present).
                            //
                            // *NOTE* we CANNOT GUARANTEE that this will stop the upstream data driver publisher from
                            // being subscribed to or even consumed, because there is no guarantee that this code
                            // will be executed for the BUFFER phase after the entire Flux generated downstream
                            // for the HEAD phase (see STEP 5 in the stream being built). Actually, it might even be
                            // executed concurrently to one of the steps of a Flux for the HEAD/BUFFER phases, which
                            // is why the IThrottledProcessor.isFinished() called here needs to be thread-safe.
                            if (throttledProcessor.isFinished()) {
                                // We can short-cut, and if we are lucky even avoid the BUFFER phase.
                                emitter.complete();
                                return null;
                            }

                            switch (phase) {

                                case DATA_DRIVEN_PHASE_HEAD:
                                    emitter.next(Mono.just(DataDrivenFluxStep.forHead(throttledProcessor)));
                                    return DATA_DRIVEN_PHASE_BUFFER;

                                case DATA_DRIVEN_PHASE_BUFFER:
                                    if (!sse || bufferSizeElements == 1) {
                                        // Not doing SSE, or doing SSE with buffer size = 1, so we just create
                                        // one step per list of values
                                        emitter.next(dataDrivenBufferedStream.map(values -> DataDrivenFluxStep.forBuffer(throttledProcessor, values)));
                                    } else {
                                        // Doing SSE with buffer size > 1. We will need to partition our buffers
                                        // because SSE requires one output buffer per element (incl. some metadata)
                                        emitter.next(dataDrivenBufferedStream.concatMap(
                                                values -> Flux.fromIterable(values).map(value -> DataDrivenFluxStep.forBuffer(throttledProcessor, Collections.singletonList(value)))));
                                    }
                                    return DATA_DRIVEN_PHASE_TAIL;

                                case DATA_DRIVEN_PHASE_TAIL:
                                    emitter.next(Mono.just(DataDrivenFluxStep.forTail(throttledProcessor)));
                                    emitter.complete();

                            }

                            return null;

                        }
                )),

                // No need to explicitly dispose the throttled template processor.
                throttledProcessor -> { /* Nothing to be done here! */ });


        // STEP 5: React to each buffer of published data by creating one or many (concatMap) DataBuffers containing
        //         the result of processing only that buffer.
        final Flux<DataBuffer> stream = dataDrivenWithContextStream.concatMap(
                (step) -> Flux.generate(

                        // We set initialize to TRUE as a state, so that the first step executed for this Flux
                        // performs the initialization of the dataDrivenIterator for the entire Flux. It is a need
                        // that this initialization is performed when the first step of this Flux is executed,
                        // because initialization actually consists of a lateral effect on a mutable variable
                        // (the dataDrivenIterator). And this way we are certain that it is executed in the
                        // right order, given concatMap guarantees to us that these Fluxes generated here will
                        // be consumed in the right order and executed one at a time (and the Reactor guarantees us
                        // that there will be no thread visibility issues between Flux steps).
                        () -> Boolean.TRUE,

                        // The first time this is executed, initialize will be TRUE. From then on, it will be FALSE
                        // so that it is the first execution of this that initializes the (mutable) dataDrivenIterator.
                        (initialize, emitter) -> {

                            final CountingThrottledTemplateProcessor throttledProcessor = step.getThrottledProcessor();

                            // Let's check if we can short cut and simply finish execution. Maybe we can avoid consuming
                            // the data from the upstream data-driver publisher (e.g. if the data-driver variable is
                            // never actually iterated).
                            if (throttledProcessor.isFinished()) {
                                emitter.complete();
                                return Boolean.FALSE;
                            }

                            // Initialize the dataDrivenIterator. This is a lateral effect, this variable is mutable,
                            // so it is important to do it here so that we make sure it is executed in the right order.
                            if (initialize.booleanValue()) {

                                if (step.isHead()) {
                                    // Feed with no elements - we just want to output the part of the
                                    // template that goes before the iteration of the data driver.
                                    dataDrivenIterator.feedBuffer(Collections.emptyList());
                                } else if (step.isDataBuffer()) {
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

                            }

                            // Signal the start of a new chunk (we are counting them for the logs)
                            throttledProcessor.startChunk();

                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        "[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID " +
                                                "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}",
                                        new Object[]{
                                                TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                LoggingUtils.loggifyTemplateName(templateName), context.getLocale()});
                            }

                            // If we are doing SSE, buffer size limit will not be applied. Each buffer will contain one
                            // element plus its corresponding SSE metadata.
                            final int chunkSizeBytes = (sse? Integer.MAX_VALUE : responseMaxChunkSizeBytes);

                            final DataBuffer buffer =
                                    (chunkSizeBytes != Integer.MAX_VALUE ?
                                            bufferFactory.allocateBuffer(chunkSizeBytes) :
                                            bufferFactory.allocateBuffer());

                            final int bytesProduced;
                            try {

                                OutputStream outputStream = buffer.asOutputStream();

                                // If SSE, create the output stream that will add the metadata and start the event
                                if (sse) {
                                    outputStream = new SSEOutputStream(outputStream, charset);
                                    final String eventType = step.isHead()? "head" : step.isTail()? "tail" : "data";
                                    ((SSEOutputStream)outputStream).startEvent(null, eventType);
                                }

                                bytesProduced =
                                        throttledProcessor.process(chunkSizeBytes, outputStream, charset);

                                // If SSE, finish the event properly
                                if (sse) {
                                    ((SSEOutputStream)outputStream).endEvent();
                                }

                            } catch (final Throwable t) {
                                emitter.error(t);
                                return Boolean.FALSE;
                            }

                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        "[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID " +
                                                "\"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES",
                                        new Object[]{
                                                TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(),
                                                throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()),
                                                LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced)});
                            }

                            // Buffer created, send it to the output channels
                            if (sse && bytesProduced == 0) {
                                // This avoids sending empty events to the client
                                emitter.next(bufferFactory.allocateBuffer(0));
                            } else {
                                emitter.next(buffer);
                            }

                            // Now it's time to determine if we should execute another time for the same
                            // data-driven step or rather we should consider we have done everything possible
                            // for this step (e.g. produced all markup for a data stream buffer) and just
                            // emit "complete" and go for the next step.
                            if (throttledProcessor.isFinished()) {

                                if (logger.isTraceEnabled()) {
                                    logger.trace(
                                            "[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID " +
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

                                if (step.isHead() && dataDrivenIterator.hasBeenQueried()) {

                                    // We know everything before the data driven iteration has already been
                                    // processed because the iterator has been used at least once (i.e. its
                                    // 'hasNext()' or 'next()' method have been called at least once).
                                    emitter.complete();

                                } else if (step.isDataBuffer() && !dataDrivenIterator.continueBufferExecution()) {
                                    // We have finished executing this buffer of items and we can go for the
                                    // next one or maybe the tail.
                                    emitter.complete();
                                }
                                // fluxStep.isTail(): nothing to do, as the only reason we would have to emit
                                // 'complete' at the tail step would be throttledProcessor.isFinished(), which
                                // has been already checked.

                            }

                            return Boolean.FALSE;

                        }));


        // Will add some logging to the data flow
        return stream.log(LOG_CATEGORY_DATADRIVEN_OUTPUT, Level.FINEST);

    }




    /*
     * This method will apply a wrapper on the data driver variable so that a DataDrivenTemplateIterator takes
     * the place of the original data-driver variable. This is done via a wrapper in order to not perform such a
     * strong modification on the original context object. Even if context objects should not be reused among template
     * engine executions, when a non-IEngineContext implementation is used we will let that degree of liberty to the
     * user just in case.
     */
    private static IContext applyDataDriverWrapper(
            final IContext context, final String dataDriverVariableName,
            final DataDrivenTemplateIterator dataDrivenTemplateIterator) {

        // This is an IEngineContext, a very internal, low-level context implementation, so let's simply modify it
        if (context instanceof IEngineContext) {
            ((IEngineContext)context).setVariable(dataDriverVariableName, dataDrivenTemplateIterator);
            return context;
        }

        // Not an IEngineContext, but might still be an ISpringWebFluxContext and we don't want to lose that info
        if (context instanceof ISpringWebFluxContext) {
            return new DataDrivenSpringWebFluxContextWrapper(
                    (ISpringWebFluxContext)context, dataDriverVariableName, dataDrivenTemplateIterator);
        }

        // Not a recognized context interface: just use a default implementation
        return new DataDrivenContextWrapper(context, dataDriverVariableName, dataDrivenTemplateIterator);


    }




    private static String findDataDriverInModel(final IContext context) {

        // In SpringWebFluxExpressionContext (used most of the times), variables are backed by a
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
     * This internal class is used for keeping the accounting of the different phases in a data-driven stream:
     * head (no value, template before the data-driven iteration), buffer (values, data-driven iteration), and
     * tail (no value, template after the data-driven iteration).
     *
     * NOTE there is no need to synchronize these variables, even if different steps/chunks might be executed
     * (non-concurrently) by different threads, because Reactive Streams implementations like Reactor should
     * take care to establish the adequate thread synchronization/memory barriers at their asynchronous boundaries,
     * thus avoiding thread visibility issues.
     */
    static final class DataDrivenFluxStep {

        enum FluxStepPhase {DATA_DRIVEN_PHASE_HEAD, DATA_DRIVEN_PHASE_BUFFER, DATA_DRIVEN_PHASE_TAIL }

        private final CountingThrottledTemplateProcessor throttledProcessor;
        private final List<Object> values;
        private final FluxStepPhase phase;


        static DataDrivenFluxStep forHead(final CountingThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, DATA_DRIVEN_PHASE_HEAD);
        }

        static DataDrivenFluxStep forBuffer(final CountingThrottledTemplateProcessor throttledProcessor, final List<Object> values) {
            return new DataDrivenFluxStep(throttledProcessor, values, DATA_DRIVEN_PHASE_BUFFER);
        }

        static DataDrivenFluxStep forTail(final CountingThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, DATA_DRIVEN_PHASE_TAIL);
        }

        private DataDrivenFluxStep(
                final CountingThrottledTemplateProcessor throttledProcessor, final List<Object> values,
                final FluxStepPhase phase) {
            super();
            this.throttledProcessor = throttledProcessor;
            this.values = values;
            this.phase = phase;
        }

        CountingThrottledTemplateProcessor getThrottledProcessor() {
            return this.throttledProcessor;
        }

        List<Object> getValues() {
            return this.values;
        }

        boolean isHead() {
            return this.phase == DATA_DRIVEN_PHASE_HEAD;
        }

        boolean isDataBuffer() {
            return this.phase == DATA_DRIVEN_PHASE_BUFFER;
        }

        boolean isTail() {
            return this.phase == DATA_DRIVEN_PHASE_TAIL;
        }

    }



    /*
     * This wrapper of an ISpringWebFluxContext is meant to wrap the original context object sent to the
     * template engine while hiding the data driver variable, returning a DataDrivenTemplateIterator in its place.
     */
    static class DataDrivenSpringWebFluxContextWrapper
            extends DataDrivenContextWrapper implements ISpringWebFluxContext {

        private final ISpringWebFluxContext context;

        DataDrivenSpringWebFluxContextWrapper(
                final ISpringWebFluxContext context, final String dataDriverVariableName,
                final DataDrivenTemplateIterator dataDrivenTemplateIterator) {
            super(context, dataDriverVariableName, dataDrivenTemplateIterator);
            this.context = context;
        }

        @Override
        public ServerHttpRequest getRequest() {
            return this.context.getRequest();
        }

        @Override
        public ServerHttpResponse getResponse() {
            return this.context.getResponse();
        }

        @Override
        public Mono<WebSession> getSession() {
            return this.context.getSession();
        }

        @Override
        public ServerWebExchange getExchange() {
            return this.context.getExchange();
        }

    }



    /*
     * This wrapper of an IContext (non-SpringWebFlux-specific) is meant to wrap the original context object sent
     * to the template engine while hiding the data driver variable, returning a DataDrivenTemplateIterator in
     * its place.
     */
    static class DataDrivenContextWrapper implements IContext {

        private final IContext context;
        private final String dataDriverVariableName;
        private final DataDrivenTemplateIterator dataDrivenTemplateIterator;

        DataDrivenContextWrapper(
                final IContext context, final String dataDriverVariableName,
                final DataDrivenTemplateIterator dataDrivenTemplateIterator) {
            super();
            this.context = context;
            this.dataDriverVariableName = dataDriverVariableName;
            this.dataDrivenTemplateIterator = dataDrivenTemplateIterator;
        }

        public IContext getWrappedContext() {
            return this.context;
        }

        @Override
        public Locale getLocale() {
            return this.context.getLocale();
        }

        @Override
        public boolean containsVariable(final String name) {
            return this.context.containsVariable(name);
        }

        @Override
        public Set<String> getVariableNames() {
            return this.context.getVariableNames();
        }

        @Override
        public Object getVariable(final String name) {
            if (this.dataDriverVariableName.equals(name)) {
                return this.dataDrivenTemplateIterator;
            }
            return this.context.getVariable(name);
        }

    }



}
