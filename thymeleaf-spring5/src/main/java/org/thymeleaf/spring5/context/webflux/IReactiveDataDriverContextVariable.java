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

/**
 * <p>
 *   Interface to be implemented by context variables wrapping <em>asynchronous objects</em> in the form
 *   of <em>reactive data streams</em> which are meant to <em>drive</em> the reactive-friendly execution of a
 *   template.
 * </p>
 * <p>
 *   The presence of a variable of this type in the context sets the engine into <strong>data-driven
 *   mode</strong>, and only one of these variables is allowed to appear in the context for template execution.
 * </p>
 * <p>
 *   Using Reactive Streams terminology, this makes Thymeleaf act as a {@link org.reactivestreams.Processor}, given
 *   it will be a {@link org.reactivestreams.Subscriber} to the data-driver stream, and at the same time a
 *   {@link org.reactivestreams.Publisher} of output buffers (usually containing HTML markup).
 * </p>
 * <p>
 *   <strong>Templates executed in <em>data-driven</em> mode are expected to contain some kind <em>iteration</em>
 *   on the data-driver variable</strong>, normally by means of a {@code th:each} attribute. This iteration
 *   should be <em>unique</em>. Also note that, if this iteration is not present (or it doesn't end up being
 *   executed due to template logic), it is <strong>not guaranteed</strong> that the data-driven stream will not
 *   be consumed anyway -at least partially- depending on the use that the specific server implementation might make
 *   of the Reactor's back-pressure mechanism.
 * </p>
 * <p>
 *   Data-driver context variables are required to be <strong>multi-valued</strong>.
 *   Being <em>multi-valued</em> does not mean to necessarily return more than one value,
 *   but simply to have the capability to do so. E.g. a {@link reactor.core.publisher.Flux} object will
 *   be considered <em>multi-valued</em> even if it publishes none or just one result, whereas a
 *   {@link reactor.core.publisher.Mono} object will be considered <em>single-valued</em>.
 * </p>
 * <p>
 *   The {@link #getBufferSizeElements()} property describes the size (in elements) of the buffers that
 *   will be created from the data-driver stream before triggering the execution of the template (for each buffer).
 *   Normally there is no need to execute the template engine and generate output for each element of data
 *   published by the data stream, so this buffering prevents Thymeleaf from executing more times than actually needed.
 * </p>
 * <p>
 *   The {@link ReactiveDataDriverContextVariable} class contains a sensible implementation of this interface,
 *   directly usable in most scenarios. Example use:
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
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IReactiveDataDriverContextVariable {


    /**
     * <p>
     *   Returns the reactive asynchronous object being wrapped, (perhaps) having been re-shaped into a
     *   {@link Publisher} stream.
     * </p>
     *
     * @param reactiveAdapterRegistry the Spring reactive adapter registry that should be used in order to transform
     *                                other reactive implementations (RxJava, etc.) into
     *                                {@link reactor.core.publisher.Flux}. Can be null (no adaptations will be
     *                                available).
     * @return the asynchronous object (as a {@link Publisher}).
     */
    public Publisher<Object> getDataStream(final ReactiveAdapterRegistry reactiveAdapterRegistry);

    /**
     * <p>
     *   Returns the size (in elements) of the buffers that will be created from the data-driver stream
     *   before triggering the execution of the template (for each buffer).
     * </p>
     * <p>
     *   Normally there is no need to execute the template engine and generate output for each element of data
     *   published by the data stream, so this buffering  prevents Thymeleaf from executing more times
     *   than actually needed.
     * </p>
     *
     * @return the size (in elements) of the buffers to be created for each (partial) execution of the engine.
     */
    public int getBufferSizeElements();

}
