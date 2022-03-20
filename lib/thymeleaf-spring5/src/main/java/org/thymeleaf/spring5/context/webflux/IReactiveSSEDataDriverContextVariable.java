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

/**
 * <p>
 *   Interface to be implemented by context variables wrapping <em>asynchronous objects</em> in the form
 *   of <em>reactive data streams</em> which are meant to <em>drive</em> the reactive-friendly execution of a
 *   template in SSE (Server-Sent Event) mode.
 * </p>
 * <p>
 *   This interface adds to its parent {@link IReactiveDataDriverContextVariable} the possibility to specify a prefix
 *   to be applied to the names and IDs of events generated in SSE scenarios. This can be useful in scenarios such as
 *   UI composition, in which streams of markup events coming from different sources (e.g. different parts of a page)
 *   can be sent to the browser combined in a single {@code EventSource} SSE stream. That way client JavaScript
 *   code will be able to identify which part of the page the event belongs to by means of its prefix. Also, combining
 *   several (prefixed) SSE streams into one can also serve to overcome limitations in the amount of concurrent
 *   active {@code EventSource} allowed.
 * </p>
 * <p>
 *   This interface also allows the specification of the first ID value to be used in these SSE events. This is
 *   useful in SSE scenarios in which the browser requests the server to reconnect after a connection
 *   failure, specifying the HTTP {@code Last-Event-ID} header so that the
 *   application can start generating events again starting from the event following the last one successfully
 *   processed by the browser (note this <em>resume</em> operation has to be supported by whoever is in charge of
 *   creating the data stream that Thymeleaf subscribes to in {@code DATA-DRIVEN} mode, not by Thymeleaf itself
 *   which is only in charge of rendering the view layer).
 * </p>
 * <p>
 *   Returning SSE (Server-Sent Events) through Thymeleaf requires the presence of a variable implementing this
 *   interface in the context. Thymeleaf will generate three types of events during rendering:
 * </p>
 * <ul>
 *     <li>Header ({@code event: head} or {@code event: {prefix}_head}), a single event containing all the
 *         markup previous to the iterated data (if any).</li>
 *     <li>Data message ({@code event: message} or {@code event: {prefix}_message})), a series of n events, one
 *         for each value produced by the data driver.</li>
 *     <li>Tail ({@code event: tail} or {@code event: {prefix}_tail})), a single event containing all the markup
 *         following the last iterated piece of data (if any).</li>
 * </ul>
 * <p>
 *   Note that in the case of SSE, the value assigned to the {@link #getBufferSizeElements()} property does
 *   affect the immediacy of the generated (published) events being sent to the browser. If this buffer is set e.g.
 *   to 4, only when a total of 4 items of data are generated will be sent to the browser as SSE events.
 * </p>
 * <p>
 *   The {@link ReactiveDataDriverContextVariable} class contains a sensible implementation of this interface,
 *   directly usable in most scenarios. Example use:
 * </p>
 * <pre><code>
 * &#64;RequestMapping("/something")
 * public String doSomething(final Model model) {
 *     final Publisher&lt;Item&gt; data = ...; // This has to be MULTI-VALUED (e.g. Flux)
 *     model.addAttribute("data", new ReactiveDataDriverContextVariable(data, 100, 1L)); // firstEventID = 1L
 *     return "view";
 * }
 * </code></pre>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.4
 *
 */
public interface IReactiveSSEDataDriverContextVariable extends IReactiveDataDriverContextVariable {


    /**
     * <p>
     *   Returns the (optional) prefix to be used for SSE event names and IDs.
     * </p>
     * <p>
     *   Using a prefix for SSE events can be useful in scenarios such as UI composition, in which streams of
     *   markup events coming from different sources (e.g. different parts of a page) can be sent to the browser
     *   combined in a single {@code EventSource} SSE stream. That way client JavaScript
     *   code will be able to identify which part of the page the event belongs to by means of its
     *   prefix. Also, combining several (prefixed) SSE streams into one can also serve to overcome limitations
     *   in the amount of concurrent active {@code EventSource} allowed.
     * </p>
     *
     * @return the prefix to be applied to event names and IDs, or {@code null} if no prefix has been set.
     * @since 3.0.8
     */
    public String getSseEventsPrefix();


    /**
     * <p>
     *   Returns the first value to be used as an {@code id} in the case this response is rendered as SSE
     *   (Server-Sent Events) with content type {@code text/event-stream}.
     * </p>
     * <p>
     *   After the first generated events, subsequent ones will be assigned an {@code id} by incrementing this
     *   first value.
     * </p>
     *
     * @return the first value to be used for returning the data driver variable values as SSE events.
     */
    public long getSseEventsFirstID();

}
