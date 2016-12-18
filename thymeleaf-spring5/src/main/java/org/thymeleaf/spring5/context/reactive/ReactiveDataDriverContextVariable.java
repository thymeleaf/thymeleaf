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
 *   The <em>reactive data stream</em> wrapped by this class will usually have the shape of an implementation of the
 *   {@link Publisher} interface, such as {@link reactor.core.publisher.Flux}. But other types of reactive
 *   artifacts are supported thanks to Spring's {@link org.springframework.core.ReactiveAdapterRegistry}
 *   mechanism.
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
public class ReactiveDataDriverContextVariable implements IReactiveDataDriverContextVariable {

    /**
     * <p>
     *   Default buffer size to be applied if none is specified. Value = <tt>100</tt>.
     * </p>
     */
    public static final int DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS = 100;

    private final Object dataStream;
    private final int dataStreamBufferSizeElements;
    private ReactiveAdapterRegistry adapterRegistry;


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream.
     * </p>
     * <p>
     *   Buffer size will be set to {@link #DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS}.
     * </p>
     * <p>
     *   The specified <tt>dataStream</tt> must be <em>adaptable</em> to a Reactive Stream's
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution.
     * </p>
     * <p>
     *   Note the specified <tt>dataStream</tt> must be <strong>multi-valued</strong>.
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
        this(dataStream, DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS);
    }


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous data stream and specifying a
     *   buffer size.
     * </p>
     * <p>
     *   The specified <tt>dataStream</tt> must be <em>adaptable</em> to a Reactive Streams
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution.
     * </p>
     * <p>
     *   Note the specified <tt>dataStream</tt> must be <strong>multi-valued</strong>.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} (but not
     *   {@link reactor.core.publisher.Mono}), and also RxJava's <tt>Observable</tt>
     *   (but not <tt>Single</tt>).
     * </p>
     *
     * @param dataStream the asynchronous object, which must be convertible to a multi-valued {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     * @param dataStreamBufferSizeElements the buffer size to be applied (in elements).
     */
    public ReactiveDataDriverContextVariable(final Object dataStream, final int dataStreamBufferSizeElements) {
        super();
        Validate.notNull(dataStream, "Data stream cannot be null");
        Validate.isTrue(dataStreamBufferSizeElements > 0, "Data Buffer Size cannot be <= 0");
        this.dataStream = dataStream;
        this.dataStreamBufferSizeElements = dataStreamBufferSizeElements;
    }


    /**
     * <p>
     *   Sets the {@link ReactiveAdapterRegistry} used for converting (if necessary) the wrapped asynchronous
     *   object into a {@link Publisher}.
     * </p>
     * <p>
     *   This method is transparently called before template execution in order
     *   to initialize lazy context variables. It can also be called programmatically, but there is normally
     *   no reason to do this. If not called at all, only {@link Flux} will be allowed as valid type
     *   for the wrapped data stream.
     * </p>
     *
     * @param reactiveAdapterRegistry the reactive adapter registry.
     */
    public final void setReactiveAdapterRegistry(final ReactiveAdapterRegistry reactiveAdapterRegistry) {
        // Note the presence of the ReactiveAdapterRegistry is optional, so this method might never be
        // called. We can only be sure that it will be called if this context variable is part of a model
        // used for rendering a ThymeleafReactiveView (which, anyway, will be most of the cases).
        this.adapterRegistry = reactiveAdapterRegistry;
    }


    @Override
    public Publisher<Object> getDataStream() {
        final Publisher<Object> publisher =
                ReactiveContextVariableUtils.computePublisherValue(this.dataStream, this.adapterRegistry);
        if (!(publisher instanceof Flux)) {
            throw new IllegalArgumentException(
                    "Reactive Data Driver context variable was set single-valued aynchronous object. But data driver " +
                    "variables must wrap multi-valued data streams (so that they can be iterated at the template");
        }
        return publisher;
    }


    @Override
    public final int getBufferSizeElements() {
        return this.dataStreamBufferSizeElements;
    }

}
