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

import java.util.Collections;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.context.ILazyContextVariable;
import org.thymeleaf.context.LazyContextVariable;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Implementation of the {@link org.thymeleaf.context.ILazyContextVariable} interface meant to contain
 *   a reactive data stream that should not be resolved until view is rendered.
 * </p>
 * <p>
 *   By being added to the context/model wrapped by an object of this class, reactive asynchronous objects will
 *   reach the execution phase of the view layer unresolved, and will only be resolved if they are really
 *   needed. So asynchronous variables that the template does not really need in the end (because template
 *   logic resolves in a way that doesn't make use of them) will never be consumed at all.
 * </p>
 * <p>
 *   Note that <em>resolving</em> this kind of objects means actually <em>blocking</em> and <em>collecting</em>
 *   its values.
 * </p>
 * <p>
 *   The <em>reactive async object</em> wrapped by this class will usually have the shape of an implementation of the
 *   {@link Publisher} interface, such as {@link reactor.core.publisher.Flux}. But other types of reactive
 *   artifacts are supported thanks to Spring's {@link org.springframework.core.ReactiveAdapterRegistry}
 *   mechanism.
 * </p>
 * <p>
 *   When <em>lazily</em> resolving these variables, this class mirrors the mechanism used by Spring for resolving
 *   asynchronous variables at the model of views:
 * </p>
 * <ul>
 *     <li><tt>Flux&lt;T&gt;</tt> or other <em>multi-valued</em> streams are resolved as
 *         <tt>List&lt;T&gt;</tt> so that they are <em>iterable</em>.</li>
 *     <li><tt>Mono&lt;T&gt;</tt> or other <em>single-valued</em> streams are resolved as
 *         <tt>T</tt> so that they are directly referenceable just like any other object.</li>
 * </ul>
 * <p>
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
 *
 *     final Publisher&lt;Item&gt; async = ...;
 *
 *     // If 'async' is multi-valued, 'someData' will be usable as if it were of type List&lt;Item&gt;
 *     // If 'async' is single-valued, 'someData' will be usable as if it were of type Item
 *     model.addAttribute("someData", new ReactiveLazyContextVariable(async));
 *
 *     return "view";
 *
 * }
 * </code></pre>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context variables.
 * </p>
 *
 * @see ILazyContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ReactiveLazyContextVariable
        extends LazyContextVariable<Object>
        implements ILazyContextVariable<Object> {

    private final Object asyncObject;
    private ReactiveAdapterRegistry adapterRegistry;


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous object.
     * </p>
     * <p>
     *   The specified <tt>asyncObject</tt> must be <em>adaptable</em> to a Reactive Streams
     *   {@link Publisher} by means of Spring's {@link ReactiveAdapterRegistry} mechanism. If no
     *   adapter has been registered for the type of the asynchronous object, and exception will be
     *   thrown during lazy resolution.
     * </p>
     * <p>
     *   Examples of supported implementations are Reactor's {@link Flux} and {@link Mono}, and also
     *   RxJava's <tt>Observable</tt> and <tt>Single</tt>.
     * </p>
     *
     * @param asyncObject the asynchronous object, which must be convertible to a {@link Publisher} by
     *                    means of Spring's {@link ReactiveAdapterRegistry}.
     */
    public ReactiveLazyContextVariable(final Object asyncObject) {
        super();
        Validate.notNull(asyncObject, "Lazy context variable value cannot be null");
        this.asyncObject = asyncObject;
        this.adapterRegistry = null; // optional, but set transparently by ThymeleafReactiveView before rendering
    }


    /**
     * <p>
     *   Sets the {@link ReactiveAdapterRegistry} used for converting (if necessary) the wrapped asynchronous
     *   object into a {@link Publisher}.
     * </p>
     * <p>
     *   This method is transparently called before template execution in order
     *   to initialize lazy context variables. It can also be called programmatically, but there is normally
     *   no reason to do this. If not called at all, only {@link Flux} and {@link Mono} will be allowed as valid types
     *   for the wrapped asynchronous object.
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
    protected final Object loadValue() {
        /*
         * First the async object will be 'normalized' into a Publisher (which will ALWAYS be a Flux or a Mono
         * object), and then we will block in order to collect its values and return, as if the variable was never
         * async.
         */
        final Publisher<Object> publisher =
                ReactiveContextVariableUtils.computePublisherValue(this.asyncObject, this.adapterRegistry);
        if (publisher instanceof Flux) {
            // Data stream is multi-valued
            return ((Flux<Object>)publisher).collectList().defaultIfEmpty(Collections.emptyList()).block();
        }
        // Data stream is single-valued
        return ((Mono<Object>)publisher).block(); // Will return null if empty, which is OK
    }

}
