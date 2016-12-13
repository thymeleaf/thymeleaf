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
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.context.LazyContextVariable;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Basic implementation of the {@link IReactiveLazyContextVariable} interface.
 * </p>
 * <p>
 *   The <em>reactive async object</em> wrapped by this class will usually have the shape of an implementation of the
 *   {@link Publisher} interface, such as {@link reactor.core.publisher.Flux}. But other types of reactive
 *   artifacts are supported thanks to Spring's {@link org.springframework.core.ReactiveAdapterRegistry}
 *   mechanism.
 * </p>
 * <p>
 *   When <em>lazily</em> resolving these variables, this class mirrors the mechanism used by Spring for resolving
 *   asynchronous variables at the model of views (see Spring's
 *   {@link org.springframework.web.reactive.result.view.ViewResolutionResultHandler}):
 * </p>
 * <ul>
 *     <li><tt>Flux&lt;T&gt;</tt> or other <em>multi-valued</em> streams are resolved as
 *         <tt>List&lt;T&gt;</tt> so that they are <em>iterable</em>.</li>
 *     <li><tt>Mono&lt;T&gt;</tt> or other <em>single-valued</em> streams are resolved as
 *         <tt>T</tt> so that they are directly referenceable just like any other object.</li>
 * </ul>
 * <p>
 *   See {@link #isMultiValued()} for a better reference of what is considered
 *   <em>multi-valued</em> and <em>single-valued</em>.
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
 *     // If async is multi-valued, 'someData' will be usable as if it were of type List&lt;Item&gt;
 *     // If async is single-valued, 'someData' will be usable as if it were of type Item
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
 * @see IReactiveLazyContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ReactiveLazyContextVariable
        extends LazyContextVariable<Object>
        implements IReactiveLazyContextVariable {

    private final Object asyncObject;

    private volatile boolean publisherInitialized = false;
    // This Publisher will always be either Flux or Mono
    private Publisher<Object> asyncPublisher;

    private ReactiveAdapterRegistry adapterRegistry;


    /**
     * <p>
     *   Creates a new lazy context variable, wrapping a reactive asynchronous object.
     * </p>
     * <p>
     *   The specified <tt>asyncObject</tt> must be <em>adaptable</em> to a Reactive Stream's
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
     *   This method is transparently called by {@link ThymeleafReactiveView} during view resolution in order
     *   to initialize lazy context variables. It can also be called programmatically if no <tt>View</tt>
     *   is used. If not called at all, only {@link Flux} and {@link Mono} will be allowed as valid types
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
    public final Publisher<Object> getAsyncPublisher() {
        /*
         * Note the reason we don't initialize the asyncPublisher at constructor time is because at that time we haven't
         * given Thymeleaf the chance to set the ReactiveAdapterRegistry (which is set at ThymeleafReactiveView).
         */
        if (!this.publisherInitialized) {
            synchronized (this) {
                if (!this.publisherInitialized) {
                    this.asyncPublisher = loadPublisherValue(this.asyncObject, this.adapterRegistry);
                    if (this.asyncPublisher == null ||
                            !(this.asyncPublisher instanceof Flux || this.asyncPublisher instanceof Mono)) {
                        throw new IllegalStateException(
                                "Loaded Publisher Value at " +
                                ReactiveLazyContextVariable.class.getSimpleName() + " must be non-null, and either " +
                                Flux.class.getName() + " or " + Mono.class.getName() + ", but was: " +
                                (this.asyncPublisher == null? "null" : this.asyncPublisher.getClass().getName()));
                    }
                    this.publisherInitialized = true;
                }
            }
        }
        return this.asyncPublisher;
    }


    @Override
    public final boolean isMultiValued() {
        final Publisher<Object> publisher = getAsyncPublisher();
        return publisher instanceof Flux;
    }


    @Override
    protected final Object loadValue() {
        /*
         * This method should be called when this reactive lazy variable is used as a normal variable (simply to
         * avoid resolving data streams we won't need for view rendering), but should never be called when this
         * is used as a data driver (Thymeleaf executed in Data-Driven mode). In such case, the asyncPublisher
         * will be directly used by the Thymeleaf throttling engine.
         */
        final Publisher<Object> publisher = getAsyncPublisher();
        if (publisher instanceof Flux) {
            return ((Flux<Object>)publisher).collectList().defaultIfEmpty(Collections.emptyList()).block();
        }
        return ((Mono<Object>)publisher).block(); // Will return null if empty, which is OK
    }


    /**
     * <p>
     *   Lazily resolve the reactive asynchronous object into a {@link Publisher}.
     * </p>
     * <p>
     *   This method will only be called once, the first time this resolution is needed.
     * </p>
     * <p>
     *   This method can be overridden in order to apply new behaviour to the resolution of lazy reactive variables.
     * </p>
     *
     * @param asyncObj the asynchronous object being wrapped by this lazy variable.
     * @param reactiveAdapterRegistry the Spring {@link ReactiveAdapterRegistry}.
     * @return the resolved {@link Publisher}.
     */
    protected Publisher<Object> loadPublisherValue(
            final Object asyncObj, final ReactiveAdapterRegistry reactiveAdapterRegistry) {

        if (asyncObj instanceof Flux<?> || asyncObj instanceof Mono<?>) {
            // If the async object is a Flux or a Mono, we don't need the ReactiveAdapterRegistry (and we allow
            // initialization to happen without the registry, which is not possible with other Publisher<?>
            // implementations.
            return (Publisher<Object>) asyncObj;
        }


        if (reactiveAdapterRegistry == null) {
            throw new IllegalArgumentException(
                    "Could not initialize " + ReactiveLazyContextVariable.class.getSimpleName() + " : " +
                    "Value is of class " + asyncObj.getClass().getName() +", but no ReactiveAdapterRegistry " +
                    "has been set so far. This can happen if this context variable is used for rendering a template " +
                    "without going through a " + ThymeleafReactiveView.class.getSimpleName() + " or if there is no " +
                    "ReactiveAdapterRegistry bean registered at the application context. In such cases, it is " +
                    "required that these lazy variables are instances of either " + Flux.class.getName() + " or " +
                    Mono.class.getName() + ".");
        }

        final ReactiveAdapter adapter = reactiveAdapterRegistry.getAdapter(null, asyncObj);
        if (adapter != null) {
            final Publisher<Object> publisher = adapter.toPublisher(asyncObj);
            if (adapter.isMultiValue()) {
                return Flux.from(publisher);
            } else {
                return Mono.from(publisher);
            }
        }

        throw new IllegalArgumentException(
                "Value set for " + ReactiveLazyContextVariable.class.getSimpleName() + " context variable " +
                "is of class " + asyncObj.getClass().getName() +", but the ReactiveAdapterRegistry " +
                "does not contain a valid adapter able to convert it into a supported reactive data stream.");

    }

}
