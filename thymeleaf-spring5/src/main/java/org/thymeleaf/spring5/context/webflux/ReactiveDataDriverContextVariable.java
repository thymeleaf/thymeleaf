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
package org.thymeleaf.spring5.context.webflux;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;

/**
 * <p>
 *   Basic implementation of the {@link IReactiveDataDriverContextVariable} interface, including also
 *   the extensions specified in {@link IReactiveSSEDataDriverContextVariable}.
 * </p>
 * <p>
 *   The <em>reactive data stream</em> wrapped by this class will usually have the shape of an implementation of the
 *   {@link Publisher} interface, such as {@link reactor.core.publisher.Flux}. But other types of reactive
 *   artifacts are supported thanks to Spring's {@link org.springframework.core.ReactiveAdapterRegistry}
 *   mechanism if such adapter registry has been set in the context
 *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}).
 * </p>
 * <p>
 *   Data-driver context variables are required to be <strong>multi-valued</strong>.
 *   Being <em>multi-valued</em> does not mean to necessarily return more than one value,
 *   but simply to have the capability to do so. E.g. a {@link reactor.core.publisher.Flux} object will
 *   be considered <em>multi-valued</em> even if it publishes none or just one result, whereas a
 *   {@link reactor.core.publisher.Mono} object will be considered <em>single-valued</em>.
 * </p>
 * <p>
 *   Example use:
 * </p>
 * <pre><code>
 * &#64;RequestMapping("/something")
 * public String doSomething(final Model model) {
 *     final Publisher&lt;Item&gt; data = ...; // This has to be MULTI-VALUED (e.g. Flux)
 *     model.addAttribute("data", new ReactiveDataDriverContextVariable(data, 100));
 *     return "view";
 * }
 * </code></pre>
 * <p>
 *   And then at the template:
 * </p>
 * <pre><code>
 * &lt;table&gt;
 *   &lt;tbody&gt;
 *     &lt;tr th:each=&quot;item : ${data}&quot;&gt;
 *       &lt;td th:text=&quot;${item}&quot;&gt;some item...&lt;/td&gt;
 *     &lt;/tr&gt;
 *   &lt;/tbody&gt;
 * &lt;/table&gt;
 * </code></pre>
 * <p>
 *   For more information on the way this class would work in SSE (Server-Sent Event) scenarios, see
 *   {@link IReactiveSSEDataDriverContextVariable}.
 * </p>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context variables.
 * </p>
 *
 * @see IReactiveDataDriverContextVariable
 * @see IReactiveSSEDataDriverContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ReactiveDataDriverContextVariable implements IReactiveSSEDataDriverContextVariable {

    /**
     * <p>
     *   Default buffer size to be applied if none is specified. Value = {@code 10}.
     * </p>
     */
    public static final int DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS = 10;

    /**
     * <p>
     *   Default value for the first event ID (for SSE scenarios). Value = {@code 0}.
     * </p>
     */
    public static final long DEFAULT_FIRST_EVENT_ID = 0L;


    private final Object dataStream;
    private final int dataStreamBufferSizeElements;
    private final String sseEventsPrefix;
    private final long sseEventsFirstID;


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream.
     * </p>
     * <p>
     *   Buffer size will be set to {@link #DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS}.
     * </p>
     * <p>
     *   The specified {@code dataStream} must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution. If no adapter registry has been set into the context
     *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}) this data stream must mandatorily
     *   be a {@link Flux}.
     * </p>
     * <p>
     *   Note the specified {@code dataStream} must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's {@code Observable}
     *   (but not {@code Single}).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     */
    public ReactiveDataDriverContextVariable(final Object dataStream) {
        this(dataStream, DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS, null, DEFAULT_FIRST_EVENT_ID);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   buffer size.
     * </p>
     * <p>
     *   The specified {@code dataStream} must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution. If no adapter registry has been set into the context
     *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}) this data stream must mandatorily
     *   be a {@link Flux}.
     * </p>
     * <p>
     *   Note the specified {@code dataStream} must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's {@code Observable}
     *   (but not {@code Single}).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataStreamBufferSizeElements the buffer size to be applied (in elements).
     */
    public ReactiveDataDriverContextVariable(final Object dataStream, final int dataStreamBufferSizeElements) {
        this(dataStream, dataStreamBufferSizeElements, null, DEFAULT_FIRST_EVENT_ID);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   buffer size and a prefix for all the names and IDs of events generated from a specific SSE stream.
     * </p>
     * <p>
     *   The specified {@code dataStream} must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution. If no adapter registry has been set into the context
     *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}) this data stream must mandatorily
     *   be a {@link Flux}.
     * </p>
     * <p>
     *   Note the specified {@code dataStream} must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's {@code Observable}
     *   (but not {@code Single}).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataStreamBufferSizeElements the buffer size to be applied (in elements).
     * @param sseEventsPrefix the prefix to be used for event names and IDs, so that events coming from a specific
     *                        SSE stream can be identified (if applies). Can be null.
     *
     * @since 3.0.8
     */
    public ReactiveDataDriverContextVariable(
            final Object dataStream, final int dataStreamBufferSizeElements,
            final String sseEventsPrefix) {
        this(dataStream, dataStreamBufferSizeElements, sseEventsPrefix, DEFAULT_FIRST_EVENT_ID);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   buffer size and a value for the ID of the first event generated in SSE scenarios.
     * </p>
     * <p>
     *   The specified {@code dataStream} must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution. If no adapter registry has been set into the context
     *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}) this data stream must mandatorily
     *   be a {@link Flux}.
     * </p>
     * <p>
     *   Note the specified {@code dataStream} must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's {@code Observable}
     *   (but not {@code Single}).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataStreamBufferSizeElements the buffer size to be applied (in elements).
     * @param sseEventsFirstID the first value to be used as event ID in SSE scenarios (if applies).
     *
     * @since 3.0.4
     */
    public ReactiveDataDriverContextVariable(
            final Object dataStream, final int dataStreamBufferSizeElements,
            final long sseEventsFirstID) {
        this(dataStream, dataStreamBufferSizeElements, null, sseEventsFirstID);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   buffer size and a value for the ID of the first event generated in SSE scenarios and a prefix for all
     *   the names and IDs of events generated from a specific SSE stream.
     * </p>
     * <p>
     *   The specified {@code dataStream} must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution. If no adapter registry has been set into the context
     *   (see {@link SpringWebFluxContext#getReactiveAdapterRegistry()}) this data stream must mandatorily
     *   be a {@link Flux}.
     * </p>
     * <p>
     *   Note the specified {@code dataStream} must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's {@code Observable}
     *   (but not {@code Single}).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataStreamBufferSizeElements the buffer size to be applied (in elements).
     * @param sseEventsPrefix the prefix to be used for event names and IDs, so that events coming from a specific
     *                        SSE stream can be identified (if applies). Can be null.
     * @param sseEventsFirstID the first value to be used as event ID in SSE scenarios (if applies).
     *
     * @since 3.0.8
     */
    public ReactiveDataDriverContextVariable(
            final Object dataStream, final int dataStreamBufferSizeElements,
            final String sseEventsPrefix, final long sseEventsFirstID) {
        super();
        Validate.notNull(dataStream, "Data stream cannot be null");
        Validate.isTrue(dataStreamBufferSizeElements > 0, "Data Buffer Size cannot be <= 0");
        // The prefix for SSE events CAN be null
        Validate.isTrue(sseEventsFirstID >= 0L, "First Event ID cannot be < 0");
        this.dataStream = dataStream;
        this.dataStreamBufferSizeElements = dataStreamBufferSizeElements;
        this.sseEventsPrefix = sseEventsPrefix;
        this.sseEventsFirstID = sseEventsFirstID;
    }


    @Override
    public Publisher<Object> getDataStream(final ReactiveAdapterRegistry reactiveAdapterRegistry) {
        final Publisher<Object> publisher =
                ReactiveContextVariableUtils.computePublisherValue(this.dataStream, reactiveAdapterRegistry);
        if (!(publisher instanceof Flux)) {
            throw new IllegalArgumentException(
                    "Reactive Data Driver context variable was set single-valued asynchronous object. But data driver " +
                    "variables must wrap multi-valued data streams (so that they can be iterated at the template");
        }
        return publisher;
    }


    @Override
    public final int getBufferSizeElements() {
        return this.dataStreamBufferSizeElements;
    }


    @Override
    public final String getSseEventsPrefix() {
        return this.sseEventsPrefix;
    }


    @Override
    public final long getSseEventsFirstID() {
        return this.sseEventsFirstID;
    }

}
