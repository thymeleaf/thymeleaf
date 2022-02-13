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
package org.thymeleaf.spring5;

import java.nio.charset.Charset;
import java.util.Set;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.thymeleaf.context.IContext;


/**
 * <p>
 *   Sub-interface of {@link ISpringTemplateEngine} meant for Spring WebFlux applications, adding
 *   methods specifically needed for the execution of templates in a reactive-friendly way.
 * </p>
 * <p>
 *   Template engines implementing this interface offer three possible processing modes (note Reactive
 *   Streams terminology is used in the explanation):
 * </p>
 * <ol>
 *     <li><strong>Full</strong> mode: Output buffer size not limited and
 *        no <em>data-driven</em> execution (no context variable of type {@link Publisher}
 *        driving the template engine execution): In this case Thymeleaf will be executed <em>unthrottled</em>,
 *        computing the whole template in memory and sending all output to the output channels in a single
 *        {@link Subscriber#onNext(Object)} call, immediately followed by {@link Subscriber#onComplete()}.
 *     </li>
 *     <li><strong>Chunked</strong>: Output buffers limited in size but no <em>data-driven</em>
 *        execution (no {@link Publisher} driving engine execution). All context variables are
 *        expected to be fully resolved (in a non-blocking fashion) by WebFlux before engine execution and the Thymeleaf
 *        engine will execute in <em>throttled</em> mode, performing a full-stop each time the output buffer reaches
 *        the specified size, sending it to the output channels with {@link Subscriber#onNext(Object)} and then
 *        waiting until these output channels make the engine resume its work with a new
 *        {@link Subscription#request(long)} back-pressure call.
 *     </li>
 *     <li><strong>Data-Driven</strong>: one of the context variables is a reactive
 *        {@link Publisher} data stream wrapped inside an implementation
 *        of the {@link org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable} interface. In
 *        this case, Thymeleaf will act as a {@link Subscriber} of this data stream and a
 *        {@link Publisher} of output buffers (the combination of which turns Thymeleaf into a {@link Processor}
 *        in Reactive Streams terminology). Thymeleaf will execute as a response to
 *        {@link Subscriber#onNext(Object)} events triggered by this <em>data-driver</em>
 *        {@link Publisher}. Thymeleaf will expect to find a {@code th:each} iteration on the data-driven variable
 *        inside the processed template, and will be executed in throttled mode for the published elements, sending
 *        the resulting output buffers to the output channels via {@link Subscriber#onNext(Object)} and stopping
 *        until the <em>data-driver</em> {@link Publisher} produces new data (normally after being requested to do
 *        so via back-pressure. When execution is <em>data-driven</em>, a limit in size can be optionally specified for
 *        the output buffers which will make Thymeleaf never send to the output channels a buffer bigger than that.
 *     </li>
 * </ol>
 * <p>
 *   The {@link SpringWebFluxTemplateEngine} implementation of this interface (or a subclass) should be used
 *   in almost every case, but this interface improves testability of these artifacts.
 * </p>
 *
 * @see SpringWebFluxTemplateEngine
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public interface ISpringWebFluxTemplateEngine extends ISpringTemplateEngine {


    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final MediaType mediaType, final Charset charset);

    public Publisher<DataBuffer> processStream(
            final String template, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferFactory, final MediaType mediaType, final Charset charset, final int responseMaxChunkSizeBytes);


}
