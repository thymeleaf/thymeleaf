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
package org.thymeleaf.spring5.context.webflux;

/**
 * <p>
 *   Interface to be implemented by context variables wrapping <em>asynchronous objects</em> in the form
 *   of <em>reactive data streams</em> which are meant to <em>drive</em> the reactive-friendly execution of a
 *   template in SSE (Server-Sent Event) mode.
 * </p>
 * <p>
 *   This interface adds to {@link IReactiveDataDriverContextVariable} the possibility to specify the first ID value
 *   to be used for generating SSE events. This is useful in SSE scenarios in which the browser requests the server
 *   to reconnect after a connection failure, specifying the HTTP <tt>Last-Event-ID</tt> header so that the
 *   application can start generating events again starting from the event following the last one successfully
 *   processed by the browser.
 * </p>
 * <p>
 *   Returning SSE (Server-Sent Events) through Thymeleaf requires the presence of a variable implementing this
 *   interface in the context. Thymeleaf will generate three types of events during rendering:
 * </p>
 * <ul>
 *     <li>Header (<tt>event: head</tt>), a single event containing all the markup previous to the iterated
 *         data (if any).</li>
 *     <li>Data (<tt>event: data</tt>), a series of n events, one for each value produced by the data driver.</li>
 *     <li>Tail (<tt>event: tail</tt>), a single event containing all the markup following the last iterated
 *         piece of data (if any).</li>
 * </ul>
 * <p>
 *   Note that in the case of SSE, the value assigned to the {@link #getBufferSizeElements()} property can actually
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
     *   Returns the first value to be used as an <tt>id</tt> in the case this response is rendered as SSE
     *   (Server-Sent Events) with content type <tt>text/event-stream</tt>.
     * </p>
     * <p>
     *   After the first generated events, subsequent ones will be assigned an <tt>id</tt> by incrementing this
     *   first value.
     * </p>
     *
     * @return the first value to be used for returning the data driver variable values as SSE events.
     */
    public long getFirstEventID();

}
