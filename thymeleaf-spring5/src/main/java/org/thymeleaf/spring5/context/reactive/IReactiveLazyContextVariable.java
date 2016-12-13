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
 *   Interface to be implemented by context variables wrapping <em>asymchronous reactive objects</em> so that these
 *   are not resolved <em>before</em> the template executes.
 * </p>
 * <p>
 *   By being added to the context/model wrapped by an object implementing this interface,
 *   reactive asynchronous objects will reach the execution phase of the view layer unresolved, and
 *   will only be resolved if they are really needed. So asynchronous variables that the template does not
 *   really need in the end (because template logic resolves in a way that doesn't make use of them) will never
 *   be consumed at all.
 * </p>
 * <p>
 *   If the variable being added to the context is meant to work as a <strong><em>data-driver</em></strong>, i.e. to
 *   make the template engine execute as a consumer of a data stream and work as a producer of output (effectively
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
 *
 * @see ReactiveLazyContextVariable
 * @see org.thymeleaf.context.ILazyContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IReactiveLazyContextVariable extends ILazyContextVariable<Object> {


    /**
     * <p>
     *   Returns whether the wrapped reactive asynchronous object is meant to be multi-valued or not.
     * </p>
     * <p>
     *   Note that being <em>multi-valued</em> does not mean to necessarily return more than one value,
     *   but simply to have the capability to do so. E.g. a {@link reactor.core.publisher.Flux} object will
     *   be considered <em>multi-valued</em> even if it publishes no or just one result, whereas a
     *   {@link reactor.core.publisher.Mono} object will be considered <em>single-valued</em>.
     * </p>
     *
     * @return <tt>true</tt> if the asynchronous object can return more than one results, <tt>false</tt> if not.
     */
    public boolean isMultiValued();


    /**
     * <p>
     *   Returns the reactive asynchronous object being wrapped, having been (possibly) re-shaped into a
     *   {@link Publisher} stream.
     * </p>
     *
     * @return the asynchronous object (as a {@link Publisher}).
     */
    public Publisher<Object> getAsyncPublisher();

}
