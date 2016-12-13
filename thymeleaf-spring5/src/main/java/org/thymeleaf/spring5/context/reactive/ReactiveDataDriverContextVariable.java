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
package org.thymeleaf.spring5.context.reactive;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;

/**
 * <p>
 *   Basic implementation of the {@link IReactiveDataDriverContextVariable} interface.
 * </p>
 * <p>
 *   This class works very similarly to {@link ReactiveLazyContextVariable} (from which it extends),
 *   but also marks the variable as a valid <strong>data-driver</strong> for the template, effectively
 *   putting Thymeleaf in <em>data-driven</em> execution mode.
 *   <strong>Templates executed in <em>data-driven</em> mode are expected to have some kind <em>iteration</em>
 *   on the data-driver variable</strong>, normally by means of a <tt>th:each</tt> attribute.
 * </p>
 * <p>
 *   Note that data-driver variables require the reactive asynchronous object they wrap to be
 *   <strong>multi-valued</strong>. See {@link IReactiveLazyContextVariable#isMultiValued()} for details on what
 *   this means.
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
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context variables.
 * </p>
 *
 * @see IReactiveDataDriverContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ReactiveDataDriverContextVariable
        extends ReactiveLazyContextVariable
        implements IReactiveDataDriverContextVariable {

    /**
     * <p>
     *   Default chunk size to be applied if none is specified. Value = <tt>100</tt>.
     * </p>
     */
    public static final int DEFAULT_DATA_DRIVER_CHUNK_SIZE_ELEMENTS = 100;

    private final int dataChunkSize;


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream.
     * </p>
     * <p>
     *   Chunk size will be set to {@link #DEFAULT_DATA_DRIVER_CHUNK_SIZE_ELEMENTS}.
     * </p>
     * <p>
     *   The specified <tt>dataStream</tt> must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution.
     * </p>
     * <p>
     *   Note the specified <tt>dataStream</tt> must be <strong>multi-valued</strong>. See
     *   {@link IReactiveLazyContextVariable#isMultiValued()}.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's <tt>Observable</tt>
     *   (but not <tt>Single</tt>).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     */
    public ReactiveDataDriverContextVariable(final Object dataStream) {
        this(dataStream, DEFAULT_DATA_DRIVER_CHUNK_SIZE_ELEMENTS);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   chunk size.
     * </p>
     * <p>
     *   The specified <tt>dataStream</tt> must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution.
     * </p>
     * <p>
     *   Note the specified <tt>dataStream</tt> must be <strong>multi-valued</strong>. See
     *   {@link IReactiveLazyContextVariable#isMultiValued()}.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's <tt>Observable</tt>
     *   (but not <tt>Single</tt>).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataChunkSizeElements the chunk size to be applied.
     */
    public ReactiveDataDriverContextVariable(final Object dataStream, final int dataChunkSizeElements) {
        super(dataStream);
        Validate.isTrue(dataChunkSizeElements > 0, "Data Chunk Size cannot be <= 0");
        this.dataChunkSize = dataChunkSizeElements;
    }


    public final int getDataStreamBufferSizeElements() {
        return this.dataChunkSize;
    }


    @Override
    protected Publisher<Object> loadPublisherValue(final Object asyncObj, final ReactiveAdapterRegistry reactiveAdapterRegistry) {
        final Publisher<Object> publisher = super.loadPublisherValue(asyncObj, reactiveAdapterRegistry);
        if (!(publisher instanceof Flux)) {
            throw new IllegalArgumentException(
                    "Reactive Data Driver context variable was set single-valued aynchronous object. But data driver " +
                    "variables must wrap multi-valued data streams (so that they can be iterated at the template");
        }
        return publisher;
    }

}
