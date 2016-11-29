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
import org.thymeleaf.context.ILazyContextVariable;

/**
 * <p>
 *   Interface to be implemented by context variables wrapping <em>reactive data streams</em> so that these
 *   are not resolved <em>before</em> the template executes.
 * </p>
 * <p>
 *   The wrapped <em>data stream</em> variable will usually have the shape of an implementation of the
 *   {@link Publisher} interface, such as {@link reactor.core.publisher.Flux}.
 * </p>
 * <p>
 *   By being added to the context/model wrapped by an object implementing this interface,
 *   reactive data streams will reach the execution phase of the view layer unresolved, and
 *   will only be resolved if they are really needed. So data stream variables that the template does not
 *   really need in the end (because template logic resolves in a way that doesn't make use of them) will never
 *   be consumed at all.
 * </p>
 * <p>
 *   If the variable being added to the context is meant to work as a <strong><em>data-driver</em></strong>, i.e. to
 *   make the template engine execute as a consumer of the data stream and work as a producer of output (effectively
 *   turning Thymeleaf into just another step in the data stream converting data into markup output), a subinterface
 *   of this interface should be used instead: {@link IReactiveDataDriverContextVariable}.
 * </p>
 * <p>
 *   Note this <em>lazy resolution</em> can only be performed when the lazy variable is added to the context as a
 *   <strong>first-level</strong> variable. i.e. <tt>${lazy}</tt> will work, but <tt>${container.lazy}</tt>
 *   will not.
 * </p>
 * <p>
 *   The {@link ReactiveLazyContextVariable} class contains a sensible implementation of this interface,
 *   directly usable in most scenarios.
 * </p>
 * <p>
 *   Example use:
 * </p>
 * <pre><code>
 * &#64;RequestMapping("/something")
 * public String doSomething(final Model model) {
 *     final Publisher&lt;Item&gt; someStream = ...;
 *     model.addAttribute("someData", new ReactiveLazyContextVariable&lt;&gt;(someStream);
 *     return "view";
 * }
 * </code></pre>
 *
 * @param <T> the type of the values being returned by the wrapped data stream.
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IReactiveLazyContextVariable<T> extends ILazyContextVariable<Iterable<T>> {

    /**
     * <p>
     *   Returns the data stream being wrapped.
     * </p>
     *
     * @return the wrapped data stream object.
     */
    public Publisher<T> getDataStream();

}
